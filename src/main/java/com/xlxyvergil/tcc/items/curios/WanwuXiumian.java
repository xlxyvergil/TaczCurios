package com.xlxyvergil.tcc.items.curios;

import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.IGun;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.util.BaseCurioItem;
import com.xlxyvergil.tcc.util.GunTypeChecker;
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
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio.DropRule;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class WanwuXiumian extends BaseCurioItem {

    private static final UUID OVERHEAL_UUID = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890");

    public WanwuXiumian(Properties properties) {
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
        if (GunTypeChecker.isHoldingRifle(livingEntity)) {
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
        if (!(entity instanceof Player player)) return;
        if (player.level().isClientSide()) return;
        if (player.tickCount % 20 != 0) return;
        if (!GunTypeChecker.isHoldingRifle(player)) return;

        ItemStack held = player.getMainHandItem();
        IGun iGun = IGun.getIGunOrNull(held);
        if (iGun == null) return;

        int currentAmmo = iGun.getCurrentAmmoCount(held);
        int maxAmmo = TimelessAPI.getCommonGunIndex(iGun.getGunId(held))
            .map(index -> index.getGunData().getAmmoAmount()).orElse(0);
        if (maxAmmo <= 0 || currentAmmo >= maxAmmo) return;

        double percent = TaczCuriosConfig.COMMON.wanwuXiumianAmmoRegenPercent.get();
        int regenAmmo = (int) Math.max(1, Math.round(maxAmmo * percent));
        int newAmmo = Math.min(currentAmmo + regenAmmo, maxAmmo);
        CompoundTag tag = held.getOrCreateTag();
        tag.putInt("AmmoCount", newAmmo);
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

    @Override
    public void applyGunSwitchEffect(LivingEntity livingEntity) {
        applyEffects(livingEntity);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        tooltip.add(Component.literal(""));

        String gunTypes = GunTypeChecker.formatGunTypes(List.of("rifle"));
        tooltip.add(Component.translatable("tcc.tooltip.restricted_gun_types", gunTypes));

        tooltip.add(Component.translatable("item.tcc.wanwu_xiumian.effect",
                String.format("%.0f", TaczCuriosConfig.COMMON.wanwuXiumianOverheal.get() * 100),
                String.format("%.0f", TaczCuriosConfig.COMMON.wanwuXiumianAmmoRegenPercent.get() * 100))
            .withStyle(ChatFormatting.AQUA));

        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable("tcc.tooltip.rarity.rare"));

        CompoundTag tag = stack.getTag();
        if (tag != null && tag.getBoolean("IsBound")) {
            String boundPlayerName = tag.getString("BoundPlayerName");
            tooltip.add(Component.literal(""));
            tooltip.add(Component.translatable("tcc.tooltip.bound", boundPlayerName)
                .withStyle(ChatFormatting.RED));
        }
    }
}
