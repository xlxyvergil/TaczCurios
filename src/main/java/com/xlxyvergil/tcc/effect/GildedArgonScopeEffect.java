package com.xlxyvergil.tcc.effect;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.AttributeHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class GildedArgonScopeEffect extends MobEffect {
    public GildedArgonScopeEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFFCC66);
        double perLevelValue = TaczCuriosConfig.COMMON.gildedArgonScopeBaseCritChance.get();
        this.addAttributeModifier(AttributeHelper.CRIT_CHANCE,
            "cf77490e1-6c8f-41b6-b363-3faa0a08474c", perLevelValue, AttributeModifier.Operation.MULTIPLY_BASE);
    }

}
