package com.xlxyvergil.tcc.effect;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.registries.TccItems;
import com.xlxyvergil.tcc.util.AttributeHelper;
import java.util.UUID;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class SharpAmmoEffect extends MobEffect {
    public SharpAmmoEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFF6644);
        double baseValue = TaczCuriosConfig.COMMON.sharpAmmoBaseCritDamage.get();
        this.addAttributeModifier(AttributeHelper.CRIT_DAMAGE,
            "7ebabc7f-2ccd-4217-8602-ea39b9263673", baseValue, AttributeModifier.Operation.MULTIPLY_BASE);
        AttributeHelper.registerSourceItem(UUID.fromString("7ebabc7f-2ccd-4217-8602-ea39b9263673"), TccItems.SHARP_AMMO);
    }

    @Override
    public double getAttributeModifierValue(int amplifier, AttributeModifier modifier) {
        return (amplifier + 1) * TaczCuriosConfig.COMMON.sharpAmmoBaseCritDamage.get();
    }
}
