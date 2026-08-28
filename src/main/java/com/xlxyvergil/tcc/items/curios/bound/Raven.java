package com.xlxyvergil.tcc.items.curios.bound;

import com.xlxyvergil.tcc.attribute.TccAttributes;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.helpers.ImaginaryResistanceHelper;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.items.BoundCurioItem;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio.DropRule;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class Raven extends BoundCurioItem {
    private static final UUID ARMOR_UUID = UUID.fromString("3d18c48e-0b11-4cb9-ae4e-55f9e1bf78d6");
    private static final UUID MOVE_SPEED_UUID = UUID.fromString("c2c51883-9c72-46bf-9f46-2d4a622b0e08");
    private static final UUID IMAGINARY_RESISTANCE_UUID = UUID.fromString("22b2a8f2-1f8d-4b32-b243-5021d626b1fa");

    public Raven(Properties properties) {
        super(properties);
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        if (matchesRestriction(livingEntity)) {
            ItemStack equipped = findEquippedStack(livingEntity);
            CompoundTag tag = equipped.getTag();
            double total = ImaginaryResistanceHelper.calculateTotalResistance(1, tag);

            AttributeHelper.applyModifier(livingEntity, AttributeHelper.ARMOR, TaczCuriosConfig.COMMON.ravenArmorMultiplier.get(), ARMOR_UUID,
                "tcc.raven.armor", AttributeModifier.Operation.MULTIPLY_TOTAL);
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.MOVEMENT_SPEED, TaczCuriosConfig.COMMON.ravenSpeedMultiplier.get(), MOVE_SPEED_UUID,
                "tcc.raven.movement_speed", AttributeModifier.Operation.MULTIPLY_BASE);
            AttributeHelper.applyModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get(), total, IMAGINARY_RESISTANCE_UUID,
                "tcc.raven.imaginary_resistance", AttributeModifier.Operation.ADDITION);
        }
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.ARMOR, ARMOR_UUID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.MOVEMENT_SPEED, MOVE_SPEED_UUID);
        AttributeHelper.removeModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get(), IMAGINARY_RESISTANCE_UUID);
    }

    @Override
    public java.util.List<String> getWeaponTypeRestriction() {
        return java.util.List.of("sniper");
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
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity entity = slotContext.entity();
        if (entity.level().isClientSide) return;

        // 仅手持狙击枪时保持隐身
        if (!matchesRestriction(entity)) return;

        // 每N秒重新施加隐身（持续刷新）
        if (entity.tickCount % TaczCuriosConfig.COMMON.ravenInvisRefreshInterval.get() == 0) {
            int duration = TaczCuriosConfig.COMMON.ravenInvisDuration.get();
            entity.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, duration, 0, false, false, true));

            // 如果加载了铁魔法，同时施加真实隐身（完全阻止怪物追踪）
            if (ModList.get().isLoaded("irons_spellbooks")) {
                MobEffect trueInvis = ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation("irons_spellbooks", "true_invisibility"));
                if (trueInvis != null) {
                    entity.addEffect(new MobEffectInstance(trueInvis, duration, 0, false, false, true));
                }
            }
        }

        // 攻击后N秒破除隐身
        int lastHurtTs = entity.getLastHurtMobTimestamp();
        int breakDelay = TaczCuriosConfig.COMMON.ravenInvisBreakDelay.get();
        if (lastHurtTs > 0 && entity.tickCount - lastHurtTs == breakDelay) {
            entity.removeEffect(MobEffects.INVISIBILITY);
        }
    }

    public static boolean hasEquipped(LivingEntity livingEntity) {
        return !findEquippedStack(livingEntity).isEmpty();
    }

    private static ItemStack findEquippedStack(LivingEntity livingEntity) {
        return CurioSearchHelper.findFirstEquippedStack(livingEntity, stack -> stack.getItem() instanceof Raven);
    }

    

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        tooltip.add(Component.literal(""));

        double armorBoost = TaczCuriosConfig.COMMON.ravenArmorMultiplier.get() * 100;
        double speedBoost = TaczCuriosConfig.COMMON.ravenSpeedMultiplier.get() * 100;
        double invisIntervalSecs = TaczCuriosConfig.COMMON.ravenInvisRefreshInterval.get() / 20.0;
        double invisDurationSecs = TaczCuriosConfig.COMMON.ravenInvisDuration.get() / 20.0;

        appendImaginaryResistance(stack, tooltip);

        tooltip.add(formatModifierTooltip(armorBoost, "%.0f%%", Component.translatable(AttributeHelper.ARMOR.getDescriptionId()))
                .withStyle(ChatFormatting.GOLD));
        tooltip.add(formatModifierTooltip(speedBoost, "%.0f%%", Component.translatable(AttributeHelper.MOVEMENT_SPEED.getDescriptionId()))
                .withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.translatable("item.tcc.raven.special_invis",
                String.format("%.1f", invisIntervalSecs),
                String.format("%.1f", invisDurationSecs))
            .withStyle(ChatFormatting.WHITE));

        appendBoundPlayer(stack, tooltip);

        tooltip.add(Component.literal(""));

    }
}
