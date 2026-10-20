package com.xlxyvergil.tcc.util;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.ForgeRegistries;

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
    public static Map<MobEffect, MobEffectInstance> guarded(LivingEntity entity, Map<MobEffect, MobEffectInstance> original) {
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
        var key = ForgeRegistries.MOB_EFFECTS.getKey(effect);
        return key != null && key.getNamespace().equals("tcc");
    }
}
