package com.xlxyvergil.tcc.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;



/**
 * 红-有-三饰品 - 提供150%持枪移动速度加成
 * 通过TaczAttributeAdd的move_speed属性实现
 */
public class RedMovementTag extends ItemBaseCurio {
    
    // 移动速度修饰符的UUID（确保唯一性）
    private static final UUID MOVE_SPEED_MODIFIER_UUID = UUID.fromString("12345678-1234-1234-1234-123456789012");
    
    public RedMovementTag(Properties properties) {
        super(properties);
    }
    
    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        if (slotContext.entity() instanceof Player) {
            applyMovementSpeedEffect(slotContext.entity());
        }
        super.onEquip(slotContext, prevStack, stack);
    }
    
    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        if (slotContext.entity() instanceof Player) {
            removeMovementSpeedEffect(slotContext.entity());
        }
        super.onUnequip(slotContext, newStack, stack);
    }
    
    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        // 每tick检查并确保移动速度效果生效
        if (slotContext.entity() instanceof Player player) {
            ensureMovementSpeedEffect(slotContext.entity());
        }
    }
    
    /**
     * 应用移动速度加成效果
     */
    private void applyMovementSpeedEffect(LivingEntity entity) {
        var attributes = entity.getAttributes();
        
        // 应用移动速度加成
        var moveSpeedAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new net.minecraft.resources.ResourceLocation("taa", "move_speed")
            )
        );
        
        if (moveSpeedAttribute != null) {
            // 移除可能存在的旧修饰符，然后添加新的
            moveSpeedAttribute.removeModifier(MOVE_SPEED_MODIFIER_UUID);
            moveSpeedAttribute.addTransientModifier(
                new AttributeModifier(
                    MOVE_SPEED_MODIFIER_UUID,
                    "tcc_red_movement_speed_boost",
                    1.50, // 150%移动速度加成
                    AttributeModifier.Operation.ADDITION
                )
            );
        }
    }
    
    /**
     * 移除移动速度加成效果
     */
    private void removeMovementSpeedEffect(LivingEntity entity) {
        var attributes = entity.getAttributes();
        
        // 移除移动速度加成
        var moveSpeedAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new net.minecraft.resources.ResourceLocation("taa", "move_speed")
            )
        );
        
        if (moveSpeedAttribute != null) {
            moveSpeedAttribute.removeModifier(MOVE_SPEED_MODIFIER_UUID);
        }
    }
    
    /**
     * 确保移动速度效果持续生效
     */
    private void ensureMovementSpeedEffect(LivingEntity entity) {
        var attributes = entity.getAttributes();
        
        var moveSpeedAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new net.minecraft.resources.ResourceLocation("taa", "move_speed")
            )
        );
        
        if (moveSpeedAttribute != null) {
            // 检查修饰符是否还存在，如果不存在则重新添加
            var modifier = moveSpeedAttribute.getModifier(MOVE_SPEED_MODIFIER_UUID);
            if (modifier == null) {
                moveSpeedAttribute.addTransientModifier(
                    new AttributeModifier(
                        MOVE_SPEED_MODIFIER_UUID,
                        "tcc_red_movement_speed_boost",
                        1.50,
                        AttributeModifier.Operation.ADDITION
                    )
                );
            }
        }
    }
    
    @Override
    public String getDescriptionId(ItemStack stack) {
        return "item.tcc.red_movement_tag";
    }
    
    /**
     * 添加物品的悬浮提示信息（鼠标悬停时显示）
     */
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        // 添加物品描述
        tooltip.add(Component.translatable("item.tcc.red_movement_tag.desc")
            .withStyle(net.minecraft.ChatFormatting.GRAY));
        
        // 添加空行分隔
        tooltip.add(Component.literal(""));
        
        // 添加装备效果
        tooltip.add(Component.translatable("item.tcc.red_movement_tag.effect")
            .withStyle(net.minecraft.ChatFormatting.LIGHT_PURPLE));
        
        // 添加饰品槽位信息
        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("§7装备槽位：§aTCC饰品栏")
            .withStyle(net.minecraft.ChatFormatting.GRAY));
        
        // 添加稀有度提示
        tooltip.add(Component.literal("§7稀有度：§b罕见")
            .withStyle(net.minecraft.ChatFormatting.GRAY));
    }
}