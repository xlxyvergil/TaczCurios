package com.xlxyvergil.tcc.evolution;

import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.util.GunTypeChecker;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Matches achievement conditions against in-game events.
 * Used by all trigger handlers to check if a player meets
 * the requirements for advancing an achievement's progress.
 */
public final class AchievementConditionMatcher {
    private AchievementConditionMatcher() {}

    /**
     * Check if ALL conditions for an achievement are met for a kill event.
     */
    public static boolean matchesKillConditions(Player player, LivingEntity killed,
                                                 ResourceLocation gunId, AchievementDefinitions.AchievementDef def) {
        AchievementDefinitions.AchievementConditions c = def.conditions();
        if (c == null) return true;

        // Check equipped curios
        if (c.equippedCurios() != null) {
            for (String curio : c.equippedCurios()) {
                if (!LivingDeathEventHandler.hasEquipped(player, curio)) return false;
            }
        }

        // Check required effects
        if (c.requiredEffects() != null) {
            for (String effectId : c.requiredEffects()) {
                MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(effectId));
                if (effect == null || !player.hasEffect(effect)) return false;
            }
        }

        // Check gun type
        if (c.holdingGunTypes() != null && !c.holdingGunTypes().isEmpty() && gunId != null) {
            if (!GunTypeChecker.matchesGunTypes(gunId, c.holdingGunTypes())) return false;
        }

        // Check min distance
        if (c.minDistance() != null && killed != null) {
            double min = c.minDistance();
            if (player.distanceToSqr(killed) < min * min) return false;
        }

        // Check kill entity type
        if (c.kills() != null && !c.kills().isEmpty() && killed != null) {
            String killedKey = BuiltInRegistries.ENTITY_TYPE.getKey(killed.getType()).toString();
            boolean matched = false;
            for (AchievementDefinitions.KillCondition kc : c.kills()) {
                if ("*".equals(kc.entity()) || killedKey.equals(kc.entity())) {
                    matched = true;
                    break;
                }
            }
            if (!matched) return false;
        }

        // Check attributes
        if (c.attributes() != null) {
            for (AchievementDefinitions.AttributeCondition ac : c.attributes()) {
                Attribute attr = AttributeHelper.resolveAttribute(ac.attribute());
                if (attr == null) return false;
                double value = player.getAttributeValue(attr);
                if (!compare(value, ac.comparator(), ac.value())) return false;
            }
        }

        return true;
    }

    /**
     * Check conditions for a death event (no gunId).
     */
    public static boolean matchesDeathConditions(Player player, LivingEntity killed,
                                                  Entity otherEntity, AchievementDefinitions.AchievementDef def) {
        AchievementDefinitions.AchievementConditions c = def.conditions();
        if (c == null) return true;

        // Check equipped curios
        if (c.equippedCurios() != null) {
            for (String curio : c.equippedCurios()) {
                if (!LivingDeathEventHandler.hasEquipped(player, curio)) return false;
            }
        }

        // Check killer entity type
        if (c.killer() != null && otherEntity != null) {
            String killerKey = BuiltInRegistries.ENTITY_TYPE.getKey(otherEntity.getType()).toString();
            if (!c.killer().equals(killerKey)) return false;
        }

        // Check attributes
        if (c.attributes() != null) {
            for (AchievementDefinitions.AttributeCondition ac : c.attributes()) {
                Attribute attr = AttributeHelper.resolveAttribute(ac.attribute());
                if (attr == null) return false;
                double value = player.getAttributeValue(attr);
                if (!compare(value, ac.comparator(), ac.value())) return false;
            }
        }

        return true;
    }

    private static boolean compare(double current, String comparator, double expected) {
        return switch (comparator) {
            case "gt" -> current > expected;
            case "gte" -> current >= expected;
            case "lt" -> current < expected;
            case "lte" -> current <= expected;
            case "eq" -> Double.compare(current, expected) == 0;
            case "ne" -> Double.compare(current, expected) != 0;
            default -> current >= expected;
        };
    }
}
