package com.xlxyvergil.tcc.effect;

import com.xlxyvergil.taa.attribute.EntityAttributeRegistry;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.registries.TccItems;
import com.xlxyvergil.tcc.util.AttributeHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import net.minecraft.resources.ResourceLocation;
import com.xlxyvergil.tcc.TaczCurios;
public class HeavenFireApocalypseBuffEffect extends MobEffect {

    public HeavenFireApocalypseBuffEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFF5555);
        this.addAttributeModifier(
            EntityAttributeRegistry.BULLET_GUNDAMAGE,
            ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "heaven_fire_apocalypse_buff_effect_ba764054_012d"),
            AttributeModifier.Operation.ADD_MULTIPLIED_BASE,
            amplifier -> (amplifier + 1) * TaczCuriosConfig.COMMON.heavenFireApocalypseNearbyPlayerDamageBoost.get()
        );
        AttributeHelper.registerSourceItem(ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "heaven_fire_apocalypse_buff_effect_ba764054_012d"), TccItems.HEAVEN_FIRE_APOCALYPSE);
    }
}
