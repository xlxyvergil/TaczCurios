package com.xlxyvergil.tcc.items;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.resources.ResourceLocation;
import top.theillusivec4.curios.api.SlotContext;
import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;



/**
 * 极恶精准饰品
 * 效果：降低90%后坐力，降低36%射速（都加算）
 */
public class EvilAccuracy extends ItemBaseCurio {
    
    // 属性修饰符UUID - 用于唯一标识这些修饰符
    private static final UUID RECOIL_UUID = UUID.fromString("98a8e44a-7d8d-4d10-b934-7e1e1c1c8fca");
    private static final UUID ROUNDS_PER_MINUTE_UUID = UUID.fromString("7da86e2a-9c63-4d3f-8237-feda8559638e");
    
    // 修饰符名称
    private static final String RECOIL_NAME = "tcc.evil_accuracy.recoil";
    private static final String ROUNDS_PER_MINUTE_NAME = "tcc.evil_accuracy.rounds_per_minute";
    
    public EvilAccuracy(Properties properties) {
        super(properties);
    }
    
    /**
     * 当饰品被装备时调用
     */
    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        super.onEquip(slotContext, prevStack, stack);
        
        // 给生物添加属性加成
        applyEffects((LivingEntity) slotContext.entity());
    }
    
    /**
     * 当饰品被卸下时调用
     */
    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        super.onUnequip(slotContext, newStack, stack);
        
        // 移除生物的属性加成
        removeEffects((LivingEntity) slotContext.entity());
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
     * 降低配置中的后坐力和射速（都加算）
     */
    public void applyEffects(LivingEntity livingEntity) {
        // 获取配置中的后坐力降低和射速降低值
        double recoilReduction = TaczCuriosConfig.COMMON.evilAccuracyRecoilReduction.get();
        double fireRateReduction = -TaczCuriosConfig.COMMON.evilAccuracyFireRateReduction.get();
        
        // 应用后坐力降低
        applyAttributeModifier(livingEntity, "taa", "recoil", recoilReduction, RECOIL_UUID, RECOIL_NAME, AttributeModifier.Operation.ADDITION);
        
        // 应用射速降低
        applyAttributeModifier(livingEntity, "taa", "rounds_per_minute", fireRateReduction, ROUNDS_PER_MINUTE_UUID, ROUNDS_PER_MINUTE_NAME, AttributeModifier.Operation.ADDITION);
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
    public void removeEffects(LivingEntity livingEntity) {
        removeAttributeModifier(livingEntity, "taa", "recoil", RECOIL_UUID);
        removeAttributeModifier(livingEntity, "taa", "rounds_per_minute", ROUNDS_PER_MINUTE_UUID);
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
        applyEffects((LivingEntity) slotContext.entity());
    }

    /**
     * 添加物品的悬浮提示信息（鼠标悬停时显示）
     */
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        
        // 添加物品描述
        tooltip.add(Component.translatable("item.tcc.evil_accuracy.desc")
            .withStyle(net.minecraft.ChatFormatting.GRAY));
        
        // 添加空行分隔
        tooltip.add(Component.literal(""));
        
        // 添加装备效果
        double recoilReduction = TaczCuriosConfig.COMMON.evilAccuracyRecoilReduction.get() * 100;
        double fireRateReduction = TaczCuriosConfig.COMMON.evilAccuracyFireRateReduction.get() * 100;
        tooltip.add(Component.translatable("item.tcc.evil_accuracy.effect", 
                String.format("%.0f", recoilReduction), String.format("%.0f", fireRateReduction))
            .withStyle(net.minecraft.ChatFormatting.LIGHT_PURPLE));
        
        // 添加饰品槽位信息
        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable("tcc.tooltip.slot"));
        
        // 添加稀有度提示
        tooltip.add(Component.translatable("tcc.tooltip.rarity.rare"));
    }
    
    /**
     * 当生物切换武器时应用效果
     */
    @Override
    public void applyGunSwitchEffect(LivingEntity livingEntity) {
        applyEffects(livingEntity);
    }
}