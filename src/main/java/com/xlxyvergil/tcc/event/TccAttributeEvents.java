package com.xlxyvergil.tcc.event;

import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.tacz.guns.api.event.common.GunDamageSourcePart;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.attribute.TccAttributes;
import com.xlxyvergil.tcc.compat.maid.MaidCompat;
import com.xlxyvergil.tcc.core.TccDamageSources;
import com.xlxyvergil.tcc.capability.ImaginaryHealthLedgerCapability;
import com.xlxyvergil.tcc.util.ForcedKillHelper;
import com.xlxyvergil.tcc.util.ImaginaryInfectionHelper;
import com.xlxyvergil.tcc.registries.TccMobEffects;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
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
     * 重入防护：目标在 applyImaginaryDamage 的 hurt 路径结算期间加入本集合，
     * 避免嵌套 LivingHurtEvent 二次结算、以及饰品监听器重复触发造成的递归。
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

    /**
     * 虚数/崩解伤害的落点：扣减实体身上的虚数死亡进度账本（capability）。
     * 账本不写血量字段，因此不受 setHealth 限伤/锁血影响；起点在首次命中时取 getMaxHealth()。
     * 归零后：先把实体判定生死读取的血量字段直接改为 0（不经过 setHealth/SynchedEntityData.set），
     * 再走标准 die()；若 die() 被拦截未置位 dead，则补掉落并 remove(KILLED)。
     * tcc:tcc_forced_kill 命中则跳过 die()，直接补掉落 + 移除。
     */
    private static boolean applyLedgerDamage(LivingEntity target, DamageSource source, float finalDamage) {
        var ledger = ImaginaryHealthLedgerCapability.get(target);
        if (ledger == null) return false;

        if (!ledger.isInitialized()) {
            ledger.init(target.getMaxHealth());
        }
        float remain = ledger.reduce(finalDamage);
        notifyLedgerChange(target, source, ledger);
        if (remain > 0) {
            return true;
        }

        // 账本归零：直接把实体判定生死读取的血量字段改为 0，不经过 setHealth/SynchedEntityData.set。
        ledger.reset();
        ForcedKillHelper.forceZeroHealth(target);

        // 强制快杀：跳过 die()，直接补掉落 + 移除。
        if (ForcedKillHelper.requiresForcedKill(target)) {
            ForcedKillHelper.dropAllDeathLoot(target, source);
            target.remove(Entity.RemovalReason.KILLED);
            return true;
        }

        // 交给实体自身死亡链路（die -> tickDeath 负责掉落与移除）。
        target.die(source);
        // die() 未置位 dead 说明被拦截（如血量被接管的下界亚波伦），走兜底。
        if (!target.dead) {
            ForcedKillHelper.dropAllDeathLoot(target, source);
            target.remove(Entity.RemovalReason.KILLED);
        }
        return true;
    }

    /** 账本每次变动后向相关玩家反馈：侵蚀 + 实体名 + 剩余百分比。 */
    private static void notifyLedgerChange(LivingEntity target, DamageSource source, ImaginaryHealthLedgerCapability.Handler ledger) {
        Component message = Component.translatable(
            "message.tcc.imaginary_erosion",
            target.getDisplayName(),
            String.format("%.1f%%", ledger.getProgress() * 100.0F)
        );

        if (source.getEntity() instanceof ServerPlayer player) {
            player.sendSystemMessage(message);
            return;
        }
        for (ServerPlayer player : target.level().getEntitiesOfClass(ServerPlayer.class, target.getBoundingBox().inflate(32.0D))) {
            player.sendSystemMessage(message);
        }
    }

    /**
     * 虚数伤害结算入口（非崩解）：由饰品命中时调用。
     * imaginaryDamageUseSetHealth 为 false（默认）走常规 hurt；为 true 走账本。
     */
    public static boolean applyImaginaryDamage(LivingEntity target, DamageSource source, float intendedDamage) {
        if (intendedDamage <= 0) return false;
        if (IMAGINARY_HURT_GUARD.contains(target)) return false;

        target.invulnerableTime = 0;

        float finalDamage = resolveFinalImaginaryDamage(target, source, intendedDamage);
        if (finalDamage <= 0) return false;

        if (source.getEntity() instanceof LivingEntity attacker) {
            target.setLastHurtByMob(attacker);
        }

        if (TaczCuriosConfig.COMMON.imaginaryDamageUseSetHealth.get()) {
            return applyLedgerDamage(target, source, finalDamage);
        }

        IMAGINARY_HURT_GUARD.add(target);
        try {
            return target.hurt(source, finalDamage);
        } finally {
            IMAGINARY_HURT_GUARD.remove(target);
        }
    }

    /** 崩解伤害入口：直接扣账本。由 ImaginaryCollapseEffect 调用。 */
    public static boolean applyCollapseDamage(LivingEntity target, DamageSource source, float intendedDamage) {
        if (intendedDamage <= 0) return false;

        target.invulnerableTime = 0;

        float finalDamage = resolveFinalImaginaryDamage(target, source, intendedDamage);
        if (finalDamage <= 0) return false;

        if (source.getEntity() instanceof LivingEntity attacker) {
            target.setLastHurtByMob(attacker);
        }

        return applyLedgerDamage(target, source, finalDamage);
    }

    private static float resolveFinalImaginaryDamage(LivingEntity target, DamageSource source, float baseDamage) {
        if (baseDamage <= 0) return 0;

        double resistance = target.getAttributeValue(TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get());
        resistance = Math.max(-100.0, Math.min(100.0, resistance));

        float damageAfterResistance = (float) (baseDamage * (1.0 - resistance / 100.0));

        double ampPerLevel = TaczCuriosConfig.COMMON.imaginaryInfectionAmpPerLevel.get();
        int infectionLevel = 0;
        var infectionEffect = TccMobEffects.IMAGINARY_INFECTION.get();
        if (infectionEffect != null) {
            var effectInstance = target.getEffect(infectionEffect);
            if (effectInstance != null) {
                infectionLevel = effectInstance.getAmplifier() + 1;
            }
        }

        return (float) ((float) Math.round((damageAfterResistance * (1.0 + infectionLevel * ampPerLevel)) * 10000.0) / 10000.0);
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

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void imaginaryDamageOnAttack(LivingHurtEvent event) {
        LivingEntity target = event.getEntity();
        if (target.level().isClientSide || target.isDeadOrDying()) return;
        if (IMAGINARY_HURT_GUARD.contains(target)) return;

        DamageSource source = event.getSource();

        if (source.is(TccDamageSources.IMAGINARY_DAMAGE_TAG)) {
            event.setAmount(resolveFinalImaginaryDamage(target, source, event.getAmount()));
        }
    }

    @SubscribeEvent
    public static void onLivingHeal(LivingHealEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.hasEffect(TccMobEffects.IMAGINARY_INFECTION.get())
                || entity.hasEffect(TccMobEffects.IMAGINARY_COLLAPSE.get())) {
            event.setCanceled(true);
        }
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
