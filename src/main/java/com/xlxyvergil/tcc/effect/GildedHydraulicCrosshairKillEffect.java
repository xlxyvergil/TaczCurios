package com.xlxyvergil.tcc.effect;

import java.util.UUID;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.registries.TccItems;
import com.xlxyvergil.tcc.util.AttributeHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class GildedHydraulicCrosshairKillEffect extends MobEffect {
    public GildedHydraulicCrosshairKillEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFFCC44);
        double perLevelValue = TaczCuriosConfig.COMMON.gildedHydraulicCrosshairCritChancePerLevel.get();
        this.addAttributeModifier(AttributeHelper.CRIT_CHANCE,
            "410756c5-8d3b-4bcf-ab77-9bc3728175a2", perLevelValue, AttributeModifier.Operation.MULTIPLY_BASE);
        AttributeHelper.registerSourceItem(UUID.fromString("410756c5-8d3b-4bcf-ab77-9bc3728175a2"), TccItems.GILDED_HYDRAULIC_CROSSHAIR);
    }

}
