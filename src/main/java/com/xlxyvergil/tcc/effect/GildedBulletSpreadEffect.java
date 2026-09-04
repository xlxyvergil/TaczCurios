package com.xlxyvergil.tcc.effect;

import java.util.UUID;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.registries.TccItems;
import com.xlxyvergil.tcc.util.AttributeHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class GildedBulletSpreadEffect extends MobEffect {
    public GildedBulletSpreadEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x44AAFF);
        double perLevelValue = TaczCuriosConfig.COMMON.gildedBulletSpreadBulletCountPerLevel.get();
        this.addAttributeModifier(AttributeHelper.BULLET_COUNT,
            "78e5804f-3c3c-4563-a6e9-589d540f52ee", perLevelValue, AttributeModifier.Operation.MULTIPLY_BASE);
        AttributeHelper.registerSourceItem(UUID.fromString("78e5804f-3c3c-4563-a6e9-589d540f52ee"), TccItems.GILDED_BULLET_SPREAD);
    }

}
