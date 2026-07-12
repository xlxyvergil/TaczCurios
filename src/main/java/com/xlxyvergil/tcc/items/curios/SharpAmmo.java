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
 * 尖锐子弹 - 手枪饰品（击杀触发Buff）
 * 基础：暴击伤害+75%，击杀→Buff期间暴击伤害提升（9s，不叠加）
 */
public class SharpAmmo extends BaseCurioItem {

    public SharpAmmo(Properties properties) {
        super(properties);
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity) {
        // 不再有装备常驻效果，+75%暴击伤害由击杀Buff提供
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        // 不再有装备常驻效果需要清理
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.literal(""));
        double baseCritDmg = TaczCuriosConfig.COMMON.sharpAmmoBaseCritDamage.get() * 100;
        int duration = TaczCuriosConfig.COMMON.sharpAmmoDuration.get();
        tooltip.add(Component.translatable("item.tcc.sharp_ammo.effect",
                String.format("%+.0f", baseCritDmg), duration)
            .withStyle(ChatFormatting.AQUA));
        tooltip.add(Component.literal(""));
        
        tooltip.add(Component.translatable("tcc.tooltip.rarity.uncommon"));
    }
}
