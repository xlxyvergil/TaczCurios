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
 * 压迫点 - 提升120%近战伤害
 * 效果：提升120%近战伤害（加算）
 */
public class OppressionPoint extends ItemBaseCurio {
    
    // 属性修饰符UUID - 用于唯一标识这个修饰符
    private static final UUID MELEE_DAMAGE_UUID = UUID.fromString("1f7eab00-eb00-4941-9404-4fdd3eb10515");
    
    // 修饰符名称
    private static final String MELEE_DAMAGE_NAME = "tcc.oppression_point.melee_damage";
    
    // 效果参数
    private static final double MELEE_DAMAGE_BOOST = 1.20; // 120%加成
    
    public OppressionPoint(Properties properties) {
        super(properties);
    }
    
    /**
     * 检查是否可以装备到指定插槽
     * OppressionPoint与OppressionPointPrime互斥，不能同时装备
     */
    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        // 检查是否装备在TCC饰品槽位
        if (!slotContext.identifier().equals("tcc_slot")) {
            return false;
        }
        
        // 检查是否已经装备了OppressionPointPrime
        return !top.theillusivec4.curios.api.CuriosApi.getCuriosInventory(slotContext.entity())
            .map(inv -> inv.findFirstCurio(
                itemStack -> itemStack.getItem() instanceof OppressionPointPrime))
            .orElse(java.util.Optional.empty()).isPresent();
    }
    
    /**
     * 当饰品被装备时调用
     */
    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        applyOppressionPointEffects((LivingEntity) slotContext.entity());
    }
    
    /**
     * 当饰品被卸下时调用
     */
    @Override
    public void onUnequip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        removeOppressionPointEffects((LivingEntity) slotContext.entity());
    }
    
    /**
     * 应用压迫点效果
     * 给生物添加120%的近战伤害加成（加算）
     */
    private void applyOppressionPointEffects(LivingEntity livingEntity) {
        var attributes = livingEntity.getAttributes();
        
        // 近战伤害属性
        var meleeDamageAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new ResourceLocation("taa", "melee_damage")
            )
        );
        
        if (meleeDamageAttribute != null) {
            // 检查是否已经存在相同的修饰符，如果存在则移除
            meleeDamageAttribute.removeModifier(MELEE_DAMAGE_UUID);
            
            // 添加120%的近战伤害加成（加算）
            var meleeDamageModifier = new AttributeModifier(
                MELEE_DAMAGE_UUID,
                MELEE_DAMAGE_NAME,
                MELEE_DAMAGE_BOOST,
                AttributeModifier.Operation.ADDITION
            );
            meleeDamageAttribute.addPermanentModifier(meleeDamageModifier);
        }
    }
    
    /**
     * 移除压迫点效果
     */
    private void removeOppressionPointEffects(LivingEntity livingEntity) {
        var attributes = livingEntity.getAttributes();
        
        // 近战伤害属性
        var meleeDamageAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new ResourceLocation("taa", "melee_damage")
            )
        );
        
        if (meleeDamageAttribute != null) {
            meleeDamageAttribute.removeModifier(MELEE_DAMAGE_UUID);
        }
    }
    
    /**
     * 添加物品的悬浮提示信息（鼠标悬停时显示）
     */
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        
        // 添加物品描述
        tooltip.add(Component.translatable("item.tcc.oppression_point.desc")
            .withStyle(net.minecraft.ChatFormatting.GRAY));
        
        // 添加空行分隔
        tooltip.add(Component.literal(""));
        
        // 添加装备效果
        double meleeDamageBoost = TaczCuriosConfig.COMMON.oppressionPointMeleeDamageBoost.get() * 100;
        tooltip.add(Component.translatable("item.tcc.oppression_point.effect", String.format("%.0f", meleeDamageBoost))
            .withStyle(net.minecraft.ChatFormatting.LIGHT_PURPLE));
        
        // 添加饰品槽位信息
        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable("tcc.tooltip.slot"));
        
        // 添加稀有度提示
        tooltip.add(Component.translatable("tcc.tooltip.rarity.common"));
    }
}