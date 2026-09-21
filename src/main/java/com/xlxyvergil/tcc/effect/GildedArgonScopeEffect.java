package com.xlxyvergil.tcc.effect;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.registries.TccItems;
import com.xlxyvergil.tcc.util.AttributeHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.resources.ResourceLocation;
import com.xlxyvergil.tcc.TaczCurios;


public class GildedArgonScopeEffect extends MobEffect {
    public GildedArgonScopeEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFFCC66);
        double perLevelValue = TaczCuriosConfig.COMMON.gildedArgonScopeBaseCritChance.get();
        this.addAttributeModifier(AttributeHelper.CRIT_CHANCE,
            ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "gilded_argon_scope_effect_cf77490e_474c"), perLevelValue, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    }

}
