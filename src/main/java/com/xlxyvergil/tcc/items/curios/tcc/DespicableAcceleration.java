package com.xlxyvergil.tcc.items.curios.tcc;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.items.TccCurioItem;
import com.xlxyvergil.tcc.util.FusionData;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.minecraft.resources.ResourceLocation;
import com.xlxyvergil.tcc.TaczCurios;
public class DespicableAcceleration extends TccCurioItem {
    private static final Map<String, ResourceLocation> DAMAGE_IDS = new HashMap<>();
    
    static {
        DAMAGE_IDS.put("pistol", ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "despicable_acceleration_ca7ca83d_5430"));
        DAMAGE_IDS.put("rifle", ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "despicable_acceleration_7821adde_c24b"));
        DAMAGE_IDS.put("shotgun", ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "despicable_acceleration_bc3c2aee_3ccc"));
        DAMAGE_IDS.put("sniper", ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "despicable_acceleration_c8d0deac_0c34"));
        DAMAGE_IDS.put("smg", ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "despicable_acceleration_8cb8dcdb_7082"));
        DAMAGE_IDS.put("lmg", ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "despicable_acceleration_e20233c4_dc4e"));
        DAMAGE_IDS.put("launcher", ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "despicable_acceleration_d55edb82_5ddb"));
        
    }
    
    private static final ResourceLocation FIRING_SPEED_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "despicable_acceleration_46acf410_4831");
    private static final ResourceLocation GENERAL_DAMAGE_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "despicable_acceleration_adfdea37_0cde");
    
    
    public DespicableAcceleration(Properties properties) {
        super(properties);
    }
    
    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        // 登记该饰品施加的修饰符 ID → 来源饰品，供客户端属性面板显示来源图标。
        AttributeHelper.registerSourceItem(FIRING_SPEED_ID, stack.getItem());
        AttributeHelper.registerSourceItem(GENERAL_DAMAGE_ID, stack.getItem());
        for (ResourceLocation uuid : DAMAGE_IDS.values()) {
            AttributeHelper.registerSourceItem(uuid, stack.getItem());
        }
        if (matchesRestriction(livingEntity)) {
            double firingSpeedBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.despicableAccelerationFireRateBoost.get());
            double damageReduction = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.despicableAccelerationDamageReduction.get());
            
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.ROUNDS_PER_MINUTE, firingSpeedBoost, FIRING_SPEED_ID, AttributeModifier.Operation.ADD_VALUE);
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE, damageReduction, GENERAL_DAMAGE_ID, AttributeModifier.Operation.ADD_VALUE);
            
            for (String gunType : DAMAGE_IDS.keySet()) {
                var attribute = getAttributeByType(gunType);
                if (attribute != null) {
                    AttributeHelper.applyModifier(livingEntity, attribute, damageReduction, DAMAGE_IDS.get(gunType), AttributeModifier.Operation.ADD_VALUE);
                }
            }
        }
    }
    
    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.ROUNDS_PER_MINUTE, FIRING_SPEED_ID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE, GENERAL_DAMAGE_ID);
        
        for (String gunType : DAMAGE_IDS.keySet()) {
            var attribute = getAttributeByType(gunType);
            if (attribute != null) {
                AttributeHelper.removeModifier(livingEntity, attribute, DAMAGE_IDS.get(gunType));
            }
        }
    }

    @Override
    public java.util.List<String> getWeaponTypeRestriction() {
        return java.util.List.of("rifle", "sniper", "smg", "mg", "rpg");
    }
    

    private Holder<Attribute> getAttributeByType(String gunType) {
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
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        Level level = context.level();
        super.appendHoverText(stack, context, tooltip, flag);
        
        tooltip.add(Component.literal(""));
        
        double firingSpeedBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.despicableAccelerationFireRateBoost.get() ) * 100;
        double damageReduction = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.despicableAccelerationDamageReduction.get() ) * 100;
        tooltip.add(Component.translatable("item.tcc.despicable_acceleration.effect", 
                String.format("%+.0f", firingSpeedBoost), String.format("%+.0f", damageReduction))
            .withStyle(ChatFormatting.GOLD));
        
        tooltip.add(Component.literal(""));
        
    }
    
}