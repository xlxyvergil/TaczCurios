package com.xlxyvergil.tcc.client.renderer;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.client.SpawnerHighlightClientData;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

/**
 * 客户端渲染：把 {@link SpawnerHighlightClientData} 中记录的每一个刷怪笼
 * 交给 {@link HighlightPillarRenderer} 画成绿色光柱。
 *
 * <p>坐标由服务端下发、仅发给该玩家本人，因此光柱只在对应玩家客户端显示。</p>
 */
@EventBusSubscriber(value = Dist.CLIENT, modid = TaczCurios.MODID)
public final class SpawnerHighlightsRenderer {

    private SpawnerHighlightsRenderer() {}

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES) {
            return;
        }
        HighlightPillarRenderer.render(event, SpawnerHighlightClientData.getHighlights());
    }
}
