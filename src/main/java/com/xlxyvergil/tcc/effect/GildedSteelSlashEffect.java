package com.xlxyvergil.tcc.effect;

import com.xlxyvergil.tcc.registries.TccItems;
import com.xlxyvergil.tcc.util.AttributeHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.UUID;

public class GildedSteelSlashEffect extends MobEffect {
    /** 每级数值。注册期读不到配置，所以写死在常量里；tooltip 复用同一个常量。 */
    public static final double VALUE_PER_LEVEL = 0.025D;

    public GildedSteelSlashEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFF8866);
        this.addAttributeModifier(AttributeHelper.CRIT_DAMAGE,
            "f7508ebd-4a0e-461c-a9cc-97c658ad7364", VALUE_PER_LEVEL, AttributeModifier.Operation.MULTIPLY_BASE);
    }

}
