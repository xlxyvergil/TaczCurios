package com.xlxyvergil.tcc.effect;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.AttributeHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

/**
 * 尖刃弹头Buff - 击杀触发，暴击伤害提升（不叠加）
 */
public class SharpBulletEffect extends MobEffect {
    public SharpBulletEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFF6644);
        double baseValue = TaczCuriosConfig.COMMON.sharpBulletBaseCritDamage.get();
        this.addAttributeModifier(AttributeHelper.CRIT_DAMAGE,
            "7de1fac5-2f2f-4462-b6b2-1ab5d1b88194", baseValue, AttributeModifier.Operation.MULTIPLY_BASE);
    }

    @Override
    public double getAttributeModifierValue(int amplifier, AttributeModifier modifier) {
        return (amplifier + 1) * TaczCuriosConfig.COMMON.sharpBulletBaseCritDamage.get();
    }
}
