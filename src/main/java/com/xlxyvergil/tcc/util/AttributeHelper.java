package com.xlxyvergil.tcc.util;

import com.xlxyvergil.taa.attribute.EntityAttributeRegistry;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import dev.shadowsoffire.apothic_attributes.api.ALObjects;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AttributeHelper {

    /** 记录修饰符 ID → 来源饰品 Item，供客户端属性面板显示来源图标。 */
    private static final Map<ResourceLocation, Item> MODIFIER_SOURCES = new ConcurrentHashMap<>();

    /** 登记某个修饰符的来源饰品。 */
    public static void registerSourceItem(ResourceLocation id, Item item) {
        if (id != null && item != null) {
            MODIFIER_SOURCES.putIfAbsent(id, item);
        }
    }

    /** 按修饰符 ID 查询来源饰品。 */
    public static Item getSourceItem(ResourceLocation id) {
        return id == null ? null : MODIFIER_SOURCES.get(id);
    }


    public static final Holder<Attribute> ATTACK_DAMAGE = Attributes.ATTACK_DAMAGE;

    public static final Holder<Attribute> MAX_HEALTH = Attributes.MAX_HEALTH;

    public static final Holder<Attribute> ARMOR = Attributes.ARMOR;

    public static final Holder<Attribute> MOVEMENT_SPEED = Attributes.MOVEMENT_SPEED;

    public static final Holder<Attribute> LUCK = Attributes.LUCK;


    public static final Holder<Attribute> ENTITY_REACH = Attributes.ENTITY_INTERACTION_RANGE;

    public static final Holder<Attribute> BLOCK_REACH = Attributes.BLOCK_INTERACTION_RANGE;


    public static final Holder<Attribute> CRIT_CHANCE = ALObjects.Attributes.CRIT_CHANCE;

    public static final Holder<Attribute> CRIT_DAMAGE = ALObjects.Attributes.CRIT_DAMAGE;

    public static final Holder<Attribute> LIFE_STEAL = ALObjects.Attributes.LIFE_STEAL;

    public static final Holder<Attribute> OVERHEAL = ALObjects.Attributes.OVERHEAL;


    public static final Holder<Attribute> BULLET_GUNDAMAGE = EntityAttributeRegistry.BULLET_GUNDAMAGE;

    public static final Holder<Attribute> ADS_TIME = EntityAttributeRegistry.ADS_TIME;

    public static final Holder<Attribute> AMMO_SPEED = EntityAttributeRegistry.AMMO_SPEED;

    public static final Holder<Attribute> ARMOR_IGNORE = EntityAttributeRegistry.ARMOR_IGNORE;

    public static final Holder<Attribute> EFFECTIVE_RANGE = EntityAttributeRegistry.EFFECTIVE_RANGE;

    public static final Holder<Attribute> EXPLOSION_RADIUS = EntityAttributeRegistry.EXPLOSION_RADIUS;

    public static final Holder<Attribute> EXPLOSION_DAMAGE = EntityAttributeRegistry.EXPLOSION_DAMAGE;

    public static final Holder<Attribute> EXPLOSION_KNOCKBACK = EntityAttributeRegistry.EXPLOSION_KNOCKBACK;

    public static final Holder<Attribute> EXPLOSION_DESTROY_BLOCK = EntityAttributeRegistry.EXPLOSION_DESTROY_BLOCK;

    public static final Holder<Attribute> EXPLOSION_DELAY = EntityAttributeRegistry.EXPLOSION_DELAY;

    public static final Holder<Attribute> EXPLOSION_ENABLED = EntityAttributeRegistry.EXPLOSION_ENABLED;

    public static final Holder<Attribute> MOVE_SPEED = EntityAttributeRegistry.MOVE_SPEED;

    public static final Holder<Attribute> HEADSHOT_MULTIPLIER = EntityAttributeRegistry.HEADSHOT_MULTIPLIER;

    public static final Holder<Attribute> IGNITE = EntityAttributeRegistry.IGNITE;

    public static final Holder<Attribute> INACCURACY = EntityAttributeRegistry.INACCURACY;

    public static final Holder<Attribute> INACCURACY_STAND = EntityAttributeRegistry.INACCURACY_STAND;

    public static final Holder<Attribute> INACCURACY_MOVE = EntityAttributeRegistry.INACCURACY_MOVE;

    public static final Holder<Attribute> INACCURACY_SNEAK = EntityAttributeRegistry.INACCURACY_SNEAK;

    public static final Holder<Attribute> INACCURACY_LIE = EntityAttributeRegistry.INACCURACY_LIE;

    public static final Holder<Attribute> INACCURACY_AIM = EntityAttributeRegistry.INACCURACY_AIM;

    public static final Holder<Attribute> KNOCKBACK = EntityAttributeRegistry.KNOCKBACK;

    public static final Holder<Attribute> PIERCE = EntityAttributeRegistry.PIERCE;

    public static final Holder<Attribute> RECOIL = EntityAttributeRegistry.RECOIL;

    public static final Holder<Attribute> RECOIL_PITCH = EntityAttributeRegistry.RECOIL_PITCH;

    public static final Holder<Attribute> RECOIL_YAW = EntityAttributeRegistry.RECOIL_YAW;

    public static final Holder<Attribute> ROUNDS_PER_MINUTE = EntityAttributeRegistry.ROUNDS_PER_MINUTE;

    public static final Holder<Attribute> SILENCE = EntityAttributeRegistry.SILENCE;

    public static final Holder<Attribute> WEIGHT = EntityAttributeRegistry.WEIGHT;

    public static final Holder<Attribute> BULLET_COUNT = EntityAttributeRegistry.BULLET_COUNT;

    public static final Holder<Attribute> MAGAZINE_CAPACITY = EntityAttributeRegistry.MAGAZINE_CAPACITY;

    public static final Holder<Attribute> RELOAD_TIME = EntityAttributeRegistry.RELOAD_TIME;

    public static final Holder<Attribute> MELEE_DAMAGE = EntityAttributeRegistry.MELEE_DAMAGE;

    public static final Holder<Attribute> MELEE_DISTANCE = EntityAttributeRegistry.MELEE_DISTANCE;


    public static final Holder<Attribute> HEAT_MAX = EntityAttributeRegistry.HEAT_MAX;

    public static final Holder<Attribute> HEAT_COOLING = EntityAttributeRegistry.HEAT_COOLING;

    public static final Holder<Attribute> BULLET_GUNDAMAGE_PISTOL = EntityAttributeRegistry.BULLET_GUNDAMAGE_PISTOL;
    public static final Holder<Attribute> BULLET_GUNDAMAGE_RIFLE = EntityAttributeRegistry.BULLET_GUNDAMAGE_RIFLE;
    public static final Holder<Attribute> BULLET_GUNDAMAGE_SHOTGUN = EntityAttributeRegistry.BULLET_GUNDAMAGE_SHOTGUN;
    public static final Holder<Attribute> BULLET_GUNDAMAGE_SNIPER = EntityAttributeRegistry.BULLET_GUNDAMAGE_SNIPER;
    public static final Holder<Attribute> BULLET_GUNDAMAGE_SMG = EntityAttributeRegistry.BULLET_GUNDAMAGE_SMG;
    public static final Holder<Attribute> BULLET_GUNDAMAGE_LMG = EntityAttributeRegistry.BULLET_GUNDAMAGE_LMG;
    public static final Holder<Attribute> BULLET_GUNDAMAGE_LAUNCHER = EntityAttributeRegistry.BULLET_GUNDAMAGE_LAUNCHER;

    public static Holder<Attribute> resolveAttribute(String attributeId) {
        if (attributeId == null || attributeId.isBlank()) {
            return null;
        }
        ResourceLocation loc = ResourceLocation.tryParse(attributeId);
        if (loc == null) return null;
        return BuiltInRegistries.ATTRIBUTE.getHolder(loc).orElse(null);
    }

    public static AttributeInstance getInstance(LivingEntity entity, Holder<Attribute> attribute) {
        return entity.getAttributes().getInstance(attribute);
    }

    public static void applyModifier(LivingEntity entity, Holder<Attribute> attribute, double value, ResourceLocation id, AttributeModifier.Operation operation) {
        AttributeInstance instance = getInstance(entity, attribute);

        if (instance != null) {
            instance.removeModifier(id);

            AttributeModifier modifier = new AttributeModifier(id, value, operation);
            instance.addPermanentModifier(modifier);
        }
    }

    public static void removeModifier(LivingEntity entity, Holder<Attribute> attribute, ResourceLocation id) {
        AttributeInstance instance = getInstance(entity, attribute);

        if (instance != null) {
            instance.removeModifier(id);
        }
    }


    public static void applyStackingModifier(LivingEntity entity, Holder<Attribute> attribute, double delta,
                                             ResourceLocation id, AttributeModifier.Operation operation) {
        AttributeInstance instance = getInstance(entity, attribute);
        if (instance == null) {
            return;
        }
        double old = 0.0;
        AttributeModifier oldModifier = instance.getModifier(id);
        if (oldModifier != null) {
            old = oldModifier.amount();
        }
        double newValue = old + delta;
        instance.removeModifier(id);
        if (newValue != 0.0) {
            instance.addPermanentModifier(new AttributeModifier(id, newValue, operation));
        }
    }


    public static void applyAllAttributesModifier(LivingEntity entity, ResourceLocation id,
                                                  double value, AttributeModifier.Operation operation) {
        Collection<? extends String> blacklist = TaczCuriosConfig.COMMON.attributeBonusBlacklist.get();
        applyAllAttributesModifier(entity, id, value, operation, blacklist);
    }


    public static void applyInfiniteAllAttributesModifier(LivingEntity entity, ResourceLocation id, double value) {
        Collection<? extends String> blacklist = TaczCuriosConfig.COMMON.attributeBonusBlacklist.get();
        applyAllAttributesModifier(entity, id, value, AttributeModifier.Operation.ADD_MULTIPLIED_BASE, blacklist);
    }

    private static void applyAllAttributesModifier(LivingEntity entity, ResourceLocation id,
                                                   double value, AttributeModifier.Operation operation,
                                                   Collection<? extends String> blacklist) {
        for (Holder.Reference<Attribute> holder : BuiltInRegistries.ATTRIBUTE.holders().toList()) {
            if (blacklist != null && !blacklist.isEmpty()) {
                ResourceLocation key = holder.key().location();
                if (blacklist.contains(key.toString())) {
                    continue;
                }
            }
            AttributeInstance instance = entity.getAttributes().getInstance(holder);
            if (instance == null) {
                continue;
            }
            instance.removeModifier(id);
            if (value != 0.0) {
                instance.addTransientModifier(new AttributeModifier(id, value, operation));
            }
        }
    }
}
