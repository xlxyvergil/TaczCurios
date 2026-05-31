package com.xlxyvergil.tcc.effect;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.AttributeHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

/**
 * 镀层斩铁Buff - 击杀触发，暴击伤害提升（可叠加4层）
 */
public class GildedSteelSlashEffect extends MobEffect {
    public GildedSteelSlashEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFF8866);
        double perLevelValue = TaczCuriosConfig.COMMON.gildedSteelSlashCritDamagePerLevel.get();
        this.addAttributeModifier(AttributeHelper.CRIT_DAMAGE,
            "c1d2e3f4-6104-4000-8000-000000000001", perLevelValue, AttributeModifier.Operation.MULTIPLY_BASE);
    }

    @Override
    public double getAttributeModifierValue(int amplifier, AttributeModifier modifier) {
        return (amplifier + 1) * TaczCuriosConfig.COMMON.gildedSteelSlashCritDamagePerLevel.get();
    }
}
