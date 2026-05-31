package com.xlxyvergil.tcc.items;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.util.BaseCurioItem;

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

/**
 * 撕裂Prime饰品
 * 效果：增加5%射速（乘算），增加0.2穿透（加算）
 */
public class RippingPrime extends BaseCurioItem {
    
    // 属性修饰符UUID - 用于唯一标识这些修饰
    private static final UUID ROUNDS_PER_MINUTE_UUID = UUID.fromString("e3eb5b32-fdfc-47ca-988c-a82d9d8173a7");
    private static final UUID PIERCE_UUID = UUID.fromString("269dbf48-02f5-43f9-a4f2-50bf03aa10a6");
    
    // 修饰符名
    private static final String ROUNDS_PER_MINUTE_NAME = "tcc.ripping_prime.rounds_per_minute";
    private static final String PIERCE_NAME = "tcc.ripping_prime.pierce";
    
    public RippingPrime(Properties properties) {
        super(properties);
    }
    
    @Override
    protected void applyEffects(LivingEntity livingEntity) {
        double fireRateBoost = TaczCuriosConfig.COMMON.rippingPrimeFireRateBoost.get();
        double penetrationBoost = TaczCuriosConfig.COMMON.rippingPrimePenetrationBoost.get();
        
        AttributeHelper.applyModifier(livingEntity, AttributeHelper.ROUNDS_PER_MINUTE, fireRateBoost, ROUNDS_PER_MINUTE_UUID, ROUNDS_PER_MINUTE_NAME, AttributeModifier.Operation.MULTIPLY_BASE);
        AttributeHelper.applyModifier(livingEntity, AttributeHelper.PIERCE, penetrationBoost, PIERCE_UUID, PIERCE_NAME, AttributeModifier.Operation.ADDITION);
    }
    
    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.ROUNDS_PER_MINUTE, ROUNDS_PER_MINUTE_UUID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.PIERCE, PIERCE_UUID);
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
        double fireRateBoost = TaczCuriosConfig.COMMON.rippingPrimeFireRateBoost.get() * 100;
        double penetrationBoost = TaczCuriosConfig.COMMON.rippingPrimePenetrationBoost.get();
        tooltip.add(Component.translatable("item.tcc.ripping_prime.effect", 
                String.format("%+.0f", fireRateBoost), String.format("%.1f", penetrationBoost))
            .withStyle(ChatFormatting.WHITE));
        
        // 添加饰品槽位信息
        tooltip.add(Component.literal(""));
        
        
        // 添加稀有度提示
        tooltip.add(Component.translatable("tcc.tooltip.rarity.epic"));
    }
    
    @Override
    public void applyGunSwitchEffect(LivingEntity livingEntity) {
        applyEffects(livingEntity);
    }
}
