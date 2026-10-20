package com.xlxyvergil.tcc.mixin;

import com.xlxyvergil.tcc.util.TccEffectGuard;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.Map;

/**
 * tcc 效果保护入口 3：让 getActiveEffectsMap() / getActiveEffects() 返回受控视图。
 *
 * 不替换 activeEffects 字段，vanilla 的批量移除、到期、死亡、NBT 读档都按原版跑。
 * 覆盖 getActiveEffectsMap().remove(...)、getActiveEffects().clear() 这类直接改表。
 *
 * 两个方法都是 vanilla 的（SRG: m_21221_ / m_21220_），走默认 remap 交给 refmap。
 */
@Mixin(value = LivingEntity.class, priority = 1500)
@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class LivingEntityActiveEffectsGuardMixin {

    @Shadow
    @Final
    private Map<MobEffect, MobEffectInstance> activeEffects;

    @Inject(method = "getActiveEffectsMap", at = @At("RETURN"), cancellable = true)
    private void tcc$guardEffectsMap(CallbackInfoReturnable<Map> cir) {
        cir.setReturnValue(TccEffectGuard.guarded((LivingEntity) (Object) this, this.activeEffects));
    }

    @Inject(method = "getActiveEffects", at = @At("HEAD"), cancellable = true)
    private void tcc$guardEffectsView(CallbackInfoReturnable<Collection> cir) {
        cir.setReturnValue(TccEffectGuard.guarded((LivingEntity) (Object) this, this.activeEffects).values());
    }
}
