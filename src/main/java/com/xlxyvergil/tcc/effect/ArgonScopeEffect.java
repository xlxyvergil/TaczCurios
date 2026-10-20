package com.xlxyvergil.tcc.effect;

import com.xlxyvergil.tcc.registries.TccItems;
import com.xlxyvergil.tcc.util.AttributeHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.UUID;

public class ArgonScopeEffect extends MobEffect {
    /** 每级数值。注册期读不到配置，所以写死在常量里；tooltip 复用同一个常量。 */
    public static final double VALUE_PER_LEVEL = 0.15D;

    public ArgonScopeEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFFAA44);
        this.addAttributeModifier(AttributeHelper.CRIT_CHANCE,
            "a46002d7-028e-4b39-b90c-c7f949aee441", VALUE_PER_LEVEL, AttributeModifier.Operation.MULTIPLY_BASE);
        AttributeHelper.registerSourceItem(UUID.fromString("a46002d7-028e-4b39-b90c-c7f949aee441"), TccItems.ARGON_SCOPE);
    }

}
