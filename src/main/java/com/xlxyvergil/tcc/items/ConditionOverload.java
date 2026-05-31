package com.xlxyvergil.tcc.items;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.BaseCurioItem;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 异况超量 - 近战饰品
 * 手持近战武器时，目标每有一种负面效果，伤害+80%（LivingHurtEvent直接乘算）
 */
public class ConditionOverload extends BaseCurioItem {

    public ConditionOverload(Properties properties) {
        super(properties);
    }

    private static boolean isHoldingMeleeWeapon(LivingEntity entity) {
        return !entity.getMainHandItem().getAttributeModifiers(EquipmentSlot.MAINHAND)
            .get(Attributes.ATTACK_DAMAGE).isEmpty();
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity) {
        // 无基础属性，伤害乘算在LivingHurtEvent中处理
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        // 无基础属性
    }

    @Override
    public void curioTick(top.theillusivec4.curios.api.SlotContext slotContext, ItemStack stack) {
        // tick不需要处理
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.literal(""));
        double perHarmful = TaczCuriosConfig.COMMON.conditionOverloadPerHarmful.get() * 100;
        tooltip.add(Component.translatable("item.tcc.condition_overload.effect",
                String.format("%+.0f", perHarmful))
            .withStyle(ChatFormatting.BLUE));
        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable("tcc.tooltip.slot"));
        tooltip.add(Component.translatable("tcc.tooltip.rarity.rare"));
    }

    @Override
    public void applyGunSwitchEffect(LivingEntity livingEntity) {
        applyEffects(livingEntity);
    }
}
