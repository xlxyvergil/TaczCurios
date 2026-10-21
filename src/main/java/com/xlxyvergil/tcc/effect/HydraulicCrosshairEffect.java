package com.xlxyvergil.tcc.effect;

import com.xlxyvergil.tcc.registries.TccItems;
import com.xlxyvergil.tcc.util.AttributeHelper;
import java.util.UUID;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class HydraulicCrosshairEffect extends MobEffect {
    /** 每级数值。注册期读不到配置，所以写死在常量里；tooltip 复用同一个常量。 */
    public static final double VALUE_PER_LEVEL = 0.15D;

    public HydraulicCrosshairEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFF9944);
        this.addAttributeModifier(AttributeHelper.CRIT_CHANCE,
            "f32f9e0d-9078-4da4-985b-c3ac3636b349", VALUE_PER_LEVEL, AttributeModifier.Operation.MULTIPLY_BASE);
    }

}
