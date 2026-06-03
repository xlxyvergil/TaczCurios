package com.xlxyvergil.tcc.items;

import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.util.BaseCurioItem;
import com.xlxyvergil.tcc.util.GunTypeChecker;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.UUID;

public class SevenThunders extends BaseCurioItem {

    private static final UUID HEADSHOT_MULTIPLIER_UUID = UUID.fromString("6e2a8b94-6e4f-4f4d-bf5f-0c8fcbcf0b2a");
    private static final UUID CRIT_CHANCE_UUID = UUID.fromString("e0c9302a-6b28-4e39-b6f8-7ae8120d2b65");
    private static final UUID CRIT_DAMAGE_UUID = UUID.fromString("a5a91c44-e7a1-46d4-97e8-0b2c2bdbf6c0");

    public SevenThunders(Properties properties) {
        super(properties);
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity) {
        if (GunTypeChecker.isHoldingSniper(livingEntity)) {
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.HEADSHOT_MULTIPLIER, 2.0, HEADSHOT_MULTIPLIER_UUID,
                "tcc.seven_thunders.headshot_multiplier", AttributeModifier.Operation.ADDITION);
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.CRIT_CHANCE, 0.2, CRIT_CHANCE_UUID,
                "tcc.seven_thunders.crit_chance", AttributeModifier.Operation.MULTIPLY_BASE);
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.CRIT_DAMAGE, 0.5, CRIT_DAMAGE_UUID,
                "tcc.seven_thunders.crit_damage", AttributeModifier.Operation.MULTIPLY_BASE);
        } else {
            removeEffects(livingEntity);
        }
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.HEADSHOT_MULTIPLIER, HEADSHOT_MULTIPLIER_UUID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.CRIT_CHANCE, CRIT_CHANCE_UUID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.CRIT_DAMAGE, CRIT_DAMAGE_UUID);
    }

    @Override
    public void applyGunSwitchEffect(LivingEntity livingEntity) {
        applyEffects(livingEntity);
    }
}
