package com.xlxyvergil.tcc.effect;

import com.xlxyvergil.tcc.registries.TccItems;
import com.xlxyvergil.tcc.util.AttributeHelper;
import java.util.UUID;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class LaserScopeEffect extends MobEffect {
    /** 每级数值。注册期读不到配置，所以写死在常量里；tooltip 复用同一个常量。 */
    public static final double VALUE_PER_LEVEL = 0.13D;

    public LaserScopeEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFF8844);
        this.addAttributeModifier(AttributeHelper.CRIT_CHANCE,
            "b9fb5573-bd2f-4c82-98e4-8eeb2b6b6169", VALUE_PER_LEVEL, AttributeModifier.Operation.MULTIPLY_BASE);
        AttributeHelper.registerSourceItem(UUID.fromString("b9fb5573-bd2f-4c82-98e4-8eeb2b6b6169"), TccItems.LASER_SCOPE);
    }

}
