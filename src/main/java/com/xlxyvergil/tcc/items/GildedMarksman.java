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
 * 镀层准确射手 - 手枪饰品
 * 手持手枪时，目标每有一种负面效果，伤害直接乘算（LivingHurtEvent处理）
 */
public class GildedMarksman extends BaseCurioItem {

    public GildedMarksman(Properties properties) {
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
        double perHarmful = TaczCuriosConfig.COMMON.gildedMarksmanPerHarmful.get() * 100;
        tooltip.add(Component.translatable("item.tcc.gilded_marksman.effect",
                String.format("%+.0f", perHarmful))
            .withStyle(ChatFormatting.WHITE));
        tooltip.add(Component.literal(""));
        
        tooltip.add(Component.translatable("tcc.tooltip.rarity.epic"));
    }

    @Override
    public void applyGunSwitchEffect(LivingEntity livingEntity) {
        applyEffects(livingEntity);
    }
}
