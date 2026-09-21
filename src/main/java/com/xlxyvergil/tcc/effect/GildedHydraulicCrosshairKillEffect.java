package com.xlxyvergil.tcc.effect;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.registries.TccItems;
import com.xlxyvergil.tcc.util.AttributeHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import net.minecraft.resources.ResourceLocation;
import com.xlxyvergil.tcc.TaczCurios;
public class GildedHydraulicCrosshairKillEffect extends MobEffect {
    public GildedHydraulicCrosshairKillEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFFCC44);
        double perLevelValue = TaczCuriosConfig.COMMON.gildedHydraulicCrosshairCritChancePerLevel.get();
        this.addAttributeModifier(AttributeHelper.CRIT_CHANCE,
            ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "gilded_hydraulic_crosshair_kill_effect_410756c5_75a2"), perLevelValue, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        AttributeHelper.registerSourceItem(ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "gilded_hydraulic_crosshair_kill_effect_410756c5_75a2"), TccItems.GILDED_HYDRAULIC_CROSSHAIR);
    }

}
