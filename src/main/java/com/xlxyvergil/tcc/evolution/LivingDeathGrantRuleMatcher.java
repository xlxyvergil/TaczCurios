package com.xlxyvergil.tcc.evolution;

import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.util.EntityConditionHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;

public final class LivingDeathGrantRuleMatcher {
    private LivingDeathGrantRuleMatcher() {
    }

    public static boolean matches(Player player, LivingEntity killed, Entity otherEntity, DamageSource source, EvolutionRegistry.Rule rule) {
        return matches(player, killed, otherEntity, source, rule, false);
    }

    public static boolean matches(Player player, LivingEntity killed, Entity otherEntity, DamageSource source, EvolutionRegistry.Rule rule, boolean ignoreEnabled) {
        if (rule.type != EvolutionRegistry.RuleType.GRANT || (!ignoreEnabled && !rule.enabled)) {
            return false;
        }

        if (!matchesRequirements(player, killed, otherEntity, rule)) {
            return false;
        }

        if (!passesOncePerPlayerTag(player, rule)) {
            return false;
        }

        return LivingDeathEventHandler.matchesDamageSource(rule, source);
    }

    private static boolean matchesKiller(EvolutionRegistry.Rule rule, Entity killer) {
        EvolutionRegistry.EntityRef cond = rule.killer;
        if (cond == null) {
            return true;
        }
        if (killer == null) {
            return false;
        }
        String killerId = BuiltInRegistries.ENTITY_TYPE.getKey(killer.getType()).toString();
        if (!cond.key.equals(killerId)) {
            return false;
        }
        if (killer instanceof LivingEntity livingKiller) {
            return EntityConditionHelper.matchesNbtFilters(livingKiller, cond.nbt);
        }
        return cond.nbt.isEmpty();
    }

    private static boolean matchesRequirements(Player player, LivingEntity killed, Entity otherEntity, EvolutionRegistry.Rule rule) {
        if (rule.grant == null) {
            return false;
        }

        if (rule.item != null && !rule.item.isBlank()) {
            if (!LivingDeathEventHandler.hasEquipped(player, rule.item)) {
                return false;
            }
        }

        for (String requiredCurio : rule.requirements.equippedCurios) {
            if (!LivingDeathEventHandler.hasEquipped(player, requiredCurio)) {
                return false;
            }
        }

        if (rule.playerKilled) {
            if (!matchesKiller(rule, otherEntity)) {
                return false;
            }
            if (!rule.requirements.kills.isEmpty()) {
                return false;
            }
        } else {
            if (!matchesKilled(rule, killed)) {
                return false;
            }
        }

        for (EvolutionRegistry.AttributeRequirement req : rule.requirements.attributes) {
            Attribute attr = AttributeHelper.resolveAttribute(req.attribute);
            if (attr == null) {
                return false;
            }
            double value = player.getAttributeValue(attr);
            if (!compare(value, req.comparator, req.value)) {
                return false;
            }
        }

        return true;
    }

    private static boolean matchesKilled(EvolutionRegistry.Rule rule, LivingEntity killed) {
        if (rule.requirements.kills.isEmpty()) {
            return true;
        }
        if (killed == null) {
            return false;
        }
        String killedKey = BuiltInRegistries.ENTITY_TYPE.getKey(killed.getType()).toString();
        for (EvolutionRegistry.KillRequirement req : rule.requirements.kills) {
            if (req == null || req.entity == null) {
                continue;
            }
            if (!killedKey.equals(req.entity.key)) {
                continue;
            }
            if (EntityConditionHelper.matchesNbtFilters(killed, req.entity.nbt)) {
                return true;
            }
        }
        return false;
    }

    private static boolean passesOncePerPlayerTag(Player player, EvolutionRegistry.Rule rule) {
        String key = oncePerPlayerKey(rule);
        return key == null || !player.getPersistentData().getBoolean(key);
    }

    static String oncePerPlayerKey(EvolutionRegistry.Rule rule) {
        if (rule == null || rule.grant == null) {
            return null;
        }
        return rule.grant.oncePerPlayer ? rule.ruleId : null;
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
