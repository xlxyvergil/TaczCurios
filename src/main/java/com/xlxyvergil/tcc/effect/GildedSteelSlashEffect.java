package com.xlxyvergil.tcc.effect;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.registries.TccItems;
import com.xlxyvergil.tcc.util.AttributeHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import net.minecraft.resources.ResourceLocation;
import com.xlxyvergil.tcc.TaczCurios;
public class GildedSteelSlashEffect extends MobEffect {
    public GildedSteelSlashEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFF8866);
        this.addAttributeModifier(AttributeHelper.CRIT_DAMAGE,
            ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "gilded_steel_slash_effect_f7508ebd_7364"),
            AttributeModifier.Operation.ADD_MULTIPLIED_BASE,
            amplifier -> (amplifier + 1) * TaczCuriosConfig.getOrDefault(TaczCuriosConfig.COMMON.gildedSteelSlashCritDamagePerLevel));
        AttributeHelper.registerSourceItem(ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "gilded_steel_slash_effect_f7508ebd_7364"), TccItems.GILDED_STEEL_SLASH);
    }

}
