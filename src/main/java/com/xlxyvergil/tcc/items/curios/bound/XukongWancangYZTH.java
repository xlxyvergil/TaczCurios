package com.xlxyvergil.tcc.items.curios.bound;

import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.tacz.guns.api.item.IGun;
import com.xlxyvergil.tcc.TaczCurios;
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
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import com.xlxyvergil.tcc.attribute.TccAttributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio.DropRule;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class XukongWancangYZTH extends BoundCurioItem {

    private static final UUID HEAT_MAX_UUID = UUID.fromString("8d1e4345-5ff6-467d-b871-a5fe3906be12");
    private static final UUID HEAT_COOLING_UUID = UUID.fromString("30f486ba-c5ec-45d8-9439-1610c695d2e0");

    public XukongWancangYZTH(Properties properties) {
        super(properties);
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        if (matchesRestriction(livingEntity)) {
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.HEAT_MAX,
                TaczCuriosConfig.COMMON.xukongWancangYZTHHeatMax.get(), HEAT_MAX_UUID,
                "tcc.xukong_wancang_yzth.heat_max", AttributeModifier.Operation.MULTIPLY_BASE);
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.HEAT_COOLING,
                TaczCuriosConfig.COMMON.xukongWancangYZTHHeatCooling.get(), HEAT_COOLING_UUID,
                "tcc.xukong_wancang_yzth.heat_cooling", AttributeModifier.Operation.MULTIPLY_BASE);
        } else {
            AttributeHelper.removeModifier(livingEntity, AttributeHelper.HEAT_MAX, HEAT_MAX_UUID);
            AttributeHelper.removeModifier(livingEntity, AttributeHelper.HEAT_COOLING, HEAT_COOLING_UUID);
        }
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.HEAT_MAX, HEAT_MAX_UUID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.HEAT_COOLING, HEAT_COOLING_UUID);
    }

    @Override
    protected boolean isBoundItem() {
        return true;
    }

    @Override
    public DropRule getDropRule(SlotContext slotContext, DamageSource source, int lootingLevel, boolean recentlyHit, ItemStack stack) {
        return DropRule.ALWAYS_KEEP;
    }

    public static boolean isEquipped(LivingEntity entity) {
        return !CurioSearchHelper.findFirstEquippedStack(entity,
            stack -> stack.getItem() instanceof XukongWancangYZTH).isEmpty();
    }

    @SubscribeEvent
    public static void onGunHurtPre(EntityHurtByGunEvent.Pre event) {
        LivingEntity attacker = event.getAttacker();
        if (attacker == null || !isEquipped(attacker)) return;
        if (!GunTypeChecker.isHoldingHeavyWeapon(attacker)) return;
        if (!(attacker.level() instanceof ServerLevel)) return;

        ImaginaryConversionHelper.convertToImaginary(event);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onGunHurtPost(EntityHurtByGunEvent.Post event) {
        LivingEntity attacker = event.getAttacker();
        if (attacker == null || !isEquipped(attacker)) return;
        if (!GunTypeChecker.isHoldingHeavyWeapon(attacker)) return;
        if (!(attacker.level() instanceof ServerLevel)) return;

        Entity hurtEntity = event.getHurtEntity();
        if (!(hurtEntity instanceof LivingEntity targetLiving)) return;
        if (targetLiving.isDeadOrDying()) return;

        // 额外虚数伤害：20 + attack_damage * (虚数抗性 / 100)
        double attackDamage = attacker.getAttributeValue(Attributes.ATTACK_DAMAGE);
        double imaginaryResistance = attacker.getAttributeValue(TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get());
        float imaginaryBonus = (float) (TaczCuriosConfig.COMMON.xukongWancangYZTHImaginaryDamage.get().floatValue()
            + (float) Math.round(attackDamage * (imaginaryResistance / 100.0) * 10000.0) / 10000.0);
        TccAttributeEvents.applyImaginaryDamage(
            targetLiving,
            TccDamageSources.imaginaryDamage(targetLiving.level(), attacker),
            imaginaryBonus
        );

        if (event.getBullet() == null) return;
        if (!event.getBullet().getPersistentData().getBoolean(ImaginaryConversionHelper.INFECTION_KEY)) return;

        // 攻击必定触发侵染（自定义持续秒数）
        ImaginaryConversionHelper.applyInfection(targetLiving, attacker, 1,
            TaczCuriosConfig.COMMON.xukongWancangYZTHInfectionDuration.get());
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity entity = slotContext.entity();
        if (entity.level().isClientSide()) return;

        // 每秒恢复一次弹药
        if (entity.tickCount % 20 != 0) return;
        if (!GunTypeChecker.isHoldingHeavyWeapon(entity)) return;

        ItemStack held = entity.getMainHandItem();
        IGun iGun = IGun.getIGunOrNull(held);
        if (iGun == null) return;

        AmmoRegenHelper.regenAmmo(entity, held, iGun,
            (double) TaczCuriosConfig.COMMON.xukongWancangYZTHAmmoRegenPercent.get());
    }

    @Override
    public List<String> getWeaponTypeRestriction() {
        return List.of("rpg", "mg");
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        tooltip.add(Component.literal(""));

        double ammoRegen = TaczCuriosConfig.COMMON.xukongWancangYZTHAmmoRegenPercent.get() * 100;

        double computedImaginaryDamage = 0;
        if (level != null && level.isClientSide()) {
            Player player = Minecraft.getInstance().player;
            if (player != null) {
                double attackDamage = player.getAttributeValue(Attributes.ATTACK_DAMAGE);
                double resistance = player.getAttributeValue(TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get());
                computedImaginaryDamage = TaczCuriosConfig.COMMON.xukongWancangYZTHImaginaryDamage.get()
                    + attackDamage * (resistance / 100.0);
            }
        }
        tooltip.add(Component.translatable("item.tcc.xukong_wancang_yzth.effect.damage",
                String.format("%.0f", computedImaginaryDamage))
            .withStyle(ChatFormatting.RED));
        tooltip.add(Component.translatable("item.tcc.xukong_wancang_yzth.effect.ammo",
                String.format("%.0f", ammoRegen))
            .withStyle(ChatFormatting.RED));
        tooltip.add(Component.translatable("tcc.tooltip.gun_to_imaginary")
            .withStyle(ChatFormatting.RED));
        tooltip.add(Component.translatable("tcc.tooltip.always_infection")
            .withStyle(ChatFormatting.RED));

        tooltip.add(Component.translatable("tcc.tooltip.affected_by_imaginary_resistance")
            .withStyle(ChatFormatting.LIGHT_PURPLE));

        tooltip.add(Component.literal(""));
        appendBoundPlayer(stack, tooltip);
    }
}
