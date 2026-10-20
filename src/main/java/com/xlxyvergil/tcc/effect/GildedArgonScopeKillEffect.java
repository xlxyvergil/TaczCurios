package com.xlxyvergil.tcc.effect;

import com.xlxyvergil.tcc.registries.TccItems;
import com.xlxyvergil.tcc.util.AttributeHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.UUID;

public class GildedArgonScopeKillEffect extends MobEffect {
    /** 每级数值。注册期读不到配置，所以写死在常量里；tooltip 复用同一个常量。 */
    public static final double VALUE_PER_LEVEL = 0.033D;

    public GildedArgonScopeKillEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFFBB44);
        this.addAttributeModifier(AttributeHelper.CRIT_CHANCE,
            "3da691c2-0f73-48cc-938a-d8c9152ed1ee", VALUE_PER_LEVEL, AttributeModifier.Operation.MULTIPLY_BASE);
        AttributeHelper.registerSourceItem(UUID.fromString("3da691c2-0f73-48cc-938a-d8c9152ed1ee"), TccItems.GILDED_ARGON_SCOPE);
    }

}
