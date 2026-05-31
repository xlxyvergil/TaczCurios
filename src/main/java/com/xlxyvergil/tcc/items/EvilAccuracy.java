package com.xlxyvergil.tcc.items;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.util.BaseCurioItem;

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
 * 极恶精准饰品
 * 效果：降低90%后坐力，降低36%射速（都加算）
 */
public class EvilAccuracy extends BaseCurioItem {
    
    // 属性修饰符UUID - 用于唯一标识这些修饰符
    private static final UUID RECOIL_UUID = UUID.fromString("98a8e44a-7d8d-4d10-b934-7e1e1c1c8fca");
    private static final UUID ROUNDS_PER_MINUTE_UUID = UUID.fromString("7da86e2a-9c63-4d3f-8237-feda8559638e");
    
    // 修饰符名称
    private static final String RECOIL_NAME = "tcc.evil_accuracy.recoil";
    private static final String ROUNDS_PER_MINUTE_NAME = "tcc.evil_accuracy.rounds_per_minute";
    
    public EvilAccuracy(Properties properties) {
        super(properties);
    }
    
    /**
     * 应用所有效果加成
     * 降低配置中的后坐力和射速（都加算）
     */
    @Override
    protected void applyEffects(LivingEntity livingEntity) {
        double recoilReduction = TaczCuriosConfig.COMMON.evilAccuracyRecoilReduction.get();
        double fireRateReduction = TaczCuriosConfig.COMMON.evilAccuracyFireRateReduction.get();
        
        AttributeHelper.applyModifier(livingEntity, AttributeHelper.RECOIL, recoilReduction, RECOIL_UUID, RECOIL_NAME, AttributeModifier.Operation.ADDITION);
        AttributeHelper.applyModifier(livingEntity, AttributeHelper.ROUNDS_PER_MINUTE, fireRateReduction, ROUNDS_PER_MINUTE_UUID, ROUNDS_PER_MINUTE_NAME, AttributeModifier.Operation.ADDITION);
    }
    
    /**
     * 移除所有效果加成
     */
    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.RECOIL, RECOIL_UUID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.ROUNDS_PER_MINUTE, ROUNDS_PER_MINUTE_UUID);
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
        double recoilReduction = TaczCuriosConfig.COMMON.evilAccuracyRecoilReduction.get() * 100;
        double fireRateReduction = TaczCuriosConfig.COMMON.evilAccuracyFireRateReduction.get() * 100;
        tooltip.add(Component.translatable("item.tcc.evil_accuracy.effect", 
                String.format("%+.0f", recoilReduction), String.format("%+.0f", fireRateReduction))
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