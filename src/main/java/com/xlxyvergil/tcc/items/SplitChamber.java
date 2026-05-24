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
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * 分裂膛室 - 提升弹头数量
 * 效果：提升弹头数量（加算），仅对步枪、狙击枪、冲锋枪、机枪、发射器生效
 */
public class SplitChamber extends BaseCurioItem {
    
    // 属性修饰符UUID - 用于唯一标识这些修饰
    private static final UUID AMMO_UUID = UUID.fromString("7ee8eee4-ae89-490c-83d1-1392a6a71aa7");
    
    // 修饰符名
    private static final String AMMO_NAME = "tcc.split_chamber.bullet_count";
    
    public SplitChamber(Properties properties) {
        super(properties);
    }
    
    @Override
    protected void applyEffects(LivingEntity livingEntity) {
        if (GunTypeChecker.isHoldingDmgBoostGunType(livingEntity)) {
            double ammoBoost = TaczCuriosConfig.COMMON.splitChamberBulletCountBoost.get();
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.BULLET_COUNT, ammoBoost, AMMO_UUID, AMMO_NAME, AttributeModifier.Operation.ADDITION);
        }
    }
    
    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.BULLET_COUNT, AMMO_UUID);
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
        double ammoBoost = TaczCuriosConfig.COMMON.splitChamberBulletCountBoost.get() * 100;
        tooltip.add(Component.translatable("item.tcc.split_chamber.effect", String.format("%+.0f", ammoBoost))
            .withStyle(ChatFormatting.GOLD));
        
        // 添加饰品槽位信息
        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable("tcc.tooltip.slot"));
        
        // 添加稀有度提示
        tooltip.add(Component.translatable("tcc.tooltip.rarity.rare"));
    }
    
    @Override
    public void applyGunSwitchEffect(LivingEntity livingEntity) {
        applyEffects(livingEntity);
    }
}
