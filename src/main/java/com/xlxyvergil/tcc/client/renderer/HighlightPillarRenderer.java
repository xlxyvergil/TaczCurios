package com.xlxyvergil.tcc.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;

import java.util.Collection;

/**
 * 绿色光柱的统一绘制工具：把一批方块坐标画成从方块中心向上 {@link #HEIGHT} 格的半透明绿色光柱。
 *
 * <p>由 Lootr 战利品箱子高亮（{@code LootrHighlightsRenderer}）与刷怪笼高亮
 * （{@code SpawnerHighlightsRenderer}）共同调用，坐标均由服务端定向下发、仅发给触发玩家本人。</p>
 */
public final class HighlightPillarRenderer {

    /** 光柱高度（格）。 */
    private static final double HEIGHT = 16.0D;
    /** 光柱截面半宽（格）。 */
    private static final double TOP_RADIUS = 0.20D;
    /** 光柱颜色（绿色）。 */
    private static final float RED = 0.22F;
    private static final float GREEN = 0.95F;
    private static final float BLUE = 0.35F;
    private static final float ALPHA = 0.45F;

    private HighlightPillarRenderer() {}

    /** 在 {@link RenderLevelStageEvent.Stage#AFTER_ENTITIES} 阶段把给定坐标渲染成绿色光柱；坐标为空时不做任何事。 */
    public static void render(RenderLevelStageEvent event, Collection<BlockPos> positions) {
        if (positions.isEmpty() || Minecraft.getInstance().level == null) {
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

        for (BlockPos pos : positions) {
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
