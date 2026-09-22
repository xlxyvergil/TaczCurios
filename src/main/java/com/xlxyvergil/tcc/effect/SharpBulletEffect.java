package com.xlxyvergil.tcc.effect;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.registries.TccItems;
import com.xlxyvergil.tcc.util.AttributeHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import net.minecraft.resources.ResourceLocation;
import com.xlxyvergil.tcc.TaczCurios;
public class SharpBulletEffect extends MobEffect {
    public SharpBulletEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFF6644);
        this.addAttributeModifier(AttributeHelper.CRIT_DAMAGE,
            ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "sharp_bullet_effect_7de1fac5_8194"),
            AttributeModifier.Operation.ADD_MULTIPLIED_BASE,
            amplifier -> (amplifier + 1) * TaczCuriosConfig.getOrDefault(TaczCuriosConfig.COMMON.sharpBulletBaseCritDamage));
        AttributeHelper.registerSourceItem(ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "sharp_bullet_effect_7de1fac5_8194"), TccItems.SHARP_BULLET);
    }
}
