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

public class OverloadedMagazine extends TccCurioItem {
    private static final UUID MAGAZINE_CAPACITY_UUID = UUID.fromString("ac732131-54e3-4205-addf-96167a044710");
    private static final UUID RELOAD_UUID = UUID.fromString("9cd14129-3da2-47a1-bff1-b78ab747b9e9");

    private static final String MAGAZINE_CAPACITY_NAME = "tcc.overloaded_magazine.magazine_capacity";
    private static final String RELOAD_NAME = "tcc.overloaded_magazine.reload";

    public OverloadedMagazine(Properties properties) {
        super(properties);
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        // 登记该饰品施加的修饰符 UUID → 来源饰品，供客户端属性面板显示来源图标。
        AttributeHelper.registerSourceItem(MAGAZINE_CAPACITY_UUID, stack.getItem());
        AttributeHelper.registerSourceItem(RELOAD_UUID, stack.getItem());
        if (matchesRestriction(livingEntity)) {
            double magazineCapacityBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.overloadedMagazineCapacityBoost.get());
            double reloadDebuff = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.overloadedMagazineReloadSpeedReduction.get());

            AttributeHelper.applyModifier(livingEntity, AttributeHelper.MAGAZINE_CAPACITY, magazineCapacityBoost, MAGAZINE_CAPACITY_UUID, MAGAZINE_CAPACITY_NAME, AttributeModifier.Operation.ADDITION);
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.RELOAD_TIME, reloadDebuff, RELOAD_UUID, RELOAD_NAME, AttributeModifier.Operation.ADDITION);
        }
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.MAGAZINE_CAPACITY, MAGAZINE_CAPACITY_UUID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.RELOAD_TIME, RELOAD_UUID);
    }

    @Override
    public java.util.List<String> getWeaponTypeRestriction() {
        return java.util.List.of("shotgun");
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        tooltip.add(Component.literal(""));

        double magazineCapacityBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.overloadedMagazineCapacityBoost.get() ) * 100;
        double reloadDebuff = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.overloadedMagazineReloadSpeedReduction.get() ) * 100;
        tooltip.add(Component.translatable("item.tcc.overloaded_magazine.effect", 
                String.format("%+.0f", magazineCapacityBoost), String.format("%+.0f", reloadDebuff))
            .withStyle(ChatFormatting.GOLD));

        tooltip.add(Component.literal(""));

    }
    
}