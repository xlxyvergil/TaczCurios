package com.xlxyvergil.tcc.effect;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.AttributeHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

/**
 * 镀层弹头扩散Buff - 击杀触发，弹头数量提升（可叠加4层）
 */
public class GildedBulletSpreadEffect extends MobEffect {
    public GildedBulletSpreadEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x44AAFF);
        double perLevelValue = TaczCuriosConfig.COMMON.gildedBulletSpreadBulletCountPerLevel.get();
        this.addAttributeModifier(AttributeHelper.BULLET_COUNT,
            "c1d2e3f4-6203-4000-8000-000000000001", perLevelValue, AttributeModifier.Operation.MULTIPLY_BASE);
    }

    @Override
    public double getAttributeModifierValue(int amplifier, AttributeModifier modifier) {
        return (amplifier + 1) * TaczCuriosConfig.COMMON.gildedBulletSpreadBulletCountPerLevel.get();
    }
}
