package com.xlxyvergil.tcc.items.curios.bound;

import com.xlxyvergil.tcc.attribute.TccAttributes;
import com.xlxyvergil.tcc.helpers.ImaginaryResistanceHelper;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.items.BoundCurioItem;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio.DropRule;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class Fusheng extends BoundCurioItem {
    private static final UUID ARMOR_UUID = UUID.fromString("7108d2bf-ef7c-4c9c-89da-1057deec9c02");
    private static final UUID TOUGHNESS_UUID = UUID.fromString("9570537d-6e7a-4f40-b31e-3024518fddc1");
    private static final UUID IMAGINARY_RESISTANCE_UUID = UUID.fromString("d2e8b5a1-4c7f-4e9a-8b3d-6f1e2c9a4d67");

    public Fusheng(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean isBoundItem() {
        return true;
    }

    @Override
    public DropRule getDropRule(SlotContext slotContext, DamageSource source, int lootingLevel, boolean recentlyHit, ItemStack stack) {
        return DropRule.ALWAYS_KEEP;
    }

    @Override
    public List<String> getWeaponTypeRestriction() {
        return List.of("melee");
    }

    public static boolean isEquipped(LivingEntity entity) {
        return !CurioSearchHelper.findFirstEquippedStack(entity,
                stack -> stack.getItem() instanceof Fusheng).isEmpty();
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        ItemStack equipped = CurioSearchHelper.findFirstEquippedStack(livingEntity,
                s -> s.getItem() instanceof Fusheng);
        CompoundTag tag = equipped.getTag();
        double total = 1.0
                + ImaginaryResistanceHelper.getExtraResistanceFromProgress(tag);
        AttributeHelper.applyModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get(),
                total, IMAGINARY_RESISTANCE_UUID,
                "tcc.fusheng.imaginary_resistance", AttributeModifier.Operation.ADDITION);
        if (matchesRestriction(livingEntity)) {
            double pct = ImaginaryResistanceHelper.getResistanceValue(livingEntity) / 100.0;
            AttributeHelper.applyModifier(livingEntity, Attributes.ARMOR,
                    pct, ARMOR_UUID,
                    "tcc.transient.armor", AttributeModifier.Operation.MULTIPLY_TOTAL);
            AttributeHelper.applyModifier(livingEntity, Attributes.ARMOR_TOUGHNESS,
                    pct, TOUGHNESS_UUID,
                    "tcc.transient.toughness", AttributeModifier.Operation.MULTIPLY_TOTAL);
        } else {
            removeEffects(livingEntity);
        }
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get(), IMAGINARY_RESISTANCE_UUID);
        AttributeHelper.removeModifier(livingEntity, Attributes.ARMOR, ARMOR_UUID);
        AttributeHelper.removeModifier(livingEntity, Attributes.ARMOR_TOUGHNESS, TOUGHNESS_UUID);
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

        // 与繁星三阶段一致：客户端实时读取持有者虚数抗性，直显护甲/护甲韧性加成（加成百分比 = 虚数抗性值）
        double resistance = 0;
        if (level != null && level.isClientSide()) {
            Player player = Minecraft.getInstance().player;
            if (player != null) {
                resistance = player.getAttributeValue(TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get());
            }
        }
        tooltip.add(formatModifierTooltip(resistance, "%.0f%%", Component.translatable(Attributes.ARMOR.getDescriptionId()))
                .withStyle(ChatFormatting.GOLD));
        tooltip.add(formatModifierTooltip(resistance, "%.0f%%", Component.translatable(Attributes.ARMOR_TOUGHNESS.getDescriptionId()))
                .withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.translatable("tcc.tooltip.affected_by_imaginary_resistance")
                .withStyle(ChatFormatting.LIGHT_PURPLE));

        appendBoundPlayer(stack, tooltip);
    }
}
