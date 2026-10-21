package com.xlxyvergil.tcc.items.curios.bound;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.api.items.IDamageMitigation;
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
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.common.Mod;
import top.theillusivec4.curios.api.SlotContext;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.resources.ResourceLocation;
import com.xlxyvergil.tcc.util.ItemNbtHelper;
public class Juezhe extends BoundCurioItem implements IDamageMitigation {
    private static final ResourceLocation IMAGINARY_RESISTANCE_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "juezhe_c3d4e5f6_7892");
    private static final ResourceLocation MAX_HEALTH_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "juezhe_d4e5f6a7_cdf0");

    public Juezhe(Properties properties) {
        super(properties);
    }

    @Override
    protected void declareModifiers(LivingEntity livingEntity, ItemStack stack) {
        ItemStack equipped = CurioSearchHelper.findFirstEquippedStack(livingEntity,
                s -> s.getItem() instanceof Juezhe);
        CompoundTag tag = ItemNbtHelper.getTag(equipped);
        double total = 1.0
                + ImaginaryResistanceHelper.getExtraResistanceFromProgress(tag);
        AttributeHelper.applyModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE,
            total, IMAGINARY_RESISTANCE_ID, AttributeModifier.Operation.ADD_VALUE);
        AttributeHelper.applyModifier(livingEntity, Attributes.MAX_HEALTH,
            TaczCuriosConfig.COMMON.juezheMaxHealthReduction.get(), MAX_HEALTH_ID, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    }

    @Override
    protected void removeModifiers(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE, IMAGINARY_RESISTANCE_ID);
        AttributeHelper.removeModifier(livingEntity, Attributes.MAX_HEALTH, MAX_HEALTH_ID);
        // 抗性提升 buff 卸下后不再主动移除，让其自然到期（时长 2 分钟）
    }

    /** 持步枪时按配置比例减伤；受击当刻现算，卸下即失效。 */
    @Override
    public Float retainFactor(LivingEntity wearer, DamageSource source, float amount) {
        if (!matchesRestriction(wearer)) return null;
        return (float) (1 - TaczCuriosConfig.COMMON.juezheDamageTakenFactor.get());
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        super.curioTick(slotContext, stack);
        LivingEntity entity = slotContext.entity();
        if (entity.level().isClientSide) return;
        // 抗性提升：只要装备即生效，移出枪械判定；每 10 秒续期（时长 2 分钟）
        if (entity.tickCount % 200 == 0) {
            entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 2 * 60 * 20,
                TaczCuriosConfig.COMMON.juezheResistanceLevel.get(), false, false, true));
        }
    }

    @Override
    protected boolean isBoundItem() {
        return true;
    }

    public static boolean isEquipped(LivingEntity entity) {
        return !CurioSearchHelper.findFirstEquippedStack(entity,
            stack -> stack.getItem() instanceof Juezhe).isEmpty();
    }

    @Override
    public List<String> getWeaponTypeRestriction() {
        return List.of("rifle");
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        Level level = context.level();
        super.appendHoverText(stack, context, tooltip, flag);

        appendImaginaryResistance(stack, tooltip);
        double maxHealthReduction = TaczCuriosConfig.COMMON.juezheMaxHealthReduction.get() * 100;
        double damageTakenFactor = TaczCuriosConfig.COMMON.juezheDamageTakenFactor.get() * 100;

        tooltip.add(Component.literal(""));

        tooltip.add(formatModifierTooltip(maxHealthReduction, "%.0f%%", Component.translatable(AttributeHelper.MAX_HEALTH.value().getDescriptionId()))
                .withStyle(ChatFormatting.WHITE));
        tooltip.add(Component.translatable("tcc.tooltip.damage_reduction",
                String.format("%.0f", damageTakenFactor))
            .withStyle(ChatFormatting.WHITE));

        tooltip.add(formatEffectTooltip(MobEffects.DAMAGE_RESISTANCE.value(),
                TaczCuriosConfig.COMMON.juezheResistanceLevel.get())
            .withStyle(ChatFormatting.WHITE));

        tooltip.add(Component.translatable("tcc.tooltip.projectile_immunity")
            .withStyle(ChatFormatting.WHITE));

        tooltip.add(Component.literal(""));

        appendBoundPlayer(stack, tooltip);
    }
}
