package com.xlxyvergil.tcc.items.curios.bound;

import com.xlxyvergil.tcc.api.items.IFluidWalk;
import com.xlxyvergil.tcc.attribute.TccAttributes;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.helpers.ImaginaryResistanceHelper;
import com.xlxyvergil.tcc.util.ItemNbtHelper;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.items.BoundCurioItem;
import com.xlxyvergil.tcc.util.ItemNbtHelper;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.resources.ResourceLocation;
import com.xlxyvergil.tcc.TaczCurios;
public class Kosma extends BoundCurioItem implements IFluidWalk {
    private static final ResourceLocation ATTACK_SPEED_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "kosma_e46b57b8_666e");
    private static final ResourceLocation ATTACK_DAMAGE_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "kosma_9bd59d29_b938");
    private static final ResourceLocation IMAGINARY_RESISTANCE_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "kosma_b8d1e4a6_2d90");

    private static double attackSpeedPct() {
        return TaczCuriosConfig.COMMON.kosmaAttackSpeedPercent.get();
    }

    private static double attackDamagePct() {
        return TaczCuriosConfig.COMMON.kosmaAttackDamagePercent.get();
    }

    public Kosma(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean isBoundItem() {
        return true;
    }

    @Override
    public boolean canStandOnFluid(LivingEntity wearer, FluidState state) {
        return state.is(FluidTags.LAVA);
    }

    @Override
    public List<String> getWeaponTypeRestriction() {
        return List.of("melee");
    }

    public static boolean isEquipped(LivingEntity entity) {
        return !CurioSearchHelper.findFirstEquippedStack(entity,
                stack -> stack.getItem() instanceof Kosma).isEmpty();
    }

    @Override
    protected void declareModifiers(LivingEntity livingEntity, ItemStack stack) {
        ItemStack equipped = CurioSearchHelper.findFirstEquippedStack(livingEntity,
                s -> s.getItem() instanceof Kosma);
        CompoundTag tag = ItemNbtHelper.getTag(equipped);
        double total = 1.0
                + ImaginaryResistanceHelper.getExtraResistanceFromProgress(tag);
        AttributeHelper.applyModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE,
                total, IMAGINARY_RESISTANCE_ID, AttributeModifier.Operation.ADD_VALUE);
        if (matchesRestriction(livingEntity)) {
            AttributeHelper.applyModifier(livingEntity, Attributes.ATTACK_SPEED,
                    attackSpeedPct(), ATTACK_SPEED_ID, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
            AttributeHelper.applyModifier(livingEntity, Attributes.ATTACK_DAMAGE,
                    attackDamagePct(), ATTACK_DAMAGE_ID, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        } else {
            // 武器不匹配时只清理受限制的攻击加成，虚数抗性为无条件施加，需保留。
            AttributeHelper.removeModifier(livingEntity, Attributes.ATTACK_SPEED, ATTACK_SPEED_ID);
            AttributeHelper.removeModifier(livingEntity, Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE_ID);
        }
    }

    @Override
    protected void removeModifiers(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE, IMAGINARY_RESISTANCE_ID);
        AttributeHelper.removeModifier(livingEntity, Attributes.ATTACK_SPEED, ATTACK_SPEED_ID);
        AttributeHelper.removeModifier(livingEntity, Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE_ID);
    }

    @Override
    public boolean isEventDriven() {
        return true;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        Level level = context.level();
        super.appendHoverText(stack, context, tooltip, flag);
        appendImaginaryResistance(stack, tooltip);
        tooltip.add(formatModifierTooltip(attackSpeedPct() * 100, "%.0f%%", Component.translatable(Attributes.ATTACK_SPEED.value().getDescriptionId()))
                .withStyle(ChatFormatting.GOLD));
        tooltip.add(formatModifierTooltip(attackDamagePct() * 100, "%.0f%%", Component.translatable(AttributeHelper.ATTACK_DAMAGE.value().getDescriptionId()))
                .withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.translatable("tcc.tooltip.lava_walk")
                .withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.translatable("tcc.tooltip.fire_lava_immunity")
                .withStyle(ChatFormatting.RED));
        appendBoundPlayer(stack, tooltip);
    }
}
