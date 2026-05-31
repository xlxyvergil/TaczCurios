package com.xlxyvergil.tcc.items;

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
 * 并合膛线 - 提升特定枪械伤害，提高持枪移动速度
 * 效果：特定枪械伤害加成（加算），持枪移动速度加成（加算）
 */
public class MergedRifling extends BaseCurioItem {
    
    // 属性修饰符UUID - 用于唯一标识这些修饰符
    private static final UUID[] DAMAGE_UUIDS = {
        UUID.fromString("f36f64c9-c3ec-4faf-b233-1d3ae64ef940"),
        UUID.fromString("32254b9b-364b-44de-bbf2-352df3726ac5"),
        UUID.fromString("adfae406-517c-442b-99cb-1708ec1f1f63"),
        UUID.fromString("f1f1f906-2111-425c-bb8c-be24a54a1f95"),
        UUID.fromString("39f3a9fd-562e-48bf-b26f-fbe3d106e7e8")
    };
    private static final UUID MOVEMENT_SPEED_UUID = UUID.fromString("6967f153-c8f1-4f6c-9752-bd2f5e5253c2");
    
    // 修饰符名称
    private static final String[] DAMAGE_NAMES = {
        "tcc.merged_rifling.rifle_damage",
        "tcc.merged_rifling.sniper_damage",
        "tcc.merged_rifling.smg_damage",
        "tcc.merged_rifling.lmg_damage",
        "tcc.merged_rifling.launcher_damage"
    };
    private static final String MOVEMENT_SPEED_NAME = "tcc.merged_rifling.movement_speed";
    
    public MergedRifling(Properties properties) {
        super(properties);
    }
    
    /**
     * 应用膛线效果
     * 提升特定枪械伤害和持枪移动速度（都使用加算
     */
    @Override
    protected void applyEffects(LivingEntity livingEntity) {
        double damageBoost = TaczCuriosConfig.COMMON.mergedRiflingDamageBoost.get();
        double speedBoost = TaczCuriosConfig.COMMON.mergedRiflingMovementSpeedBoost.get();
        
        AttributeHelper.applyModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE_RIFLE, damageBoost, DAMAGE_UUIDS[0], DAMAGE_NAMES[0], AttributeModifier.Operation.ADDITION);
        AttributeHelper.applyModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE_SNIPER, damageBoost, DAMAGE_UUIDS[1], DAMAGE_NAMES[1], AttributeModifier.Operation.ADDITION);
        AttributeHelper.applyModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE_SMG, damageBoost, DAMAGE_UUIDS[2], DAMAGE_NAMES[2], AttributeModifier.Operation.ADDITION);
        AttributeHelper.applyModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE_LMG, damageBoost, DAMAGE_UUIDS[3], DAMAGE_NAMES[3], AttributeModifier.Operation.ADDITION);
        AttributeHelper.applyModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE_LAUNCHER, damageBoost, DAMAGE_UUIDS[4], DAMAGE_NAMES[4], AttributeModifier.Operation.ADDITION);
        
        if (GunTypeChecker.isHoldingDmgBoostGunType(livingEntity)) {
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.MOVE_SPEED, speedBoost, MOVEMENT_SPEED_UUID, MOVEMENT_SPEED_NAME, AttributeModifier.Operation.ADDITION);
        }
    }
    
    /**
     * 移除膛线效果
     */
    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE_RIFLE, DAMAGE_UUIDS[0]);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE_SNIPER, DAMAGE_UUIDS[1]);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE_SMG, DAMAGE_UUIDS[2]);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE_LMG, DAMAGE_UUIDS[3]);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE_LAUNCHER, DAMAGE_UUIDS[4]);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.MOVE_SPEED, MOVEMENT_SPEED_UUID);
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
        double damageBoost = TaczCuriosConfig.COMMON.mergedRiflingDamageBoost.get() * 100;
        double speedBoost = TaczCuriosConfig.COMMON.mergedRiflingMovementSpeedBoost.get() * 100;
        tooltip.add(Component.translatable("item.tcc.merged_rifling.effect", 
                String.format("%+.0f", damageBoost), String.format("%+.0f", speedBoost))
            .withStyle(ChatFormatting.WHITE));
        
        // 添加饰品槽位信息
        tooltip.add(Component.literal(""));
        
        
        // 添加稀有度提示
        tooltip.add(Component.translatable("tcc.tooltip.rarity.epic"));
    }
    
    /**
     * 当生物切换武器时应用效果
     */
    @Override
    public void applyGunSwitchEffect(LivingEntity livingEntity) {
        applyEffects(livingEntity);
    }
}