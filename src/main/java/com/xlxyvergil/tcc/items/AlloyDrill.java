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
 * 合金钻头 - 提升200%穿透能力
 * 效果：穿透能力+200%
 */
public class AlloyDrill extends ItemBaseCurio {
    
    // 属性修饰符UUID - 用于唯一标识这些修饰符
    private static final UUID PIERCE_UUID = UUID.fromString("62345678-1234-1234-1234-123456789abc");
    
    // 修饰符名称
    private static final String PIERCE_NAME = "tcc.alloy_drill.pierce";
    
    // 效果参数
    private static final double PIERCE_BOOST = 2.0;       // 200%穿透能力提升
    
    public AlloyDrill(Properties properties) {
        super(properties);
    }
    
    /**
     * 当饰品被装备时调用
     */
    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        super.onEquip(slotContext, prevStack, stack);
        
        // 给玩家添加穿透能力属性加成
        if (slotContext.entity() instanceof Player player) {
            applyDrillEffects(player);
        }
    }
    
    /**
     * 当饰品被卸下时调用
     */
    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        super.onUnequip(slotContext, newStack, stack);
        
        // 移除玩家的穿透能力属性加成
        if (slotContext.entity() instanceof Player player) {
            removeDrillEffects(player);
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
     * 应用钻头效果
     * 提升穿透能力
     */
    private void applyDrillEffects(Player player) {
        var attributes = player.getAttributes();
        
        // 应用穿透能力提升
        var pierceAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new net.minecraft.resources.ResourceLocation("taa", "pierce")
            )
        );
        
        if (pierceAttribute != null) {
            // 检查是否已经存在相同的修饰符，如果存在则移除
            pierceAttribute.removeModifier(PIERCE_UUID);
            
            // 添加200%的穿透能力加成
            var pierceModifier = new net.minecraft.world.entity.ai.attributes.AttributeModifier(
                PIERCE_UUID,
                PIERCE_NAME,
                PIERCE_BOOST,
                net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADDITION
            );
            pierceAttribute.addPermanentModifier(pierceModifier);
        }
    }
    
    /**
     * 移除钻头效果
     */
    private void removeDrillEffects(Player player) {
        var attributes = player.getAttributes();
        
        // 移除穿透能力加成
        var pierceAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new net.minecraft.resources.ResourceLocation("taa", "pierce")
            )
        );
        
        if (pierceAttribute != null) {
            pierceAttribute.removeModifier(PIERCE_UUID);
        }
    }
    
    /**
     * 当玩家持有时，每tick更新效果
     */
    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        // 确保效果持续生效
        if (slotContext.entity() instanceof Player player) {
            applyDrillEffects(player);
        }
    }
    
    /**
     * 当物品被装备时，显示提示信息
     */
    @Override
    public void onEquipFromUse(SlotContext slotContext, ItemStack stack) {
        if (slotContext.entity() instanceof Player player) {
            player.displayClientMessage(
                net.minecraft.network.chat.Component.literal(
                    "§6合金钻头已装备 - 穿透能力+200%"
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
        tooltip.add(Component.translatable("item.tcc.alloy_drill.desc")
            .withStyle(net.minecraft.ChatFormatting.GRAY));
        
        // 添加空行分隔
        tooltip.add(Component.literal(""));
        
        // 添加装备效果
        tooltip.add(Component.translatable("item.tcc.alloy_drill.effect")
            .withStyle(net.minecraft.ChatFormatting.LIGHT_PURPLE));
        
        // 添加饰品槽位信息
        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("§7装备槽位：§aTCC饰品栏")
            .withStyle(net.minecraft.ChatFormatting.GRAY));
        
        // 添加稀有度提示
        tooltip.add(Component.literal("§7稀有度：§6稀有")
            .withStyle(net.minecraft.ChatFormatting.GRAY));
    }
    
    /**
     * 获取装备槽位
     */
    public String getSlot() {
        return "tcc:tcc_slot";
    }
}