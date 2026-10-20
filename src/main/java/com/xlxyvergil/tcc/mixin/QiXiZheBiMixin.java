package com.xlxyvergil.tcc.mixin;

import com.xlxyvergil.tcc.registries.TccMobEffects;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 气息遮蔽的两处原版钩子，只对带该效果的实体生效。
 */
@Mixin(LivingEntity.class)
public abstract class QiXiZheBiMixin {

    // 原版这个方法只认 MobEffects.INVISIBILITY，这里把自己的效果也补进去
    @Inject(method = "updateInvisibilityStatus", at = @At("TAIL"))
    private void tcc$forceInvisible(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self.hasEffect(TccMobEffects.QI_XI_ZHE_BI)) {
            self.setInvisible(true);
        }
    }

    // 隐身系数是 0.7 * max(getArmorCoverPercentage(), 0.1)，满甲能把侦测距离拉到十倍
    @Inject(method = "getArmorCoverPercentage", at = @At("HEAD"), cancellable = true)
    private void tcc$noArmorExposure(CallbackInfoReturnable<Float> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self.hasEffect(TccMobEffects.QI_XI_ZHE_BI)) {
            cir.setReturnValue(0.0F);
        }
    }
}
