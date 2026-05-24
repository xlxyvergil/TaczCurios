package com.xlxyvergil.tcc.items;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.util.BaseCurioItem;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * 抵近射击Prime饰品 - 提供165%霰弹枪伤害加成
 * 效果：为玩家提供165%的霰弹枪伤害加成
 */
public class CloseCombatPrime extends BaseCurioItem {
    
    // 霰弹枪伤害属性的UUID和配置
    private static final UUID SHOTGUN_DAMAGE_UUID = UUID.fromString("fa19535c-5dcb-4c3c-833f-53ea1c9bc5b0");
    private static final String SHOTGUN_DAMAGE_NAME = "tcc.close_combat_prime.shotgun_damage";
    
    public CloseCombatPrime(Properties properties) {
        super(properties);
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
        LivingEntity entity = (LivingEntity) slotContext.entity();
        return !hasEquipped(entity, itemStack -> itemStack.getItem() instanceof CloseRangeShot);
    }
    
    /**
     * 应用霰弹枪伤害加成
     * 给生物添加霰弹枪伤害加成（加法）
     */
    @Override
    protected void applyEffects(LivingEntity livingEntity) {
        double damageBoost = TaczCuriosConfig.COMMON.closeCombatPrimeShotgunDamageBoost.get();
        AttributeHelper.applyModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE_SHOTGUN, damageBoost, SHOTGUN_DAMAGE_UUID, SHOTGUN_DAMAGE_NAME, AttributeModifier.Operation.ADDITION);
    }
    
    /**
     * 移除霰弹枪伤害加成
     */
    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE_SHOTGUN, SHOTGUN_DAMAGE_UUID);
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
        double damageBoost = TaczCuriosConfig.COMMON.closeCombatPrimeShotgunDamageBoost.get() * 100;
        tooltip.add(Component.translatable("item.tcc.close_combat_prime.effect", String.format("%+.0f", damageBoost))
            .withStyle(ChatFormatting.WHITE));
        
        
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