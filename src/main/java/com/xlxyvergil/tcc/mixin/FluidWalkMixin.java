package com.xlxyvergil.tcc.mixin;

import com.xlxyvergil.tcc.util.FluidWalkHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 流体行走：覆盖实体判定「能否站在该流体上」，并在移动结算之后补一次托举。
 * <p>
 * 具体谁能走哪种流体由佩戴的饰品（IFluidWalk）决定，本 mixin 只做转发。
 */
@Mixin(value = LivingEntity.class, priority = 1500)
public abstract class FluidWalkMixin {

    @Inject(method = "canStandOnFluid", at = @At("HEAD"), cancellable = true)
    private void tcc$canStandOnFluid(FluidState state, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (FluidWalkHelper.canStandOnFluid(self, state)) {
            cir.setReturnValue(true);
        }
    }

    /**
     * aiStep 末尾即 travel 与重力结算之后，与原版 Strider 的 floatStrider 同序。
     */
    @Inject(method = "aiStep", at = @At("TAIL"))
    private void tcc$fluidWalkTick(CallbackInfo ci) {
        FluidWalkHelper.tickFluidWalk((LivingEntity) (Object) this);
    }
}
