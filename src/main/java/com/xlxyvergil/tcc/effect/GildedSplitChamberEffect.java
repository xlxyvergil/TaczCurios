package com.xlxyvergil.tcc.effect;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.AttributeHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class GildedSplitChamberEffect extends MobEffect {
    public GildedSplitChamberEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x44AAFF);
        double perLevelValue = TaczCuriosConfig.COMMON.gildedSplitChamberBulletCountPerLevel.get();
        this.addAttributeModifier(AttributeHelper.BULLET_COUNT,
            "0f372759-929e-4699-b253-dc73336e8a01", perLevelValue, AttributeModifier.Operation.MULTIPLY_BASE);
    }

}
