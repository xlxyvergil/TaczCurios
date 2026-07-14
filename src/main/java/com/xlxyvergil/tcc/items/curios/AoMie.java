package com.xlxyvergil.tcc.items.curios;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.attribute.TccAttributes;
import com.xlxyvergil.tcc.capability.CurioAdaptationCapability;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.helpers.ImaginaryResistanceHelper;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio.DropRule;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class AoMie extends BaseCurioItem {

    private static final UUID IMAGINARY_RESISTANCE_UUID = UUID.fromString("d4e5f6a7-b8c9-0123-def4-567890abcdef");
    private static final UUID MAX_HEALTH_UUID = UUID.fromString("e5f6a7b8-c9d0-1234-ef56-7890abcdef01");
    private static final String ADAPT_ID = "aomie";
    private static final String ADAPT_REGISTERED_KEY = TaczCurios.MODID + ":aomie_adapt_registered";

    public AoMie(Properties properties) {
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
        ItemStack equipped = CurioSearchHelper.findFirstEquippedStack(livingEntity,
                stack -> stack.getItem() instanceof AoMie);
        CompoundTag tag = equipped.getTag();
        double total = TaczCuriosConfig.COMMON.kalpasImaginaryResistance.get()
                + ImaginaryResistanceHelper.getExtraResistanceFromProgress(tag);
        AttributeHelper.applyModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get(),
            total, IMAGINARY_RESISTANCE_UUID,
            "tcc.aomie.imaginary_resistance", AttributeModifier.Operation.ADDITION);

        double totalResistance = livingEntity.getAttributeValue(TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get());
        double healthBoost = Math.round(totalResistance * TaczCuriosConfig.COMMON.aoMieHealthPerResistance.get() * 100.0) / 100.0;
        AttributeHelper.applyModifier(livingEntity, Attributes.MAX_HEALTH,
            healthBoost, MAX_HEALTH_UUID,
            "tcc.aomie.max_health", AttributeModifier.Operation.ADDITION);

        if (GunTypeChecker.isHoldingMeleeWeapon(livingEntity)) {
            if (!livingEntity.getPersistentData().getBoolean(ADAPT_REGISTERED_KEY)) {
                livingEntity.getCapability(CurioAdaptationCapability.CAPABILITY).ifPresent(h -> {
                    h.register(ADAPT_ID,
                        TaczCuriosConfig.COMMON.aoMieMaxSlots.get(),
                        TaczCuriosConfig.COMMON.aoMieAdaptFactor.get(),
                        TaczCuriosConfig.COMMON.aoMieDecaySeconds.get());
                });
                livingEntity.getPersistentData().putBoolean(ADAPT_REGISTERED_KEY, true);
            }
        } else {
            unregisterAdaptation(livingEntity);
        }
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get(), IMAGINARY_RESISTANCE_UUID);
        AttributeHelper.removeModifier(livingEntity, Attributes.MAX_HEALTH, MAX_HEALTH_UUID);
        unregisterAdaptation(livingEntity);
    }

    @Override
    public void applyGunSwitchEffect(LivingEntity livingEntity) {
        applyEffects(livingEntity);
    }

    private void unregisterAdaptation(LivingEntity livingEntity) {
        if (livingEntity.getPersistentData().getBoolean(ADAPT_REGISTERED_KEY)) {
            livingEntity.getCapability(CurioAdaptationCapability.CAPABILITY).ifPresent(h -> {
                h.unregister(ADAPT_ID);
            });
            livingEntity.getPersistentData().putBoolean(ADAPT_REGISTERED_KEY, false);
        }
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

    public static boolean isEquipped(LivingEntity entity) {
        return !CurioSearchHelper.findFirstEquippedStack(entity,
            stack -> stack.getItem() instanceof AoMie).isEmpty();
    }

    @Override
    public DropRule getDropRule(SlotContext slotContext, DamageSource source, int lootingLevel, boolean recentlyHit, ItemStack stack) {
        return DropRule.ALWAYS_KEEP;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        CompoundTag tag = stack.getTag();

        // 虚数抗性显示
        double baseValue = TaczCuriosConfig.COMMON.kalpasImaginaryResistance.get();
        double total = baseValue + ImaginaryResistanceHelper.getExtraResistanceFromProgress(tag);
        if (level != null && level.isClientSide()) {
            Player player = Minecraft.getInstance().player;
            if (player != null && isEquipped(player)) {
                total = player.getAttributeValue(TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get());
            }
        }
        tooltip.add(Component.literal(""));
        int maxSlots = TaczCuriosConfig.COMMON.aoMieMaxSlots.get();
        double adaptFactor = TaczCuriosConfig.COMMON.aoMieAdaptFactor.get() * 100;
        int decaySeconds = TaczCuriosConfig.COMMON.aoMieDecaySeconds.get();

        tooltip.add(Component.translatable("tcc.tooltip.imaginary_resistance", String.format("%.0f", total))
            .withStyle(ChatFormatting.GOLD));

        tooltip.add(Component.literal(""));

        tooltip.add(Component.translatable("tcc.tooltip.restricted_melee"));

        double healthFromResistance = 0;
        if (level != null && level.isClientSide()) {
            Player player = Minecraft.getInstance().player;
            if (player != null && isEquipped(player)) {
                double resistance = player.getAttributeValue(TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get());
                healthFromResistance = resistance * TaczCuriosConfig.COMMON.aoMieHealthPerResistance.get();
            }
        }
        tooltip.add(Component.translatable("attribute.modifier.plus.0",
                String.format("%.2f", healthFromResistance),
                Component.translatable(AttributeHelper.MAX_HEALTH.getDescriptionId()))
                .withStyle(ChatFormatting.RED));

        tooltip.add(Component.translatable("item.tcc.aomie.special_adapt",
                maxSlots,
                String.format("%.2f", adaptFactor),
                decaySeconds)
            .withStyle(ChatFormatting.RED));

        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable("tcc.tooltip.rarity.rift"));

        if (tag != null && tag.getBoolean("IsBound")) {
            String boundPlayerName = tag.getString("BoundPlayerName");
            tooltip.add(Component.literal(""));
            tooltip.add(Component.translatable("tcc.tooltip.bound", boundPlayerName)
                .withStyle(ChatFormatting.RED));
        }
    }
}
