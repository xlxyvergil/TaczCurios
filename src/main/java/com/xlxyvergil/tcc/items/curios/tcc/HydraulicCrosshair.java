package com.xlxyvergil.tcc.items.curios.tcc;

import com.xlxyvergil.tcc.effect.HydraulicCrosshairEffect;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.items.TccCurioItem;
import com.xlxyvergil.tcc.util.FusionData;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class HydraulicCrosshair extends TccCurioItem {
    public HydraulicCrosshair(Properties properties) {
        super(properties);
    }

    @Override
    protected void declareModifiers(LivingEntity livingEntity, ItemStack stack) {
        // 不再有装备常驻效果，+135%暴击几率由爆头Buff提供
    }

    @Override
    protected void removeModifiers(LivingEntity livingEntity) {
        // 不再有装备常驻效果需要清除
    }

    @Override
    public java.util.List<String> getWeaponTypeRestriction() {
        return java.util.List.of("pistol");
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.literal(""));
        double baseCrit = FusionData.from(stack).getActualValue(HydraulicCrosshairEffect.VALUE_PER_LEVEL) * 100;
        int duration = TaczCuriosConfig.COMMON.hydraulicCrosshairDuration.get();
        tooltip.add(Component.translatable("item.tcc.hydraulic_crosshair.effect",
                String.format("%+.0f", baseCrit), duration)
            .withStyle(ChatFormatting.BLUE));
        tooltip.add(Component.literal(""));
        
    }
}
