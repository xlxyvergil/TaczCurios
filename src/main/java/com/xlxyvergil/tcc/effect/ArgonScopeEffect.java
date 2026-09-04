package com.xlxyvergil.tcc.effect;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.registries.TccItems;
import com.xlxyvergil.tcc.util.AttributeHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.UUID;

public class ArgonScopeEffect extends MobEffect {
    public ArgonScopeEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFFAA44);
        double baseValue = TaczCuriosConfig.COMMON.argonScopeBaseCritChance.get();
        this.addAttributeModifier(AttributeHelper.CRIT_CHANCE,
            "a46002d7-028e-4b39-b90c-c7f949aee441", baseValue, AttributeModifier.Operation.MULTIPLY_BASE);
        AttributeHelper.registerSourceItem(UUID.fromString("a46002d7-028e-4b39-b90c-c7f949aee441"), TccItems.ARGON_SCOPE);
    }

    @Override
    public double getAttributeModifierValue(int amplifier, AttributeModifier modifier) {
        return (amplifier + 1) * TaczCuriosConfig.COMMON.argonScopeBaseCritChance.get();
    }
}
