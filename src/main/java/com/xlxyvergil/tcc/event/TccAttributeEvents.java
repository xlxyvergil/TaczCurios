package com.xlxyvergil.tcc.event;

import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.tacz.guns.api.event.common.GunDamageSourcePart;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.attribute.TccAttributes;
import com.xlxyvergil.tcc.compat.maid.MaidCompat;
import com.xlxyvergil.tcc.core.TccDamageSources;
import com.xlxyvergil.tcc.util.ImaginaryInfectionHelper;
import com.xlxyvergil.tcc.util.TccEffectGuard;
import com.xlxyvergil.tcc.registries.TccMobEffects;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;

import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

@EventBusSubscriber(modid = "tcc")
public class TccAttributeEvents {

    public static final String INFECTION_ATTACKER_KEY = "tcc_infection_attacker";

    /**
     * 重入防护：目标在 applyImaginaryDamage / applyMagicDamage 的 hurt 路径期间加入本集合，
     * 避免饰品监听器在附加伤害结算过程中再次发起附加伤害造成递归。
     */
    private static final Set<LivingEntity> BONUS_HURT_GUARD = Collections.newSetFromMap(new IdentityHashMap<>());

    /**
     * 判断伤害来源是否属于实体的主动攻击：
     *  玩家/生物普通攻击（PLAYER_ATTACK / MOB_ATTACK）、
     *  枪械子弹（tacz:bullets）、
     *  以及虚数枪伤（tcc:imaginary_damage，由 ImaginaryConversionHelper 在 Pre 阶段把子弹源替换而来）。
     * 用于过滤其它模组在 LivingIncomingDamageEvent 中通过 FastHurt 再入产生的强制子伤害
     *  （IN_FIRE / WIND_FLOW / FROST_FLAME / MOB_CUTTING / GENERIC_KILL 等），切断再入栈溢出。
     */
    public static boolean isActiveAttackSource(DamageSource source) {
        return source.is(DamageTypes.PLAYER_ATTACK)
            || source.is(DamageTypes.MOB_ATTACK)
            || TccDamageSources.isTaczBulletSource(source)
            || source.is(TccDamageSources.IMAGINARY_DAMAGE_TAG);
    }

    /** 虚数伤害入口（含崩解）：由饰品命中或崩解 DoT 调用，只传入基础值，抗性结算统一由 LivingIncomingDamageEvent 处理。 */
    public static boolean applyImaginaryDamage(LivingEntity target, DamageSource source, float intendedDamage) {
        if (intendedDamage <= 0) return false;
        if (BONUS_HURT_GUARD.contains(target)) return false;

        // 无需清空 invulnerableTime：虚数伤害类型挂了 minecraft:bypasses_cooldown，
        // 原版 hurt 会跳过无敌帧判断、直接走完整伤害分支。
        if (source.getEntity() instanceof LivingEntity attacker) {
            target.setLastHurtByMob(attacker);
        }

        BONUS_HURT_GUARD.add(target);
        try {
            return target.hurt(source, intendedDamage);
        } finally {
            BONUS_HURT_GUARD.remove(target);
        }
    }

    /**
     * 魔法伤害入口：走原版 minecraft:magic 管线（无视护甲，受保护附魔与抗性药水减免，计入 forge:is_magic），
     * 与渡鸦之键 / 裁决之键溅射的魔法伤害口径一致。命中后的第二笔结算必须先清空无敌帧，
     * 否则会被本次枪械命中的无敌帧吞掉。
     */
    public static boolean applyMagicDamage(LivingEntity target, DamageSource source, float intendedDamage) {
        if (intendedDamage <= 0) return false;
        if (BONUS_HURT_GUARD.contains(target)) return false;

        if (source.getEntity() instanceof LivingEntity attacker) {
            target.setLastHurtByMob(attacker);
        }

        BONUS_HURT_GUARD.add(target);
        target.invulnerableTime = 0;
        try {
            return target.hurt(source, intendedDamage);
        } finally {
            BONUS_HURT_GUARD.remove(target);
        }
    }

    /** 崩解伤害入口：与其它虚数伤害一致，统一走常规 hurt。由 ImaginaryCollapseEffect 调用。 */
    public static boolean applyCollapseDamage(LivingEntity target, DamageSource source, float intendedDamage) {
        return applyImaginaryDamage(target, source, intendedDamage);
    }

    private static float resolveFinalImaginaryDamage(LivingEntity target, DamageSource source, float baseDamage) {
        if (baseDamage <= 0) return 0;

        double resistance = target.getAttributeValue(TccAttributes.IMAGINARY_DAMAGE_RESISTANCE);
        resistance = Math.max(-100.0, Math.min(100.0, resistance));

        float damageAfterResistance = (float) (baseDamage * (1.0 - resistance / 100.0));

        // 侵染不再直接增伤，伤害增益统一由侵染降低虚数抗性体现。
        return (float) ((float) Math.round(damageAfterResistance * 10000.0) / 10000.0);
    }

    @SubscribeEvent
    public static void applyImaginaryInfection(EntityHurtByGunEvent.Post event) {
        if (event.getLogicalSide().isClient()) return;
        var target = event.getHurtEntity();
        if (!(target instanceof LivingEntity living) || living.isDeadOrDying()) return;

        DamageSource source = event.getDamageSource(GunDamageSourcePart.NON_ARMOR_PIERCING);
        if (!source.is(TccDamageSources.IMAGINARY_DAMAGE_TAG)) return;
        if (source.getEntity() == target) return;

        var srcEntity = source.getEntity();
        if (!(srcEntity instanceof LivingEntity attacker)) return;

        applyInfection(living, attacker, ImaginaryInfectionHelper.resolveMaxLevel(attacker));
    }

    public static void applyInfection(LivingEntity living, LivingEntity attacker, int maxLevel) {
        if (maxLevel <= 0) return;
        int duration = TaczCuriosConfig.COMMON.imaginaryInfectionDuration.get();

        MobEffectInstance existingEffect = living.getEffect(TccMobEffects.IMAGINARY_INFECTION);
        int newAmplifier = 0;
        if (existingEffect != null) {
            newAmplifier = Math.min(existingEffect.getAmplifier() + 1, maxLevel - 1);
        }
        var newInstance = new MobEffectInstance(
            TccMobEffects.IMAGINARY_INFECTION,
            duration * 20,
            newAmplifier,
            false, false, true
        );

        // 强制写入：原版实现直接操作 LivingEntity#activeEffects 字段，
        // 不经过可能被 mixin 覆盖为不可变 Map 的 getActiveEffectsMap()，
        // 同时绕过 MobEffectEvent.Added 被其他 mod 取消。
        living.forceAddEffect(newInstance, attacker);

        // 记录侵染来源攻击者，使虚数崩解击杀能正确归属
        // （若攻击者是女仆，转换为女仆主人，以让崩解击杀计入主人名下）
        LivingEntity credited = MaidCompat.resolveOwnerPlayer(attacker);
        living.getPersistentData().putString(
                INFECTION_ATTACKER_KEY, (credited != null ? credited : attacker).getStringUUID());
    }

    /**
     * 统一施加剧增崩解的实际入口：写入来源攻击者 NBT 并施加崩解效果。
     * 供各饰品在命中时（含概率判定通过后）调用。
     * 返回是否真正新施加了崩解（目标此前无崩解且存活）；目标已有崩解或已死亡则返回 false。
     */
    public static boolean applyCollapse(LivingEntity target, LivingEntity attacker) {
        if (target == null || attacker == null) return false;
        if (target.isDeadOrDying()) return false;
        var collapse = TccMobEffects.IMAGINARY_COLLAPSE;
        if (target.hasEffect(collapse)) return false;

        int duration = TaczCuriosConfig.COMMON.imaginaryInfectionDuration.get();
        var collapseInstance = new MobEffectInstance(
            collapse,
            duration * 20,
            0,
            false, false, true
        );
        // 强制写入：原版实现直接操作 LivingEntity#activeEffects 字段，
        // 不经过可能被 mixin 覆盖为不可变 Map 的 getActiveEffectsMap()，
        // 同时绕过 MobEffectEvent.Added 被其他 mod 取消。
        target.forceAddEffect(collapseInstance, attacker);

        // 记录侵染来源攻击者，使崩解击杀能正确归属（女仆转换为女仆主人）
        LivingEntity credited = MaidCompat.resolveOwnerPlayer(attacker);
        target.getPersistentData().putString(
                INFECTION_ATTACKER_KEY, (credited != null ? credited : attacker).getStringUUID());
        return true;
    }

    /**
     * 虚数伤害统一结算入口：显式入口（applyImaginaryDamage）与直接走 hurt 管线的虚数伤害都在此应用抗性结算。
     * 抗性结算只在此处执行一次，避免重复结算。
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void imaginaryDamageOnAttack(LivingIncomingDamageEvent event) {
        LivingEntity target = event.getEntity();
        if (target.level().isClientSide || target.isDeadOrDying()) return;

        DamageSource source = event.getSource();

        if (source.is(TccDamageSources.IMAGINARY_DAMAGE_TAG)) {
            event.setAmount(resolveFinalImaginaryDamage(target, source, event.getAmount()));
            // 生命偷取/过量治疗：虚数伤害在抗性结算后统一处理（覆盖枪械子弹来源）
            if (source.getEntity() instanceof LivingEntity attacker && attacker != target) {
                LifeStealOverhealHandler.applyImaginary(attacker, target, event.getAmount());
            }
        }
    }

    @SubscribeEvent
    public static void onLivingHeal(LivingHealEvent event) {
        LivingEntity entity = event.getEntity();
        if (!entity.hasEffect(TccMobEffects.IMAGINARY_INFECTION)
                && !entity.hasEffect(TccMobEffects.IMAGINARY_COLLAPSE)) {
            return;
        }

        // 仅再生 buff 生效时允许回血。
        if (entity.hasEffect(MobEffects.REGENERATION)) {
            return;
        }

        event.setCanceled(true);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onEffectApplicable(MobEffectEvent.Applicable event) {
        if (event.getResult() == MobEffectEvent.Applicable.Result.APPLY) return;
        var key = BuiltInRegistries.MOB_EFFECT.getKey(event.getEffectInstance().getEffect().value());
        if (key != null && key.getNamespace().equals("tcc")) {
            event.setResult(MobEffectEvent.Applicable.Result.APPLY);
        }
    }

    /**
     * tcc 效果未到期时拒绝批量移除（/effect clear、牛奶桶、净化）。
     *
     * 批量路径的顺序是 post(Remove) → onEffectRemoved → iterator.remove()，
     * 只有在这里取消才来得及，否则属性已经被清掉、实例却还留着。
     * 点名移除由 LivingEntityEffectProtectionMixin 拦，直接改表由 GuardedEffectMap 拦。
     * 自然到期走 MobEffectEvent.Expired，不经过这里；死亡时放行。
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onEffectRemove(MobEffectEvent.Remove event) {
        LivingEntity entity = event.getEntity();
        // 死亡时放行：允许尸体/复活流程清空 tcc 效果
        if (entity.isDeadOrDying()) return;

        Holder<MobEffect> effect = event.getEffect();

        if (TccEffectGuard.isOwnEffect(effect)) {
            // 直接取事件自带的效果实例，不翻阅可能被 mixin 覆盖为不可变 Map 的 getActiveEffectsMap()
            MobEffectInstance instance = event.getEffectInstance();
            if (instance != null && instance.getDuration() > 0) {
                event.setCanceled(true);
            }
        }
    }

}
