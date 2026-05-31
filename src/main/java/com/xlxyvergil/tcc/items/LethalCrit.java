package com.xlxyvergil.tcc.items;

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
 * 致命一击 - 步枪类饰品（步枪/狙击枪/冲锋枪/机枪/发射器）
 * 效果：暴击几率 +150%
 */
public class LethalCrit extends BaseCurioItem {

    private static final UUID CRIT_CHANCE_UUID = UUID.fromString("a1b2c3d4-2001-4000-8000-000000000001");

    private static final String CRIT_CHANCE_NAME = "tcc.lethal_crit.crit_chance";

    public LethalCrit(Properties properties) {
        super(properties);
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity) {
        if (GunTypeChecker.isHoldingDmgBoostGunType(livingEntity)) {
            double critChanceBoost = TaczCuriosConfig.COMMON.lethalCritCritChance.get();
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.CRIT_CHANCE, critChanceBoost, CRIT_CHANCE_UUID, CRIT_CHANCE_NAME, AttributeModifier.Operation.MULTIPLY_BASE);
        } else {
            AttributeHelper.removeModifier(livingEntity, AttributeHelper.CRIT_CHANCE, CRIT_CHANCE_UUID);
        }
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.CRIT_CHANCE, CRIT_CHANCE_UUID);
    }

    @Override
    public void curioTick(top.theillusivec4.curios.api.SlotContext slotContext, ItemStack stack) {
        applyEffects(slotContext.entity());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        tooltip.add(Component.literal(""));

        double critChanceBoost = TaczCuriosConfig.COMMON.lethalCritCritChance.get() * 100;
        tooltip.add(Component.translatable("item.tcc.lethal_crit.effect",
                String.format("%+.0f", critChanceBoost))
            .withStyle(ChatFormatting.BLUE));

        tooltip.add(Component.literal(""));
        
        tooltip.add(Component.translatable("tcc.tooltip.rarity.common"));
    }

    @Override
    public void applyGunSwitchEffect(LivingEntity livingEntity) {
        applyEffects(livingEntity);
    }
}
