package com.xlxyvergil.tcc.items.curios;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.attribute.TccAttributes;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.util.BaseCurioItem;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import com.xlxyvergil.tcc.util.GunTypeChecker;
import com.xlxyvergil.tcc.util.LuckHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio.DropRule;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class Fanxing extends BaseCurioItem {

    private static final String COOLDOWN_KEY = TaczCurios.MODID + ":fanxing_hurt_cooldown";
    private static final UUID IMAGINARY_RESISTANCE_UUID = UUID.fromString("e1f2a3b4-c5d6-7890-abcd-ef1234567801");

    public Fanxing(Properties properties) {
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
        if (GunTypeChecker.isHoldingAnyGun(livingEntity)) {
            double resistance = TaczCuriosConfig.COMMON.fanxingImaginaryResistance.get();
            AttributeHelper.applyModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get(),
                resistance, IMAGINARY_RESISTANCE_UUID,
                "tcc.fanxing.imaginary_resistance", AttributeModifier.Operation.ADDITION);

            // 每点虚数抗性提供幸运值（基于 total 虚数抗性）
            double totalResistance = livingEntity.getAttributeValue(TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get());
            int luckFromResistance = (int) (totalResistance * TaczCuriosConfig.COMMON.fanxingLuckPerResistance.get());
            LuckHelper.setFanxingLuck(livingEntity, luckFromResistance);
        } else {
            removeEffects(livingEntity);
        }
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get(), IMAGINARY_RESISTANCE_UUID);
        LuckHelper.clearFanxingLuck(livingEntity);
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
            stack -> stack.getItem() instanceof Fanxing).isEmpty();
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof Player player)) return;
        if (!isEquipped(player)) return;
        if (!GunTypeChecker.isHoldingAnyGun(player)) return;

        int cooldown = player.getPersistentData().getInt(COOLDOWN_KEY);
        if (cooldown > 0) {
            event.setCanceled(true);
        } else {
            int luck = LuckHelper.getLuck(player);
            int cooldownTicks = TaczCuriosConfig.COMMON.fanxingBaseCooldown.get()
                + (luck / 2) * TaczCuriosConfig.COMMON.fanxingLuckPerTick.get();
            int maxCooldown = TaczCuriosConfig.COMMON.fanxingMaxCooldown.get();
            if (cooldownTicks > maxCooldown) cooldownTicks = maxCooldown;
            player.getPersistentData().putInt(COOLDOWN_KEY, cooldownTicks);
        }
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (!entity.isAlive()) return;
        int cooldown = entity.getPersistentData().getInt(COOLDOWN_KEY);
        if (cooldown > 0) {
            entity.getPersistentData().putInt(COOLDOWN_KEY, cooldown - 1);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        tooltip.add(Component.literal(""));

        String gunTypes = GunTypeChecker.formatGunTypes(List.of("pistol", "rifle", "shotgun", "sniper", "smg", "mg", "rpg"));
        tooltip.add(Component.translatable("tcc.tooltip.restricted_gun_types", gunTypes));

        tooltip.add(Component.translatable("item.tcc.fanxing.effect",
                TaczCuriosConfig.COMMON.fanxingBaseCooldown.get(),
                TaczCuriosConfig.COMMON.fanxingMaxCooldown.get(),
                TaczCuriosConfig.COMMON.fanxingImaginaryResistance.get(),
                TaczCuriosConfig.COMMON.fanxingLuckPerResistance.get())
            .withStyle(ChatFormatting.RED));

        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable("tcc.tooltip.rarity.rift"));

        CompoundTag tag = stack.getTag();
        if (tag != null && tag.getBoolean("IsBound")) {
            String boundPlayerName = tag.getString("BoundPlayerName");
            tooltip.add(Component.literal(""));
            tooltip.add(Component.translatable("item.tcc.fanxing.bound", boundPlayerName)
                .withStyle(ChatFormatting.RED));
        }
    }
}
