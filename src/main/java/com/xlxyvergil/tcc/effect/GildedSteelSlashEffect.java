package com.xlxyvergil.tcc.effect;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.AttributeHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class GildedSteelSlashEffect extends MobEffect {
    public GildedSteelSlashEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFF8866);
        double perLevelValue = TaczCuriosConfig.COMMON.gildedSteelSlashCritDamagePerLevel.get();
        this.addAttributeModifier(AttributeHelper.CRIT_DAMAGE,
            "f7508ebd-4a0e-461c-a9cc-97c658ad7364", perLevelValue, AttributeModifier.Operation.MULTIPLY_BASE);
    }

}
