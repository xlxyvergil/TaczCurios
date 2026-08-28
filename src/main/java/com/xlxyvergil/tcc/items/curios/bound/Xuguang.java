package com.xlxyvergil.tcc.items.curios.bound;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.items.BoundCurioItem;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
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
 * 旭光系列·人物线（tcc_3rd）：旭光。
 * <p>
 * 攻速 +25%、攻伤 +20%、暴击伤害 +30%。
 */
public class Xuguang extends BoundCurioItem {

    private static final UUID ATTACK_SPEED_UUID = UUID.fromString("161e973d-170a-4654-83d9-c258b7a368d7");
    private static final UUID ATTACK_DAMAGE_UUID = UUID.fromString("ca6fabe6-07dd-4cd6-92fe-28c49e9b01b5");
    private static final UUID CRIT_CHANCE_UUID = UUID.fromString("559c865d-5738-4b33-8d89-eae39b75e4a8");
    private static final UUID CRIT_DAMAGE_UUID = UUID.fromString("71aa144b-16ee-45da-a2fb-f9438400f2e0");

    private static double attackSpeedPct() {
        return TaczCuriosConfig.COMMON.xuguangAttackSpeedPercent.get();
    }

    private static double attackDamagePct() {
        return TaczCuriosConfig.COMMON.xuguangAttackDamagePercent.get();
    }

    private static final double CRIT_CHANCE = 0;

    private static double critDamage() {
        return TaczCuriosConfig.COMMON.xuguangCritDamagePercent.get();
    }

    public Xuguang(Properties properties) {
        super(properties);
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
                stack -> stack.getItem() instanceof Xuguang).isEmpty();
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
            if (critDamage() > 0) {
                AttributeHelper.applyModifier(livingEntity, AttributeHelper.CRIT_DAMAGE,
                        critDamage(), CRIT_DAMAGE_UUID,
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
        if (critDamage() > 0) {
            tooltip.add(formatModifierTooltip(critDamage() * 100, "%.0f%%", Component.translatable(AttributeHelper.CRIT_DAMAGE.getDescriptionId()))
                    .withStyle(ChatFormatting.GOLD));
        }
        appendBoundPlayer(stack, tooltip);
    }
}
