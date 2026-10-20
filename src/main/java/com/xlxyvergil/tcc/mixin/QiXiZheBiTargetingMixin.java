package com.xlxyvergil.tcc.mixin;

import com.xlxyvergil.tcc.registries.TccMobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 气息遮蔽对所有距离都不被寻敌。
 * 原版这里会给隐身目标留一个最小可见距离，贴到附近照样会被锁上，直接把结果打 false 就绕过了。
 */
@Mixin(TargetingConditions.class)
public abstract class QiXiZheBiTargetingMixin {

    // 参数顺序是 (观察者, 目标)
    @Inject(method = "test(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/LivingEntity;)Z",
            at = @At("RETURN"), cancellable = true)
    private void tcc$untargetable(LivingEntity observer, LivingEntity target, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue() || target == null) {
            return;
        }
        if (target.hasEffect(TccMobEffects.QI_XI_ZHE_BI)) {
            cir.setReturnValue(false);
        }
    }
}
