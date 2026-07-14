package com.xlxyvergil.tcc.items.curios;

import com.xlxyvergil.tcc.attribute.TccAttributes;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.evolution.EvolutionRegistry;
import com.xlxyvergil.tcc.helpers.ImaginaryResistanceHelper;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.util.BaseCurioItem;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import com.xlxyvergil.tcc.util.EntityConditionHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class Xiora extends BaseCurioItem {

    private static final UUID ARMOR_UUID = UUID.fromString("1b0eb9dc-4f2c-4b2f-9e80-cb6c3b0a9b3c");
    private static final UUID MOVE_SPEED_UUID = UUID.fromString("d8a8f4c6-1a12-4c3a-9ee2-7b190f0a0cf7");
    private static final UUID IMAGINARY_RESISTANCE_UUID = UUID.fromString("8f21d12f-2c90-4c8f-a3f0-6bf0f64b4fdf");

    public Xiora(Properties properties) {
        super(properties);
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity) {
        ItemStack equipped = findEquippedStack(livingEntity);
        CompoundTag tag = equipped.getTag();
        double total = ImaginaryResistanceHelper.calculateTotalResistance(TaczCuriosConfig.COMMON.xioraBaseResistance.get(), tag);

        AttributeHelper.applyModifier(livingEntity, AttributeHelper.ARMOR, TaczCuriosConfig.COMMON.xioraArmorMultiplier.get(), ARMOR_UUID,
            "tcc.xiora.armor", AttributeModifier.Operation.MULTIPLY_TOTAL);
        AttributeHelper.applyModifier(livingEntity, AttributeHelper.MOVEMENT_SPEED, TaczCuriosConfig.COMMON.xioraSpeedMultiplier.get(), MOVE_SPEED_UUID,
            "tcc.xiora.movement_speed", AttributeModifier.Operation.MULTIPLY_BASE);
        AttributeHelper.applyModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get(), total, IMAGINARY_RESISTANCE_UUID,
            "tcc.xiora.imaginary_resistance", AttributeModifier.Operation.ADDITION);
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.ARMOR, ARMOR_UUID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.MOVEMENT_SPEED, MOVE_SPEED_UUID);
        AttributeHelper.removeModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get(), IMAGINARY_RESISTANCE_UUID);
    }

    @Override
    public void applyGunSwitchEffect(LivingEntity livingEntity) {
        applyEffects(livingEntity);
    }

    public static boolean hasEquipped(LivingEntity livingEntity) {
        return !findEquippedStack(livingEntity).isEmpty();
    }

    private static ItemStack findEquippedStack(LivingEntity livingEntity) {
        return CurioSearchHelper.findFirstEquippedStack(livingEntity, stack -> stack.getItem() instanceof Xiora);
    }

    

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        tooltip.add(Component.literal(""));

        double armorBoost = TaczCuriosConfig.COMMON.xioraArmorMultiplier.get() * 100;
        double speedBoost = TaczCuriosConfig.COMMON.xioraSpeedMultiplier.get() * 100;

        CompoundTag tag = stack.getTag();
        double extra = ImaginaryResistanceHelper.getExtraResistanceFromProgress(tag);
        double total = TaczCuriosConfig.COMMON.xioraBaseResistance.get() + extra;
        if (level != null && level.isClientSide()) {
            Player player = Minecraft.getInstance().player;
            if (player != null && hasEquipped(player)) {
                total = player.getAttributeValue(TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get());
            }
        }

        tooltip.add(Component.translatable("tcc.tooltip.imaginary_resistance", String.format("%.0f", total))
            .withStyle(ChatFormatting.GOLD));

        tooltip.add(Component.translatable("attribute.modifier.plus.1",
                String.format("%+.0f", armorBoost),
                Component.translatable(AttributeHelper.ARMOR.getDescriptionId()))
                .withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.translatable("attribute.modifier.plus.1",
                String.format("%+.0f", speedBoost),
                Component.translatable(AttributeHelper.MOVEMENT_SPEED.getDescriptionId()))
                .withStyle(ChatFormatting.GOLD));

        tooltip.add(Component.literal(""));

        tooltip.add(Component.translatable("tcc.tooltip.rarity.rare"));
    }
}
