package com.xlxyvergil.tcc.items.curios.tcc;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.items.TccCurioItem;
import com.xlxyvergil.tcc.util.FusionData;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class OppressionPointPrime extends TccCurioItem {
    // 属性修饰符UUID - 用于唯一标识这个修饰符
    private static final UUID ATTACK_DAMAGE_UUID = UUID.fromString("b4763540-e80b-4bab-9e64-a4a2494d9f5e");
    
    // 修饰符名称
    private static final String ATTACK_DAMAGE_NAME = "tcc.oppression_point_prime.attack_damage";
    
    public OppressionPointPrime(Properties properties) {
        super(properties);
    }
    
    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        // 登记该饰品施加的修饰符 UUID → 来源饰品，供客户端属性面板显示来源图标。
        AttributeHelper.registerSourceItem(ATTACK_DAMAGE_UUID, stack.getItem());
        if (matchesRestriction(livingEntity)) {
            double damageBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.oppressionPointPrimeMeleeDamageBoost.get());
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.ATTACK_DAMAGE, damageBoost, ATTACK_DAMAGE_UUID, ATTACK_DAMAGE_NAME, AttributeModifier.Operation.MULTIPLY_BASE);
        }
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.ATTACK_DAMAGE, ATTACK_DAMAGE_UUID);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        tooltip.add(Component.literal(""));

        double meleeDamageBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.oppressionPointPrimeMeleeDamageBoost.get() ) * 100;
        tooltip.add(Component.translatable("item.tcc.oppression_point_prime.effect", String.format("%+.0f", meleeDamageBoost))
            .withStyle(ChatFormatting.WHITE));

        tooltip.add(Component.literal(""));

    }

    @Override
    public java.util.List<String> getWeaponTypeRestriction() {
        return java.util.List.of("melee");
    }
}