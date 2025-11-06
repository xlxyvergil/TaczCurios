package artifacts.client.item.renderer;

import artifacts.client.item.model.LegsModel;
import artifacts.platform.PlatformServices;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.function.Function;

public class BootArtifactRenderer implements ArtifactRenderer {

    private final ResourceLocation texture;
    private final LegsModel model;
    private final LegsModel armorModel;

    public BootArtifactRenderer(String name, Function<Boolean, LegsModel> model) {
        this.texture = ArtifactRenderer.getTexturePath(name);
        this.model = model.apply(false);
        this.armorModel = model.apply(true);
    }

    protected ResourceLocation getTexture() {
        return texture;
    }

    protected HumanoidModel<LivingEntity> getModel(LivingEntity entity) {
        return PlatformServices.platformHelper.areBootsHidden(entity) || entity.getItemBySlot(EquipmentSlot.FEET).isEmpty() ? model : armorModel;
    }

    @Override
    public void render(
            ItemStack stack,
            LivingEntity entity,
            int slotIndex,
            PoseStack poseStack,
            MultiBufferSource multiBufferSource,
            int light,
            float limbSwing,
            float limbSwingAmount,
            float partialTicks,
            float ageInTicks,
            float netHeadYaw,
            float headPitch
    ) {
        HumanoidModel<LivingEntity> model = getModel(entity);

        model.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        model.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
        ArtifactRenderer.followBodyRotations(entity, model);
        render(model, poseStack, multiBufferSource, light, stack.hasFoil());
    }

    protected void render(HumanoidModel<LivingEntity> model, PoseStack matrixStack, MultiBufferSource buffer, int light, boolean hasFoil) {
        RenderType renderType = model.renderType(getTexture());
        VertexConsumer vertexBuilder = ItemRenderer.getFoilBuffer(buffer, renderType, false, hasFoil);
        model.renderToBuffer(matrixStack, vertexBuilder, light, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
    }
}
