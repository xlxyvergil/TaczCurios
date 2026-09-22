package com.xlxyvergil.tcc.effect;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.registries.TccItems;
import com.xlxyvergil.tcc.util.AttributeHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import net.minecraft.resources.ResourceLocation;
import com.xlxyvergil.tcc.TaczCurios;
public class HydraulicCrosshairEffect extends MobEffect {
    public HydraulicCrosshairEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFF9944);
        this.addAttributeModifier(AttributeHelper.CRIT_CHANCE,
            ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "hydraulic_crosshair_effect_f32f9e0d_b349"),
            AttributeModifier.Operation.ADD_MULTIPLIED_BASE,
            amplifier -> (amplifier + 1) * TaczCuriosConfig.getOrDefault(TaczCuriosConfig.COMMON.hydraulicCrosshairBaseCritChance));
        AttributeHelper.registerSourceItem(ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "hydraulic_crosshair_effect_f32f9e0d_b349"), TccItems.HYDRAULIC_CROSSHAIR);
    }
}
