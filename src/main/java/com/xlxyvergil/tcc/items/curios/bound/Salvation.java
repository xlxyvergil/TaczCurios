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
import net.minecraft.world.damagesource.DamageSource;
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
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio.DropRule;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * 救世 - 裂隙级饰品
 */
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
            
            // 常驻抗性提升（可配置，通过效果实现）
            livingEntity.removeEffect(MobEffects.DAMAGE_RESISTANCE);
            livingEntity.addEffect(new MobEffectInstance(
                MobEffects.DAMAGE_RESISTANCE,
                300,  // 15秒，tick会刷新
                2,    // 等级2 = 抗性提升III（等级从0开始）
                false, false, true));
            
            // 免疫击退（knockback_resistance = 1.0）
            AttributeHelper.applyModifier(livingEntity, Attributes.KNOCKBACK_RESISTANCE, 
                1.0, KNOCKBACK_RESISTANCE_UUID, "tcc_salvation_knockback_immunity", AttributeModifier.Operation.ADDITION);
            
            // 伤害降低：由 onPlayerTick 中维护的常驻比例减伤实现（仅手枪，可配置，对 setHealth 亦生效）
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
    
    /**
     * 读取救世装备上的继承抗性值
     */
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
        
        // 显示降低比例：配置值 × 100，例如 0.4 显示为降低40%
        double damageReduction = TaczCuriosConfig.COMMON.salvationDamageReduction.get() * 100;
        
        appendImaginaryResistance(stack, tooltip);
        
        // 常驻加成
        tooltip.add(Component.translatable("item.tcc.salvation.passive_bonuses")
            .withStyle(ChatFormatting.RED));
        // 伤害减免（公共语言键，独立一行）
        tooltip.add(Component.translatable("tcc.tooltip.damage_reduction", String.format("%.0f", damageReduction))
            .withStyle(ChatFormatting.RED));
        
        // 槽位和稀有度
        tooltip.add(Component.literal(""));
 
        appendBoundPlayer(stack, tooltip);
    }
    
    
    /**
     * 每tick刷新抗性提升效果
     */
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (event.player.level().isClientSide()) return;
        if (!hasSalvationEquipped(event.player)) return;
        // 检查武器类型限制（仅手枪生效）
        if (!GunTypeChecker.isHoldingPistol(event.player)) {
            // 不满足限制时清除常驻限伤
            DamageResistanceHelper.clearDamageReduction(event.player);
            return;
        }

        // 常驻比例减伤（仅手枪）：对标准 hurt 与直接 setHealth 扣血均生效
        DamageResistanceHelper.setDamageReduction(event.player,
            (float) (1 - TaczCuriosConfig.COMMON.salvationDamageReduction.get()));

        // 每15秒刷新一次抗性提升
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

    @Override
    public DropRule getDropRule(SlotContext slotContext, DamageSource source, int lootingLevel,
            boolean recentlyHit, ItemStack stack) {
        return DropRule.ALWAYS_KEEP;
    }
    
    /**
     * 检查实体是否装备了救世
     */
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
