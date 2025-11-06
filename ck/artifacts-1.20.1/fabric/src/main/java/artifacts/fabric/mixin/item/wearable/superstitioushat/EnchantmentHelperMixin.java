package artifacts.fabric.mixin.item.wearable.superstitioushat;

import artifacts.fabric.trinket.TrinketsHelper;
import artifacts.item.wearable.WearableArtifactItem;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {

    @ModifyReturnValue(method = "getMobLooting", at = @At("RETURN"))
    private static int increaseLooting(int original, LivingEntity entity) {
        return original + TrinketsHelper.findAllEquippedBy(entity)
                .map(ItemStack::getItem)
                .map(item -> (WearableArtifactItem) item)
                .map(WearableArtifactItem::getLootingLevel)
                .max(Integer::compareTo)
                .orElse(0);
    }
}
