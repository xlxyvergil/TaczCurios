package com.xlxyvergil.tcc.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.entity.ZhenWoBarrierEntity;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

/**
 * 结界地面特效渲染器：将 zhenwo.png 平铺在实体脚下的水平面上（固定于玩家脚底），
 * 特效尺寸按结界直径（zhenWoBarrierRadius）缩放，带淡入淡出。
 */
public class ZhenWoBarrierRenderer extends EntityRenderer<ZhenWoBarrierEntity> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(TaczCurios.MODID, "textures/zhenwo.png");

    public ZhenWoBarrierRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(ZhenWoBarrierEntity entity, float entityYaw, float partialTick,
                       PoseStack pose, MultiBufferSource buffer, int packedLight) {
        // 淡入（前 10 tick）与淡出（最后 10 tick），避免突然出现/消失
        float fadeIn = Mth.clamp(entity.tickCount / 10.0F, 0.0F, 1.0F);
        int total = entity.getTotalTicks();
        float fadeOut = total <= 0 ? 1.0F : Mth.clamp(entity.getRemainingTicks() / 10.0F, 0.0F, 1.0F);
        float alpha = 0.8F * fadeIn * fadeOut;
        if (alpha <= 0.01F) return;

        // 配置值为结界直径（128），quad 半宽 = 直径 / 2
        float half = TaczCuriosConfig.COMMON.zhenWoBarrierRadius.get().floatValue() / 2.0F;
        pose.pushPose();
        // 旋转 90° 将贴图平面从竖直平铺到水平（地面）
        pose.mulPose(Axis.XP.rotationDegrees(90.0F));
        VertexConsumer vc = buffer.getBuffer(RenderType.entityTranslucent(TEXTURE));
        Matrix4f mat = pose.last().pose();
        addVertex(vc, mat, -half, -half, 0.0F, 0.0F, alpha);
        addVertex(vc, mat, -half, half, 0.0F, 1.0F, alpha);
        addVertex(vc, mat, half, half, 1.0F, 1.0F, alpha);
        addVertex(vc, mat, half, -half, 1.0F, 0.0F, alpha);
        pose.popPose();
        super.render(entity, entityYaw, partialTick, pose, buffer, packedLight);
    }

    private void addVertex(VertexConsumer vc, Matrix4f mat, float x, float z, float u, float v, float alpha) {
        vc.vertex(mat, x, 0.0F, z).color(1.0F, 1.0F, 1.0F, alpha)
            .uv(u, v).overlayCoords(0).uv2(LightTexture.FULL_BRIGHT).normal(0.0F, 1.0F, 0.0F).endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(ZhenWoBarrierEntity entity) {
        return TEXTURE;
    }
}
