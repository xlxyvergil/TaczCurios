package artifacts.client.item.renderer;

import artifacts.client.item.model.ArmsModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;

import java.util.function.Function;

public class GlowingGloveArtifactRenderer extends GloveArtifactRenderer {

    private final ResourceLocation wideGlowTexture;
    private final ResourceLocation slimGlowTexture;

    public GlowingGloveArtifactRenderer(String name, Function<Boolean, ArmsModel> model) {
        super(name, model);
        wideGlowTexture = ArtifactRenderer.getTexturePath(name, "%s_wide_overlay".formatted(name));
        slimGlowTexture = ArtifactRenderer.getTexturePath(name, "%s_slim_overlay".formatted(name));
    }

    private ResourceLocation getGlowTexture(boolean hasSlimArms) {
        return hasSlimArms ? slimGlowTexture : wideGlowTexture;
    }

    @Override
    protected void renderArm(ArmsModel model, PoseStack poseStack, MultiBufferSource multiBufferSource, HumanoidArm armSide, int light, boolean hasSlimArms, boolean hasFoil) {
        super.renderArm(model, poseStack, multiBufferSource, armSide, light, hasSlimArms, hasFoil);
        RenderType renderType = model.renderType(getGlowTexture(hasSlimArms));
        VertexConsumer builder = ItemRenderer.getFoilBuffer(multiBufferSource, renderType, false, hasFoil);
        model.renderArm(armSide, poseStack, builder, LightTexture.pack(15, 15), OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
    }

    @Override
    protected void renderFirstPersonArm(ArmsModel model, ModelPart arm, PoseStack poseStack, MultiBufferSource multiBufferSource, int light, boolean hasSlimArms, boolean hasFoil) {
        super.renderFirstPersonArm(model, arm, poseStack, multiBufferSource, light, hasSlimArms, hasFoil);
        VertexConsumer builder = ItemRenderer.getFoilBuffer(multiBufferSource, model.renderType(getGlowTexture(hasSlimArms)), false, hasFoil);
        arm.render(poseStack, builder, LightTexture.pack(15, 15), OverlayTexture.NO_OVERLAY);
    }
}
