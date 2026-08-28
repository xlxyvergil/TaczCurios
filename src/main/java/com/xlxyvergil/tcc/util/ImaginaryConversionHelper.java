package com.xlxyvergil.tcc.util;

import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.tacz.guns.api.event.common.GunDamageSourcePart;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.core.TccDamageSources;
import com.xlxyvergil.tcc.registries.TccMobEffects;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;

/**
 * 神之键通用效果工具（§0.1）。
 * 将伤害转为虚数伤害（Pathway B：EntityHurtByGunEvent.Pre 替换伤害源），并同时施加不限等级的虚数侵染。
 */
public final class ImaginaryConversionHelper {

    /** 子弹 NBT 标记：表示该子弹已转为虚数伤害，Post 阶段据此施加侵染 */
    public static final String INFECTION_KEY = "tcc_imaginary_infection_flag";

    private ImaginaryConversionHelper() {
    }

    /**
     * 将枪械伤害源替换为虚数伤害，并在子弹上标记侵染。
     * 3 阶神之键在 EntityHurtByGunEvent.Pre 中调用。
     * TACZ 近战攻击（枪托等）同样触发该事件，此时 bullet 为 null，伤害源使用无子弹重载。
     */
    public static void convertToImaginary(EntityHurtByGunEvent.Pre event) {
        LivingEntity attacker = event.getAttacker();
        if (attacker == null || !(attacker.level() instanceof ServerLevel)) {
            return;
        }
        if (event.getBullet() != null) {
            event.setDamageSource(GunDamageSourcePart.NON_ARMOR_PIERCING,
                    TccDamageSources.imaginaryDamage(attacker.level(), event.getBullet(), attacker));
            event.setDamageSource(GunDamageSourcePart.ARMOR_PIERCING,
                    TccDamageSources.imaginaryDamage(attacker.level(), event.getBullet(), attacker));
            event.getBullet().getPersistentData().putBoolean(INFECTION_KEY, true);
        } else {
            // 近战攻击：无子弹，直接替换伤害源为虚数（attacker 作为直接来源）
            event.setDamageSource(GunDamageSourcePart.NON_ARMOR_PIERCING,
                    TccDamageSources.imaginaryDamage(attacker.level(), attacker));
            event.setDamageSource(GunDamageSourcePart.ARMOR_PIERCING,
                    TccDamageSources.imaginaryDamage(attacker.level(), attacker));
        }
    }

    /**
     * 为被击中目标施加不限等级的虚数侵染。
     * 3 阶神之键在 EntityHurtByGunEvent.Post 中调用。
     * direct 为 true 时不依赖子弹标记（近战攻击，bullet 为 null），直接施加侵染。
     */
    public static void applyInfection(EntityHurtByGunEvent.Post event, LivingEntity attacker, boolean direct) {
        if (attacker == null || !(attacker.level() instanceof ServerLevel)) {
            return;
        }
        boolean flagged = event.getBullet() != null && event.getBullet().getPersistentData().getBoolean(INFECTION_KEY);
        if (!direct && !flagged) {
            return;
        }
        Entity hurtEntity = event.getHurtEntity();
        if (!(hurtEntity instanceof LivingEntity target) || target.isDeadOrDying()) {
            return;
        }
        int duration = TaczCuriosConfig.COMMON.imaginaryInfectionDuration.get();
        MobEffectInstance instance = new MobEffectInstance(
                TccMobEffects.IMAGINARY_INFECTION.get(),
                duration * 20,
                0,
                false, false, true
        );
        target.addEffect(instance, attacker);
        forceAddEffect(target, instance);
    }

    /**
     * 为指定目标施加指定等级的虚数侵染（持续 durationSeconds 秒）。
     * 用于戒律·神之键线的范围光环效果（每 1 秒刷新），高等级需绕过 maxLevel 限制。
     */
    public static void applyInfection(@Nullable LivingEntity target, @Nullable LivingEntity source, int level, int durationSeconds) {
        if (target == null || target.isDeadOrDying()) {
            return;
        }
        int amplifier = Math.max(0, level - 1);
        MobEffectInstance instance = new MobEffectInstance(
                TccMobEffects.IMAGINARY_INFECTION.get(),
                durationSeconds * 20,
                amplifier,
                false, false, true
        );
        if (source != null) {
            target.addEffect(instance, source);
        } else {
            target.addEffect(instance);
        }
        forceAddEffect(target, instance);
    }

    /** 绕过 maxLevel 限制强制写入效果（参考 ShijieFanyan） */
    private static void forceAddEffect(LivingEntity entity, MobEffectInstance instance) {
        MobEffect effect = instance.getEffect();
        MobEffectInstance old = entity.getActiveEffectsMap().get(effect);
        if (old == null) {
            entity.getActiveEffectsMap().put(effect, instance);
            effect.addAttributeModifiers(entity, entity.getAttributes(), instance.getAmplifier());
            entity.onEffectAdded(instance, null);
        } else {
            int prevAmp = old.getAmplifier();
            old.update(instance);
            if (old.getAmplifier() != prevAmp) {
                effect.addAttributeModifiers(entity, entity.getAttributes(), old.getAmplifier());
            }
        }
    }
}
