package com.xlxyvergil.tcc.evolution;

import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.util.EntityConditionHelper;
import com.xlxyvergil.tcc.util.GunTypeChecker;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.ForgeRegistries;

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

        if (!rule.requirements.requiredEffects.isEmpty()) {
            for (String effectId : rule.requirements.requiredEffects) {
                MobEffect effect;
                try {
                    effect = ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(effectId));
                } catch (Exception e) {
                    return false;
                }
                if (effect == null || !player.hasEffect(effect)) {
                    return false;
                }
            }
        }

        if (!rule.requirements.holdingGunTypes.isEmpty()) {
            if (!GunTypeChecker.isHoldingConfiguredGunTypes(player, rule.requirements.holdingGunTypes)) {
                return false;
            }
        }

        if (rule.requirements.minDistance != null && !rule.playerKilled) {
            if (killed == null) {
                return false;
            }
            double min = rule.requirements.minDistance;
            if (player.distanceToSqr(killed) < min * min) {
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
            if (!"*".equals(req.entity.key) && !killedKey.equals(req.entity.key)) {
                continue;
            }
            if (EntityConditionHelper.matchesNbtFilters(killed, req.entity.nbt)) {
                return true;
            }
        }
        return false;
    }

    static boolean passesOncePerPlayerTag(Player player, EvolutionRegistry.Rule rule) {
        String key = oncePerPlayerKey(rule);
        if (key == null) {
            return true;
        }
        // Quick check: persistent data (in-memory, same tick)
        if (player.getPersistentData().getBoolean(key)) {
            return false;
        }
        // Reliable check: SavedData (persists across sessions)
        GrantHistoryData history = GrantHistoryData.get(player.getServer());
        if (history == null) {
            return true;
        }
        return !history.hasReceived(rule.ruleId, player.getUUID());
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
