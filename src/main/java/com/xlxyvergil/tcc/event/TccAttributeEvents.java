package com.xlxyvergil.tcc.event;

import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.tacz.guns.api.event.common.GunDamageSourcePart;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.attribute.TccAttributes;
import com.xlxyvergil.tcc.compat.apollyon.ApollyonCompat;
import com.xlxyvergil.tcc.core.TccDamageSources;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.util.ImaginaryInfectionHelper;
import com.xlxyvergil.tcc.items.curios.bound.IslandBoomRaven;
import com.xlxyvergil.tcc.registries.TccItems;
import com.xlxyvergil.tcc.registries.TccMobEffects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.curios.api.CuriosApi;


@Mod.EventBusSubscriber(modid = "tcc", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TccAttributeEvents {

    /** 虚数侵染来源 attacker UUID，供 ImaginaryCollapseEffect 读取以创建带 attacker 的 DamageSource */
    public static final String INFECTION_ATTACKER_KEY = "tcc_infection_attacker";

    /**
     * 虚数伤害统一注入入口（setHealth 直伤）。不经过 hurt()，故不触发 LivingHurtEvent / 护甲 / 盾牌 / 吸收 /
     * 荆棘等管线，为无视防御的真伤；倍率结算由 resolveFinalImaginaryDamage 内联完成，致死时调用 die(source)。
     */
    public static boolean applyImaginaryDamage(LivingEntity target, DamageSource source, float intendedDamage) {
        if (intendedDamage <= 0) return false;

        target.invulnerableTime = 0;

        // 吃到攻击者实体属性上的 TAA 枪械增伤：BULLET_GUNDAMAGE × BULLET_COUNT（默认均为 1.0，无加成时不改变伤害）。
        if (source.getEntity() instanceof LivingEntity attacker) {
            double damageMult = attacker.getAttributeValue(AttributeHelper.BULLET_GUNDAMAGE);
            double bulletCountMult = attacker.getAttributeValue(AttributeHelper.BULLET_COUNT);
            intendedDamage = (float) (intendedDamage * damageMult * bulletCountMult);
        }

        // 亚波伦路径：下界走自定义 HealthData 直伤，完全绕过 RevelationFix 限伤
        if (ApollyonCompat.isRevelationFixApostle(target)) {
            if (target.level().dimension() == Level.NETHER) {
                float newHealth = ApollyonCompat.applyDirectDamage(target, intendedDamage);
                if (newHealth <= 0) {
                    target.die(source);
                }
                return true;
            }
            // 非下界：同样走 setHealth 直伤（同样不受 APOLLYON_HURT_LIMIT 限制）
            ApollyonCompat.clearHitCooldown(target);
        }

        // 内联虚数倍率结算（setHealth 不触发 LivingHurtEvent，原 imaginaryDamageOnAttack 不再生效）
        float finalDamage = resolveFinalImaginaryDamage(target, source, intendedDamage);
        if (finalDamage <= 0) return false;

        // 击杀归属：保证 killed_by_player 战利品条件、击杀者经验、TACZ 枪杀判定正常
        if (source.getEntity() instanceof LivingEntity attacker) {
            target.setLastHurtByMob(attacker);
        }

        float newHealth = target.getHealth() - finalDamage;
        if (newHealth <= 0) {
            target.setHealth(0);
            // die() 内部会 post LivingDeathEvent 并生成掉落物/经验，不要手动再 post
            target.die(source);
            return true;
        }
        target.setHealth(newHealth);
        return true;
    }

    /**
     * 虚数伤害倍率结算（原 imaginaryDamageOnAttack 内联逻辑）：目标虚数抗性 → 侵染等级倍率 → 岛爆渡鸦加成，
     * 直伤与 hurt 两条路径共用。
     */
    private static float resolveFinalImaginaryDamage(LivingEntity target, DamageSource source, float baseDamage) {
        if (baseDamage <= 0) return 0;

        double resistance = target.getAttributeValue(TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get());
        // 抗性范围 -100~100，正值按百分比减伤，负值按百分比增伤
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

        double attackerBonus = 1.0;
        if (source.getEntity() instanceof LivingEntity attacker && IslandBoomRaven.hasEquipped(attacker)) {
            double attackerRes = attacker.getAttributeValue(TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get());
            attackerBonus = Math.round((1.0 + attackerRes / 100.0) * 10000.0) / 10000.0;
        }

        return (float) ((float) Math.round((damageAfterResistance * (1.0 + infectionLevel * ampPerLevel) * attackerBonus) * 10000.0) / 10000.0);
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
        applyInfection(living, attacker, ImaginaryInfectionHelper.resolve(attacker));
    }

    /**
     * 对目标施加虚数侵染（可叠加，受饰品分级上限约束）；仅 canApplyCollapse=true 时额外触发虚数崩解。
     */
    public static void applyInfection(LivingEntity living, LivingEntity attacker, ImaginaryInfectionHelper.InfectionInfo info) {
        if (!info.isValid()) return;
        int maxLevel = info.maxLevel();
        boolean canApplyCollapse = info.canApplyCollapse();
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
        // forceAddEffect 写 Map，addEffect 负责网络同步（Applicable 事件由 HIGHEST 监听器放行）
        forceAddEffect(living, newInstance);
        living.addEffect(newInstance, attacker);

        // 记录侵染来源 attacker，供 ImaginaryCollapseEffect 创建带 attacker 的 DamageSource
        living.getPersistentData().putString(INFECTION_ATTACKER_KEY, attacker.getStringUUID());

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
     * 处理 Pathway A 虚数伤害的 overheal：子弹为直接实体时 Apothic 无法原生触发，故通过 TACZ Post 事件补足。
     */
    @SubscribeEvent
    public static void onGunOverheal(EntityHurtByGunEvent.Post event) {
        if (event.getLogicalSide().isClient()) return;

        LivingEntity attacker = event.getAttacker();
        if (attacker == null) return;

        DamageSource source = event.getDamageSource(GunDamageSourcePart.NON_ARMOR_PIERCING);
        if (!source.is(TccDamageSources.IMAGINARY_DAMAGE_TAG)) return;
        // Pathway B 的攻击者已是直接实体，Apothic 原生可处理，跳过避免 double trigger
        if (source.getDirectEntity() instanceof LivingEntity) return;

        Attribute overhealAttr = ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation("attributeslib", "overheal"));
        if (overhealAttr == null) return;
        float overheal = (float) attacker.getAttributeValue(overhealAttr);
        if (overheal <= 0) return;

        float damage = event.getBaseAmount();
        if (event.isHeadShot()) damage *= event.getHeadshotMultiplier();

        // Apothic 用 min(damage, targetHealth) 防止溢出伤害转化为护盾
        // Post 事件中目标已承伤，取 min(damage, maxHealth) 作为保守估计
        if (!(event.getHurtEntity() instanceof LivingEntity target)) return;
        float effectiveDamage = Math.min(damage, target.getMaxHealth());

        float maxOverheal = attacker.getMaxHealth() * 0.5F;
        if (attacker.getAbsorptionAmount() < maxOverheal) {
            attacker.setAbsorptionAmount(
                Math.min(maxOverheal, attacker.getAbsorptionAmount() + effectiveDamage * overheal)
            );
        }
    }

    private static boolean attackerHasHarmfulCurio(LivingEntity attacker) {
        if (!(attacker instanceof Player)) return false;
        return CuriosApi.getCuriosInventory(attacker).resolve()
            .map(inv -> 
                inv.findFirstCurio(TccItems.GILDED_RIFLE_APTITUDE).isPresent() ||
                inv.findFirstCurio(TccItems.GILDED_SHOTGUN_SAVVY).isPresent() ||
                inv.findFirstCurio(TccItems.GILDED_MARKSMAN).isPresent() ||
                inv.findFirstCurio(TccItems.CONDITION_OVERLOAD).isPresent()
            ).orElse(false);
    }

    /**
     * 直接写 getActiveEffectsMap().put 并绕过 MobEffectEvent.Added，避免外部监听器干扰；old.update(ins) 原地刷新时长/等级。
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

    /**
     * 处理 Pathway B（走 hurt 的子弹虚数伤害）倍率结算；注意 applyImaginaryDamage 已改走 setHealth 直伤，
     * 其倍率由 resolveFinalImaginaryDamage 内联完成。
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
     * 阻止虚空珍珠等拦截至 tcc 效果的添加；优先级 LOWEST，在 EnigmaticEventHandler.onApplyPotion(Applicable) 之后用
     * ALLOW 覆盖其 DENY（Forge setResult 最后调用者胜出）。
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
     * 双重保险阻止 tcc 效果被移除，优先级 HIGHEST 最先处理。
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

}
