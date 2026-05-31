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
 * 三饰- 提供持枪移动速度加成
 * 通过TaczAttributeAdd的move_speed属性实现
 */
public class RedMovementTag extends BaseCurioItem {
    
    // 移动速度修饰符的UUID（确保唯一性）
    private static final UUID MOVE_SPEED_MODIFIER_UUID = UUID.fromString("e3ab07c7-2719-4942-99c7-526d58ba3736");
    
    public RedMovementTag(Properties properties) {
        super(properties);
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity) {
        double speedBoost = TaczCuriosConfig.COMMON.redMovementTagSpeedBoost.get();
        AttributeHelper.applyModifier(livingEntity, AttributeHelper.MOVE_SPEED, speedBoost, MOVE_SPEED_MODIFIER_UUID, "tcc_red_movement_speed_boost", AttributeModifier.Operation.ADDITION);
    }
    
    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.MOVE_SPEED, MOVE_SPEED_MODIFIER_UUID);
    }
    
    /**
     * 添加物品的悬浮提示信息（鼠标悬停时显示）
     */
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {

        
        // 添加空行分隔
        tooltip.add(Component.literal(""));
        
        // 添加装备效果
        double speedBoost = TaczCuriosConfig.COMMON.redMovementTagSpeedBoost.get() * 100;
        tooltip.add(Component.translatable("item.tcc.red_movement_tag.effect", String.format("%+.0f", speedBoost))
            .withStyle(ChatFormatting.AQUA));
        
        // 添加饰品槽位信息
        tooltip.add(Component.literal(""));
        
        
        // 添加稀有度提示
        tooltip.add(Component.translatable("tcc.tooltip.rarity.uncommon"));
    }
    
    @Override
    public void applyGunSwitchEffect(LivingEntity livingEntity) {
        applyEffects(livingEntity);
    }
}
