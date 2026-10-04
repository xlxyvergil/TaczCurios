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

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class IslandBoomRaven extends BoundCurioItem {
    private static final UUID ARMOR_UUID = UUID.fromString("2dddf4c2-5d16-4f88-9e08-e5f9131c7b4e");
    private static final UUID MOVE_SPEED_UUID = UUID.fromString("1ed0c2f3-7bcd-4a1e-bc6f-13d1fcb6c7ad");
    private static final UUID IMAGINARY_RESISTANCE_UUID = UUID.fromString("90e98bd7-80b6-4e7f-8b1f-b6a0d74c3f78");

    public IslandBoomRaven(Properties properties) {
        super(properties);
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        // 登记该饰品施加的修饰符 UUID → 来源饰品，供客户端属性面板显示来源图标。
        AttributeHelper.registerSourceItem(ARMOR_UUID, stack.getItem());
        AttributeHelper.registerSourceItem(MOVE_SPEED_UUID, stack.getItem());
        AttributeHelper.registerSourceItem(IMAGINARY_RESISTANCE_UUID, stack.getItem());
        // 虚数抗性不受武器类型限制，装备即生效。
        ItemStack equipped = findEquippedStack(livingEntity);
        CompoundTag tag = equipped.getTag();
        double total = ImaginaryResistanceHelper.calculateTotalResistance(1, tag);
        AttributeHelper.applyModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get(), total, IMAGINARY_RESISTANCE_UUID,
            "tcc.island_boom_raven.imaginary_resistance", AttributeModifier.Operation.ADDITION);

        if (matchesRestriction(livingEntity)) {
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.ARMOR, TaczCuriosConfig.COMMON.islandBoomRavenArmorMultiplier.get(), ARMOR_UUID,
                "tcc.island_boom_raven.armor", AttributeModifier.Operation.MULTIPLY_TOTAL);
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.MOVEMENT_SPEED, TaczCuriosConfig.COMMON.islandBoomRavenSpeedMultiplier.get(), MOVE_SPEED_UUID,
                "tcc.island_boom_raven.movement_speed", AttributeModifier.Operation.MULTIPLY_BASE);
        }
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.ARMOR, ARMOR_UUID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.MOVEMENT_SPEED, MOVE_SPEED_UUID);
        AttributeHelper.removeModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get(), IMAGINARY_RESISTANCE_UUID);
        // 生命恢复改为无限时长，卸下时移除
        livingEntity.removeEffect(MobEffects.REGENERATION);
        // 隐身改为无限时长，卸下时移除
        livingEntity.removeEffect(MobEffects.INVISIBILITY);
        if (ModList.get().isLoaded("irons_spellbooks")) {
            MobEffect trueInvis = ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation("irons_spellbooks", "true_invisibility"));
            if (trueInvis != null) {
                livingEntity.removeEffect(trueInvis);
            }
        }
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

        // 隐身为永久（无限时长），周期性刷新作为保险，卸下时移除
        if (entity.tickCount % TaczCuriosConfig.COMMON.islandBoomRavenInvisRefreshInterval.get() == 0) {
            entity.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, MobEffectInstance.INFINITE_DURATION, 0, false, false, true));

            if (ModList.get().isLoaded("irons_spellbooks")) {
                MobEffect trueInvis = ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation("irons_spellbooks", "true_invisibility"));
                if (trueInvis != null) {
                    // 真实隐身保持原有时长（有限），按刷新间隔重复施加
                    entity.addEffect(new MobEffectInstance(trueInvis, TaczCuriosConfig.COMMON.islandBoomRavenInvisDuration.get(), 0, false, false, true));
                }
            }
        }

        // 生命恢复：与抗性提升一致，时长为无限、每 10t 刷新、卸下时移除
        if (entity.tickCount % 10 == 0) {
            entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, MobEffectInstance.INFINITE_DURATION,
                TaczCuriosConfig.COMMON.islandBoomRavenRegenAmplifier.get(), false, false, true));
        }
    }

    public static boolean isEquipped(LivingEntity livingEntity) {
        return !findEquippedStack(livingEntity).isEmpty();
    }

    private static ItemStack findEquippedStack(LivingEntity livingEntity) {
        return CurioSearchHelper.findFirstEquippedStack(livingEntity, stack -> stack.getItem() instanceof IslandBoomRaven);
    }

    

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        tooltip.add(Component.literal(""));

        double armorBoost = TaczCuriosConfig.COMMON.islandBoomRavenArmorMultiplier.get() * 100;
        double speedBoost = TaczCuriosConfig.COMMON.islandBoomRavenSpeedMultiplier.get() * 100;
        double invisIntervalSecs = TaczCuriosConfig.COMMON.islandBoomRavenInvisRefreshInterval.get() / 20.0;
        double trueInvisDurationSecs = TaczCuriosConfig.COMMON.islandBoomRavenInvisDuration.get() / 20.0;

        appendImaginaryResistance(stack, tooltip);

        tooltip.add(formatModifierTooltip(armorBoost, "%.0f%%", Component.translatable(AttributeHelper.ARMOR.getDescriptionId()))
                .withStyle(ChatFormatting.GOLD));

        tooltip.add(formatModifierTooltip(speedBoost, "%.0f%%", Component.translatable(AttributeHelper.MOVEMENT_SPEED.getDescriptionId()))
                .withStyle(ChatFormatting.GOLD));

        tooltip.add(formatEffectTooltip(MobEffects.REGENERATION,
                TaczCuriosConfig.COMMON.islandBoomRavenRegenAmplifier.get())
            .withStyle(ChatFormatting.GOLD));

        tooltip.add(Component.translatable("item.tcc.island_boom_raven.special_invis",
                String.format("%.1f", invisIntervalSecs),
                String.format("%.1f", trueInvisDurationSecs))
            .withStyle(ChatFormatting.RED));

        tooltip.add(Component.translatable("tcc.tooltip.silent_movement")
            .withStyle(ChatFormatting.RED));



        appendBoundPlayer(stack, tooltip);

        tooltip.add(Component.literal(""));

    }

    }
