package com.xlxyvergil.tcc.items.curios.tcc;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.items.TccCurioItem;
import com.xlxyvergil.tcc.util.FusionData;

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
 * 我小心海也绝非鳝类：重型武器伤害、爆炸伤害、爆炸范围各+300%
 */
public class CarefulHeart extends TccCurioItem {
    
    // 属性修饰符UUID - 用于唯一标识这些修饰符
    private static final UUID LAUNCHER_DAMAGE_UUID = UUID.fromString("81343572-fe9a-4cf2-bdf9-253af5a153c1");
    private static final UUID EXPLOSION_DAMAGE_UUID = UUID.fromString("2fd98843-cf54-4ca7-949d-e8d50e295c86");
    private static final UUID EXPLOSION_RADIUS_UUID = UUID.fromString("e966cd29-d1c2-4770-a422-0f71c2ef4409");
    private static final UUID EXPLOSION_ENABLED_UUID = UUID.fromString("248f06a5-5144-4770-b56a-6d830ade21b9");
    
    // 修饰符名称
    private static final String LAUNCHER_DAMAGE_NAME = "tcc.careful_heart.launcher_damage";
    private static final String EXPLOSION_DAMAGE_NAME = "tcc.careful_heart.explosion_damage";
    private static final String EXPLOSION_RADIUS_NAME = "tcc.careful_heart.explosion_radius";
    private static final String EXPLOSION_ENABLED_NAME = "tcc.careful_heart.explosion_enabled";
    
    public CarefulHeart(Properties properties) {
        super(properties);
    }
    
    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        if (matchesRestriction(livingEntity)) {
            double launcherDamageBoost = FusionData.from(stack).getActualValue(
                    TaczCuriosConfig.COMMON.carefulHeartLauncherDamageBoost.get());
            double explosionDamageBoost = FusionData.from(stack).getActualValue(
                    TaczCuriosConfig.COMMON.carefulHeartExplosionDamageBoost.get());
            double explosionRadiusBoost = FusionData.from(stack).getActualValue(
                    TaczCuriosConfig.COMMON.carefulHeartExplosionRadiusBoost.get());
            double explosionEnabled = TaczCuriosConfig.COMMON.carefulHeartExplosionEnabled.get();

            AttributeHelper.applyModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE_LAUNCHER, launcherDamageBoost, LAUNCHER_DAMAGE_UUID, LAUNCHER_DAMAGE_NAME, AttributeModifier.Operation.ADDITION);
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.EXPLOSION_DAMAGE, explosionDamageBoost, EXPLOSION_DAMAGE_UUID, EXPLOSION_DAMAGE_NAME, AttributeModifier.Operation.ADDITION);
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.EXPLOSION_RADIUS, explosionRadiusBoost, EXPLOSION_RADIUS_UUID, EXPLOSION_RADIUS_NAME, AttributeModifier.Operation.ADDITION);
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.EXPLOSION_ENABLED, explosionEnabled, EXPLOSION_ENABLED_UUID, EXPLOSION_ENABLED_NAME, AttributeModifier.Operation.ADDITION);
        }
    }
    
    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE_LAUNCHER, LAUNCHER_DAMAGE_UUID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.EXPLOSION_DAMAGE, EXPLOSION_DAMAGE_UUID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.EXPLOSION_RADIUS, EXPLOSION_RADIUS_UUID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.EXPLOSION_ENABLED, EXPLOSION_ENABLED_UUID);
    }

    @Override
    public java.util.List<String> getWeaponTypeRestriction() {
        return java.util.List.of("rpg", "mg");
    }
    

    
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        tooltip.add(Component.literal(""));

        double launcherDamageBoost = FusionData.from(stack).getActualValue(
                TaczCuriosConfig.COMMON.carefulHeartLauncherDamageBoost.get()) * 100;
        double explosionDamageBoost = FusionData.from(stack).getActualValue(
                TaczCuriosConfig.COMMON.carefulHeartExplosionDamageBoost.get()) * 100;
        double explosionRadiusBoost = FusionData.from(stack).getActualValue(
                TaczCuriosConfig.COMMON.carefulHeartExplosionRadiusBoost.get()) * 100;
        tooltip.add(Component.translatable("item.tcc.careful_heart.effect", 
                String.format("%+.0f", launcherDamageBoost), String.format("%+.0f", explosionDamageBoost), String.format("%+.0f", explosionRadiusBoost))
            .withStyle(ChatFormatting.WHITE));

        tooltip.add(Component.literal(""));

    }

}