package com.xlxyvergil.tcc.effect;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.registries.TccItems;
import com.xlxyvergil.tcc.util.AttributeHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.UUID;

public class GildedHydraulicCrosshairEffect extends MobEffect {
    public GildedHydraulicCrosshairEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFFBB66);
        double perLevelValue = TaczCuriosConfig.COMMON.gildedHydraulicCrosshairBaseCritChance.get();
        this.addAttributeModifier(AttributeHelper.CRIT_CHANCE,
            "2a51f151-cd51-4617-8192-af104611e7aa", perLevelValue, AttributeModifier.Operation.MULTIPLY_BASE);
        AttributeHelper.registerSourceItem(UUID.fromString("2a51f151-cd51-4617-8192-af104611e7aa"), TccItems.GILDED_HYDRAULIC_CROSSHAIR);
    }

}
