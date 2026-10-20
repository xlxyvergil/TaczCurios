package com.xlxyvergil.tcc.client.renderer;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.client.SpawnerHighlightClientData;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

/**
 * 客户端渲染：把 SpawnerHighlightClientData 中记录的每一个刷怪笼
 * 交给 HighlightOutlineRenderer 画成红色线框轮廓（隔墙可见）。
 *
 * 坐标由服务端下发、仅发给该玩家本人，因此轮廓只在对应玩家客户端显示。
 */
@EventBusSubscriber(value = Dist.CLIENT, modid = TaczCurios.MODID)
public final class SpawnerHighlightsRenderer {

    /** 刷怪笼轮廓颜色（红色）。 */
    private static final float RED = 0.95F;
    private static final float GREEN = 0.20F;
    private static final float BLUE = 0.20F;

    private SpawnerHighlightsRenderer() {}

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES) {
            return;
        }
        // 只在收到的 30 秒内绘制
        if (!SpawnerHighlightClientData.isActive()) {
            return;
        }
        HighlightOutlineRenderer.render(event, SpawnerHighlightClientData.getHighlights(), RED, GREEN, BLUE);
    }
}
