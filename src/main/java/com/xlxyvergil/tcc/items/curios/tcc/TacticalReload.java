package com.xlxyvergil.tcc.items.curios.tcc;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.items.TccCurioItem;
import com.xlxyvergil.tcc.util.FusionUpgradeUtil;
import com.xlxyvergil.tcc.util.GunTypeChecker;
import com.xlxyvergil.tcc.util.FusionData;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * 战术上膛 - 提升装填速度
 * 效果：提升装填速度（加算）
 */
public class TacticalReload extends TccCurioItem {

    // 属性修饰符UUID - 用于唯一标识修饰
    private static final UUID RELOAD_UUID = UUID.fromString("11efa1b9-0f1d-4dcb-bc3f-ff0a5dc42811");

    // 修饰符名
    private static final String RELOAD_NAME = "tcc.tactical_reload.reload";

    public TacticalReload(Properties properties) {
        super(properties);
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        if (matchesRestriction(livingEntity)) {
            double reloadBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.tacticalReloadSpeedBoost.get());
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.RELOAD_TIME, reloadBoost, RELOAD_UUID, RELOAD_NAME, AttributeModifier.Operation.ADDITION);
        }
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.RELOAD_TIME, RELOAD_UUID);
    }

    @Override
    public java.util.List<String> getWeaponTypeRestriction() {
        return java.util.List.of("shotgun");
    }

    /**
     * 添加物品的悬浮提示信息（鼠标悬停时显示）
     */
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);



        // 添加空行分隔
        tooltip.add(Component.literal(""));

        // 添加装备效果
        double reloadBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.tacticalReloadSpeedBoost.get() ) * 100;
        tooltip.add(Component.translatable("item.tcc.tactical_reload.effect", String.format("%+.0f", reloadBoost))
            .withStyle(ChatFormatting.AQUA));

        // 添加饰品槽位信息
        tooltip.add(Component.literal(""));
        

    }
    

}

