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

public class RippingPrime extends TccCurioItem {
    private static final UUID ROUNDS_PER_MINUTE_UUID = UUID.fromString("e3eb5b32-fdfc-47ca-988c-a82d9d8173a7");
    private static final UUID PIERCE_UUID = UUID.fromString("269dbf48-02f5-43f9-a4f2-50bf03aa10a6");
    
    private static final String ROUNDS_PER_MINUTE_NAME = "tcc.ripping_prime.rounds_per_minute";
    private static final String PIERCE_NAME = "tcc.ripping_prime.pierce";
    
    public RippingPrime(Properties properties) {
        super(properties);
    }
    
    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        if (matchesRestriction(livingEntity)) {
            double fireRateBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.rippingPrimeFireRateBoost.get());
            double penetrationBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.rippingPrimePenetrationBoost.get());
            
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.ROUNDS_PER_MINUTE, fireRateBoost, ROUNDS_PER_MINUTE_UUID, ROUNDS_PER_MINUTE_NAME, AttributeModifier.Operation.MULTIPLY_BASE);
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.PIERCE, penetrationBoost, PIERCE_UUID, PIERCE_NAME, AttributeModifier.Operation.ADDITION);
        }
    }
    
    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.ROUNDS_PER_MINUTE, ROUNDS_PER_MINUTE_UUID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.PIERCE, PIERCE_UUID);
    }

    @Override
    public java.util.List<String> getWeaponTypeRestriction() {
        return java.util.List.of("rifle", "sniper", "smg", "mg", "rpg");
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        tooltip.add(Component.literal(""));

        double fireRateBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.rippingPrimeFireRateBoost.get() ) * 100;
        double penetrationBoost = TaczCuriosConfig.COMMON.rippingPrimePenetrationBoost.get();
        tooltip.add(Component.translatable("item.tcc.ripping_prime.effect", 
                String.format("%+.0f", fireRateBoost), String.format("%.1f", penetrationBoost))
            .withStyle(ChatFormatting.WHITE));

        tooltip.add(Component.literal(""));

    }
    

}
