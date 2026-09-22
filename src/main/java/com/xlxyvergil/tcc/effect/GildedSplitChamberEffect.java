package com.xlxyvergil.tcc.effect;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.registries.TccItems;
import com.xlxyvergil.tcc.util.AttributeHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import net.minecraft.resources.ResourceLocation;
import com.xlxyvergil.tcc.TaczCurios;
public class GildedSplitChamberEffect extends MobEffect {
    public GildedSplitChamberEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x44AAFF);
        this.addAttributeModifier(AttributeHelper.BULLET_COUNT,
            ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "gilded_split_chamber_effect_0f372759_8a01"),
            AttributeModifier.Operation.ADD_MULTIPLIED_BASE,
            amplifier -> (amplifier + 1) * TaczCuriosConfig.getOrDefault(TaczCuriosConfig.COMMON.gildedSplitChamberBulletCountPerLevel));
        AttributeHelper.registerSourceItem(ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "gilded_split_chamber_effect_0f372759_8a01"), TccItems.GILDED_SPLIT_CHAMBER);
    }

}
