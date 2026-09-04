package com.xlxyvergil.tcc.items.curios.bound;

import com.xlxyvergil.tcc.attribute.TccAttributes;
import com.xlxyvergil.tcc.helpers.ImaginaryResistanceHelper;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.items.BoundCurioItem;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SummerBeach extends BoundCurioItem {
    private static final UUID IMAGINARY_RESISTANCE_MODIFIER_UUID = UUID.fromString("a7ef6d4d-d047-4627-99b7-2bcc32b577a7");
    
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
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        // 登记该饰品施加的修饰符 UUID → 来源饰品，供客户端属性面板显示来源图标。
        AttributeHelper.registerSourceItem(IMAGINARY_RESISTANCE_MODIFIER_UUID, stack.getItem());
        if (matchesRestriction(livingEntity)) {
            ItemStack equipped = findEquippedStack(livingEntity);
            CompoundTag tag = equipped.getTag();
            double total = ImaginaryResistanceHelper.calculateTotalResistance(getBaseResistance(), tag);
            AttributeHelper.applyModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get(),
                total, IMAGINARY_RESISTANCE_MODIFIER_UUID, "tcc_summer_beach_resistance", AttributeModifier.Operation.ADDITION);
        }
    }
    
    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get(), IMAGINARY_RESISTANCE_MODIFIER_UUID);
    }

    @Override
    public java.util.List<String> getWeaponTypeRestriction() {
        return java.util.List.of("pistol");
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal(""));
        
        appendImaginaryResistance(stack, tooltip);
        
        appendBoundPlayer(stack, tooltip);

        tooltip.add(Component.literal(""));
 
    }

    public static boolean hasSummerBeachEquipped(LivingEntity livingEntity) {
        return !CurioSearchHelper.findFirstEquippedStack(livingEntity, stack -> stack.getItem() instanceof SummerBeach).isEmpty();
    }

    private static ItemStack findEquippedStack(LivingEntity livingEntity) {
        return CurioSearchHelper.findFirstEquippedStack(livingEntity, stack -> stack.getItem() instanceof SummerBeach);
    }

    private static int getBaseResistance() {
        return 1;
    }

    }
