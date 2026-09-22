package com.xlxyvergil.tcc.effect;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.registries.TccItems;
import com.xlxyvergil.tcc.util.AttributeHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import net.minecraft.resources.ResourceLocation;
import com.xlxyvergil.tcc.TaczCurios;
public class ArgonScopeEffect extends MobEffect {
    public ArgonScopeEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFFAA44);
        this.addAttributeModifier(AttributeHelper.CRIT_CHANCE,
            ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "argon_scope_effect_a46002d7_e441"),
            AttributeModifier.Operation.ADD_MULTIPLIED_BASE,
            amplifier -> (amplifier + 1) * TaczCuriosConfig.getOrDefault(TaczCuriosConfig.COMMON.argonScopeBaseCritChance));
        AttributeHelper.registerSourceItem(ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "argon_scope_effect_a46002d7_e441"), TccItems.ARGON_SCOPE);
    }
}
