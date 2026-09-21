package com.xlxyvergil.tcc.items.curios.bound;

import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.xlxyvergil.tcc.attribute.TccAttributes;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.event.TccAttributeEvents;
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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;
import top.theillusivec4.curios.api.SlotContext;

import javax.annotation.Nullable;
import java.util.List;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import net.minecraft.resources.ResourceLocation;
import com.xlxyvergil.tcc.TaczCurios;
@EventBusSubscriber(modid = "tcc")
public class HeavenFireApocalypseEndless extends BoundCurioItem {
    private static final ResourceLocation GUN_DAMAGE_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "heaven_fire_apocalypse_endless_8c87e97e_b219");
    private static final ResourceLocation EXPLOSION_DAMAGE_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "heaven_fire_apocalypse_endless_3de85a73_c951");
    
    
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
        // 登记该饰品施加的修饰符 ID → 来源饰品，供客户端属性面板显示来源图标。
        AttributeHelper.registerSourceItem(GUN_DAMAGE_ID, stack.getItem());
        AttributeHelper.registerSourceItem(EXPLOSION_DAMAGE_ID, stack.getItem());
        if (!GunTypeChecker.isHoldingConfiguredGunTypes(livingEntity, List.of("pistol"))) return;
        
        double damageBoost = TaczCuriosConfig.COMMON.endlessDamageBoost.get();
        double explosionDamageBoost = TaczCuriosConfig.COMMON.endlessExplosionDamage.get();

        double imaginaryResistance = livingEntity.getAttributeValue(TccAttributes.IMAGINARY_DAMAGE_RESISTANCE);
        double resistanceBoost = imaginaryResistance * TaczCuriosConfig.COMMON.endlessImaginaryResistanceDamagePerPoint.get() / 100.0;
        double totalDamageBoost = damageBoost + resistanceBoost;

        AttributeHelper.applyModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE, totalDamageBoost, GUN_DAMAGE_ID, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        AttributeHelper.applyModifier(livingEntity, AttributeHelper.EXPLOSION_DAMAGE, explosionDamageBoost, EXPLOSION_DAMAGE_ID, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    }
    
    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE, GUN_DAMAGE_ID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.EXPLOSION_DAMAGE, EXPLOSION_DAMAGE_ID);
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
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        Level level = context.level();
        super.appendHoverText(stack, context, tooltip, flag);
        
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
                double imaginaryResistance = wearer.getAttributeValue(TccAttributes.IMAGINARY_DAMAGE_RESISTANCE);
                resistanceBonus = imaginaryResistance * TaczCuriosConfig.COMMON.endlessImaginaryResistanceDamagePerPoint.get();
            }
        }
        double totalDamageBoost = damageBoost + resistanceBonus;
        tooltip.add(formatModifierTooltip(totalDamageBoost, "%.0f%%", Component.translatable(AttributeHelper.BULLET_GUNDAMAGE.value().getDescriptionId()))
                .withStyle(ChatFormatting.RED));
        tooltip.add(formatModifierTooltip(explosionDamageBoost, "%.0f%%", Component.translatable(AttributeHelper.EXPLOSION_DAMAGE.value().getDescriptionId()))
                .withStyle(ChatFormatting.RED));
        tooltip.add(Component.translatable("tcc.tooltip.gun_to_imaginary")
            .withStyle(ChatFormatting.RED));
        tooltip.add(Component.translatable("item.tcc.heaven_fire_apocalypse_endless.special",
                String.format("%.0f", nearbyPlayerRadius), 
                String.format("%+d", totalNearbyPlayerDamageBoost),
                String.format("%d", nearbyPlayerDuration))
            .withStyle(ChatFormatting.RED));

        tooltip.add(Component.translatable("tcc.tooltip.affected_by_imaginary_resistance")
            .withStyle(ChatFormatting.LIGHT_PURPLE));

        tooltip.add(Component.literal(""));
        
        appendAlwaysImaginaryCollapse(tooltip);
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

        // 统一经由 applyCollapse 施加剧增崩解：命中且目标存活时必定施加
        Entity hurt = event.getHurtEntity();
        if (hurt instanceof LivingEntity target && !target.isDeadOrDying()) {
            TccAttributeEvents.applyCollapse(target, attacker);
        }

        double nearbyPlayerRadius = TaczCuriosConfig.COMMON.endlessNearbyPlayerRadius.get();
        List<Player> nearbyPlayers = attacker.level().getEntitiesOfClass(Player.class, attacker.getBoundingBox().inflate(nearbyPlayerRadius));
        int nearbyPlayerDuration = TaczCuriosConfig.COMMON.endlessNearbyPlayerDuration.get();

        for (Player nearbyPlayer : nearbyPlayers) {
            int potionAmplifier = TaczCuriosConfig.COMMON.endlessNearbyPlayerPotionAmplifier.get();
            nearbyPlayer.addEffect(new MobEffectInstance(
                TccMobEffects.HEAVEN_FIRE_APOCALYPSE_BUFF,
                nearbyPlayerDuration * 20,
                potionAmplifier,
                false, false, true));
        }
    }
    
    public static boolean hasHeavenFireApocalypseEndlessEquipped(LivingEntity livingEntity) {
        return !CurioSearchHelper.findFirstEquippedStack(livingEntity, stack -> stack.getItem() instanceof HeavenFireApocalypseEndless).isEmpty();
    }
    

}
