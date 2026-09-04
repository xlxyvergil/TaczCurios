package com.xlxyvergil.tcc.items.curios.bound;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.attribute.TccAttributes;
import com.xlxyvergil.tcc.capability.CurioAdaptationCapability;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.items.BoundCurioItem;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import com.xlxyvergil.tcc.helpers.ImaginaryResistanceHelper;
import net.minecraft.ChatFormatting;
import com.xlxyvergil.tcc.client.TaczCuriosClientTooltip;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class HuajieZhiyan extends BoundCurioItem {
    private static final UUID IMAGINARY_RESISTANCE_UUID = UUID.fromString("b2c3d4e5-f6a7-8901-bcde-f12345678902");
    private static final UUID MAX_HEALTH_UUID = UUID.fromString("c3d4e5f6-a7b8-9012-cdef-123456789024");
    private static final String ADAPT_ID = "huajie_zhiyan";
    private static final String ADAPT_REGISTERED_KEY = TaczCurios.MODID + ":huajie_zhiyan_adapt_registered";

    public HuajieZhiyan(Properties properties) {
        super(properties);
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        // 登记该饰品施加的修饰符 UUID → 来源饰品，供客户端属性面板显示来源图标。
        AttributeHelper.registerSourceItem(IMAGINARY_RESISTANCE_UUID, stack.getItem());
        AttributeHelper.registerSourceItem(MAX_HEALTH_UUID, stack.getItem());
        ItemStack equipped = CurioSearchHelper.findFirstEquippedStack(livingEntity,
                s -> s.getItem() instanceof HuajieZhiyan);
        CompoundTag tag = equipped.getTag();
        double total = 1.0
                + ImaginaryResistanceHelper.getExtraResistanceFromProgress(tag);
        AttributeHelper.applyModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get(),
            total, IMAGINARY_RESISTANCE_UUID,
            "tcc.huajie_zhiyan.imaginary_resistance", AttributeModifier.Operation.ADDITION);

        double totalResistance = livingEntity.getAttributeValue(TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get());
        double healthBoost = Math.round(totalResistance * TaczCuriosConfig.COMMON.huajieZhiyanHealthPerResistance.get() * 10000.0) / 10000.0;
        AttributeHelper.applyModifier(livingEntity, Attributes.MAX_HEALTH,
            healthBoost, MAX_HEALTH_UUID,
            "tcc.huajie_zhiyan.max_health", AttributeModifier.Operation.ADDITION);

        if (matchesRestriction(livingEntity)) {
            if (!livingEntity.getPersistentData().getBoolean(ADAPT_REGISTERED_KEY)) {
                livingEntity.getCapability(CurioAdaptationCapability.CAPABILITY).ifPresent(h -> {
                    h.register(ADAPT_ID,
                        TaczCuriosConfig.COMMON.huajieZhiyanMaxSlots.get(),
                        TaczCuriosConfig.COMMON.huajieZhiyanAdaptFactor.get(),
                        TaczCuriosConfig.COMMON.huajieZhiyanDecaySeconds.get());
                });
                livingEntity.getPersistentData().putBoolean(ADAPT_REGISTERED_KEY, true);
            }
        } else {
            unregisterAdaptation(livingEntity);
        }
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get(), IMAGINARY_RESISTANCE_UUID);
        AttributeHelper.removeModifier(livingEntity, Attributes.MAX_HEALTH, MAX_HEALTH_UUID);
        unregisterAdaptation(livingEntity);
    }

    private void unregisterAdaptation(LivingEntity livingEntity) {
        if (livingEntity.getPersistentData().getBoolean(ADAPT_REGISTERED_KEY)) {
            livingEntity.getCapability(CurioAdaptationCapability.CAPABILITY).ifPresent(h -> {
                h.unregister(ADAPT_ID);
            });
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
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        appendImaginaryResistance(stack, tooltip);
        int maxSlots = TaczCuriosConfig.COMMON.huajieZhiyanMaxSlots.get();
        double adaptFactor = TaczCuriosConfig.COMMON.huajieZhiyanAdaptFactor.get() * 100;
        int decaySeconds = TaczCuriosConfig.COMMON.huajieZhiyanDecaySeconds.get();

        tooltip.add(Component.literal(""));



        double healthFromResistance = 0;
        if (level != null && level.isClientSide()) {
            LivingEntity wearer = TaczCuriosClientTooltip.resolveWearer(stack);
            if (wearer != null && isEquipped(wearer)) {
                double resistance = wearer.getAttributeValue(TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get());
                healthFromResistance = resistance * TaczCuriosConfig.COMMON.huajieZhiyanHealthPerResistance.get();
            }
        }
        tooltip.add(formatModifierTooltip(healthFromResistance, "%.1f", Component.translatable(AttributeHelper.MAX_HEALTH.getDescriptionId()))
                .withStyle(ChatFormatting.WHITE));

        tooltip.add(Component.translatable("item.tcc.huajie_zhiyan.special_adapt",
                maxSlots,
                String.format("%.2f", adaptFactor),
                decaySeconds)
            .withStyle(ChatFormatting.WHITE));

        tooltip.add(Component.translatable("tcc.tooltip.affected_by_imaginary_resistance")
            .withStyle(ChatFormatting.LIGHT_PURPLE));

        tooltip.add(Component.literal(""));
        appendBoundPlayer(stack, tooltip);
    }
}
