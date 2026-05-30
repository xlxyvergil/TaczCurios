package com.xlxyvergil.tcc.items;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.util.BaseCurioItem;
import com.xlxyvergil.tcc.util.GunTypeChecker;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.resource.modifier.AttachmentPropertyManager;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * 膛室Prime - 当玩家手持狙击枪且弹匣满弹药时，提升伤害（乘算）
 * 效果：狙击枪伤害加成（乘算）
 * 与Chamber互斥
 */
public class ChamberPrime extends BaseCurioItem {
    
    // 属性修饰符UUID - 用于唯一标识修饰符
    private static final UUID DAMAGE_UUID = UUID.fromString("9ab0de22-87e4-4fa1-a539-cd50dfe7590c");
    
    // 修饰符名称
    private static final String DAMAGE_NAME = "tcc.chamber_prime.damage";
    
    public ChamberPrime(Properties properties) {
        super(properties);
    }
    
    /**
     * 应用膛室Prime效果
     * 提升狙击枪伤害（乘算）
     */
    @Override
    protected void applyEffects(LivingEntity livingEntity) {
        // 检查生物主手是否持有狙击枪且弹匣满弹药
        boolean shouldApply = GunTypeChecker.isHoldingSniper(livingEntity) && GunTypeChecker.isHoldingGunWithFullMagazine(livingEntity);
        
        if (shouldApply) {
            double damageBoost = TaczCuriosConfig.COMMON.chamberPrimeSniperDamageBoost.get();
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE, damageBoost, DAMAGE_UUID, DAMAGE_NAME, AttributeModifier.Operation.MULTIPLY_BASE);
        } else {
            // 如果条件不满足，移除修饰符
            AttributeHelper.removeModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE, DAMAGE_UUID);
        }
        
        // 更新TACZ缓存
        updateTaczCache(livingEntity);
    }
    
    /**
     * 移除膛室Prime效果
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
        if (mainHandItem.getItem() instanceof IGun) {
            AttachmentPropertyManager.postChangeEvent(livingEntity, mainHandItem);
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
        double damageBoost = TaczCuriosConfig.COMMON.chamberPrimeSniperDamageBoost.get() * 100;
        tooltip.add(Component.translatable("item.tcc.chamber_prime.effect", String.format("%+.0f", damageBoost))
            .withStyle(ChatFormatting.WHITE));
        
        // 添加饰品槽位信息
        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable("tcc.tooltip.slot"));
        
        // 添加稀有度提示
        tooltip.add(Component.translatable("tcc.tooltip.rarity.legendary"));
    }
}