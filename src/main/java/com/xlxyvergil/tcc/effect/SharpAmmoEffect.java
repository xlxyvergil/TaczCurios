package com.xlxyvergil.tcc.effect;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.registries.TccItems;
import com.xlxyvergil.tcc.util.AttributeHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import net.minecraft.resources.ResourceLocation;
import com.xlxyvergil.tcc.TaczCurios;
public class SharpAmmoEffect extends MobEffect {
    public SharpAmmoEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFF6644);
        this.addAttributeModifier(AttributeHelper.CRIT_DAMAGE,
            ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "sharp_ammo_effect_7ebabc7f_3673"),
            AttributeModifier.Operation.ADD_MULTIPLIED_BASE,
            amplifier -> (amplifier + 1) * TaczCuriosConfig.getOrDefault(TaczCuriosConfig.COMMON.sharpAmmoBaseCritDamage));
        AttributeHelper.registerSourceItem(ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "sharp_ammo_effect_7ebabc7f_3673"), TccItems.SHARP_AMMO);
    }
}
