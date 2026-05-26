package com.xlxyvergil.tcc.items;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.util.BaseCurioItem;

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

/**
 * 压迫点 - 提升近战伤害
 * 效果：提升近战伤害（加算）
 */
public class OppressionPoint extends BaseCurioItem {
    
    // 属性修饰符UUID - 用于唯一标识这个修饰符
    private static final UUID ATTACK_DAMAGE_UUID = UUID.fromString("1f7eab00-eb00-4941-9404-4fdd3eb10515");
    
    // 修饰符名称
    private static final String ATTACK_DAMAGE_NAME = "tcc.oppression_point.attack_damage";
    
    public OppressionPoint(Properties properties) {
        super(properties);
    }
    
    /**
     * 应用压迫点效果
     * 给生物添加近战伤害加成（加算）
     */
    @Override
    protected void applyEffects(LivingEntity livingEntity) {
        double damageBoost = TaczCuriosConfig.COMMON.oppressionPointMeleeDamageBoost.get();
        AttributeHelper.applyModifier(livingEntity, AttributeHelper.ATTACK_DAMAGE, damageBoost, ATTACK_DAMAGE_UUID, ATTACK_DAMAGE_NAME, AttributeModifier.Operation.ADDITION);
    }
    
    /**
     * 移除压迫点效果
     */
    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.ATTACK_DAMAGE, ATTACK_DAMAGE_UUID);
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
        double meleeDamageBoost = TaczCuriosConfig.COMMON.oppressionPointMeleeDamageBoost.get() * 100;
        tooltip.add(Component.translatable("item.tcc.oppression_point.effect", String.format("%+.0f", meleeDamageBoost))
            .withStyle(ChatFormatting.BLUE));
        
        // 添加饰品槽位信息
        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable("tcc.tooltip.slot"));
        
        // 添加稀有度提示
        tooltip.add(Component.translatable("tcc.tooltip.rarity.common"));
    }
}