package com.xlxyvergil.tcc.items;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.util.BaseCurioItem;
import com.xlxyvergil.tcc.util.GunTypeChecker;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * 爆发装填 - 提升装填速度
 * 效果：提升装填速度（加算），仅对步枪、狙击枪、冲锋枪、机枪、发射器生效
 */
public class BurstReload extends BaseCurioItem {
    
    // 属性修饰符UUID - 用于唯一标识这些修饰符
    private static final UUID RELOAD_UUID = UUID.fromString("cf64bdda-7972-4439-ab6a-1fe552c4caa3");
    
    // 修饰符名称
    private static final String RELOAD_NAME = "tcc.burst_reload.reload_speed";
    
    public BurstReload(Properties properties) {
        super(properties
            .stacksTo(1));
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity) {
        if (GunTypeChecker.isHoldingDmgBoostGunType(livingEntity)) {
            double reloadBoost = TaczCuriosConfig.COMMON.burstReloadReloadSpeedBoost.get();
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.RELOAD_TIME, reloadBoost, RELOAD_UUID, RELOAD_NAME, AttributeModifier.Operation.ADDITION);
        }
    }
    
    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.RELOAD_TIME, RELOAD_UUID);
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
        double reloadBoost = TaczCuriosConfig.COMMON.burstReloadReloadSpeedBoost.get() * 100;
        tooltip.add(Component.translatable("item.tcc.burst_reload.effect", String.format("%+.0f", reloadBoost))
            .withStyle(ChatFormatting.BLUE));
        
        // 添加饰品槽位信息
        tooltip.add(Component.literal(""));
        
        
        // 添加稀有度提示
        tooltip.add(Component.translatable("tcc.tooltip.rarity.common"));
    }
    
    @Override
    public void applyGunSwitchEffect(LivingEntity livingEntity) {
        applyEffects(livingEntity);
    }
}