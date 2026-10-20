package com.xlxyvergil.tcc.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
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
 * 高亮渲染：把一批方块坐标画成<strong>方块线框轮廓</strong>（12 条棱），纯色、100% 不透明。
 *
 * 由 Lootr 战利品箱子/战利品矿车高亮（LootrHighlightsRenderer，绿色）与刷怪笼高亮
 * （SpawnerHighlightsRenderer，红色）共同调用，颜色由调用方传入。
 *
 * 与之前球形辉光的区别：
 * <ul>
 *   <li>只画棱线，不铺面也不发光，视野干净，叠在一起也不会糊成一团；</li>
 *   <li><strong>关闭深度测试</strong>，轮廓不会被墙体/方块挡住，隔着石头也能看见；</li>
 *   <li>不透明度固定 1.0，不受背景亮度影响（不依赖加算混合）。</li>
 * </ul>
 *
 * 坐标由服务端定向下发、仅发给触发玩家本人。
 */
public final class HighlightOutlineRenderer {

    /** 线宽（像素）。部分驱动会把大于 1 的值夹到 1，属于正常现象，只是看起来细一点。 */
    private static final float LINE_WIDTH = 2.0F;

    /** 轮廓外扩量（格）：让线浮在方块表面外侧一点，不和方块自身的边缘重叠。 */
    private static final double INFLATE = 0.002D;

    /** 线的不透明度：完全不透明。 */
    private static final float ALPHA = 1.0F;

    private HighlightOutlineRenderer() {}

    /**
     * 在 RenderLevelStageEvent.Stage.AFTER_ENTITIES 阶段把给定坐标渲染成指定颜色的方块轮廓；
     * 坐标为空时不做任何事。
     */
    public static void render(RenderLevelStageEvent event, Collection<BlockPos> positions,
                              float red, float green, float blue) {
        Minecraft minecraft = Minecraft.getInstance();
        if (positions.isEmpty() || minecraft.level == null) {
            return;
        }

        Matrix4f mat = event.getPoseStack().last().pose();
        Vec3 camPos = event.getCamera().getPosition();

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.enableBlend();
        // 普通透明混合 + alpha 1.0 = 纯色实心线条
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        // 关键：关掉深度测试，轮廓不会被方块遮挡（隔墙可见）；同时不写深度，避免影响后续渲染
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.lineWidth(LINE_WIDTH);

        BufferBuilder builder = Tesselator.getInstance().getBuilder();
        builder.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);

        for (BlockPos pos : positions) {
            // 相机相对坐标：RenderLevelStageEvent 的 poseStack 只带视图旋转，平移由这里补
            double x = pos.getX() - camPos.x;
            double y = pos.getY() - camPos.y;
            double z = pos.getZ() - camPos.z;
            renderBoxEdges(builder, mat, x, y, z, red, green, blue);
        }

        BufferUploader.drawWithShader(builder.end());

        RenderSystem.lineWidth(1.0F);
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.enableCull();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
    }

    /** 画一个整格方块（外扩 INFLATE）的 12 条棱。 */
    private static void renderBoxEdges(BufferBuilder builder, Matrix4f mat,
                                       double x, double y, double z,
                                       float red, float green, float blue) {
        float x0 = (float) (x - INFLATE);
        float x1 = (float) (x + 1.0D + INFLATE);
        float y0 = (float) (y - INFLATE);
        float y1 = (float) (y + 1.0D + INFLATE);
        float z0 = (float) (z - INFLATE);
        float z1 = (float) (z + 1.0D + INFLATE);

        // 上下两个面的 4 条边
        edge(builder, mat, x0, y0, z0, x1, y0, z0, red, green, blue);
        edge(builder, mat, x1, y0, z0, x1, y0, z1, red, green, blue);
        edge(builder, mat, x1, y0, z1, x0, y0, z1, red, green, blue);
        edge(builder, mat, x0, y0, z1, x0, y0, z0, red, green, blue);

        edge(builder, mat, x0, y1, z0, x1, y1, z0, red, green, blue);
        edge(builder, mat, x1, y1, z0, x1, y1, z1, red, green, blue);
        edge(builder, mat, x1, y1, z1, x0, y1, z1, red, green, blue);
        edge(builder, mat, x0, y1, z1, x0, y1, z0, red, green, blue);

        // 4 条竖棱
        edge(builder, mat, x0, y0, z0, x0, y1, z0, red, green, blue);
        edge(builder, mat, x1, y0, z0, x1, y1, z0, red, green, blue);
        edge(builder, mat, x1, y0, z1, x1, y1, z1, red, green, blue);
        edge(builder, mat, x0, y0, z1, x0, y1, z1, red, green, blue);
    }

    private static void edge(BufferBuilder builder, Matrix4f mat,
                             float ax, float ay, float az,
                             float bx, float by, float bz,
                             float red, float green, float blue) {
        builder.vertex(mat, ax, ay, az).color(red, green, blue, ALPHA).endVertex();
        builder.vertex(mat, bx, by, bz).color(red, green, blue, ALPHA).endVertex();
    }
}
