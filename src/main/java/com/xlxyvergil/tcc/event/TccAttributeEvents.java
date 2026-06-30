package com.xlxyvergil.tcc.event;

import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.tacz.guns.api.event.common.GunDamageSourcePart;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.items.CursedRing;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.attribute.TccAttributes;
import com.xlxyvergil.tcc.compat.apollyon.ApollyonCompat;
import com.xlxyvergil.tcc.core.TccDamageSources;
import com.xlxyvergil.tcc.util.ImaginaryInfectionHelper;
import com.xlxyvergil.tcc.items.BrahmaBeasts;
import com.xlxyvergil.tcc.items.IslandBoomRaven;
import com.xlxyvergil.tcc.items.Salvation;
import com.xlxyvergil.tcc.items.SummerBeach;
import com.xlxyvergil.tcc.registries.TaczItems;
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
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.curios.api.CuriosApi;


@Mod.EventBusSubscriber(modid = "tcc", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TccAttributeEvents {

    private static final java.util.concurrent.ConcurrentHashMap<LivingEntity, Float> INTENDED_DAMAGE
        = new java.util.concurrent.ConcurrentHashMap<>();

    public static void setIntendedDamage(LivingEntity entity, float damage) {
        INTENDED_DAMAGE.put(entity, damage);
    }
    public static Float takeIntendedDamage(LivingEntity entity) {
        return INTENDED_DAMAGE.remove(entity);
    }
    public static Float peekIntendedDamage(LivingEntity entity) {
        return INTENDED_DAMAGE.get(entity);
    }

    public static boolean applyImaginaryDamage(LivingEntity target, DamageSource source, float intendedDamage) {
        if (intendedDamage <= 0) return false;

        target.invulnerableTime = 0;

        // 亚波伦直伤路径：不经过 hurt() 以避免 RevelationFix 的限伤拦截
        if (ApollyonCompat.isRevelationFixApostle(target)) {
            ApollyonCompat.applyDirectDamage(target, intendedDamage);
            return true;
        }

        setIntendedDamage(target, intendedDamage);
        try {
            return target.hurt(source, intendedDamage);
        } finally {
            takeIntendedDamage(target);
        }
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

        // 通过工具类获取侵染配置（新增饰品只需修改 ImaginaryInfectionHelper）
        var info = ImaginaryInfectionHelper.resolve(attacker);
        if (!info.isValid()) return;
        int maxLevel = info.maxLevel();
        boolean canApplyCollapse = info.canApplyCollapse();
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
        // forceAddEffect 写 Map，addEffect 负责网络同步（Applicable 事件由 HIGHEST 监听器放行）
        forceAddEffect(living, newInstance);
        living.addEffect(newInstance, attacker);

        // 仅天火劫灭/劫灭无尽可触发虚数崩解
        // 自然消失前无法再次施加，避免枪械连射导致 duration 被反复刷新为 20 的倍数，
        // 进而使 isDurationEffectTick(duration % 20 == 0) 频繁命中，造成异常高伤害。
        if (canApplyCollapse) {
            var collapse = TccMobEffects.IMAGINARY_COLLAPSE.get();
            if (!living.hasEffect(collapse)) {
                var collapseInstance = new MobEffectInstance(
                    collapse,
                    duration * 20,
                    0,
                    false, false, true
                );
                forceAddEffect(living, collapseInstance);
                living.addEffect(collapseInstance, attacker);

                // 检查攻击者是否装备负面增伤饰品（镀层步枪才能/通晓霰弹枪/准确射手/异况超量）
                if (attackerHasHarmfulCurio(attacker)) {
                    var erosion = TccMobEffects.EROSION.get();
                    var erosionInstance = new MobEffectInstance(
                        erosion,
                        duration * 20,
                        0,
                        false, false, true
                    );
                    forceAddEffect(living, erosionInstance);
                    living.addEffect(erosionInstance, attacker);
                }
            }
        }
    }

    /**
     * 检查攻击者是否装备了负面效果种数增伤饰品。
     */
    private static boolean attackerHasHarmfulCurio(LivingEntity attacker) {
        if (!(attacker instanceof Player)) return false;
        return CuriosApi.getCuriosInventory(attacker).resolve()
            .map(inv -> 
                inv.findFirstCurio(TaczItems.GILDED_RIFLE_APTITUDE.get()).isPresent() ||
                inv.findFirstCurio(TaczItems.GILDED_SHOTGUN_SAVVY.get()).isPresent() ||
                inv.findFirstCurio(TaczItems.GILDED_MARKSMAN.get()).isPresent() ||
                inv.findFirstCurio(TaczItems.CONDITION_OVERLOAD.get()).isPresent()
            ).orElse(false);
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
            double attackerBonus = 1.0;
            if (source.getEntity() instanceof LivingEntity attacker && IslandBoomRaven.hasEquipped(attacker)) {
                double attackerRes = attacker.getAttributeValue(TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get());
                attackerBonus = 1.0 + attackerRes / 100.0;
            }

            float finalDamage = (float) (damageAfterResistance * (1.0 + infectionLevel * ampPerLevel) * attackerBonus);

            event.setAmount(finalDamage);
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
     * 阻止虚空珍珠等拦截至 tcc 效果的添加。
     * 优先级 LOWEST，在 EnigmaticEventHandler.onApplyPotion(Applicable) 之后执行，
     * 对 tcc 效果用 ALLOW 覆盖其 DENY（Forge setResult 最后调用者胜出）。
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onEffectApplicable(MobEffectEvent.Applicable event) {
        if (event.getResult() == Result.ALLOW) return;
        var key = ForgeRegistries.MOB_EFFECTS.getKey(event.getEffectInstance().getEffect());
        if (key != null && key.getNamespace().equals("tcc")) {
            event.setResult(Result.ALLOW);
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
