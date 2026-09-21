package com.xlxyvergil.tcc.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.client.LootrHighlightClientData;
import com.xlxyvergil.tcc.compat.lootr.LootrCompat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;
import org.joml.Matrix4f;

/**
 * 客户端渲染：在 {@link RenderLevelStageEvent} 阶段，对 {@link LootrHighlightClientData} 中记录的
 * 每一个"该玩家尚未开启的 Lootr 箱子"，绘制一根从箱子坐标向上 16 格的半透明光柱。
 *
 * <p>坐标由服务端下发、仅发给该玩家本人，因此光柱只在对应玩家客户端显示。</p>
 */
@EventBusSubscriber(value = Dist.CLIENT, modid = TaczCurios.MODID)
public final class LootrHighlightsRenderer {

    /** 光柱高度（格）。 */
    private static final double HEIGHT = 16.0D;
    /** 光柱截面半宽（格）。 */
    private static final double TOP_RADIUS = 0.20D;
    /** 光柱颜色（绿色）。 */
    private static final float RED = 0.22F;
    private static final float GREEN = 0.95F;
    private static final float BLUE = 0.35F;
    private static final float ALPHA = 0.45F;

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
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return;
        }
        if (LootrHighlightClientData.getHighlights().isEmpty()) {
            return;
        }

        PoseStack pose = event.getPoseStack();
        Vec3 camPos = event.getCamera().getPosition();
        Matrix4f mat = pose.last().pose();

        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.debugQuads());

        for (BlockPos pos : LootrHighlightClientData.getHighlights()) {
            // 将世界坐标转换为相对相机坐标（RenderLevelStageEvent 的 poseStack 已按相机平移）
            double x = pos.getX() + 0.5D - camPos.x;
            double y = pos.getY() - camPos.y;
            double z = pos.getZ() + 0.5D - camPos.z;
            renderPillar(consumer, mat, x, y, z);
        }

        bufferSource.endBatch(RenderType.debugQuads());
    }

    /** 绘制一根从 y 到 y+HEIGHT、截面半宽 TOP_RADIUS 的方形光柱（4 个侧面 + 顶部）。 */
    private static void renderPillar(VertexConsumer consumer, Matrix4f mat, double x, double y, double z) {
        double x0 = x - TOP_RADIUS;
        double x1 = x + TOP_RADIUS;
        double z0 = z - TOP_RADIUS;
        double z1 = z + TOP_RADIUS;
        double y1 = y + HEIGHT;

        float r = RED, g = GREEN, b = BLUE, a = ALPHA;

        // z = z0 面
        vertex(consumer, mat, x0, y, z0, r, g, b, a);
        vertex(consumer, mat, x1, y, z0, r, g, b, a);
        vertex(consumer, mat, x1, y1, z0, r, g, b, a);
        vertex(consumer, mat, x0, y1, z0, r, g, b, a);

        // z = z1 面
        vertex(consumer, mat, x0, y, z1, r, g, b, a);
        vertex(consumer, mat, x1, y, z1, r, g, b, a);
        vertex(consumer, mat, x1, y1, z1, r, g, b, a);
        vertex(consumer, mat, x0, y1, z1, r, g, b, a);

        // x = x0 面
        vertex(consumer, mat, x0, y, z0, r, g, b, a);
        vertex(consumer, mat, x0, y, z1, r, g, b, a);
        vertex(consumer, mat, x0, y1, z1, r, g, b, a);
        vertex(consumer, mat, x0, y1, z0, r, g, b, a);

        // x = x1 面
        vertex(consumer, mat, x1, y, z0, r, g, b, a);
        vertex(consumer, mat, x1, y, z1, r, g, b, a);
        vertex(consumer, mat, x1, y1, z1, r, g, b, a);
        vertex(consumer, mat, x1, y1, z0, r, g, b, a);

        // 顶部
        vertex(consumer, mat, x0, y1, z0, r, g, b, a);
        vertex(consumer, mat, x1, y1, z0, r, g, b, a);
        vertex(consumer, mat, x1, y1, z1, r, g, b, a);
        vertex(consumer, mat, x0, y1, z1, r, g, b, a);
    }

    private static void vertex(VertexConsumer consumer, Matrix4f mat, double x, double y, double z,
                               float r, float g, float b, float a) {
        consumer.addVertex(mat, (float) x, (float) y, (float) z).setColor(r, g, b, a);
    }
}
