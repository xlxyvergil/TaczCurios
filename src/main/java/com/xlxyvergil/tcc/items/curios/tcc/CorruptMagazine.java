package com.xlxyvergil.tcc.items.curios.tcc;


import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.items.TccCurioItem;
import com.xlxyvergil.tcc.util.FusionData;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.resources.ResourceLocation;
import com.xlxyvergil.tcc.TaczCurios;
public class CorruptMagazine extends TccCurioItem {
    // 属性修饰符UUID - 用于唯一标识这些修饰符
    private static final ResourceLocation MAGAZINE_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "corrupt_magazine_5d489ba1_cccb");
    private static final ResourceLocation RELOAD_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "corrupt_magazine_b747742d_453f");
    
    // 修饰符名称
    
    public CorruptMagazine(Properties properties) {
        super(properties);
    }
    
    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        // 登记该饰品施加的修饰符 ID → 来源饰品，供客户端属性面板显示来源图标。
        AttributeHelper.registerSourceItem(MAGAZINE_ID, stack.getItem());
        AttributeHelper.registerSourceItem(RELOAD_ID, stack.getItem());
        if (matchesRestriction(livingEntity)) {
            double magazineBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.corruptMagazineCapacityBoost.get());
            double reloadPenalty = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.corruptMagazineReloadSpeedReduction.get());
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.MAGAZINE_CAPACITY, magazineBoost, MAGAZINE_ID, AttributeModifier.Operation.ADD_VALUE);
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.RELOAD_TIME, reloadPenalty, RELOAD_ID, AttributeModifier.Operation.ADD_VALUE);
        }
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.MAGAZINE_CAPACITY, MAGAZINE_ID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.RELOAD_TIME, RELOAD_ID);
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

        double magazineBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.corruptMagazineCapacityBoost.get() ) * 100;
        double reloadPenalty = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.corruptMagazineReloadSpeedReduction.get() ) * 100;
        tooltip.add(Component.translatable("item.tcc.corrupt_magazine.effect", String.format("%+.0f", magazineBoost), String.format("%+.0f", reloadPenalty))
            .withStyle(ChatFormatting.GOLD));

        tooltip.add(Component.literal(""));

    }
    
}