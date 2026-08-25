package com.xlxyvergil.tcc.helpers;

import com.xlxyvergil.tcc.attribute.TccAttributes;
import com.xlxyvergil.tcc.evolution.EvolutionRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
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

    /**
     * 读取实体实际虚数抗性并转为百分比概率（§0.2）。
     * <p>
     * 实际抗性 ÷100 = 概率；负值按 0%；100+ 封顶 100%。
     */
    public static double getResistanceProbability(LivingEntity entity) {
        if (entity == null) {
            return 0.0;
        }
        double resistance = entity.getAttributeValue(TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get());
        return Math.max(0.0, Math.min(1.0, resistance / 100.0));
    }

    /**
     * 读取实体实际虚数抗性数值（§0.2，用作数值百分比，如削甲/护甲加成）。
     * 负值按 0 处理。
     */
    public static double getResistanceValue(LivingEntity entity) {
        if (entity == null) {
            return 0.0;
        }
        double resistance = entity.getAttributeValue(TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get());
        return Math.max(0.0, resistance);
    }
}