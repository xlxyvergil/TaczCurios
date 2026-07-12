package com.xlxyvergil.tcc.items.curios;

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
 * 弹头扩散 - 提升120%弹头数量
 * 效果：提升120%弹头数量（加算）
 */
public class BulletSpread extends BaseCurioItem {
    
    // 属性修饰符UUID - 用于唯一标识修饰符
    private static final UUID BULLET_COUNT_UUID = UUID.fromString("0e7e5d6a-c006-4b94-b5fa-ada36d9f71d2");
    
    // 修饰符名称
    private static final String BULLET_COUNT_NAME = "tcc.bullet_spread.bullet_count";
    
    public BulletSpread(Properties properties) {
        super(properties);
    }
    
    /**
     * 应用弹头扩散效果
     * 提升弹头数量（加算）
     */
    @Override
    protected void applyEffects(LivingEntity livingEntity) {
        // 检查生物是否持有手枪，只有持有手枪时才应用加成
        if (GunTypeChecker.isHoldingPistol(livingEntity)) {
            double bulletCountBoost = TaczCuriosConfig.COMMON.bulletSpreadBulletCountBoost.get();
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.BULLET_COUNT, bulletCountBoost, BULLET_COUNT_UUID, BULLET_COUNT_NAME, AttributeModifier.Operation.ADDITION);
        }
    }
    
    /**
     * 移除弹头扩散效果
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
        double bulletCountBoost = TaczCuriosConfig.COMMON.bulletSpreadBulletCountBoost.get() * 100;
        tooltip.add(Component.translatable("item.tcc.bullet_spread.effect", String.format("%+.0f", bulletCountBoost))
            .withStyle(ChatFormatting.GOLD));

        // 添加饰品槽位信息
        tooltip.add(Component.literal(""));
        
        
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