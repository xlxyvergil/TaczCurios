package com.xlxyvergil.tcc.items.curios.bound;

import net.minecraftforge.event.entity.living.LivingHurtEvent;
import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.attribute.TccAttributes;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.core.TccDamageSources;
import com.xlxyvergil.tcc.event.TccAttributeEvents;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.items.BoundCurioItem;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import com.xlxyvergil.tcc.util.GunTypeChecker;
import com.xlxyvergil.tcc.util.ImaginaryInfectionHelper;

import net.minecraft.ChatFormatting;
import com.xlxyvergil.tcc.client.TaczCuriosClientTooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
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
public class MetaMorph extends BoundCurioItem {
    private static final UUID ATTACK_DAMAGE_UUID = UUID.fromString("c3d4e5f6-a7b8-9012-cdef-1234567893");
    private static final UUID LIFE_STEAL_UUID = UUID.fromString("d4e5f6a7-b8c9-0123-def4-567890abcdef");

    public MetaMorph(Properties properties) {
        super(properties);
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        double totalResistance = livingEntity.getAttributeValue(TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get());
        AttributeHelper.applyModifier(livingEntity, Attributes.ATTACK_DAMAGE,
            totalResistance, ATTACK_DAMAGE_UUID,
            "tcc.meta_morph.attack_damage", AttributeModifier.Operation.ADDITION);

        double lifeSteal = Math.round(totalResistance * TaczCuriosConfig.COMMON.metaMorphLifeStealPerResistance.get() * 10000.0) / 10000.0;
        AttributeHelper.applyModifier(livingEntity, AttributeHelper.LIFE_STEAL,
            lifeSteal, LIFE_STEAL_UUID,
            "tcc.meta_morph.life_steal", AttributeModifier.Operation.ADDITION);
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE_UUID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.LIFE_STEAL, LIFE_STEAL_UUID);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        applyEffects(slotContext.entity(), stack);
    }

    @Override
    protected boolean isBoundItem() {
        return true;
    }

    public static boolean isEquipped(LivingEntity entity) {
        return !CurioSearchHelper.findFirstEquippedStack(entity,
            stack -> stack.getItem() instanceof MetaMorph).isEmpty();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurt(LivingHurtEvent event) {
        if (!TccAttributeEvents.isActiveAttackSource(event.getSource())) return;
        if (event.getEntity().level().isClientSide()) return;

        DamageSource source = event.getSource();
        if (source.is(TccDamageSources.IMAGINARY_DAMAGE_TAG)) return;

        Entity attackerEntity = source.getEntity();
        if (!(attackerEntity instanceof LivingEntity attacker)) return;
        if (!isEquipped(attacker)) return;
        if (!GunTypeChecker.isHoldingMeleeWeapon(attacker)) return;

        LivingEntity target = event.getEntity();
        if (target.isDeadOrDying()) return;

        double imaginaryResistance = attacker.getAttributeValue(TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get());
        double attackDamage = attacker.getAttributeValue(Attributes.ATTACK_DAMAGE);
        float imaginaryBonus = (float) ((imaginaryResistance / 100.0) * attackDamage
            * TaczCuriosConfig.COMMON.metaMorphImaginaryDamageScale.get());
        TccAttributeEvents.applyImaginaryDamage(
            target,
            TccDamageSources.imaginaryDamage(target.level(), attacker),
            imaginaryBonus
        );
        // 攻击命中时同时施加虚数侵染
        TccAttributeEvents.applyInfection(target, attacker, ImaginaryInfectionHelper.resolveMaxLevel(attacker));
    }

    @Override
    public List<String> getWeaponTypeRestriction() {
        return List.of("melee");
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        tooltip.add(Component.literal(""));



        double attackFromResistance = 0;
        double lifeStealFromResistance = 0;
        double imaginaryDamage = 0;
        if (level != null && level.isClientSide()) {
            LivingEntity wearer = TaczCuriosClientTooltip.resolveWearer(stack);
            if (wearer != null && isEquipped(wearer)) {
                double resistance = wearer.getAttributeValue(TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get());
                attackFromResistance = resistance;
                lifeStealFromResistance = resistance * TaczCuriosConfig.COMMON.metaMorphLifeStealPerResistance.get();
                double attackDamage = wearer.getAttributeValue(Attributes.ATTACK_DAMAGE);
                imaginaryDamage = (resistance / 100.0) * attackDamage
                    * TaczCuriosConfig.COMMON.metaMorphImaginaryDamageScale.get();
            }
        }
        tooltip.add(formatModifierTooltip(attackFromResistance, "%.1f", Component.translatable(AttributeHelper.ATTACK_DAMAGE.getDescriptionId()))
                .withStyle(ChatFormatting.RED));
        tooltip.add(formatModifierTooltip(lifeStealFromResistance, "%.2f", Component.translatable(AttributeHelper.LIFE_STEAL.getDescriptionId()))
                .withStyle(ChatFormatting.RED));
        tooltip.add(Component.translatable("tcc.tooltip.affected_by_imaginary_resistance")
                .withStyle(ChatFormatting.LIGHT_PURPLE));
        tooltip.add(Component.translatable("item.tcc.meta_morph.special_damage",
                String.format("%.2f", imaginaryDamage))
            .withStyle(ChatFormatting.RED));

        tooltip.add(Component.literal(""));
        appendBoundPlayer(stack, tooltip);
    }
}
