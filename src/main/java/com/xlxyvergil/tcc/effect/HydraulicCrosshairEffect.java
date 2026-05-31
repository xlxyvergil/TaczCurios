package com.xlxyvergil.tcc.effect;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.AttributeHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

/**
 * 液压准心Buff - 爆头触发，暴击几率提升（不叠加）
 */
public class HydraulicCrosshairEffect extends MobEffect {
    public HydraulicCrosshairEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFF9944);
        double perLevelValue = TaczCuriosConfig.COMMON.hydraulicCrosshairCritChancePerLevel.get();
        this.addAttributeModifier(AttributeHelper.CRIT_CHANCE,
            "c1d2e3f4-6004-4000-8000-000000000001", perLevelValue, AttributeModifier.Operation.MULTIPLY_BASE);
    }

    @Override
    public double getAttributeModifierValue(int amplifier, AttributeModifier modifier) {
        return (amplifier + 1) * TaczCuriosConfig.COMMON.hydraulicCrosshairCritChancePerLevel.get();
    }
}
