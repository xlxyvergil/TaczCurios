package com.xlxyvergil.tcc.mixin;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 效果保护：仅拦截外部 mod 通过 removeEffect 精准移除 tcc 效果；
 */
@Mixin(value = LivingEntity.class, priority = 1)
public abstract class LivingEntityEffectProtectionMixin {

    @Inject(method = "removeEffect(Lnet/minecraft/world/effect/MobEffect;)Z",
        at = @At("HEAD"), cancellable = true)
    private void tcc$injectRemoveEffectHead(MobEffect effect, CallbackInfoReturnable<Boolean> cir) {
        if (tcc$isOurEffect(effect)) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }

    private static boolean tcc$isOurEffect(MobEffect effect) {
        var key = ForgeRegistries.MOB_EFFECTS.getKey(effect);
        return key != null && key.getNamespace().equals("tcc");
    }
}
