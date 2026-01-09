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
 * 三饰- 提供持枪移动速度加成
 * 通过TaczAttributeAdd的move_speed属性实
 */
public class RedMovementTag extends ItemBaseCurio {
    
    // 移动速度修饰符的UUID（确保唯一性）
    private static final UUID MOVE_SPEED_MODIFIER_UUID = UUID.fromString("e3ab07c7-2719-4942-99c7-526d58ba3736");
    
    public RedMovementTag(Properties properties) {
        super(properties);
    }
    
    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        if (slotContext.entity() instanceof LivingEntity) {
            applyMovementSpeedEffect((LivingEntity) slotContext.entity());
        }
        super.onEquip(slotContext, prevStack, stack);
    }
    
    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        if (slotContext.entity() instanceof LivingEntity) {
            removeMovementSpeedEffect((LivingEntity) slotContext.entity());
        }
        super.onUnequip(slotContext, newStack, stack);
    }
    
    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        // 每tick检查并确保移动速度效果生效
        ensureMovementSpeedEffect((LivingEntity) slotContext.entity());
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
            // 获取配置中的移动速度加成
            double speedBoost = TaczCuriosConfig.COMMON.redMovementTagSpeedBoost.get();
            moveSpeedAttribute.addTransientModifier(
                new AttributeModifier(
                    MOVE_SPEED_MODIFIER_UUID,
                    "tcc_red_movement_speed_boost",
                    speedBoost, // 配置中的移动速度加成
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
                // 获取配置中的移动速度加成
                double speedBoost = TaczCuriosConfig.COMMON.redMovementTagSpeedBoost.get();
                moveSpeedAttribute.addTransientModifier(
                    new AttributeModifier(
                        MOVE_SPEED_MODIFIER_UUID,
                        "tcc_red_movement_speed_boost",
                        speedBoost,
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
        double speedBoost = TaczCuriosConfig.COMMON.redMovementTagSpeedBoost.get() * 100;
        tooltip.add(Component.translatable("item.tcc.red_movement_tag.effect", String.format("%.0f", speedBoost))
            .withStyle(net.minecraft.ChatFormatting.LIGHT_PURPLE));
        
        // 添加饰品槽位信息
        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable("tcc.tooltip.slot"));
        
        // 添加稀有度提示
        tooltip.add(Component.translatable("tcc.tooltip.rarity.uncommon"));
    }
    
    /**
     * 当玩家切换武器时应用效果
     */
    @Override
    public void applyGunSwitchEffect(LivingEntity livingEntity) {
        applyMovementSpeedEffect(livingEntity);
    }
}
