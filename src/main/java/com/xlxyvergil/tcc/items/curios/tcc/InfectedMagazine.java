package com.xlxyvergil.tcc.items.curios.tcc;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.items.TccCurioItem;
import com.xlxyvergil.tcc.util.FusionData;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * 感染弹匣：提升弹匣容量、降低装填速度（加算）
 */
public class InfectedMagazine extends TccCurioItem {

    // 属性修饰符UUID - 用于唯一标识修饰
    private static final UUID MAGAZINE_CAPACITY_UUID = UUID.fromString("f7d6ce3b-7168-44d0-9637-c4eb2caf0fbc");
    private static final UUID RELOAD_UUID = UUID.fromString("fa325acb-cb87-4288-8d10-c3d637b9242c");

    // 修饰符名
    private static final String MAGAZINE_CAPACITY_NAME = "tcc.infected_magazine.magazine_capacity";
    private static final String RELOAD_NAME = "tcc.infected_magazine.reload";

    public InfectedMagazine(Properties properties) {
        super(properties);
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        if (matchesRestriction(livingEntity)) {
            double magazineCapacityBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.infectedMagazineCapacityBoost.get());
            double reloadDebuff = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.infectedMagazineReloadSpeedReduction.get());

            AttributeHelper.applyModifier(livingEntity, AttributeHelper.MAGAZINE_CAPACITY, magazineCapacityBoost, MAGAZINE_CAPACITY_UUID, MAGAZINE_CAPACITY_NAME, AttributeModifier.Operation.ADDITION);
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.RELOAD_TIME, reloadDebuff, RELOAD_UUID, RELOAD_NAME, AttributeModifier.Operation.ADDITION);
        }
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.MAGAZINE_CAPACITY, MAGAZINE_CAPACITY_UUID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.RELOAD_TIME, RELOAD_UUID);
    }


    @Override
    public java.util.List<String> getWeaponTypeRestriction() {
        return java.util.List.of("pistol");
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        tooltip.add(Component.literal(""));

        double magazineCapacityBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.infectedMagazineCapacityBoost.get() ) * 100;
        double reloadDebuff = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.infectedMagazineReloadSpeedReduction.get() ) * 100;
        tooltip.add(Component.translatable("item.tcc.infected_magazine.effect", 
                String.format("%+.0f", magazineCapacityBoost), String.format("%+.0f", reloadDebuff))
            .withStyle(ChatFormatting.GOLD));

        tooltip.add(Component.literal(""));

    }
    
}