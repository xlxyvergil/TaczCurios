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
 * 乌拉尔银狼饰品 - 提供爆头倍率加成
 * 通过TaczAttributeAdd的headshot_multiplier属性实现
 */
public class UralWolfTag extends BaseCurioItem {
    
    // 爆头倍率修饰符的UUID（确保唯一性）
    private static final UUID HEADSHOT_MULTIPLIER_MODIFIER_UUID = UUID.fromString("96a4146f-8ea4-4c23-be07-d007e222c5f6");
    
    public UralWolfTag(Properties properties) {
        super(properties);
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity) {
        double multiplierBoost = TaczCuriosConfig.COMMON.uralWolfTagHeadshotMultiplierBoost.get();
        AttributeHelper.applyModifier(livingEntity, AttributeHelper.HEADSHOT_MULTIPLIER, multiplierBoost, HEADSHOT_MULTIPLIER_MODIFIER_UUID, "tcc.ural_wolf_tag.headshot_multiplier", AttributeModifier.Operation.ADDITION);
    }
    
    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.HEADSHOT_MULTIPLIER, HEADSHOT_MULTIPLIER_MODIFIER_UUID);
    }

    /**
     * 添加物品的悬浮提示信息（鼠标悬停时显示）
     */
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {

        
        // 添加空行分隔
        tooltip.add(Component.literal(""));
        
        // 添加装备效果
        double multiplierBoost = TaczCuriosConfig.COMMON.uralWolfTagHeadshotMultiplierBoost.get() * 100;
        tooltip.add(Component.translatable("item.tcc.ural_wolf_tag.effect", String.format("%+.0f", multiplierBoost))
            .withStyle(ChatFormatting.AQUA));
        
        // 添加饰品槽位信息
        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable("tcc.tooltip.slot"));
        
        // 添加稀有度提示
        tooltip.add(Component.translatable("tcc.tooltip.rarity.uncommon"));
    }
    
    @Override
    public void applyGunSwitchEffect(LivingEntity livingEntity) {
        applyEffects(livingEntity);
    }
}