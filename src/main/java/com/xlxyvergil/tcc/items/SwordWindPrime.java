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
 * 剑风Prime - 提升实体交互范围
 * 效果：提升实体交互范围（加算）
 */
public class SwordWindPrime extends BaseCurioItem {
    
    // 属性修饰符UUID - 用于唯一标识这个修饰
    private static final UUID ENTITY_INTERACTION_RANGE_UUID = UUID.fromString("3a32b0f7-9ef5-4c63-bb08-610762942881");
    
    // 修饰符名
    private static final String ENTITY_INTERACTION_RANGE_NAME = "tcc.sword_wind_prime.entity_interaction_range";
    
 
    
    public SwordWindPrime(Properties properties) {
        super(properties);
    }
    
    @Override
    protected void applyEffects(LivingEntity livingEntity) {
        double rangeBoost = TaczCuriosConfig.COMMON.swordWindPrimeMeleeRangeBoost.get();
        AttributeHelper.applyModifier(livingEntity, AttributeHelper.ENTITY_REACH, rangeBoost, ENTITY_INTERACTION_RANGE_UUID, ENTITY_INTERACTION_RANGE_NAME, AttributeModifier.Operation.ADDITION);
    }
    
    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.ENTITY_REACH, ENTITY_INTERACTION_RANGE_UUID);
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
        double meleeDistanceBoost = TaczCuriosConfig.COMMON.swordWindPrimeMeleeRangeBoost.get();
        tooltip.add(Component.translatable("item.tcc.sword_wind_prime.effect", String.format("%+.0f", meleeDistanceBoost))
            .withStyle(ChatFormatting.WHITE));
        
        // 添加饰品槽位信息
        tooltip.add(Component.literal(""));
        
        
        // 添加稀有度提示
        tooltip.add(Component.translatable("tcc.tooltip.rarity.epic"));
    }
}

