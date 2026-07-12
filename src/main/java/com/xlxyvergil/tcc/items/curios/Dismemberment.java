package com.xlxyvergil.tcc.items.curios;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
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

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * 肢解 - 近战饰品
 * 效果：暴击伤害 +90%
 */
public class Dismemberment extends BaseCurioItem {

    private static final UUID CRIT_DAMAGE_UUID = UUID.fromString("a1b2c3d4-5002-4000-8000-000000000001");

    private static final String CRIT_DAMAGE_NAME = "tcc.dismemberment.crit_damage";

    public Dismemberment(Properties properties) {
        super(properties);
    }

    private static boolean isHoldingMeleeWeapon(LivingEntity entity) {
        return !entity.getMainHandItem().getAttributeModifiers(EquipmentSlot.MAINHAND)
            .get(Attributes.ATTACK_DAMAGE).isEmpty();
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity) {
        if (isHoldingMeleeWeapon(livingEntity)) {
            double critDamageBoost = TaczCuriosConfig.COMMON.dismembermentCritDamage.get();
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.CRIT_DAMAGE, critDamageBoost, CRIT_DAMAGE_UUID, CRIT_DAMAGE_NAME, AttributeModifier.Operation.MULTIPLY_BASE);
        } else {
            AttributeHelper.removeModifier(livingEntity, AttributeHelper.CRIT_DAMAGE, CRIT_DAMAGE_UUID);
        }
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.CRIT_DAMAGE, CRIT_DAMAGE_UUID);
    }

    @Override
    public void curioTick(top.theillusivec4.curios.api.SlotContext slotContext, ItemStack stack) {
        applyEffects(slotContext.entity());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        tooltip.add(Component.literal(""));

        double critDamageBoost = TaczCuriosConfig.COMMON.dismembermentCritDamage.get() * 100;
        tooltip.add(Component.translatable("item.tcc.dismemberment.effect",
                String.format("%+.0f", critDamageBoost))
            .withStyle(ChatFormatting.BLUE));

        tooltip.add(Component.literal(""));
        
        tooltip.add(Component.translatable("tcc.tooltip.rarity.common"));
    }

    @Override
    public void applyGunSwitchEffect(LivingEntity livingEntity) {
        applyEffects(livingEntity);
    }
}
