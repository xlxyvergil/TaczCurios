package artifacts.mixin.item.umbrella;

import artifacts.item.UmbrellaItem;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @SuppressWarnings({"UnreachableCode", "ConstantValue"})
    @ModifyReturnValue(method = "isInRain", at = @At("RETURN"))
    private boolean blockRain(boolean original) {
        boolean umbrellaBlockingRain = (Object) this instanceof LivingEntity entity && UmbrellaItem.isHoldingUmbrellaUpright(entity);
        return original && !umbrellaBlockingRain;
    }
}
