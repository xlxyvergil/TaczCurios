package com.xlxyvergil.tcc.items.curios;

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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class YongjieZhijian extends BaseCurioItem {

    private static final UUID CRIT_CHANCE_UUID = UUID.fromString("a81d5c7e-9b3f-4e62-b8d5-3c7a1f6e8d2b");
    private static final UUID CRIT_DAMAGE_UUID = UUID.fromString("c92e6f8d-1a4b-5f73-c9e6-4d8b2f7a0e3c");
    private static final UUID LUCK_UUID = UUID.fromString("b2c3d4e5-f6a7-8901-bcde-f12345678902");

    public YongjieZhijian(Properties properties) {
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
        AttributeHelper.applyModifier(slotContext.entity(), AttributeHelper.LUCK,
            TaczCuriosConfig.COMMON.yongjieZhijianLuck.get(), LUCK_UUID,
            "tcc.yongjie_zhijian.luck", AttributeModifier.Operation.ADDITION);
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity) {
        if (GunTypeChecker.isHoldingAnyGun(livingEntity)) {
            int luck = (int) livingEntity.getAttributeValue(AttributeHelper.LUCK);
            double critChance = Math.round(luck * TaczCuriosConfig.COMMON.yongjieZhijianCritChancePerLuck.get() * 100.0) / 100.0;
            double critDamage = Math.round(luck * TaczCuriosConfig.COMMON.yongjieZhijianCritDamagePerLuck.get() * 100.0) / 100.0;

            AttributeHelper.applyModifier(livingEntity, AttributeHelper.CRIT_CHANCE,
                critChance, CRIT_CHANCE_UUID,
                "tcc.yongjie_zhijian.crit_chance", AttributeModifier.Operation.ADDITION);
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.CRIT_DAMAGE,
                critDamage, CRIT_DAMAGE_UUID,
                "tcc.yongjie_zhijian.crit_damage", AttributeModifier.Operation.ADDITION);
        } else {
            removeEffects(livingEntity);
        }
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.CRIT_CHANCE, CRIT_CHANCE_UUID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.CRIT_DAMAGE, CRIT_DAMAGE_UUID);
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        super.onUnequip(slotContext, newStack, stack);
        AttributeHelper.removeModifier(slotContext.entity(), AttributeHelper.LUCK, LUCK_UUID);
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
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        applyEffects(slotContext.entity());
    }

    @Override
    public void applyGunSwitchEffect(LivingEntity livingEntity) {
        applyEffects(livingEntity);
    }

    @Override
    public DropRule getDropRule(SlotContext slotContext, DamageSource source, int lootingLevel, boolean recentlyHit, ItemStack stack) {
        return DropRule.ALWAYS_KEEP;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        tooltip.add(Component.literal(""));

        String gunTypes = GunTypeChecker.formatGunTypes(List.of("pistol", "rifle", "shotgun", "sniper", "smg", "mg", "rpg"));
        tooltip.add(Component.translatable("tcc.tooltip.restricted_gun_types", gunTypes));

        int luck = TaczCuriosConfig.COMMON.yongjieZhijianLuck.get();
        double critChance = luck * TaczCuriosConfig.COMMON.yongjieZhijianCritChancePerLuck.get() * 100;
        double critDamage = luck * TaczCuriosConfig.COMMON.yongjieZhijianCritDamagePerLuck.get() * 100;
        String yzjCritChanceStr = String.format("%.0f", critChance);
        String yzjCritDamageStr = String.format("%.0f", critDamage);
        tooltip.add(formatModifierTooltip(luck, "%.0f", Component.translatable(AttributeHelper.LUCK.getDescriptionId()))
                .withStyle(ChatFormatting.WHITE));
        tooltip.add(formatModifierTooltip(critChance, "%.0f%%", Component.translatable(AttributeHelper.CRIT_CHANCE.getDescriptionId()))
                .withStyle(ChatFormatting.WHITE));
        tooltip.add(formatModifierTooltip(critDamage, "%.0f%%", Component.translatable(AttributeHelper.CRIT_DAMAGE.getDescriptionId()))
                .withStyle(ChatFormatting.WHITE));

        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable("tcc.tooltip.rarity.epic"));

        CompoundTag tag = stack.getTag();
        if (tag != null && tag.getBoolean("IsBound")) {
            String boundPlayerName = tag.getString("BoundPlayerName");
            tooltip.add(Component.literal(""));
            tooltip.add(Component.translatable("tcc.tooltip.bound", boundPlayerName)
                .withStyle(ChatFormatting.RED));
        }
    }
}
