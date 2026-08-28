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
 * 破片射击 - 霰弹枪饰品（击杀触发Buff期间）
 * 基础：暴击伤害99%，击杀→Buff期间暴击伤害提升Xs，不叠加
 */
public class FragmentShot extends TccCurioItem {

    public FragmentShot(Properties properties) {
        super(properties);
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        // 不再有装备常驻效果，+99%暴击伤害由击杀Buff提供
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        // 不再有装备常驻效果需要清除
    }

    @Override
    public java.util.List<String> getWeaponTypeRestriction() {
        return java.util.List.of("shotgun");
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.literal(""));
        double baseCritDmg = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.fragmentShotBaseCritDamage.get() ) * 100;
        int duration = TaczCuriosConfig.COMMON.fragmentShotDuration.get();
        tooltip.add(Component.translatable("item.tcc.fragment_shot.effect",
                String.format("%+.0f", baseCritDmg), duration)
            .withStyle(ChatFormatting.BLUE));
        tooltip.add(Component.literal(""));
        
    }

}
