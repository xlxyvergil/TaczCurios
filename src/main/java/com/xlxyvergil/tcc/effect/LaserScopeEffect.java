package com.xlxyvergil.tcc.effect;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.AttributeHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

/**
 * 雷射瞄具Buff - 爆头触发，暴击几率提升（不叠加）
 */
public class LaserScopeEffect extends MobEffect {
    public LaserScopeEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFF8844);
        double baseValue = TaczCuriosConfig.COMMON.laserScopeBaseCritChance.get();
        this.addAttributeModifier(AttributeHelper.CRIT_CHANCE,
            "c1d2e3f4-6003-4000-8000-000000000001", baseValue, AttributeModifier.Operation.MULTIPLY_BASE);
    }

    @Override
    public double getAttributeModifierValue(int amplifier, AttributeModifier modifier) {
        return (amplifier + 1) * TaczCuriosConfig.COMMON.laserScopeBaseCritChance.get();
    }
}
