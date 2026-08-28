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
 * 异况超量：目标每有一种负面效果，伤害+80%（LivingHurtEvent直接乘算）
 */
public class ConditionOverload extends TccCurioItem {

    public ConditionOverload(Properties properties) {
        super(properties);
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        // 无基础属性，伤害乘算在LivingHurtEvent中处理
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        // 无基础属性
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

    @Override
    public java.util.List<String> getWeaponTypeRestriction() {
        return java.util.List.of("melee");
    }

}
