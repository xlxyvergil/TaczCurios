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
        this.addAttributeModifier(AttributeHelper.CRIT_CHANCE,
            ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "gilded_hydraulic_crosshair_kill_effect_410756c5_75a2"),
            AttributeModifier.Operation.ADD_MULTIPLIED_BASE,
            amplifier -> (amplifier + 1) * TaczCuriosConfig.getOrDefault(TaczCuriosConfig.COMMON.gildedHydraulicCrosshairCritChancePerLevel));
        AttributeHelper.registerSourceItem(ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "gilded_hydraulic_crosshair_kill_effect_410756c5_75a2"), TccItems.GILDED_HYDRAULIC_CROSSHAIR);
    }

}
