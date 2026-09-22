package com.xlxyvergil.tcc.effect;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.registries.TccItems;
import com.xlxyvergil.tcc.util.AttributeHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import net.minecraft.resources.ResourceLocation;
import com.xlxyvergil.tcc.TaczCurios;
public class GildedHydraulicCrosshairEffect extends MobEffect {
    public GildedHydraulicCrosshairEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFFBB66);
        this.addAttributeModifier(AttributeHelper.CRIT_CHANCE,
            ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "gilded_hydraulic_crosshair_effect_2a51f151_e7aa"),
            AttributeModifier.Operation.ADD_MULTIPLIED_BASE,
            amplifier -> (amplifier + 1) * TaczCuriosConfig.getOrDefault(TaczCuriosConfig.COMMON.gildedHydraulicCrosshairBaseCritChance));
        AttributeHelper.registerSourceItem(ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "gilded_hydraulic_crosshair_effect_2a51f151_e7aa"), TccItems.GILDED_HYDRAULIC_CROSSHAIR);
    }

}
