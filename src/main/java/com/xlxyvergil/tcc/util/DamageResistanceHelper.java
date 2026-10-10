package com.xlxyvergil.tcc.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 提供常驻比例减伤（source-agnostic）公共 API，采用每 tick 血量对账实现：对每 tick 实际血量下降按保留因子统一削减，
 * 无论伤害来自 hurt()/setHealth() 还是绕过 setHealth 的第三方实现都生效；对账由 DamageResistanceMixin 每服务端 tick 调用 reconcileHealth()。
 */
public final class DamageResistanceHelper {

    /** 常驻比例减伤：每次扣血保留的伤害比例（0~1），0 为完全免伤。 */
    public static final Map<UUID, Float> DAMAGE_RETAIN_MAP = new ConcurrentHashMap<>();
    /** 对账基线：记录实体最近的参考血量（每 tick 末、以及写血出口削减时更新），用于识别血量下降。 */
    public static final Map<UUID, Float> REDUCTION_BASELINE_MAP = new ConcurrentHashMap<>();
    /** 受伤冷却：该实体在剩余 tick 内所有血量下降都会被拦截归零（受击触发式）。 */
    public static final Map<UUID, Integer> COOLDOWN_MAP = new ConcurrentHashMap<>();
    /** 单次受伤上限：限制单次 setHealth 扣血不超过该值（受击触发式）。 */
    public static final Map<UUID, Float> DAMAGE_CAP_MAP = new ConcurrentHashMap<>();
    /** 强制免伤（写血出口）：UUID → 每次写血保留的伤害比例（0~1），0.2 表示免伤 80%，0 完全免伤。 */
    public static final Map<UUID, Float> FORCED_RETAIN_MAP = new ConcurrentHashMap<>();
    /**
     * 受击自动冷却触发器：UUID → 每次实际扣血后自动进入的冷却时长（tick）。
     * 已登记实体一旦真正掉血，setHealth 拦截会立即把它写入 COOLDOWN_MAP 进入冷却；冷却期内扣血归零。
     * 用于"先扣一次血、随后无敌一小段、冷却结束再次受击才扣血"的饰品（格蕾修 / 繁星 / 绘世之卷）。
     */
    public static final Map<UUID, Integer> HURT_COOLDOWN_MAP = new ConcurrentHashMap<>();

    /**
     * 登记标记键：在实体自身的持久化数据（NBT）中打标，表示该实体佩戴了相关饰品、需承受本类的减伤/冷却/上限逻辑。
     * <p>
     * 标记随实体一同保存/加载（区块重载、跨维度、重登后依然有效），无需维护全局集合与清理。
     * mixin 的每 tick / setHealth 钩子入口先读该标记，未打标实体直接返回，从而避免为绝大多数生物
     * 计算 UUID 并多次查表。对任意 LivingEntity（玩家、女仆等）通用。
     */
    private static final String TRACKED_KEY = "tcc_damage_tracked";

    private DamageResistanceHelper() {}

    /** 实体是否已被登记（佩戴了相关饰品，需承受本类的减伤/冷却/上限逻辑）。 */
    public static boolean isTracked(LivingEntity entity) {
        return entity != null && entity.getPersistentData().getBoolean(TRACKED_KEY);
    }

    /** 为实体打上登记标记（幂等，已打标时不重复写入）。 */
    private static void markTracked(LivingEntity entity) {
        CompoundTag data = entity.getPersistentData();
        if (!data.getBoolean(TRACKED_KEY)) {
            data.putBoolean(TRACKED_KEY, true);
        }
    }

    /** 重新评估实体是否仍需被登记：四个状态表均无记录时清除标记。 */
    public static void refreshTracked(LivingEntity entity) {
        if (entity == null) return;
        UUID id = entity.getUUID();
        boolean active = DAMAGE_RETAIN_MAP.containsKey(id)
                || COOLDOWN_MAP.containsKey(id)
                || HURT_COOLDOWN_MAP.containsKey(id)
                || DAMAGE_CAP_MAP.containsKey(id)
                || FORCED_RETAIN_MAP.containsKey(id);
        CompoundTag data = entity.getPersistentData();
        if (active) {
            if (!data.getBoolean(TRACKED_KEY)) {
                data.putBoolean(TRACKED_KEY, true);
            }
        } else if (data.contains(TRACKED_KEY)) {
            data.remove(TRACKED_KEY);
        }
    }

    /**
     * 设置受伤冷却（受击触发式）：cooldownTicks 内该实体所有血量下降都会被拦截归零。
     */
    public static void setDamageCooldown(LivingEntity entity, int cooldownTicks) {
        if (entity == null || cooldownTicks <= 0) return;
        COOLDOWN_MAP.put(entity.getUUID(), cooldownTicks);
        markTracked(entity);
    }

    public static void clearDamageCooldown(LivingEntity entity) {
        if (entity != null) {
            COOLDOWN_MAP.remove(entity.getUUID());
            refreshTracked(entity);
        }
    }

    /**
     * 登记/更新"受击后自动进入冷却"的时长（tick）：实体每次实际扣血后由 setHealth 拦截自动写入 COOLDOWN_MAP 进入冷却；
     * 传入 &lt;= 0 视为解除。装备方需每 tick 刷新（武器限制变化时同步刷新/清除）。首次扣血照常生效，不会被打断。
     */
    public static void setHurtCooldown(LivingEntity entity, int cooldownTicks) {
        if (entity == null) return;
        if (cooldownTicks <= 0) {
            clearHurtCooldown(entity);
            return;
        }
        HURT_COOLDOWN_MAP.put(entity.getUUID(), cooldownTicks);
        markTracked(entity);
    }

    public static void clearHurtCooldown(LivingEntity entity) {
        if (entity != null) {
            HURT_COOLDOWN_MAP.remove(entity.getUUID());
            refreshTracked(entity);
        }
    }

    /**
     * 设置单次受伤上限（受击触发式）：限制单次 setHealth 扣血不超过 maxDamage。
     */
    public static void setDamageCap(LivingEntity entity, float maxDamage) {
        if (entity == null || maxDamage <= 0) return;
        DAMAGE_CAP_MAP.put(entity.getUUID(), maxDamage);
        markTracked(entity);
    }

    public static void clearDamageCap(LivingEntity entity) {
        if (entity != null) {
            DAMAGE_CAP_MAP.remove(entity.getUUID());
            refreshTracked(entity);
        }
    }

    public static void clearAll(LivingEntity entity) {
        if (entity != null) {
            UUID id = entity.getUUID();
            COOLDOWN_MAP.remove(id);
            DAMAGE_CAP_MAP.remove(id);
            HURT_COOLDOWN_MAP.remove(id);
            DAMAGE_RETAIN_MAP.remove(id);
            FORCED_RETAIN_MAP.remove(id);
            REDUCTION_BASELINE_MAP.remove(id);
            refreshTracked(entity);
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
        Float previous = DAMAGE_RETAIN_MAP.get(id);
        // 比例与佩戴状态均未变化：跳过重复写入与打标（佩戴方每 tick 刷新时的常见路径）。
        if (previous != null && previous.floatValue() == retainedFactor) {
            return;
        }
        DAMAGE_RETAIN_MAP.put(id, retainedFactor);
        // 仅首次进入保护时重置基线；持续佩戴/切换比例时不重置，否则每 tick 清零基线会使对账无从比较。
        if (previous == null) {
            REDUCTION_BASELINE_MAP.remove(id);
        }
        markTracked(entity);
    }

    public static void clearDamageReduction(LivingEntity entity) {
        if (entity != null) {
            UUID id = entity.getUUID();
            if (DAMAGE_RETAIN_MAP.remove(id) == null) {
                // 本无比例减伤登记：无需触碰基线与打标状态
                return;
            }
            REDUCTION_BASELINE_MAP.remove(id);
            refreshTracked(entity);
        }
    }

    /** 设置强制免伤（写血出口）：每次扣血按 retainedFactor 保留（0~1），0.2 表示免伤 80%，0 完全免伤；1 视为解除。 */
    public static void setForcedDamageReduction(LivingEntity entity, float retainedFactor) {
        if (entity == null || retainedFactor < 0.0F) return;
        if (retainedFactor >= 1.0F) {
            clearForcedDamageReduction(entity);
            return;
        }
        UUID id = entity.getUUID();
        Float previous = FORCED_RETAIN_MAP.get(id);
        // 比例与佩戴状态均未变化：跳过重复写入与打标（佩戴方每 tick 刷新时的常见路径）。
        if (previous != null && previous.floatValue() == retainedFactor) {
            return;
        }
        FORCED_RETAIN_MAP.put(id, retainedFactor);
        markTracked(entity);
    }

    public static void clearForcedDamageReduction(LivingEntity entity) {
        if (entity != null) {
            if (FORCED_RETAIN_MAP.remove(entity.getUUID()) == null) {
                // 本无强制免伤登记：无需刷新打标状态
                return;
            }
            refreshTracked(entity);
        }
    }

    /** 写血出口已按保留因子削减并落盘：同步对账基线，避免每 tick 对账对同一次扣血二次削减。 */
    public static void updateForcedBaseline(LivingEntity entity, float writtenHealth) {
        if (entity == null) return;
        UUID id = entity.getUUID();
        if (FORCED_RETAIN_MAP.containsKey(id)) {
            REDUCTION_BASELINE_MAP.put(id, writtenHealth);
        }
    }

    /**
     * 服务端每 tick 调用：与基线比较，下降量按保留因子削减后回写（治疗仅更新基线）；覆盖绕过写血出口的血量下降。
     */
    public static void reconcileHealth(LivingEntity entity) {
        if (entity == null || entity.level().isClientSide) return;
        UUID id = entity.getUUID();
        Float retain = DAMAGE_RETAIN_MAP.get(id);
        if (retain == null) {
            // 写血出口强制免伤：兜底拦截绕过写血出口（如反射直写 DataItem.value）的血量下降
            retain = FORCED_RETAIN_MAP.get(id);
        }
        if (retain == null) {
            // 仅在确有历史基线时才做移除，避免无人登记时对每个生物每 tick 做一次无谓的哈希表删除
            if (!REDUCTION_BASELINE_MAP.isEmpty()) {
                REDUCTION_BASELINE_MAP.remove(id);
            }
            return;
        }

        float now = entity.getHealth();

        // 血量归零致死处理：非完全减伤时让位给饰品的死亡取消流程（onLivingDeath）；
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
            if (!Float.isFinite(rawDrop)) {
                // 异常写入（如直接写入 -Inf 的致死哨兵）：直接回到基线血量
                entity.setHealth(last);
                REDUCTION_BASELINE_MAP.put(id, last);
                return;
            }
            float reducedHealth = last - rawDrop * retain;
            entity.setHealth(reducedHealth);
            REDUCTION_BASELINE_MAP.put(id, reducedHealth);
        } else {
            // 血量持平或治疗，更新基线
            REDUCTION_BASELINE_MAP.put(id, now);
        }
    }
}
