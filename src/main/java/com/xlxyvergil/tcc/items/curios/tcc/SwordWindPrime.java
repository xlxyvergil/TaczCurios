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
public class SwordWindPrime extends TccCurioItem {
    private static final ResourceLocation ENTITY_INTERACTION_RANGE_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "sword_wind_prime_3a32b0f7_2881");
    
    
 
    
    public SwordWindPrime(Properties properties) {
        super(properties);
    }
    
    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        // 登记该饰品施加的修饰符 ID → 来源饰品，供客户端属性面板显示来源图标。
        AttributeHelper.registerSourceItem(ENTITY_INTERACTION_RANGE_ID, stack.getItem());
        if (matchesRestriction(livingEntity)) {
            double rangeBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.swordWindPrimeMeleeRangeBoost.get());
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.ENTITY_REACH, rangeBoost, ENTITY_INTERACTION_RANGE_ID, AttributeModifier.Operation.ADD_VALUE);
        }
    }
    
    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.ENTITY_REACH, ENTITY_INTERACTION_RANGE_ID);
    }
    
    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        Level level = context.level();
        super.appendHoverText(stack, context, tooltip, flag);

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

