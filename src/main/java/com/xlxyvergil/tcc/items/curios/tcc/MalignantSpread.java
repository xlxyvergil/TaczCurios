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

import net.minecraft.resources.ResourceLocation;
import com.xlxyvergil.tcc.TaczCurios;
public class MalignantSpread extends TccCurioItem {
    private static final ResourceLocation DAMAGE_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "malignant_spread_5bfabff0_bf69");
    private static final ResourceLocation INACCURACY_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "malignant_spread_03755bb2_f149");
    
    
    
    public MalignantSpread(Properties properties) {
        super(properties);
    }
    
    @Override
    protected void declareModifiers(LivingEntity livingEntity, ItemStack stack) {
        double damageBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.malignantSpreadDamageBoost.get());
        double inaccuracyBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.malignantSpreadAccuracyReduction.get());
        
        // 枪械伤害加成只在主手持有对应武器类型时生效；切换武器时由 GunSwitchEventHandler 触发 refreshEffects 重新评估
        if (matchesRestriction(livingEntity)) {
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE_SHOTGUN, damageBoost, DAMAGE_ID, AttributeModifier.Operation.ADD_VALUE);

            AttributeHelper.applyModifier(livingEntity, AttributeHelper.INACCURACY, inaccuracyBoost, INACCURACY_ID, AttributeModifier.Operation.ADD_VALUE);
        }
    }
    
    @Override
    protected void removeModifiers(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE_SHOTGUN, DAMAGE_ID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.INACCURACY, INACCURACY_ID);
    }
    

    @Override
    public java.util.List<String> getWeaponTypeRestriction() {
        return java.util.List.of("shotgun");
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        Level level = context.level();
        super.appendHoverText(stack, context, tooltip, flag);

        tooltip.add(Component.literal(""));

        double damageBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.malignantSpreadDamageBoost.get() ) * 100;
        double inaccuracyBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.malignantSpreadAccuracyReduction.get() ) * 100;
        tooltip.add(formatModifierTooltip(damageBoost, "%.0f%%", Component.translatable(AttributeHelper.BULLET_GUNDAMAGE_SHOTGUN.value().getDescriptionId())).withStyle(ChatFormatting.GOLD));
        tooltip.add(formatModifierTooltip(inaccuracyBoost, "%.0f%%", Component.translatable(AttributeHelper.INACCURACY.value().getDescriptionId())).withStyle(ChatFormatting.GOLD));

        tooltip.add(Component.literal(""));

    }
    
}