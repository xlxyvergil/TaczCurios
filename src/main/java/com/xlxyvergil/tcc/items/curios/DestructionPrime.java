package com.xlxyvergil.tcc.items.curios;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.util.BaseCurioItem;
import com.xlxyvergil.tcc.util.FusionUpgradeUtil;
import com.xlxyvergil.tcc.util.GunTypeChecker;
import com.xlxyvergil.tcc.util.FusionData;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * 破灭Prime - 霰弹枪饰�?
 * 效果：暴击伤�?+110%
 */
public class DestructionPrime extends BaseCurioItem {

    private static final UUID CRIT_DAMAGE_UUID = UUID.fromString("a1b2c3d4-3002-4000-8000-000000000001");

    private static final String CRIT_DAMAGE_NAME = "tcc.destruction_prime.crit_damage";

    public DestructionPrime(Properties properties) {
        super(properties);
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        if (GunTypeChecker.isHoldingShotgun(livingEntity)) {
            double critDamageBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.destructionPrimeCritDamage.get());
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.CRIT_DAMAGE, critDamageBoost, CRIT_DAMAGE_UUID, CRIT_DAMAGE_NAME, AttributeModifier.Operation.MULTIPLY_BASE);
        } else {
            AttributeHelper.removeModifier(livingEntity, AttributeHelper.CRIT_DAMAGE, CRIT_DAMAGE_UUID);
        }
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.CRIT_DAMAGE, CRIT_DAMAGE_UUID);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        tooltip.add(Component.literal(""));

        double critDamageBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.destructionPrimeCritDamage.get() ) * 100;
        tooltip.add(Component.translatable("item.tcc.destruction_prime.effect",
                String.format("%+.0f", critDamageBoost))
            .withStyle(ChatFormatting.WHITE));

        tooltip.add(Component.literal(""));
        
    }

}
