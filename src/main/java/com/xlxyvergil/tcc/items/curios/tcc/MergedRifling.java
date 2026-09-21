package com.xlxyvergil.tcc.items.curios.tcc;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.items.TccCurioItem;
import com.xlxyvergil.tcc.util.FusionData;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;


import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;



import net.minecraft.resources.ResourceLocation;
import com.xlxyvergil.tcc.TaczCurios;
public class MergedRifling extends TccCurioItem {
    private static final ResourceLocation[] DAMAGE_IDS = {
        ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "merged_rifling_f36f64c9_f940"),
        ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "merged_rifling_32254b9b_6ac5"),
        ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "merged_rifling_adfae406_1f63"),
        ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "merged_rifling_f1f1f906_1f95"),
        ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "merged_rifling_39f3a9fd_e7e8")
    };
    private static final ResourceLocation MOVEMENT_SPEED_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "merged_rifling_6967f153_53c2");
    
    
    public MergedRifling(Properties properties) {
        super(properties);
    }
    
    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        // 登记该饰品施加的修饰符 ID → 来源饰品，供客户端属性面板显示来源图标。
        AttributeHelper.registerSourceItem(MOVEMENT_SPEED_ID, stack.getItem());
        for (ResourceLocation uuid : DAMAGE_IDS) {
            AttributeHelper.registerSourceItem(uuid, stack.getItem());
        }
        double damageBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.mergedRiflingDamageBoost.get());
        double speedBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.mergedRiflingMovementSpeedBoost.get());
        
        // 枪械伤害加成只在主手持有对应武器类型时生效；切换武器时由 GunSwitchEventHandler 触发 refreshEffects 重新评估
        if (matchesRestriction(livingEntity)) {
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE_RIFLE, damageBoost, DAMAGE_IDS[0], AttributeModifier.Operation.ADD_VALUE);
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE_SNIPER, damageBoost, DAMAGE_IDS[1], AttributeModifier.Operation.ADD_VALUE);
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE_SMG, damageBoost, DAMAGE_IDS[2], AttributeModifier.Operation.ADD_VALUE);
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE_LMG, damageBoost, DAMAGE_IDS[3], AttributeModifier.Operation.ADD_VALUE);
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE_LAUNCHER, damageBoost, DAMAGE_IDS[4], AttributeModifier.Operation.ADD_VALUE);

            AttributeHelper.applyModifier(livingEntity, AttributeHelper.MOVE_SPEED, speedBoost, MOVEMENT_SPEED_ID, AttributeModifier.Operation.ADD_VALUE);
        }
    }
    
    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE_RIFLE, DAMAGE_IDS[0]);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE_SNIPER, DAMAGE_IDS[1]);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE_SMG, DAMAGE_IDS[2]);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE_LMG, DAMAGE_IDS[3]);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE_LAUNCHER, DAMAGE_IDS[4]);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.MOVE_SPEED, MOVEMENT_SPEED_ID);
    }
    

    @Override
    public java.util.List<String> getWeaponTypeRestriction() {
        return java.util.List.of("rifle", "sniper", "smg", "mg", "rpg");
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        Level level = context.level();
        super.appendHoverText(stack, context, tooltip, flag);

        tooltip.add(Component.literal(""));

        double damageBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.mergedRiflingDamageBoost.get() ) * 100;
        double speedBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.mergedRiflingMovementSpeedBoost.get() ) * 100;
        tooltip.add(Component.translatable("item.tcc.merged_rifling.effect", 
                String.format("%+.0f", damageBoost), String.format("%+.0f", speedBoost))
            .withStyle(ChatFormatting.WHITE));

        tooltip.add(Component.literal(""));

    }
    
}