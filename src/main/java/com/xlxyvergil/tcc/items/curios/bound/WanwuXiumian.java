package com.xlxyvergil.tcc.items.curios.bound;

import com.tacz.guns.api.item.IGun;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.items.BoundCurioItem;
import com.xlxyvergil.tcc.util.AmmoRegenHelper;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.util.GunTypeChecker;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import top.theillusivec4.curios.api.SlotContext;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class WanwuXiumian extends BoundCurioItem {
    private static final UUID OVERHEAL_UUID = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890");

    public WanwuXiumian(Properties properties) {
        super(properties);
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        if (matchesRestriction(livingEntity)) {
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.OVERHEAL,
                TaczCuriosConfig.COMMON.wanwuXiumianOverheal.get(), OVERHEAL_UUID,
                "tcc.wanwu_xiumian.overheal", AttributeModifier.Operation.ADDITION);
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

        AmmoRegenHelper.regenAmmo(entity, held, iGun,
            TaczCuriosConfig.COMMON.wanwuXiumianAmmoRegenPercent.get());
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

        double overheal = TaczCuriosConfig.COMMON.wanwuXiumianOverheal.get() * 100;
        double ammoRegen = TaczCuriosConfig.COMMON.wanwuXiumianAmmoRegenPercent.get() * 100;

        tooltip.add(formatModifierTooltip(overheal, "%.0f%%", Component.translatable(AttributeHelper.OVERHEAL.getDescriptionId()))
                .withStyle(ChatFormatting.AQUA));
        tooltip.add(Component.translatable("item.tcc.wanwu_xiumian.special_ammo",
                String.format("%.0f", ammoRegen))
            .withStyle(ChatFormatting.AQUA));

        tooltip.add(Component.literal(""));
        appendBoundPlayer(stack, tooltip);
    }
}
