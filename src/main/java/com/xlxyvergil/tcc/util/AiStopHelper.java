package com.xlxyvergil.tcc.util;

import net.minecraft.world.entity.LivingEntity;

/**
 * AI 停止（定身）工具 - 浮生神之键线。
 * <p>
 * 通过实体 persistentData 写入停止截止时间，由 {@code AiStopHandler} 每 tick 执行定身。
 * 目标死亡/卸载后 NBT 自动清空，效果不跨生命周期。
 */
public final class AiStopHelper {

    public static final String AI_STOP_UNTIL_KEY = "tcc_ai_stop_until";

    /** 记录定身前的原始 NoAI 状态，用于定身结束后恢复 */
    public static final String AI_STOP_PREV_NOAI_KEY = "tcc_ai_stop_prev_noai";

    private AiStopHelper() {
    }

    /** 使目标停止 AI 指定时长（tick），客户端忽略 */
    public static void apply(LivingEntity target, int durationTicks) {
        if (target == null || target.level().isClientSide) {
            return;
        }
        target.getPersistentData().putLong(AI_STOP_UNTIL_KEY, target.level().getGameTime() + durationTicks);
    }

    /** 目标当前是否处于定身状态 */
    public static boolean isStopped(LivingEntity entity) {
        return entity.getPersistentData().getLong(AI_STOP_UNTIL_KEY) > entity.level().getGameTime();
    }
}
