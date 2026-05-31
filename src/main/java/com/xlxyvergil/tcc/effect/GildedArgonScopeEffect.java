package com.xlxyvergil.tcc.effect;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.AttributeHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

/**
 * 镀层氩晶瞄具Buff - 爆头/爆头击杀触发，暴击几率提升（可叠加5层）
 */
public class GildedArgonScopeEffect extends MobEffect {
    public GildedArgonScopeEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFFCC66);
        double perLevelValue = TaczCuriosConfig.COMMON.gildedArgonScopeCritChancePerLevel.get();
        this.addAttributeModifier(AttributeHelper.CRIT_CHANCE,
            "c1d2e3f4-6002-4000-8000-000000000001", perLevelValue, AttributeModifier.Operation.MULTIPLY_BASE);
    }

    @Override
    public double getAttributeModifierValue(int amplifier, AttributeModifier modifier) {
        return (amplifier + 1) * TaczCuriosConfig.COMMON.gildedArgonScopeCritChancePerLevel.get();
    }
}
