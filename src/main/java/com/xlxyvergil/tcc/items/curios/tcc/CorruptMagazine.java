package com.xlxyvergil.tcc.items.curios.tcc;


import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.items.TccCurioItem;
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

public class CorruptMagazine extends TccCurioItem {
    // 属性修饰符UUID - 用于唯一标识这些修饰符
    private static final UUID MAGAZINE_UUID = UUID.fromString("5d489ba1-55da-4f3a-83ea-69096eb4cccb");
    private static final UUID RELOAD_UUID = UUID.fromString("b747742d-1f42-4921-a900-af73409d453f");
    
    // 修饰符名称
    private static final String MAGAZINE_NAME = "tcc.corrupt_magazine.magazine_capacity";
    private static final String RELOAD_NAME = "tcc.corrupt_magazine.reload_speed";
    
    public CorruptMagazine(Properties properties) {
        super(properties);
    }
    
    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        // 登记该饰品施加的修饰符 UUID → 来源饰品，供客户端属性面板显示来源图标。
        AttributeHelper.registerSourceItem(MAGAZINE_UUID, stack.getItem());
        AttributeHelper.registerSourceItem(RELOAD_UUID, stack.getItem());
        if (matchesRestriction(livingEntity)) {
            double magazineBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.corruptMagazineCapacityBoost.get());
            double reloadPenalty = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.corruptMagazineReloadSpeedReduction.get());
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.MAGAZINE_CAPACITY, magazineBoost, MAGAZINE_UUID, MAGAZINE_NAME, AttributeModifier.Operation.ADDITION);
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.RELOAD_TIME, reloadPenalty, RELOAD_UUID, RELOAD_NAME, AttributeModifier.Operation.ADDITION);
        }
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.MAGAZINE_CAPACITY, MAGAZINE_UUID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.RELOAD_TIME, RELOAD_UUID);
    }

    @Override
    public java.util.List<String> getWeaponTypeRestriction() {
        return java.util.List.of("rifle", "sniper", "smg", "mg", "rpg");
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        tooltip.add(Component.literal(""));

        double magazineBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.corruptMagazineCapacityBoost.get() ) * 100;
        double reloadPenalty = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.corruptMagazineReloadSpeedReduction.get() ) * 100;
        tooltip.add(Component.translatable("item.tcc.corrupt_magazine.effect", String.format("%+.0f", magazineBoost), String.format("%+.0f", reloadPenalty))
            .withStyle(ChatFormatting.GOLD));

        tooltip.add(Component.literal(""));

    }
    
}