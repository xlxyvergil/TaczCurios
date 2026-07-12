package com.xlxyvergil.tcc.items.curios;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.util.BaseCurioItem;
import com.xlxyvergil.tcc.util.GunTypeChecker;

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
 * 过载弹匣 - 提升弹匣容量，降低装填速度
 * 效果：提升弹匣容量（加算），降低装填速度（加算）
 */
public class OverloadedMagazine extends BaseCurioItem {

    // 属性修饰符UUID - 用于唯一标识修饰
    private static final UUID MAGAZINE_CAPACITY_UUID = UUID.fromString("ac732131-54e3-4205-addf-96167a044710");
    private static final UUID RELOAD_UUID = UUID.fromString("9cd14129-3da2-47a1-bff1-b78ab747b9e9");

    // 修饰符名
    private static final String MAGAZINE_CAPACITY_NAME = "tcc.overloaded_magazine.magazine_capacity";
    private static final String RELOAD_NAME = "tcc.overloaded_magazine.reload";

    public OverloadedMagazine(Properties properties) {
        super(properties);
    }

    /**
     * 应用过载弹匣效果
     * 提升弹匣容量（加算）和降低装填速度（加算）
     */
    @Override
    protected void applyEffects(LivingEntity livingEntity) {
        if (GunTypeChecker.isHoldingShotgun(livingEntity)) {
            double magazineCapacityBoost = TaczCuriosConfig.COMMON.overloadedMagazineCapacityBoost.get();
            double reloadDebuff = TaczCuriosConfig.COMMON.overloadedMagazineReloadSpeedReduction.get();

            AttributeHelper.applyModifier(livingEntity, AttributeHelper.MAGAZINE_CAPACITY, magazineCapacityBoost, MAGAZINE_CAPACITY_UUID, MAGAZINE_CAPACITY_NAME, AttributeModifier.Operation.ADDITION);
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.RELOAD_TIME, reloadDebuff, RELOAD_UUID, RELOAD_NAME, AttributeModifier.Operation.ADDITION);
        }
    }

    /**
     * 移除过载弹匣效果
     */
    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.MAGAZINE_CAPACITY, MAGAZINE_CAPACITY_UUID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.RELOAD_TIME, RELOAD_UUID);
    }

    /**
     * 添加物品的悬浮提示信息（鼠标悬停时显示）
     */
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);



        // 添加空行分隔
        tooltip.add(Component.literal(""));

        // 添加装备效果
        double magazineCapacityBoost = TaczCuriosConfig.COMMON.overloadedMagazineCapacityBoost.get() * 100;
        double reloadDebuff = TaczCuriosConfig.COMMON.overloadedMagazineReloadSpeedReduction.get() * 100;
        tooltip.add(Component.translatable("item.tcc.overloaded_magazine.effect", 
                String.format("%+.0f", magazineCapacityBoost), String.format("%+.0f", reloadDebuff))
            .withStyle(ChatFormatting.GOLD));

        // 添加饰品槽位信息
        tooltip.add(Component.literal(""));
        

        // 添加稀有度提示
        tooltip.add(Component.translatable("tcc.tooltip.rarity.rare"));
    }
    
    /**
     * 当生物切换武器时应用效果
     */
    @Override
    public void applyGunSwitchEffect(LivingEntity livingEntity) {
        applyEffects(livingEntity);
    }
}