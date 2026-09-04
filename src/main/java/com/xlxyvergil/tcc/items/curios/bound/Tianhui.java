package com.xlxyvergil.tcc.items.curios.bound;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.attribute.TccAttributes;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.helpers.ImaginaryResistanceHelper;
import com.xlxyvergil.tcc.util.DamageResistanceHelper;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.items.BoundCurioItem;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.SlotContext;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class Tianhui extends BoundCurioItem {
    private static final UUID IMAGINARY_RESISTANCE_UUID = UUID.fromString("e5f6a7b8-c9d0-1234-ef56-7890abcdef02");
    private static final UUID MAX_HEALTH_UUID = UUID.fromString("f6a7b8c9-d0e1-2345-f678-90abcdef0123");

    public Tianhui(Properties properties) {
        super(properties);
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        // 登记该饰品施加的修饰符 UUID → 来源饰品，供客户端属性面板显示来源图标。
        AttributeHelper.registerSourceItem(IMAGINARY_RESISTANCE_UUID, stack.getItem());
        AttributeHelper.registerSourceItem(MAX_HEALTH_UUID, stack.getItem());
        if (matchesRestriction(livingEntity)) {
            ItemStack equipped = CurioSearchHelper.findFirstEquippedStack(livingEntity,
                    s -> s.getItem() instanceof Tianhui);
            CompoundTag tag = equipped.getTag();
            double total = 1.0
                    + ImaginaryResistanceHelper.getExtraResistanceFromProgress(tag);
            AttributeHelper.applyModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get(),
                total, IMAGINARY_RESISTANCE_UUID,
                "tcc.tianhui.imaginary_resistance", AttributeModifier.Operation.ADDITION);
            AttributeHelper.applyModifier(livingEntity, Attributes.MAX_HEALTH,
                TaczCuriosConfig.COMMON.tianhuiMaxHealthReduction.get(), MAX_HEALTH_UUID,
                "tcc.tianhui.max_health", AttributeModifier.Operation.MULTIPLY_BASE);
        }
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get(), IMAGINARY_RESISTANCE_UUID);
        AttributeHelper.removeModifier(livingEntity, Attributes.MAX_HEALTH, MAX_HEALTH_UUID);
        DamageResistanceHelper.clearDamageCap(livingEntity);
        DamageResistanceHelper.clearDamageReduction(livingEntity);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        super.curioTick(slotContext, stack);
        LivingEntity entity = slotContext.entity();
        if (entity.level().isClientSide) return;
        if (!matchesRestriction(entity)) {
            DamageResistanceHelper.clearDamageReduction(entity);
            return;
        }
        DamageResistanceHelper.setDamageReduction(entity, calculateRetainFactor(entity));
    }

    private static float calculateRetainFactor(LivingEntity entity) {
        double totalImaginaryResistance = entity.getAttributeValue(TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get());
        double baseFactor = 1.0;
        double resistanceScale = TaczCuriosConfig.COMMON.tianhuiResistanceScale.get();
        double minFactor = TaczCuriosConfig.COMMON.tianhuiMinDamageFactor.get();

        double factor = Math.round((baseFactor - (totalImaginaryResistance * resistanceScale)) * 10000.0) / 10000.0;
        if (factor < minFactor) {
            factor = minFactor;
        }
        return (float) factor;
    }

    @Override
    protected boolean isBoundItem() {
        return true;
    }

    @Override
    public List<String> getWeaponTypeRestriction() {
        return List.of("rifle");
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        appendImaginaryResistance(stack, tooltip);
        double maxHealthReduction = TaczCuriosConfig.COMMON.tianhuiMaxHealthReduction.get() * 100;

        tooltip.add(Component.literal(""));

        tooltip.add(Component.literal(""));
        tooltip.add(formatModifierTooltip(maxHealthReduction, "%.0f%%", Component.translatable(AttributeHelper.MAX_HEALTH.getDescriptionId()))
                .withStyle(ChatFormatting.RED));

        int minDamagePercent = (int)(TaczCuriosConfig.COMMON.tianhuiMinDamageFactor.get() * 100);
        tooltip.add(Component.translatable("item.tcc.tianhui.special_damage_limit", minDamagePercent)
            .withStyle(ChatFormatting.RED));

        tooltip.add(Component.translatable("tcc.tooltip.affected_by_imaginary_resistance")
            .withStyle(ChatFormatting.LIGHT_PURPLE));

        tooltip.add(Component.literal(""));

        appendBoundPlayer(stack, tooltip);
    }
}
