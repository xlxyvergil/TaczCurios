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
 * 预言契约 - 提升手枪90%伤害
 * 效果：手枪伤90%（加算）
 */
public class ProphecyPact extends BaseCurioItem {
    
    // 属性修饰符UUID - 用于唯一标识这个修饰
    private static final UUID DAMAGE_UUID = UUID.fromString("6edbaedf-2502-4fe0-8e2c-9054d6a9ecc1");
    
    // 修饰符名
    private static final String DAMAGE_NAME = "tcc.prophecy_pact.pistol_damage";
    
    public ProphecyPact(Properties properties) {
        super(properties);
    }
    
    /**
     * 应用效果
     * 提升手枪伤害（加算）
     */
    @Override
    protected void applyEffects(LivingEntity livingEntity) {
        double damageBoost = TaczCuriosConfig.COMMON.prophecyPactDamageBoost.get();
        AttributeHelper.applyModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE_PISTOL, damageBoost, DAMAGE_UUID, DAMAGE_NAME, AttributeModifier.Operation.ADDITION);
    }
    
    /**
     * 移除效果
     */
    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE_PISTOL, DAMAGE_UUID);
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
        double damageBoost = TaczCuriosConfig.COMMON.prophecyPactDamageBoost.get() * 100;
        tooltip.add(Component.translatable("item.tcc.prophecy_pact.effect", String.format("%+.0f", damageBoost))
            .withStyle(ChatFormatting.BLUE));
        
        // 添加饰品槽位信息
        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable("tcc.tooltip.slot"));
        
        // 添加稀有度提示
        tooltip.add(Component.translatable("tcc.tooltip.rarity.common"));
    }
    
    /**
     * 当玩家切换武器时应用效果
     */
    @Override
    public void applyGunSwitchEffect(LivingEntity livingEntity) {
        applyEffects(livingEntity);
    }
}