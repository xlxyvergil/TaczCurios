package com.xlxyvergil.tcc.items.curios;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.attribute.TccAttributes;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.DamageResistanceHelper;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.util.BaseCurioItem;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import com.xlxyvergil.tcc.util.GunTypeChecker;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio.DropRule;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class Tianhui extends BaseCurioItem {

    private static final UUID IMAGINARY_RESISTANCE_UUID = UUID.fromString("e5f6a7b8-c9d0-1234-ef56-7890abcdef02");
    private static final UUID MAX_HEALTH_UUID = UUID.fromString("f6a7b8c9-d0e1-2345-f678-90abcdef0123");

    public Tianhui(Properties properties) {
        super(properties);
    }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        super.onEquip(slotContext, prevStack, stack);
        if (slotContext.entity() instanceof Player player) {
            CompoundTag tag = stack.getOrCreateTag();
            if (!tag.getBoolean("IsBound")) {
                tag.putBoolean("IsBound", true);
                tag.putString("BoundPlayer", player.getStringUUID());
                tag.putString("BoundPlayerName", player.getGameProfile().getName());
            }
        }
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity) {
        AttributeHelper.applyModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get(),
            TaczCuriosConfig.COMMON.suImaginaryResistance.get(), IMAGINARY_RESISTANCE_UUID,
            "tcc.tianhui.imaginary_resistance", AttributeModifier.Operation.ADDITION);
        AttributeHelper.applyModifier(livingEntity, Attributes.MAX_HEALTH,
            TaczCuriosConfig.COMMON.tianhuiMaxHealthReduction.get(), MAX_HEALTH_UUID,
            "tcc.tianhui.max_health", AttributeModifier.Operation.MULTIPLY_BASE);
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get(), IMAGINARY_RESISTANCE_UUID);
        AttributeHelper.removeModifier(livingEntity, Attributes.MAX_HEALTH, MAX_HEALTH_UUID);
        DamageResistanceHelper.clearDamageCap(livingEntity);
    }

    @Override
    public void applyGunSwitchEffect(LivingEntity livingEntity) {
        applyEffects(livingEntity);
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

    @Override
    protected boolean isBoundItem() {
        return true;
    }

    @Override
    public DropRule getDropRule(SlotContext slotContext, DamageSource source, int lootingLevel, boolean recentlyHit, ItemStack stack) {
        return DropRule.ALWAYS_KEEP;
    }

    public static boolean isEquipped(LivingEntity entity) {
        return !CurioSearchHelper.findFirstEquippedStack(entity,
            stack -> stack.getItem() instanceof Tianhui).isEmpty();
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof Player player)) return;
        if (!isEquipped(player)) return;
        if (!GunTypeChecker.isHoldingRifle(player)) return;
        if (entity.level().isClientSide()) return;

        double totalImaginaryResistance = player.getAttributeValue(TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get());
        double baseFactor = 1.0;
        double resistanceScale = TaczCuriosConfig.COMMON.tianhuiResistanceScale.get();
        double minFactor = TaczCuriosConfig.COMMON.tianhuiMinDamageFactor.get();

        double factor = Math.round((baseFactor - (totalImaginaryResistance * resistanceScale)) * 100.0) / 100.0;
        if (factor < minFactor) {
            factor = minFactor;
        }

        float cap = event.getAmount() * (float) factor;
        DamageResistanceHelper.setDamageCap(entity, cap);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        tooltip.add(Component.literal(""));

        double resistance = TaczCuriosConfig.COMMON.suImaginaryResistance.get();
        double computedDamageLimit = 0;
        if (level != null && level.isClientSide()) {
            Player player = Minecraft.getInstance().player;
            if (player != null && isEquipped(player)) {
                double actualResistance = player.getAttributeValue(TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get());
                double factor = 1.0 - (actualResistance * TaczCuriosConfig.COMMON.tianhuiResistanceScale.get());
                double minFactor = TaczCuriosConfig.COMMON.tianhuiMinDamageFactor.get();
                computedDamageLimit = Math.max(minFactor, factor) * 100;
            }
        }
        tooltip.add(Component.translatable("item.tcc.tianhui.effect",
                (int)resistance,
                String.format("%.2f", TaczCuriosConfig.COMMON.tianhuiMaxHealthReduction.get() * 100),
                (int)computedDamageLimit,
                String.format("%.2f", TaczCuriosConfig.COMMON.tianhuiMinDamageFactor.get() * 100))
            .withStyle(ChatFormatting.GOLD));

        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable("tcc.tooltip.rarity.rift")
            .withStyle(ChatFormatting.RED));

        CompoundTag tag = stack.getTag();
        if (tag != null && tag.getBoolean("IsBound")) {
            String boundPlayerName = tag.getString("BoundPlayerName");
            tooltip.add(Component.literal(""));
            tooltip.add(Component.translatable("tcc.tooltip.bound", boundPlayerName)
                .withStyle(ChatFormatting.RED));
        }
    }
}
