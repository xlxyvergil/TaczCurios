package com.xlxyvergil.tcc.client.renderer;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.client.LootrHighlightClientData;
import com.xlxyvergil.tcc.compat.lootr.LootrCompat;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 客户端渲染：把 {@link LootrHighlightClientData} 中记录的每一个"该玩家尚未开启的 Lootr 箱子"
 * 交给 {@link HighlightPillarRenderer} 画成绿色光柱。
 *
 * 坐标由服务端下发、仅发给该玩家本人，因此光柱只在对应玩家客户端显示。
 */
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class LootrHighlightsRenderer {

    /** 战利品箱光柱颜色（绿色）。 */
    private static final float RED = 0.22F;
    private static final float GREEN = 0.95F;
    private static final float BLUE = 0.35F;

    private LootrHighlightsRenderer() {}

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES) {
            return;
        }
        // Lootr 未安装则不渲染
        if (!LootrCompat.isLoaded()) {
            return;
        }
        HighlightPillarRenderer.render(event, LootrHighlightClientData.getHighlights(), RED, GREEN, BLUE);
    }
}
