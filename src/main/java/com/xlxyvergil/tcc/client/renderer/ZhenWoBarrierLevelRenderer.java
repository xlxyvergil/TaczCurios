package com.xlxyvergil.tcc.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.registries.TccMobEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;

/**
 * 结界地面特效渲染器（无实体）。
 * <p>
 * 状态由玩家身上的结界标记 buff（ZhenWoBarrierEffect）驱动：服务端在结界激活期间
 * 施加/续期该 buff，客户端检测到后直接以本地玩家的实时渲染位置为特效中心绘制
 * 标记 buff 图标平铺贴图 + 粉色范围圆环。特效中心取自本地玩家，无实体位置插值延迟。
 */
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ZhenWoBarrierLevelRenderer {

    /** 地面特效贴图 = 结界标记 buff 图标（mob_effect/zhen_wo_barrier.png） */
    private static final ResourceLocation TEXTURE = new ResourceLocation(TaczCurios.MODID, "textures/mob_effect/zhen_wo_barrier.png");
    /** 特效平面悬浮在玩家脚底上方的高度 */
    private static final double LIFT = 0.05D;

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;
        Player player = mc.player;
        MobEffectInstance barrier = player.getEffect(TccMobEffects.ZHEN_WO_BARRIER.get());
        if (barrier == null) return;

        // 特效完全不透明显示：不透明度固定为 1.0（取消半透明与淡出）
        float alpha = 1.0F;

        // 中心 = 本地玩家实时渲染位置（零延迟跟随）
        Vec3 center = player.getPosition(event.getPartialTick());
        renderBarrier(center, alpha, event.getPoseStack(), event.getCamera().getPosition());
    }

    private static void renderBarrier(Vec3 center, float alpha, PoseStack pose, Vec3 camPos) {
        float half = 1.0F;
        Vec3 offset = center.add(0.0D, LIFT, 0.0D).subtract(camPos);

        pose.pushPose();
        pose.translate(offset.x, offset.y, offset.z);

        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, TEXTURE);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.disableCull();

        // 脚下贴图（半径 2 格）
        BufferBuilder builder = Tesselator.getInstance().getBuilder();
        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        Matrix4f mat = pose.last().pose();
        addVertex(builder, mat, -half, -half, 0.0F, 0.0F, alpha);
        addVertex(builder, mat, half, -half, 1.0F, 0.0F, alpha);
        addVertex(builder, mat, half, half, 1.0F, 1.0F, alpha);
        addVertex(builder, mat, -half, half, 0.0F, 1.0F, alpha);
        BufferUploader.drawWithShader(builder.end());

        // 粉色圆环：沿结界圆周（半径 = 配置值）绘制连续圆环带（内圆 + 外圆）
        float ringRadius = TaczCuriosConfig.COMMON.zhenWoBarrierRadius.get().floatValue();
        float ringWidth = 1.0F;
        int ringCount = 128;
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        BufferBuilder ringBuilder = Tesselator.getInstance().getBuilder();
        ringBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        float outer = ringRadius + ringWidth / 2.0F;
        float inner = ringRadius - ringWidth / 2.0F;
        for (int i = 0; i < ringCount; i++) {
            double a1 = Math.PI * 2.0D * i / ringCount;
            double a2 = Math.PI * 2.0D * (i + 1) / ringCount;
            float x1o = (float) (Math.cos(a1) * outer);
            float z1o = (float) (Math.sin(a1) * outer);
            float x2o = (float) (Math.cos(a2) * outer);
            float z2o = (float) (Math.sin(a2) * outer);
            float x1i = (float) (Math.cos(a1) * inner);
            float z1i = (float) (Math.sin(a1) * inner);
            float x2i = (float) (Math.cos(a2) * inner);
            float z2i = (float) (Math.sin(a2) * inner);
            ringBuilder.vertex(mat, x1o, 0.0F, z1o).color(1.0F, 0.55F, 0.8F, alpha).endVertex();
            ringBuilder.vertex(mat, x2o, 0.0F, z2o).color(1.0F, 0.55F, 0.8F, alpha).endVertex();
            ringBuilder.vertex(mat, x2i, 0.0F, z2i).color(1.0F, 0.55F, 0.8F, alpha).endVertex();
            ringBuilder.vertex(mat, x1i, 0.0F, z1i).color(1.0F, 0.55F, 0.8F, alpha).endVertex();
        }
        BufferUploader.drawWithShader(ringBuilder.end());

        RenderSystem.enableCull();
        pose.popPose();
    }

    private static void addVertex(BufferBuilder builder, Matrix4f mat, float x, float z, float u, float v, float alpha) {
        builder.vertex(mat, x, 0.0F, z).uv(u, v).color(1.0F, 1.0F, 1.0F, alpha).endVertex();
    }
}
