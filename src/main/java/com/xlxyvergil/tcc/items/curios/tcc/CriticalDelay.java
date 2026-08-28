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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class CriticalDelay extends TccCurioItem {
    private static final UUID CRIT_CHANCE_UUID = UUID.fromString("3984a595-15c5-4f27-98f4-7c314aa7b819");
    private static final UUID FIRE_RATE_UUID = UUID.fromString("e061a849-c50b-4a24-8782-b27bb8782bc7");

    private static final String CRIT_CHANCE_NAME = "tcc.critical_delay.crit_chance";
    private static final String FIRE_RATE_NAME = "tcc.critical_delay.fire_rate";

    public CriticalDelay(Properties properties) {
        super(properties);
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        double critChanceBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.criticalDelayCritChanceBoost.get());
        double fireRateReduction = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.criticalDelayFireRateReduction.get());

        AttributeHelper.applyModifier(livingEntity, AttributeHelper.CRIT_CHANCE, critChanceBoost, CRIT_CHANCE_UUID, CRIT_CHANCE_NAME, AttributeModifier.Operation.MULTIPLY_BASE);
        AttributeHelper.applyModifier(livingEntity, AttributeHelper.ROUNDS_PER_MINUTE, fireRateReduction, FIRE_RATE_UUID, FIRE_RATE_NAME, AttributeModifier.Operation.MULTIPLY_BASE);
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.CRIT_CHANCE, CRIT_CHANCE_UUID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.ROUNDS_PER_MINUTE, FIRE_RATE_UUID);
    }

    @Override
    public java.util.List<String> getWeaponTypeRestriction() {
        return GunTypeChecker.ALL_GUN_TYPES_LIST;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        tooltip.add(Component.literal(""));

        double critChanceBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.criticalDelayCritChanceBoost.get() ) * 100;
        double fireRateReduction = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.criticalDelayFireRateReduction.get() ) * 100;
        tooltip.add(Component.translatable("item.tcc.critical_delay.effect",
                String.format("%+.0f", critChanceBoost), String.format("%+.0f", fireRateReduction))
            .withStyle(ChatFormatting.GOLD));

        tooltip.add(Component.literal(""));
        
    }

}
