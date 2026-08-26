package com.xlxyvergil.tcc.util;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 减伤工具类（非 Mixin），提供常驻比例减伤的公共 API。
 * <p>
 * 常驻比例减伤为「通用（source-agnostic）」实现：
 * 不同于只在 {@code LivingEntity.setHealth} 层面拦截的旧方案，
 * 这里采用「每 tick 血量对账（reconcile）」——对佩戴者每 tick 实际发生的血量下降
 * 重新按保留因子修正。这样无论伤害是通过标准 {@code hurt()}、{@code setHealth()}，
 * 还是绕过 {@code setHealth} 直接写入 {@link net.minecraft.network.syncher.SynchedEntityData}
 * 血量的第三方实现（如经 {@code ActuallyHurt} 的 {@code catchSetTrueHealth}），
 * 只要最终血量下降，都会被统一削减，无需针对任何特定 Mod。
 * <p>
 * 对账逻辑在 {@link com.xlxyvergil.tcc.mixin.DamageResistanceMixin} 的 tick 中每服务端 tick 调用
 * {@link #reconcileHealth(LivingEntity)}。
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
     * 设置受伤冷却（受击触发式）。调用后该实体在 {@code cooldownTicks} 内受到的所有血量下降都会被拦截归零。
     */
    public static void setDamageCooldown(LivingEntity entity, int cooldownTicks) {
        if (entity == null || cooldownTicks <= 0) return;
        COOLDOWN_MAP.put(entity.getUUID(), cooldownTicks);
    }

    /** 移除受伤冷却状态。 */
    public static void clearDamageCooldown(LivingEntity entity) {
        if (entity != null) {
            COOLDOWN_MAP.remove(entity.getUUID());
        }
    }

    /**
     * 设置单次受伤上限（受击触发式）。限制单次 setHealth 扣血不超过 {@code maxDamage}。
     */
    public static void setDamageCap(LivingEntity entity, float maxDamage) {
        if (entity == null || maxDamage <= 0) return;
        DAMAGE_CAP_MAP.put(entity.getUUID(), maxDamage);
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
            DAMAGE_RETAIN_MAP.remove(id);
            REDUCTION_BASELINE_MAP.remove(id);
        }
    }

    /**
     * 设置常驻比例减伤（通用减伤）。
     * 该方法会将每 tick 实际发生的血量下降按 {@code retainedFactor} 保留（0~1），
     * 例如 0.2 表示减伤 80%，0 表示减伤 100%（完全免伤）。
     * 对任意来源的扣血均生效（标准 hurt/setHealth 或绕过 setHealth 的直接写入），无需受击触发。
     * 卸下饰品时应调用 {@link #clearDamageReduction} 清除。
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
        // 仅当实体首次进入保护状态时重置基线；持续佩戴/结界切换减伤比例时不重置，
        // 否则每 tick 清零基线会导致对账无从比较，伤害无法被削减。
        if (!wasProtected) {
            REDUCTION_BASELINE_MAP.remove(id);
        }
    }

    /** 移除常驻比例减伤状态。 */
    public static void clearDamageReduction(LivingEntity entity) {
        if (entity != null) {
            UUID id = entity.getUUID();
            DAMAGE_RETAIN_MAP.remove(id);
            REDUCTION_BASELINE_MAP.remove(id);
        }
    }

    /**
     * 服务端每 tick 对常驻比例减伤实体执行一次血量对账（通用方法）。
     * <p>
     * 在实体本 tick 结束时，将其当前血量与上一 tick 结束时记录的对账基线比较：
     * <ul>
     *   <li>血量下降 → 该下降量即为本 tick 累计的原始扣血，按保留因子削减后回写；</li>
     *   <li>血量持平/上升（治疗）→ 仅更新基线。</li>
     * </ul>
     * 因此即使伤害绕过了 {@code setHealth}，只要最终血量下降，也会被统一削减。
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

        // 血量归零致死的处理：仅在「非完全减伤」时让位给饰品自身的死亡取消/复活流程。
        // 完全减伤（retain <= 0，如真我结界期间 100%）时仍按对账回写血量，避免任何来源
        // （含绕过 setHealth 直接写血量的第三方实现）把玩家直接打死。
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
            logReconciliation(entity, rawDrop, reducedHealth, retain);
            REDUCTION_BASELINE_MAP.put(id, reducedHealth);
        } else {
            // 血量持平或治疗，更新基线
            REDUCTION_BASELINE_MAP.put(id, now);
        }
    }

    /** 限伤诊断最近一次输出 tick（节流，防高频刷屏） */
    private static final Map<UUID, Integer> LAST_LOG_TICK = new ConcurrentHashMap<>();

    /**
     * 对账减伤诊断：在聊天框向佩戴者（玩家）输出本次削减情况。
     * 节流——距上次输出不足 5 tick 则跳过，避免高频伤害刷屏。仅服务端向客户端发送。
     */
    private static void logReconciliation(LivingEntity entity, float rawDrop, float reducedHealth, float retain) {
        if (!(entity instanceof Player player) || player.level().isClientSide) return;

        int now = entity.tickCount;
        Integer last = LAST_LOG_TICK.get(entity.getUUID());
        if (last != null && now - last < 5) return;
        LAST_LOG_TICK.put(entity.getUUID(), now);

        float appliedDrop = rawDrop * retain;
        String msg = String.format("§e[TCC限伤] 原伤害 %.1f → 实际扣血 %.1f（保留因子 %.0f%%）",
                rawDrop, appliedDrop, retain * 100);
        player.displayClientMessage(Component.literal(msg), false);
    }
}
