package com.xlxyvergil.tcc.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.resources.ResourceLocation;
import top.theillusivec4.curios.api.SlotContext;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * 剑风 - 提升1.1近战距离
 * 效果：提升1.1近战距离（加算）
 */
public class SwordWind extends ItemBaseCurio {
    
    // 属性修饰符UUID - 用于唯一标识这个修饰符
    private static final UUID MELEE_DISTANCE_UUID = UUID.fromString("88888888-1234-1234-1234-123456789abe");
    
    // 修饰符名称
    private static final String MELEE_DISTANCE_NAME = "tcc.sword_wind.melee_distance";
    
    // 效果参数
    private static final double MELEE_DISTANCE_BOOST = 1.1; // 1.1加成
    
    public SwordWind(Properties properties) {
        super(properties);
    }
    
    /**
     * 当饰品被装备时调用
     */
    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        if (slotContext.entity() instanceof Player player) {
            applySwordWindEffects(player);
        }
    }
    
    /**
     * 当饰品被卸下时调用
     */
    @Override
    public void onUnequip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        if (slotContext.entity() instanceof Player player) {
            removeSwordWindEffects(player);
        }
    }
    
    /**
     * 应用剑风效果
     * 给玩家添加1.1的近战距离加成（加算）
     */
    private void applySwordWindEffects(Player player) {
        var attributes = player.getAttributes();
        
        // 近战距离属性
        var meleeDistanceAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new ResourceLocation("taa", "melee_distance")
            )
        );
        
        if (meleeDistanceAttribute != null) {
            // 检查是否已经存在相同的修饰符，如果存在则移除
            meleeDistanceAttribute.removeModifier(MELEE_DISTANCE_UUID);
            
            // 添加1.1的近战距离加成（加算）
            var meleeDistanceModifier = new AttributeModifier(
                MELEE_DISTANCE_UUID,
                MELEE_DISTANCE_NAME,
                MELEE_DISTANCE_BOOST,
                AttributeModifier.Operation.ADDITION
            );
            meleeDistanceAttribute.addPermanentModifier(meleeDistanceModifier);
        }
    }
    
    /**
     * 移除剑风效果
     */
    private void removeSwordWindEffects(Player player) {
        var attributes = player.getAttributes();
        
        // 近战距离属性
        var meleeDistanceAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new ResourceLocation("taa", "melee_distance")
            )
        );
        
        if (meleeDistanceAttribute != null) {
            meleeDistanceAttribute.removeModifier(MELEE_DISTANCE_UUID);
        }
    }
    
    /**
     * 添加物品的悬浮提示信息（鼠标悬停时显示）
     */
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        
        // 添加物品描述
        tooltip.add(Component.translatable("item.tcc.sword_wind.desc")
            .withStyle(net.minecraft.ChatFormatting.GRAY));
        
        // 添加空行分隔
        tooltip.add(Component.literal(""));
        
        // 添加装备效果
        tooltip.add(Component.translatable("item.tcc.sword_wind.effect")
            .withStyle(net.minecraft.ChatFormatting.LIGHT_PURPLE));
    }
}