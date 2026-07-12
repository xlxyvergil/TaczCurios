package com.xlxyvergil.tcc.items.curios;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.BaseCurioItem;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 液压准心 - 手枪饰品（爆头触发Buff）
 * 基础：暴击几率+135%，爆头命中→Buff期间暴击几率提升（9s，不叠加）
 */
public class HydraulicCrosshair extends BaseCurioItem {

    public HydraulicCrosshair(Properties properties) {
        super(properties);
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity) {
        // 不再有装备常驻效果，+135%暴击几率由爆头Buff提供
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        // 不再有装备常驻效果需要清理
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.literal(""));
        double baseCrit = TaczCuriosConfig.COMMON.hydraulicCrosshairBaseCritChance.get() * 100;
        int duration = TaczCuriosConfig.COMMON.hydraulicCrosshairDuration.get();
        tooltip.add(Component.translatable("item.tcc.hydraulic_crosshair.effect",
                String.format("%+.0f", baseCrit), duration)
            .withStyle(ChatFormatting.BLUE));
        tooltip.add(Component.literal(""));
        
        tooltip.add(Component.translatable("tcc.tooltip.rarity.common"));
    }
}
