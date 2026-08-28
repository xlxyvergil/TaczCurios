package com.xlxyvergil.tcc.effect;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.AttributeHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

/**
 * 镀层液压准心爆头Buff - 爆头触发，暴击几率提升（不叠加）
 */
public class GildedHydraulicCrosshairEffect extends MobEffect {
    public GildedHydraulicCrosshairEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFFBB66);
        double perLevelValue = TaczCuriosConfig.COMMON.gildedHydraulicCrosshairBaseCritChance.get();
        this.addAttributeModifier(AttributeHelper.CRIT_CHANCE,
            "2a51f151-cd51-4617-8192-af104611e7aa", perLevelValue, AttributeModifier.Operation.MULTIPLY_BASE);
    }

}
