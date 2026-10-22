package com.xlxyvergil.tcc.client.renderer;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.client.ZhenWoBarrierClientData;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

/**
 * 真我结界的落地视觉（仅客户端）：佩戴者脚下贴一朵缓慢自转的结界花朵，
 * 并在结界的真实作用半径处描一圈地面边界。
 *
 * 全部画在地面上，不做立体包络：既不挡视线，也不会因为"罩住半个屏幕"影响观感。
 * 位置每帧按 wearer.getPosition(partialTick) 取，人走到哪画到哪；
 * 结界结束（服务端撤掉 buff）即停止绘制，最后 1 秒整体淡出。
 */
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ZhenWoBarrierRenderer {

    /** 中心花朵贴图（同时也是真我结界的效果图标）。 */
    private static final ResourceLocation SIGIL =
            new ResourceLocation(TaczCurios.MODID, "textures/mob_effect/zhen_wo_barrier.png");

    /** 中心花朵贴图半边长（格）：2.4×2.4。 */
    private static final float SIGIL_FLOWER_HALF = 1.2F;

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

        PoseStack poseStack = event.getPoseStack();
        poseStack.pushPose();
        // 世界绝对坐标 → 相机相对坐标：之后的平移都以相机为原点
        Vec3 camPos = event.getCamera().getPosition();
        poseStack.translate(-camPos.x, -camPos.y, -camPos.z);
        Matrix4f base = poseStack.last().pose();
        float partialTick = event.getPartialTick();
        float boundaryRadius = TaczCuriosConfig.COMMON.zhenWoBarrierRadius.get().floatValue();
        float spin = (minecraft.level.getGameTime() + partialTick) / 20.0F * SIGIL_SPIN_PER_SECOND;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        // 显式打开深度测试：地面上的光环必须被地形正常遮挡
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(false);

        // 1) 真实作用半径的地面边界（纯几何）
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        BufferBuilder rings = Tesselator.getInstance().getBuilder();
        rings.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        for (Player wearer : wearers) {
            float fade = fadeOf(wearer);
            if (fade <= 0.0F) {
                continue;
            }
            Vec3 pos = wearer.getPosition(partialTick);
            Matrix4f boundary = new Matrix4f(base)
                    .translate((float) pos.x, (float) (pos.y + BOUNDARY_LIFT), (float) pos.z);
            appendFlatRing(rings, boundary, boundaryRadius, BOUNDARY_WIDTH, BOUNDARY_SEGMENTS,
                    BOUNDARY_ALPHA * fade);
        }
        BufferUploader.drawWithShader(rings.end());

        // 2) 中心花朵（贴图）：用加算混合，贴图自带的粉色光晕会发光而不是压在草地上成色块
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, SIGIL);
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        BufferBuilder flowers = Tesselator.getInstance().getBuilder();
        flowers.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        for (Player wearer : wearers) {
            float fade = fadeOf(wearer);
            if (fade <= 0.0F) {
                continue;
            }
            Vec3 pos = wearer.getPosition(partialTick);
            appendFlatFlower(flowers,
                    new Matrix4f(base).translate((float) pos.x, (float) (pos.y + FLOWER_LIFT + 0.004D), (float) pos.z),
                    SIGIL_FLOWER_HALF, -spin, 0.80F * fade);
        }
        BufferUploader.drawWithShader(flowers.end());
        RenderSystem.defaultBlendFunc();

        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.enableCull();
        RenderSystem.disableBlend();

        poseStack.popPose();
    }

    /** 剩余 tick 对应的整体透明度：最后 1 秒线性淡出，避免硬切。 */
    private static float fadeOf(Player wearer) {
        return Mth.clamp(ZhenWoBarrierClientData.ticksLeft(wearer.getId()) / FADE_OUT_TICKS, 0.0F, 1.0F);
    }

    /** 贴地环带：外圈略暗、内圈略亮；用于真实作用范围的边界。 */
    private static void appendFlatRing(BufferBuilder builder, Matrix4f mat,
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

            flatVertex(builder, mat, c1 * outer, s1 * outer, alpha * 0.6F);
            flatVertex(builder, mat, c2 * outer, s2 * outer, alpha * 0.6F);
            flatVertex(builder, mat, c2 * inner, s2 * inner, alpha);
            flatVertex(builder, mat, c1 * inner, s1 * inner, alpha);
        }
    }

    private static void flatVertex(BufferBuilder builder, Matrix4f mat, float x, float z, float alpha) {
        builder.vertex(mat, x, 0.0F, z).color(PINK_RED, PINK_GREEN, PINK_BLUE, alpha).endVertex();
    }

    /** 贴地花朵：贴图四边形，绕 Y 轴自转。 */
    private static void appendFlatFlower(BufferBuilder builder, Matrix4f mat, float half,
                                         float spinDegrees, float alpha) {
        double rad = Math.toRadians(spinDegrees);
        float cos = (float) Math.cos(rad);
        float sin = (float) Math.sin(rad);
        float[][] corners = {{-half, -half}, {half, -half}, {half, half}, {-half, half}};
        float[][] uvs = {{0.0F, 1.0F}, {1.0F, 1.0F}, {1.0F, 0.0F}, {0.0F, 0.0F}};
        for (int i = 0; i < 4; i++) {
            float x = corners[i][0] * cos - corners[i][1] * sin;
            float z = corners[i][0] * sin + corners[i][1] * cos;
            builder.vertex(mat, x, 0.0F, z)
                    .uv(uvs[i][0], uvs[i][1])
                    .color(1.0F, 1.0F, 1.0F, alpha)
                    .endVertex();
        }
    }
}
