package com.xlxyvergil.tcc.evolution;

import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.util.EntityConditionHelper;
import com.xlxyvergil.tcc.util.GunTypeChecker;
import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Optional;

/**
 * Matches achievement conditions against in-game events.
 * Used by all trigger handlers to check if a player meets
 * the requirements for advancing an achievement's progress.
 */
public final class AchievementConditionMatcher {
    private static final Logger LOGGER = LogUtils.getLogger();
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
        if (c.holdingGunTypes() != null && !c.holdingGunTypes().isEmpty()) {
            if (gunId == null) return false;
            if (!GunTypeChecker.matchesGunTypes(gunId, c.holdingGunTypes())) return false;
        }

        // Check min distance
        if (c.minDistance() != null && killed != null) {
            double min = c.minDistance();
            if (player.distanceToSqr(killed) < min * min) return false;
        }

        // Check kill entity type (with NBT)
        if (c.kills() != null && !c.kills().isEmpty() && killed != null) {
            if (findMatchingKillCondition(killed, c).isEmpty()) return false;
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
        if (c.killer() != null) {
            if (otherEntity == null) return false;
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
            default -> {
                LOGGER.warn("Unknown attribute comparator '{}' — returning false", comparator);
                yield false;
            }
        };
    }

    /**
     * 在成就的击杀条件列表中查找匹配的 {@link KillCondition}。
     * 同时检查实体类型和 NBT 标签。
     * @return 匹配到的 KillCondition；若未匹配则返回 {@link Optional#empty()}
     */
    public static Optional<AchievementDefinitions.KillCondition> findMatchingKillCondition(
            LivingEntity killed, AchievementDefinitions.AchievementConditions conditions) {
        if (killed == null || conditions == null || conditions.kills() == null || conditions.kills().isEmpty()) {
            return Optional.empty();
        }
        String killedKey = BuiltInRegistries.ENTITY_TYPE.getKey(killed.getType()).toString();
        for (AchievementDefinitions.KillCondition kc : conditions.kills()) {
            if (!"*".equals(kc.entity()) && !killedKey.equals(kc.entity())) {
                continue;
            }
            if (!EntityConditionHelper.matchesNbtFilters(killed, kc.nbt())) {
                continue;
            }
            return Optional.of(kc);
        }
        return Optional.empty();
    }
}
