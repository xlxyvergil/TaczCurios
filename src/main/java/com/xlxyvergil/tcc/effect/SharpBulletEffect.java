package com.xlxyvergil.tcc.effect;

import com.xlxyvergil.tcc.registries.TccItems;
import com.xlxyvergil.tcc.util.AttributeHelper;
import java.util.UUID;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class SharpBulletEffect extends MobEffect {
    /** 每级数值。注册期读不到配置，所以写死在常量里；tooltip 复用同一个常量。 */
    public static final double VALUE_PER_LEVEL = 0.13D;

    public SharpBulletEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFF6644);
        this.addAttributeModifier(AttributeHelper.CRIT_DAMAGE,
            "7de1fac5-2f2f-4462-b6b2-1ab5d1b88194", VALUE_PER_LEVEL, AttributeModifier.Operation.MULTIPLY_BASE);
    }

}
