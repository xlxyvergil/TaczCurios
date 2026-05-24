package com.xlxyvergil.tcc.items;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.util.BaseCurioItem;
import com.xlxyvergil.tcc.util.GunTypeChecker;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.resource.modifier.AttachmentPropertyManager;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * 膛室 - 当玩家手持狙击枪且弹匣满弹药时，提升伤害（乘算）
 * 效果：狙击枪伤害加成（乘算）
 * 与ChamberPrime互斥
 */
public class Chamber extends BaseCurioItem {
    
    // 属性修饰符UUID - 用于唯一标识修饰符
    private static final UUID DAMAGE_UUID = UUID.fromString("0d407ca4-24c0-4db7-bc3a-f7d92ab8f2ed");
    
    // 修饰符名称
    private static final String DAMAGE_NAME = "tcc.chamber.damage";
    
    public Chamber(Properties properties) {
        super(properties);
    }
    
    /**
     * 检查是否可以装备到指定插槽
     * Chamber与ChamberPrime互斥，不能同时装备
     */
    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        // 检查是否装备在TCC饰品槽位
        if (!slotContext.identifier().equals("tcc_slot")) {
            return false;
        }
        
        // 检查生物是否已经装备了ChamberPrime
        LivingEntity entity = (LivingEntity) slotContext.entity();
        return !hasEquipped(entity, itemStack -> itemStack.getItem() instanceof ChamberPrime);
    }
    
    /**
     * 应用膛室效果
     * 提升狙击枪伤害（乘算）
     */
    @Override
    protected void applyEffects(LivingEntity livingEntity) {
        // 检查生物主手是否持有狙击枪且弹匣满弹药
        boolean shouldApply = GunTypeChecker.isHoldingSniper(livingEntity) && GunTypeChecker.isHoldingGunWithFullMagazine(livingEntity);
        
        if (shouldApply) {
            double damageBoost = TaczCuriosConfig.COMMON.chamberSniperDamageBoost.get();
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE, damageBoost, DAMAGE_UUID, DAMAGE_NAME, AttributeModifier.Operation.MULTIPLY_BASE);
        } else {
            // 如果条件不满足，移除修饰符
            AttributeHelper.removeModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE, DAMAGE_UUID);
        }
        
        // 更新TACZ缓存
        updateTaczCache(livingEntity);
    }
    
    /**
     * 移除膛室效果
     */
    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE, DAMAGE_UUID);
        updateTaczCache(livingEntity);
    }
    
    /**
     * 更新TACZ缓存
     */
    private void updateTaczCache(LivingEntity livingEntity) {
        ItemStack mainHandItem = livingEntity.getMainHandItem();
        if (mainHandItem.getItem() instanceof IGun && livingEntity instanceof ServerPlayer serverPlayer) {
            AttachmentPropertyManager.postChangeEvent(serverPlayer, mainHandItem);
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
        double damageBoost = TaczCuriosConfig.COMMON.chamberSniperDamageBoost.get() * 100;
        tooltip.add(Component.translatable("item.tcc.chamber.effect", String.format("%+.0f", damageBoost))
            .withStyle(ChatFormatting.AQUA));
        
        // 添加饰品槽位信息
        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable("tcc.tooltip.slot"));
        
        // 添加稀有度提示
        tooltip.add(Component.translatable("tcc.tooltip.rarity.uncommon"));
    }
    
    /**
     * 当生物切换武器时应用效果
     */
    @Override
    public void applyGunSwitchEffect(LivingEntity livingEntity) {
        applyEffects(livingEntity);
    }
}