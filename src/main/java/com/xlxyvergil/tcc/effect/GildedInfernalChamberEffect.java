package com.xlxyvergil.tcc.effect;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.registries.TccItems;
import com.xlxyvergil.tcc.util.AttributeHelper;
import java.util.UUID;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class GildedInfernalChamberEffect extends MobEffect {
    public GildedInfernalChamberEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x44AAFF);
        double perLevelValue = TaczCuriosConfig.COMMON.gildedInfernalChamberBulletCountPerLevel.get();
        this.addAttributeModifier(AttributeHelper.BULLET_COUNT,
            "d64182ae-c6b7-43b3-a451-f6844b197823", perLevelValue, AttributeModifier.Operation.MULTIPLY_BASE);
        AttributeHelper.registerSourceItem(UUID.fromString("d64182ae-c6b7-43b3-a451-f6844b197823"), TccItems.GILDED_INFERNAL_CHAMBER);
    }

}
