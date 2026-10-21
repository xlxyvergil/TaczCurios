package com.xlxyvergil.tcc.items.curios.tcc;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.items.TccCurioItem;
import com.xlxyvergil.tcc.util.FusionData;
import com.xlxyvergil.tcc.util.GunTypeChecker;

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
public class BlazeStorm extends TccCurioItem {
    // 属性修饰符UUID - 用于唯一标识这些修饰符
    private static final ResourceLocation EXPLOSION_RADIUS_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "blaze_storm_aceef087_dcbc");
    private static final ResourceLocation EXPLOSION_DAMAGE_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "blaze_storm_5e9bcd94_ac6a");
    private static final ResourceLocation EXPLOSION_ENABLED_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "blaze_storm_5e9bcd94_ac6a");
    
    // 修饰符名称
    
    public BlazeStorm(Properties properties) {
        super(properties);
    }
    
    @Override
    protected void declareModifiers(LivingEntity livingEntity, ItemStack stack) {
        if (matchesRestriction(livingEntity)) {
            double explosionRadiusBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.blazeStormExplosionRadiusBoost.get());
            double explosionDamageBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.blazeStormExplosionDamageBoost.get());
            double explosionEnabled = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.blazeStormExplosionEnabled.get());

            AttributeHelper.applyModifier(livingEntity, AttributeHelper.EXPLOSION_RADIUS, explosionRadiusBoost, EXPLOSION_RADIUS_ID, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);

            AttributeHelper.applyModifier(livingEntity, AttributeHelper.EXPLOSION_DAMAGE, explosionDamageBoost, EXPLOSION_DAMAGE_ID, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);

            AttributeHelper.applyModifier(livingEntity, AttributeHelper.EXPLOSION_ENABLED, explosionEnabled, EXPLOSION_ENABLED_ID, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        }
    }

    @Override
    protected void removeModifiers(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.EXPLOSION_RADIUS, EXPLOSION_RADIUS_ID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.EXPLOSION_DAMAGE, EXPLOSION_DAMAGE_ID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.EXPLOSION_ENABLED, EXPLOSION_ENABLED_ID);
    }

    @Override
    public java.util.List<String> getWeaponTypeRestriction() {
        return GunTypeChecker.ALL_GUN_TYPES_LIST;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        Level level = context.level();
        super.appendHoverText(stack, context, tooltip, flag);

        tooltip.add(Component.literal(""));

        double explosionRadiusBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.blazeStormExplosionRadiusBoost.get() ) * 100;
        double explosionDamageBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.blazeStormExplosionDamageBoost.get() ) * 100;
        tooltip.add(formatModifierTooltip(explosionRadiusBoost, "%.0f%%",
                Component.translatable(AttributeHelper.EXPLOSION_RADIUS.value().getDescriptionId()))
            .withStyle(ChatFormatting.GOLD));
        tooltip.add(formatModifierTooltip(explosionDamageBoost, "%.0f%%",
                Component.translatable(AttributeHelper.EXPLOSION_DAMAGE.value().getDescriptionId()))
            .withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.translatable("item.tcc.blaze_storm.note").withStyle(ChatFormatting.GRAY));

        tooltip.add(Component.literal(""));

        
    }
    
}