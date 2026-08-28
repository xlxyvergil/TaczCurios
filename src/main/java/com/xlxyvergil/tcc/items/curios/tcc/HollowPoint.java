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
import java.util.List;
import java.util.UUID;

/**
 * 空尖弹：暴击伤害+60%，手枪伤害-15%
 */
public class HollowPoint extends TccCurioItem {

    private static final UUID CRIT_DAMAGE_UUID = UUID.fromString("56dd9ca3-8c1d-400b-b471-f611876ef639");
    private static final UUID PISTOL_DAMAGE_UUID = UUID.fromString("75109b44-e547-454e-b88a-91dae3c0bd41");

    private static final String CRIT_DAMAGE_NAME = "tcc.hollow_point.crit_damage";
    private static final String PISTOL_DAMAGE_NAME = "tcc.hollow_point.pistol_damage";

    public HollowPoint(Properties properties) {
        super(properties);
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        if (matchesRestriction(livingEntity)) {
            double critDamageBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.hollowPointCritDamage.get());
            double pistolDamageReduction = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.hollowPointPistolDamageReduction.get());
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.CRIT_DAMAGE, critDamageBoost, CRIT_DAMAGE_UUID, CRIT_DAMAGE_NAME, AttributeModifier.Operation.MULTIPLY_BASE);
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE_PISTOL, pistolDamageReduction, PISTOL_DAMAGE_UUID, PISTOL_DAMAGE_NAME, AttributeModifier.Operation.MULTIPLY_BASE);
        } else {
            AttributeHelper.removeModifier(livingEntity, AttributeHelper.CRIT_DAMAGE, CRIT_DAMAGE_UUID);
            AttributeHelper.removeModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE_PISTOL, PISTOL_DAMAGE_UUID);
        }
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.CRIT_DAMAGE, CRIT_DAMAGE_UUID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE_PISTOL, PISTOL_DAMAGE_UUID);
    }

    @Override
    public java.util.List<String> getWeaponTypeRestriction() {
        return java.util.List.of("pistol");
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        tooltip.add(Component.literal(""));

        double critDamageBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.hollowPointCritDamage.get() ) * 100;
        double pistolDamageReduction = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.hollowPointPistolDamageReduction.get() ) * 100;
        tooltip.add(Component.translatable("item.tcc.hollow_point.effect",
                String.format("%+.0f", critDamageBoost), String.format("%+.0f", pistolDamageReduction))
            .withStyle(ChatFormatting.GOLD));

        tooltip.add(Component.literal(""));
        
    }


}
