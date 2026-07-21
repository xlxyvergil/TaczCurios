package com.xlxyvergil.tcc.evolution;

import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.util.EntityConditionHelper;
import com.xlxyvergil.tcc.util.GunTypeChecker;
import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.stats.Stats;

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

        // Check required effects（实时检测玩家身上的 Buff）
        if (c.requiredEffects() != null) {
            for (String effectId : c.requiredEffects()) {
                ResourceLocation effectRl = ResourceLocation.tryParse(effectId);
                if (effectRl == null) return false;
                var effect = net.minecraftforge.registries.ForgeRegistries.MOB_EFFECTS.getValue(effectRl);
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

        // Check health max (player health must be <= healthMax)
        if (c.healthMax() != null && player.getHealth() > c.healthMax()) return false;

        return true;
    }

    /**
     * Check conditions for a kill event (no gunId).
     */
    public static boolean matchesDeathConditions(Player player, LivingEntity killed,
                                                  Entity otherEntity, AchievementDefinitions.AchievementDef def) {
        AchievementDefinitions.AchievementConditions c = def.conditions();
        if (c == null) return true;

        // Check equipped curios
        if (c.equippedCurios() != null) {
            for (String curio : c.equippedCurios()) {
                boolean has = LivingDeathEventHandler.hasEquipped(player, curio);
                LOGGER.info("[TCC-DEBUG] matchesDeathConditions({}): equippedCurio={}, has={}", def.id(), curio, has);
                if (!has) return false;
            }
        }

        // Check killer entity type (who killed the player)
        if (c.killer() != null) {
            if (otherEntity == null) { LOGGER.info("[TCC-DEBUG] matchesDeathConditions({}): FAIL killer=null", def.id()); return false; }
            String killerKey = BuiltInRegistries.ENTITY_TYPE.getKey(otherEntity.getType()).toString();
            if (!c.killer().equals(killerKey)) { LOGGER.info("[TCC-DEBUG] matchesDeathConditions({}): FAIL killer expected={} actual={}", def.id(), c.killer(), killerKey); return false; }
        }

        // Check killed entity (what the player killed, for melee kills)
        if (c.kills() != null && !c.kills().isEmpty() && killed != null) {
            String killedKey = BuiltInRegistries.ENTITY_TYPE.getKey(killed.getType()).toString();
            boolean matched = false;
            for (AchievementDefinitions.KillCondition kc : c.kills()) {
                if ("*".equals(kc.entity()) || killedKey.equals(kc.entity())) {
                    matched = true;
                    break;
                }
            }
            if (!matched) { LOGGER.info("[TCC-DEBUG] matchesDeathConditions({}): FAIL kills expected={} actual={}", def.id(), c.kills(), killedKey); return false; }
        }

        // Check stat threshold (for melee kill achievements that also require stat milestones)
        if (c.stat() != null && player instanceof ServerPlayer sp) {
            ResourceLocation statKey = ResourceLocation.tryParse(c.stat());
            if (statKey == null) { LOGGER.info("[TCC-DEBUG] matchesDeathConditions({}): FAIL stat parse={}", def.id(), c.stat()); return false; }
            ResourceLocation canonicalId = BuiltInRegistries.CUSTOM_STAT.get(statKey);
            if (canonicalId == null) { LOGGER.info("[TCC-DEBUG] matchesDeathConditions({}): FAIL stat not found in CUSTOM_STAT={}", def.id(), statKey); return false; }
            var stat = Stats.CUSTOM.get(canonicalId);
            if (stat == null) { LOGGER.info("[TCC-DEBUG] matchesDeathConditions({}): FAIL Stats.CUSTOM.get() returned null", def.id()); return false; }
            int actualValue = sp.getStats().getValue(stat);
            if (actualValue < c.statThreshold()) { LOGGER.info("[TCC-DEBUG] matchesDeathConditions({}): FAIL stat={} required={} actual={}", def.id(), c.stat(), c.statThreshold(), actualValue); return false; }
            LOGGER.info("[TCC-DEBUG] matchesDeathConditions({}): PASS stat={} required={} actual={}", def.id(), c.stat(), c.statThreshold(), actualValue);
        }

        // Check extra stat thresholds (for achievements requiring multiple stat checks)
        if (c.extraStats() != null && player instanceof ServerPlayer sp2) {
            for (AchievementDefinitions.StatCondition sc : c.extraStats()) {
                ResourceLocation key = ResourceLocation.tryParse(sc.stat());
                if (key == null) return false;
                ResourceLocation canonicalId = BuiltInRegistries.CUSTOM_STAT.get(key);
                if (canonicalId == null) return false;
                var s = Stats.CUSTOM.get(canonicalId);
                if (s == null) return false;
                if (sp2.getStats().getValue(s) < sc.statThreshold()) return false;
            }
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

        // Check health max (player health must be <= healthMax)
        if (c.healthMax() != null && player.getHealth() > c.healthMax()) return false;

        return true;
    }

    /**
     * Check conditions for a stat_polling / biome_visit event (no kill/death context).
     * Checks equipped curios, attribute thresholds, and dimension.
     */
    public static boolean matchesStatBiomeConditions(Player player, AchievementDefinitions.AchievementDef def) {
        AchievementDefinitions.AchievementConditions c = def.conditions();
        if (c == null) return true;

        // Check equipped curios
        if (c.equippedCurios() != null) {
            for (String curio : c.equippedCurios()) {
                if (!LivingDeathEventHandler.hasEquipped(player, curio)) return false;
            }
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

        // Check dimension
        if (c.dimension() != null) {
            ResourceLocation rl = ResourceLocation.tryParse(c.dimension());
            if (rl == null) return false;
            ResourceKey<Level> target = ResourceKey.create(Registries.DIMENSION, rl);
            if (player.level().dimension() != target) return false;
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
