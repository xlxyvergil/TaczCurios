package com.xlxyvergil.tcc.core;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class TccDamageSources {

    public static final ResourceKey<DamageType> IMAGINARY_DAMAGE =
        ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation("tcc", "imaginary_damage"));

    public static final TagKey<DamageType> IMAGINARY_DAMAGE_TAG =
        TagKey.create(Registries.DAMAGE_TYPE, new ResourceLocation("tcc", "imaginary_damage"));

    /**
     * 近战专属虚数伤害类型：与枪械虚数共用 tcc:imaginary_damage tag（抗性结算/侵染逻辑一致），
     * 但类型本身独立，供枪杀判定区分近战与枪械来源。
     */
    public static final ResourceKey<DamageType> IMAGINARY_DAMAGE_MELEE =
        ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation("tcc", "imaginary_damage_melee"));

    /** tacz:bullets：TACZ 枪械子弹伤害 tag（bullet / bullet_ignore_armor / bullet_void / bullet_void_ignore_armor）。 */
    public static final TagKey<DamageType> TACZ_BULLETS_TAG =
        TagKey.create(Registries.DAMAGE_TYPE, new ResourceLocation("tacz", "bullets"));

    public static DamageSource imaginaryDamageMelee(Level level, Entity attacker) {
        return new DamageSource(
            level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(IMAGINARY_DAMAGE_MELEE),
            attacker, attacker);
    }

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

    /** 原版魔法伤害（minecraft:magic），击杀归属 attacker；用于梅比乌斯线的命中溅射。 */
    public static DamageSource magicDamage(Level level, Entity attacker) {
        return new DamageSource(
            level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.MAGIC),
            attacker, attacker);
    }
}
