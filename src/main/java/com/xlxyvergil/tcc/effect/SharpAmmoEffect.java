package com.xlxyvergil.tcc.effect;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.AttributeHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

/**
 * 尖锐子弹Buff - 击杀触发，暴击伤害提升（不叠加）
 */
public class SharpAmmoEffect extends MobEffect {
    public SharpAmmoEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFF6644);
        double perLevelValue = TaczCuriosConfig.COMMON.sharpAmmoCritDamagePerLevel.get();
        this.addAttributeModifier(AttributeHelper.CRIT_DAMAGE,
            "c1d2e3f4-6103-4000-8000-000000000001", perLevelValue, AttributeModifier.Operation.MULTIPLY_BASE);
    }

    @Override
    public double getAttributeModifierValue(int amplifier, AttributeModifier modifier) {
        return (amplifier + 1) * TaczCuriosConfig.COMMON.sharpAmmoCritDamagePerLevel.get();
    }
}
