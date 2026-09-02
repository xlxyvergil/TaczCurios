package com.xlxyvergil.tcc.event;

import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.tacz.guns.api.event.common.GunDamageSourcePart;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.attribute.TccAttributes;
import com.xlxyvergil.tcc.compat.apollyon.ApollyonCompat;
import com.xlxyvergil.tcc.compat.maid.MaidCompat;
import com.xlxyvergil.tcc.core.TccDamageSources;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.util.ImaginaryInfectionHelper;
import com.xlxyvergil.tcc.items.curios.bound.IslandBoomRaven;
import com.xlxyvergil.tcc.registries.TccItems;
import com.xlxyvergil.tcc.registries.TccMobEffects;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
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

    
    public static final String INFECTION_ATTACKER_KEY = "tcc_infection_attacker";

    /** tacz:bullets —— TACZ 枪械子弹伤害 tag */
    private static final TagKey<DamageType> TACZ_BULLETS_TAG =
        TagKey.create(Registries.DAMAGE_TYPE, new ResourceLocation("tacz", "bullets"));

    /**
     * 判断伤害来源是否属于实体的主动攻击：
     *  玩家/生物普通攻击（PLAYER_ATTACK / MOB_ATTACK）、
     *  枪械子弹（tacz:bullets）、
     *  以及虚数枪伤（tcc:imaginary_damage，由 ImaginaryConversionHelper 在 Pre 阶段把子弹源替换而来）。
     * 用于过滤其它模组在 LivingHurtEvent 中通过 FastHurt 再入产生的强制子伤害
     *  （IN_FIRE / WIND_FLOW / FROST_FLAME / MOB_CUTTING / GENERIC_KILL 等），切断再入栈溢出。
     */
    public static boolean isActiveAttackSource(DamageSource source) {
        return source.is(DamageTypes.PLAYER_ATTACK)
            || source.is(DamageTypes.MOB_ATTACK)
            || source.is(TACZ_BULLETS_TAG)
            || source.is(TccDamageSources.IMAGINARY_DAMAGE_TAG);
    }

    
    public static boolean applyImaginaryDamage(LivingEntity target, DamageSource source, float intendedDamage) {
        if (intendedDamage <= 0) return false;

        target.invulnerableTime = 0;

        
        if (source.getEntity() instanceof LivingEntity attacker) {
            double damageMult = attacker.getAttributeValue(AttributeHelper.ATTACK_DAMAGE);
            intendedDamage = (float) (intendedDamage * (1 + damageMult / TaczCuriosConfig.COMMON.imaginaryDamageAttackAmplification.get()));
        }

        
        if (ApollyonCompat.isRevelationFixApostle(target)) {
            if (target.level().dimension() == Level.NETHER) {
                float newHealth = ApollyonCompat.applyDirectDamage(target, intendedDamage);
                if (newHealth <= 0) {
                    target.die(source);
                }
                return true;
            }
            
            ApollyonCompat.clearHitCooldown(target);
        }

        
        float finalDamage = resolveFinalImaginaryDamage(target, source, intendedDamage);
        if (finalDamage <= 0) return false;

        
        if (source.getEntity() instanceof LivingEntity attacker) {
            target.setLastHurtByMob(attacker);
        }

        float newHealth = target.getHealth() - finalDamage;
        if (newHealth <= 0) {
            target.setHealth(0);
            
            target.die(source);
            return true;
        }
        target.setHealth(newHealth);
        return true;
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

        
        applyInfection(living, attacker, ImaginaryInfectionHelper.resolve(attacker));
    }

    
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
        
        forceAddEffect(living, newInstance);
        living.addEffect(newInstance, attacker);

        
        // 记录侵染来源攻击者，使虚数崩解击杀能正确归属
        // （若攻击者是女仆，转换为女仆主人，以让崩解击杀计入主人名下）
        LivingEntity credited = MaidCompat.resolveOwnerPlayer(attacker);
        living.getPersistentData().putString(
                INFECTION_ATTACKER_KEY, (credited != null ? credited : attacker).getStringUUID());

        
        
        
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
