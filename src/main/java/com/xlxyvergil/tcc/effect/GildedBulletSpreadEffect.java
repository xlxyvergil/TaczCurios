package com.xlxyvergil.tcc.effect;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.registries.TccItems;
import com.xlxyvergil.tcc.util.AttributeHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import net.minecraft.resources.ResourceLocation;
import com.xlxyvergil.tcc.TaczCurios;
public class GildedBulletSpreadEffect extends MobEffect {
    public GildedBulletSpreadEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x44AAFF);
        this.addAttributeModifier(AttributeHelper.BULLET_COUNT,
            ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "gilded_bullet_spread_effect_78e5804f_52ee"),
            AttributeModifier.Operation.ADD_MULTIPLIED_BASE,
            amplifier -> (amplifier + 1) * TaczCuriosConfig.getOrDefault(TaczCuriosConfig.COMMON.gildedBulletSpreadBulletCountPerLevel));
        AttributeHelper.registerSourceItem(ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "gilded_bullet_spread_effect_78e5804f_52ee"), TccItems.GILDED_BULLET_SPREAD);
    }

}
