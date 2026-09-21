package com.xlxyvergil.tcc.mixin;

import com.xlxyvergil.tcc.items.curios.bound.ZhenWo;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 真我结界持续清怪会产生海量经验球并在区块内堆积、拖垮服务端 TPS，故处于任一激活结界范围内的实体死亡时不生成经验球。
 * 仅在死亡瞬间标记（die HEAD），且只拦截经验球掉落（dropExperience），不影响物理击杀与物品/材料掉落。
 */
@Mixin(LivingEntity.class)
public abstract class ImaginaryDeathDropMixin {

    /** 用于标记「该实体在真我结界内死亡」，仅在死亡瞬间有效。 */
    private static final String SUPPRESS_ORB_TAG = "tcc_suppress_orb_drop";

    @Inject(method = "die", at = @At("HEAD"))
    private void tcc$markBarrierDeath(DamageSource source, CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self.level().isClientSide) return;
        if (ZhenWo.isInsideActiveBarrier(self)) {
            self.getPersistentData().putBoolean(SUPPRESS_ORB_TAG, true);
        }
    }

    @Inject(method = "dropExperience", at = @At("HEAD"), cancellable = true)
    private void tcc$suppressOrbDrop(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self.getPersistentData().getBoolean(SUPPRESS_ORB_TAG)) {
            ci.cancel();
        }
    }
}
