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
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.SlotContext;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class Su extends BoundCurioItem {
    private static final UUID IMAGINARY_RESISTANCE_UUID = UUID.fromString("90b6f508-6de3-4f7e-913b-ac7c509be823");
    private static final UUID MAX_HEALTH_UUID = UUID.fromString("3b900565-c385-48b6-8d45-b08b5399d6c5");

    public Su(Properties properties) {
        super(properties);
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        ItemStack equipped = CurioSearchHelper.findFirstEquippedStack(livingEntity,
                s -> s.getItem() instanceof Su);
        CompoundTag tag = equipped.getTag();
        double total = 1.0
                + ImaginaryResistanceHelper.getExtraResistanceFromProgress(tag);
        AttributeHelper.applyModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get(),
            total, IMAGINARY_RESISTANCE_UUID,
            "tcc.su.imaginary_resistance", AttributeModifier.Operation.ADDITION);
        AttributeHelper.applyModifier(livingEntity, Attributes.MAX_HEALTH,
            TaczCuriosConfig.COMMON.suMaxHealthReduction.get(), MAX_HEALTH_UUID,
            "tcc.su.max_health", AttributeModifier.Operation.MULTIPLY_BASE);
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get(), IMAGINARY_RESISTANCE_UUID);
        AttributeHelper.removeModifier(livingEntity, Attributes.MAX_HEALTH, MAX_HEALTH_UUID);
        DamageResistanceHelper.clearDamageCap(livingEntity);
        DamageResistanceHelper.clearDamageReduction(livingEntity);
    }

    /**
     * 常驻比例减伤（仅步枪，可配置）：满足武器限制时设置保留比例，否则清除。
     * 对标准 hurt 与直接 setHealth 扣血均生效，无需依赖 LivingHurtEvent。
     */
    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        super.curioTick(slotContext, stack);
        LivingEntity entity = slotContext.entity();
        if (entity.level().isClientSide) return;
        if (!matchesRestriction(entity)) {
            DamageResistanceHelper.clearDamageReduction(entity);
            return;
        }
        DamageResistanceHelper.setDamageReduction(entity,
            (float) (1 - TaczCuriosConfig.COMMON.suDamageTakenFactor.get()));
    }

    public static boolean isEquipped(LivingEntity entity) {
        return !CurioSearchHelper.findFirstEquippedStack(entity,
            stack -> stack.getItem() instanceof Su).isEmpty();
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
        double maxHealthReduction = TaczCuriosConfig.COMMON.suMaxHealthReduction.get() * 100;
        double damageTakenFactor = TaczCuriosConfig.COMMON.suDamageTakenFactor.get() * 100;
        tooltip.add(Component.literal(""));

        tooltip.add(formatModifierTooltip(maxHealthReduction, "%.0f%%", Component.translatable(AttributeHelper.MAX_HEALTH.getDescriptionId()))
                .withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.translatable("tcc.tooltip.damage_reduction",
                String.format("%.0f", damageTakenFactor))
            .withStyle(ChatFormatting.GOLD));

        tooltip.add(Component.literal(""));
        appendBoundPlayer(stack, tooltip);
    }
}
