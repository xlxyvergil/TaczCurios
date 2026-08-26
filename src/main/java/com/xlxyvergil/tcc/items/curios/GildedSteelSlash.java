package com.xlxyvergil.tcc.items.curios;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.util.BaseCurioItem;
import com.xlxyvergil.tcc.util.FusionUpgradeUtil;
import com.xlxyvergil.tcc.util.GunTypeChecker;
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
 * 镀层斩铁 - 近战饰品（击杀触发Buff，可叠加）
 * 基础：暴击几率+110%，击杀→Buff暴击伤害+30%/层（20s，可叠加4层）
 */
public class GildedSteelSlash extends BaseCurioItem {

    private static final UUID BASE_CRIT_CHANCE_UUID = UUID.fromString("4fa674c5-0f8a-4057-acfd-0388047c63c6");
    private static final String BASE_CRIT_CHANCE_NAME = "tcc.gilded_steel_slash.base_crit_chance";

    public GildedSteelSlash(Properties properties) {
        super(properties);
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        if (matchesRestriction(livingEntity)) {
            double baseCritChance = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.gildedSteelSlashCritChanceBase.get());
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.CRIT_CHANCE, baseCritChance, BASE_CRIT_CHANCE_UUID, BASE_CRIT_CHANCE_NAME, AttributeModifier.Operation.MULTIPLY_BASE);
        } else {
            removeEffects(livingEntity);
        }
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.CRIT_CHANCE, BASE_CRIT_CHANCE_UUID);
    }

    @Override
    public List<String> getWeaponTypeRestriction() {
        return List.of("melee");
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.literal(""));


        double baseCritChance = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.gildedSteelSlashCritChanceBase.get()) * 100;
        int fusionLevel = FusionData.from(stack).level();
        double buffCritDmg = TaczCuriosConfig.COMMON.gildedSteelSlashCritDamagePerLevel.get() * 100 * (fusionLevel + 1);
        int duration = TaczCuriosConfig.COMMON.gildedSteelSlashDuration.get();
        int maxStacks = TaczCuriosConfig.COMMON.gildedSteelSlashMaxStacks.get() / TaczCuriosConfig.COMMON.fusionMaxLevelEpic.get();
        tooltip.add(Component.translatable("item.tcc.gilded_steel_slash.effect_base",
                String.format("%+.0f", baseCritChance))
            .withStyle(ChatFormatting.WHITE));
        tooltip.add(Component.translatable("item.tcc.gilded_steel_slash.effect_kill",
                String.format("%+.0f", buffCritDmg), maxStacks, duration)
            .withStyle(ChatFormatting.WHITE));
        tooltip.add(Component.literal(""));
        
    }

}
