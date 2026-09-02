package com.xlxyvergil.tcc.items.curios.bound;

import com.xlxyvergil.tcc.TaczCurios;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.core.TccDamageSources;
import com.xlxyvergil.tcc.event.TccAttributeEvents;
import com.xlxyvergil.tcc.util.AiStopHelper;
import com.xlxyvergil.tcc.items.BoundCurioItem;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import net.minecraft.ChatFormatting;
import com.xlxyvergil.tcc.client.TaczCuriosClientTooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FanchenNandu extends BoundCurioItem {
    private static int stopDuration() {
        return TaczCuriosConfig.COMMON.fanchenNanduStopDurationSeconds.get() * 20;
    }

    private static double stopChance() {
        return TaczCuriosConfig.COMMON.fanchenNanduStopChance.get();
    }

    private static double armorImaginaryScale() {
        return TaczCuriosConfig.COMMON.fanchenNanduArmorImaginaryScale.get();
    }

    public FanchenNandu(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean isBoundItem() {
        return true;
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
    }

    @Override
    public List<String> getWeaponTypeRestriction() {
        return List.of("melee");
    }

    public static boolean isEquipped(LivingEntity entity) {
        return !CurioSearchHelper.findFirstEquippedStack(entity,
                stack -> stack.getItem() instanceof FanchenNandu).isEmpty();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurt(LivingHurtEvent event) {
        if (!TccAttributeEvents.isActiveAttackSource(event.getSource())) return;
        if (!(event.getEntity().level() instanceof ServerLevel)) {
            return;
        }
        LivingEntity attacker = resolveAttacker(event);
        if (attacker == null) {
            return;
        }
        ItemStack equipped = CurioSearchHelper.findFirstEquippedStack(attacker,
                stack -> stack.getItem() instanceof FanchenNandu);
        if (equipped.isEmpty()) {
            return;
        }
        if (!((FanchenNandu) equipped.getItem()).matchesRestriction(attacker)) {
            return;
        }
        LivingEntity target = event.getEntity();
        if (target.isDeadOrDying()) {
            return;
        }
        if (attacker.getRandom().nextDouble() < stopChance()) {
            AiStopHelper.apply(target, stopDuration());
        }
        double armor = attacker.getAttributeValue(Attributes.ARMOR);
        float imaginary = (float) (armor * armorImaginaryScale());
        TccAttributeEvents.applyImaginaryDamage(target,
                TccDamageSources.imaginaryDamage(target.level(), attacker), imaginary);
    }

    private static LivingEntity resolveAttacker(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof LivingEntity living) {
            return living;
        }
        if (event.getSource().getDirectEntity() instanceof LivingEntity living) {
            return living;
        }
        return null;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("item.tcc.transient.key_effect",
                String.format("%.0f", stopChance() * 100))
                .withStyle(ChatFormatting.GOLD));
        double imaginaryDamage = 0;
        if (level != null && level.isClientSide()) {
            LivingEntity wearer = TaczCuriosClientTooltip.resolveWearer(stack);
            if (wearer != null && isEquipped(wearer)) {
                double armor = wearer.getAttributeValue(Attributes.ARMOR);
                imaginaryDamage = armor * armorImaginaryScale();
            }
        }
        tooltip.add(Component.translatable("item.tcc.transient.key_effect_armor_imaginary_damage",
                String.format("%.2f", imaginaryDamage))
                .withStyle(ChatFormatting.GOLD));
        appendBoundPlayer(stack, tooltip);
    }
}
