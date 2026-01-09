package com.xlxyvergil.tcc.items;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * 乌拉尔银狼饰品 - 提供爆头倍率加成
 * 通过TaczAttributeAdd的headshot_multiplier属性实现
 */
public class UralWolfTag extends ItemBaseCurio {
    
    // 爆头倍率修饰符的UUID（确保唯一性）
    private static final UUID HEADSHOT_MULTIPLIER_MODIFIER_UUID = UUID.fromString("96a4146f-8ea4-4c23-be07-d007e222c5f6");
    
    public UralWolfTag(Properties properties) {
        super(properties);
    }
    
    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        if (slotContext.entity() instanceof LivingEntity) {
            applyHeadshotMultiplierEffect((LivingEntity) slotContext.entity());
        }
        super.onEquip(slotContext, prevStack, stack);
    }
    
    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        if (slotContext.entity() instanceof LivingEntity) {
            removeHeadshotMultiplierEffect((LivingEntity) slotContext.entity());
        }
        super.onUnequip(slotContext, newStack, stack);
    }
    
    /**
     * 应用爆头倍率效果
     */
    private void applyHeadshotMultiplierEffect(LivingEntity entity) {
        // 获取实体的属性系统
        var attributes = entity.getAttributes();
        
        // 获取爆头倍率属性
        var headshotMultiplierAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new net.minecraft.resources.ResourceLocation("taa", "headshot_multiplier")
            )
        );
        
        if (headshotMultiplierAttribute != null) {
            // 移除旧的修饰符（如果有）
            headshotMultiplierAttribute.removeModifier(HEADSHOT_MULTIPLIER_MODIFIER_UUID);
            
            // 获取配置中的爆头倍率加成值
            double multiplierBoost = TaczCuriosConfig.COMMON.uralWolfTagHeadshotMultiplierBoost.get();
            // 添加配置中的爆头倍率加成
            AttributeModifier modifier = new AttributeModifier(
                HEADSHOT_MULTIPLIER_MODIFIER_UUID,
                "tcc.ural_wolf_tag.headshot_multiplier",
                multiplierBoost,
                AttributeModifier.Operation.ADDITION
            );
            
            headshotMultiplierAttribute.addPermanentModifier(modifier);
        }
    }
    
    /**
     * 移除爆头倍率效果
     */
    private void removeHeadshotMultiplierEffect(LivingEntity entity) {
        // 获取实体的属性系统
        var attributes = entity.getAttributes();
        
        // 获取爆头倍率属性
        var headshotMultiplierAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new net.minecraft.resources.ResourceLocation("taa", "headshot_multiplier")
            )
        );
        
        if (headshotMultiplierAttribute != null) {
            // 移除修饰符
            headshotMultiplierAttribute.removeModifier(HEADSHOT_MULTIPLIER_MODIFIER_UUID);
        }
    }

    /**
     * 添加物品的悬浮提示信息（鼠标悬停时显示）
     */
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        // 添加物品描述
        tooltip.add(Component.translatable("item.tcc.ural_wolf_tag.desc")
            .withStyle(net.minecraft.ChatFormatting.GRAY));
        
        // 添加空行分隔
        tooltip.add(Component.literal(""));
        
        // 添加装备效果
        double multiplierBoost = TaczCuriosConfig.COMMON.uralWolfTagHeadshotMultiplierBoost.get() * 100;
        tooltip.add(Component.translatable("item.tcc.ural_wolf_tag.effect", String.format("%.0f", multiplierBoost))
            .withStyle(net.minecraft.ChatFormatting.LIGHT_PURPLE));
        
        // 添加饰品槽位信息
        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable("tcc.tooltip.slot"));
        
        // 添加稀有度提示
        tooltip.add(Component.translatable("tcc.tooltip.rarity.uncommon"));
    }
    
    /**
     * 当生物切换武器时应用效果
     */
    @Override
    public void applyGunSwitchEffect(LivingEntity livingEntity) {
        applyHeadshotMultiplierEffect(livingEntity);
    }
}