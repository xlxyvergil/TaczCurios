package com.xlxyvergil.tcc.items;


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
 * 腐败弹匣 - +66%弹匣容量，-33%装填速度
 * 效果：提升66%弹匣容量（加算），降低33%装填速度（加算），仅对步枪、狙击枪、冲锋枪、机枪、发射器生效
 */
public class CorruptMagazine extends BaseCurioItem {
    
    // 属性修饰符UUID - 用于唯一标识这些修饰符
    private static final UUID MAGAZINE_UUID = UUID.fromString("5d489ba1-55da-4f3a-83ea-69096eb4cccb");
    private static final UUID RELOAD_UUID = UUID.fromString("b747742d-1f42-4921-a900-af73409d453f");
    
    // 修饰符名称
    private static final String MAGAZINE_NAME = "tcc.corrupt_magazine.magazine_capacity";
    private static final String RELOAD_NAME = "tcc.corrupt_magazine.reload_speed";
    
    public CorruptMagazine(Properties properties) {
        super(properties);
    }
    
    /**
     * 应用腐败弹匣效果
     * 提升弹匣容量（加算）并降低装填速度（加算）
     */
    @Override
    protected void applyEffects(LivingEntity livingEntity) {
        // 检查生物是否持有支持的枪械类型，只有持有支持的枪械时才应用加成
        if (GunTypeChecker.isHoldingDmgBoostGunType(livingEntity)) {
            double magazineBoost = TaczCuriosConfig.COMMON.corruptMagazineCapacityBoost.get();
            double reloadPenalty = TaczCuriosConfig.COMMON.corruptMagazineReloadSpeedReduction.get();
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.MAGAZINE_CAPACITY, magazineBoost, MAGAZINE_UUID, MAGAZINE_NAME, AttributeModifier.Operation.ADDITION);
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.RELOAD_TIME, reloadPenalty, RELOAD_UUID, RELOAD_NAME, AttributeModifier.Operation.ADDITION);
        }
    }
    
    /**
     * 移除腐败弹匣效果
     */
    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.MAGAZINE_CAPACITY, MAGAZINE_UUID);
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
        double magazineBoost = TaczCuriosConfig.COMMON.corruptMagazineCapacityBoost.get() * 100;
        double reloadPenalty = TaczCuriosConfig.COMMON.corruptMagazineReloadSpeedReduction.get() * 100;
        tooltip.add(Component.translatable("item.tcc.corrupt_magazine.effect", String.format("%+.0f", magazineBoost), String.format("%+.0f", reloadPenalty))
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