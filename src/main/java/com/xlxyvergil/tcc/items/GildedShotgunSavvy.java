package com.xlxyvergil.tcc.items;

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
 * 镀层通晓霰弹枪 - 霰弹饰品
 * 手持霰弹枪时，目标每有一种负面效果，伤害直接乘算（LivingHurtEvent处理）
 */
public class GildedShotgunSavvy extends BaseCurioItem {

    public GildedShotgunSavvy(Properties properties) {
        super(properties);
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity) {
        // 无基础属性，伤害乘算在TccEventHandler.onLivingHurt中处理
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.literal(""));
        double perHarmful = TaczCuriosConfig.COMMON.gildedShotgunSavvyPerHarmful.get() * 100;
        tooltip.add(Component.translatable("item.tcc.gilded_shotgun_savvy.effect",
                String.format("%+.0f", perHarmful))
            .withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable("tcc.tooltip.slot"));
        tooltip.add(Component.translatable("tcc.tooltip.rarity.epic"));
    }

    @Override
    public void applyGunSwitchEffect(LivingEntity livingEntity) {
        applyEffects(livingEntity);
    }
}
