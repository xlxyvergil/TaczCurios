package artifacts.mixin.item.wearable.anglershat;

import artifacts.registry.ModGameRules;
import artifacts.registry.ModItems;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FishingRodItem.class)
public abstract class FishingRodItemMixin {

    @ModifyExpressionValue(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getFishingLuckBonus(Lnet/minecraft/world/item/ItemStack;)I"))
    private int getFishingLuckBonus(int original, Level level, Player player, InteractionHand interactionHand) {
        if (!ModItems.ANGLERS_HAT.get().isEquippedBy(player)) {
            return original;
        }
        return original + ModGameRules.ANGLERS_HAT_LUCK_OF_THE_SEA_LEVEL_BONUS.get();
    }

    @ModifyExpressionValue(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getFishingSpeedBonus(Lnet/minecraft/world/item/ItemStack;)I"))
    private int getFishingSpeedBonus(int original, Level level, Player player, InteractionHand interactionHand) {
        // Lure >5 breaks fishing, don't return more than 5 unless original was more than 5
        if (original >= 5 || !ModItems.ANGLERS_HAT.get().isEquippedBy(player)) {
            return original;
        }
        return Math.min(5, original + ModGameRules.ANGLERS_HAT_LURE_LEVEL_BONUS.get());
    }
}
