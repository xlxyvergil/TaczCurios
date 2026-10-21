package com.xlxyvergil.tcc.effect;

import com.xlxyvergil.taa.attribute.EntityAttributeRegistry;
import com.xlxyvergil.tcc.registries.TccItems;
import com.xlxyvergil.tcc.util.AttributeHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.UUID;

public class HeavenFireApocalypseBuffEffect extends MobEffect {
    /** 每级数值。注册期读不到配置，所以写死在常量里；tooltip 复用同一个常量。 */
    public static final double VALUE_PER_LEVEL = 0.5D;

    public HeavenFireApocalypseBuffEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFF5555);
        this.addAttributeModifier(
            EntityAttributeRegistry.BULLET_GUNDAMAGE.get(),
            "ba764054-9ad7-493e-b1bd-a6def116012d",
            VALUE_PER_LEVEL,
            AttributeModifier.Operation.MULTIPLY_BASE
        );
    }

}
