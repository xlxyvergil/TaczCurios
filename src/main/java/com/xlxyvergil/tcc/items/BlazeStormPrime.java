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
 * 烈焰风暴Prime饰品
 * 效果：增加爆炸范围（乘算），增加爆炸伤害（乘算）
 */
public class BlazeStormPrime extends BaseCurioItem {
    
    // 属性修饰符UUID - 用于唯一标识这些修饰符
    private static final UUID EXPLOSION_RADIUS_UUID = UUID.fromString("248f06a5-5144-47a0-b56a-6d830ade21b9");
    private static final UUID EXPLOSION_DAMAGE_UUID = UUID.fromString("76d7748e-2371-4513-90cc-880f469f389a");
    private static final UUID EXPLOSION_ENABLED_UUID = UUID.fromString("248f06a5-5144-90cc-b56a-6d830ade21b9");
    
    // 修饰符名称
    private static final String EXPLOSION_RADIUS_NAME = "tcc.blaze_storm_prime.explosion_radius";
    private static final String EXPLOSION_DAMAGE_NAME = "tcc.blaze_storm_prime.explosion_damage";
    private static final String EXPLOSION_ENABLED_NAME = "tcc.blaze_storm_prime.explosion_enabled";
    
    public BlazeStormPrime(Properties properties) {
        super(properties);
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
     * 应用所有效果加成
     * 增加配置中的爆炸范围和爆炸伤害加成（乘算）
     */
    @Override
    protected void applyEffects(LivingEntity livingEntity) {
        // 获取配置中的爆炸范围、爆炸伤害和爆炸启用属性值
        double explosionRadiusBoost = TaczCuriosConfig.COMMON.blazeStormPrimeExplosionRadiusBoost.get();
        double explosionDamageBoost = TaczCuriosConfig.COMMON.blazeStormPrimeExplosionDamageBoost.get();
        double explosionEnabled = TaczCuriosConfig.COMMON.blazeStormPrimeExplosionEnabled.get();
        
        // 应用爆炸范围加成
        AttributeHelper.applyModifier(livingEntity, AttributeHelper.EXPLOSION_RADIUS, explosionRadiusBoost, EXPLOSION_RADIUS_UUID, EXPLOSION_RADIUS_NAME, AttributeModifier.Operation.MULTIPLY_BASE);
        
        // 应用爆炸伤害加成
        AttributeHelper.applyModifier(livingEntity, AttributeHelper.EXPLOSION_DAMAGE, explosionDamageBoost, EXPLOSION_DAMAGE_UUID, EXPLOSION_DAMAGE_NAME, AttributeModifier.Operation.MULTIPLY_BASE);
        
        // 应用爆炸启用属性
        AttributeHelper.applyModifier(livingEntity, AttributeHelper.EXPLOSION_ENABLED, explosionEnabled, EXPLOSION_ENABLED_UUID, EXPLOSION_ENABLED_NAME, AttributeModifier.Operation.MULTIPLY_BASE);
    }
    
    /**
     * 移除所有效果加成
     */
    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.EXPLOSION_RADIUS, EXPLOSION_RADIUS_UUID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.EXPLOSION_DAMAGE, EXPLOSION_DAMAGE_UUID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.EXPLOSION_ENABLED, EXPLOSION_ENABLED_UUID);
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
        double explosionRadiusBoost = TaczCuriosConfig.COMMON.blazeStormPrimeExplosionRadiusBoost.get() * 100;
        double explosionDamageBoost = TaczCuriosConfig.COMMON.blazeStormPrimeExplosionDamageBoost.get() * 100;
        tooltip.add(Component.translatable("item.tcc.blaze_storm_prime.effect", 
                String.format("%+.0f", explosionRadiusBoost), String.format("%+.0f", explosionDamageBoost))
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