package com.xlxyvergil.tcc.items;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
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

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * 致命洪流 - 提升60%射速和60%弹头数量
 * 效果：提升60%射速（加算），提升60%弹头数量（加算）
 */
public class DeadlySurge extends BaseCurioItem {

    // 属性修饰符UUID - 用于唯一标识修饰符
    private static final UUID ROUNDS_PER_MINUTE_UUID = UUID.fromString("d8e4852c-2b0c-4a77-a9b3-a2a84683ae93");
    private static final UUID BULLET_COUNT_UUID = UUID.fromString("b00e1320-1674-4bdb-8456-6fe4b80791fc");

    // 修饰符名称
    private static final String ROUNDS_PER_MINUTE_NAME = "tcc.deadly_surge.rounds_per_minute";
    private static final String BULLET_COUNT_NAME = "tcc.deadly_surge.bullet_count";

    public DeadlySurge(Properties properties) {
        super(properties);
    }

    /**
     * 应用致命洪流效果
     * 提升射速（加算）和弹头数量（加算）
     */
    @Override
    protected void applyEffects(LivingEntity livingEntity) {
        // 检查生物是否持有手枪，只有持有手枪时才应用加成
        if (GunTypeChecker.isHoldingPistol(livingEntity)) {
            double roundsPerMinuteBoost = TaczCuriosConfig.COMMON.deadlySurgeFireRateBoost.get();
            double bulletCountBoost = TaczCuriosConfig.COMMON.deadlySurgeBulletCountBoost.get();
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.ROUNDS_PER_MINUTE, roundsPerMinuteBoost, ROUNDS_PER_MINUTE_UUID, ROUNDS_PER_MINUTE_NAME, AttributeModifier.Operation.ADDITION);
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.BULLET_COUNT, bulletCountBoost, BULLET_COUNT_UUID, BULLET_COUNT_NAME, AttributeModifier.Operation.ADDITION);
        }
    }

    /**
     * 移除致命洪流效果
     */
    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.ROUNDS_PER_MINUTE, ROUNDS_PER_MINUTE_UUID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.BULLET_COUNT, BULLET_COUNT_UUID);
    }


    /**
     * 添加物品的悬浮提示信息（鼠标悬停时显示）
     */
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);



        // 添加空行分隔
        tooltip.add(Component.literal(""));

        // 添加装备效果
        double roundsPerMinuteBoost = TaczCuriosConfig.COMMON.deadlySurgeFireRateBoost.get() * 100;
        double bulletCountBoost = TaczCuriosConfig.COMMON.deadlySurgeBulletCountBoost.get() * 100;
        tooltip.add(Component.translatable("item.tcc.deadly_surge.effect", String.format("%+.0f", roundsPerMinuteBoost), String.format("%+.0f", bulletCountBoost))
            .withStyle(ChatFormatting.GOLD));

        // 添加饰品槽位信息
        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable("tcc.tooltip.slot"));

        // 添加稀有度提示
        tooltip.add(Component.translatable("tcc.tooltip.rarity.rare"));
    }
    
    /**
     * 当生物切换武器时应用效果
     */
    @Override
    public void applyGunSwitchEffect(LivingEntity livingEntity) {
        applyEffects(livingEntity);
    }
}