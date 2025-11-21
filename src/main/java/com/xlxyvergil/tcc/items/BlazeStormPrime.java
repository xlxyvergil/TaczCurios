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
 * 烈焰风暴Prime饰品
 * 效果：增加66%爆炸范围（乘算），增加66%爆炸伤害（乘算）
 */
public class BlazeStormPrime extends ItemBaseCurio {
    
    // 属性修饰符UUID - 用于唯一标识这些修饰符
    private static final UUID EXPLOSION_RADIUS_UUID = UUID.fromString("22222222-1234-1234-1234-123456789abc");
    private static final UUID EXPLOSION_DAMAGE_UUID = UUID.fromString("22222222-1234-1234-1234-123456789abd");
    
    // 修饰符名称
    private static final String EXPLOSION_RADIUS_NAME = "tcc.blaze_storm_prime.explosion_radius";
    private static final String EXPLOSION_DAMAGE_NAME = "tcc.blaze_storm_prime.explosion_damage";
    
    public BlazeStormPrime(Properties properties) {
        super(properties);
    }
    
    /**
     * 当饰品被装备时调用
     */
    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        super.onEquip(slotContext, prevStack, stack);
        
        // 给玩家添加属性加成
        if (slotContext.entity() instanceof Player player) {
            applyEffects(player);
        }
    }
    
    /**
     * 当饰品被卸下时调用
     */
    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        super.onUnequip(slotContext, newStack, stack);
        
        // 移除玩家的属性加成
        if (slotContext.entity() instanceof Player player) {
            removeEffects(player);
        }
    }
    
    /**
     * 检查是否可以装备到指定插槽
     * 只能装备到tcc_slot槽位
     * 与BlazeStorm互斥，不能同时装备
     */
    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        // 检查是否装备在指定的槽位
        if (!"tcc_slot".equals(slotContext.identifier())) {
            return false;
        }
        
        // 检查是否已经装备了BlazeStorm
        return !top.theillusivec4.curios.api.CuriosApi.getCuriosInventory(slotContext.entity())
            .map(inv -> inv.findFirstCurio(
                itemStack -> itemStack.getItem() instanceof BlazeStorm))
            .orElse(java.util.Optional.empty()).isPresent();
    }
    
    /**
     * 返回饰品槽位ID
     */
    public String getSlot() {
        return "tcc:tcc_slot";
    }
    
    /**
     * 应用所有效果加成
     * 增加66%爆炸范围（乘算），增加66%爆炸伤害（乘算）
     */
    private void applyEffects(Player player) {
        // 应用爆炸范围加成 (66%乘算)
        applyAttributeModifier(player, "taa", "explosion_radius", 0.66, EXPLOSION_RADIUS_UUID, EXPLOSION_RADIUS_NAME);
        
        // 应用爆炸伤害加成 (66%乘算)
        applyAttributeModifier(player, "taa", "explosion_damage", 0.66, EXPLOSION_DAMAGE_UUID, EXPLOSION_DAMAGE_NAME);
    }
    
    /**
     * 通用的属性修饰符应用方法
     */
    private void applyAttributeModifier(Player player, String namespace, String attributeName, double multiplier, UUID uuid, String modifierName) {
        var attributes = player.getAttributes();
        var attribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
ResourceLocation.fromNamespaceAndPath(namespace, attributeName)
            )
        );
        
        if (attribute != null) {
            // 检查是否已经存在相同的修饰符，如果存在则移除
            attribute.removeModifier(uuid);
            
            // 添加乘算的属性修饰符
            AttributeModifier modifier = new AttributeModifier(
                uuid,
                modifierName,
                multiplier,
                AttributeModifier.Operation.MULTIPLY_BASE
            );
            attribute.addPermanentModifier(modifier);
        }
    }
    
    /**
     * 移除所有效果加成
     */
    private void removeEffects(Player player) {
        removeAttributeModifier(player, "taa", "explosion_radius", EXPLOSION_RADIUS_UUID);
        removeAttributeModifier(player, "taa", "explosion_damage", EXPLOSION_DAMAGE_UUID);
    }
    
    /**
     * 通用的属性修饰符移除方法
     */
    private void removeAttributeModifier(Player player, String namespace, String attributeName, UUID uuid) {
        var attributes = player.getAttributes();
        var attribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                ResourceLocation.fromNamespaceAndPath(namespace, attributeName)
            )
        );
        
        if (attribute != null) {
            attribute.removeModifier(uuid);
        }
    }
    
    /**
     * 添加物品的悬浮提示信息（鼠标悬停时显示）
     */
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        
        // 添加物品描述
        tooltip.add(Component.translatable("item.tcc.blaze_storm_prime.desc")
            .withStyle(net.minecraft.ChatFormatting.GRAY));
        
        // 添加空行分隔
        tooltip.add(Component.literal(""));
        
        // 添加装备效果
        tooltip.add(Component.translatable("item.tcc.blaze_storm_prime.effect")
            .withStyle(net.minecraft.ChatFormatting.LIGHT_PURPLE));
        
        // 添加饰品槽位信息
        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("§7装备槽位：§aTCC饰品栏")
            .withStyle(net.minecraft.ChatFormatting.GRAY));
        
        // 添加稀有度提示
        tooltip.add(Component.literal("§7稀有度：§f传说")
            .withStyle(net.minecraft.ChatFormatting.GRAY));
    }
}