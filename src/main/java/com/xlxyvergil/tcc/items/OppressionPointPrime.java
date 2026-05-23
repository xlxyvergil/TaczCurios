package com.xlxyvergil.tcc.items;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * 压迫点Prime - 提升近战伤害
 * 效果：提升近战伤害（加算）
 */
public class OppressionPointPrime extends ItemBaseCurio {
    
    // 属性修饰符UUID - 用于唯一标识这个修饰符
    private static final UUID ATTACK_DAMAGE_UUID = UUID.fromString("b4763540-e80b-4bab-9e64-a4a2494d9f5e");
    
    // 修饰符名称
    private static final String ATTACK_DAMAGE_NAME = "tcc.oppression_point_prime.attack_damage";
    
    
    public OppressionPointPrime(Properties properties) {
        super(properties);
    }
    
    /**
     * 检查是否可以装备到指定插槽
     * OppressionPointPrime与OppressionPoint互斥，不能同时装备
     */
    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        // 检查是否装备在TCC饰品槽位
        if (!slotContext.identifier().equals("tcc_slot")) {
            return false;
        }
        
        // 检查是否已经装备了OppressionPoint
        return !top.theillusivec4.curios.api.CuriosApi.getCuriosInventory(slotContext.entity())
            .map(inv -> inv.findFirstCurio(
                itemStack -> itemStack.getItem() instanceof OppressionPoint))
            .orElse(java.util.Optional.empty()).isPresent();
    }
    
    /**
     * 当饰品被装备时调用
     */
    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        applyOppressionPointPrimeEffects((LivingEntity) slotContext.entity());
    }
    
    /**
     * 当饰品被卸下时调用
     */
    @Override
    public void onUnequip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        removeOppressionPointPrimeEffects((LivingEntity) slotContext.entity());
    }
    
    /**
     * 应用压迫点Prime效果
     * 给生物添加近战伤害加成（加算）
     */
    private void applyOppressionPointPrimeEffects(LivingEntity livingEntity) {
        var attributes = livingEntity.getAttributes();
        
        // 攻击伤害属性（原版）
        var attackDamageAttribute = attributes.getInstance(Attributes.ATTACK_DAMAGE);
        
        if (attackDamageAttribute != null) {
            // 检查是否已经存在相同的修饰符，如果存在则移除
            attackDamageAttribute.removeModifier(ATTACK_DAMAGE_UUID);
            
            // 从配置文件获取近战伤害加成
            double damageBoost = TaczCuriosConfig.COMMON.oppressionPointPrimeMeleeDamageBoost.get();
            
            // 添加配置的近战伤害加成（加算）
            var damageModifier = new AttributeModifier(
                ATTACK_DAMAGE_UUID,
                ATTACK_DAMAGE_NAME,
                damageBoost,
                AttributeModifier.Operation.ADDITION
            );
            attackDamageAttribute.addPermanentModifier(damageModifier);
        }
    }
    
    /**
     * 移除压迫点Prime效果
     */
    private void removeOppressionPointPrimeEffects(LivingEntity livingEntity) {
        var attributes = livingEntity.getAttributes();
        
        // 攻击伤害属性（原版）
        var attackDamageAttribute = attributes.getInstance(Attributes.ATTACK_DAMAGE);
        
        if (attackDamageAttribute != null) {
            attackDamageAttribute.removeModifier(ATTACK_DAMAGE_UUID);
        }
    }
    
    /**
     * 添加物品的悬浮提示信息（鼠标悬停时显示）
     */
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        

        
        // 添加空行分隔
        tooltip.add(Component.literal(""));
        
        // 添加装备效果
        double meleeDamageBoost = TaczCuriosConfig.COMMON.oppressionPointPrimeMeleeDamageBoost.get() * 100;
        tooltip.add(Component.translatable("item.tcc.oppression_point_prime.effect", String.format("%+.0f", meleeDamageBoost))
            .withStyle(ChatFormatting.WHITE));
        
        // 添加饰品槽位信息
        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable("tcc.tooltip.slot"));
        
        // 添加稀有度提示
        tooltip.add(Component.translatable("tcc.tooltip.rarity.legendary"));
    }
}