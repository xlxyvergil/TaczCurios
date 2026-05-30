package com.xlxyvergil.tcc.core;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class TccDamageSources {

    public static final ResourceKey<DamageType> IMAGINARY_DAMAGE =
        ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation("tcc", "imaginary_damage"));

    public static final TagKey<DamageType> IMAGINARY_DAMAGE_TAG =
        TagKey.create(Registries.DAMAGE_TYPE, new ResourceLocation("tcc", "imaginary_damage"));

    public static DamageSource imaginaryDamage(Level level, Entity attacker) {
        return new DamageSource(
            level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(IMAGINARY_DAMAGE),
            attacker, attacker);
    }

    public static DamageSource imaginaryDamage(Level level, Entity bullet, Entity attacker) {
        return new DamageSource(
            level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(IMAGINARY_DAMAGE),
            bullet, attacker);
    }
}
