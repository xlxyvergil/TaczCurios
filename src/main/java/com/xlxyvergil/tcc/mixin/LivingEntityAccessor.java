package com.xlxyvergil.tcc.mixin;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(value = LivingEntity.class, priority = 1)
public abstract class LivingEntityAccessor {

    @Invoker("checkTotemDeathProtection")
    public abstract boolean callCheckTotemDeathProtection(DamageSource damageSource);

    /**
     * 拦截 removeEffect 方法的返回值，确保 tcc 效果不会被移除
     */
    @Inject(method = "removeEffect(Lnet/minecraft/world/effect/MobEffect;)Z", at = @At("HEAD"), cancellable = true)
    private void tcc$injectRemoveEffectHead(MobEffect effect, CallbackInfoReturnable<Boolean> cir) {
        if (tcc$isOurEffect(effect)) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }

    /**
     * 直接拦截 removeEffectNoUpdate 方法（返回值 MobEffectInstance，非 void）
     */
    @Inject(method = "removeEffectNoUpdate", at = @At("HEAD"), cancellable = true, require = 1)
    private void tcc$preventRemoveEffectNoUpdate(MobEffect effect, CallbackInfoReturnable<MobEffectInstance> cir) {
        if (tcc$isOurEffect(effect)) {
            cir.setReturnValue(null);
            cir.cancel();
        }
    }

    /**
     * 拦截 removeAllEffects 方法
     */
    @Inject(method = "removeAllEffects", at = @At("HEAD"), cancellable = true, require = 1)
    private void tcc$preventRemoveAllEffects(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        for (MobEffect effect : self.getActiveEffectsMap().keySet()) {
            if (tcc$isOurEffect(effect)) {
                cir.setReturnValue(false);
                cir.cancel();
                return;
            }
        }
    }

    /**
     * 拦截 getActiveEffectsMap().remove() 操作
     */
    @Redirect(method = "removeEffectNoUpdate", 
              at = @At(value = "INVOKE", target = "Ljava/util/Map;remove(Ljava/lang/Object;)Ljava/lang/Object;", 
                       remap = false))
    private Object tcc$redirectMapRemove(Map<MobEffect, MobEffectInstance> map, Object key) {
        MobEffect effect = (MobEffect) key;
        if (tcc$isOurEffect(effect)) {
            return map.get(effect);
        }
        return map.remove(key);
    }

    private static boolean tcc$isOurEffect(MobEffect effect) {
        var key = ForgeRegistries.MOB_EFFECTS.getKey(effect);
        return key != null && key.getNamespace().equals("tcc");
    }
}
