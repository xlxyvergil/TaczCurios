package com.xlxyvergil.tcc.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.registries.TccMobEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;
import org.joml.Matrix4f;


@EventBusSubscriber(value = Dist.CLIENT, modid = TaczCurios.MODID)
public class ZhenWoBarrierLevelRenderer {

    
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "textures/mob_effect/zhen_wo_barrier.png");
    
    private static final double LIFT = 0.05D;

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;

        
        float alpha = 1.0F;
        Vec3 camPos = event.getCamera().getPosition();
        float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(false);

        
        for (Entity entity : mc.level.entitiesForRendering()) {
            if (!(entity instanceof LivingEntity living)) {
                continue;
            }
            MobEffectInstance barrier = living.getEffect(TccMobEffects.ZHEN_WO_BARRIER);
            if (barrier == null) {
                continue;
            }
            Vec3 center = living.getPosition(partialTick);
            renderBarrier(center, alpha, event.getPoseStack(), camPos, mc);
        }
    }

    private static void renderBarrier(Vec3 center, float alpha, PoseStack pose, Vec3 camPos, Minecraft mc) {
        float half = 1.0F;
        Vec3 offset = center.add(0.0D, LIFT, 0.0D).subtract(camPos);

        pose.pushPose();
        pose.translate(offset.x, offset.y, offset.z);
        Matrix4f mat = pose.last().pose();

        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();

        
        // 地面平铺贴图：使用自定义 RenderType（NO_CULL + ALWAYS 深度，避免贴地 quad 被地面遮挡）
        RenderType barrierType = TccRenderTypes.barrier(TEXTURE);
        VertexConsumer textureConsumer = bufferSource.getBuffer(barrierType);
        addVertex(textureConsumer, mat, -half, -half, 0.0F, 0.0F, alpha);
        addVertex(textureConsumer, mat, half, -half, 1.0F, 0.0F, alpha);
        addVertex(textureConsumer, mat, half, half, 1.0F, 1.0F, alpha);
        addVertex(textureConsumer, mat, -half, half, 0.0F, 1.0F, alpha);
        bufferSource.endBatch(barrierType);

        
        float ringRadius = TaczCuriosConfig.COMMON.zhenWoBarrierRadius.get().floatValue();
        float ringWidth = 1.0F;
        int ringCount = 128;
        VertexConsumer ringConsumer = bufferSource.getBuffer(RenderType.debugQuads());
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
            ringConsumer.addVertex(mat, x1o, 0.0F, z1o).setColor(1.0F, 0.55F, 0.8F, alpha);
            ringConsumer.addVertex(mat, x2o, 0.0F, z2o).setColor(1.0F, 0.55F, 0.8F, alpha);
            ringConsumer.addVertex(mat, x2i, 0.0F, z2i).setColor(1.0F, 0.55F, 0.8F, alpha);
            ringConsumer.addVertex(mat, x1i, 0.0F, z1i).setColor(1.0F, 0.55F, 0.8F, alpha);
        }
        bufferSource.endBatch(RenderType.debugQuads());

        pose.popPose();
    }

    private static void addVertex(VertexConsumer consumer, Matrix4f mat, float x, float z, float u, float v, float alpha) {
        consumer.addVertex(mat, x, 0.0F, z)
                .setColor(1.0F, 1.0F, 1.0F, alpha)
                .setUv(u, v)
                .setLight(LightTexture.FULL_BRIGHT);
    }
}
