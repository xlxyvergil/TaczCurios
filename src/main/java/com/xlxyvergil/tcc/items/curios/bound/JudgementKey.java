package com.xlxyvergil.tcc.items.curios.bound;

import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.core.TccDamageSources;
import com.xlxyvergil.tcc.event.TccAttributeEvents;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.items.BoundCurioItem;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import com.xlxyvergil.tcc.util.GunTypeChecker;
import com.xlxyvergil.tcc.util.ImaginaryConversionHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class JudgementKey extends BoundCurioItem {
    private static final UUID CRIT_CHANCE_UUID = UUID.fromString("f13a5b08-523d-4b62-b9f4-8a284f9c3bdf");
    private static final UUID CRIT_DAMAGE_UUID = UUID.fromString("2a1e47bd-1b05-44cf-9a2c-ea6c0612b47c");

    private static final String PROC_KEY = "tcc_judgement_key_set_proc";
    private static final String PROC_DAMAGE_KEY = "tcc_judgement_key_set_damage";
    private static final String PROC_DAMAGE_AFTER_HEADSHOT_KEY = "tcc_judgement_key_set_damage_after_headshot";

    public JudgementKey(Properties properties) {
        super(properties);
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        if (matchesRestriction(livingEntity)) {
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.CRIT_CHANCE,
                TaczCuriosConfig.COMMON.judgementKeyCritChance.get(), CRIT_CHANCE_UUID,
                "tcc.judgement_key.crit_chance", AttributeModifier.Operation.MULTIPLY_BASE);
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.CRIT_DAMAGE,
                TaczCuriosConfig.COMMON.judgementKeyCritDamage.get(), CRIT_DAMAGE_UUID,
                "tcc.judgement_key.crit_damage", AttributeModifier.Operation.MULTIPLY_BASE);
        } else {
            removeEffects(livingEntity);
        }
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.CRIT_CHANCE, CRIT_CHANCE_UUID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.CRIT_DAMAGE, CRIT_DAMAGE_UUID);
    }

    @Override
    protected boolean isBoundItem() {
        return true;
    }

    public static boolean hasEquipped(LivingEntity livingEntity) {
        return !CurioSearchHelper.findFirstEquippedStack(livingEntity, stack -> stack.getItem() instanceof JudgementKey).isEmpty();
    }

    @SubscribeEvent
    public static void onGunHurtPre(EntityHurtByGunEvent.Pre event) {
        LivingEntity attacker = event.getAttacker();
        if (attacker == null || !hasEquipped(attacker)) return;
        if (!(attacker.level() instanceof ServerLevel)) return;
        if (!GunTypeChecker.isHoldingSniper(attacker)) return;

        ImaginaryConversionHelper.convertToImaginary(event);

        if (!event.isHeadShot()) return;

        if (event.getBullet() != null) {
            event.getBullet().getPersistentData().putBoolean(PROC_KEY, true);
            float damage = event.getBaseAmount();
            event.getBullet().getPersistentData().putFloat(PROC_DAMAGE_KEY, damage);
            float damageAfterHeadshot = damage * event.getHeadshotMultiplier();
            event.getBullet().getPersistentData().putFloat(PROC_DAMAGE_AFTER_HEADSHOT_KEY, damageAfterHeadshot);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onGunHurtPost(EntityHurtByGunEvent.Post event) {
        LivingEntity attacker = event.getAttacker();
        if (attacker == null || !hasEquipped(attacker)) return;
        if (!(attacker.level() instanceof ServerLevel)) return;
        if (!GunTypeChecker.isHoldingSniper(attacker)) return;

        Entity bullet = event.getBullet();
        if (bullet == null) return;

        var data = bullet.getPersistentData();
        if (!data.getBoolean(PROC_KEY)) return;

        float damageAfterHeadshot = data.getFloat(PROC_DAMAGE_AFTER_HEADSHOT_KEY);

        Entity hurtEntity = event.getHurtEntity();
        if (!(hurtEntity instanceof LivingEntity targetLiving)) return;
        if (targetLiving.isDeadOrDying()) return;

        double setHealthProc = TaczCuriosConfig.COMMON.judgementProcChance.get();
        if (attacker.getRandom().nextDouble() < setHealthProc && damageAfterHeadshot > 0) {
            double directPercent = TaczCuriosConfig.COMMON.judgementDirectDamagePercent.get();
            float directDamage = (float) (damageAfterHeadshot * directPercent);
            TccAttributeEvents.applyImaginaryDamage(targetLiving, TccDamageSources.imaginaryDamage(targetLiving.level(), attacker), directDamage);
        }

        double collapseProc = TaczCuriosConfig.COMMON.judgementCollapseProcChance.get();
        if (attacker.getRandom().nextDouble() < collapseProc) {
            TccAttributeEvents.applyCollapse(targetLiving, attacker);
        }
    }

    @Override
    public List<String> getWeaponTypeRestriction() {
        return List.of("sniper");
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        tooltip.add(Component.literal(""));

        double critChance = TaczCuriosConfig.COMMON.judgementKeyCritChance.get() * 100;
        double critDamage = TaczCuriosConfig.COMMON.judgementKeyCritDamage.get() * 100;

        tooltip.add(formatModifierTooltip(critChance, "%.0f%%", Component.translatable(AttributeHelper.CRIT_CHANCE.getDescriptionId()))
                .withStyle(ChatFormatting.RED));
        tooltip.add(formatModifierTooltip(critDamage, "%.0f%%", Component.translatable(AttributeHelper.CRIT_DAMAGE.getDescriptionId()))
                .withStyle(ChatFormatting.RED));
        tooltip.add(Component.translatable("tcc.tooltip.gun_to_imaginary")
            .withStyle(ChatFormatting.RED));
        tooltip.add(Component.translatable("item.tcc.judgement_key.special")
            .withStyle(ChatFormatting.RED));

        tooltip.add(Component.literal(""));

        appendBoundPlayer(stack, tooltip);
    }
}
