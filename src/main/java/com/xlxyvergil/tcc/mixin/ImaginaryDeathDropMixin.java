package com.xlxyvergil.tcc.mixin;

import com.xlxyvergil.tcc.items.curios.bound.ZhenWo;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 真我结界范围内实体死亡不产生经验球。
 * 真我结界在堆怪区域持续清怪会产生海量经验球并在区块内长期堆积，拖垮服务端 TPS。
 * 此 Mixin 让「处于任一激活结界球形范围内的实体」死亡时不生成经验球：
 * die HEAD 用 ZhenWo.isInsideActiveBarrier 判断是否在结界内并打标记，dropExperience HEAD 带标记则跳过生成。
 * 仅拦截掉落经验球，不影响物理/其他饰品击杀，也不影响物品/材料掉落。
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
