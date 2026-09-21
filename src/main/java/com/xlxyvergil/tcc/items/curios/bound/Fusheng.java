package com.xlxyvergil.tcc.items.curios.bound;

import com.xlxyvergil.tcc.attribute.TccAttributes;
import com.xlxyvergil.tcc.helpers.ImaginaryResistanceHelper;
import com.xlxyvergil.tcc.util.ItemNbtHelper;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.items.BoundCurioItem;
import com.xlxyvergil.tcc.util.ItemNbtHelper;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import net.minecraft.ChatFormatting;
import com.xlxyvergil.tcc.client.TaczCuriosClientTooltip;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import top.theillusivec4.curios.api.SlotContext;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.resources.ResourceLocation;
import com.xlxyvergil.tcc.TaczCurios;
public class Fusheng extends BoundCurioItem {
    private static final ResourceLocation ARMOR_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "fusheng_7108d2bf_9c02");
    private static final ResourceLocation TOUGHNESS_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "fusheng_9570537d_ddc1");
    private static final ResourceLocation IMAGINARY_RESISTANCE_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "fusheng_d2e8b5a1_4d67");

    public Fusheng(Properties properties) {
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
                stack -> stack.getItem() instanceof Fusheng).isEmpty();
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        // 登记该饰品施加的修饰符 ID → 来源饰品，供客户端属性面板显示来源图标。
        AttributeHelper.registerSourceItem(ARMOR_ID, stack.getItem());
        AttributeHelper.registerSourceItem(TOUGHNESS_ID, stack.getItem());
        AttributeHelper.registerSourceItem(IMAGINARY_RESISTANCE_ID, stack.getItem());
        ItemStack equipped = CurioSearchHelper.findFirstEquippedStack(livingEntity,
                s -> s.getItem() instanceof Fusheng);
        CompoundTag tag = ItemNbtHelper.getTag(equipped);
        double total = 1.0
                + ImaginaryResistanceHelper.getExtraResistanceFromProgress(tag);
        AttributeHelper.applyModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE,
                total, IMAGINARY_RESISTANCE_ID, AttributeModifier.Operation.ADD_VALUE);
        if (matchesRestriction(livingEntity)) {
            double pct = ImaginaryResistanceHelper.getResistanceValue(livingEntity) / 100.0;
            AttributeHelper.applyModifier(livingEntity, Attributes.ARMOR,
                    pct, ARMOR_ID, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
            AttributeHelper.applyModifier(livingEntity, Attributes.ARMOR_TOUGHNESS,
                    pct, TOUGHNESS_ID, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        } else {
            // 武器不匹配时只清理受限制的护甲加成，虚数抗性为无条件施加，需保留。
            AttributeHelper.removeModifier(livingEntity, Attributes.ARMOR, ARMOR_ID);
            AttributeHelper.removeModifier(livingEntity, Attributes.ARMOR_TOUGHNESS, TOUGHNESS_ID);
        }
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE, IMAGINARY_RESISTANCE_ID);
        AttributeHelper.removeModifier(livingEntity, Attributes.ARMOR, ARMOR_ID);
        AttributeHelper.removeModifier(livingEntity, Attributes.ARMOR_TOUGHNESS, TOUGHNESS_ID);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        applyEffects(slotContext.entity(), stack);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        Level level = context.level();
        super.appendHoverText(stack, context, tooltip, flag);
        appendImaginaryResistance(stack, tooltip);

        double resistance = 0;
        if (level != null && level.isClientSide()) {
            LivingEntity wearer = TaczCuriosClientTooltip.resolveWearer(stack);
            if (wearer != null) {
                resistance = wearer.getAttributeValue(TccAttributes.IMAGINARY_DAMAGE_RESISTANCE);
            }
        }
        tooltip.add(formatModifierTooltip(resistance, "%.0f%%", Component.translatable(Attributes.ARMOR.value().getDescriptionId()))
                .withStyle(ChatFormatting.GOLD));
        tooltip.add(formatModifierTooltip(resistance, "%.0f%%", Component.translatable(Attributes.ARMOR_TOUGHNESS.value().getDescriptionId()))
                .withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.translatable("tcc.tooltip.affected_by_imaginary_resistance")
                .withStyle(ChatFormatting.LIGHT_PURPLE));

        appendBoundPlayer(stack, tooltip);
    }
}
