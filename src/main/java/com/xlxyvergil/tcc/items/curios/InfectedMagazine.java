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
 * 感染弹匣 - 提升弹匣容量，降低装填速度
 * 效果：提升弹匣容量（加算），降低装填速度（加算）
 */
public class InfectedMagazine extends BaseCurioItem {

    // 属性修饰符UUID - 用于唯一标识修饰
    private static final UUID MAGAZINE_CAPACITY_UUID = UUID.fromString("f7d6ce3b-7168-44d0-9637-c4eb2caf0fbc");
    private static final UUID RELOAD_UUID = UUID.fromString("fa325acb-cb87-4288-8d10-c3d637b9242c");

    // 修饰符名
    private static final String MAGAZINE_CAPACITY_NAME = "tcc.infected_magazine.magazine_capacity";
    private static final String RELOAD_NAME = "tcc.infected_magazine.reload";

    public InfectedMagazine(Properties properties) {
        super(properties);
    }

    /**
     * 应用感染弹匣效果
     * 提升弹匣容量（加算）和降低装填速度（加算）
     */
    @Override
    protected void applyEffects(LivingEntity livingEntity) {
        // 检查生物是否持有手枪，只有持有手枪时才应用加成
        if (GunTypeChecker.isHoldingPistol(livingEntity)) {
            double magazineCapacityBoost = TaczCuriosConfig.COMMON.infectedMagazineCapacityBoost.get();
            double reloadDebuff = TaczCuriosConfig.COMMON.infectedMagazineReloadSpeedReduction.get();

            AttributeHelper.applyModifier(livingEntity, AttributeHelper.MAGAZINE_CAPACITY, magazineCapacityBoost, MAGAZINE_CAPACITY_UUID, MAGAZINE_CAPACITY_NAME, AttributeModifier.Operation.ADDITION);
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.RELOAD_TIME, reloadDebuff, RELOAD_UUID, RELOAD_NAME, AttributeModifier.Operation.ADDITION);
        }
    }

    /**
     * 移除感染弹匣效果
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
        double magazineCapacityBoost = TaczCuriosConfig.COMMON.infectedMagazineCapacityBoost.get() * 100;
        double reloadDebuff = TaczCuriosConfig.COMMON.infectedMagazineReloadSpeedReduction.get() * 100;
        tooltip.add(Component.translatable("item.tcc.infected_magazine.effect", 
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