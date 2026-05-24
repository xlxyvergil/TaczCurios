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

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * 我小心海也绝非鳝类 - 提升300%发射器伤害加成，300%爆炸伤害加成，300%爆炸范围加成
 * 效果：发射器伤害+300%，爆炸伤害+300%，爆炸范围+300%
 */
public class CarefulHeart extends BaseCurioItem {
    
    // 属性修饰符UUID - 用于唯一标识这些修饰符
    private static final UUID LAUNCHER_DAMAGE_UUID = UUID.fromString("81343572-fe9a-4cf2-bdf9-253af5a153c1");
    private static final UUID EXPLOSION_DAMAGE_UUID = UUID.fromString("2fd98843-cf54-4ca7-949d-e8d50e295c86");
    private static final UUID EXPLOSION_RADIUS_UUID = UUID.fromString("e966cd29-d1c2-4770-a422-0f71c2ef4409");
    private static final UUID EXPLOSION_ENABLED_UUID = UUID.fromString("248f06a5-5144-4770-b56a-6d830ade21b9");
    
    // 修饰符名称
    private static final String LAUNCHER_DAMAGE_NAME = "tcc.careful_heart.launcher_damage";
    private static final String EXPLOSION_DAMAGE_NAME = "tcc.careful_heart.explosion_damage";
    private static final String EXPLOSION_RADIUS_NAME = "tcc.careful_heart.explosion_radius";
    private static final String EXPLOSION_ENABLED_NAME = "tcc.careful_heart.explosion_enabled";
    
    public CarefulHeart(Properties properties) {
        super(properties);
    }
    
    /**
     * 应用心海效果
     * 提升发射器伤害、爆炸伤害和爆炸范围
     */
    @Override
    protected void applyEffects(LivingEntity livingEntity) {
        double launcherDamageBoost = TaczCuriosConfig.COMMON.carefulHeartLauncherDamageBoost.get();
        double explosionDamageBoost = TaczCuriosConfig.COMMON.carefulHeartExplosionDamageBoost.get();
        double explosionRadiusBoost = TaczCuriosConfig.COMMON.carefulHeartExplosionRadiusBoost.get();
        double explosionEnabled = TaczCuriosConfig.COMMON.carefulHeartExplosionEnabled.get();
        
        AttributeHelper.applyModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE_LAUNCHER, launcherDamageBoost, LAUNCHER_DAMAGE_UUID, LAUNCHER_DAMAGE_NAME, AttributeModifier.Operation.ADDITION);
        AttributeHelper.applyModifier(livingEntity, AttributeHelper.EXPLOSION_DAMAGE, explosionDamageBoost, EXPLOSION_DAMAGE_UUID, EXPLOSION_DAMAGE_NAME, AttributeModifier.Operation.ADDITION);
        AttributeHelper.applyModifier(livingEntity, AttributeHelper.EXPLOSION_RADIUS, explosionRadiusBoost, EXPLOSION_RADIUS_UUID, EXPLOSION_RADIUS_NAME, AttributeModifier.Operation.ADDITION);
        AttributeHelper.applyModifier(livingEntity, AttributeHelper.EXPLOSION_ENABLED, explosionEnabled, EXPLOSION_ENABLED_UUID, EXPLOSION_ENABLED_NAME, AttributeModifier.Operation.ADDITION);
    }
    
    /**
     * 移除心海效果
     */
    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE_LAUNCHER, LAUNCHER_DAMAGE_UUID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.EXPLOSION_DAMAGE, EXPLOSION_DAMAGE_UUID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.EXPLOSION_RADIUS, EXPLOSION_RADIUS_UUID);
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
        double launcherDamageBoost = TaczCuriosConfig.COMMON.carefulHeartLauncherDamageBoost.get() * 100;
        double explosionDamageBoost = TaczCuriosConfig.COMMON.carefulHeartExplosionDamageBoost.get() * 100;
        double explosionRadiusBoost = TaczCuriosConfig.COMMON.carefulHeartExplosionRadiusBoost.get() * 100;
        tooltip.add(Component.translatable("item.tcc.careful_heart.effect", 
                String.format("%+.0f", launcherDamageBoost), String.format("%+.0f", explosionDamageBoost), String.format("%+.0f", explosionRadiusBoost))
            .withStyle(ChatFormatting.WHITE));
        
        // 添加饰品槽位信息
        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable("tcc.tooltip.slot"));
        
        // 添加稀有度提示
        tooltip.add(Component.translatable("tcc.tooltip.rarity.legendary"));
    }
    
    /**
     * 当玩家切换武器时应用效果
     */
    @Override
    public void applyGunSwitchEffect(LivingEntity livingEntity) {
        applyEffects(livingEntity);
    }
}