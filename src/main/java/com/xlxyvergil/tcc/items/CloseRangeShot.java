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
 * 抵近射击 - 提升霰弹枪90%伤害
 * 效果：霰弹枪伤害+90%（加算）
 */
public class CloseRangeShot extends BaseCurioItem {
    
    // 属性修饰符UUID - 用于唯一标识这个修饰符
    private static final UUID DAMAGE_UUID = UUID.fromString("606453a5-947e-4020-8fc8-3f43c2c8cce9");
    
    // 修饰符名称
    private static final String DAMAGE_NAME = "tcc.close_range_shot.shotgun_damage";
    
    public CloseRangeShot(Properties properties) {
        super(properties);
    }
    
    /**
     * 应用效果
     * 提升霰弹枪伤害（加算）
     */
    @Override
    protected void applyEffects(LivingEntity livingEntity) {
        double damageBoost = TaczCuriosConfig.COMMON.closeRangeShotDamageBoost.get();
        AttributeHelper.applyModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE_SHOTGUN, damageBoost, DAMAGE_UUID, DAMAGE_NAME, AttributeModifier.Operation.ADDITION);
    }
    
    /**
     * 移除效果
     */
    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE_SHOTGUN, DAMAGE_UUID);
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
        double damageBoost = TaczCuriosConfig.COMMON.closeRangeShotDamageBoost.get() * 100;
        tooltip.add(Component.translatable("item.tcc.close_range_shot.effect", String.format("%+.0f", damageBoost))
            .withStyle(ChatFormatting.AQUA));
        
        // 添加饰品槽位信息
        tooltip.add(Component.literal(""));
        
        
        // 添加稀有度提示
        tooltip.add(Component.translatable("tcc.tooltip.rarity.uncommon"));
    }
    
    /**
     * 当生物切换武器时应用效果
     */
    @Override
    public void applyGunSwitchEffect(LivingEntity livingEntity) {
        applyEffects(livingEntity);
    }
}