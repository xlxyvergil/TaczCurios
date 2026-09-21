package com.xlxyvergil.tcc.items.curios.tcc;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.items.TccCurioItem;
import com.xlxyvergil.tcc.util.FusionData;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.resources.ResourceLocation;
import com.xlxyvergil.tcc.TaczCurios;
public class RippingPrime extends TccCurioItem {
    private static final ResourceLocation ROUNDS_PER_MINUTE_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "ripping_prime_e3eb5b32_73a7");
    private static final ResourceLocation PIERCE_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "ripping_prime_269dbf48_10a6");
    
    
    public RippingPrime(Properties properties) {
        super(properties);
    }
    
    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        // 登记该饰品施加的修饰符 ID → 来源饰品，供客户端属性面板显示来源图标。
        AttributeHelper.registerSourceItem(ROUNDS_PER_MINUTE_ID, stack.getItem());
        AttributeHelper.registerSourceItem(PIERCE_ID, stack.getItem());
        if (matchesRestriction(livingEntity)) {
            double fireRateBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.rippingPrimeFireRateBoost.get());
            double penetrationBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.rippingPrimePenetrationBoost.get());
            
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.ROUNDS_PER_MINUTE, fireRateBoost, ROUNDS_PER_MINUTE_ID, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.PIERCE, penetrationBoost, PIERCE_ID, AttributeModifier.Operation.ADD_VALUE);
        }
    }
    
    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.ROUNDS_PER_MINUTE, ROUNDS_PER_MINUTE_ID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.PIERCE, PIERCE_ID);
    }

    @Override
    public java.util.List<String> getWeaponTypeRestriction() {
        return java.util.List.of("rifle", "sniper", "smg", "mg", "rpg");
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        Level level = context.level();
        super.appendHoverText(stack, context, tooltip, flag);

        tooltip.add(Component.literal(""));

        double fireRateBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.rippingPrimeFireRateBoost.get() ) * 100;
        double penetrationBoost = TaczCuriosConfig.COMMON.rippingPrimePenetrationBoost.get();
        tooltip.add(Component.translatable("item.tcc.ripping_prime.effect", 
                String.format("%+.0f", fireRateBoost), String.format("%.1f", penetrationBoost))
            .withStyle(ChatFormatting.WHITE));

        tooltip.add(Component.literal(""));

    }
    

}
