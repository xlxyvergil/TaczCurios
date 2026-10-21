package com.xlxyvergil.tcc.effect;

import com.xlxyvergil.tcc.registries.TccItems;
import com.xlxyvergil.tcc.util.AttributeHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import net.minecraft.resources.ResourceLocation;
import com.xlxyvergil.tcc.TaczCurios;
public class GildedInfernalChamberEffect extends MobEffect {
    /** 每级数值。注册期读不到配置，所以写死在常量里；tooltip 复用同一个常量。 */
    public static final double VALUE_PER_LEVEL = 0.025D;

    public GildedInfernalChamberEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x44AAFF);
        this.addAttributeModifier(AttributeHelper.BULLET_COUNT,
            ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "gilded_infernal_chamber_effect_d64182ae_7823"),
            AttributeModifier.Operation.ADD_MULTIPLIED_BASE,
            amplifier -> (amplifier + 1) * VALUE_PER_LEVEL);
    }

}
