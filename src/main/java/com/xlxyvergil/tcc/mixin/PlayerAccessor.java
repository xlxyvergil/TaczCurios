package com.xlxyvergil.tcc.mixin;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerAccessor {

    @Inject(method = "removeEffect(Lnet/minecraft/world/effect/MobEffect;)Z", at = @At("HEAD"), cancellable = true)
    private void tcc$preventRemoveEffect(MobEffect effect, CallbackInfoReturnable<Boolean> cir) {
        if (tcc$isOurEffect(effect)) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }

    @Inject(method = "removeEffectNoUpdate", at = @At("HEAD"), cancellable = true)
    private void tcc$preventRemoveEffectNoUpdate(MobEffect effect, CallbackInfoReturnable<Boolean> cir) {
        if (tcc$isOurEffect(effect)) {
            cir.cancel();
        }
    }

    private static boolean tcc$isOurEffect(MobEffect effect) {
        var key = ForgeRegistries.MOB_EFFECTS.getKey(effect);
        return key != null && key.getNamespace().equals("tcc");
    }
}
