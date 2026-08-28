package com.xlxyvergil.tcc.effect;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.AttributeHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

/**
 * 镀层弹头扩散Buff - 击杀触发，弹头数量提升（可叠加48层）
 */
public class GildedBulletSpreadEffect extends MobEffect {
    public GildedBulletSpreadEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x44AAFF);
        double perLevelValue = TaczCuriosConfig.COMMON.gildedBulletSpreadBulletCountPerLevel.get();
        this.addAttributeModifier(AttributeHelper.BULLET_COUNT,
            "78e5804f-3c3c-4563-a6e9-589d540f52ee", perLevelValue, AttributeModifier.Operation.MULTIPLY_BASE);
    }

}
