package com.xlxyvergil.tcc.items.curios.bound;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.attribute.TccAttributes;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.helpers.ImaginaryResistanceHelper;
import com.xlxyvergil.tcc.util.ItemNbtHelper;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.items.BoundCurioItem;
import com.xlxyvergil.tcc.util.ItemNbtHelper;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import com.xlxyvergil.tcc.util.ItemNbtHelper;
import com.xlxyvergil.tcc.util.GunTypeChecker;
import net.minecraft.ChatFormatting;
import com.xlxyvergil.tcc.client.TaczCuriosClientTooltip;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;
import top.theillusivec4.curios.api.SlotContext;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.resources.ResourceLocation;
@EventBusSubscriber(modid = TaczCurios.MODID)
public class Fanxing extends BoundCurioItem {
    private static final String COOLDOWN_KEY = TaczCurios.MODID + ":fanxing_hurt_cooldown";
    private static final ResourceLocation IMAGINARY_RESISTANCE_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "fanxing_e1f2a3b4_7801");
    private static final ResourceLocation LUCK_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "fanxing_d4e5f6a7_9004");

    public Fanxing(Properties properties) {
        super(properties);
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        // 登记该饰品施加的修饰符 ID → 来源饰品，供客户端属性面板显示来源图标。
        AttributeHelper.registerSourceItem(IMAGINARY_RESISTANCE_ID, stack.getItem());
        AttributeHelper.registerSourceItem(LUCK_ID, stack.getItem());
        // 虚数抗性不受武器类型限制，装备即生效。
        ItemStack equipped = findEquippedStack(livingEntity);
        CompoundTag tag = ItemNbtHelper.getTag(equipped);
        double resistance = 1.0
                + ImaginaryResistanceHelper.getExtraResistanceFromProgress(tag);
        AttributeHelper.applyModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE,
            resistance, IMAGINARY_RESISTANCE_ID, AttributeModifier.Operation.ADD_VALUE);

        if (matchesRestriction(livingEntity)) {
            double totalResistance = livingEntity.getAttributeValue(TccAttributes.IMAGINARY_DAMAGE_RESISTANCE);
            int luckFromResistance = (int) ((int) Math.round(totalResistance * TaczCuriosConfig.COMMON.fanxingLuckPerResistance.get() * 10000.0) / 10000.0);
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.LUCK,
                luckFromResistance, LUCK_ID, AttributeModifier.Operation.ADD_VALUE);
        }
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE, IMAGINARY_RESISTANCE_ID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.LUCK, LUCK_ID);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity entity = slotContext.entity();
        if (entity != null) {
            applyEffects(entity, stack);
        }
    }

    @Override
    protected boolean isBoundItem() {
        return true;
    }

    public static boolean isEquipped(LivingEntity entity) {
        return !CurioSearchHelper.findFirstEquippedStack(entity,
            stack -> stack.getItem() instanceof Fanxing).isEmpty();
    }

    private static ItemStack findEquippedStack(LivingEntity livingEntity) {
        return CurioSearchHelper.findFirstEquippedStack(livingEntity,
            stack -> stack.getItem() instanceof Fanxing);
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingIncomingDamageEvent event) {
        LivingEntity entity = event.getEntity();
        if (!isEquipped(entity)) return;
        if (!GunTypeChecker.isHoldingAnyGun(entity)) return;

        int cooldown = entity.getPersistentData().getInt(COOLDOWN_KEY);
        if (cooldown > 0) {
            event.setCanceled(true);
        } else {
            int luck = (int) entity.getAttributeValue(AttributeHelper.LUCK);
            int cooldownTicks = TaczCuriosConfig.COMMON.fanxingBaseCooldown.get()
                + (luck / 2) * TaczCuriosConfig.COMMON.fanxingLuckPerTick.get();
            int maxCooldown = TaczCuriosConfig.COMMON.fanxingMaxCooldown.get();
            if (cooldownTicks > maxCooldown) cooldownTicks = maxCooldown;
            entity.getPersistentData().putInt(COOLDOWN_KEY, cooldownTicks);
        }
    }

    @SubscribeEvent
    public static void onLivingTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof LivingEntity entity)) return;
        if (!entity.isAlive()) return;
        int cooldown = entity.getPersistentData().getInt(COOLDOWN_KEY);
        if (cooldown > 0) {
            entity.getPersistentData().putInt(COOLDOWN_KEY, cooldown - 1);
        }
    }

    @Override
    public List<String> getWeaponTypeRestriction() {
        return List.of("smg");
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        Level level = context.level();
        super.appendHoverText(stack, context, tooltip, flag);

        appendImaginaryResistance(stack, tooltip);

        tooltip.add(Component.literal(""));

        double resistance = 0;
        int computedLuck = 0;
        int computedCooldown = TaczCuriosConfig.COMMON.fanxingBaseCooldown.get();
        if (level != null && level.isClientSide()) {
            LivingEntity wearer = TaczCuriosClientTooltip.resolveWearer(stack);
            if (wearer != null) {
                resistance = wearer.getAttributeValue(TccAttributes.IMAGINARY_DAMAGE_RESISTANCE);
                int luck = (int) wearer.getAttributeValue(AttributeHelper.LUCK);
                computedLuck = (int)(resistance * TaczCuriosConfig.COMMON.fanxingLuckPerResistance.get());
                computedCooldown = TaczCuriosConfig.COMMON.fanxingBaseCooldown.get()
                    + (luck / 2) * TaczCuriosConfig.COMMON.fanxingLuckPerTick.get();
                int max = TaczCuriosConfig.COMMON.fanxingMaxCooldown.get();
                if (computedCooldown > max) computedCooldown = max;
            }
        }


        tooltip.add(formatModifierTooltip(computedLuck, "%.0f", Component.translatable(AttributeHelper.LUCK.value().getDescriptionId()))
                .withStyle(ChatFormatting.GOLD));

        tooltip.add(Component.translatable("tcc.tooltip.affected_by_luck")
            .withStyle(ChatFormatting.LIGHT_PURPLE));
        tooltip.add(Component.translatable("item.tcc.fanxing.special_cooldown",
                computedCooldown)
            .withStyle(ChatFormatting.RED));
        tooltip.add(Component.literal(""));
        appendBoundPlayer(stack, tooltip);
    }
}
