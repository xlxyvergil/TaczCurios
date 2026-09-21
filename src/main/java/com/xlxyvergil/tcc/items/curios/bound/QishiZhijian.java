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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;
import top.theillusivec4.curios.api.SlotContext;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.resources.ResourceLocation;
@EventBusSubscriber(modid = TaczCurios.MODID)
public class QishiZhijian extends BoundCurioItem {
    private static final ResourceLocation HEAT_MAX_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "qishi_zhijian_b2c3d4e5_5d11");
    private static final ResourceLocation HEAT_COOLING_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "qishi_zhijian_b2c3d4e5_5d12");

    public QishiZhijian(Properties properties) {
        super(properties);
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        // 登记该饰品施加的修饰符 ID → 来源饰品，供客户端属性面板显示来源图标。
        AttributeHelper.registerSourceItem(HEAT_MAX_ID, stack.getItem());
        AttributeHelper.registerSourceItem(HEAT_COOLING_ID, stack.getItem());
        if (matchesRestriction(livingEntity)) {
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.HEAT_MAX,
                TaczCuriosConfig.COMMON.qishiZhijianHeatMax.get(), HEAT_MAX_ID, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.HEAT_COOLING,
                TaczCuriosConfig.COMMON.qishiZhijianHeatCooling.get(), HEAT_COOLING_ID, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        } else {
            AttributeHelper.removeModifier(livingEntity, AttributeHelper.HEAT_MAX, HEAT_MAX_ID);
            AttributeHelper.removeModifier(livingEntity, AttributeHelper.HEAT_COOLING, HEAT_COOLING_ID);
        }
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.HEAT_MAX, HEAT_MAX_ID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.HEAT_COOLING, HEAT_COOLING_ID);
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
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        Level level = context.level();
        super.appendHoverText(stack, context, tooltip, flag);

        tooltip.add(Component.literal(""));

        double imaginaryDamage = TaczCuriosConfig.COMMON.qishiZhijianImaginaryDamage.get();
        double ammoRegen = TaczCuriosConfig.COMMON.qishiZhijianAmmoRegenPercent.get() * 100;

        tooltip.add(Component.translatable("item.tcc.qishi_zhijian.effect",
                String.format("%.2f", imaginaryDamage),
                String.format("%.0f", ammoRegen))
            .withStyle(ChatFormatting.WHITE));

        tooltip.add(formatModifierTooltip(
                TaczCuriosConfig.COMMON.qishiZhijianHeatMax.get() * 100, "%.0f%%",
                Component.translatable(AttributeHelper.HEAT_MAX.value().getDescriptionId()))
            .withStyle(ChatFormatting.WHITE));
        tooltip.add(formatModifierTooltip(
                TaczCuriosConfig.COMMON.qishiZhijianHeatCooling.get() * 100, "%.0f%%",
                Component.translatable(AttributeHelper.HEAT_COOLING.value().getDescriptionId()))
            .withStyle(ChatFormatting.WHITE));

        tooltip.add(Component.literal(""));
        appendBoundPlayer(stack, tooltip);
    }
}
