package com.xlxyvergil.tcc.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.client.LootrHighlightClientData;
import com.xlxyvergil.tcc.compat.lootr.LootrCompat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;

/**
 * 客户端渲染：在 {@link RenderLevelStageEvent} 阶段，对 {@link LootrHighlightClientData} 中记录的
 * 每一个"该玩家尚未开启的 Lootr 箱子"，绘制一根从箱子坐标向上 16 格的半透明光柱。
 *
 * <p>坐标由服务端下发、仅发给该玩家本人，因此光柱只在对应玩家客户端显示。</p>
 */
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class LootrHighlightsRenderer {

    /** 光柱高度（格）。 */
    private static final double HEIGHT = 16.0D;
    /** 光柱截面半宽（格）。 */
    private static final double TOP_RADIUS = 0.20D;
    /** 光柱颜色（金色系，呼应"战利品"）。 */
    private static final float RED = 1.0F;
    private static final float GREEN = 0.82F;
    private static final float BLUE = 0.22F;
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

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.disableCull();

        BufferBuilder builder = Tesselator.getInstance().getBuilder();
        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        for (BlockPos pos : LootrHighlightClientData.getHighlights()) {
            // 将世界坐标转换为相对相机坐标（RenderLevelStageEvent 的 poseStack 已按相机平移）
            double x = pos.getX() + 0.5D - camPos.x;
            double y = pos.getY() - camPos.y;
            double z = pos.getZ() + 0.5D - camPos.z;
            renderPillar(builder, mat, x, y, z);
        }

        BufferUploader.drawWithShader(builder.end());

        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }

    /** 绘制一根从 y 到 y+HEIGHT、截面半宽 TOP_RADIUS 的方形光柱（4 个侧面 + 顶部）。 */
    private static void renderPillar(BufferBuilder builder, Matrix4f mat, double x, double y, double z) {
        double x0 = x - TOP_RADIUS;
        double x1 = x + TOP_RADIUS;
        double z0 = z - TOP_RADIUS;
        double z1 = z + TOP_RADIUS;
        double y1 = y + HEIGHT;

        float r = RED, g = GREEN, b = BLUE, a = ALPHA;

        // z = z0 面
        vertex(builder, mat, x0, y, z0, r, g, b, a);
        vertex(builder, mat, x1, y, z0, r, g, b, a);
        vertex(builder, mat, x1, y1, z0, r, g, b, a);
        vertex(builder, mat, x0, y1, z0, r, g, b, a);

        // z = z1 面
        vertex(builder, mat, x0, y, z1, r, g, b, a);
        vertex(builder, mat, x1, y, z1, r, g, b, a);
        vertex(builder, mat, x1, y1, z1, r, g, b, a);
        vertex(builder, mat, x0, y1, z1, r, g, b, a);

        // x = x0 面
        vertex(builder, mat, x0, y, z0, r, g, b, a);
        vertex(builder, mat, x0, y, z1, r, g, b, a);
        vertex(builder, mat, x0, y1, z1, r, g, b, a);
        vertex(builder, mat, x0, y1, z0, r, g, b, a);

        // x = x1 面
        vertex(builder, mat, x1, y, z0, r, g, b, a);
        vertex(builder, mat, x1, y, z1, r, g, b, a);
        vertex(builder, mat, x1, y1, z1, r, g, b, a);
        vertex(builder, mat, x1, y1, z0, r, g, b, a);

        // 顶部
        vertex(builder, mat, x0, y1, z0, r, g, b, a);
        vertex(builder, mat, x1, y1, z0, r, g, b, a);
        vertex(builder, mat, x1, y1, z1, r, g, b, a);
        vertex(builder, mat, x0, y1, z1, r, g, b, a);
    }

    private static void vertex(BufferBuilder builder, Matrix4f mat, double x, double y, double z,
                               float r, float g, float b, float a) {
        builder.vertex(mat, (float) x, (float) y, (float) z).color(r, g, b, a).endVertex();
    }
}
