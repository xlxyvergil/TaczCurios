package com.xlxyvergil.tcc.items;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.core.TccAttributes;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.util.BaseCurioItem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio.DropRule;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

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
        // 虚数抗性（可配置）
        double imaginaryResistance = com.xlxyvergil.tcc.config.TaczCuriosConfig.COMMON.salvationImaginaryResistance.get();
        AttributeHelper.applyModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get(), 
            imaginaryResistance, IMAGINARY_RESISTANCE_UUID, "tcc_salvation_imaginary_resistance", AttributeModifier.Operation.ADDITION);
        
        // 常驻抗性提升（可配置，通过效果实现）
        livingEntity.removeEffect(net.minecraft.world.effect.MobEffects.DAMAGE_RESISTANCE);
        livingEntity.addEffect(new net.minecraft.world.effect.MobEffectInstance(
            net.minecraft.world.effect.MobEffects.DAMAGE_RESISTANCE,
            300,  // 15秒，tick会刷新
            2,    // 等级2 = 抗性提升III（等级从0开始）
            false, false, true));
        
        // 免疫击退（knockback_resistance = 1.0）
        AttributeHelper.applyModifier(livingEntity, net.minecraft.world.entity.ai.attributes.Attributes.KNOCKBACK_RESISTANCE, 
            1.0, KNOCKBACK_RESISTANCE_UUID, "tcc_salvation_knockback_immunity", AttributeModifier.Operation.ADDITION);
        
        // 注意：伤害降低通过 HeavenFireHealthListener 中的 LivingHurtEvent 实现（可配置）
    }
    
    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get(), 
            IMAGINARY_RESISTANCE_UUID);
        AttributeHelper.removeModifier(livingEntity, net.minecraft.world.entity.ai.attributes.Attributes.KNOCKBACK_RESISTANCE, 
            KNOCKBACK_RESISTANCE_UUID);
        // 移除抗性提升效果
        livingEntity.removeEffect(net.minecraft.world.effect.MobEffects.DAMAGE_RESISTANCE);
    }
    
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal(""));
        
        // 基础效果（从配置读取）
        double imaginaryResistance = com.xlxyvergil.tcc.config.TaczCuriosConfig.COMMON.salvationImaginaryResistance.get();
        tooltip.add(Component.translatable("item.tcc.salvation.effect", String.format("+%.0f", imaginaryResistance))
            .withStyle(ChatFormatting.AQUA));
        
        // 常驻加成
        tooltip.add(Component.translatable("item.tcc.salvation.passive_bonuses")
            .withStyle(ChatFormatting.GREEN));
        
        // 天火增强
        double multiplier = com.xlxyvergil.tcc.config.TaczCuriosConfig.COMMON.salvationHeavenFireMultiplier.get();
        tooltip.add(Component.translatable("item.tcc.salvation.heaven_fire_boost", String.format("%.0f", multiplier * 100))
            .withStyle(ChatFormatting.GOLD));
        
        // 槽位和稀有度
        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable("tcc.tooltip.slot.3rd"));
        tooltip.add(Component.translatable("tcc.tooltip.rarity.rift"));
        
        // 添加获取方式
        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable("item.tcc.salvation.how_to_obtain")
            .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
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
            int level = com.xlxyvergil.tcc.config.TaczCuriosConfig.COMMON.salvationResistanceLevel.get();
            event.player.removeEffect(net.minecraft.world.effect.MobEffects.DAMAGE_RESISTANCE);
            event.player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                net.minecraft.world.effect.MobEffects.DAMAGE_RESISTANCE,
                300, level, false, false, true));
        }
    }
    
    @Override
    public boolean canUnequip(SlotContext context, ItemStack stack) {
        // 仅创造模式可卸下
        if (context.entity() instanceof Player player && player.isCreative())
            return super.canUnequip(context, stack);
        return false;
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
        return CuriosApi.getCuriosInventory(livingEntity)
            .map(handler -> {
                var stacksHandler = handler.getCurios().get("tcc_3rd");
                if (stacksHandler != null) {
                    for (int i = 0; i < stacksHandler.getSlots(); i++) {
                        ItemStack stack = stacksHandler.getStacks().getStackInSlot(i);
                        if (stack.getItem() instanceof Salvation) {
                            return true;
                        }
                    }
                }
                return false;
            })
            .orElse(false);
    }
}
