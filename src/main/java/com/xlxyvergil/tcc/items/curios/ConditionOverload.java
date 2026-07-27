package com.xlxyvergil.tcc.items.curios;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.BaseCurioItem;
import com.xlxyvergil.tcc.util.FusionUpgradeUtil;
import com.xlxyvergil.tcc.util.GunTypeChecker;
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
 * 异况超量 - 近战饰品
 * 手持近战武器时，目标每有一种负面效果，伤害+80%（LivingHurtEvent直接乘算�?
 */
public class ConditionOverload extends BaseCurioItem {

    public ConditionOverload(Properties properties) {
        super(properties);
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        // 无基础属性，伤害乘算在LivingHurtEvent中处�?
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        // 无基础属�?
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.literal(""));
        double perHarmful = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.conditionOverloadPerHarmful.get() ) * 100;
        tooltip.add(Component.translatable("item.tcc.condition_overload.effect",
                String.format("%+.0f", perHarmful))
            .withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.literal(""));
        
    }

}
