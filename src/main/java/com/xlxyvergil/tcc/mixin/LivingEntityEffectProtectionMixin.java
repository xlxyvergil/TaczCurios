package com.xlxyvergil.tcc.mixin;

import com.xlxyvergil.tcc.util.TccEffectGuard;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * tcc 效果保护入口 1：removeEffect 的 HEAD cancel。
 *
 * 用 mixin 而不是只靠事件，是因为别的 mod 可能注入到 removeEffect 方法体里强制摘除，那条路不发事件。
 * priority = 1 保证本注入先于其它 mod 的注入执行。
 *
 * 批量移除（removeAllEffects / curePotionEffects / 牛奶桶）不经过这里，由
 * {@code TccAttributeEvents#onEffectRemove} 拦；直接改表由 {@code GuardedEffectMap} 拦。
 */
@Mixin(value = LivingEntity.class, priority = 1)
public abstract class LivingEntityEffectProtectionMixin {

    @Inject(method = "removeEffect(Lnet/minecraft/core/Holder;)Z",
        at = @At("HEAD"), cancellable = true)
    private void tcc$injectRemoveEffectHead(Holder<MobEffect> effect, CallbackInfoReturnable<Boolean> cir) {
        if (TccEffectGuard.isOwnEffect(effect)) {
            cir.setReturnValue(false);
        }
    }
}
