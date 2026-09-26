package com.xlxyvergil.tcc.client.renderer;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.client.SpawnerHighlightClientData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 客户端渲染：把 {@link SpawnerHighlightClientData} 中记录的每一个刷怪笼
 * 交给 {@link HighlightPillarRenderer} 画成红色光柱。
 *
 * <p>坐标由服务端下发、仅发给该玩家本人，因此光柱只在对应玩家客户端显示。</p>
 */
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class SpawnerHighlightsRenderer {

    /** 刷怪笼光柱颜色（红色）。 */
    private static final float RED = 0.95F;
    private static final float GREEN = 0.20F;
    private static final float BLUE = 0.20F;

    private SpawnerHighlightsRenderer() {}

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES) {
            return;
        }
        HighlightPillarRenderer.render(event, SpawnerHighlightClientData.getHighlights(), RED, GREEN, BLUE);
    }
}
