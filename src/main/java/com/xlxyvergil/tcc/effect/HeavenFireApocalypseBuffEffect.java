package com.xlxyvergil.tcc.effect;

import com.xlxyvergil.taa.attribute.EntityAttributeRegistry;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.registries.TccItems;
import com.xlxyvergil.tcc.util.AttributeHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.UUID;

public class HeavenFireApocalypseBuffEffect extends MobEffect {

    public HeavenFireApocalypseBuffEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFF5555);
        double perLevelValue = TaczCuriosConfig.COMMON.heavenFireApocalypseNearbyPlayerDamageBoost.get();
        this.addAttributeModifier(
            EntityAttributeRegistry.BULLET_GUNDAMAGE.get(),
            "ba764054-9ad7-493e-b1bd-a6def116012d",
            perLevelValue,
            AttributeModifier.Operation.MULTIPLY_BASE
        );
        AttributeHelper.registerSourceItem(UUID.fromString("ba764054-9ad7-493e-b1bd-a6def116012d"), TccItems.HEAVEN_FIRE_APOCALYPSE);
    }

    @Override
    public double getAttributeModifierValue(int amplifier, AttributeModifier modifier) {
        return (amplifier + 1) * TaczCuriosConfig.COMMON.heavenFireApocalypseNearbyPlayerDamageBoost.get();
    }
}
