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
 * 地狱弹膛 - 提升弹头数量
 * 效果：提升弹头数量（加算
 */
public class InfernalChamber extends BaseCurioItem {

    // 属性修饰符UUID - 用于唯一标识修饰
    private static final UUID BULLET_COUNT_UUID = UUID.fromString("50d58834-a161-4b25-a13d-e56a375cd970");

    // 修饰符名
    private static final String BULLET_COUNT_NAME = "tcc.infernal_chamber.bullet_count";

    public InfernalChamber(Properties properties) {
        super(properties);
    }

    /**
     * 应用地狱弹膛效果
     * 提升弹头数量（加算）
     */
    @Override
    protected void applyEffects(LivingEntity livingEntity) {
        // 检查生物是否持有霰弹枪，只有持有霰弹枪时才应用加成
        if (GunTypeChecker.isHoldingShotgun(livingEntity)) {
            double bulletCountBoost = TaczCuriosConfig.COMMON.infernalChamberBulletCountBoost.get();
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.BULLET_COUNT, bulletCountBoost, BULLET_COUNT_UUID, BULLET_COUNT_NAME, AttributeModifier.Operation.ADDITION);
        }
    }

    /**
     * 移除地狱弹膛效果
     */
    @Override
    protected void removeEffects(LivingEntity livingEntity) {
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
        double bulletCountBoost = TaczCuriosConfig.COMMON.infernalChamberBulletCountBoost.get() * 100;
        tooltip.add(Component.translatable("item.tcc.infernal_chamber.effect", String.format("%+.0f", bulletCountBoost))
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