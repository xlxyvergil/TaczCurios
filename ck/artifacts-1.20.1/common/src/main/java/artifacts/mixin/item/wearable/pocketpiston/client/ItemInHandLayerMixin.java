package artifacts.mixin.item.wearable.pocketpiston.client;

import artifacts.client.item.RendererUtil;
import artifacts.extensions.pocketpiston.LivingEntityExtensions;
import artifacts.platform.PlatformServices;
import artifacts.registry.ModItems;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandLayer.class)
public class ItemInHandLayerMixin {

    @Inject(method = "renderArmWithItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"))
    private void renderArmWithItem(LivingEntity entity, ItemStack itemStack, ItemDisplayContext itemDisplayContext, HumanoidArm humanoidArm, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, CallbackInfo ci) {
        InteractionHand hand = RendererUtil.getInteractionHand(entity, humanoidArm);
        if (entity instanceof Player player
                && PlatformServices.platformHelper.isVisibleOnHand(player, hand, ModItems.POCKET_PISTON.get())
        ) {
            poseStack.translate(0, 0, -1.5 / 16);
            if (player.swingingArm == hand) {
                float length = ((LivingEntityExtensions) entity).artifacts$getPocketPistonLength() * 2;
                poseStack.translate(0, 0, -length / 16);
            }
        }
    }
}
