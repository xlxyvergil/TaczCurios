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
 * 恶性扩- 提升165%霰弹枪伤害，提高55%不精准度
 * 效果：提65%霰弹枪伤害（加算），提高55%不精准度（加算）
 */
public class MalignantSpread extends BaseCurioItem {
    
    // 属性修饰符UUID - 用于唯一标识修饰
    private static final UUID DAMAGE_UUID = UUID.fromString("5bfabff0-b8df-48cd-9ecb-95027aafbf69");
    private static final UUID INACCURACY_UUID = UUID.fromString("03755bb2-350f-47ee-821f-db51a2a7f149");
    
    // 修饰符名
    private static final String DAMAGE_NAME = "tcc.malignant_spread.damage";
    private static final String INACCURACY_NAME = "tcc.malignant_spread.inaccuracy";
    
    
    public MalignantSpread(Properties properties) {
        super(properties);
    }
    
    /**
     * 应用恶性扩散效     * 提升霰弹枪伤害（加算）和不精准度（乘算）
     */
    @Override
    protected void applyEffects(LivingEntity livingEntity) {
        double damageBoost = TaczCuriosConfig.COMMON.malignantSpreadDamageBoost.get();
        double inaccuracyBoost = TaczCuriosConfig.COMMON.malignantSpreadAccuracyReduction.get();
        
        AttributeHelper.applyModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE_SHOTGUN, damageBoost, DAMAGE_UUID, DAMAGE_NAME, AttributeModifier.Operation.ADDITION);
        
        if (GunTypeChecker.isHoldingShotgun(livingEntity)) {
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.INACCURACY, inaccuracyBoost, INACCURACY_UUID, INACCURACY_NAME, AttributeModifier.Operation.ADDITION);
        }
    }
    
    /**
     * 移除恶性扩散效     */
    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE_SHOTGUN, DAMAGE_UUID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.INACCURACY, INACCURACY_UUID);
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
        double damageBoost = TaczCuriosConfig.COMMON.malignantSpreadDamageBoost.get() * 100;
        double inaccuracyBoost = TaczCuriosConfig.COMMON.malignantSpreadAccuracyReduction.get() * 100;
        tooltip.add(Component.translatable("item.tcc.malignant_spread.effect", String.format("%+.0f", damageBoost), String.format("%+.0f", inaccuracyBoost))
            .withStyle(ChatFormatting.GOLD));
        
        // 添加饰品槽位信息
        tooltip.add(Component.literal(""));
        
        
        // 添加稀有度提示
        tooltip.add(Component.translatable("tcc.tooltip.rarity.rare"));
    }
    
    /**
     * 当实体切换武器时应用效果
     */
    @Override
    public void applyGunSwitchEffect(LivingEntity livingEntity) {
        applyEffects(livingEntity);
    }
}