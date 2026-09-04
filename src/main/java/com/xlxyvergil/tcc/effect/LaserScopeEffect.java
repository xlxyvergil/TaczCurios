package com.xlxyvergil.tcc.effect;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.registries.TccItems;
import com.xlxyvergil.tcc.util.AttributeHelper;
import java.util.UUID;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class LaserScopeEffect extends MobEffect {
    public LaserScopeEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFF8844);
        double baseValue = TaczCuriosConfig.COMMON.laserScopeBaseCritChance.get();
        this.addAttributeModifier(AttributeHelper.CRIT_CHANCE,
            "b9fb5573-bd2f-4c82-98e4-8eeb2b6b6169", baseValue, AttributeModifier.Operation.MULTIPLY_BASE);
        AttributeHelper.registerSourceItem(UUID.fromString("b9fb5573-bd2f-4c82-98e4-8eeb2b6b6169"), TccItems.LASER_SCOPE);
    }

    @Override
    public double getAttributeModifierValue(int amplifier, AttributeModifier modifier) {
        return (amplifier + 1) * TaczCuriosConfig.COMMON.laserScopeBaseCritChance.get();
    }
}
