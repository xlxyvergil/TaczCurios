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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 卑劣加速 - 提升射击速度，但降低通用伤害和全部7种特定枪械伤害
 * 效果：射击速度+X%，通用伤害-Y%，特定枪械伤害-Y%
 */
public class DespicableAcceleration extends BaseCurioItem {
    
    // 7种特定枪械伤害属性的UUID和配置
    private static final Map<String, UUID> DAMAGE_UUIDS = new HashMap<>();
    private static final Map<String, String> DAMAGE_NAMES = new HashMap<>();
    
    static {
        // 初始化7种特定枪械的UUID和名称
        DAMAGE_UUIDS.put("pistol", UUID.fromString("ca7ca83d-5439-4fde-a7bf-236d257d5430"));
        DAMAGE_UUIDS.put("rifle", UUID.fromString("7821adde-57d1-42e8-a873-91612b86c24b"));
        DAMAGE_UUIDS.put("shotgun", UUID.fromString("bc3c2aee-ea60-4c98-8f2b-7f7d89523ccc"));
        DAMAGE_UUIDS.put("sniper", UUID.fromString("c8d0deac-5040-4044-9986-bc7735750c34"));
        DAMAGE_UUIDS.put("smg", UUID.fromString("8cb8dcdb-2eef-433c-a4db-6d17b1617082"));
        DAMAGE_UUIDS.put("lmg", UUID.fromString("e20233c4-668a-4bf4-97a1-17889faedc4e"));
        DAMAGE_UUIDS.put("launcher", UUID.fromString("d55edb82-be08-4b59-a93c-efd41b825ddb"));
        
        DAMAGE_NAMES.put("pistol", "tcc.despicable_acceleration.pistol_damage");
        DAMAGE_NAMES.put("rifle", "tcc.despicable_acceleration.rifle_damage");
        DAMAGE_NAMES.put("shotgun", "tcc.despicable_acceleration.shotgun_damage");
        DAMAGE_NAMES.put("sniper", "tcc.despicable_acceleration.sniper_damage");
        DAMAGE_NAMES.put("smg", "tcc.despicable_acceleration.smg_damage");
        DAMAGE_NAMES.put("lmg", "tcc.despicable_acceleration.lmg_damage");
        DAMAGE_NAMES.put("launcher", "tcc.despicable_acceleration.launcher_damage");
    }
    
    // 射击速度和通用伤害的属性修饰符
    private static final UUID FIRING_SPEED_UUID = UUID.fromString("46acf410-597b-4388-a0c2-9f39f5934831");
    private static final UUID GENERAL_DAMAGE_UUID = UUID.fromString("adfdea37-0701-41c8-b042-59f7453b0cde");
    
    // 修饰符名称
    private static final String FIRING_SPEED_NAME = "tcc.despicable_acceleration.firing_speed";
    private static final String GENERAL_DAMAGE_NAME = "tcc.despicable_acceleration.general_damage";
    
    public DespicableAcceleration(Properties properties) {
        super(properties);
    }
    
    /**
     * 应用加速效果
     * 提升射击速度，降低通用伤害和7种特定枪械伤害
     */
    @Override
    protected void applyEffects(LivingEntity livingEntity) {
        double firingSpeedBoost = TaczCuriosConfig.COMMON.despicableAccelerationFireRateBoost.get();
        double damageReduction = TaczCuriosConfig.COMMON.despicableAccelerationDamageReduction.get();
        
        AttributeHelper.applyModifier(livingEntity, AttributeHelper.ROUNDS_PER_MINUTE, firingSpeedBoost, FIRING_SPEED_UUID, FIRING_SPEED_NAME, AttributeModifier.Operation.ADDITION);
        AttributeHelper.applyModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE, damageReduction, GENERAL_DAMAGE_UUID, GENERAL_DAMAGE_NAME, AttributeModifier.Operation.ADDITION);
        
        // 应用7种特定枪械伤害降低
        for (String gunType : DAMAGE_UUIDS.keySet()) {
            var attribute = getAttributeByType(gunType);
            if (attribute != null) {
                AttributeHelper.applyModifier(livingEntity, attribute, damageReduction, DAMAGE_UUIDS.get(gunType), DAMAGE_NAMES.get(gunType), AttributeModifier.Operation.ADDITION);
            }
        }
    }
    
    /**
     * 移除加速效果
     */
    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.ROUNDS_PER_MINUTE, FIRING_SPEED_UUID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE, GENERAL_DAMAGE_UUID);
        
        // 移除7种特定枪械伤害降低
        for (String gunType : DAMAGE_UUIDS.keySet()) {
            var attribute = getAttributeByType(gunType);
            if (attribute != null) {
                AttributeHelper.removeModifier(livingEntity, attribute, DAMAGE_UUIDS.get(gunType));
            }
        }
    }
    
    /**
     * 根据枪械类型获取对应的属性
     */
    private net.minecraft.world.entity.ai.attributes.Attribute getAttributeByType(String gunType) {
        return switch (gunType) {
            case "pistol" -> AttributeHelper.BULLET_GUNDAMAGE_PISTOL;
            case "rifle" -> AttributeHelper.BULLET_GUNDAMAGE_RIFLE;
            case "shotgun" -> AttributeHelper.BULLET_GUNDAMAGE_SHOTGUN;
            case "sniper" -> AttributeHelper.BULLET_GUNDAMAGE_SNIPER;
            case "smg" -> AttributeHelper.BULLET_GUNDAMAGE_SMG;
            case "lmg" -> AttributeHelper.BULLET_GUNDAMAGE_LMG;
            case "launcher" -> AttributeHelper.BULLET_GUNDAMAGE_LAUNCHER;
            default -> null;
        };
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
        double firingSpeedBoost = TaczCuriosConfig.COMMON.despicableAccelerationFireRateBoost.get() * 100;
        double damageReduction = TaczCuriosConfig.COMMON.despicableAccelerationDamageReduction.get() * 100;
        tooltip.add(Component.translatable("item.tcc.despicable_acceleration.effect", 
                String.format("%+.0f", firingSpeedBoost), String.format("%+.0f", damageReduction))
            .withStyle(ChatFormatting.GOLD));
        
        // 添加饰品槽位信息
        tooltip.add(Component.literal(""));
        
        
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