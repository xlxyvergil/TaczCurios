package com.xlxyvergil.tcc.items;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import javax.annotation.Nullable;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;

import java.util.List;
import java.util.UUID;



/**
 * 抵近射击Prime饰品 - 提供165%霰弹枪伤害加成
 * 效果：为玩家提供165%的霰弹枪伤害加成
 */
public class CloseCombatPrime extends ItemBaseCurio {
    
    // 霰弹枪伤害属性的UUID和配置
    private static final UUID SHOTGUN_DAMAGE_UUID = UUID.fromString("fa19535c-5dcb-4c3c-833f-53ea1c9bc5b0");
    private static final String SHOTGUN_DAMAGE_NAME = "tcc.close_combat_prime.shotgun_damage";
    
    public CloseCombatPrime(Properties properties) {
        super(properties);
    }
    
    /**
     * 当饰品被装备时调用
     */
    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        super.onEquip(slotContext, prevStack, stack);
        
        // 给生物添加霰弹枪伤害属性加成
        applyShotgunDamageBonus((LivingEntity) slotContext.entity());
    }
    
    /**
     * 当饰品被卸下时调用
     */
    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        super.onUnequip(slotContext, newStack, stack);
        
        // 移除生物的霰弹枪伤害属性加成
        removeShotgunDamageBonus((LivingEntity) slotContext.entity());
    }
    
    /**
     * 检查是否可以装备到指定插槽
     * CloseCombatPrime与CloseRangeShot互斥，不能同时装备
     */
    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        // 检查是否装备在TCC饰品槽位
        if (!slotContext.identifier().equals("tcc_slot")) {
            return false;
        }
        
        // 检查生物是否已经装备了CloseRangeShot
        ICuriosItemHandler curiosHandler = top.theillusivec4.curios.api.CuriosApi.getCuriosInventory(slotContext.entity()).orElse(null);
        if (curiosHandler != null) {
            ICurioStacksHandler tccSlotHandler = curiosHandler.getCurios().get("tcc_slot");
            if (tccSlotHandler != null) {
                for (int i = 0; i < tccSlotHandler.getSlots(); i++) {
                    ItemStack equippedStack = tccSlotHandler.getStacks().getStackInSlot(i);
                    if (equippedStack.getItem() instanceof CloseRangeShot) {
                        return false; // 如果已经装备了CloseRangeShot，则不能装备CloseCombatPrime
                    }
                }
            }
        }
        
        return true;
    }
    
    /**
     * 返回饰品槽位ID
     */
    public String getSlot() {
        return "tcc:tcc_slot";
    }
    
    /**
     * 应用霰弹枪伤害加成
     * 给生物添加霰弹枪伤害加成（加法）
     */
    private void applyShotgunDamageBonus(LivingEntity livingEntity) {
        var attributes = livingEntity.getAttributes();
        
        var damageAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new ResourceLocation("taa", "bullet_gundamage_shotgun")
            )
        );
        
        if (damageAttribute != null) {
            // 检查是否已经存在相同的修饰符
            if (damageAttribute.getModifier(SHOTGUN_DAMAGE_UUID) == null) {
                // 从配置文件获取伤害加成值
                double damageBoost = TaczCuriosConfig.COMMON.closeCombatPrimeShotgunDamageBoost.get();
                
                // 添加配置的伤害加成
                AttributeModifier modifier = new AttributeModifier(
                    SHOTGUN_DAMAGE_UUID,
                    SHOTGUN_DAMAGE_NAME,
                    damageBoost,
                    AttributeModifier.Operation.ADDITION
                );
                damageAttribute.addPermanentModifier(modifier);
            }
        }
    }
    
    /**
     * 移除霰弹枪伤害加成
     */
    private void removeShotgunDamageBonus(LivingEntity livingEntity) {
        var attributes = livingEntity.getAttributes();
        
        var damageAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new ResourceLocation("taa", "bullet_gundamage_shotgun")
            )
        );
        
        if (damageAttribute != null) {
            // 移除之前添加的修饰符
            damageAttribute.removeModifier(SHOTGUN_DAMAGE_UUID);
        }
    }
    
    /**
     * 当玩家持有时，每tick更新效果
     */
    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        // 确保效果持续生效
        applyShotgunDamageBonus((LivingEntity) slotContext.entity());
    }
    
    /**
     * 添加物品的悬浮提示信息（鼠标悬停时显示）
     */
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        
        // 添加物品描述
        tooltip.add(Component.translatable("item.tcc.close_combat_prime.desc")
            .withStyle(net.minecraft.ChatFormatting.GRAY));
        
        // 添加空行分隔
        tooltip.add(Component.literal(""));
        
        // 添加装备效果
        double damageBoost = TaczCuriosConfig.COMMON.closeCombatPrimeShotgunDamageBoost.get() * 100;
        tooltip.add(Component.translatable("item.tcc.close_combat_prime.effect", String.format("%.0f", damageBoost))
            .withStyle(net.minecraft.ChatFormatting.LIGHT_PURPLE));
        
        // 添加霰弹枪伤害加成的详细列表
        tooltip.add(Component.literal("  §7• §6+" + String.format("%.0f", damageBoost) + "% §7霰弹枪伤害")
            .withStyle(net.minecraft.ChatFormatting.GRAY));
        
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
        applyShotgunDamageBonus(livingEntity);
    }
}