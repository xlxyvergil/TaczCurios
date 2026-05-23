package com.xlxyvergil.tcc.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

@Mixin(LivingEntity.class)
public interface LivingEntityAccessor {
    
    @Invoker("checkTotemDeathProtection")
    boolean callCheckTotemDeathProtection(DamageSource damageSource);
}
