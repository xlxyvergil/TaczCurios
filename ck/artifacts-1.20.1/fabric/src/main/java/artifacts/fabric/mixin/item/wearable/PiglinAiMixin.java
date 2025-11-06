package artifacts.fabric.mixin.item.wearable;

import artifacts.item.wearable.WearableArtifactItem;
import artifacts.platform.PlatformServices;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PiglinAi.class)
public abstract class PiglinAiMixin {

    @ModifyReturnValue(method = "isWearingGold", at = @At("RETURN"))
    private static boolean isWearingGold(boolean original, LivingEntity entity) {
        return original || PlatformServices.platformHelper.isEquippedBy(entity, stack ->
                stack.getItem() instanceof WearableArtifactItem item && item.makesPiglinsNeutral()
        );
    }
}
