package com.xlxyvergil.tcc.items.curios;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.registries.TccItems;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.util.BaseCurioItem;
import com.xlxyvergil.tcc.util.GunTypeChecker;

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
 * 牺牲压迫点 - 近战饰品
 * 效果：近战伤害 +110%
 * 套装：同时装备牺牲斩铁时，额外 +25%
 */
public class SacrificeOppression extends BaseCurioItem {

    private static final UUID MELEE_DAMAGE_UUID = UUID.fromString("a1b2c3d4-5003-4000-8000-000000000001");
    private static final UUID SET_BONUS_UUID = UUID.fromString("a1b2c3d4-5005-4000-8000-000000000001");

    private static final String MELEE_DAMAGE_NAME = "tcc.sacrifice_oppression.melee_damage";
    private static final String SET_BONUS_NAME = "tcc.sacrifice_oppression.set_bonus";

    public SacrificeOppression(Properties properties) {
        super(properties);
    }

    /**
     * 检测是否同时装备了牺牲斩铁（Curios API）
     */
    private static boolean hasSacrificeSteel(LivingEntity entity) {
        return CuriosApi.getCuriosInventory(entity).resolve()
            .map(inv -> inv.findFirstCurio(TccItems.SACRIFICE_STEEL).isPresent())
            .orElse(false);
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity) {
        double meleeDamageBoost = TaczCuriosConfig.COMMON.sacrificeOppressionMeleeDamage.get();
        AttributeHelper.applyModifier(livingEntity, AttributeHelper.MELEE_DAMAGE, meleeDamageBoost, MELEE_DAMAGE_UUID, MELEE_DAMAGE_NAME, AttributeModifier.Operation.MULTIPLY_BASE);

        // 套装效果：同时装备牺牲斩铁时，额外 +25%
        if (hasSacrificeSteel(livingEntity)) {
            double setBonus = TaczCuriosConfig.COMMON.sacrificeSetBonus.get();
            double bonusModifier = meleeDamageBoost * (setBonus - 1.0);
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.MELEE_DAMAGE, bonusModifier, SET_BONUS_UUID, SET_BONUS_NAME, AttributeModifier.Operation.MULTIPLY_BASE);
        } else {
            AttributeHelper.removeModifier(livingEntity, AttributeHelper.MELEE_DAMAGE, SET_BONUS_UUID);
        }
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.MELEE_DAMAGE, MELEE_DAMAGE_UUID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.MELEE_DAMAGE, SET_BONUS_UUID);
    }

    @Override
    public void curioTick(top.theillusivec4.curios.api.SlotContext slotContext, ItemStack stack) {
        applyEffects(slotContext.entity());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        tooltip.add(Component.literal(""));

        double meleeDamageBoost = TaczCuriosConfig.COMMON.sacrificeOppressionMeleeDamage.get() * 100;
        tooltip.add(Component.translatable("item.tcc.sacrifice_oppression.effect",
                String.format("%+.0f", meleeDamageBoost))
            .withStyle(ChatFormatting.WHITE));

        // 套装提示
        double setBonusPct = (TaczCuriosConfig.COMMON.sacrificeSetBonus.get() - 1.0) * 100;
        tooltip.add(Component.translatable("item.tcc.sacrifice_oppression.set_bonus",
                String.format("%+.0f", setBonusPct))
            .withStyle(ChatFormatting.LIGHT_PURPLE));

        tooltip.add(Component.literal(""));
        
        tooltip.add(Component.translatable("tcc.tooltip.rarity.epic"));
    }

    @Override
    public void applyGunSwitchEffect(LivingEntity livingEntity) {
        applyEffects(livingEntity);
    }
}
