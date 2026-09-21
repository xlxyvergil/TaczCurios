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
public class HollowPoint extends TccCurioItem {
    private static final ResourceLocation CRIT_DAMAGE_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "hollow_point_56dd9ca3_f639");
    private static final ResourceLocation PISTOL_DAMAGE_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "hollow_point_75109b44_bd41");


    public HollowPoint(Properties properties) {
        super(properties);
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        // 登记该饰品施加的修饰符 ID → 来源饰品，供客户端属性面板显示来源图标。
        AttributeHelper.registerSourceItem(CRIT_DAMAGE_ID, stack.getItem());
        AttributeHelper.registerSourceItem(PISTOL_DAMAGE_ID, stack.getItem());
        if (matchesRestriction(livingEntity)) {
            double critDamageBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.hollowPointCritDamage.get());
            double pistolDamageReduction = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.hollowPointPistolDamageReduction.get());
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.CRIT_DAMAGE, critDamageBoost, CRIT_DAMAGE_ID, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE_PISTOL, pistolDamageReduction, PISTOL_DAMAGE_ID, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        } else {
            AttributeHelper.removeModifier(livingEntity, AttributeHelper.CRIT_DAMAGE, CRIT_DAMAGE_ID);
            AttributeHelper.removeModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE_PISTOL, PISTOL_DAMAGE_ID);
        }
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.CRIT_DAMAGE, CRIT_DAMAGE_ID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE_PISTOL, PISTOL_DAMAGE_ID);
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

        double critDamageBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.hollowPointCritDamage.get() ) * 100;
        double pistolDamageReduction = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.hollowPointPistolDamageReduction.get() ) * 100;
        tooltip.add(Component.translatable("item.tcc.hollow_point.effect",
                String.format("%+.0f", critDamageBoost), String.format("%+.0f", pistolDamageReduction))
            .withStyle(ChatFormatting.GOLD));

        tooltip.add(Component.literal(""));
        
    }


}
