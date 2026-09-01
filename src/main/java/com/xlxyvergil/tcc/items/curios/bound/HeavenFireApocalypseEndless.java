package com.xlxyvergil.tcc.items.curios.bound;

import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.xlxyvergil.tcc.attribute.TccAttributes;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.registries.TccMobEffects;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.items.BoundCurioItem;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import com.xlxyvergil.tcc.util.GunTypeChecker;
import com.xlxyvergil.tcc.util.ImaginaryConversionHelper;
import com.xlxyvergil.tcc.util.TacDamageHelper;
import net.minecraft.ChatFormatting;
import com.xlxyvergil.tcc.client.TaczCuriosClientTooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.SlotContext;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@Mod.EventBusSubscriber(modid = "tcc", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class HeavenFireApocalypseEndless extends BoundCurioItem {
    private static final UUID GUN_DAMAGE_UUID = UUID.fromString("8c87e97e-cc63-415f-b92d-6ac2e521b219");
    private static final UUID EXPLOSION_DAMAGE_UUID = UUID.fromString("3de85a73-816c-49c0-bc43-4c7dec18c951");
    
    private static final String GUN_DAMAGE_NAME = "tcc.heaven_fire_apocalypse_endless.gun_damage";
    private static final String EXPLOSION_DAMAGE_NAME = "tcc.heaven_fire_apocalypse_endless.explosion_damage";
    
    public HeavenFireApocalypseEndless(Properties properties) {
        super(properties.stacksTo(1).fireResistant());
    }
    
    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        super.onEquip(slotContext, prevStack, stack);
        LivingEntity entity = (LivingEntity) slotContext.entity();
        applyEffects(entity, stack);
    }

    @Override
    protected boolean isBoundItem() {
        return true;
    }
    
    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        if (!GunTypeChecker.isHoldingConfiguredGunTypes(livingEntity, List.of("pistol"))) return;
        
        double damageBoost = TaczCuriosConfig.COMMON.endlessDamageBoost.get();
        double explosionDamageBoost = TaczCuriosConfig.COMMON.endlessExplosionDamage.get();

        double imaginaryResistance = livingEntity.getAttributeValue(TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get());
        double resistanceBoost = imaginaryResistance * TaczCuriosConfig.COMMON.endlessImaginaryResistanceDamagePerPoint.get() / 100.0;
        double totalDamageBoost = damageBoost + resistanceBoost;

        AttributeHelper.applyModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE, totalDamageBoost, GUN_DAMAGE_UUID, GUN_DAMAGE_NAME, AttributeModifier.Operation.MULTIPLY_BASE);
        AttributeHelper.applyModifier(livingEntity, AttributeHelper.EXPLOSION_DAMAGE, explosionDamageBoost, EXPLOSION_DAMAGE_UUID, EXPLOSION_DAMAGE_NAME, AttributeModifier.Operation.MULTIPLY_BASE);
    }
    
    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE, GUN_DAMAGE_UUID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.EXPLOSION_DAMAGE, EXPLOSION_DAMAGE_UUID);
    }
    
    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        applyEffects(slotContext.entity(), stack);
    }

    @Override
    public List<String> getWeaponTypeRestriction() {
        return List.of("pistol");
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        
        tooltip.add(Component.literal(""));
        
        double explosionDamageBoost = TaczCuriosConfig.COMMON.endlessExplosionDamage.get() * 100;
        double nearbyPlayerRadius = TaczCuriosConfig.COMMON.endlessNearbyPlayerRadius.get();
        double perLevelBoost = TaczCuriosConfig.COMMON.heavenFireApocalypseNearbyPlayerDamageBoost.get() * 100;
        int potionAmplifier = TaczCuriosConfig.COMMON.endlessNearbyPlayerPotionAmplifier.get();
        int totalNearbyPlayerDamageBoost = (int)(perLevelBoost * (potionAmplifier + 1));
        int nearbyPlayerDuration = TaczCuriosConfig.COMMON.endlessNearbyPlayerDuration.get();
        
        double damageBoost = TaczCuriosConfig.COMMON.endlessDamageBoost.get() * 100;
        double resistanceBonus = 0;
        if (level != null && level.isClientSide()) {
            LivingEntity wearer = TaczCuriosClientTooltip.resolveWearer(stack);
            if (wearer != null) {
                double imaginaryResistance = wearer.getAttributeValue(TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get());
                resistanceBonus = imaginaryResistance * TaczCuriosConfig.COMMON.endlessImaginaryResistanceDamagePerPoint.get();
            }
        }
        double totalDamageBoost = damageBoost + resistanceBonus;
        tooltip.add(formatModifierTooltip(totalDamageBoost, "%.0f%%", Component.translatable(AttributeHelper.BULLET_GUNDAMAGE.getDescriptionId()))
                .withStyle(ChatFormatting.RED));
        tooltip.add(formatModifierTooltip(explosionDamageBoost, "%.0f%%", Component.translatable(AttributeHelper.EXPLOSION_DAMAGE.getDescriptionId()))
                .withStyle(ChatFormatting.RED));
        tooltip.add(Component.translatable("tcc.tooltip.gun_to_imaginary")
            .withStyle(ChatFormatting.RED));
        tooltip.add(Component.translatable("item.tcc.heaven_fire_apocalypse_endless.special",
                String.format("%.0f", nearbyPlayerRadius), 
                String.format("%+d", totalNearbyPlayerDamageBoost),
                String.format("%d", nearbyPlayerDuration))
            .withStyle(ChatFormatting.RED));
        
        int infectionMax = TaczCuriosConfig.COMMON.endlessImaginaryInfectionMaxLevel.get();
        tooltip.add(Component.translatable("item.tcc.heaven_fire_apocalypse.inflection_max",
                String.format("%d", infectionMax))
            .withStyle(ChatFormatting.RED));

        tooltip.add(Component.translatable("tcc.tooltip.affected_by_imaginary_resistance")
            .withStyle(ChatFormatting.LIGHT_PURPLE));

        tooltip.add(Component.literal(""));
        
        appendBoundPlayer(stack, tooltip);
    }
    
    @SubscribeEvent
    public static void onGunHurtPre(EntityHurtByGunEvent.Pre event) {
        LivingEntity attacker = event.getAttacker();
        if (attacker == null || !hasHeavenFireApocalypseEndlessEquipped(attacker)) {
            return;
        }
        
        if (!(attacker.level() instanceof ServerLevel)) {
            return;
        }

        if (!GunTypeChecker.isHoldingConfiguredGunTypes(attacker, List.of("pistol"))) return;

        ImaginaryConversionHelper.convertToImaginary(event);
    }
    
    @SubscribeEvent
    public static void onGunHurt(EntityHurtByGunEvent.Post event) {
        LivingEntity attacker = TacDamageHelper.getAttacker(event);
        if (attacker == null) {
            return;
        }
        
        if (!hasHeavenFireApocalypseEndlessEquipped(attacker)) {
            return;
        }
        
        if (!(attacker.level() instanceof ServerLevel)) {
            return;
        }

        if (!GunTypeChecker.isHoldingConfiguredGunTypes(attacker, List.of("pistol"))) return;

        double nearbyPlayerRadius = TaczCuriosConfig.COMMON.endlessNearbyPlayerRadius.get();
        List<Player> nearbyPlayers = attacker.level().getEntitiesOfClass(Player.class, attacker.getBoundingBox().inflate(nearbyPlayerRadius));
        int nearbyPlayerDuration = TaczCuriosConfig.COMMON.endlessNearbyPlayerDuration.get();

        for (Player nearbyPlayer : nearbyPlayers) {
            int potionAmplifier = TaczCuriosConfig.COMMON.endlessNearbyPlayerPotionAmplifier.get();
            nearbyPlayer.addEffect(new MobEffectInstance(
                TccMobEffects.HEAVEN_FIRE_APOCALYPSE_BUFF.get(),
                nearbyPlayerDuration * 20,
                potionAmplifier,
                false, false, true));
        }
    }
    
    public static boolean hasHeavenFireApocalypseEndlessEquipped(LivingEntity livingEntity) {
        return !CurioSearchHelper.findFirstEquippedStack(livingEntity, stack -> stack.getItem() instanceof HeavenFireApocalypseEndless).isEmpty();
    }
    

}
