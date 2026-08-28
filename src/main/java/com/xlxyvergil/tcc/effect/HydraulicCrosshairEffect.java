package com.xlxyvergil.tcc.effect;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.AttributeHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class HydraulicCrosshairEffect extends MobEffect {
    public HydraulicCrosshairEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFF9944);
        double baseValue = TaczCuriosConfig.COMMON.hydraulicCrosshairBaseCritChance.get();
        this.addAttributeModifier(AttributeHelper.CRIT_CHANCE,
            "f32f9e0d-9078-4da4-985b-c3ac3636b349", baseValue, AttributeModifier.Operation.MULTIPLY_BASE);
    }

    @Override
    public double getAttributeModifierValue(int amplifier, AttributeModifier modifier) {
        return (amplifier + 1) * TaczCuriosConfig.COMMON.hydraulicCrosshairBaseCritChance.get();
    }
}
