package com.xlxyvergil.tcc.items.curios.bound;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.attribute.TccAttributes;
import com.xlxyvergil.tcc.helpers.ImaginaryResistanceHelper;
import com.xlxyvergil.tcc.util.ItemNbtHelper;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.items.BoundCurioItem;
import com.xlxyvergil.tcc.util.ItemNbtHelper;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import com.xlxyvergil.tcc.util.ItemNbtHelper;
import com.xlxyvergil.tcc.util.DamageResistanceHelper;
import com.xlxyvergil.tcc.util.ItemNbtHelper;
import com.xlxyvergil.tcc.util.GunTypeChecker;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;

import javax.annotation.Nullable;
import java.util.List;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import net.minecraft.resources.ResourceLocation;
import com.xlxyvergil.tcc.TaczCurios;
@EventBusSubscriber(modid = "tcc")
public class Salvation extends BoundCurioItem {
    private static final ResourceLocation IMAGINARY_RESISTANCE_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "salvation_c3d4e5f6_9012");
    private static final ResourceLocation KNOCKBACK_RESISTANCE_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "salvation_f6a7b8c9_2345");
    
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
        // 登记该饰品施加的修饰符 ID → 来源饰品，供客户端属性面板显示来源图标。
        AttributeHelper.registerSourceItem(IMAGINARY_RESISTANCE_ID, stack.getItem());
        AttributeHelper.registerSourceItem(KNOCKBACK_RESISTANCE_ID, stack.getItem());
        // 虚数抗性不受武器类型限制，装备即生效。
        double imaginaryResistance = getSalvationResistance(livingEntity);
        AttributeHelper.applyModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE,
            imaginaryResistance, IMAGINARY_RESISTANCE_ID, AttributeModifier.Operation.ADD_VALUE);

        if (matchesRestriction(livingEntity)) {
            livingEntity.removeEffect(MobEffects.DAMAGE_RESISTANCE);
            livingEntity.addEffect(new MobEffectInstance(
                MobEffects.DAMAGE_RESISTANCE,
                300,
                2,
                false, false, true));
            
            AttributeHelper.applyModifier(livingEntity, Attributes.KNOCKBACK_RESISTANCE, 
                1.0, KNOCKBACK_RESISTANCE_ID, AttributeModifier.Operation.ADD_VALUE);
        }
    }
    
    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE, 
            IMAGINARY_RESISTANCE_ID);
        AttributeHelper.removeModifier(livingEntity, Attributes.KNOCKBACK_RESISTANCE, 
            KNOCKBACK_RESISTANCE_ID);
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
        CompoundTag tag = ItemNbtHelper.getTag(stack);
        return ImaginaryResistanceHelper.calculateTotalResistance(getBaseResistance(), tag);
    }
    
    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        Level level = context.level();
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
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (event.getEntity().level().isClientSide()) return;
        if (!hasSalvationEquipped(event.getEntity())) return;
        if (!GunTypeChecker.isHoldingPistol(event.getEntity())) {
            DamageResistanceHelper.clearDamageReduction(event.getEntity());
            return;
        }

        DamageResistanceHelper.setDamageReduction(event.getEntity(),
            (float) (1 - TaczCuriosConfig.COMMON.salvationDamageReduction.get()));

        if (event.getEntity().tickCount % 280 == 0) {
            int level = TaczCuriosConfig.COMMON.salvationResistanceLevel.get();
            event.getEntity().removeEffect(MobEffects.DAMAGE_RESISTANCE);
            event.getEntity().addEffect(new MobEffectInstance(
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
