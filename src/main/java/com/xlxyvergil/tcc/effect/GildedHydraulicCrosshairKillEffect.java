package com.xlxyvergil.tcc.effect;

import java.util.UUID;

import com.xlxyvergil.tcc.registries.TccItems;
import com.xlxyvergil.tcc.util.AttributeHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class GildedHydraulicCrosshairKillEffect extends MobEffect {
    /** 每级数值。注册期读不到配置，所以写死在常量里；tooltip 复用同一个常量。 */
    public static final double VALUE_PER_LEVEL = 0.033D;

    public GildedHydraulicCrosshairKillEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFFCC44);
        this.addAttributeModifier(AttributeHelper.CRIT_CHANCE,
            "410756c5-8d3b-4bcf-ab77-9bc3728175a2", VALUE_PER_LEVEL, AttributeModifier.Operation.MULTIPLY_BASE);
    }

}
