package com.xlxyvergil.tcc.items.curios;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.registries.TccItems;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.util.BaseCurioItem;
import com.xlxyvergil.tcc.util.FusionUpgradeUtil;
import com.xlxyvergil.tcc.util.GunTypeChecker;
import com.xlxyvergil.tcc.util.FusionData;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.CuriosApi;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * 牺牲斩铁 - 近战饰品
 * 效果：暴击几率+220%
 * 套装：同时装备牺牲压迫点时，额外 +25%
 */
public class SacrificeSteel extends BaseCurioItem {

    private static final UUID CRIT_CHANCE_UUID = UUID.fromString("55038b53-178f-4171-96cf-ebfd873d5cc6");
    private static final UUID SET_BONUS_UUID = UUID.fromString("1aea61d9-8d99-4837-a8b1-0806085692c5");

    private static final String CRIT_CHANCE_NAME = "tcc.sacrifice_steel.crit_chance";
    private static final String SET_BONUS_NAME = "tcc.sacrifice_steel.set_bonus";

    public SacrificeSteel(Properties properties) {
        super(properties);
    }

    /**
     * 检测是否同时装备了牺牲压迫点（Curios API）
     */
    private static boolean hasSacrificeOppression(LivingEntity entity) {
        return CuriosApi.getCuriosInventory(entity).resolve()
            .map(inv -> inv.findFirstCurio(TccItems.SACRIFICE_OPPRESSION).isPresent())
            .orElse(false);
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        if (matchesRestriction(livingEntity)) {
            double critChanceBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.sacrificeSteelCritChance.get());
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.CRIT_CHANCE, critChanceBoost, CRIT_CHANCE_UUID, CRIT_CHANCE_NAME, AttributeModifier.Operation.MULTIPLY_BASE);

            // 套装效果：同时装备牺牲压迫点时，额外 +25%
            if (hasSacrificeOppression(livingEntity)) {
                double setBonus = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.sacrificeSetBonus.get());
                double bonusModifier = critChanceBoost * (setBonus - 1.0);
                AttributeHelper.applyModifier(livingEntity, AttributeHelper.CRIT_CHANCE, bonusModifier, SET_BONUS_UUID, SET_BONUS_NAME, AttributeModifier.Operation.MULTIPLY_BASE);
            } else {
                AttributeHelper.removeModifier(livingEntity, AttributeHelper.CRIT_CHANCE, SET_BONUS_UUID);
            }
        } else {
            removeEffects(livingEntity);
        }
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.CRIT_CHANCE, CRIT_CHANCE_UUID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.CRIT_CHANCE, SET_BONUS_UUID);
    }

    @Override
    public List<String> getWeaponTypeRestriction() {
        return List.of("melee");
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        tooltip.add(Component.literal(""));



        double critChanceBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.sacrificeSteelCritChance.get() ) * 100;
        tooltip.add(Component.translatable("item.tcc.sacrifice_steel.effect",
                String.format("%+.0f", critChanceBoost))
            .withStyle(ChatFormatting.WHITE));

        // 套装提示
        double setBonusPct = (FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.sacrificeSetBonus.get()) - 1.0) * 100;
        tooltip.add(Component.translatable("item.tcc.sacrifice_steel.set_bonus",
                String.format("%+.0f", setBonusPct))
            .withStyle(ChatFormatting.LIGHT_PURPLE));

        tooltip.add(Component.literal(""));
        
    }


}
