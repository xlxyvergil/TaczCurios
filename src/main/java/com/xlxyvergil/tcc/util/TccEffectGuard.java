package com.xlxyvergil.tcc.util;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

/** 效果保护的公共判据、开关和 getter 视图缓存，三层保护共用。 */
public final class TccEffectGuard {

    private TccEffectGuard() {
    }

    /** 装饰器开关，初值 true；配置载入/重载时刷新。 */
    private static volatile boolean mapGuard = true;

    /** 每只实体复用一个视图，保证 getActiveEffectsMap() 的身份稳定。 */
    private static final Map<LivingEntity, GuardedEffectMap> VIEWS =
            Collections.synchronizedMap(new WeakHashMap<>());

    public static void setMapGuardFromConfig() {
        mapGuard = !TaczCuriosConfig.COMMON_SPEC.isLoaded()
                || TaczCuriosConfig.COMMON.coreEffectMapGuard.get();
    }

    public static boolean mapGuardEnabled() {
        return mapGuard;
    }

    /** 给 getActiveEffects() 用的受控视图；开关关闭或表为空时原样返回。 */
    public static Map<Holder<MobEffect>, MobEffectInstance> guarded(LivingEntity entity, Map<Holder<MobEffect>, MobEffectInstance> original) {
        if (original == null || !mapGuard) {
            return original;
        }
        synchronized (VIEWS) {
            GuardedEffectMap view = VIEWS.get(entity);
            if (view != null && view.backingMap() == original) {
                return view;
            }
            view = new GuardedEffectMap(entity, original);
            VIEWS.put(entity, view);
            return view;
        }
    }

    /** 是否为 tcc 命名空间下注册的效果。 */
    public static boolean isOwnEffect(MobEffect effect) {
        if (effect == null) {
            return false;
        }
        ResourceLocation key = BuiltInRegistries.MOB_EFFECT.getKey(effect);
        return key != null && key.getNamespace().equals(TccEffectGuard.NAMESPACE);
    }

    /** 1.21 起 removeEffect / 事件都以 Holder 传递效果，这里直接按 key 判定，免去一次反向查表。 */
    public static boolean isOwnEffect(Holder<?> effect) {
        if (effect == null) {
            return false;
        }
        return effect.unwrapKey()
                .map(key -> key.location().getNamespace().equals(TccEffectGuard.NAMESPACE))
                .orElse(false);
    }

    /** 本 mod 的命名空间。 */
    public static final String NAMESPACE = "tcc";
}
