package com.xlxyvergil.tcc.items.curios.bound;

import com.tacz.guns.api.item.IGun;
import com.xlxyvergil.tcc.attribute.TccAttributes;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.AmmoRegenHelper;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.items.BoundCurioItem;
import com.xlxyvergil.tcc.util.GunTypeChecker;
import net.minecraft.ChatFormatting;
import com.xlxyvergil.tcc.client.TaczCuriosClientTooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TingzhiZhijian extends BoundCurioItem {
    private static final UUID OVERHEAL_UUID = UUID.fromString("c78456d7-685c-43b0-bf7a-0c69fd57862e");

    public TingzhiZhijian(Properties properties) {
        super(properties);
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        // 登记该饰品施加的修饰符 UUID → 来源饰品，供客户端属性面板显示来源图标。
        AttributeHelper.registerSourceItem(OVERHEAL_UUID, stack.getItem());
        if (matchesRestriction(livingEntity)) {
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.OVERHEAL,
                TaczCuriosConfig.COMMON.tingzhiZhijianOverheal.get(), OVERHEAL_UUID,
                "tcc.tingzhi_zhijian.overheal", AttributeModifier.Operation.ADDITION);
        } else {
            removeEffects(livingEntity);
        }
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.OVERHEAL, OVERHEAL_UUID);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity entity = slotContext.entity();
        if (entity.level().isClientSide()) return;
        if (entity.tickCount % 20 != 0) return;
        if (!GunTypeChecker.isHoldingRifle(entity)) return;

        ItemStack held = entity.getMainHandItem();
        IGun iGun = IGun.getIGunOrNull(held);
        if (iGun == null) return;

        double basePercent = TaczCuriosConfig.COMMON.tingzhiZhijianAmmoBasePercent.get();
        double totalResistance = entity.getAttributeValue(TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get());
        double resistanceScale = TaczCuriosConfig.COMMON.tingzhiZhijianAmmoResistanceScale.get();
        double percent = Math.round((basePercent + totalResistance * resistanceScale) * 10000.0) / 10000.0;

        AmmoRegenHelper.regenAmmo(entity, held, iGun, percent);
    }

    @Override
    protected boolean isBoundItem() {
        return true;
    }

    @Override
    public List<String> getWeaponTypeRestriction() {
        return List.of("rifle");
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        tooltip.add(Component.literal(""));

        double overheal = TaczCuriosConfig.COMMON.tingzhiZhijianOverheal.get() * 100;

        double resistance = 0;
        if (level != null && level.isClientSide()) {
            LivingEntity wearer = TaczCuriosClientTooltip.resolveWearer(stack);
            if (wearer != null) {
                resistance = wearer.getAttributeValue(TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get());
            }
        }
        double basePercent = TaczCuriosConfig.COMMON.tingzhiZhijianAmmoBasePercent.get();
        double scalePercent = resistance * TaczCuriosConfig.COMMON.tingzhiZhijianAmmoResistanceScale.get();
        double totalPercent = (basePercent + scalePercent) * 100;

        tooltip.add(formatModifierTooltip(overheal, "%.0f%%", Component.translatable(AttributeHelper.OVERHEAL.getDescriptionId()))
                .withStyle(ChatFormatting.WHITE));
        tooltip.add(Component.translatable("item.tcc.tingzhi_zhijian.special_ammo",
                String.format("%.1f", totalPercent))
            .withStyle(ChatFormatting.WHITE));

        tooltip.add(Component.translatable("tcc.tooltip.affected_by_imaginary_resistance")
            .withStyle(ChatFormatting.LIGHT_PURPLE));

        tooltip.add(Component.literal(""));
        appendBoundPlayer(stack, tooltip);
    }
}
