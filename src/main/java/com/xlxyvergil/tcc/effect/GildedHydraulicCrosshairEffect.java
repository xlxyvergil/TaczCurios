package com.xlxyvergil.tcc.effect;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.AttributeHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

/**
 * 镀层液压准心Buff - 爆头/爆头击杀触发，暴击几率提升（可叠加5层）
 */
public class GildedHydraulicCrosshairEffect extends MobEffect {
    public GildedHydraulicCrosshairEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFFBB66);
        double perLevelValue = TaczCuriosConfig.COMMON.gildedHydraulicCrosshairCritChancePerLevel.get();
        this.addAttributeModifier(AttributeHelper.CRIT_CHANCE,
            "c1d2e3f4-6005-4000-8000-000000000001", perLevelValue, AttributeModifier.Operation.MULTIPLY_BASE);
    }

    @Override
    public double getAttributeModifierValue(int amplifier, AttributeModifier modifier) {
        return (amplifier + 1) * TaczCuriosConfig.COMMON.gildedHydraulicCrosshairCritChancePerLevel.get();
    }
}
