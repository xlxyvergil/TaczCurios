package com.xlxyvergil.tcc.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 客户端保存的"谁的真我结界还开着、还剩多少 tick"。
 *
 * 键是实体 id，值是剩余 tick。服务端在按键开启结界时下发一次，
 * 之后由客户端每 tick 递减——不经过任何效果（buff），
 * 因此：
 * <ul>
 *   <li>渲染只由"真的按了键并开了结界"驱动，外部塞进来的同名效果不会误触发；</li>
 *   <li>喝牛奶、/effect clear 之类与它无关，不存在失同步。</li>
 * </ul>
 * 结界现在只靠时间结束，所以本地倒计时与服务端计时天然一致。
 */
@OnlyIn(Dist.CLIENT)
public final class ZhenWoBarrierClientData {

    /** 实体 id → 剩余 tick。 */
    private static final Map<Integer, Integer> REMAINING = new HashMap<>();

    private ZhenWoBarrierClientData() {}

    /** 服务端下发：覆盖该实体的剩余时间（0 表示立即关闭）。 */
    public static void set(int entityId, int remainingTicks) {
        if (remainingTicks <= 0) {
            REMAINING.remove(entityId);
        } else {
            REMAINING.put(entityId, remainingTicks);
        }
    }

    /** 该实体当前剩余的结界 tick；0 表示没开或已结束。 */
    public static int ticksLeft(int entityId) {
        Integer ticks = REMAINING.get(entityId);
        return ticks == null ? 0 : ticks;
    }

    /** 每客户端 tick 递减一次，归零即移除。 */
    public static void tick() {
        if (REMAINING.isEmpty()) {
            return;
        }
        Iterator<Map.Entry<Integer, Integer>> iterator = REMAINING.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Integer> entry = iterator.next();
            int left = entry.getValue() - 1;
            if (left <= 0) {
                iterator.remove();
            } else {
                entry.setValue(left);
            }
        }
    }

    /** 退出世界/切换维度时清空，避免残留。 */
    public static void clear() {
        REMAINING.clear();
    }
}
