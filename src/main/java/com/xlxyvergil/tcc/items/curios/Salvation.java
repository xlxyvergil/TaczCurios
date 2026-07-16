package com.xlxyvergil.tcc.items.curios;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.attribute.TccAttributes;
import com.xlxyvergil.tcc.helpers.ImaginaryResistanceHelper;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.util.BaseCurioItem;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
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
public class Salvation extends BaseCurioItem {
    // 属性修饰符UUID
    private static final UUID IMAGINARY_RESISTANCE_UUID = UUID.fromString("c3d4e5f6-a7b8-9012-cdef-123456789012");
    private static final UUID KNOCKBACK_RESISTANCE_UUID = UUID.fromString("f6a7b8c9-d0e1-2345-f012-456789012345");
    
    public Salvation(Properties properties) {
        super(properties.stacksTo(1).fireResistant());
    }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        super.onEquip(slotContext, prevStack, stack);
        if (slotContext.entity() instanceof Player player) {
            CompoundTag tag = stack.getOrCreateTag();
            if (!tag.getBoolean("IsBound")) {
                tag.putBoolean("IsBound", true);
                tag.putString("BoundPlayer", player.getStringUUID());
                tag.putString("BoundPlayerName", player.getGameProfile().getName());
            }
        }
    }

    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.getBoolean("IsBound")) {
            String boundPlayerUUID = tag.getString("BoundPlayer");
            if (slotContext.entity() instanceof Player player) {
                return player.getStringUUID().equals(boundPlayerUUID);
            }
            return false;
        }
        return super.canEquip(slotContext, stack);
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
    protected void applyEffects(LivingEntity livingEntity) {
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
        
        // 注意：伤害降低通过 HeavenFireHealthListener 中的 LivingHurtEvent 实现（可配置）
    }
    
    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get(), 
            IMAGINARY_RESISTANCE_UUID);
        AttributeHelper.removeModifier(livingEntity, Attributes.KNOCKBACK_RESISTANCE, 
            KNOCKBACK_RESISTANCE_UUID);
        // 移除抗性提升效果
        livingEntity.removeEffect(MobEffects.DAMAGE_RESISTANCE);
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
        
        double damageReduction = TaczCuriosConfig.COMMON.salvationDamageReduction.get() * 100;
        
        // 继承抗性
        CompoundTag tag = stack.getTag();
        double baseValue = getBaseResistance();
        double progressValue = ImaginaryResistanceHelper.getExtraResistanceFromProgress(tag);
        double total = baseValue + progressValue;
        tooltip.add(formatModifierTooltip(total, "%.0f", Component.translatable(TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get().getDescriptionId()))
            .withStyle(ChatFormatting.RED));
        
        // 常驻加成
        tooltip.add(Component.translatable("item.tcc.salvation.passive_bonuses", String.format("%.0f", damageReduction))
            .withStyle(ChatFormatting.RED));
        
        // 槽位和稀有度
        tooltip.add(Component.literal(""));
 
        tooltip.add(Component.translatable("tcc.tooltip.rarity.rift"));

        if (tag != null && tag.getBoolean("IsBound")) {
            String boundPlayerName = tag.getString("BoundPlayerName");
            tooltip.add(Component.literal(""));
            tooltip.add(Component.translatable("tcc.tooltip.bound", boundPlayerName)
                .withStyle(ChatFormatting.RED));
        }
    }
    
    @Override
    public void applyGunSwitchEffect(LivingEntity livingEntity) {
        applyEffects(livingEntity);
    }
    
    /**
     * 每tick刷新抗性提升效果
     */
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (event.player.level().isClientSide()) return;
        if (!hasSalvationEquipped(event.player)) return;
        
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
        return TaczCuriosConfig.COMMON.summerBeachBaseResistance.get();
    }

    }
