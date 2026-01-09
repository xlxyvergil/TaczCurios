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
 * 烈焰风暴饰品
 * 效果：增加爆炸范围（乘算），增加爆炸伤害（乘算）
 */
public class BlazeStorm extends ItemBaseCurio {
    
    // 属性修饰符UUID - 用于唯一标识这些修饰符
    private static final UUID EXPLOSION_RADIUS_UUID = UUID.fromString("aceef087-5474-41ce-89a5-6429feffdcbc");
    private static final UUID EXPLOSION_DAMAGE_UUID = UUID.fromString("5e9bcd94-dfa4-4531-8861-0856b379ac6a");
    private static final UUID EXPLOSION_ENABLED_UUID = UUID.fromString("5e9bcd94-dfa4-89a5-8861-0856b379ac6a");
    
    // 修饰符名称
    private static final String EXPLOSION_RADIUS_NAME = "tcc.blaze_storm.explosion_radius";
    private static final String EXPLOSION_DAMAGE_NAME = "tcc.blaze_storm.explosion_damage";
    private static final String EXPLOSION_ENABLED_NAME = "tcc.blaze_storm.explosion_enabled";
    
    public BlazeStorm(Properties properties) {
        super(properties);
    }
    
    /**
     * 当饰品被装备时调用
     */
    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        super.onEquip(slotContext, prevStack, stack);
        
        // 给生物添加属性加成
        applyBlazeStormEffects((LivingEntity) slotContext.entity());
    }
    
    /**
     * 当饰品被卸下时调用
     */
    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        super.onUnequip(slotContext, newStack, stack);
        
        // 移除生物的属性加成
        removeBlazeStormEffects((LivingEntity) slotContext.entity());
    }
    
    /**
     * 检查是否可以装备到指定插槽
     * 只能装备到tcc_slot槽位
     * 与BlazeStormPrime互斥，不能同时装备
     */
    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        // 检查是否装备在指定的槽位
        if (!"tcc_slot".equals(slotContext.identifier())) {
            return false;
        }
        
        // 检查是否已经装备了BlazeStormPrime
        return !top.theillusivec4.curios.api.CuriosApi.getCuriosInventory(slotContext.entity())
            .map(inv -> inv.findFirstCurio(
                itemStack -> itemStack.getItem() instanceof BlazeStormPrime))
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
     * 增加配置中的爆炸范围和爆炸伤害加成（乘算）
     */
    private void applyBlazeStormEffects(LivingEntity livingEntity) {
        // 获取配置中的爆炸范围、爆炸伤害和爆炸启用属性值
        double explosionRadiusBoost = TaczCuriosConfig.COMMON.blazeStormExplosionRadiusBoost.get();
        double explosionDamageBoost = TaczCuriosConfig.COMMON.blazeStormExplosionDamageBoost.get();
        double explosionEnabled = TaczCuriosConfig.COMMON.blazeStormExplosionEnabled.get();
        
        // 应用爆炸范围加成
        applyAttributeModifier(livingEntity, "taa", "explosion_radius", explosionRadiusBoost, EXPLOSION_RADIUS_UUID, EXPLOSION_RADIUS_NAME);
        
        // 应用爆炸伤害加成
        applyAttributeModifier(livingEntity, "taa", "explosion_damage", explosionDamageBoost, EXPLOSION_DAMAGE_UUID, EXPLOSION_DAMAGE_NAME);
        
        // 应用爆炸启用属性
        applyAttributeModifier(livingEntity, "taa", "explosion_enabled", explosionEnabled, EXPLOSION_ENABLED_UUID, EXPLOSION_ENABLED_NAME);
    }
    
    /**
     * 通用的属性修饰符应用方法
     */
    private void applyAttributeModifier(LivingEntity livingEntity, String namespace, String attributeName, double multiplier, UUID uuid, String modifierName) {
        var attributes = livingEntity.getAttributes();
        var attribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
new ResourceLocation(namespace, attributeName)
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
    private void removeBlazeStormEffects(LivingEntity livingEntity) {
        removeAttributeModifier(livingEntity, "taa", "explosion_radius", EXPLOSION_RADIUS_UUID);
        removeAttributeModifier(livingEntity, "taa", "explosion_damage", EXPLOSION_DAMAGE_UUID);
        removeAttributeModifier(livingEntity, "taa", "explosion_enabled", EXPLOSION_ENABLED_UUID);
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
     * 当玩家持有时，每tick更新效果
     */
    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        // 确保效果持续生效
        applyBlazeStormEffects((LivingEntity) slotContext.entity());
    }

    /**
     * 添加物品的悬浮提示信息（鼠标悬停时显示）
     */
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        
        // 添加物品描述
        tooltip.add(Component.translatable("item.tcc.blaze_storm.desc")
            .withStyle(net.minecraft.ChatFormatting.GRAY));
        
        // 添加空行分隔
        tooltip.add(Component.literal(""));
        
        // 添加装备效果
        double explosionRadiusBoost = TaczCuriosConfig.COMMON.blazeStormExplosionRadiusBoost.get() * 100;
        double explosionDamageBoost = TaczCuriosConfig.COMMON.blazeStormExplosionDamageBoost.get() * 100;
        tooltip.add(Component.translatable("item.tcc.blaze_storm.effect", 
                String.format("%.0f", explosionRadiusBoost), String.format("%.0f", explosionDamageBoost))
            .withStyle(net.minecraft.ChatFormatting.LIGHT_PURPLE));
        
        // 添加饰品槽位信息
        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable("tcc.tooltip.slot"));
        
        // 添加稀有度提示
        tooltip.add(Component.translatable("tcc.tooltip.rarity.rare"));
    }
    
    /**
     * 当玩家切换武器时应用效果
     */
    @Override
    public void applyGunSwitchEffect(LivingEntity livingEntity) {
        applyBlazeStormEffects(livingEntity);
    }
}