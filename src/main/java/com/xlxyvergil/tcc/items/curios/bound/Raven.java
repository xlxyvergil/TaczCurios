package com.xlxyvergil.tcc.items.curios.bound;

import com.xlxyvergil.tcc.attribute.TccAttributes;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.helpers.ImaginaryResistanceHelper;
import com.xlxyvergil.tcc.util.ItemNbtHelper;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.items.BoundCurioItem;
import com.xlxyvergil.tcc.util.ItemNbtHelper;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.neoforged.fml.ModList;
import net.minecraft.core.registries.BuiltInRegistries;
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

import com.xlxyvergil.tcc.TaczCurios;
public class Raven extends BoundCurioItem {
    private static final ResourceLocation ARMOR_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "raven_3d18c48e_78d6");
    private static final ResourceLocation MOVE_SPEED_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "raven_c2c51883_0e08");
    private static final ResourceLocation IMAGINARY_RESISTANCE_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "raven_22b2a8f2_b1fa");

    public Raven(Properties properties) {
        super(properties);
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        // 登记该饰品施加的修饰符 ID → 来源饰品，供客户端属性面板显示来源图标。
        AttributeHelper.registerSourceItem(ARMOR_ID, stack.getItem());
        AttributeHelper.registerSourceItem(MOVE_SPEED_ID, stack.getItem());
        AttributeHelper.registerSourceItem(IMAGINARY_RESISTANCE_ID, stack.getItem());
        // 虚数抗性不受武器类型限制，装备即生效。
        ItemStack equipped = findEquippedStack(livingEntity);
        CompoundTag tag = ItemNbtHelper.getTag(equipped);
        double total = ImaginaryResistanceHelper.calculateTotalResistance(1, tag);
        AttributeHelper.applyModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE, total, IMAGINARY_RESISTANCE_ID, AttributeModifier.Operation.ADD_VALUE);

        if (matchesRestriction(livingEntity)) {
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.ARMOR, TaczCuriosConfig.COMMON.ravenArmorMultiplier.get(), ARMOR_ID, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.MOVEMENT_SPEED, TaczCuriosConfig.COMMON.ravenSpeedMultiplier.get(), MOVE_SPEED_ID, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        }
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.ARMOR, ARMOR_ID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.MOVEMENT_SPEED, MOVE_SPEED_ID);
        AttributeHelper.removeModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE, IMAGINARY_RESISTANCE_ID);
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
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity entity = slotContext.entity();
        if (entity.level().isClientSide) return;

        if (!matchesRestriction(entity)) return;

        if (entity.tickCount % TaczCuriosConfig.COMMON.ravenInvisRefreshInterval.get() == 0) {
            int duration = TaczCuriosConfig.COMMON.ravenInvisDuration.get();
            entity.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, duration, 0, false, false, true));

            if (ModList.get().isLoaded("irons_spellbooks")) {
                Holder<MobEffect> trueInvis = BuiltInRegistries.MOB_EFFECT.getHolder(ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "true_invisibility")).orElse(null);
                if (trueInvis != null) {
                    entity.addEffect(new MobEffectInstance(trueInvis, duration, 0, false, false, true));
                }
            }
        }

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
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        Level level = context.level();
        super.appendHoverText(stack, context, tooltip, flag);

        tooltip.add(Component.literal(""));

        double armorBoost = TaczCuriosConfig.COMMON.ravenArmorMultiplier.get() * 100;
        double speedBoost = TaczCuriosConfig.COMMON.ravenSpeedMultiplier.get() * 100;
        double invisIntervalSecs = TaczCuriosConfig.COMMON.ravenInvisRefreshInterval.get() / 20.0;
        double invisDurationSecs = TaczCuriosConfig.COMMON.ravenInvisDuration.get() / 20.0;

        appendImaginaryResistance(stack, tooltip);

        tooltip.add(formatModifierTooltip(armorBoost, "%.0f%%", Component.translatable(AttributeHelper.ARMOR.value().getDescriptionId()))
                .withStyle(ChatFormatting.GOLD));
        tooltip.add(formatModifierTooltip(speedBoost, "%.0f%%", Component.translatable(AttributeHelper.MOVEMENT_SPEED.value().getDescriptionId()))
                .withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.translatable("item.tcc.raven.special_invis",
                String.format("%.1f", invisIntervalSecs),
                String.format("%.1f", invisDurationSecs))
            .withStyle(ChatFormatting.WHITE));

        appendBoundPlayer(stack, tooltip);

        tooltip.add(Component.literal(""));

    }
}
