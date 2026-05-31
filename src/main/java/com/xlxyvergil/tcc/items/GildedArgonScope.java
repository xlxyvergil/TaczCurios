package com.xlxyvergil.tcc.items;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.BaseCurioItem;
import com.xlxyvergil.tcc.util.GunTypeChecker;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 镀层氩晶瞄具 - 步枪饰品（爆头/爆头击杀触发不同Buff）
 * 爆头→+120%暴击几率Buff（12s，不叠加）
 * 爆头击杀→+40%/层暴击几率Buff（12s，最多5层）
 */
public class GildedArgonScope extends BaseCurioItem {

    public GildedArgonScope(Properties properties) {
        super(properties);
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity) {
        // 不再有装备常驻效果，+120%由爆头Buff提供
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        // 不再有装备常驻效果需要清理
    }

    @Override
    public void curioTick(top.theillusivec4.curios.api.SlotContext slotContext, ItemStack stack) {
        applyEffects(slotContext.entity());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.literal(""));
        double baseCrit = TaczCuriosConfig.COMMON.gildedArgonScopeBaseCritChance.get() * 100;
        double buffCrit = TaczCuriosConfig.COMMON.gildedArgonScopeCritChancePerLevel.get() * 100;
        double extraCrit = TaczCuriosConfig.COMMON.gildedArgonScopeHeadshotKillExtra.get() * 100;
        int duration = TaczCuriosConfig.COMMON.gildedArgonScopeDuration.get();
        int maxStacks = TaczCuriosConfig.COMMON.gildedArgonScopeMaxStacks.get();
        tooltip.add(Component.translatable("item.tcc.gilded_argon_scope.effect",
                String.format("%+.0f", baseCrit), String.format("%+.0f", buffCrit), String.format("%+.0f", extraCrit), duration, maxStacks)
            .withStyle(ChatFormatting.WHITE));
        tooltip.add(Component.literal(""));
        
        tooltip.add(Component.translatable("tcc.tooltip.rarity.epic"));
    }

    @Override
    public void applyGunSwitchEffect(LivingEntity livingEntity) {
        applyEffects(livingEntity);
    }
}
