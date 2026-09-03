package com.xlxyvergil.tcc.items.curios.bound;

import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.tacz.guns.api.item.IGun;
import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.attribute.TccAttributes;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.core.TccDamageSources;
import com.xlxyvergil.tcc.event.TccAttributeEvents;
import com.xlxyvergil.tcc.util.AmmoRegenHelper;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.items.BoundCurioItem;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import com.xlxyvergil.tcc.util.GunTypeChecker;
import com.xlxyvergil.tcc.util.ImaginaryConversionHelper;
import net.minecraft.ChatFormatting;
import com.xlxyvergil.tcc.client.TaczCuriosClientTooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.SlotContext;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class YinguoZhuanlun extends BoundCurioItem {
    private static final UUID OVERHEAL_UUID = UUID.fromString("2cc110cb-8a9a-4bd9-9739-457cbcc59c83");

    public YinguoZhuanlun(Properties properties) {
        super(properties);
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        if (matchesRestriction(livingEntity)) {
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.OVERHEAL,
                TaczCuriosConfig.COMMON.yinguoZhuanlunOverheal.get(), OVERHEAL_UUID,
                "tcc.yinguo_zhuanlun.overheal", AttributeModifier.Operation.ADDITION);
        } else {
            removeEffects(livingEntity);
        }
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.OVERHEAL, OVERHEAL_UUID);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity entity = slotContext.entity();
        if (entity.level().isClientSide()) return;
        if (entity.tickCount % 20 != 0) return;
        if (!GunTypeChecker.isHoldingRifle(entity)) return;

        ItemStack held = entity.getMainHandItem();
        IGun iGun = IGun.getIGunOrNull(held);
        if (iGun == null) return;

        double totalResistance = entity.getAttributeValue(TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get());
        double resistanceScale = TaczCuriosConfig.COMMON.yinguoZhuanlunAmmoResistanceScale.get();
        double percent = Math.round(totalResistance * resistanceScale * 10000.0) / 10000.0;

        AmmoRegenHelper.regenAmmo(entity, held, iGun, percent);
    }

    @Override
    protected boolean isBoundItem() {
        return true;
    }

    public static boolean hasEquipped(LivingEntity livingEntity) {
        return !CurioSearchHelper.findFirstEquippedStack(livingEntity, stack -> stack.getItem() instanceof YinguoZhuanlun).isEmpty();
    }

    @SubscribeEvent
    public static void onGunHurtPre(EntityHurtByGunEvent.Pre event) {
        LivingEntity attacker = event.getAttacker();
        if (attacker == null || !hasEquipped(attacker)) return;
        if (!(attacker.level() instanceof ServerLevel)) return;
        if (!GunTypeChecker.isHoldingRifle(attacker)) return;

        ImaginaryConversionHelper.convertToImaginary(event);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onGunHurtPost(EntityHurtByGunEvent.Post event) {
        LivingEntity attacker = event.getAttacker();
        if (attacker == null || !hasEquipped(attacker)) return;
        if (!(attacker.level() instanceof ServerLevel)) return;
        if (!GunTypeChecker.isHoldingRifle(attacker)) return;

        Entity hurtEntity = event.getHurtEntity();
        if (!(hurtEntity instanceof LivingEntity targetLiving)) return;
        if (targetLiving.isDeadOrDying()) return;

        float baseDamage = event.getBaseAmount();
        double totalResistance = attacker.getAttributeValue(TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get());
        float imaginaryDamage = (float) (baseDamage * (totalResistance / 100.0)
            * TaczCuriosConfig.COMMON.yinguoZhuanlunImaginaryDamageScale.get());
        TccAttributeEvents.applyImaginaryDamage(targetLiving,
            TccDamageSources.imaginaryDamage(targetLiving.level(), attacker), imaginaryDamage);
        TccAttributeEvents.applyCollapse(targetLiving, attacker);
    }

    @Override
    public List<String> getWeaponTypeRestriction() {
        return List.of("rifle");
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        tooltip.add(Component.literal(""));

        double overheal = TaczCuriosConfig.COMMON.yinguoZhuanlunOverheal.get() * 100;

        double resistance = 0;
        if (level != null && level.isClientSide()) {
            LivingEntity wearer = TaczCuriosClientTooltip.resolveWearer(stack);
            if (wearer != null) {
                resistance = wearer.getAttributeValue(TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get());
            }
        }
        double ammoPercent = resistance * TaczCuriosConfig.COMMON.yinguoZhuanlunAmmoResistanceScale.get() * 100;
        double damageScale = TaczCuriosConfig.COMMON.yinguoZhuanlunImaginaryDamageScale.get();

        tooltip.add(formatModifierTooltip(overheal, "%.0f%%", Component.translatable(AttributeHelper.OVERHEAL.getDescriptionId()))
                .withStyle(ChatFormatting.RED));
        tooltip.add(Component.translatable("item.tcc.yinguo_zhuanlun.special",
                String.format("%.0f", ammoPercent),
                String.format("%.2f", damageScale))
            .withStyle(ChatFormatting.RED));
        tooltip.add(Component.translatable("tcc.tooltip.gun_to_imaginary")
            .withStyle(ChatFormatting.RED));

        tooltip.add(Component.translatable("tcc.tooltip.affected_by_imaginary_resistance")
            .withStyle(ChatFormatting.LIGHT_PURPLE));

        tooltip.add(Component.literal(""));

        appendAlwaysImaginaryCollapse(tooltip);
        appendBoundPlayer(stack, tooltip);
    }
}
