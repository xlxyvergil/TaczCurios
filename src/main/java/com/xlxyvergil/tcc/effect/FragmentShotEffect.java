package com.xlxyvergil.tcc.effect;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.registries.TccItems;
import com.xlxyvergil.tcc.util.AttributeHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.UUID;

public class FragmentShotEffect extends MobEffect {
    public FragmentShotEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFF6644);
        double baseValue = TaczCuriosConfig.COMMON.fragmentShotBaseCritDamage.get();
        this.addAttributeModifier(AttributeHelper.CRIT_DAMAGE,
            "05ef1a76-86f4-492f-adc0-3d78e54e52fd", baseValue, AttributeModifier.Operation.MULTIPLY_BASE);
        AttributeHelper.registerSourceItem(UUID.fromString("05ef1a76-86f4-492f-adc0-3d78e54e52fd"), TccItems.FRAGMENT_SHOT);
    }

    @Override
    public double getAttributeModifierValue(int amplifier, AttributeModifier modifier) {
        return (amplifier + 1) * TaczCuriosConfig.COMMON.fragmentShotBaseCritDamage.get();
    }
}
