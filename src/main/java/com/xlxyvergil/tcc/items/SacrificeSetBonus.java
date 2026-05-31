package com.xlxyvergil.tcc.items;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.registries.TaczItems;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.util.BaseCurioItem;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.CuriosApi;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * 牺牲套装组合 - 近战饰品
 * 同时装备牺牲压迫点和牺牲斩铁时，两者数值各提升25%
 */
public class SacrificeSetBonus extends BaseCurioItem {

    private static final UUID BONUS_MELEE_DAMAGE_UUID = UUID.fromString("d1e2f3a4-5006-4000-8000-000000000001");
    private static final UUID BONUS_CRIT_CHANCE_UUID = UUID.fromString("d1e2f3a4-5006-4000-8000-000000000002");

    private static final String BONUS_MELEE_DAMAGE_NAME = "tcc.sacrifice_set_bonus.melee_damage";
    private static final String BONUS_CRIT_CHANCE_NAME = "tcc.sacrifice_set_bonus.crit_chance";

    public SacrificeSetBonus(Properties properties) {
        super(properties);
    }

    private static boolean isHoldingMeleeWeapon(LivingEntity entity) {
        return !entity.getMainHandItem().getAttributeModifiers(EquipmentSlot.MAINHAND)
            .get(Attributes.ATTACK_DAMAGE).isEmpty();
    }

    private boolean hasSetPieces(LivingEntity entity) {
        return CuriosApi.getCuriosInventory(entity).resolve()
            .map(inv -> inv.findFirstCurio(TaczItems.SACRIFICE_OPPRESSION.get()).isPresent()
                     && inv.findFirstCurio(TaczItems.SACRIFICE_STEEL.get()).isPresent())
            .orElse(false);
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity) {
        if (isHoldingMeleeWeapon(livingEntity) && hasSetPieces(livingEntity)) {
            double setBonus = TaczCuriosConfig.COMMON.sacrificeSetBonus.get();
            // 额外加成：原值 * (setBonus - 1.0)
            double bonusMelee = TaczCuriosConfig.COMMON.sacrificeOppressionMeleeDamage.get() * (setBonus - 1.0);
            double bonusCrit = TaczCuriosConfig.COMMON.sacrificeSteelCritChance.get() * (setBonus - 1.0);

            AttributeHelper.applyModifier(livingEntity, AttributeHelper.MELEE_DAMAGE, bonusMelee,
                BONUS_MELEE_DAMAGE_UUID, BONUS_MELEE_DAMAGE_NAME, AttributeModifier.Operation.MULTIPLY_BASE);
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.CRIT_CHANCE, bonusCrit,
                BONUS_CRIT_CHANCE_UUID, BONUS_CRIT_CHANCE_NAME, AttributeModifier.Operation.MULTIPLY_BASE);
        } else {
            AttributeHelper.removeModifier(livingEntity, AttributeHelper.MELEE_DAMAGE, BONUS_MELEE_DAMAGE_UUID);
            AttributeHelper.removeModifier(livingEntity, AttributeHelper.CRIT_CHANCE, BONUS_CRIT_CHANCE_UUID);
        }
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.MELEE_DAMAGE, BONUS_MELEE_DAMAGE_UUID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.CRIT_CHANCE, BONUS_CRIT_CHANCE_UUID);
    }

    @Override
    public void curioTick(top.theillusivec4.curios.api.SlotContext slotContext, ItemStack stack) {
        applyEffects(slotContext.entity());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.literal(""));
        double bonusPct = (TaczCuriosConfig.COMMON.sacrificeSetBonus.get() - 1.0) * 100;
        tooltip.add(Component.translatable("item.tcc.sacrifice_set_bonus.effect",
                String.format("%+.0f", bonusPct))
            .withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable("tcc.tooltip.slot"));
        tooltip.add(Component.translatable("tcc.tooltip.rarity.epic"));
    }

    @Override
    public void applyGunSwitchEffect(LivingEntity livingEntity) {
        applyEffects(livingEntity);
    }
}
