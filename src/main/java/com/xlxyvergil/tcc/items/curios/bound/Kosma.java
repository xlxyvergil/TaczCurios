package com.xlxyvergil.tcc.items.curios.bound;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.items.BoundCurioItem;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import net.minecraft.ChatFormatting;
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

/**
 * 旭光系列·人物线（tcc_3rd）：科斯魔。
 * <p>
 * 攻速 +8%、攻伤 +5%。
 */
public class Kosma extends BoundCurioItem {

    private static final UUID ATTACK_SPEED_UUID = UUID.fromString("e46b57b8-e3ca-436c-bc30-b2015ce7666e");
    private static final UUID ATTACK_DAMAGE_UUID = UUID.fromString("9bd59d29-ab0c-4c33-bef6-dd78a678b938");
    private static final UUID CRIT_CHANCE_UUID = UUID.fromString("783349b7-930b-441d-bb0d-63f0966806ef");
    private static final UUID CRIT_DAMAGE_UUID = UUID.fromString("40490711-89e7-4007-a337-622aaf9769f4");

    private static double attackSpeedPct() {
        return TaczCuriosConfig.COMMON.kosmaAttackSpeedPercent.get();
    }

    private static double attackDamagePct() {
        return TaczCuriosConfig.COMMON.kosmaAttackDamagePercent.get();
    }

    private static final double CRIT_CHANCE = 0;
    private static final double CRIT_DAMAGE = 0;

    public Kosma(Properties properties) {
        super(properties);
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
    public List<String> getWeaponTypeRestriction() {
        return List.of("melee");
    }

    public static boolean isEquipped(LivingEntity entity) {
        return !CurioSearchHelper.findFirstEquippedStack(entity,
                stack -> stack.getItem() instanceof Kosma).isEmpty();
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        if (matchesRestriction(livingEntity)) {
            AttributeHelper.applyModifier(livingEntity, Attributes.ATTACK_SPEED,
                    attackSpeedPct(), ATTACK_SPEED_UUID,
                    "tcc.dawn.attack_speed", AttributeModifier.Operation.MULTIPLY_BASE);
            AttributeHelper.applyModifier(livingEntity, Attributes.ATTACK_DAMAGE,
                    attackDamagePct(), ATTACK_DAMAGE_UUID,
                    "tcc.dawn.attack_damage", AttributeModifier.Operation.MULTIPLY_BASE);
            if (CRIT_CHANCE > 0) {
                AttributeHelper.applyModifier(livingEntity, AttributeHelper.CRIT_CHANCE,
                        CRIT_CHANCE, CRIT_CHANCE_UUID,
                        "tcc.dawn.crit_chance", AttributeModifier.Operation.ADDITION);
            }
            if (CRIT_DAMAGE > 0) {
                AttributeHelper.applyModifier(livingEntity, AttributeHelper.CRIT_DAMAGE,
                        CRIT_DAMAGE, CRIT_DAMAGE_UUID,
                        "tcc.dawn.crit_damage", AttributeModifier.Operation.ADDITION);
            }
        } else {
            removeEffects(livingEntity);
        }
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, Attributes.ATTACK_SPEED, ATTACK_SPEED_UUID);
        AttributeHelper.removeModifier(livingEntity, Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE_UUID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.CRIT_CHANCE, CRIT_CHANCE_UUID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.CRIT_DAMAGE, CRIT_DAMAGE_UUID);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        applyEffects(slotContext.entity(), stack);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        appendImaginaryResistance(stack, tooltip);
        tooltip.add(formatModifierTooltip(attackSpeedPct() * 100, "%.0f%%", Component.translatable(Attributes.ATTACK_SPEED.getDescriptionId()))
                .withStyle(ChatFormatting.GOLD));
        tooltip.add(formatModifierTooltip(attackDamagePct() * 100, "%.0f%%", Component.translatable(AttributeHelper.ATTACK_DAMAGE.getDescriptionId()))
                .withStyle(ChatFormatting.GOLD));
        if (CRIT_CHANCE > 0) {
            tooltip.add(formatModifierTooltip(CRIT_CHANCE * 100, "%.0f%%", Component.translatable(AttributeHelper.CRIT_CHANCE.getDescriptionId()))
                    .withStyle(ChatFormatting.GOLD));
        }
        if (CRIT_DAMAGE > 0) {
            tooltip.add(formatModifierTooltip(CRIT_DAMAGE * 100, "%.0f%%", Component.translatable(AttributeHelper.CRIT_DAMAGE.getDescriptionId()))
                    .withStyle(ChatFormatting.GOLD));
        }
        appendBoundPlayer(stack, tooltip);
    }
}
