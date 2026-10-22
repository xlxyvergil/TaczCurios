package com.xlxyvergil.tcc.client;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 客户端保存的"当前玩家周围的刷怪笼"坐标集合。
 * 由 SyncSpawnerHighlightsS2CPacket 从服务端同步而来，供线框高亮渲染使用。
 *
 * 每次收到下发改写 expiresAt：轮廓只在收到的 DURATION_TICKS tick 内显示，
 * 到点自动不再绘制（坐标仍留着，等下一次下发覆盖）。
 */
@OnlyIn(Dist.CLIENT)
public final class SpawnerHighlightClientData {

    /** 高亮渲染持续时间（tick）：30 秒。 */
    private static final long DURATION_TICKS = 30L * 20L;

    private static final Set<BlockPos> HIGHLIGHTS = new HashSet<>();

    /** 本批坐标的到期 gameTime；0 表示从未收到过。 */
    private static long expiresAt;

    private SpawnerHighlightClientData() {}

    /** 用服务端下发的最新坐标覆盖旧集合并重新计时（空集合 = 清除全部残留轮廓）。 */
    public static void setHighlights(List<BlockPos> positions) {
        HIGHLIGHTS.clear();
        if (positions != null) {
            HIGHLIGHTS.addAll(positions);
        }
        expiresAt = nowTicks() + DURATION_TICKS;
    }

    /** 清空全部坐标（退出世界时用），与 LootrHighlightClientData.clear() 保持一致。 */
    public static void clear() {
        HIGHLIGHTS.clear();
        expiresAt = 0L;
    }

    public static Set<BlockPos> getHighlights() {
        return HIGHLIGHTS;
    }

    /** 是否还有未到期的坐标可画。 */
    public static boolean isActive() {
        return !HIGHLIGHTS.isEmpty() && nowTicks() < expiresAt;
    }

    private static long nowTicks() {
        Minecraft minecraft = Minecraft.getInstance();
        return minecraft.level == null ? 0L : minecraft.level.getGameTime();
    }
}
