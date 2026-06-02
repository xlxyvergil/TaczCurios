package com.xlxyvergil.tcc.evolution;

import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.util.EntityConditionHelper;
import com.xlxyvergil.tcc.util.EvolutionNbtKeys;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class LivingDeathEvolutionRuleMatcher {
    private LivingDeathEvolutionRuleMatcher() {
    }

    public static boolean matches(Player player, ItemStack trackedStack, EvolutionRegistry.Rule rule) {
        CompoundTag tag = trackedStack.getOrCreateTag();

        for (String requiredCurio : rule.requirements.equippedCurios) {
            if (!LivingDeathEventHandler.hasEquipped(player, requiredCurio)) {
                return false;
            }
        }

        CompoundTag killCounts = tag.getCompound(EvolutionNbtKeys.KILL_COUNTS);
        for (EvolutionRegistry.KillRequirement req : rule.requirements.kills) {
            String matchKey = EntityConditionHelper.getMatchKey(req.entity.key, req.entity.nbt);
            if (killCounts.getInt(matchKey) < req.count) {
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
