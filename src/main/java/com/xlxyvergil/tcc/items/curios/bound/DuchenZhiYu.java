package com.xlxyvergil.tcc.items.curios.bound;

import com.xlxyvergil.tcc.attribute.TccAttributes;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.helpers.ImaginaryResistanceHelper;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.items.BoundCurioItem;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import top.theillusivec4.curios.api.SlotContext;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class DuchenZhiYu extends BoundCurioItem {
    private static final UUID ARMOR_UUID = UUID.fromString("cfa7072d-d2f0-4ee6-9d79-8ec96a638fe7");
    private static final UUID TOUGHNESS_UUID = UUID.fromString("431d864e-740c-4b87-b30b-abb21ed73aad");
    private static final UUID IMAGINARY_RESISTANCE_UUID = UUID.fromString("8f3b6d2e-1a9c-4e7b-a5d2-6c1e8a4b3d79");

    private static double armorPct() {
        return TaczCuriosConfig.COMMON.duchenZhiYuArmorPercent.get();
    }

    public DuchenZhiYu(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean isBoundItem() {
        return true;
    }

    @Override
    public List<String> getWeaponTypeRestriction() {
        return List.of("melee");
    }

    public static boolean isEquipped(LivingEntity entity) {
        return !CurioSearchHelper.findFirstEquippedStack(entity,
                stack -> stack.getItem() instanceof DuchenZhiYu).isEmpty();
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        // 登记该饰品施加的修饰符 UUID → 来源饰品，供客户端属性面板显示来源图标。
        AttributeHelper.registerSourceItem(ARMOR_UUID, stack.getItem());
        AttributeHelper.registerSourceItem(TOUGHNESS_UUID, stack.getItem());
        AttributeHelper.registerSourceItem(IMAGINARY_RESISTANCE_UUID, stack.getItem());
        ItemStack equipped = CurioSearchHelper.findFirstEquippedStack(livingEntity,
                s -> s.getItem() instanceof DuchenZhiYu);
        CompoundTag tag = equipped.getTag();
        double total = 1.0
                + ImaginaryResistanceHelper.getExtraResistanceFromProgress(tag);
        AttributeHelper.applyModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get(),
                total, IMAGINARY_RESISTANCE_UUID,
                "tcc.duchen_zhi_yu.imaginary_resistance", AttributeModifier.Operation.ADDITION);
        if (matchesRestriction(livingEntity)) {
            AttributeHelper.applyModifier(livingEntity, Attributes.ARMOR,
                    armorPct(), ARMOR_UUID,
                    "tcc.transient.armor", AttributeModifier.Operation.MULTIPLY_TOTAL);
            AttributeHelper.applyModifier(livingEntity, Attributes.ARMOR_TOUGHNESS,
                    armorPct(), TOUGHNESS_UUID,
                    "tcc.transient.toughness", AttributeModifier.Operation.MULTIPLY_TOTAL);
        } else {
            removeEffects(livingEntity);
        }
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get(), IMAGINARY_RESISTANCE_UUID);
        AttributeHelper.removeModifier(livingEntity, Attributes.ARMOR, ARMOR_UUID);
        AttributeHelper.removeModifier(livingEntity, Attributes.ARMOR_TOUGHNESS, TOUGHNESS_UUID);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        applyEffects(slotContext.entity(), stack);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        appendImaginaryResistance(stack, tooltip);
        tooltip.add(formatModifierTooltip(armorPct() * 100, "%.0f%%", Component.translatable(Attributes.ARMOR.getDescriptionId()))
                .withStyle(ChatFormatting.GOLD));
        tooltip.add(formatModifierTooltip(armorPct() * 100, "%.0f%%", Component.translatable(Attributes.ARMOR_TOUGHNESS.getDescriptionId()))
                .withStyle(ChatFormatting.GOLD));
        appendBoundPlayer(stack, tooltip);
    }
}
