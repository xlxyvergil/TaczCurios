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
 * 士兵特定挂牌 - 提供通用枪械伤害加成（乘法）
 * 效果：为玩家提供通用枪械伤害加成（乘法）
 */
public class SoldierSpecificTag extends BaseCurioItem {
    
    // 属性修饰符UUID - 用于唯一标识这个修饰
    private static final UUID GUN_DAMAGE_UUID = UUID.fromString("bbd020e4-a079-46e1-b236-3eea2c13da4f");
    
    // 修饰符名
    private static final String GUN_DAMAGE_NAME = "tcc.soldier_specific_tag.gun_damage";
    
    public SoldierSpecificTag(Properties properties) {
        super(properties);
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity) {
        double damageBoost = TaczCuriosConfig.COMMON.soldierSpecificTagDamageBoost.get();
        AttributeHelper.applyModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE, damageBoost, GUN_DAMAGE_UUID, GUN_DAMAGE_NAME, AttributeModifier.Operation.MULTIPLY_BASE);
    }
    
    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE, GUN_DAMAGE_UUID);
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
        double damageBoost = TaczCuriosConfig.COMMON.soldierSpecificTagDamageBoost.get() * 100;
        tooltip.add(Component.translatable("item.tcc.soldier_specific_tag.effect", String.format("%+.0f", damageBoost))
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
