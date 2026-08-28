package com.xlxyvergil.tcc.util;

import net.minecraft.world.entity.LivingEntity;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 提供常驻比例减伤（source-agnostic）公共 API，采用「每 tick 血量对账」实现：对每 tick 实际血量下降按保留因子统一削减，
 * 无论伤害来自 hurt()/setHealth() 还是绕过 setHealth 的第三方实现都生效；对账由 DamageResistanceMixin 每服务端 tick 调用 reconcileHealth()。
 */
public final class DamageResistanceHelper {

    /** 常驻比例减伤：每次扣血保留的伤害比例（0~1），0 为完全免伤。 */
    public static final Map<UUID, Float> DAMAGE_RETAIN_MAP = new ConcurrentHashMap<>();
    /** 对账基线：记录实体上一 tick 结束时的参考血量，用于识别该 tick 内任意来源的血量下降。 */
    public static final Map<UUID, Float> REDUCTION_BASELINE_MAP = new ConcurrentHashMap<>();
    /** 受伤冷却：该实体在剩余 tick 内所有血量下降都会被拦截归零（受击触发式）。 */
    public static final Map<UUID, Integer> COOLDOWN_MAP = new ConcurrentHashMap<>();
    /** 单次受伤上限：限制单次 setHealth 扣血不超过该值（受击触发式）。 */
    public static final Map<UUID, Float> DAMAGE_CAP_MAP = new ConcurrentHashMap<>();

    private DamageResistanceHelper() {}

    /**
     * 设置受伤冷却（受击触发式）：cooldownTicks 内该实体所有血量下降都会被拦截归零。
     */
    public static void setDamageCooldown(LivingEntity entity, int cooldownTicks) {
        if (entity == null || cooldownTicks <= 0) return;
        COOLDOWN_MAP.put(entity.getUUID(), cooldownTicks);
    }

    public static void clearDamageCooldown(LivingEntity entity) {
        if (entity != null) {
            COOLDOWN_MAP.remove(entity.getUUID());
        }
    }

    /**
     * 设置单次受伤上限（受击触发式）：限制单次 setHealth 扣血不超过 maxDamage。
     */
    public static void setDamageCap(LivingEntity entity, float maxDamage) {
        if (entity == null || maxDamage <= 0) return;
        DAMAGE_CAP_MAP.put(entity.getUUID(), maxDamage);
    }

    public static void clearDamageCap(LivingEntity entity) {
        if (entity != null) {
            DAMAGE_CAP_MAP.remove(entity.getUUID());
        }
    }

    public static void clearAll(LivingEntity entity) {
        if (entity != null) {
            UUID id = entity.getUUID();
            COOLDOWN_MAP.remove(id);
            DAMAGE_CAP_MAP.remove(id);
            DAMAGE_RETAIN_MAP.remove(id);
            REDUCTION_BASELINE_MAP.remove(id);
        }
    }

    /**
     * 设置常驻比例减伤：每 tick 血量下降按 retainedFactor 保留（0~1），0.2 表示减伤 80%，0 完全免伤；任意来源扣血均生效，无需受击触发，卸下饰品时调用 clearDamageReduction 清除。
     */
    public static void setDamageReduction(LivingEntity entity, float retainedFactor) {
        if (entity == null || retainedFactor < 0.0F) return;
        if (retainedFactor >= 1.0F) {
            clearDamageReduction(entity);
            return;
        }
        UUID id = entity.getUUID();
        boolean wasProtected = DAMAGE_RETAIN_MAP.containsKey(id);
        DAMAGE_RETAIN_MAP.put(id, retainedFactor);
        // 仅首次进入保护时重置基线；持续佩戴/切换比例时不重置，否则每 tick 清零基线会使对账无从比较。
        if (!wasProtected) {
            REDUCTION_BASELINE_MAP.remove(id);
        }
    }

    public static void clearDamageReduction(LivingEntity entity) {
        if (entity != null) {
            UUID id = entity.getUUID();
            DAMAGE_RETAIN_MAP.remove(id);
            REDUCTION_BASELINE_MAP.remove(id);
        }
    }

    /**
     * 服务端每 tick 调用：将当前血量与上一 tick 基线比较，下降量即本 tick 累计原始扣血，按保留因子削减后回写；血量持平/上升（治疗）仅更新基线。即使伤害绕过 setHealth，只要最终血量下降也会被削减。
     */
    public static void reconcileHealth(LivingEntity entity) {
        if (entity == null || entity.level().isClientSide) return;
        UUID id = entity.getUUID();
        Float retain = DAMAGE_RETAIN_MAP.get(id);
        if (retain == null) {
            REDUCTION_BASELINE_MAP.remove(id);
            return;
        }

        float now = entity.getHealth();

        // 血量归零致死处理：非完全减伤时让位给饰品自身的死亡取消/复活流程；
        // 完全减伤（retain <= 0）时仍按对账回写血量，避免任何来源把玩家直接打死。
        if (entity.isDeadOrDying() && now <= 0.0F && retain > 0.0F) {
            REDUCTION_BASELINE_MAP.remove(id);
            return;
        }

        Float last = REDUCTION_BASELINE_MAP.get(id);
        if (last == null) {
            REDUCTION_BASELINE_MAP.put(id, now);
            return;
        }

        if (now < last - 0.0001F) {
            // 本 tick 内血量下降（任意来源）
            float rawDrop = last - now;
            float reducedHealth = last - rawDrop * retain;
            entity.setHealth(reducedHealth);
            REDUCTION_BASELINE_MAP.put(id, reducedHealth);
        } else {
            // 血量持平或治疗，更新基线
            REDUCTION_BASELINE_MAP.put(id, now);
        }
    }
}
