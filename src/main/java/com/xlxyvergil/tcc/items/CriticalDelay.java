package com.xlxyvergil.tcc.items;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.util.BaseCurioItem;

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
 * 关键延迟 - 枪械通用饰品
 * 效果：暴击几率 +200%，射速 -20%
 */
public class CriticalDelay extends BaseCurioItem {

    private static final UUID CRIT_CHANCE_UUID = UUID.fromString("a1b2c3d4-1001-4000-8000-000000000001");
    private static final UUID FIRE_RATE_UUID = UUID.fromString("a1b2c3d4-1001-4000-8000-000000000002");

    private static final String CRIT_CHANCE_NAME = "tcc.critical_delay.crit_chance";
    private static final String FIRE_RATE_NAME = "tcc.critical_delay.fire_rate";

    public CriticalDelay(Properties properties) {
        super(properties);
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity) {
        double critChanceBoost = TaczCuriosConfig.COMMON.criticalDelayCritChanceBoost.get();
        double fireRateReduction = TaczCuriosConfig.COMMON.criticalDelayFireRateReduction.get();

        AttributeHelper.applyModifier(livingEntity, AttributeHelper.CRIT_CHANCE, critChanceBoost, CRIT_CHANCE_UUID, CRIT_CHANCE_NAME, AttributeModifier.Operation.MULTIPLY_BASE);
        AttributeHelper.applyModifier(livingEntity, AttributeHelper.ROUNDS_PER_MINUTE, fireRateReduction, FIRE_RATE_UUID, FIRE_RATE_NAME, AttributeModifier.Operation.MULTIPLY_BASE);
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.CRIT_CHANCE, CRIT_CHANCE_UUID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.ROUNDS_PER_MINUTE, FIRE_RATE_UUID);
    }

    @Override
    public void curioTick(top.theillusivec4.curios.api.SlotContext slotContext, ItemStack stack) {
        applyEffects(slotContext.entity());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        tooltip.add(Component.literal(""));

        double critChanceBoost = TaczCuriosConfig.COMMON.criticalDelayCritChanceBoost.get() * 100;
        double fireRateReduction = TaczCuriosConfig.COMMON.criticalDelayFireRateReduction.get() * 100;
        tooltip.add(Component.translatable("item.tcc.critical_delay.effect",
                String.format("%+.0f", critChanceBoost), String.format("%+.0f", fireRateReduction))
            .withStyle(ChatFormatting.GOLD));

        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable("tcc.tooltip.slot"));
        tooltip.add(Component.translatable("tcc.tooltip.rarity.rare"));
    }

    @Override
    public void applyGunSwitchEffect(LivingEntity livingEntity) {
        applyEffects(livingEntity);
    }
}
