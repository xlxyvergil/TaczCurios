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

public class GildedArgonScope extends TccCurioItem {
    public GildedArgonScope(Properties properties) {
        super(properties);
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        // 不再有装备常驻效果，+120%由爆头Buff提供
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        // 不再有装备常驻效果需要清除
    }

    @Override
    public java.util.List<String> getWeaponTypeRestriction() {
        return java.util.List.of("rifle", "sniper", "smg", "mg", "rpg");
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.literal(""));
        int fusionLevel = FusionData.from(stack).level();
        double baseCrit = TaczCuriosConfig.COMMON.gildedArgonScopeBaseCritChance.get() * 100 * (fusionLevel + 1);
        double buffCrit = TaczCuriosConfig.COMMON.gildedArgonScopeCritChancePerLevel.get() * 100 * (fusionLevel + 1);
        int duration = TaczCuriosConfig.COMMON.gildedArgonScopeDuration.get();
        int maxStacks = TaczCuriosConfig.COMMON.gildedArgonScopeMaxStacks.get() / TaczCuriosConfig.COMMON.fusionMaxLevelEpic.get();
        tooltip.add(Component.translatable("item.tcc.gilded_argon_scope.effect_base",
                String.format("%+.0f", baseCrit), duration)
            .withStyle(ChatFormatting.WHITE));
        tooltip.add(Component.translatable("item.tcc.gilded_argon_scope.effect_kill",
                String.format("%+.0f", buffCrit), maxStacks, duration)
            .withStyle(ChatFormatting.WHITE));
        tooltip.add(Component.literal(""));
        
    }


}
