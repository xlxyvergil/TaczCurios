package com.xlxyvergil.tcc.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.compat.maid.MaidCompat;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.registries.TccMobEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import org.joml.Matrix4f;

/**
 * 真我结界渲染层：挂在玩家与女仆的实体渲染器上，实体身上有结界 buff 时绘制。
 */
@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = TaczCurios.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ZhenWoBarrierLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "textures/mob_effect/zhen_wo_barrier.png");

    private static final double LIFT = 0.05D;
    private static final int RING_SEGMENTS = 128;
    private static final float RING_WIDTH = 1.0F;

    /** 中心贴图与地面环的绘制批次，静态持有避免逐帧创建。 */
    private static final RenderType QUAD_TYPE = TccRenderTypes.barrier(TEXTURE);
    private static final RenderType RING_TYPE = TccRenderTypes.ring();

    /** 仅用于取单位矩阵绘制世界空间坐标。 */
    private static final PoseStack UNIT_POSE = new PoseStack();

    private static float cachedRingRadius = Float.NaN;
    private static float[] cachedRingVerts;

    public ZhenWoBarrierLayer(RenderLayerParent<T, M> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T entity,
                       float limbSwing, float limbSwingAmount, float partialTicks,
                       float ageInTicks, float netHeadYaw, float headPitch) {
        if (!entity.isAlive() || entity.getEffect(TccMobEffects.ZHEN_WO_BARRIER) == null) {
            return;
        }

        // 用相机相对坐标绘制，不受实体模型变换影响
        Vec3 camPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        Vec3 center = entity.getPosition(partialTicks);
        float ox = (float) (center.x - camPos.x);
        float oy = (float) (center.y + LIFT - camPos.y);
        float oz = (float) (center.z - camPos.z);

        Matrix4f mat = UNIT_POSE.last().pose();

        // 中心贴图四边形
        VertexConsumer quad = buffer.getBuffer(QUAD_TYPE);
        float half = 1.0F;
        quad.addVertex(mat, ox - half, oy, oz - half).setColor(1.0F, 1.0F, 1.0F, 1.0F).setUv(0.0F, 0.0F).setLight(packedLight);
        quad.addVertex(mat, ox + half, oy, oz - half).setColor(1.0F, 1.0F, 1.0F, 1.0F).setUv(1.0F, 0.0F).setLight(packedLight);
        quad.addVertex(mat, ox + half, oy, oz + half).setColor(1.0F, 1.0F, 1.0F, 1.0F).setUv(1.0F, 1.0F).setLight(packedLight);
        quad.addVertex(mat, ox - half, oy, oz + half).setColor(1.0F, 1.0F, 1.0F, 1.0F).setUv(0.0F, 1.0F).setLight(packedLight);

        // 地面环形
        VertexConsumer ring = buffer.getBuffer(RING_TYPE);
        float[] verts = ringVertices();
        for (int i = 0; i < RING_SEGMENTS; i++) {
            int b = i * 8;
            ring.addVertex(mat, ox + verts[b], oy, oz + verts[b + 1]).setColor(1.0F, 0.55F, 0.8F, 1.0F);
            ring.addVertex(mat, ox + verts[b + 2], oy, oz + verts[b + 3]).setColor(1.0F, 0.55F, 0.8F, 1.0F);
            ring.addVertex(mat, ox + verts[b + 4], oy, oz + verts[b + 5]).setColor(1.0F, 0.55F, 0.8F, 1.0F);
            ring.addVertex(mat, ox + verts[b + 6], oy, oz + verts[b + 7]).setColor(1.0F, 0.55F, 0.8F, 1.0F);
        }
    }

    /** 按配置半径构建环形顶点，半径不变时复用缓存。 */
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

    /** 给玩家与女仆的渲染器挂上结界层。 */
    @SubscribeEvent
    public static void onAddLayers(EntityRenderersEvent.AddLayers event) {
        for (PlayerSkin.Model skin : event.getSkins()) {
            attachPlayer(event, skin);
        }
        attachMaid(event);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void attachPlayer(EntityRenderersEvent.AddLayers event, PlayerSkin.Model skin) {
        LivingEntityRenderer renderer = event.getSkin(skin);
        if (renderer != null) {
            renderer.addLayer(new ZhenWoBarrierLayer<>(renderer));
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void attachMaid(EntityRenderersEvent.AddLayers event) {
        EntityType<? extends LivingEntity> maidType = MaidCompat.getMaidEntityType();
        if (maidType == null) {
            return;
        }
        EntityRenderer<?> renderer = event.getRenderer(maidType);
        if (renderer instanceof LivingEntityRenderer livingRenderer) {
            livingRenderer.addLayer(new ZhenWoBarrierLayer<>(livingRenderer));
        }
    }
}
