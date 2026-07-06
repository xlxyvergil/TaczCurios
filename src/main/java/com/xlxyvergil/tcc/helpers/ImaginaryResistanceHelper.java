package com.xlxyvergil.tcc.helpers;

import com.xlxyvergil.tcc.evolution.EvolutionRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class ImaginaryResistanceHelper {
    
    public static final String IMAGINARY_RESISTANCE_ATTRIBUTE = "tcc:imaginary_damage_resistance";
    public static final String IMAGINARY_RESISTANCE_NBT_KEY = "Progress_tcc_imaginary_damage_resistance";
    
    private ImaginaryResistanceHelper() {
    }
    
    public static double getExtraResistanceFromProgress(CompoundTag tag) {
        if (tag == null) {
            return 0.0;
        }
        return tag.getDouble(IMAGINARY_RESISTANCE_NBT_KEY);
    }
    
    public static double getMaxExtraResistanceFromProgressRules(String itemId) {
        double cap = 0.0;
        for (EvolutionRegistry.Rule rule : EvolutionRegistry.getRulesByTypeAndItemOrEmpty(EvolutionRegistry.RuleType.ATTRIBUTE, itemId)) {
            EvolutionRegistry.Progress progress = rule.progress;
            if (progress == null) {
                continue;
            }
            if (!IMAGINARY_RESISTANCE_ATTRIBUTE.equals(progress.attribute)) {
                continue;
            }
            if (progress.operation != AttributeModifier.Operation.ADDITION) {
                continue;
            }
            cap = Math.max(cap, progress.cap);
        }
        return cap;
    }
    
    public static double calculateTotalResistance(int baseResistance, CompoundTag tag) {
        return baseResistance + getExtraResistanceFromProgress(tag);
    }
}