package com.xlxyvergil.tcc.core;

import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.items.CursedRing;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.items.HeavenFireApocalypse;
import com.xlxyvergil.tcc.items.HeavenFireApocalypseEndless;
import com.xlxyvergil.tcc.items.HeavenFireJudgment;
import com.xlxyvergil.tcc.items.BrahmaBeasts;
import com.xlxyvergil.tcc.items.Salvation;
import com.xlxyvergil.tcc.items.SummerBeach;
import com.xlxyvergil.tcc.registries.TccMobEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;


@Mod.EventBusSubscriber(modid = "tcc", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TccAttributeEvents {

    @SubscribeEvent
    public static void applyImaginaryInfection(EntityHurtByGunEvent.Post event) {
        // 虚数侵染/崩解的施加已移至 imaginaryDamageOnAttack (LivingHurtEvent)，
        // 利用 LivingHurtEvent 仅在伤害 > 0 时触发的特性，天然避免对无敌实体误施 buff。
    }

    /**
     * 施加虚数侵染/虚数崩解 buff。调用侧需保证伤害真实命中（LivingHurtEvent）。
     */
    private static void applyImaginaryBuffs(LivingEntity living, DamageSource source) {
        if (source.getEntity() == living) return;
        var srcEntity = source.getEntity();
        if (!(srcEntity instanceof LivingEntity attacker)) return;

        int maxLevel;
        boolean canApplyCollapse = false;
        if (HeavenFireApocalypseEndless.hasHeavenFireApocalypseEndlessEquipped(attacker)) {
            maxLevel = TaczCuriosConfig.COMMON.endlessImaginaryInfectionMaxLevel.get();
            canApplyCollapse = true;
        } else if (HeavenFireApocalypse.hasHeavenFireApocalypseEquipped(attacker)) {
            maxLevel = TaczCuriosConfig.COMMON.apocalypseImaginaryInfectionMaxLevel.get();
            canApplyCollapse = true;
        } else if (HeavenFireJudgment.hasHeavenFireJudgmentEquipped(attacker)) {
            maxLevel = TaczCuriosConfig.COMMON.judgmentImaginaryInfectionMaxLevel.get();
        } else {
            return;
        }

        if (maxLevel <= 0) return;
        int duration = TaczCuriosConfig.COMMON.imaginaryInfectionDuration.get();

        // 施加虚数侵染（可叠加，受饰品分级上限约束）
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
        // MineFargo 模式：addEffect 触发事件/属性，forceAddEffect 保证时长刷新
        living.addEffect(newInstance, attacker);
        forceAddEffect(living, newInstance);

        // 仅天火劫灭/劫灭无尽可触发虚数崩解
        if (canApplyCollapse) {
            var collapse = TccMobEffects.IMAGINARY_COLLAPSE.get();
            if (!living.hasEffect(collapse)) {
                var collapseInstance = new MobEffectInstance(
                    collapse,
                    duration * 20,
                    0,
                    false, false, true
                );
                living.addEffect(collapseInstance, attacker);
                forceAddEffect(living, collapseInstance);
            }
        }
    }

    /**
     * 完全复刻 MineFargo MyGoUtil.addEffect 模式：
     * - getActiveEffectsMap().put 直接操作 Map，不触发 onEffectAdded/onEffectUpdated
     * - 不 post MobEffectEvent.Added，避免任何外部监听器干扰
     * - old.update(ins) 在原地刷新时长/等级
     */
    private static void forceAddEffect(LivingEntity e, MobEffectInstance ins) {
        MobEffect effect = ins.getEffect();
        MobEffectInstance old = e.getActiveEffectsMap().get(effect);
        if (old == null) {
            e.getActiveEffectsMap().put(effect, ins);
            effect.addAttributeModifiers(e, e.getAttributes(), ins.getAmplifier());
            // 同步客户端（绕过 addEffect 内 MobEffectEvent.Added，避免触发外部监听器）
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

        DamageSource source = event.getSource();

        if (source.is(TccDamageSources.IMAGINARY_DAMAGE_TAG)) {
            double resistance = target.getAttributeValue(TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get());
            // 抗性范围 -100~100，正值按百分比减伤，负值按百分比增伤
            resistance = Math.max(-100.0, Math.min(100.0, resistance));

            float originalDamage = event.getAmount();
            float damageAfterResistance = (float) (originalDamage * (1.0 - resistance / 100.0));

            double ampPerLevel = TaczCuriosConfig.COMMON.imaginaryInfectionAmpPerLevel.get();
            int infectionLevel = 0;
            var infectionEffect = TccMobEffects.IMAGINARY_INFECTION.get();
            if (infectionEffect != null) {
                var effectInstance = target.getEffect(infectionEffect);
                if (effectInstance != null) {
                    infectionLevel = effectInstance.getAmplifier() + 1;
                }
            }
            float finalDamage = (float) (damageAfterResistance * (1.0 + infectionLevel * ampPerLevel));

            event.setAmount(finalDamage);

            // 伤害 > 0 保证，在此施加虚数侵染/崩解，避免对无敌实体误施 buff
            applyImaginaryBuffs(target, source);
        }
    }

    /**
     * 虚数侵染/虚数崩解持续期间抑制生命恢复。
     */
    @SubscribeEvent
    public static void onLivingHeal(LivingHealEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.hasEffect(TccMobEffects.IMAGINARY_INFECTION.get())
                || entity.hasEffect(TccMobEffects.IMAGINARY_COLLAPSE.get())) {
            event.setCanceled(true);
        }
    }

    /**
     * 阻止 tcc 模组的效果被移除（Forge 事件双重保险）。
     * 优先级 HIGHEST 确保最先处理，在其他监听器之前拦截。
     */
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

    /**
     * EL 第四诅咒补偿：根据装备的饰品等级，递减性抵消七咒之戒对怪物/EnderDragon 的伤害降低效果。
     * 优先级 LOWEST 确保在 EL 的 LivingHurtEvent 处理器之后执行。
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtELCurseCompensation(LivingHurtEvent event) {
        if (!ModList.get().isLoaded("enigmaticlegacy")) return;
        if (event.getEntity().level().isClientSide()) return;
        // EL 第四诅咒仅对 Monster 和 EnderDragon 生效
        if (!(event.getEntity() instanceof Monster || event.getEntity() instanceof EnderDragon)) return;
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        if (!SuperpositionHandler.isTheCursedOne(player)) return;

        // 按饰品等级确定诅咒削减比例（通过配置文件可调）
        double curseReduction;
        if (Salvation.hasSalvationEquipped(player)) {
            curseReduction = TaczCuriosConfig.COMMON.salvationELCurseReduction.get();
        } else if (BrahmaBeasts.hasBrahmaBeastsEquipped(player)) {
            curseReduction = TaczCuriosConfig.COMMON.brahmaBeastsELCurseReduction.get();
        } else if (SummerBeach.hasSummerBeachEquipped(player)) {
            curseReduction = TaczCuriosConfig.COMMON.summerBeachELCurseReduction.get();
        } else {
            return;
        }

        // modifier = EL 已应用的伤害倍率（默认 0.5）
        double modifier = CursedRing.monsterDamageDebuff.getValue().asModifierInverted();
        // effectiveModifier = modifier + reduction * (1 - modifier)
        double effectiveModifier = modifier + curseReduction * (1.0 - modifier);
        // compensation = effectiveModifier / modifier 把伤害恢复到目标水平
        float compensation = (float) (effectiveModifier / modifier);
        event.setAmount(event.getAmount() * compensation);
    }
}
