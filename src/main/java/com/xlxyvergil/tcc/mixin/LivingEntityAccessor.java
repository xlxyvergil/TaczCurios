package com.xlxyvergil.tcc.mixin;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.core.Holder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 效果保护：仅拦截外部 mod 通过 removeEffect 精准移除 tcc 效果；
 * 自然过期（removeEffectNoUpdate）与牛奶/死亡（removeAllEffects）路径不受影响。
 */
@Mixin(value = LivingEntity.class, priority = 1)
public abstract class LivingEntityAccessor {

    @Invoker("checkTotemDeathProtection")
    public abstract boolean callCheckTotemDeathProtection(DamageSource damageSource);

    @Inject(method = "removeEffect(Lnet/minecraft/core/Holder;)Z",
        at = @At("HEAD"), cancellable = true)
    private void tcc$injectRemoveEffectHead(Holder<MobEffect> effect, CallbackInfoReturnable<Boolean> cir) {
        if (tcc$isOurEffect(effect)) {
            cir.setReturnValue(false);
        }
    }

    private static boolean tcc$isOurEffect(Holder<MobEffect> effect) {
        return effect.unwrapKey()
                .map(key -> key.location().getNamespace().equals("tcc"))
                .orElse(false);
    }
}
