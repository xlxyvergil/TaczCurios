package com.xlxyvergil.tcc.items;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import net.minecraft.network.chat.Component;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
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
 * 效果：增5%射速（乘算）增.2穿透（加算
 */
public class RippingPrime extends ItemBaseCurio {
    
    // 属性修饰符UUID - 用于唯一标识这些修饰
    private static final UUID ROUNDS_PER_MINUTE_UUID = UUID.fromString("e3eb5b32-fdfc-47ca-988c-a82d9d8173a7");
    private static final UUID PIERCE_UUID = UUID.fromString("269dbf48-02f5-43f9-a4f2-50bf03aa10a6");
    
    // 修饰符名
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
        
        // 给生物添加属性加
        applyEffects((LivingEntity) slotContext.entity());
    }
    
    /**
     * 当饰品被卸下时调用
     */
    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        super.onUnequip(slotContext, newStack, stack);
        
        // 移除生物的属性加
        removeEffects((LivingEntity) slotContext.entity());
    }
    
    /**
     * 检查是否可以装备到指定插槽
     * 只能装备到tcc_slot槽位
     */
    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        // 检查是否装备在指定的槽
        return "tcc_slot".equals(slotContext.identifier());
    }
    
    /**
     * 返回饰品槽位ID
     */
    public String getSlot() {
        return "tcc:tcc_slot";
    }
    
    /**
     * 应用所有效果加
     * 增加配置中的射速和穿透（射速乘算，穿透加算）
     */
    private void applyEffects(LivingEntity livingEntity) {
        // 获取配置中的射速加成和穿透加成
        double fireRateBoost = TaczCuriosConfig.COMMON.rippingPrimeFireRateBoost.get();
        double penetrationBoost = TaczCuriosConfig.COMMON.rippingPrimePenetrationBoost.get();
        
        // 应用射速加
        applyAttributeModifier(livingEntity, "taa", "rounds_per_minute", fireRateBoost, ROUNDS_PER_MINUTE_UUID, ROUNDS_PER_MINUTE_NAME, AttributeModifier.Operation.MULTIPLY_BASE);
        
        // 应用穿透加
        applyAttributeModifier(livingEntity, "taa", "pierce", penetrationBoost, PIERCE_UUID, PIERCE_NAME, AttributeModifier.Operation.ADDITION);
    }
    
    /**
     * 通用的属性修饰符应用方法
     */
    private void applyAttributeModifier(LivingEntity livingEntity, String namespace, String attributeName, double value, UUID uuid, String modifierName, AttributeModifier.Operation operation) {
        var attributes = livingEntity.getAttributes();
        var attribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new ResourceLocation(namespace, attributeName)
            )
        );
        
        if (attribute != null) {
            // 检查是否已经存在相同的修饰符，如果存在则移
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
     * 移除所有效果加
     */
    private void removeEffects(LivingEntity livingEntity) {
        removeAttributeModifier(livingEntity, "taa", "rounds_per_minute", ROUNDS_PER_MINUTE_UUID);
        removeAttributeModifier(livingEntity, "taa", "pierce", PIERCE_UUID);
    }
    
    /**
     * 通用的属性修饰符移除方法
     */
    private void removeAttributeModifier(LivingEntity livingEntity, String namespace, String attributeName, UUID uuid) {
        var attributes = livingEntity.getAttributes();
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
     * 当生物持有时，每tick更新效果
     */
    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        // 确保效果持续生效
        applyEffects((LivingEntity) slotContext.entity());
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
     * 当生物切换武器时应用效果
     */
    @Override
    public void applyGunSwitchEffect(LivingEntity livingEntity) {
        applyEffects(livingEntity);
    }
}
