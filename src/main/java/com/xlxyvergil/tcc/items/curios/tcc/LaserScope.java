package com.xlxyvergil.tcc.items.curios.tcc;

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

/**
 * 雷射瞄具：基础暴击几率120%，爆头命中→Buff期间暴击几率提升
 */
public class LaserScope extends TccCurioItem {

    public LaserScope(Properties properties) {
        super(properties);
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        // 不再有装备常驻效果，+120%暴击几率由爆头Buff提供
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        // 不再有装备常驻效果需要清除
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.literal(""));
        double baseCrit = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.laserScopeBaseCritChance.get() ) * 100;
        int duration = TaczCuriosConfig.COMMON.laserScopeDuration.get();
        tooltip.add(Component.translatable("item.tcc.laser_scope.effect",
                String.format("%+.0f", baseCrit), duration)
            .withStyle(ChatFormatting.AQUA));
        tooltip.add(Component.literal(""));
        
    }

    @Override
    public java.util.List<String> getWeaponTypeRestriction() {
        return java.util.List.of("shotgun");
    }
}
