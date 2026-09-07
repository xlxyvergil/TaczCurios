package com.xlxyvergil.tcc.helpers;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * 钓鱼加速辅助类。
 * 通过将鱼钩"鱼出现前等待"字段 timeUntilLured 做一次性等比压缩，
 * 实现"钓鱼等待时间减少 X%"的效果。每个浮标只压缩一次，
 * 避免每 tick 反复压缩导致等待时间被过度缩短。
 * 不触碰咬钩阶段（timeUntilHooked），从而与钓饵附魔及同类 mod 互不干扰。
 * 字段访问由 META-INF/accesstransformer.cfg 放开为 public。
 */
public final class FishingSpeedHelper {

    /** 记录已加速过的鱼钩，防止一个浮标内反复压缩。 */
    private static final Set<FishingHook> ACCELERATED =
            Collections.newSetFromMap(new WeakHashMap<>());

    private FishingSpeedHelper() {
    }

    /**
     * 若玩家正在钓鱼，将"鱼出现前等待"缩短 reduceRatio（0.3 表示缩短 30%）。
     * 仅服务端有效；每个浮标只压缩一次。
     */
    public static void speedUp(Player player, double reduceRatio) {
        if (player == null || player.level().isClientSide) {
            return;
        }
        FishingHook hook = player.fishing;
        if (hook == null || ACCELERATED.contains(hook)) {
            return;
        }
        int current = hook.timeUntilLured;
        if (current < 2) {
            return;
        }
        hook.timeUntilLured = Math.max(1, (int) Math.round(current * (1.0 - reduceRatio)));
        ACCELERATED.add(hook);
    }
}
