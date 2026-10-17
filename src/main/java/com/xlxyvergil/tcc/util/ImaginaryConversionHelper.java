package com.xlxyvergil.tcc.util;

import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.tacz.guns.api.event.common.GunDamageSourcePart;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.core.TccDamageSources;
import com.xlxyvergil.tcc.registries.TccMobEffects;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;

/**
 * 将枪械伤害转为虚数伤害（Pre 阶段替换伤害源），并施加不限等级的虚数侵染。
 */
public final class ImaginaryConversionHelper {

    /** 子弹 NBT 标记：表示该子弹已转为虚数伤害，Post 阶段据此施加侵染 */
    public static final String INFECTION_KEY = "tcc_imaginary_infection_flag";

    private ImaginaryConversionHelper() {
    }

    /**
     * 替换伤害源为虚数并标记子弹；在 Pre 阶段被 3 阶神之键调用。
     * 仅子弹伤害转换：该事件由 EntityKineticBullet 命中实体时发布，bullet 恒为子弹实体本身。
     * bullet 为 null 属于异常（如客户端同步包未能按 id 取回实体），此时不做转换。
     */
    public static void convertToImaginary(EntityHurtByGunEvent.Pre event) {
        LivingEntity attacker = event.getAttacker();
        if (attacker == null || !(attacker.level() instanceof ServerLevel)) {
            return;
        }
        if (event.getBullet() == null) {
            return;
        }
        event.setDamageSource(GunDamageSourcePart.NON_ARMOR_PIERCING,
                TccDamageSources.imaginaryDamage(attacker.level(), event.getBullet(), attacker));
        event.setDamageSource(GunDamageSourcePart.ARMOR_PIERCING,
                TccDamageSources.imaginaryDamage(attacker.level(), event.getBullet(), attacker));
        event.getBullet().getPersistentData().putBoolean(INFECTION_KEY, true);
    }

    /**
     * 为被击中目标施加不限等级虚数侵染；Post 阶段被 3 阶神之键调用。
     * 仅子弹命中生效：依赖 Pre 阶段写入子弹的 INFECTION_KEY 标记。
     */
    public static void applyInfection(EntityHurtByGunEvent.Post event, LivingEntity attacker) {
        if (attacker == null || !(attacker.level() instanceof ServerLevel)) {
            return;
        }
        if (event.getBullet() == null || !event.getBullet().getPersistentData().getBoolean(INFECTION_KEY)) {
            return;
        }
        Entity hurtEntity = event.getHurtEntity();
        if (!(hurtEntity instanceof LivingEntity target) || target.isDeadOrDying()) {
            return;
        }
        int duration = TaczCuriosConfig.COMMON.imaginaryInfectionDuration.get();
        MobEffectInstance instance = new MobEffectInstance(
                TccMobEffects.IMAGINARY_INFECTION,
                duration * 20,
                0,
                false, false, true
        );
        // 强制写入：原版实现直接操作 LivingEntity#activeEffects 字段，
        // 不经过可能被 mixin 覆盖为不可变 Map 的 getActiveEffectsMap()，
        // 同时绕过 MobEffectEvent.Added 被其他 mod 取消。
        target.forceAddEffect(instance, attacker);
    }

    /**
     * 为指定目标施加指定等级虚数侵染（持续 durationSeconds 秒）；用于戒律·神之键线的范围光环（每 1 秒刷新），高等级需绕过 maxLevel 限制。
     */
    public static void applyInfection(@Nullable LivingEntity target, @Nullable LivingEntity source, int level, int durationSeconds) {
        if (target == null || target.isDeadOrDying()) {
            return;
        }
        int amplifier = Math.max(0, level - 1);
        MobEffectInstance instance = new MobEffectInstance(
                TccMobEffects.IMAGINARY_INFECTION,
                durationSeconds * 20,
                amplifier,
                false, false, true
        );
        // 强制写入：原版实现直接操作 LivingEntity#activeEffects 字段，
        // 绕过可能被 mixin 覆盖为不可变 Map 的 getActiveEffectsMap() 与 MobEffectEvent.Added 拦截。
        target.forceAddEffect(instance, source);
    }
}
