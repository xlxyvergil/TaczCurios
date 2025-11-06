package artifacts.mixin.item.wearable.villagerhat;

import artifacts.registry.ModGameRules;
import artifacts.registry.ModItems;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Villager.class)
public abstract class VillagerMixin {

    @SuppressWarnings("InvalidInjectorMethodSignature")
    @ModifyExpressionValue(method = "updateSpecialPrices", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/npc/Villager;getPlayerReputation(Lnet/minecraft/world/entity/player/Player;)I"))
    private int increaseReputation(int original, Player player) {
        if (ModItems.VILLAGER_HAT.get().isEquippedBy(player)) {
            original += ModGameRules.VILLAGER_HAT_REPUTATION_BONUS.get();
        }
        return original;
    }
}
