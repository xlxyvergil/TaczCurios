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
 * 重装火力 - 提升165%手枪伤害，提5%不精准度
 * 效果：提65%手枪伤害（加算），提5%不精准度（加算）
 */
public class HeavyFirepower extends BaseCurioItem {
    
    // 属性修饰符UUID - 用于唯一标识修饰
    private static final UUID DAMAGE_UUID = UUID.fromString("401a1d7b-9724-4602-a956-ff32a991648f");
    private static final UUID INACCURACY_UUID = UUID.fromString("5c03b799-4491-4c9c-ab68-7678a42a9b4a");
    
    // 修饰符名
    private static final String DAMAGE_NAME = "tcc.heavy_firepower.damage";
    private static final String INACCURACY_NAME = "tcc.heavy_firepower.inaccuracy";
    
    public HeavyFirepower(Properties properties) {
        super(properties);
    }
    
    /**
     * 应用重装火力效果
     * 提升手枪伤害（加算）和不精准度（乘算     */
    @Override
    protected void applyEffects(LivingEntity livingEntity) {
        double damageBoost = TaczCuriosConfig.COMMON.heavyFirepowerDamageBoost.get();
        double inaccuracyBoost = TaczCuriosConfig.COMMON.heavyFirepowerAccuracyReduction.get();
        
        // 直接应用手枪伤害加成，无需检查是否手持手
        AttributeHelper.applyModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE_PISTOL, damageBoost, DAMAGE_UUID, DAMAGE_NAME, AttributeModifier.Operation.ADDITION);
        
        // 应用不精准度提升（加算）
        if (GunTypeChecker.isHoldingPistol(livingEntity)) {
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.INACCURACY, inaccuracyBoost, INACCURACY_UUID, INACCURACY_NAME, AttributeModifier.Operation.ADDITION);
        }
    }
    
    /**
     * 移除重装火力效果
     */
    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE_PISTOL, DAMAGE_UUID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.INACCURACY, INACCURACY_UUID);
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
        double damageBoost = TaczCuriosConfig.COMMON.heavyFirepowerDamageBoost.get() * 100;
        double inaccuracyBoost = TaczCuriosConfig.COMMON.heavyFirepowerAccuracyReduction.get() * 100;
        tooltip.add(Component.translatable("item.tcc.heavy_firepower.effect", String.format("%+.0f", damageBoost), String.format("%+.0f", inaccuracyBoost))
            .withStyle(ChatFormatting.GOLD));
        
        // 添加饰品槽位信息
        tooltip.add(Component.literal(""));
        
        
        // 添加稀有度提示
        tooltip.add(Component.translatable("tcc.tooltip.rarity.rare"));
    }
    
    /**
     * 当实体切换武器时应用效果
     */
    @Override
    public void applyGunSwitchEffect(LivingEntity livingEntity) {
        applyEffects(livingEntity);
    }
}