package artifacts.client.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;

public abstract class RendererUtil {

    public static ModelPart bakeLayer(ModelLayerLocation layerLocation) {
        return Minecraft.getInstance().getEntityModels().bakeLayer(layerLocation);
    }

    public static HumanoidArm getArmSide(LivingEntity entity, InteractionHand hand) {
        return hand == InteractionHand.MAIN_HAND ? entity.getMainArm() : entity.getMainArm().getOpposite();
    }

    public static InteractionHand getInteractionHand(LivingEntity entity, HumanoidArm armSide) {
        return armSide == entity.getMainArm() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
    }
}
