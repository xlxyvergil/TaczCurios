package com.xlxyvergil.tcc.items.curios;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.util.BaseCurioItem;
import com.xlxyvergil.tcc.util.GunTypeChecker;

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

    private static final UUID BASE_CRIT_CHANCE_UUID = UUID.fromString("b1c2d3e4-7011-4000-8000-000000000001");
    private static final String BASE_CRIT_CHANCE_NAME = "tcc.gilded_steel_slash.base_crit_chance";

    public GildedSteelSlash(Properties properties) {
        super(properties);
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity) {
        double baseCritChance = TaczCuriosConfig.COMMON.gildedSteelSlashCritChanceBase.get();
        AttributeHelper.applyModifier(livingEntity, AttributeHelper.CRIT_CHANCE, baseCritChance, BASE_CRIT_CHANCE_UUID, BASE_CRIT_CHANCE_NAME, AttributeModifier.Operation.MULTIPLY_BASE);
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.CRIT_CHANCE, BASE_CRIT_CHANCE_UUID);
    }

    @Override
    public void curioTick(top.theillusivec4.curios.api.SlotContext slotContext, ItemStack stack) {
        applyEffects(slotContext.entity());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.literal(""));
        double baseCritChance = TaczCuriosConfig.COMMON.gildedSteelSlashCritChanceBase.get() * 100;
        double buffCritDmg = TaczCuriosConfig.COMMON.gildedSteelSlashCritDamagePerLevel.get() * 100;
        int duration = TaczCuriosConfig.COMMON.gildedSteelSlashDuration.get();
        int maxStacks = TaczCuriosConfig.COMMON.gildedSteelSlashMaxStacks.get();
        tooltip.add(Component.translatable("item.tcc.gilded_steel_slash.effect",
                String.format("%+.0f", baseCritChance), String.format("%+.0f", buffCritDmg), duration, maxStacks)
            .withStyle(ChatFormatting.WHITE));
        tooltip.add(Component.literal(""));
        
        tooltip.add(Component.translatable("tcc.tooltip.rarity.epic"));
    }

    @Override
    public void applyGunSwitchEffect(LivingEntity livingEntity) {
        applyEffects(livingEntity);
    }
}
