package com.xlxyvergil.tcc.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * 并合膛线 - 提升155%通用伤害，但降低25%持枪移动速度
 * 效果：通用伤害+155%，持枪移动速度-25%
 */
public class MergedRifling extends ItemBaseCurio {
    
    // 属性修饰符UUID - 用于唯一标识这些修饰符
    private static final UUID GENERAL_DAMAGE_UUID = UUID.fromString("52345678-1234-1234-1234-123456789abc");
    private static final UUID MOVEMENT_SPEED_UUID = UUID.fromString("52345678-1234-1234-1234-123456789abd");
    
    // 修饰符名称
    private static final String GENERAL_DAMAGE_NAME = "tcc.merged_rifling.general_damage";
    private static final String MOVEMENT_SPEED_NAME = "tcc.merged_rifling.movement_speed";
    
    // 效果参数
    private static final double DAMAGE_BOOST = 1.55;       // 155%通用伤害提升
    private static final double SPEED_REDUCTION = -0.25;    // 25%持枪移动速度降低
    
    public MergedRifling(Properties properties) {
        super(properties);
    }
    
    /**
     * 当饰品被装备时调用
     */
    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        super.onEquip(slotContext, prevStack, stack);
        
        // 给玩家添加伤害和移动速度属性修改
        if (slotContext.getWearer() instanceof Player player) {
            applyRiflingEffects(player);
        }
    }
    
    /**
     * 当饰品被卸下时调用
     */
    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        super.onUnequip(slotContext, newStack, stack);
        
        // 移除玩家的伤害和移动速度属性修改
        if (slotContext.getWearer() instanceof Player player) {
            removeRiflingEffects(player);
        }
    }
    
    /**
     * 当物品在Curios插槽中时被右键点击
     */
    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return true;
    }
    
    /**
     * 应用膛线效果
     * 提升通用伤害和持枪移动速度
     */
    private void applyRiflingEffects(Player player) {
        var attributes = player.getAttributes();
        
        // 应用通用伤害提升
        var generalDamageAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new net.minecraft.resources.ResourceLocation("taa", "bullet_gundamage")
            )
        );
        
        if (generalDamageAttribute != null) {
            // 检查是否已经存在相同的修饰符，如果存在则移除
            generalDamageAttribute.removeModifier(GENERAL_DAMAGE_UUID);
            
            // 添加155%的通用伤害加成
            var generalDamageModifier = new net.minecraft.world.entity.ai.attributes.AttributeModifier(
                GENERAL_DAMAGE_UUID,
                GENERAL_DAMAGE_NAME,
                DAMAGE_BOOST,
                net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.MULTIPLY_TOTAL
            );
            generalDamageAttribute.addPermanentModifier(generalDamageModifier);
        }
        
        // 应用持枪移动速度降低
        var movementSpeedAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new net.minecraft.resources.ResourceLocation("taa", "move_speed")
            )
        );
        
        if (movementSpeedAttribute != null) {
            // 检查是否已经存在相同的修饰符，如果存在则移除
            movementSpeedAttribute.removeModifier(MOVEMENT_SPEED_UUID);
            
            // 添加25%的持枪移动速度降低
            var movementSpeedModifier = new net.minecraft.world.entity.ai.attributes.AttributeModifier(
                MOVEMENT_SPEED_UUID,
                MOVEMENT_SPEED_NAME,
                -0.25,
                net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.MULTIPLY_TOTAL
            );
            movementSpeedAttribute.addPermanentModifier(movementSpeedModifier);
        }
    }
    
    /**
     * 移除膛线效果
     */
    private void removeRiflingEffects(Player player) {
        var attributes = player.getAttributes();
        
        // 移除通用伤害加成
        var generalDamageAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new net.minecraft.resources.ResourceLocation("taa", "bullet_gundamage")
            )
        );
        
        if (generalDamageAttribute != null) {
            generalDamageAttribute.removeModifier(GENERAL_DAMAGE_UUID);
        }
        
        // 移除持枪移动速度降低
        var movementSpeedAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new net.minecraft.resources.ResourceLocation("taa", "move_speed")
            )
        );
        
        if (movementSpeedAttribute != null) {
            movementSpeedAttribute.removeModifier(MOVEMENT_SPEED_UUID);
        }
    }
    
    /**
     * 当玩家持有时，每tick更新效果
     */
    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        // 确保效果持续生效
        if (slotContext.getWearer() instanceof Player player) {
            applyRiflingEffects(player);
        }
    }
    
    /**
     * 当物品被装备时，显示提示信息
     */
    @Override
    public void onEquipFromUse(SlotContext slotContext, ItemStack stack) {
        if (slotContext.getWearer() instanceof Player player) {
            player.displayClientMessage(
                net.minecraft.network.chat.Component.literal(
                    "§6并合膛线已装备 - 通用伤害+155%，持枪移动速度-25%"
                ),
                true
            );
        }
    }
    
    /**
     * 添加物品的悬浮提示信息（鼠标悬停时显示）
     */
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        
        // 添加物品描述
        tooltip.add(Component.translatable("item.tcc.merged_rifling.desc")
            .withStyle(net.minecraft.ChatFormatting.GRAY));
        
        // 添加空行分隔
        tooltip.add(Component.literal(""));
        
        // 添加效果说明
        tooltip.add(Component.translatable("item.tcc.merged_rifling.effect")
            .withStyle(net.minecraft.ChatFormatting.BLUE));
    }
    
    /**
     * 获取装备槽位
     */
    public String getSlot() {
        return "tcc:tcc_slot";
    }
}