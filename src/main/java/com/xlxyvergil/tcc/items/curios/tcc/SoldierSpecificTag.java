package com.xlxyvergil.tcc.items.curios.tcc;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.items.TccCurioItem;
import com.xlxyvergil.tcc.util.FusionData;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import com.xlxyvergil.tcc.util.GunTypeChecker;

import net.minecraft.resources.ResourceLocation;
import com.xlxyvergil.tcc.TaczCurios;
public class SoldierSpecificTag extends TccCurioItem {
    private static final ResourceLocation GUN_DAMAGE_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "soldier_specific_tag_bbd020e4_da4f");
    
    
    public SoldierSpecificTag(Properties properties) {
        super(properties);
    }

    @Override
    protected void declareModifiers(LivingEntity livingEntity, ItemStack stack) {
        if (matchesRestriction(livingEntity)) {
            double damageBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.soldierSpecificTagDamageBoost.get());
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE, damageBoost, GUN_DAMAGE_ID, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        }
    }
    
    @Override
    protected void removeModifiers(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE, GUN_DAMAGE_ID);
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

        double damageBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.soldierSpecificTagDamageBoost.get() ) * 100;
        tooltip.add(formatModifierTooltip(damageBoost, "%.0f%%",
                Component.translatable(AttributeHelper.BULLET_GUNDAMAGE.value().getDescriptionId()))
            .withStyle(ChatFormatting.WHITE));

        tooltip.add(Component.literal(""));

    }
    

}
