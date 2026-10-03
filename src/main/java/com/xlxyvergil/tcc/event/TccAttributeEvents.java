package com.xlxyvergil.tcc.event;

import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.tacz.guns.api.event.common.GunDamageSourcePart;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.attribute.TccAttributes;
import com.xlxyvergil.tcc.compat.maid.MaidCompat;
import com.xlxyvergil.tcc.core.TccDamageSources;
import com.xlxyvergil.tcc.util.ImaginaryInfectionHelper;
import com.xlxyvergil.tcc.registries.TccMobEffects;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;

import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;


@Mod.EventBusSubscriber(modid = "tcc", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TccAttributeEvents {

    public static final String INFECTION_ATTACKER_KEY = "tcc_infection_attacker";

    /**
     * 重入防护：目标在 applyImaginaryDamage 的 hurt 路径期间加入本集合，
     * 避免饰品监听器在虚数伤害结算过程中再次发起虚数伤害造成递归。
     */
    private static final Set<LivingEntity> IMAGINARY_HURT_GUARD = Collections.newSetFromMap(new IdentityHashMap<>());

    /** tacz:bullets：TACZ 枪械子弹伤害 tag */
    private static final TagKey<DamageType> TACZ_BULLETS_TAG =
        TagKey.create(Registries.DAMAGE_TYPE, new ResourceLocation("tacz", "bullets"));

    /**
     * 是否为主动攻击来源：玩家/生物普攻、枪械子弹（tacz:bullets）、虚数枪伤（tcc:imaginary_damage）。
     * 用于过滤其它模组在 LivingHurtEvent 中再入产生的强制子伤害（IN_FIRE / WIND_FLOW 等），避免栈溢出。
     */
    public static boolean isActiveAttackSource(DamageSource source) {
        return source.is(DamageTypes.PLAYER_ATTACK)
            || source.is(DamageTypes.MOB_ATTACK)
            || source.is(TACZ_BULLETS_TAG)
            || source.is(TccDamageSources.IMAGINARY_DAMAGE_TAG);
    }

    /** 虚数伤害入口（含崩解）：由饰品命中或崩解 DoT 调用，只传入基础值，抗性结算统一由 LivingHurtEvent 处理。 */
    public static boolean applyImaginaryDamage(LivingEntity target, DamageSource source, float intendedDamage) {
        if (intendedDamage <= 0) return false;
        if (IMAGINARY_HURT_GUARD.contains(target)) return false;

        target.invulnerableTime = 0;

        if (source.getEntity() instanceof LivingEntity attacker) {
            target.setLastHurtByMob(attacker);
        }

        IMAGINARY_HURT_GUARD.add(target);
        try {
            return target.hurt(source, intendedDamage);
        } finally {
            IMAGINARY_HURT_GUARD.remove(target);
        }
    }

    /** 崩解伤害入口：与其它虚数伤害一致，统一走常规 hurt。由 ImaginaryCollapseEffect 调用。 */
    public static boolean applyCollapseDamage(LivingEntity target, DamageSource source, float intendedDamage) {
        return applyImaginaryDamage(target, source, intendedDamage);
    }

    private static float resolveFinalImaginaryDamage(LivingEntity target, DamageSource source, float baseDamage) {
        if (baseDamage <= 0) return 0;

        double resistance = target.getAttributeValue(TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get());
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

        var imaginaryInfection = TccMobEffects.IMAGINARY_INFECTION.get();
        MobEffectInstance existingEffect = living.getEffect(imaginaryInfection);
        int newAmplifier = 0;
        if (existingEffect != null) {
            newAmplifier = Math.min(existingEffect.getAmplifier() + 1, maxLevel - 1);
        }
        var newInstance = new MobEffectInstance(
            imaginaryInfection,
            duration * 20,
            newAmplifier,
            false, false, true
        );

        forceAddEffect(living, newInstance);
        living.addEffect(newInstance, attacker);

        // 记录来源攻击者用于击杀归属（女仆则记其主人）。
        LivingEntity credited = MaidCompat.resolveOwnerPlayer(attacker);
        living.getPersistentData().putString(
                INFECTION_ATTACKER_KEY, (credited != null ? credited : attacker).getStringUUID());
    }

    /**
     * 施加剧增崩解：写入来源攻击者并施加崩解效果，供各饰品命中时调用。
     * 仅新施加时返回 true；目标已有崩解或已死亡返回 false。
     */
    public static boolean applyCollapse(LivingEntity target, LivingEntity attacker) {
        if (target == null || attacker == null) return false;
        if (target.isDeadOrDying()) return false;
        var collapse = TccMobEffects.IMAGINARY_COLLAPSE.get();
        if (collapse == null || target.hasEffect(collapse)) return false;

        int duration = TaczCuriosConfig.COMMON.imaginaryInfectionDuration.get();
        var collapseInstance = new MobEffectInstance(
            collapse,
            duration * 20,
            0,
            false, false, true
        );
        forceAddEffect(target, collapseInstance);
        target.addEffect(collapseInstance, attacker);

        // 记录来源攻击者用于击杀归属（女仆则记其主人）。
        LivingEntity credited = MaidCompat.resolveOwnerPlayer(attacker);
        target.getPersistentData().putString(
                INFECTION_ATTACKER_KEY, (credited != null ? credited : attacker).getStringUUID());
        return true;
    }

    @SubscribeEvent
    public static void onGunOverheal(EntityHurtByGunEvent.Post event) {
        if (event.getLogicalSide().isClient()) return;

        LivingEntity attacker = event.getAttacker();
        if (attacker == null) return;

        DamageSource source = event.getDamageSource(GunDamageSourcePart.NON_ARMOR_PIERCING);
        if (!source.is(TccDamageSources.IMAGINARY_DAMAGE_TAG)) return;

        if (source.getDirectEntity() instanceof LivingEntity) return;

        Attribute overhealAttr = ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation("attributeslib", "overheal"));
        if (overhealAttr == null) return;
        float overheal = (float) attacker.getAttributeValue(overhealAttr);
        if (overheal <= 0) return;

        float damage = event.getBaseAmount();
        if (event.isHeadShot()) damage *= event.getHeadshotMultiplier();

        if (!(event.getHurtEntity() instanceof LivingEntity target)) return;
        float effectiveDamage = Math.min(damage, target.getMaxHealth());

        float maxOverheal = attacker.getMaxHealth() * 0.5F;
        if (attacker.getAbsorptionAmount() < maxOverheal) {
            attacker.setAbsorptionAmount(
                Math.min(maxOverheal, attacker.getAbsorptionAmount() + effectiveDamage * overheal)
            );
        }
    }

    private static void forceAddEffect(LivingEntity e, MobEffectInstance ins) {
        MobEffect effect = ins.getEffect();
        MobEffectInstance old = e.getActiveEffectsMap().get(effect);
        if (old == null) {
            e.getActiveEffectsMap().put(effect, ins);
            effect.addAttributeModifiers(e, e.getAttributes(), ins.getAmplifier());

            e.onEffectAdded(ins, null);
        } else {
            int prevAmp = old.getAmplifier();
            old.update(ins);
            if (old.getAmplifier() != prevAmp) {
                effect.addAttributeModifiers(e, e.getAttributes(), old.getAmplifier());
            }
        }
    }

    /**
     * 虚数伤害统一结算入口：显式入口（applyImaginaryDamage）与直接走 hurt 管线的虚数伤害都在此应用抗性结算。
     * 抗性结算只在此处执行一次，避免重复结算。
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void imaginaryDamageOnAttack(LivingHurtEvent event) {
        LivingEntity target = event.getEntity();
        if (target.level().isClientSide || target.isDeadOrDying()) return;

        DamageSource source = event.getSource();

        if (source.is(TccDamageSources.IMAGINARY_DAMAGE_TAG)) {
            event.setAmount(resolveFinalImaginaryDamage(target, source, event.getAmount()));
        }
    }

    @SubscribeEvent
    public static void onLivingHeal(LivingHealEvent event) {
        LivingEntity entity = event.getEntity();
        if (!entity.hasEffect(TccMobEffects.IMAGINARY_INFECTION.get())
                && !entity.hasEffect(TccMobEffects.IMAGINARY_COLLAPSE.get())) {
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
        if (event.getResult() == Result.ALLOW) return;
        var key = ForgeRegistries.MOB_EFFECTS.getKey(event.getEffectInstance().getEffect());
        if (key != null && key.getNamespace().equals("tcc")) {
            event.setResult(Result.ALLOW);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onEffectRemove(MobEffectEvent.Remove event) {
        LivingEntity entity = event.getEntity();
        if (entity.isDeadOrDying()) return;

        MobEffect effect = event.getEffect();
        if (effect == null) return;

        var key = ForgeRegistries.MOB_EFFECTS.getKey(effect);
        if (key != null && key.getNamespace().equals("tcc")) {
            MobEffectInstance instance = entity.getActiveEffectsMap().get(effect);
            if (instance != null && instance.getDuration() > 0) {
                event.setCanceled(true);
            }
        }
    }

}
