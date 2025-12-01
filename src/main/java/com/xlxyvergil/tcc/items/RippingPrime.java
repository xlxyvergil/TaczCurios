package com.xlxyvergil.tcc.items;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
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
 * 撕裂Prime饰品
 * 效果：增加55%射速（乘算）增加2.2穿透（加算）
 */
public class RippingPrime extends ItemBaseCurio {
    
    // 属性修饰符UUID - 用于唯一标识这些修饰符
    private static final UUID ROUNDS_PER_MINUTE_UUID = UUID.fromString("33333333-1234-1234-1234-123456789abc");
    private static final UUID PIERCE_UUID = UUID.fromString("33333333-1234-1234-1234-123456789abd");
    
    // 修饰符名称
    private static final String ROUNDS_PER_MINUTE_NAME = "tcc.ripping_prime.rounds_per_minute";
    private static final String PIERCE_NAME = "tcc.ripping_prime.pierce";
    
    public RippingPrime(Properties properties) {
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
     */
    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        // 检查是否装备在指定的槽位
        return "tcc_slot".equals(slotContext.identifier());
    }
    
    /**
     * 返回饰品槽位ID
     */
    public String getSlot() {
        return "tcc:tcc_slot";
    }
    
    /**
     * 应用所有效果加成
     * 增加配置中的射速和穿透（射速乘算，穿透加算）
     */
    private void applyEffects(Player player) {
        // 获取配置中的射速加成和穿透加成值
        double fireRateBoost = TaczCuriosConfig.COMMON.rippingPrimeFireRateBoost.get();
        double penetrationBoost = TaczCuriosConfig.COMMON.rippingPrimePenetrationBoost.get();
        
        // 应用射速加成
        applyAttributeModifier(player, "taa", "rounds_per_minute", fireRateBoost, ROUNDS_PER_MINUTE_UUID, ROUNDS_PER_MINUTE_NAME, AttributeModifier.Operation.MULTIPLY_BASE);
        
        // 应用穿透加成
        applyAttributeModifier(player, "taa", "pierce", penetrationBoost, PIERCE_UUID, PIERCE_NAME, AttributeModifier.Operation.ADDITION);
    }
    
    /**
     * 通用的属性修饰符应用方法
     */
    private void applyAttributeModifier(Player player, String namespace, String attributeName, double value, UUID uuid, String modifierName, AttributeModifier.Operation operation) {
        var attributes = player.getAttributes();
        var attribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
new ResourceLocation(namespace, attributeName)
            )
        );
        
        if (attribute != null) {
            // 检查是否已经存在相同的修饰符，如果存在则移除
            attribute.removeModifier(uuid);
            
            // 添加属性修饰符
            AttributeModifier modifier = new AttributeModifier(
                uuid,
                modifierName,
                value,
                operation
            );
            attribute.addPermanentModifier(modifier);
        }
    }
    
    /**
     * 移除所有效果加成
     */
    private void removeEffects(Player player) {
        removeAttributeModifier(player, "taa", "rounds_per_minute", ROUNDS_PER_MINUTE_UUID);
        removeAttributeModifier(player, "taa", "pierce", PIERCE_UUID);
    }
    
    /**
     * 通用的属性修饰符移除方法
     */
    private void removeAttributeModifier(Player player, String namespace, String attributeName, UUID uuid) {
        var attributes = player.getAttributes();
        var attribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new ResourceLocation(namespace, attributeName)
            )
        );
        
        if (attribute != null) {
            attribute.removeModifier(uuid);
        }
    }
    
    /**
     * 当玩家持有时，每tick更新效果
     */
    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        // 确保效果持续生效
        if (slotContext.entity() instanceof Player player) {
            applyEffects(player);
        }
    }

    /**
     * 添加物品的悬浮提示信息（鼠标悬停时显示）
     */
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        
        // 添加物品描述
        tooltip.add(Component.translatable("item.tcc.ripping_prime.desc")
            .withStyle(net.minecraft.ChatFormatting.GRAY));
        
        // 添加空行分隔
        tooltip.add(Component.literal(""));
        
        // 添加装备效果
        double fireRateBoost = TaczCuriosConfig.COMMON.rippingPrimeFireRateBoost.get() * 100;
        double penetrationBoost = TaczCuriosConfig.COMMON.rippingPrimePenetrationBoost.get();
        tooltip.add(Component.translatable("item.tcc.ripping_prime.effect", 
                String.format("%.0f", fireRateBoost), String.format("%.1f", penetrationBoost))
            .withStyle(net.minecraft.ChatFormatting.LIGHT_PURPLE));
        
        // 添加饰品槽位信息
        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable("tcc.tooltip.slot"));
        
        // 添加稀有度提示
        tooltip.add(Component.translatable("tcc.tooltip.rarity.legendary"));
    }
    
    /**
     * 当玩家切换武器时应用效果
     */
    @Override
    public void applyGunSwitchEffect(Player player) {
        applyEffects(player);
    }
}