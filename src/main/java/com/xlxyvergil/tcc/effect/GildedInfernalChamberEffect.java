package com.xlxyvergil.tcc.effect;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.registries.TccItems;
import com.xlxyvergil.tcc.util.AttributeHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import net.minecraft.resources.ResourceLocation;
import com.xlxyvergil.tcc.TaczCurios;
public class GildedInfernalChamberEffect extends MobEffect {
    public GildedInfernalChamberEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x44AAFF);
        this.addAttributeModifier(AttributeHelper.BULLET_COUNT,
            ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "gilded_infernal_chamber_effect_d64182ae_7823"),
            AttributeModifier.Operation.ADD_MULTIPLIED_BASE,
            amplifier -> (amplifier + 1) * TaczCuriosConfig.getOrDefault(TaczCuriosConfig.COMMON.gildedInfernalChamberBulletCountPerLevel));
        AttributeHelper.registerSourceItem(ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "gilded_infernal_chamber_effect_d64182ae_7823"), TccItems.GILDED_INFERNAL_CHAMBER);
    }

}
