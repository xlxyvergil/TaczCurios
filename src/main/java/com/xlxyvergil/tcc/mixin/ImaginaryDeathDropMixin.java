package com.xlxyvergil.tcc.mixin;

import com.xlxyvergil.tcc.items.curios.ZhenWo;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 真我结界范围内实体死亡不产生经验球。
 * <p>
 * 真我结界在堆怪区域持续清怪时会产生海量经验球并在区块内长期堆积（经验球并不会像原版那样
 * 快速合并/消失，区块内大量经验球会把服务端 TPS 拖垮）。此 Mixin 让「处于任一激活结界球形
 * 范围内的实体」死亡时不生成经验球：
 * <ul>
 *   <li>{@code die} HEAD：用 {@link ZhenWo#isInsideActiveBarrier} 判断实体死亡位置是否在结界内，若是则打标记。</li>
 *   <li>{@code dropExperience} HEAD：带标记则跳过经验球生成。</li>
 * </ul>
 * 仅拦截掉落经验球，不影响物理击杀、其他饰品击杀，也不影响被击杀者的物品/材料掉落。
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
