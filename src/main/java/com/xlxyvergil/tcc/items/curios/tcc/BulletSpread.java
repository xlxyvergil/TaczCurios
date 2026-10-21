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
public class BulletSpread extends TccCurioItem {
    // 属性修饰符UUID - 用于唯一标识修饰符
    private static final ResourceLocation BULLET_COUNT_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "bullet_spread_0e7e5d6a_71d2");
    
    // 修饰符名称
    
    public BulletSpread(Properties properties) {
        super(properties);
    }
    
    @Override
    protected void declareModifiers(LivingEntity livingEntity, ItemStack stack) {
        if (matchesRestriction(livingEntity)) {
            double bulletCountBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.bulletSpreadBulletCountBoost.get());
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.BULLET_COUNT, bulletCountBoost, BULLET_COUNT_ID, AttributeModifier.Operation.ADD_VALUE);
        }
    }

    @Override
    protected void removeModifiers(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.BULLET_COUNT, BULLET_COUNT_ID);
    }

    @Override
    public java.util.List<String> getWeaponTypeRestriction() {
        return java.util.List.of("pistol");
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        Level level = context.level();
        super.appendHoverText(stack, context, tooltip, flag);

        tooltip.add(Component.literal(""));

        double bulletCountBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.bulletSpreadBulletCountBoost.get() ) * 100;
        tooltip.add(formatModifierTooltip(bulletCountBoost, "%.0f%%",
                Component.translatable(AttributeHelper.BULLET_COUNT.value().getDescriptionId()))
            .withStyle(ChatFormatting.GOLD));

        tooltip.add(Component.literal(""));

    }

}