package com.xlxyvergil.tcc.util;

import com.xlxyvergil.taa.attribute.EntityAttributeRegistry;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import dev.shadowsoffire.attributeslib.api.ALObjects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collection;
import java.util.UUID;

public class AttributeHelper {
    
    
    
    
    public static final Attribute ATTACK_DAMAGE = Attributes.ATTACK_DAMAGE;
    
    
    public static final Attribute MAX_HEALTH = Attributes.MAX_HEALTH;
    
    
    public static final Attribute ARMOR = Attributes.ARMOR;
    
    
    public static final Attribute MOVEMENT_SPEED = Attributes.MOVEMENT_SPEED;
    
    
    public static final Attribute LUCK = Attributes.LUCK;
    
    
    
    
    public static final Attribute ENTITY_REACH = ForgeMod.ENTITY_REACH.get();
    
    
    public static final Attribute BLOCK_REACH = ForgeMod.BLOCK_REACH.get();
    
    
    
    
    public static final Attribute CRIT_CHANCE = ALObjects.Attributes.CRIT_CHANCE.get();
    
    
    public static final Attribute CRIT_DAMAGE = ALObjects.Attributes.CRIT_DAMAGE.get();
    
    
    public static final Attribute LIFE_STEAL = ALObjects.Attributes.LIFE_STEAL.get();
    
    
    public static final Attribute OVERHEAL = ALObjects.Attributes.OVERHEAL.get();
    
    
    
    
    public static final Attribute BULLET_GUNDAMAGE = EntityAttributeRegistry.BULLET_GUNDAMAGE.get();
    
    
    public static final Attribute ADS_TIME = EntityAttributeRegistry.ADS_TIME.get();
    
    
    public static final Attribute AMMO_SPEED = EntityAttributeRegistry.AMMO_SPEED.get();
    
    
    public static final Attribute ARMOR_IGNORE = EntityAttributeRegistry.ARMOR_IGNORE.get();
    
    
    public static final Attribute EFFECTIVE_RANGE = EntityAttributeRegistry.EFFECTIVE_RANGE.get();
    
    
    public static final Attribute EXPLOSION_RADIUS = EntityAttributeRegistry.EXPLOSION_RADIUS.get();
    
    
    public static final Attribute EXPLOSION_DAMAGE = EntityAttributeRegistry.EXPLOSION_DAMAGE.get();
    
    
    public static final Attribute EXPLOSION_KNOCKBACK = EntityAttributeRegistry.EXPLOSION_KNOCKBACK.get();
    
    
    public static final Attribute EXPLOSION_DESTROY_BLOCK = EntityAttributeRegistry.EXPLOSION_DESTROY_BLOCK.get();
    
    
    public static final Attribute EXPLOSION_DELAY = EntityAttributeRegistry.EXPLOSION_DELAY.get();
    
    
    public static final Attribute EXPLOSION_ENABLED = EntityAttributeRegistry.EXPLOSION_ENABLED.get();
    
    
    public static final Attribute MOVE_SPEED = EntityAttributeRegistry.MOVE_SPEED.get();
    
    
    public static final Attribute HEADSHOT_MULTIPLIER = EntityAttributeRegistry.HEADSHOT_MULTIPLIER.get();
    
    
    public static final Attribute IGNITE = EntityAttributeRegistry.IGNITE.get();
    
    
    public static final Attribute INACCURACY = EntityAttributeRegistry.INACCURACY.get();
    
    
    public static final Attribute INACCURACY_STAND = EntityAttributeRegistry.INACCURACY_STAND.get();
    
    
    public static final Attribute INACCURACY_MOVE = EntityAttributeRegistry.INACCURACY_MOVE.get();
    
    
    public static final Attribute INACCURACY_SNEAK = EntityAttributeRegistry.INACCURACY_SNEAK.get();
    
    
    public static final Attribute INACCURACY_LIE = EntityAttributeRegistry.INACCURACY_LIE.get();
    
    
    public static final Attribute INACCURACY_AIM = EntityAttributeRegistry.INACCURACY_AIM.get();
    
    
    public static final Attribute KNOCKBACK = EntityAttributeRegistry.KNOCKBACK.get();
    
    
    public static final Attribute PIERCE = EntityAttributeRegistry.PIERCE.get();
    
    
    public static final Attribute RECOIL = EntityAttributeRegistry.RECOIL.get();
    
    
    public static final Attribute RECOIL_PITCH = EntityAttributeRegistry.RECOIL_PITCH.get();
    
    
    public static final Attribute RECOIL_YAW = EntityAttributeRegistry.RECOIL_YAW.get();
    
    
    public static final Attribute ROUNDS_PER_MINUTE = EntityAttributeRegistry.ROUNDS_PER_MINUTE.get();
    
    
    public static final Attribute SILENCE = EntityAttributeRegistry.SILENCE.get();
    
    
    public static final Attribute WEIGHT = EntityAttributeRegistry.WEIGHT.get();
    
    
    public static final Attribute BULLET_COUNT = EntityAttributeRegistry.BULLET_COUNT.get();
    
    
    public static final Attribute MAGAZINE_CAPACITY = EntityAttributeRegistry.MAGAZINE_CAPACITY.get();
    
    
    public static final Attribute RELOAD_TIME = EntityAttributeRegistry.RELOAD_TIME.get();
    
    
    public static final Attribute MELEE_DAMAGE = EntityAttributeRegistry.MELEE_DAMAGE.get();
    
    
    public static final Attribute MELEE_DISTANCE = EntityAttributeRegistry.MELEE_DISTANCE.get();
    
    
    
    
    public static final Attribute HEAT_MAX = EntityAttributeRegistry.HEAT_MAX.get();
    
    
    public static final Attribute HEAT_COOLING = EntityAttributeRegistry.HEAT_COOLING.get();
    
    
    public static final Attribute BULLET_GUNDAMAGE_PISTOL = EntityAttributeRegistry.BULLET_GUNDAMAGE_PISTOL.get();
    public static final Attribute BULLET_GUNDAMAGE_RIFLE = EntityAttributeRegistry.BULLET_GUNDAMAGE_RIFLE.get();
    public static final Attribute BULLET_GUNDAMAGE_SHOTGUN = EntityAttributeRegistry.BULLET_GUNDAMAGE_SHOTGUN.get();
    public static final Attribute BULLET_GUNDAMAGE_SNIPER = EntityAttributeRegistry.BULLET_GUNDAMAGE_SNIPER.get();
    public static final Attribute BULLET_GUNDAMAGE_SMG = EntityAttributeRegistry.BULLET_GUNDAMAGE_SMG.get();
    public static final Attribute BULLET_GUNDAMAGE_LMG = EntityAttributeRegistry.BULLET_GUNDAMAGE_LMG.get();
    public static final Attribute BULLET_GUNDAMAGE_LAUNCHER = EntityAttributeRegistry.BULLET_GUNDAMAGE_LAUNCHER.get();

    public static Attribute resolveAttribute(String attributeId) {
        if (attributeId == null || attributeId.isBlank()) {
            return null;
        }
        ResourceLocation loc = ResourceLocation.tryParse(attributeId);
        if (loc == null) return null;
        return ForgeRegistries.ATTRIBUTES.getValue(loc);
    }
    
    public static AttributeInstance getInstance(LivingEntity entity, Attribute attribute) {
        return entity.getAttributes().getInstance(attribute);
    }
    
    public static void applyModifier(LivingEntity entity, Attribute attribute, double value, UUID uuid, String name, AttributeModifier.Operation operation) {
        AttributeInstance instance = getInstance(entity, attribute);
        
        if (instance != null) {
            instance.removeModifier(uuid);
            
            AttributeModifier modifier = new AttributeModifier(uuid, name, value, operation);
            instance.addPermanentModifier(modifier);
        }
    }
    
    public static void removeModifier(LivingEntity entity, Attribute attribute, UUID uuid) {
        AttributeInstance instance = getInstance(entity, attribute);
        
        if (instance != null) {
            instance.removeModifier(uuid);
        }
    }

    
    public static void applyStackingModifier(LivingEntity entity, Attribute attribute, double delta,
                                             UUID uuid, String name, AttributeModifier.Operation operation) {
        AttributeInstance instance = getInstance(entity, attribute);
        if (instance == null) {
            return;
        }
        double old = 0.0;
        AttributeModifier oldModifier = instance.getModifier(uuid);
        if (oldModifier != null) {
            old = oldModifier.getAmount();
        }
        double newValue = old + delta;
        instance.removeModifier(uuid);
        if (newValue != 0.0) {
            instance.addPermanentModifier(new AttributeModifier(uuid, name, newValue, operation));
        }
    }

    
    public static void applyAllAttributesModifier(LivingEntity entity, UUID uuid, String name,
                                                  double value, AttributeModifier.Operation operation) {
        Collection<? extends String> blacklist = TaczCuriosConfig.COMMON.attributeBonusBlacklist.get();
        applyAllAttributesModifier(entity, uuid, name, value, operation, blacklist);
    }

    
    public static void applyInfiniteAllAttributesModifier(LivingEntity entity, UUID uuid, String name, double value) {
        Collection<? extends String> blacklist = TaczCuriosConfig.COMMON.attributeBonusBlacklist.get();
        applyAllAttributesModifier(entity, uuid, name, value, AttributeModifier.Operation.MULTIPLY_BASE, blacklist);
    }

    private static void applyAllAttributesModifier(LivingEntity entity, UUID uuid, String name,
                                                   double value, AttributeModifier.Operation operation,
                                                   Collection<? extends String> blacklist) {
        for (Attribute attribute : ForgeRegistries.ATTRIBUTES.getValues()) {
            if (blacklist != null && !blacklist.isEmpty()) {
                ResourceLocation key = ForgeRegistries.ATTRIBUTES.getKey(attribute);
                if (key != null && blacklist.contains(key.toString())) {
                    continue;
                }
            }
            AttributeInstance instance = entity.getAttributes().getInstance(attribute);
            if (instance == null) {
                continue;
            }
            instance.removeModifier(uuid);
            if (value != 0.0) {
                instance.addTransientModifier(new AttributeModifier(uuid, name, value, operation));
            }
        }
    }
}
