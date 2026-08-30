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

public class LimingZhiShao extends BoundCurioItem {
    private static final UUID ATTACK_SPEED_UUID = UUID.fromString("4a08b49f-20ce-49db-a565-6debdef40c3e");
    private static final UUID ATTACK_DAMAGE_UUID = UUID.fromString("1df8d94e-edfc-4cf3-8169-9330116aa8d0");
    private static final UUID CRIT_CHANCE_UUID = UUID.fromString("07a0819e-c85f-42a6-bdd3-295d9c64b79d");
    private static final UUID IMAGINARY_RESISTANCE_UUID = UUID.fromString("6f4b2c9e-7a1d-4e8f-b3c6-9d2e1f4a7b35");

    private static double attackSpeedPct() {
        return TaczCuriosConfig.COMMON.limingZhiShaoAttackSpeedPercent.get();
    }

    private static double attackDamagePct() {
        return TaczCuriosConfig.COMMON.limingZhiShaoAttackDamagePercent.get();
    }

    private static double critChance() {
        return TaczCuriosConfig.COMMON.limingZhiShaoCritChancePercent.get();
    }

    public LimingZhiShao(Properties properties) {
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
                stack -> stack.getItem() instanceof LimingZhiShao).isEmpty();
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        ItemStack equipped = CurioSearchHelper.findFirstEquippedStack(livingEntity,
                s -> s.getItem() instanceof LimingZhiShao);
        CompoundTag tag = equipped.getTag();
        double total = 1.0
                + ImaginaryResistanceHelper.getExtraResistanceFromProgress(tag);
        AttributeHelper.applyModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get(),
                total, IMAGINARY_RESISTANCE_UUID,
                "tcc.liming_zhi_shao.imaginary_resistance", AttributeModifier.Operation.ADDITION);
        if (matchesRestriction(livingEntity)) {
            AttributeHelper.applyModifier(livingEntity, Attributes.ATTACK_SPEED,
                    attackSpeedPct(), ATTACK_SPEED_UUID,
                    "tcc.dawn.attack_speed", AttributeModifier.Operation.MULTIPLY_BASE);
            AttributeHelper.applyModifier(livingEntity, Attributes.ATTACK_DAMAGE,
                    attackDamagePct(), ATTACK_DAMAGE_UUID,
                    "tcc.dawn.attack_damage", AttributeModifier.Operation.MULTIPLY_BASE);
            if (critChance() > 0) {
                AttributeHelper.applyModifier(livingEntity, AttributeHelper.CRIT_CHANCE,
                        critChance(), CRIT_CHANCE_UUID,
                        "tcc.dawn.crit_chance", AttributeModifier.Operation.ADDITION);
            }
        } else {
            removeEffects(livingEntity);
        }
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get(), IMAGINARY_RESISTANCE_UUID);
        AttributeHelper.removeModifier(livingEntity, Attributes.ATTACK_SPEED, ATTACK_SPEED_UUID);
        AttributeHelper.removeModifier(livingEntity, Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE_UUID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.CRIT_CHANCE, CRIT_CHANCE_UUID);
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
        if (critChance() > 0) {
            tooltip.add(formatModifierTooltip(critChance() * 100, "%.0f%%", Component.translatable(AttributeHelper.CRIT_CHANCE.getDescriptionId()))
                    .withStyle(ChatFormatting.GOLD));
        }
        appendBoundPlayer(stack, tooltip);
    }
}
