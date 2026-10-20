package com.xlxyvergil.tcc.effect;

import com.xlxyvergil.tcc.registries.TccItems;
import com.xlxyvergil.tcc.util.AttributeHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import net.minecraft.resources.ResourceLocation;
import com.xlxyvergil.tcc.TaczCurios;
public class GildedArgonScopeKillEffect extends MobEffect {
    /** 每级数值。注册期读不到配置，所以写死在常量里；tooltip 复用同一个常量。 */
    public static final double VALUE_PER_LEVEL = 0.033D;

    public GildedArgonScopeKillEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFFBB44);
        this.addAttributeModifier(AttributeHelper.CRIT_CHANCE,
            ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "gilded_argon_scope_kill_effect_3da691c2_d1ee"),
            AttributeModifier.Operation.ADD_MULTIPLIED_BASE,
            amplifier -> (amplifier + 1) * VALUE_PER_LEVEL);
        AttributeHelper.registerSourceItem(ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "gilded_argon_scope_kill_effect_3da691c2_d1ee"), TccItems.GILDED_ARGON_SCOPE);
    }

}
