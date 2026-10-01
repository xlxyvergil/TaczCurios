package com.xlxyvergil.tcc.mixin;

import com.xlxyvergil.tcc.util.ITccSynchedEntityData;
import com.xlxyvergil.tcc.util.ZhenWoGuard;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 同步数据负值保护：处于免死保护中的真我佩戴者（佩戴且不在结界冷却中），身上写入的 Float 同步量若为负或
 * NaN 一律钳为 0。第三方模组靠"负向血量修正"绕过 setHealth、直接把判定血量压到 0 以下的通道由此失效。
 * 结界冷却期间不钳制，让强杀手段能正常杀死佩戴者。
 *
 * 内部字段一律按声明类型反射识别，不依赖被混淆的字段名。
 */
@Mixin(SynchedEntityData.class)
public abstract class SynchedEntityDataMixin implements ITccSynchedEntityData {

    @Unique private static Field tcc$ownerField;
    @Unique private static boolean tcc$ownerResolved;

    @Unique private static Field tcc$itemsField;
    @Unique private static boolean tcc$itemsResolved;

    @Unique private static Class<?> tcc$valueOwner;
    @Unique private static Field tcc$valueField;

    @Unique private static EntityDataAccessor<Float> tcc$healthAccessor;
    @Unique private static boolean tcc$healthResolved;
    @Unique private static boolean tcc$handlingLethal;

    /** 写入拦截：处于免死保护中的真我佩戴者，身上的负值 / NaN 一律改写为 0。 */
    @ModifyVariable(method = "set(Lnet/minecraft/network/syncher/EntityDataAccessor;Ljava/lang/Object;)V",
            at = @At("HEAD"), argsOnly = true, index = 2)
    private Object tcc$clampNegativeFloat(Object value) {
        if (!(value instanceof Float f)) return value;
        if (!(f < 0.0F) && !f.isNaN()) return value;
        Entity owner = tcc$owner((SynchedEntityData) (Object) this);
        if (!(owner instanceof LivingEntity living)) return value;
        if (!ZhenWoGuard.canPreventDeath(living)) return value;
        return 0.0F;
    }

    /**
     * 血量归零拦截：处于免死保护中的真我佩戴者，同步血量永不被写成 0 / 负值。
     * 血量一旦归零就会被同步给客户端并弹出死亡界面，而服务端随后又把人救活，客户端已死、
     * 服务端存活，玩家点重生后服务端因血量大于 0 而忽略请求，导致无法攻击 / 交互（假死）。
     * 因此在写入源头直接截断，并立即触发免死。
     */
    @Inject(method = "set(Lnet/minecraft/network/syncher/EntityDataAccessor;Ljava/lang/Object;)V",
            at = @At("HEAD"), cancellable = true)
    private void tcc$guardLethalHealth(EntityDataAccessor<?> accessor, Object value, CallbackInfo ci) {
        if (!(value instanceof Float f)) return;
        if (f > 0.0F) return;
        if (accessor != tcc$healthAccessor()) return;
        Entity owner = tcc$owner((SynchedEntityData) (Object) this);
        if (!(owner instanceof LivingEntity living)) return;
        if (!ZhenWoGuard.canPreventDeath(living)) return;
        ci.cancel();
        // 免死流程内部会回写血量与清理残留，可能再次进入本注入，故加防重入标记
        if (tcc$handlingLethal) return;
        tcc$handlingLethal = true;
        try {
            ZhenWoGuard.onLethalHealthBlocked(living);
        } finally {
            tcc$handlingLethal = false;
        }
    }

    /** 血量同步访问器：LivingEntity 声明中唯一以 FLOAT 序列化的静态访问器，按类型识别以免依赖混淆名。 */
    @SuppressWarnings("unchecked")
    @Unique
    private static EntityDataAccessor<Float> tcc$healthAccessor() {
        if (!tcc$healthResolved) {
            tcc$healthResolved = true;
            for (Field field : LivingEntity.class.getDeclaredFields()) {
                if (!Modifier.isStatic(field.getModifiers())) continue;
                if (!EntityDataAccessor.class.isAssignableFrom(field.getType())) continue;
                try {
                    field.setAccessible(true);
                    if (field.get(null) instanceof EntityDataAccessor<?> accessor
                            && accessor.getSerializer() == EntityDataSerializers.FLOAT) {
                        tcc$healthAccessor = (EntityDataAccessor<Float>) accessor;
                        break;
                    }
                } catch (ReflectiveOperationException ignored) {
                    // 个别字段不可读时跳过，继续扫描
                }
            }
        }
        return tcc$healthAccessor;
    }

    /** 清理已经写进去的负值 / NaN：结界在受击后才激活时，旧残留需要就地归零并同步给客户端。 */
    @Override
    public void tcc$clearNegativeFloat() {
        Object container = tcc$container((SynchedEntityData) (Object) this);
        if (container == null) return;
        for (Object item : tcc$collect(container)) {
            if (!(tcc$read(item) instanceof Float f)) continue;
            if (!(f < 0.0F) && !f.isNaN()) continue;
            tcc$zero(item);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Unique
    private void tcc$zero(Object item) {
        Object accessor = tcc$accessor(item);
        if (!(accessor instanceof EntityDataAccessor)) return;
        ((SynchedEntityData) (Object) this).set((EntityDataAccessor) accessor, 0.0F);
    }

    // ---- 以下为内部结构的按类型反射识别 ----

    private static Entity tcc$owner(SynchedEntityData data) {
        if (!tcc$ownerResolved) {
            tcc$ownerResolved = true;
            for (Field field : SynchedEntityData.class.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers())) continue;
                if (field.getType() != Entity.class) continue;
                field.setAccessible(true);
                tcc$ownerField = field;
                break;
            }
        }
        if (tcc$ownerField == null) return null;
        try {
            return (Entity) tcc$ownerField.get(data);
        } catch (ReflectiveOperationException ignored) {
            return null;
        }
    }

    private static Object tcc$container(SynchedEntityData data) {
        if (!tcc$itemsResolved) {
            tcc$itemsResolved = true;
            for (Field field : SynchedEntityData.class.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers())) continue;
                Class<?> type = field.getType();
                boolean isMap = Map.class.isAssignableFrom(type);
                boolean isObjectArray = type.isArray() && !type.getComponentType().isPrimitive();
                if (isMap || isObjectArray) {
                    field.setAccessible(true);
                    tcc$itemsField = field;
                    break;
                }
            }
        }
        if (tcc$itemsField == null) return null;
        try {
            return tcc$itemsField.get(data);
        } catch (ReflectiveOperationException ignored) {
            return null;
        }
    }

    private static List<Object> tcc$collect(Object container) {
        List<Object> items = new ArrayList<>();
        if (container instanceof Map<?, ?> map) {
            items.addAll(map.values());
        } else if (container != null && container.getClass().isArray()) {
            int length = Array.getLength(container);
            for (int i = 0; i < length; i++) {
                Object item = Array.get(container, i);
                if (item != null) items.add(item);
            }
        }
        return items;
    }

    /** 数据项里存放实际值的字段：泛型擦除后其声明类型为 Object。 */
    private static Field tcc$valueField(Class<?> itemClass) {
        if (itemClass == tcc$valueOwner) return tcc$valueField;
        Field found = null;
        for (Field field : itemClass.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers())) continue;
            if (field.getType() != Object.class) continue;
            field.setAccessible(true);
            found = field;
            break;
        }
        tcc$valueOwner = itemClass;
        tcc$valueField = found;
        return found;
    }

    private static Object tcc$read(Object item) {
        Field field = tcc$valueField(item.getClass());
        if (field == null) return null;
        try {
            return field.get(item);
        } catch (ReflectiveOperationException ignored) {
            return null;
        }
    }

    /** 数据项的访问器读取方法：按返回类型识别，避免依赖方法名。 */
    private static Object tcc$accessor(Object item) {
        for (Method method : item.getClass().getMethods()) {
            if (method.getParameterCount() != 0) continue;
            if (!EntityDataAccessor.class.isAssignableFrom(method.getReturnType())) continue;
            try {
                return method.invoke(item);
            } catch (ReflectiveOperationException ignored) {
                return null;
            }
        }
        return null;
    }
}
