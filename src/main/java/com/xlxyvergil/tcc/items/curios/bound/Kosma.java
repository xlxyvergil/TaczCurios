package com.xlxyvergil.tcc.items.curios.bound;

import com.xlxyvergil.tcc.attribute.TccAttributes;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.helpers.ImaginaryResistanceHelper;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.items.BoundCurioItem;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import top.theillusivec4.curios.api.SlotContext;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class Kosma extends BoundCurioItem {
    private static final UUID ATTACK_SPEED_UUID = UUID.fromString("e46b57b8-e3ca-436c-bc30-b2015ce7666e");
    private static final UUID ATTACK_DAMAGE_UUID = UUID.fromString("9bd59d29-ab0c-4c33-bef6-dd78a678b938");
    private static final UUID IMAGINARY_RESISTANCE_UUID = UUID.fromString("b8d1e4a6-2f9c-4e7b-a5d3-1c6e8f4b2d90");

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
    public List<String> getWeaponTypeRestriction() {
        return List.of("melee");
    }

    public static boolean isEquipped(LivingEntity entity) {
        return !CurioSearchHelper.findFirstEquippedStack(entity,
                stack -> stack.getItem() instanceof Kosma).isEmpty();
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        ItemStack equipped = CurioSearchHelper.findFirstEquippedStack(livingEntity,
                s -> s.getItem() instanceof Kosma);
        CompoundTag tag = equipped.getTag();
        double total = 1.0
                + ImaginaryResistanceHelper.getExtraResistanceFromProgress(tag);
        AttributeHelper.applyModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get(),
                total, IMAGINARY_RESISTANCE_UUID,
                "tcc.kosma.imaginary_resistance", AttributeModifier.Operation.ADDITION);
        if (matchesRestriction(livingEntity)) {
            AttributeHelper.applyModifier(livingEntity, Attributes.ATTACK_SPEED,
                    attackSpeedPct(), ATTACK_SPEED_UUID,
                    "tcc.dawn.attack_speed", AttributeModifier.Operation.MULTIPLY_BASE);
            AttributeHelper.applyModifier(livingEntity, Attributes.ATTACK_DAMAGE,
                    attackDamagePct(), ATTACK_DAMAGE_UUID,
                    "tcc.dawn.attack_damage", AttributeModifier.Operation.MULTIPLY_BASE);
        } else {
            removeEffects(livingEntity);
        }
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get(), IMAGINARY_RESISTANCE_UUID);
        AttributeHelper.removeModifier(livingEntity, Attributes.ATTACK_SPEED, ATTACK_SPEED_UUID);
        AttributeHelper.removeModifier(livingEntity, Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE_UUID);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        applyEffects(slotContext.entity(), stack);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        appendImaginaryResistance(stack, tooltip);
        tooltip.add(formatModifierTooltip(attackSpeedPct() * 100, "%.0f%%", Component.translatable(Attributes.ATTACK_SPEED.getDescriptionId()))
                .withStyle(ChatFormatting.GOLD));
        tooltip.add(formatModifierTooltip(attackDamagePct() * 100, "%.0f%%", Component.translatable(AttributeHelper.ATTACK_DAMAGE.getDescriptionId()))
                .withStyle(ChatFormatting.GOLD));
        appendBoundPlayer(stack, tooltip);
    }
}
