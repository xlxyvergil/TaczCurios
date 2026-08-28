package com.xlxyvergil.tcc.effect;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.AttributeHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

/**
 * 氩晶瞄具Buff - 爆头触发，暴击几率提升（不叠加）
 */
public class ArgonScopeEffect extends MobEffect {
    public ArgonScopeEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFFAA44);
        double baseValue = TaczCuriosConfig.COMMON.argonScopeBaseCritChance.get();
        this.addAttributeModifier(AttributeHelper.CRIT_CHANCE,
            "a46002d7-028e-4b39-b90c-c7f949aee441", baseValue, AttributeModifier.Operation.MULTIPLY_BASE);
    }

    @Override
    public double getAttributeModifierValue(int amplifier, AttributeModifier modifier) {
        return (amplifier + 1) * TaczCuriosConfig.COMMON.argonScopeBaseCritChance.get();
    }
}
