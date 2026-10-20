package com.xlxyvergil.tcc.effect;

import com.xlxyvergil.tcc.registries.TccItems;
import com.xlxyvergil.tcc.util.AttributeHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.UUID;

public class GildedArgonScopeEffect extends MobEffect {
    /** 每级数值。注册期读不到配置，所以写死在常量里；tooltip 复用同一个常量。 */
    public static final double VALUE_PER_LEVEL = 0.11D;

    public GildedArgonScopeEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFFCC66);
        this.addAttributeModifier(AttributeHelper.CRIT_CHANCE,
            "cf77490e-6c8f-41b6-b363-3faa0a08474c", VALUE_PER_LEVEL, AttributeModifier.Operation.MULTIPLY_BASE);
    }

}
