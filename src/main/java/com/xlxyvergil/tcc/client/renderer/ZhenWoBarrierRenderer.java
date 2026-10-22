package com.xlxyvergil.tcc.client.renderer;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.client.ZhenWoBarrierClientData;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

/**
 * 真我结界的落地视觉（仅客户端）：佩戴者脚下贴一朵缓慢自转的结界花朵，
 * 并在结界的真实作用半径处描一圈地面边界。
 *
 * 全部画在地面上，不做立体包络：既不挡视线，也不会因为"罩住半个屏幕"影响观感。
 * 位置每帧按 {@code wearer.getPosition(partialTick)} 取，人走到哪画到哪；
 * 状态来自服务端下发的 {@link ZhenWoBarrierClientData}，倒计时归零即停止绘制，最后 1 秒整体淡出。
 *
 * 1.21.1 形态：1.20.1 是在 {@code RenderLevelStageEvent} 里直接
 * {@code RenderSystem.setShader} + {@code Tesselator} 手工提交。1.21.1 的
 * BufferBuilder 已是流式 API，本项目其余世界空间渲染统一走
 * {@code MultiBufferSource + VertexConsumer + RenderType}，这里沿用同一路线
 * （见 {@link TccRenderTypes#zhenWoBoundary()} / {@link TccRenderTypes#zhenWoFlower()}）。
 */
@EventBusSubscriber(value = Dist.CLIENT, modid = TaczCurios.MODID)
public final class ZhenWoBarrierRenderer {

    /** 中心花朵贴图半边长（格）：2.4×2.4。贴图本身由 {@link TccRenderTypes#zhenWoFlower()} 绑定。 */
    private static final float SIGIL_FLOWER_HALF = 1.2F;

    /**
     * 花朵叠加强度：标准 alpha 混合下顶点 alpha 直接就是"盖住地面的比例"，
     * 0.55 既能看清纹样，又不会把草地整片刷白。
     */
    private static final float FLOWER_ALPHA = 0.55F;

    /** 真实作用半径处的地面边界：要看得清，但仍是贴地描线而不是立体圈。 */
    private static final float BOUNDARY_WIDTH = 0.40F;
    private static final float BOUNDARY_ALPHA = 0.95F;
    private static final int BOUNDARY_SEGMENTS = 192;

    /** 贴地花朵的抬升：只留一点点，让它压在方块上表面而不是浮空。 */
    private static final double FLOWER_LIFT = 0.02D;
    /** 远处边界的抬升更大：64 格外的深度精度不足以支撑 0.02 的偏移，会与地面打架。 */
    private static final double BOUNDARY_LIFT = 0.15D;

    private static final float PINK_RED = 1.0F;
    private static final float PINK_GREEN = 0.28F;
    private static final float PINK_BLUE = 0.78F;

    /** 纹章自转角速度（度/秒）。 */
    private static final float SIGIL_SPIN_PER_SECOND = 24.0F;
    /** 结束前的淡出时长（tick）：最后这段时间整体透明度线性收敛到 0。 */
    private static final float FADE_OUT_TICKS = 20.0F;

    /** 全亮灯光值：花朵是自发光贴图，不该被环境明暗压暗。 */
    private static final int FULL_BRIGHT = 0xF000F0;

    private ZhenWoBarrierRenderer() {}

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            return;
        }

        List<Player> wearers = new ArrayList<>();
        for (Player player : minecraft.level.players()) {
            if (player.isAlive() && ZhenWoBarrierClientData.ticksLeft(player.getId()) > 0) {
                wearers.add(player);
            }
        }
        if (wearers.isEmpty()) {
            return;
        }

        Matrix4f base = event.getPoseStack().last().pose();
        Vec3 camPos = event.getCamera().getPosition();
        float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(false);
        float boundaryRadius = TaczCuriosConfig.COMMON.zhenWoBarrierRadius.get().floatValue();
        float spin = (minecraft.level.getGameTime() + partialTick) / 20.0F * SIGIL_SPIN_PER_SECOND;

        MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();

        // 1) 真实作用半径的地面边界（纯色环带）
        VertexConsumer boundary = bufferSource.getBuffer(TccRenderTypes.zhenWoBoundary());
        for (Player wearer : wearers) {
            float fade = fadeOf(wearer);
            if (fade <= 0.0F) {
                continue;
            }
            Vec3 pos = wearer.getPosition(partialTick);
            Matrix4f mat = new Matrix4f(base)
                    .translate((float) (pos.x - camPos.x),
                               (float) (pos.y + BOUNDARY_LIFT - camPos.y),
                               (float) (pos.z - camPos.z));
            appendFlatRing(boundary, mat, boundaryRadius, BOUNDARY_WIDTH, BOUNDARY_SEGMENTS,
                    BOUNDARY_ALPHA * fade);
        }
        bufferSource.endBatch(TccRenderTypes.zhenWoBoundary());

        // 2) 中心花朵（贴图 + 加算混合），比边界环略高一丁点，避免同一平面打架
        VertexConsumer flower = bufferSource.getBuffer(TccRenderTypes.zhenWoFlower());
        for (Player wearer : wearers) {
            float fade = fadeOf(wearer);
            if (fade <= 0.0F) {
                continue;
            }
            Vec3 pos = wearer.getPosition(partialTick);
            Matrix4f mat = new Matrix4f(base)
                    .translate((float) (pos.x - camPos.x),
                               (float) (pos.y + FLOWER_LIFT + 0.004D - camPos.y),
                               (float) (pos.z - camPos.z));
            appendFlatFlower(flower, mat, SIGIL_FLOWER_HALF, -spin, FLOWER_ALPHA * fade);
        }
        bufferSource.endBatch(TccRenderTypes.zhenWoFlower());
    }

    /** 剩余 tick 对应的整体透明度：最后 1 秒线性淡出，避免硬切。 */
    private static float fadeOf(Player wearer) {
        return Mth.clamp(ZhenWoBarrierClientData.ticksLeft(wearer.getId()) / FADE_OUT_TICKS, 0.0F, 1.0F);
    }

    /** 贴地环带：外圈略暗、内圈略亮；用于真实作用范围的边界。 */
    private static void appendFlatRing(VertexConsumer consumer, Matrix4f mat,
                                       float radius, float width, int segments, float alpha) {
        float inner = Math.max(radius - width / 2.0F, 0.02F);
        float outer = radius + width / 2.0F;
        for (int i = 0; i < segments; i++) {
            double a1 = Math.PI * 2.0D * i / segments;
            double a2 = Math.PI * 2.0D * (i + 1) / segments;
            float c1 = (float) Math.cos(a1);
            float s1 = (float) Math.sin(a1);
            float c2 = (float) Math.cos(a2);
            float s2 = (float) Math.sin(a2);

            flatVertex(consumer, mat, c1 * outer, s1 * outer, alpha * 0.6F);
            flatVertex(consumer, mat, c2 * outer, s2 * outer, alpha * 0.6F);
            flatVertex(consumer, mat, c2 * inner, s2 * inner, alpha);
            flatVertex(consumer, mat, c1 * inner, s1 * inner, alpha);
        }
    }

    private static void flatVertex(VertexConsumer consumer, Matrix4f mat, float x, float z, float alpha) {
        consumer.addVertex(mat, x, 0.0F, z).setColor(PINK_RED, PINK_GREEN, PINK_BLUE, alpha);
    }

    /** 贴地花朵：贴图四边形，绕 Y 轴自转；顶点灯光取全亮，颜色只控制透明度。 */
    private static void appendFlatFlower(VertexConsumer consumer, Matrix4f mat, float half,
                                         float spinDegrees, float alpha) {
        double rad = Math.toRadians(spinDegrees);
        float cos = (float) Math.cos(rad);
        float sin = (float) Math.sin(rad);
        float[][] corners = {{-half, -half}, {half, -half}, {half, half}, {-half, half}};
        float[][] uvs = {{0.0F, 1.0F}, {1.0F, 1.0F}, {1.0F, 0.0F}, {0.0F, 0.0F}};
        for (int i = 0; i < 4; i++) {
            float x = corners[i][0] * cos - corners[i][1] * sin;
            float z = corners[i][0] * sin + corners[i][1] * cos;
            consumer.addVertex(mat, x, 0.0F, z)
                    .setColor(1.0F, 1.0F, 1.0F, alpha)
                    .setUv(uvs[i][0], uvs[i][1])
                    .setLight(FULL_BRIGHT);
        }
    }
}
