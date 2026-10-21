package com.xlxyvergil.tcc.items.curios.bound;

import com.xlxyvergil.tcc.attribute.TccAttributes;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.helpers.ImaginaryResistanceHelper;
import com.xlxyvergil.tcc.registries.TccMobEffects;
import com.xlxyvergil.tcc.util.ItemNbtHelper;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.items.BoundCurioItem;
import com.xlxyvergil.tcc.util.ItemNbtHelper;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;

import javax.annotation.Nullable;
import java.util.List;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import net.minecraft.resources.ResourceLocation;
import com.xlxyvergil.tcc.TaczCurios;
public class Xiora extends BoundCurioItem {
    private static final ResourceLocation ARMOR_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "xiora_1b0eb9dc_9b3c");
    private static final ResourceLocation MOVE_SPEED_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "xiora_d8a8f4c6_0cf7");
    private static final ResourceLocation IMAGINARY_RESISTANCE_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "xiora_8f21d12f_4fdf");

    public Xiora(Properties properties) {
        super(properties);
    }

    @Override
    protected void declareModifiers(LivingEntity livingEntity, ItemStack stack) {
        // 虚数抗性不受武器类型限制，装备即生效。
        ItemStack equipped = findEquippedStack(livingEntity);
        CompoundTag tag = ItemNbtHelper.getTag(equipped);
        double total = ImaginaryResistanceHelper.calculateTotalResistance(1, tag);
        AttributeHelper.applyModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE, total, IMAGINARY_RESISTANCE_ID, AttributeModifier.Operation.ADD_VALUE);

        if (matchesRestriction(livingEntity)) {
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.ARMOR, TaczCuriosConfig.COMMON.xioraArmorMultiplier.get(), ARMOR_ID, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.MOVEMENT_SPEED, TaczCuriosConfig.COMMON.xioraSpeedMultiplier.get(), MOVE_SPEED_ID, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        }
    }

    @Override
    protected void removeModifiers(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.ARMOR, ARMOR_ID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.MOVEMENT_SPEED, MOVE_SPEED_ID);
        AttributeHelper.removeModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE, IMAGINARY_RESISTANCE_ID);
    }

    @Override
    public java.util.List<String> getWeaponTypeRestriction() {
        return java.util.List.of("sniper");
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity entity = slotContext.entity();
        if (entity.level().isClientSide) return;

        if (!matchesRestrictionEitherHand(entity)) return;

        // 气息遮蔽，按刷新间隔续期，卸下后自然到期（与渡鸦一致）
        if (entity.tickCount % TaczCuriosConfig.COMMON.xioraInvisRefreshInterval.get() == 0) {
            entity.addEffect(new MobEffectInstance(TccMobEffects.QI_XI_ZHE_BI,
                TaczCuriosConfig.COMMON.xioraInvisDuration.get(), 0, false, false, true));
        }
    }

    public static boolean isEquipped(LivingEntity livingEntity) {
        return !findEquippedStack(livingEntity).isEmpty();
    }

    private static ItemStack findEquippedStack(LivingEntity livingEntity) {
        return CurioSearchHelper.findFirstEquippedStack(livingEntity, stack -> stack.getItem() instanceof Xiora);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        Level level = context.level();
        super.appendHoverText(stack, context, tooltip, flag);

        tooltip.add(Component.literal(""));

        double armorBoost = TaczCuriosConfig.COMMON.xioraArmorMultiplier.get() * 100;
        double speedBoost = TaczCuriosConfig.COMMON.xioraSpeedMultiplier.get() * 100;

        appendImaginaryResistance(stack, tooltip);

        tooltip.add(formatModifierTooltip(armorBoost, "%.0f%%", Component.translatable(AttributeHelper.ARMOR.value().getDescriptionId()))
                .withStyle(ChatFormatting.GOLD));
        tooltip.add(formatModifierTooltip(speedBoost, "%.0f%%", Component.translatable(AttributeHelper.MOVEMENT_SPEED.value().getDescriptionId()))
                .withStyle(ChatFormatting.GOLD));

        double invisIntervalSecs = TaczCuriosConfig.COMMON.xioraInvisRefreshInterval.get() / 20.0;
        double invisDurationSecs = TaczCuriosConfig.COMMON.xioraInvisDuration.get() / 20.0;
        tooltip.add(Component.translatable("item.tcc.xiora.special_invis",
                String.format("%.1f", invisIntervalSecs),
                String.format("%.1f", invisDurationSecs))
            .withStyle(ChatFormatting.WHITE));

        tooltip.add(Component.translatable("tcc.tooltip.silent_movement")
                .withStyle(ChatFormatting.RED));

        tooltip.add(Component.literal(""));

        appendBoundPlayer(stack, tooltip);
    }
}
