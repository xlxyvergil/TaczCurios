package com.xlxyvergil.tcc.util;

import net.minecraft.world.entity.LivingEntity;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 减伤工具类（非 Mixin），提供限伤/受伤冷却的公共 API。
 * 数据在 {@link com.xlxyvergil.tcc.mixin.DamageResistanceMixin} 的 setHealth 和 tick 注入中消费。
 */
public final class DamageResistanceHelper {

    public static final Map<UUID, Integer> COOLDOWN_MAP = new ConcurrentHashMap<>();
    public static final Map<UUID, Float> DAMAGE_CAP_MAP = new ConcurrentHashMap<>();

    private DamageResistanceHelper() {}

    /**
     * 设置受伤冷却（受击触发式）。
     * 调用后，该实体在 {@code cooldownTicks} 内受到的所有血量下降都会被拦截归零。
     */
    public static void setDamageCooldown(LivingEntity entity, int cooldownTicks) {
        if (entity == null || cooldownTicks <= 0) return;
        COOLDOWN_MAP.put(entity.getUUID(), cooldownTicks);
    }

    /**
     * 设置单次受伤上限（受击触发式）。
     * 需在 LivingHurtEvent 中持续调用以维持上限，卸下饰品时调用 {@link #clearDamageCap} 清除。
     */
    public static void setDamageCap(LivingEntity entity, float maxDamage) {
        if (entity == null || maxDamage <= 0) return;
        DAMAGE_CAP_MAP.put(entity.getUUID(), maxDamage);
    }

    /** 移除受伤冷却状态。 */
    public static void clearDamageCooldown(LivingEntity entity) {
        if (entity != null) {
            COOLDOWN_MAP.remove(entity.getUUID());
        }
    }

    /** 移除受伤上限状态。 */
    public static void clearDamageCap(LivingEntity entity) {
        if (entity != null) {
            DAMAGE_CAP_MAP.remove(entity.getUUID());
        }
    }

    /** 移除所有减伤状态。 */
    public static void clearAll(LivingEntity entity) {
        if (entity != null) {
            UUID id = entity.getUUID();
            COOLDOWN_MAP.remove(id);
            DAMAGE_CAP_MAP.remove(id);
        }
    }
}
