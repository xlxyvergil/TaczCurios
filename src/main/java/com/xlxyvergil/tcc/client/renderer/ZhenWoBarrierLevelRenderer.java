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
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;


@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ZhenWoBarrierLevelRenderer {

    
    private static final ResourceLocation TEXTURE = new ResourceLocation(TaczCurios.MODID, "textures/mob_effect/zhen_wo_barrier.png");
    
    private static final double LIFT = 0.05D;

    /** 环形分段数（固定）。 */
    private static final int RING_SEGMENTS = 128;
    /** 环形宽度（固定，仅半径可配置）。 */
    private static final float RING_WIDTH = 1.0F;

    /**
     * 预计算的地面环形顶点（本地坐标，相对结界中心）。
     * 每段 8 个 float：外弧起点 x,z、外弧终点 x,z、内弧终点 x,z、内弧起点 x,z。
     * 半径未变化时复用，避免逐实体重复计算三角函数。
     */
    private static float cachedRingRadius = Float.NaN;
    private static float[] cachedRingVerts;

    /**
     * 当前携带结界 buff 的实体列表，按游戏刻缓存。
     * 真我可能被玩家或女仆佩戴，客户端只能以「buff 是否存在」作为登记信号，
     * 且该 buff 由网络同步下发（不会触发 MobEffectEvent.Added），因此只能扫描实体表，
     * 扫描结果按刻缓存、每帧复用。
     */
    private static ClientLevel cachedLevel;
    private static long cachedGameTime = Long.MIN_VALUE;
    private static List<LivingEntity> cachedWearers = List.of();

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;

        
        float alpha = 1.0F;
        Vec3 camPos = event.getCamera().getPosition();

        // 先收集所有带结界的实体中心：无结界实体时不做任何绘制，也避免后续 BufferBuilder 的空 begin/end。
        // 真我可能被玩家或女仆佩戴，buff 由服务端仅在激活期间下发；故按实体表筛出携带者，
        // 结果按游戏刻缓存，每帧直接复用，避免逐帧全表扫描。
        List<LivingEntity> wearers = barrierWearers(mc);
        if (wearers.isEmpty()) {
            return;
        }
        List<Vec3> centers = new ArrayList<>(wearers.size());
        for (LivingEntity wearer : wearers) {
            centers.add(wearer.getPosition(event.getPartialTick()));
        }
        renderAll(centers, alpha, event.getPoseStack(), camPos);
    }

    /** 返回当前携带结界 buff 的实体；同一次游戏刻内复用缓存，跨刻或换维度时重建。 */
    private static List<LivingEntity> barrierWearers(Minecraft mc) {
        long gameTime = mc.level.getGameTime();
        if (mc.level != cachedLevel || gameTime != cachedGameTime) {
            List<LivingEntity> wearers = new ArrayList<>();
            for (Entity entity : mc.level.entitiesForRendering()) {
                if (entity instanceof LivingEntity living && living.isAlive()
                        && living.getEffect(TccMobEffects.ZHEN_WO_BARRIER.get()) != null) {
                    wearers.add(living);
                }
            }
            cachedLevel = mc.level;
            cachedGameTime = gameTime;
            cachedWearers = wearers;
        }
        return cachedWearers;
    }

    /**
     * 所有结界实体合并绘制：
     * 中心贴图四边形合并到一次 draw call，地面环形合并到另一次 draw call，
     * 把原先「每实体 2 次 draw call」压到「每帧固定 2 次」。
     */
    private static void renderAll(List<Vec3> centers, float alpha, PoseStack pose, Vec3 camPos) {
        Matrix4f mat = pose.last().pose();

        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.disableCull();

        // 第一批：中心贴图四边形
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, TEXTURE);
        BufferBuilder quad = Tesselator.getInstance().getBuilder();
        quad.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        float half = 1.0F;
        for (Vec3 center : centers) {
            float ox = (float) (center.x - camPos.x);
            float oy = (float) (center.y + LIFT - camPos.y);
            float oz = (float) (center.z - camPos.z);
            quad.vertex(mat, ox - half, oy, oz - half).uv(0.0F, 0.0F).color(1.0F, 1.0F, 1.0F, alpha).endVertex();
            quad.vertex(mat, ox + half, oy, oz - half).uv(1.0F, 0.0F).color(1.0F, 1.0F, 1.0F, alpha).endVertex();
            quad.vertex(mat, ox + half, oy, oz + half).uv(1.0F, 1.0F).color(1.0F, 1.0F, 1.0F, alpha).endVertex();
            quad.vertex(mat, ox - half, oy, oz + half).uv(0.0F, 1.0F).color(1.0F, 1.0F, 1.0F, alpha).endVertex();
        }
        BufferUploader.drawWithShader(quad.end());

        // 第二批：地面环形
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        float[] ring = ringVertices();
        BufferBuilder ringBuilder = Tesselator.getInstance().getBuilder();
        ringBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        for (Vec3 center : centers) {
            float ox = (float) (center.x - camPos.x);
            float oy = (float) (center.y + LIFT - camPos.y);
            float oz = (float) (center.z - camPos.z);
            for (int i = 0; i < RING_SEGMENTS; i++) {
                int b = i * 8;
                ringBuilder.vertex(mat, ox + ring[b], oy, oz + ring[b + 1]).color(1.0F, 0.55F, 0.8F, alpha).endVertex();
                ringBuilder.vertex(mat, ox + ring[b + 2], oy, oz + ring[b + 3]).color(1.0F, 0.55F, 0.8F, alpha).endVertex();
                ringBuilder.vertex(mat, ox + ring[b + 4], oy, oz + ring[b + 5]).color(1.0F, 0.55F, 0.8F, alpha).endVertex();
                ringBuilder.vertex(mat, ox + ring[b + 6], oy, oz + ring[b + 7]).color(1.0F, 0.55F, 0.8F, alpha).endVertex();
            }
        }
        BufferUploader.drawWithShader(ringBuilder.end());

        RenderSystem.enableCull();
    }

    /** 按当前配置半径构建（并缓存）环形本地顶点；半径未变化时直接复用缓存。 */
    private static float[] ringVertices() {
        float radius = TaczCuriosConfig.COMMON.zhenWoBarrierRadius.get().floatValue();
        if (cachedRingVerts != null && cachedRingRadius == radius) {
            return cachedRingVerts;
        }
        float outer = radius + RING_WIDTH / 2.0F;
        float inner = radius - RING_WIDTH / 2.0F;
        float[] verts = new float[RING_SEGMENTS * 8];
        for (int i = 0; i < RING_SEGMENTS; i++) {
            double a1 = Math.PI * 2.0D * i / RING_SEGMENTS;
            double a2 = Math.PI * 2.0D * (i + 1) / RING_SEGMENTS;
            float c1 = (float) Math.cos(a1);
            float s1 = (float) Math.sin(a1);
            float c2 = (float) Math.cos(a2);
            float s2 = (float) Math.sin(a2);
            int b = i * 8;
            verts[b] = c1 * outer;
            verts[b + 1] = s1 * outer;
            verts[b + 2] = c2 * outer;
            verts[b + 3] = s2 * outer;
            verts[b + 4] = c2 * inner;
            verts[b + 5] = s2 * inner;
            verts[b + 6] = c1 * inner;
            verts[b + 7] = s1 * inner;
        }
        cachedRingRadius = radius;
        cachedRingVerts = verts;
        return verts;
    }
}
