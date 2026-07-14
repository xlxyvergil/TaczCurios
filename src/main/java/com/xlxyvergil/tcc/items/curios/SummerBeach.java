package com.xlxyvergil.tcc.items.curios;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.attribute.TccAttributes;
import com.xlxyvergil.tcc.evolution.EvolutionRegistry;
import com.xlxyvergil.tcc.helpers.ImaginaryResistanceHelper;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.util.BaseCurioItem;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import com.xlxyvergil.tcc.util.EntityConditionHelper;
import com.xlxyvergil.tcc.util.EvolutionNbtKeys;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * 夏日沙滩 - 提供基础20点虚数抗性，击杀配置列表中的实体获得额外抗性
 */
public class SummerBeach extends BaseCurioItem {
    // 虚数抗性修饰符的UUID（确保唯一性）
    private static final UUID IMAGINARY_RESISTANCE_MODIFIER_UUID = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890");
    
    public SummerBeach(Properties properties) {
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
        ItemStack equipped = findEquippedStack(livingEntity);
        CompoundTag tag = equipped.getTag();
        double total = ImaginaryResistanceHelper.calculateTotalResistance(getBaseResistance(), tag);
        AttributeHelper.applyModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get(),
            total, IMAGINARY_RESISTANCE_MODIFIER_UUID, "tcc_summer_beach_resistance", AttributeModifier.Operation.ADDITION);
    }
    
    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get(), IMAGINARY_RESISTANCE_MODIFIER_UUID);
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
    
    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal(""));
        
        // NBT
        CompoundTag tag = stack.getTag();
        
        double baseValue = getBaseResistance();
        double total = baseValue + ImaginaryResistanceHelper.getExtraResistanceFromProgress(tag);
        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable("tcc.tooltip.imaginary_resistance", String.format("%.0f", total))
            .withStyle(ChatFormatting.GOLD));
        
        // EL 第四诅咒削弱（仅加载神秘遗物时显示）
        if (net.minecraftforge.fml.ModList.get().isLoaded("enigmaticlegacy")) {
            double curseReduction = TaczCuriosConfig.COMMON.summerBeachELCurseReduction.get();
            tooltip.add(Component.translatable("item.tcc.summer_beach.el_curse_reduction", String.format("%.0f", curseReduction * 100))
                .withStyle(ChatFormatting.LIGHT_PURPLE));
        }
        
        // 绑定信息
        if (tag != null && tag.getBoolean("IsBound")) {
            String boundPlayerName = tag.getString("BoundPlayerName");
            tooltip.add(Component.literal(""));
            tooltip.add(Component.translatable("tcc.tooltip.bound", boundPlayerName)
                .withStyle(ChatFormatting.RED));
        }
        
        // 槽位和稀有度
        tooltip.add(Component.literal(""));
 
        tooltip.add(Component.translatable("tcc.tooltip.rarity.rare"));
    }

    @Override
    public void applyGunSwitchEffect(LivingEntity livingEntity) {
        applyEffects(livingEntity);
    }
    
    public static boolean hasSummerBeachEquipped(LivingEntity livingEntity) {
        return !CurioSearchHelper.findFirstEquippedStack(livingEntity, stack -> stack.getItem() instanceof SummerBeach).isEmpty();
    }

    private static ItemStack findEquippedStack(LivingEntity livingEntity) {
        return CurioSearchHelper.findFirstEquippedStack(livingEntity, stack -> stack.getItem() instanceof SummerBeach);
    }

    private static int getBaseResistance() {
        return TaczCuriosConfig.COMMON.summerBeachBaseResistance.get();
    }

    }
