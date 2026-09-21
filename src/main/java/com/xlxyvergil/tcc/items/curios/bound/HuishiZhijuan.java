package com.xlxyvergil.tcc.items.curios.bound;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.attribute.TccAttributes;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.helpers.ImaginaryResistanceHelper;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.items.BoundCurioItem;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
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

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.resources.ResourceLocation;
import com.xlxyvergil.tcc.util.ItemNbtHelper;
@EventBusSubscriber(modid = TaczCurios.MODID)
public class HuishiZhijuan extends BoundCurioItem {
    private static final String COOLDOWN_KEY = TaczCurios.MODID + ":huishi_hurt_cooldown";
    private static final ResourceLocation IMAGINARY_RESISTANCE_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "huishi_zhijuan_a7b8c9d0_6789");

    public HuishiZhijuan(Properties properties) {
        super(properties);
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        // 登记该饰品施加的修饰符 ID → 来源饰品，供客户端属性面板显示来源图标。
        AttributeHelper.registerSourceItem(IMAGINARY_RESISTANCE_ID, stack.getItem());
        ItemStack equipped = findEquippedStack(livingEntity);
        CompoundTag tag = ItemNbtHelper.getTag(equipped);
        double total = 1.0
                + ImaginaryResistanceHelper.getExtraResistanceFromProgress(tag);
        AttributeHelper.applyModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE,
            total, IMAGINARY_RESISTANCE_ID, AttributeModifier.Operation.ADD_VALUE);
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE, IMAGINARY_RESISTANCE_ID);
    }

    @Override
    protected boolean isBoundItem() {
        return true;
    }

    public static boolean isEquipped(LivingEntity entity) {
        return !CurioSearchHelper.findFirstEquippedStack(entity,
            stack -> stack.getItem() instanceof HuishiZhijuan).isEmpty();
    }

    private static ItemStack findEquippedStack(LivingEntity livingEntity) {
        return CurioSearchHelper.findFirstEquippedStack(livingEntity,
            stack -> stack.getItem() instanceof HuishiZhijuan);
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
            int cooldownTicks = TaczCuriosConfig.COMMON.huishiZhijuanBaseCooldown.get()
                + (luck / 2) * TaczCuriosConfig.COMMON.huishiZhijuanLuckPerTick.get();
            int maxCooldown = TaczCuriosConfig.COMMON.huishiZhijuanMaxCooldown.get();
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

    public static int getCooldownTicks(int luck) {
        int cooldown = TaczCuriosConfig.COMMON.huishiZhijuanBaseCooldown.get()
            + (luck / 2) * TaczCuriosConfig.COMMON.huishiZhijuanLuckPerTick.get();
        int max = TaczCuriosConfig.COMMON.huishiZhijuanMaxCooldown.get();
        return Math.min(cooldown, max);
    }

    @Override
    public List<String> getWeaponTypeRestriction() {
        return List.of("smg");
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        Level level = context.level();
        super.appendHoverText(stack, context, tooltip, flag);

        appendImaginaryResistance(stack, tooltip);

        int computedCooldown = TaczCuriosConfig.COMMON.huishiZhijuanBaseCooldown.get();
        if (level != null && level.isClientSide()) {
            LivingEntity wearer = TaczCuriosClientTooltip.resolveWearer(stack);
            if (wearer != null) {
                int luck = (int) wearer.getAttributeValue(AttributeHelper.LUCK);
                computedCooldown = getCooldownTicks(luck);
            }
        }
        tooltip.add(Component.translatable("item.tcc.huishi_zhijuan.effect",
                computedCooldown)
            .withStyle(ChatFormatting.WHITE));

        tooltip.add(Component.translatable("tcc.tooltip.affected_by_luck")
            .withStyle(ChatFormatting.LIGHT_PURPLE));

        tooltip.add(Component.literal(""));
        appendBoundPlayer(stack, tooltip);
    }
}
