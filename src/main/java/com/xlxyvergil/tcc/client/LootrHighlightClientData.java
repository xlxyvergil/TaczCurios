package com.xlxyvergil.tcc.client;

import net.minecraft.core.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 客户端保存的"当前玩家尚未开启的 Lootr 箱子"坐标集合。
 * 由 {@code SyncLootrHighlightsS2CPacket} 从服务端同步而来，供光柱渲染使用。
 */
@OnlyIn(Dist.CLIENT)
public final class LootrHighlightClientData {

    private static final Set<BlockPos> HIGHLIGHTS = new HashSet<>();

    private LootrHighlightClientData() {}

    /** 用服务端下发的最新坐标覆盖旧集合（空集合 = 清除全部残留光柱）。 */
    public static void setHighlights(List<BlockPos> positions) {
        HIGHLIGHTS.clear();
        if (positions != null) {
            HIGHLIGHTS.addAll(positions);
        }
    }

    public static Set<BlockPos> getHighlights() {
        return HIGHLIGHTS;
    }
}
