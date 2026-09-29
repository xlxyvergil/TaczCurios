package com.xlxyvergil.tcc.util;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 击杀收尾工具：绕过第三方模组的限伤/锁血，并补回被 die 拦住的掉落。
 * forceZeroHealth 反射直接改 SynchedEntityData 内部缓存的值字段，把判定生死读取的血量写成 0，
 * 不经过 setHealth 与 SynchedEntityData.set；之后调用方走标准 die()，若 dead 未置位，
 * 再补 dropAllDeathLoot 并 remove(KILLED)。
 * FORCED_KILL tag 为可选覆盖：命中的实体跳过 die()，直接补掉落 + remove(KILLED)。
 */
public final class ForcedKillHelper {

    /** 必须强制移除（vanilla die 无效）的实体类型 tag，见 data/tcc/tags/entity_type/tcc_forced_kill.json */
    public static final TagKey<EntityType<?>> FORCED_KILL =
        TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath("tcc", "tcc_forced_kill"));

    /**
     * 反射缓存的 LivingEntity#dropAllDeathLoot(DamageSource)。
     * 该方法为 protected，且正式环境为 SRG 名（m_6668_）、开发环境为官方名，
     * 故按名反射解析并缓存，避免 AccessTransformer 与 Mixin Accessor。
     */
    private static Method dropAllDeathLootMethod;
    private static boolean dropMethodResolved;

    /** SynchedEntityData 内部承载所有数据项的那个容器字段（Map，兼容数组实现）。 */
    private static Field itemsField;
    private static boolean itemsFieldResolved;

    /** 单个数据项里存放实际值的那个字段（泛型擦除后声明类型为 Object）。 */
    private static Class<?> valueFieldOwner;
    private static Field valueFieldCache;

    private ForcedKillHelper() {}

    /** 判断目标是否必须强制移除（命中则跳过 die()，改为先补掉落再 remove(KILLED)）。 */
    public static boolean requiresForcedKill(LivingEntity target) {
        return target.getType().is(FORCED_KILL);
    }

    /**
     * 直接把实体判定生死读取的那份 EntityData 值写成 0。
     * 不调用 setHealth（会被覆写限伤/回滚），也不调用 SynchedEntityData.set（会被 mixin 拦截），
     * 而是反射改内部缓存条目的值字段，纯字段写入。
     * 判定生死所读的浮点值当前等于 getHealth() 的模组都能命中：
     * vanilla / EEEABsMobs 的 DATA_HEALTH_ID、亚波伦下界形态的自管 accessor 等。
     *
     * @return 是否至少改写了一项
     */
    public static boolean forceZeroHealth(LivingEntity target) {
        float current = target.getHealth();
        if (current <= 0.0F) return false;
        try {
            Object container = resolveItems(target.getEntityData());
            if (container == null) return false;

            boolean rewritten = false;
            for (Object item : collectItems(container)) {
                Field valueField = resolveValueField(item.getClass());
                if (valueField == null) continue;
                Object value = valueField.get(item);
                // 命中取值等于当前血量的浮点数据项
                if (value instanceof Float f && f.floatValue() == current) {
                    valueField.set(item, 0.0F);
                    rewritten = true;
                }
            }
            return rewritten;
        } catch (Throwable ignored) {
            // 反射结构变化时静默放弃，交给上层兜底（补掉落 + remove(KILLED)）
            return false;
        }
    }

    /** 补调标准掉落 LivingEntity#dropAllDeathLoot(DamageSource)，补回因 die 被拦截丢失的掉落/经验。 */
    public static void dropAllDeathLoot(LivingEntity target, DamageSource source) {
        Method method = resolveDropMethod();
        if (method == null) return;
        try {
            method.invoke(target, source);
        } catch (ReflectiveOperationException ignored) {
            // 补掉落失败不应影响强制移除流程
        }
    }

    private static Method resolveDropMethod() {
        if (dropMethodResolved) return dropAllDeathLootMethod;
        dropMethodResolved = true;
        for (String name : new String[]{"m_6668_", "dropAllDeathLoot"}) {
            try {
                Method method = LivingEntity.class.getDeclaredMethod(name, DamageSource.class);
                method.setAccessible(true);
                dropAllDeathLootMethod = method;
                break;
            } catch (NoSuchMethodException ignored) {
                // 换下一个命名空间再试
            }
        }
        return dropAllDeathLootMethod;
    }

    /** 解析 SynchedEntityData 里存放数据项的容器（按字段类型识别，避免依赖被混淆的字段名）。 */
    private static Object resolveItems(SynchedEntityData data) throws IllegalAccessException {
        if (!itemsFieldResolved) {
            itemsFieldResolved = true;
            for (Field field : SynchedEntityData.class.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers())) continue;
                Class<?> type = field.getType();
                boolean isMap = Map.class.isAssignableFrom(type);
                boolean isObjectArray = type.isArray() && !type.getComponentType().isPrimitive();
                if (isMap || isObjectArray) {
                    field.setAccessible(true);
                    itemsField = field;
                    break;
                }
            }
        }
        return itemsField == null ? null : itemsField.get(data);
    }

    private static List<Object> collectItems(Object container) {
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

    /** 解析数据项里存放实际值的字段：泛型擦除后其声明类型为 Object，无需依赖字段名。 */
    private static Field resolveValueField(Class<?> itemClass) {
        if (itemClass == valueFieldOwner) return valueFieldCache;
        Field found = null;
        for (Field field : itemClass.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers())) continue;
            if (field.getType() != Object.class) continue;
            field.setAccessible(true);
            found = field;
            break;
        }
        valueFieldOwner = itemClass;
        valueFieldCache = found;
        return found;
    }
}
