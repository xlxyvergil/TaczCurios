package com.xlxyvergil.tcc.items.curios.bound;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.attribute.TccAttributes;
import com.xlxyvergil.tcc.helpers.ImaginaryResistanceHelper;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.items.BoundCurioItem;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import com.xlxyvergil.tcc.util.DamageResistanceHelper;
import com.xlxyvergil.tcc.util.GunTypeChecker;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@Mod.EventBusSubscriber(modid = "tcc", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class Salvation extends BoundCurioItem {
    private static final UUID IMAGINARY_RESISTANCE_UUID = UUID.fromString("c3d4e5f6-a7b8-9012-cdef-123456789012");
    private static final UUID KNOCKBACK_RESISTANCE_UUID = UUID.fromString("f6a7b8c9-d0e1-2345-f012-456789012345");
    
    public Salvation(Properties properties) {
        super(properties.stacksTo(1).fireResistant());
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }
    
    @Override
    public int getEnchantmentValue(ItemStack stack) {
        return 0;
    }
    
    @Override
    public boolean hasCraftingRemainingItem(ItemStack stack) {
        return false;
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        if (matchesRestriction(livingEntity)) {
            double imaginaryResistance = getSalvationResistance(livingEntity);
            AttributeHelper.applyModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get(), 
                imaginaryResistance, IMAGINARY_RESISTANCE_UUID, "tcc_salvation_imaginary_resistance", AttributeModifier.Operation.ADDITION);
            
            livingEntity.removeEffect(MobEffects.DAMAGE_RESISTANCE);
            livingEntity.addEffect(new MobEffectInstance(
                MobEffects.DAMAGE_RESISTANCE,
                300,
                2,
                false, false, true));
            
            AttributeHelper.applyModifier(livingEntity, Attributes.KNOCKBACK_RESISTANCE, 
                1.0, KNOCKBACK_RESISTANCE_UUID, "tcc_salvation_knockback_immunity", AttributeModifier.Operation.ADDITION);
        }
    }
    
    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get(), 
            IMAGINARY_RESISTANCE_UUID);
        AttributeHelper.removeModifier(livingEntity, Attributes.KNOCKBACK_RESISTANCE, 
            KNOCKBACK_RESISTANCE_UUID);
        livingEntity.removeEffect(MobEffects.DAMAGE_RESISTANCE);
        DamageResistanceHelper.clearDamageCap(livingEntity);
        DamageResistanceHelper.clearDamageReduction(livingEntity);
    }

    @Override
    public java.util.List<String> getWeaponTypeRestriction() {
        return java.util.List.of("pistol");
    }
    
    private static double getSalvationResistance(LivingEntity entity) {
        ItemStack stack = findEquippedStack(entity);
        if (stack.isEmpty()) {
            return 0;
        }
        CompoundTag tag = stack.getTag();
        return ImaginaryResistanceHelper.calculateTotalResistance(getBaseResistance(), tag);
    }
    
    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal(""));
        
        double damageReduction = TaczCuriosConfig.COMMON.salvationDamageReduction.get() * 100;
        
        appendImaginaryResistance(stack, tooltip);
        
        tooltip.add(Component.translatable("item.tcc.salvation.passive_bonuses")
            .withStyle(ChatFormatting.RED));
        tooltip.add(Component.translatable("tcc.tooltip.damage_reduction", String.format("%.0f", damageReduction))
            .withStyle(ChatFormatting.RED));
        
        tooltip.add(Component.literal(""));
 
        appendBoundPlayer(stack, tooltip);
    }
    
    
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (event.player.level().isClientSide()) return;
        if (!hasSalvationEquipped(event.player)) return;
        if (!GunTypeChecker.isHoldingPistol(event.player)) {
            DamageResistanceHelper.clearDamageReduction(event.player);
            return;
        }

        DamageResistanceHelper.setDamageReduction(event.player,
            (float) (1 - TaczCuriosConfig.COMMON.salvationDamageReduction.get()));

        if (event.player.tickCount % 280 == 0) {
            int level = TaczCuriosConfig.COMMON.salvationResistanceLevel.get();
            event.player.removeEffect(MobEffects.DAMAGE_RESISTANCE);
            event.player.addEffect(new MobEffectInstance(
                MobEffects.DAMAGE_RESISTANCE,
                300, level, false, false, true));
        }
    }
    
    @Override
    protected boolean isBoundItem() {
        return true;
    }
    
    public static boolean hasSalvationEquipped(LivingEntity livingEntity) {
        return !CurioSearchHelper.findFirstEquippedStack(livingEntity, stack -> stack.getItem() instanceof Salvation).isEmpty();
    }

    private static ItemStack findEquippedStack(LivingEntity livingEntity) {
        return CurioSearchHelper.findFirstEquippedStack(livingEntity, stack -> stack.getItem() instanceof Salvation);
    }

    private static int getBaseResistance() {
        return 1;
    }

    }
