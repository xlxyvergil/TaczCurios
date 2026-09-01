package com.xlxyvergil.tcc.items.curios.bound;

import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.tacz.guns.api.item.IGun;
import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.core.TccDamageSources;
import com.xlxyvergil.tcc.event.TccAttributeEvents;
import com.xlxyvergil.tcc.util.AmmoRegenHelper;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.items.BoundCurioItem;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import com.xlxyvergil.tcc.util.GunTypeChecker;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.SlotContext;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class QishiZhijian extends BoundCurioItem {
    private static final UUID HEAT_MAX_UUID = UUID.fromString("b2c3d4e5-f6a7-5b6c-9d0e-1f2a3b4c5d11");
    private static final UUID HEAT_COOLING_UUID = UUID.fromString("b2c3d4e5-f6a7-5b6c-9d0e-1f2a3b4c5d12");

    public QishiZhijian(Properties properties) {
        super(properties);
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        if (matchesRestriction(livingEntity)) {
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.HEAT_MAX,
                TaczCuriosConfig.COMMON.qishiZhijianHeatMax.get(), HEAT_MAX_UUID,
                "tcc.qishi_zhijian.heat_max", AttributeModifier.Operation.MULTIPLY_BASE);
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.HEAT_COOLING,
                TaczCuriosConfig.COMMON.qishiZhijianHeatCooling.get(), HEAT_COOLING_UUID,
                "tcc.qishi_zhijian.heat_cooling", AttributeModifier.Operation.MULTIPLY_BASE);
        } else {
            AttributeHelper.removeModifier(livingEntity, AttributeHelper.HEAT_MAX, HEAT_MAX_UUID);
            AttributeHelper.removeModifier(livingEntity, AttributeHelper.HEAT_COOLING, HEAT_COOLING_UUID);
        }
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.HEAT_MAX, HEAT_MAX_UUID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.HEAT_COOLING, HEAT_COOLING_UUID);
    }

    @Override
    protected boolean isBoundItem() {
        return true;
    }

    public static boolean isEquipped(LivingEntity entity) {
        return !CurioSearchHelper.findFirstEquippedStack(entity,
            stack -> stack.getItem() instanceof QishiZhijian).isEmpty();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onGunHurtPost(EntityHurtByGunEvent.Post event) {
        LivingEntity attacker = event.getAttacker();
        if (attacker == null || !isEquipped(attacker)) return;
        if (!GunTypeChecker.isHoldingHeavyWeapon(attacker)) return;
        if (!(attacker.level() instanceof ServerLevel)) return;

        Entity hurtEntity = event.getHurtEntity();
        if (!(hurtEntity instanceof LivingEntity targetLiving)) return;
        if (targetLiving.isDeadOrDying()) return;

        TccAttributeEvents.applyImaginaryDamage(
            targetLiving,
            TccDamageSources.imaginaryDamage(targetLiving.level(), attacker),
            TaczCuriosConfig.COMMON.qishiZhijianImaginaryDamage.get().floatValue()
        );
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity entity = slotContext.entity();
        if (entity.level().isClientSide()) return;

        if (entity.tickCount % 20 != 0) return;
        if (!GunTypeChecker.isHoldingHeavyWeapon(entity)) return;

        ItemStack held = entity.getMainHandItem();
        IGun iGun = IGun.getIGunOrNull(held);
        if (iGun == null) return;

        AmmoRegenHelper.regenAmmo(entity, held, iGun,
            (double) TaczCuriosConfig.COMMON.qishiZhijianAmmoRegenPercent.get());
    }

    @Override
    public List<String> getWeaponTypeRestriction() {
        return List.of("rpg", "mg");
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        tooltip.add(Component.literal(""));

        double imaginaryDamage = TaczCuriosConfig.COMMON.qishiZhijianImaginaryDamage.get();
        double ammoRegen = TaczCuriosConfig.COMMON.qishiZhijianAmmoRegenPercent.get() * 100;

        tooltip.add(Component.translatable("item.tcc.qishi_zhijian.effect",
                String.format("%.2f", imaginaryDamage),
                String.format("%.0f", ammoRegen))
            .withStyle(ChatFormatting.WHITE));

        tooltip.add(Component.literal(""));
        appendBoundPlayer(stack, tooltip);
    }
}
