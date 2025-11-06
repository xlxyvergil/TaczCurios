package artifacts.mixin.item.wearable.nightvisiongoggles.client;

import artifacts.registry.ModGameRules;
import artifacts.registry.ModItems;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @SuppressWarnings("unused")
    @ModifyReturnValue(method = "getNightVisionScale", at = @At("RETURN"))
    private static float getNightVisionScale(float original, LivingEntity entity, float f) {
        MobEffectInstance effect = entity.getEffect(MobEffects.NIGHT_VISION);
        if (effect == null
                || effect.getDuration() - f > 60
                || !ModItems.NIGHT_VISION_GOGGLES.get().isEquippedBy(entity)
                || ModGameRules.NIGHT_VISION_GOGGLES_STRENGTH.get() == 0
        ) {
            return original;
        }
        return Mth.lerp(Math.max(0, effect.getDuration() - f - 40) / (60 - 40), ModGameRules.NIGHT_VISION_GOGGLES_STRENGTH.get().floatValue(), original);
    }
}
