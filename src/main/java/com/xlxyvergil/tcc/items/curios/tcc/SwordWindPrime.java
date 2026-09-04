package com.xlxyvergil.tcc.items.curios.tcc;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.items.TccCurioItem;
import com.xlxyvergil.tcc.util.FusionData;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class SwordWindPrime extends TccCurioItem {
    private static final UUID ENTITY_INTERACTION_RANGE_UUID = UUID.fromString("3a32b0f7-9ef5-4c63-bb08-610762942881");
    
    private static final String ENTITY_INTERACTION_RANGE_NAME = "tcc.sword_wind_prime.entity_interaction_range";
    
 
    
    public SwordWindPrime(Properties properties) {
        super(properties);
    }
    
    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        // 登记该饰品施加的修饰符 UUID → 来源饰品，供客户端属性面板显示来源图标。
        AttributeHelper.registerSourceItem(ENTITY_INTERACTION_RANGE_UUID, stack.getItem());
        if (matchesRestriction(livingEntity)) {
            double rangeBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.swordWindPrimeMeleeRangeBoost.get());
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.ENTITY_REACH, rangeBoost, ENTITY_INTERACTION_RANGE_UUID, ENTITY_INTERACTION_RANGE_NAME, AttributeModifier.Operation.ADDITION);
        }
    }
    
    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.ENTITY_REACH, ENTITY_INTERACTION_RANGE_UUID);
    }
    
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        tooltip.add(Component.literal(""));

        double meleeDistanceBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.swordWindPrimeMeleeRangeBoost.get());
        tooltip.add(Component.translatable("item.tcc.sword_wind_prime.effect", String.format("%+.1f", meleeDistanceBoost))
            .withStyle(ChatFormatting.WHITE));

        tooltip.add(Component.literal(""));

    }

    @Override
    public java.util.List<String> getWeaponTypeRestriction() {
        return java.util.List.of("melee");
    }
}

