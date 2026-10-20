package com.xlxyvergil.tcc.client.renderer;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.client.LootrHighlightClientData;
import com.xlxyvergil.tcc.compat.lootr.LootrCompat;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

/**
 * 客户端渲染：把 LootrHighlightClientData 中记录的战利品目标交给 HighlightOutlineRenderer
 * 画成方块线框轮廓（隔墙可见）。
 *
 * 箱子（方块实体）画绿，运输矿车（实体）画蓝——两组坐标由客户端在收到时分组。
 * 坐标仅发给触发玩家本人，因此轮廓只在该玩家客户端显示。
 */
@EventBusSubscriber(value = Dist.CLIENT, modid = TaczCurios.MODID)
public final class LootrHighlightsRenderer {

    /** 战利品箱子颜色（绿色）。 */
    private static final float CHEST_RED = 0.22F;
    private static final float CHEST_GREEN = 0.95F;
    private static final float CHEST_BLUE = 0.35F;
    /** 战利品运输矿车颜色（蓝色）。 */
    private static final float CART_RED = 0.30F;
    private static final float CART_GREEN = 0.60F;
    private static final float CART_BLUE = 1.00F;

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
        // 只在收到的 30 秒内绘制
        if (!LootrHighlightClientData.isActive()) {
            return;
        }
        HighlightOutlineRenderer.render(event, LootrHighlightClientData.getChests(),
                CHEST_RED, CHEST_GREEN, CHEST_BLUE);
        HighlightOutlineRenderer.render(event, LootrHighlightClientData.getCarts(),
                CART_RED, CART_GREEN, CART_BLUE);
    }
}
