package com.xlxyvergil.tcc.evolution;

import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.util.EntityConditionHelper;
import com.xlxyvergil.tcc.util.EvolutionNbtKeys;
import com.xlxyvergil.tcc.util.GunTypeChecker;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public final class LivingDeathEvolutionRuleMatcher {
    private LivingDeathEvolutionRuleMatcher() {
    }

    public static boolean matches(Player player, ItemStack trackedStack, LivingEntity killed, EvolutionRegistry.Rule rule) {
        CompoundTag tag = trackedStack.getOrCreateTag();

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

        if (rule.requirements.minDistance != null) {
            if (killed == null) {
                return false;
            }
            double min = rule.requirements.minDistance;
            if (player.distanceToSqr(killed) < min * min) {
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
