package com.xlxyvergil.tcc.effect;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.registries.TccItems;
import com.xlxyvergil.tcc.util.AttributeHelper;
import java.util.UUID;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class SharpBulletEffect extends MobEffect {
    public SharpBulletEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFF6644);
        double baseValue = TaczCuriosConfig.COMMON.sharpBulletBaseCritDamage.get();
        this.addAttributeModifier(AttributeHelper.CRIT_DAMAGE,
            "7de1fac5-2f2f-4462-b6b2-1ab5d1b88194", baseValue, AttributeModifier.Operation.MULTIPLY_BASE);
        AttributeHelper.registerSourceItem(UUID.fromString("7de1fac5-2f2f-4462-b6b2-1ab5d1b88194"), TccItems.SHARP_BULLET);
    }

    @Override
    public double getAttributeModifierValue(int amplifier, AttributeModifier modifier) {
        return (amplifier + 1) * TaczCuriosConfig.COMMON.sharpBulletBaseCritDamage.get();
    }
}
