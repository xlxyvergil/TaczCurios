package com.xlxyvergil.tcc.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 通过实体 persistentData 写入停止截止时间，由 AiStopHandler 每 tick 执行定身；目标死亡/卸载后 NBT 自动清空，效果不跨生命周期。
 */
public final class AiStopHelper {

    public static final String AI_STOP_UNTIL_KEY = "tcc_ai_stop_until";

    /** 记录定身前的原始 NoAI 状态，用于定身结束后恢复。 */
    public static final String AI_STOP_PREV_NOAI_KEY = "tcc_ai_stop_prev_noai";

    /**
     * 当前处于定身中的实体 UUID 集合，仅作为 AiStopHandler 的快速路径提示：
     * 集合为空时可跳过对全部生物逐 tick 读取 persistentData。
     * <p>
     * 该集合不是权威数据（权威数据是各实体自身的 NBT）：即使因异常卸载而残留条目，
     * 也只会退化为「没有优化」，不会影响定身逻辑本身的正确性。
     */
    private static final Set<UUID> ACTIVE_STOPS = ConcurrentHashMap.newKeySet();

    private AiStopHelper() {
    }

    /** 使目标停止 AI 指定时长（tick）；客户端不生效。 */
    public static void apply(LivingEntity target, int durationTicks) {
        if (target == null || target.level().isClientSide) {
            return;
        }
        CompoundTag data = target.getPersistentData();
        long now = target.level().getGameTime();
        boolean wasActive = data.getLong(AI_STOP_UNTIL_KEY) > now;
        data.putLong(AI_STOP_UNTIL_KEY, now + durationTicks);
        if (!wasActive) {
            ACTIVE_STOPS.add(target.getUUID());
        }
    }

    public static boolean isStopped(LivingEntity entity) {
        return entity.getPersistentData().getLong(AI_STOP_UNTIL_KEY) > entity.level().getGameTime();
    }

    /** 是否存在任何进行中的定身（快速路径判空）。 */
    public static boolean hasAnyActive() {
        return !ACTIVE_STOPS.isEmpty();
    }

    /** 补登记：供 handler 处理历史遗留（重启前写入）的定身 NBT 时使用。 */
    public static void markActive(LivingEntity entity) {
        if (entity != null) {
            ACTIVE_STOPS.add(entity.getUUID());
        }
    }

    /** 解除登记：定身结束、实体死亡或离开世界时调用。 */
    public static void clearActive(LivingEntity entity) {
        if (entity != null) {
            ACTIVE_STOPS.remove(entity.getUUID());
        }
    }

    /** 清空全部登记：世界加载时用于重新同步。 */
    public static void clearAllActive() {
        ACTIVE_STOPS.clear();
    }
}
