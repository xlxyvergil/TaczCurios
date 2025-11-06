package artifacts.fabric.mixin.item.umbrella;

import artifacts.item.UmbrellaItem;
import artifacts.item.wearable.necklace.CharmOfSinkingItem;
import artifacts.registry.ModGameRules;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    public LivingEntityMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    @Shadow
    public abstract boolean hasEffect(MobEffect effect);

    // TODO 1.20.5 use gravity attribute
    @SuppressWarnings("ConstantConditions")
    @ModifyVariable(method = "travel", ordinal = 0, name = "d", at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/world/level/Level;getFluidState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/material/FluidState;"))
    private double changeGravity(double gravity) {
        LivingEntity entity = (LivingEntity) (Object) this;
        boolean isFalling = !this.onGround() && this.getDeltaMovement().y <= 0;
        boolean isInWater = this.isInWater() && !CharmOfSinkingItem.shouldSink(entity);

        if (UmbrellaItem.isHoldingUmbrellaUpright(entity)
                && isFalling
                && !isInWater
                && !this.hasEffect(MobEffects.SLOW_FALLING)
                && ModGameRules.UMBRELLA_IS_GLIDER.get()
        ) {
            gravity -= 0.07;
            this.fallDistance = 0;
        }

        return gravity;
    }
}
