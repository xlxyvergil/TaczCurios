package com.xlxyvergil.tcc.effect;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.registries.TccItems;
import com.xlxyvergil.tcc.util.AttributeHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import net.minecraft.resources.ResourceLocation;
import com.xlxyvergil.tcc.TaczCurios;
public class GildedArgonScopeKillEffect extends MobEffect {
    public GildedArgonScopeKillEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFFBB44);
        double perLevelValue = TaczCuriosConfig.COMMON.gildedArgonScopeCritChancePerLevel.get();
        this.addAttributeModifier(AttributeHelper.CRIT_CHANCE,
            ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "gilded_argon_scope_kill_effect_3da691c2_d1ee"), perLevelValue, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        AttributeHelper.registerSourceItem(ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "gilded_argon_scope_kill_effect_3da691c2_d1ee"), TccItems.GILDED_ARGON_SCOPE);
    }

}
