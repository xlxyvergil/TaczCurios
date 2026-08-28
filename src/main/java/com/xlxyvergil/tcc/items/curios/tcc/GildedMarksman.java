package com.xlxyvergil.tcc.items.curios.tcc;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.items.TccCurioItem;
import com.xlxyvergil.tcc.util.FusionUpgradeUtil;
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
 * 镀层准确射手 - 手枪饰品
 * 手持手枪时，目标每有一种负面效果，伤害直接乘算（LivingHurtEvent处理）
 */
public class GildedMarksman extends TccCurioItem {

    public GildedMarksman(Properties properties) {
        super(properties);
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        // 无基础属性，伤害乘算在其他处理器中处理
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
    }

    @Override
    public java.util.List<String> getWeaponTypeRestriction() {
        return java.util.List.of("pistol");
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.literal(""));
        double perHarmful = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.gildedMarksmanPerHarmful.get() ) * 100;
        tooltip.add(Component.translatable("item.tcc.gilded_marksman.effect",
                String.format("%+.0f", perHarmful))
            .withStyle(ChatFormatting.WHITE));
        tooltip.add(Component.literal(""));
        
    }


}
