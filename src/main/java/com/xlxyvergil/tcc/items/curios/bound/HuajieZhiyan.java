package com.xlxyvergil.tcc.items.curios.bound;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.attribute.TccAttributes;
import com.xlxyvergil.tcc.capability.CurioAdaptationCapability;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.ItemNbtHelper;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.items.BoundCurioItem;
import com.xlxyvergil.tcc.util.ItemNbtHelper;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import com.xlxyvergil.tcc.helpers.ImaginaryResistanceHelper;
import net.minecraft.ChatFormatting;
import com.xlxyvergil.tcc.client.TaczCuriosClientTooltip;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.resources.ResourceLocation;
public class HuajieZhiyan extends BoundCurioItem {
    private static final ResourceLocation IMAGINARY_RESISTANCE_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "huajie_zhiyan_b2c3d4e5_8902");
    private static final ResourceLocation MAX_HEALTH_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "huajie_zhiyan_c3d4e5f6_9024");
    private static final String ADAPT_ID = "huajie_zhiyan";
    private static final String ADAPT_REGISTERED_KEY = TaczCurios.MODID + ":huajie_zhiyan_adapt_registered";

    public HuajieZhiyan(Properties properties) {
        super(properties);
    }

    @Override
    protected void declareModifiers(LivingEntity livingEntity, ItemStack stack) {
        ItemStack equipped = CurioSearchHelper.findFirstEquippedStack(livingEntity,
                s -> s.getItem() instanceof HuajieZhiyan);
        CompoundTag tag = ItemNbtHelper.getTag(equipped);
        double total = 1.0
                + ImaginaryResistanceHelper.getExtraResistanceFromProgress(tag);
        AttributeHelper.applyModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE,
            total, IMAGINARY_RESISTANCE_ID, AttributeModifier.Operation.ADD_VALUE);

        double totalResistance = livingEntity.getAttributeValue(TccAttributes.IMAGINARY_DAMAGE_RESISTANCE);
        double healthBoost = Math.round(totalResistance * TaczCuriosConfig.COMMON.huajieZhiyanHealthPerResistance.get() * 10000.0) / 10000.0;
        AttributeHelper.applyModifier(livingEntity, Attributes.MAX_HEALTH,
            healthBoost, MAX_HEALTH_ID, AttributeModifier.Operation.ADD_VALUE);
    }

    /** 适应度登记会写 capability 与 persistentData，属于副作用，不能出现在声明里。 */
    @Override
    protected void applySideEffects(LivingEntity livingEntity, ItemStack stack) {
        if (matchesRestriction(livingEntity)) {
            if (!livingEntity.getPersistentData().getBoolean(ADAPT_REGISTERED_KEY)) {
                CurioAdaptationCapability.of(livingEntity).register(ADAPT_ID,
                    TaczCuriosConfig.COMMON.huajieZhiyanAdaptFactor.get(),
                    TaczCuriosConfig.COMMON.huajieZhiyanDecaySeconds.get());
                livingEntity.getPersistentData().putBoolean(ADAPT_REGISTERED_KEY, true);
            }
        } else {
            unregisterAdaptation(livingEntity);
        }
    }

    @Override
    protected void removeModifiers(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE, IMAGINARY_RESISTANCE_ID);
        AttributeHelper.removeModifier(livingEntity, Attributes.MAX_HEALTH, MAX_HEALTH_ID);
        unregisterAdaptation(livingEntity);
    }

    private void unregisterAdaptation(LivingEntity livingEntity) {
        if (livingEntity.getPersistentData().getBoolean(ADAPT_REGISTERED_KEY)) {
            CurioAdaptationCapability.of(livingEntity).unregister(ADAPT_ID);
            livingEntity.getPersistentData().putBoolean(ADAPT_REGISTERED_KEY, false);
        }
    }

    @Override
    protected boolean isBoundItem() {
        return true;
    }

    public static boolean isEquipped(LivingEntity entity) {
        return !CurioSearchHelper.findFirstEquippedStack(entity,
            stack -> stack.getItem() instanceof HuajieZhiyan).isEmpty();
    }

    @Override
    public List<String> getWeaponTypeRestriction() {
        return List.of("melee");
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        Level level = context.level();
        super.appendHoverText(stack, context, tooltip, flag);

        appendImaginaryResistance(stack, tooltip);
        double adaptFactor = TaczCuriosConfig.COMMON.huajieZhiyanAdaptFactor.get() * 100;
        int decaySeconds = TaczCuriosConfig.COMMON.huajieZhiyanDecaySeconds.get();

        tooltip.add(Component.literal(""));



        double healthFromResistance = 0;
        if (level != null && level.isClientSide()) {
            LivingEntity wearer = TaczCuriosClientTooltip.resolveWearer(stack);
            if (wearer != null && isEquipped(wearer)) {
                double resistance = wearer.getAttributeValue(TccAttributes.IMAGINARY_DAMAGE_RESISTANCE);
                healthFromResistance = resistance * TaczCuriosConfig.COMMON.huajieZhiyanHealthPerResistance.get();
            }
        }
        tooltip.add(formatModifierTooltip(healthFromResistance, "%.1f", Component.translatable(AttributeHelper.MAX_HEALTH.value().getDescriptionId()))
                .withStyle(ChatFormatting.WHITE));

        tooltip.add(Component.translatable("item.tcc.huajie_zhiyan.special_adapt",
                String.format("%.0f", adaptFactor),
                decaySeconds)
            .withStyle(ChatFormatting.WHITE));

        tooltip.add(Component.translatable("tcc.tooltip.cooked_drops")
            .withStyle(ChatFormatting.GOLD));

        tooltip.add(Component.translatable("tcc.tooltip.affected_by_imaginary_resistance")
            .withStyle(ChatFormatting.LIGHT_PURPLE));

        tooltip.add(Component.literal(""));
        appendBoundPlayer(stack, tooltip);
    }
}
