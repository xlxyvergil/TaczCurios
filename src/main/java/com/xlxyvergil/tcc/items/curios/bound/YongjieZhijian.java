package com.xlxyvergil.tcc.items.curios.bound;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.items.BoundCurioItem;
import com.xlxyvergil.tcc.util.GunTypeChecker;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class YongjieZhijian extends BoundCurioItem {
    private static final UUID CRIT_CHANCE_UUID = UUID.fromString("a81d5c7e-9b3f-4e62-b8d5-3c7a1f6e8d2b");
    private static final UUID CRIT_DAMAGE_UUID = UUID.fromString("c92e6f8d-1a4b-5f73-c9e6-4d8b2f7a0e3c");
    private static final UUID LUCK_UUID = UUID.fromString("8acb73d0-e2be-4b76-ab94-aeab82337608");

    public YongjieZhijian(Properties properties) {
        super(properties);
    }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        super.onEquip(slotContext, prevStack, stack);
        AttributeHelper.applyModifier(slotContext.entity(), AttributeHelper.LUCK,
            TaczCuriosConfig.COMMON.yongjieZhijianLuck.get(), LUCK_UUID,
            "tcc.yongjie_zhijian.luck", AttributeModifier.Operation.ADDITION);
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        if (matchesRestriction(livingEntity)) {
            int luck = (int) livingEntity.getAttributeValue(AttributeHelper.LUCK);
            double critChance = Math.round(luck * TaczCuriosConfig.COMMON.yongjieZhijianCritChancePerLuck.get() * 10000.0) / 10000.0;
            double critDamage = Math.round(luck * TaczCuriosConfig.COMMON.yongjieZhijianCritDamagePerLuck.get() * 10000.0) / 10000.0;

            AttributeHelper.applyModifier(livingEntity, AttributeHelper.CRIT_CHANCE,
                critChance, CRIT_CHANCE_UUID,
                "tcc.yongjie_zhijian.crit_chance", AttributeModifier.Operation.ADDITION);
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.CRIT_DAMAGE,
                critDamage, CRIT_DAMAGE_UUID,
                "tcc.yongjie_zhijian.crit_damage", AttributeModifier.Operation.ADDITION);
        } else {
            removeEffects(livingEntity);
        }
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.CRIT_CHANCE, CRIT_CHANCE_UUID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.CRIT_DAMAGE, CRIT_DAMAGE_UUID);
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        super.onUnequip(slotContext, newStack, stack);
        AttributeHelper.removeModifier(slotContext.entity(), AttributeHelper.LUCK, LUCK_UUID);
    }

    @Override
    protected boolean isBoundItem() {
        return true;
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        applyEffects(slotContext.entity(), stack);
    }

    @Override
    public List<String> getWeaponTypeRestriction() {
        return List.of("smg");
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        tooltip.add(Component.literal(""));

        int luck = TaczCuriosConfig.COMMON.yongjieZhijianLuck.get();
        double critChance = luck * TaczCuriosConfig.COMMON.yongjieZhijianCritChancePerLuck.get();
        double critDamage = luck * TaczCuriosConfig.COMMON.yongjieZhijianCritDamagePerLuck.get();
        tooltip.add(formatModifierTooltip(luck, "%.0f", Component.translatable(AttributeHelper.LUCK.getDescriptionId()))
                .withStyle(ChatFormatting.WHITE));
        tooltip.add(formatModifierTooltip(critChance, "%.2f", Component.translatable(AttributeHelper.CRIT_CHANCE.getDescriptionId()))
                .withStyle(ChatFormatting.WHITE));
        tooltip.add(formatModifierTooltip(critDamage, "%.2f", Component.translatable(AttributeHelper.CRIT_DAMAGE.getDescriptionId()))
                .withStyle(ChatFormatting.WHITE));

        tooltip.add(Component.translatable("tcc.tooltip.affected_by_luck")
            .withStyle(ChatFormatting.LIGHT_PURPLE));

        tooltip.add(Component.literal(""));
        appendBoundPlayer(stack, tooltip);
    }
}
