package com.xlxyvergil.tcc.items.curios.tcc;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.items.TccCurioItem;
import com.xlxyvergil.tcc.util.FusionData;

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

public class DespicableAcceleration extends TccCurioItem {
    private static final Map<String, UUID> DAMAGE_UUIDS = new HashMap<>();
    private static final Map<String, String> DAMAGE_NAMES = new HashMap<>();
    
    static {
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
    
    private static final UUID FIRING_SPEED_UUID = UUID.fromString("46acf410-597b-4388-a0c2-9f39f5934831");
    private static final UUID GENERAL_DAMAGE_UUID = UUID.fromString("adfdea37-0701-41c8-b042-59f7453b0cde");
    
    private static final String FIRING_SPEED_NAME = "tcc.despicable_acceleration.firing_speed";
    private static final String GENERAL_DAMAGE_NAME = "tcc.despicable_acceleration.general_damage";
    
    public DespicableAcceleration(Properties properties) {
        super(properties);
    }
    
    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        // 登记该饰品施加的修饰符 UUID → 来源饰品，供客户端属性面板显示来源图标。
        AttributeHelper.registerSourceItem(FIRING_SPEED_UUID, stack.getItem());
        AttributeHelper.registerSourceItem(GENERAL_DAMAGE_UUID, stack.getItem());
        for (UUID uuid : DAMAGE_UUIDS.values()) {
            AttributeHelper.registerSourceItem(uuid, stack.getItem());
        }
        if (matchesRestriction(livingEntity)) {
            double firingSpeedBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.despicableAccelerationFireRateBoost.get());
            double damageReduction = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.despicableAccelerationDamageReduction.get());
            
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.ROUNDS_PER_MINUTE, firingSpeedBoost, FIRING_SPEED_UUID, FIRING_SPEED_NAME, AttributeModifier.Operation.ADDITION);
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE, damageReduction, GENERAL_DAMAGE_UUID, GENERAL_DAMAGE_NAME, AttributeModifier.Operation.ADDITION);
            
            for (String gunType : DAMAGE_UUIDS.keySet()) {
                var attribute = getAttributeByType(gunType);
                if (attribute != null) {
                    AttributeHelper.applyModifier(livingEntity, attribute, damageReduction, DAMAGE_UUIDS.get(gunType), DAMAGE_NAMES.get(gunType), AttributeModifier.Operation.ADDITION);
                }
            }
        }
    }
    
    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.ROUNDS_PER_MINUTE, FIRING_SPEED_UUID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE, GENERAL_DAMAGE_UUID);
        
        for (String gunType : DAMAGE_UUIDS.keySet()) {
            var attribute = getAttributeByType(gunType);
            if (attribute != null) {
                AttributeHelper.removeModifier(livingEntity, attribute, DAMAGE_UUIDS.get(gunType));
            }
        }
    }

    @Override
    public java.util.List<String> getWeaponTypeRestriction() {
        return java.util.List.of("rifle", "sniper", "smg", "mg", "rpg");
    }
    

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
    

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        
        tooltip.add(Component.literal(""));
        
        double firingSpeedBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.despicableAccelerationFireRateBoost.get() ) * 100;
        double damageReduction = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.despicableAccelerationDamageReduction.get() ) * 100;
        tooltip.add(Component.translatable("item.tcc.despicable_acceleration.effect", 
                String.format("%+.0f", firingSpeedBoost), String.format("%+.0f", damageReduction))
            .withStyle(ChatFormatting.GOLD));
        
        tooltip.add(Component.literal(""));
        
    }
    
}