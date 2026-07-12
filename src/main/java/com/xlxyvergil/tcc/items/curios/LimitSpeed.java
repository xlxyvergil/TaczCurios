package com.xlxyvergil.tcc.items.curios;

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
 * 极限速度饰品
 * 效果：提0%弹药速度（乘算）
 */
public class LimitSpeed extends BaseCurioItem {
    
    // 属性修饰符UUID - 用于唯一标识这些修饰
    private static final UUID AMMO_SPEED_UUID = UUID.fromString("ad27e195-8647-4497-8792-9720043e1e95");
    
    // 修饰符名
    private static final String AMMO_SPEED_NAME = "tcc.limit_speed.ammo_speed";
    
    public LimitSpeed(Properties properties) {
        super(properties);
    }
    
    /**
     * 应用所有效果加
     * 提高配置中的弹药速度（乘算）
     */
    @Override
    protected void applyEffects(LivingEntity livingEntity) {
        double ammoSpeedBoost = TaczCuriosConfig.COMMON.limitSpeedBulletSpeedBoost.get();
        AttributeHelper.applyModifier(livingEntity, AttributeHelper.AMMO_SPEED, ammoSpeedBoost, AMMO_SPEED_UUID, AMMO_SPEED_NAME, AttributeModifier.Operation.MULTIPLY_BASE);
    }
    
    /**
     * 移除所有效果加
     */
    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.AMMO_SPEED, AMMO_SPEED_UUID);
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
        double ammoSpeedBoost = TaczCuriosConfig.COMMON.limitSpeedBulletSpeedBoost.get() * 100;
        tooltip.add(Component.translatable("item.tcc.limit_speed.effect", String.format("%+.0f", ammoSpeedBoost))
            .withStyle(ChatFormatting.AQUA));
        
        // 添加饰品槽位信息
        tooltip.add(Component.literal(""));
        
        
        // 添加稀有度提示
        tooltip.add(Component.translatable("tcc.tooltip.rarity.uncommon"));
    }
    
    /**
     * 当玩家切换武器时应用效果
     */
    @Override
    public void applyGunSwitchEffect(LivingEntity livingEntity) {
        applyEffects(livingEntity);
    }
}
