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

import java.util.List;

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

    /**
     * TACZ 官方的四个子弹伤害类型（与 ModDamageTypes 一致）。
     * 用于在 tacz:bullets tag 不可用（被数据包覆盖 / 缺失）时精确兜底判定。
     */
    public static final List<ResourceKey<DamageType>> TACZ_BULLET_KEYS = List.of(
        ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation("tacz", "bullet")),
        ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation("tacz", "bullet_ignore_armor")),
        ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation("tacz", "bullet_void")),
        ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation("tacz", "bullet_void_ignore_armor"))
    );

    /**
     * 是否为 TACZ 枪械子弹伤害：优先用 tacz:bullets tag；
     * 当该 tag 在本机环境查不到（数据包覆盖 / 版本差异）时，退化为按官方四个伤害类型精确比对。
     */
    public static boolean isTaczBulletSource(DamageSource source) {
        if (source.is(TACZ_BULLETS_TAG)) return true;
        for (ResourceKey<DamageType> key : TACZ_BULLET_KEYS) {
            if (source.is(key)) return true;
        }
        return false;
    }

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
