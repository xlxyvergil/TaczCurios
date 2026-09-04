package com.xlxyvergil.tcc.items.curios.tcc;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.items.TccCurioItem;
import com.xlxyvergil.tcc.util.FusionData;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class DeadlySurge extends TccCurioItem {
    private static final UUID ROUNDS_PER_MINUTE_UUID = UUID.fromString("d8e4852c-2b0c-4a77-a9b3-a2a84683ae93");
    private static final UUID BULLET_COUNT_UUID = UUID.fromString("b00e1320-1674-4bdb-8456-6fe4b80791fc");

    private static final String ROUNDS_PER_MINUTE_NAME = "tcc.deadly_surge.rounds_per_minute";
    private static final String BULLET_COUNT_NAME = "tcc.deadly_surge.bullet_count";

    public DeadlySurge(Properties properties) {
        super(properties);
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        // 登记该饰品施加的修饰符 UUID → 来源饰品，供客户端属性面板显示来源图标。
        AttributeHelper.registerSourceItem(ROUNDS_PER_MINUTE_UUID, stack.getItem());
        AttributeHelper.registerSourceItem(BULLET_COUNT_UUID, stack.getItem());
        if (matchesRestriction(livingEntity)) {
            double roundsPerMinuteBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.deadlySurgeFireRateBoost.get());
            double bulletCountBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.deadlySurgeBulletCountBoost.get());
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.ROUNDS_PER_MINUTE, roundsPerMinuteBoost, ROUNDS_PER_MINUTE_UUID, ROUNDS_PER_MINUTE_NAME, AttributeModifier.Operation.ADDITION);
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.BULLET_COUNT, bulletCountBoost, BULLET_COUNT_UUID, BULLET_COUNT_NAME, AttributeModifier.Operation.ADDITION);
        }
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.ROUNDS_PER_MINUTE, ROUNDS_PER_MINUTE_UUID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.BULLET_COUNT, BULLET_COUNT_UUID);
    }


    @Override
    public java.util.List<String> getWeaponTypeRestriction() {
        return java.util.List.of("pistol");
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        tooltip.add(Component.literal(""));

        double roundsPerMinuteBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.deadlySurgeFireRateBoost.get() ) * 100;
        double bulletCountBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.deadlySurgeBulletCountBoost.get() ) * 100;
        tooltip.add(Component.translatable("item.tcc.deadly_surge.effect", String.format("%+.0f", roundsPerMinuteBoost), String.format("%+.0f", bulletCountBoost))
            .withStyle(ChatFormatting.GOLD));

        tooltip.add(Component.literal(""));

    }
    
}