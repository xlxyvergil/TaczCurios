package com.xlxyvergil.tcc.items.curios.tcc;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.items.TccCurioItem;
import com.xlxyvergil.tcc.util.FusionData;
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



public class BlazeStormPrime extends TccCurioItem {
    // 属性修饰符UUID - 用于唯一标识这些修饰符
    private static final UUID EXPLOSION_RADIUS_UUID = UUID.fromString("248f06a5-5144-47a0-b56a-6d830ade21b9");
    private static final UUID EXPLOSION_DAMAGE_UUID = UUID.fromString("76d7748e-2371-4513-90cc-880f469f389a");
    private static final UUID EXPLOSION_ENABLED_UUID = UUID.fromString("248f06a5-5144-90cc-b56a-6d830ade21b9");
    
    // 修饰符名称
    private static final String EXPLOSION_RADIUS_NAME = "tcc.blaze_storm_prime.explosion_radius";
    private static final String EXPLOSION_DAMAGE_NAME = "tcc.blaze_storm_prime.explosion_damage";
    private static final String EXPLOSION_ENABLED_NAME = "tcc.blaze_storm_prime.explosion_enabled";
    
    public BlazeStormPrime(Properties properties) {
        super(properties);
    }
    
    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        // 登记该饰品施加的修饰符 UUID → 来源饰品，供客户端属性面板显示来源图标。
        AttributeHelper.registerSourceItem(EXPLOSION_RADIUS_UUID, stack.getItem());
        AttributeHelper.registerSourceItem(EXPLOSION_DAMAGE_UUID, stack.getItem());
        AttributeHelper.registerSourceItem(EXPLOSION_ENABLED_UUID, stack.getItem());
        if (matchesRestriction(livingEntity)) {
            double explosionRadiusBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.blazeStormPrimeExplosionRadiusBoost.get());
            double explosionDamageBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.blazeStormPrimeExplosionDamageBoost.get());
            double explosionEnabled = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.blazeStormPrimeExplosionEnabled.get());

            AttributeHelper.applyModifier(livingEntity, AttributeHelper.EXPLOSION_RADIUS, explosionRadiusBoost, EXPLOSION_RADIUS_UUID, EXPLOSION_RADIUS_NAME, AttributeModifier.Operation.MULTIPLY_BASE);

            AttributeHelper.applyModifier(livingEntity, AttributeHelper.EXPLOSION_DAMAGE, explosionDamageBoost, EXPLOSION_DAMAGE_UUID, EXPLOSION_DAMAGE_NAME, AttributeModifier.Operation.MULTIPLY_BASE);

            AttributeHelper.applyModifier(livingEntity, AttributeHelper.EXPLOSION_ENABLED, explosionEnabled, EXPLOSION_ENABLED_UUID, EXPLOSION_ENABLED_NAME, AttributeModifier.Operation.MULTIPLY_BASE);
        }
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.EXPLOSION_RADIUS, EXPLOSION_RADIUS_UUID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.EXPLOSION_DAMAGE, EXPLOSION_DAMAGE_UUID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.EXPLOSION_ENABLED, EXPLOSION_ENABLED_UUID);
    }

    @Override
    public java.util.List<String> getWeaponTypeRestriction() {
        return GunTypeChecker.ALL_GUN_TYPES_LIST;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        tooltip.add(Component.literal(""));

        double explosionRadiusBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.blazeStormPrimeExplosionRadiusBoost.get() ) * 100;
        double explosionDamageBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.blazeStormPrimeExplosionDamageBoost.get() ) * 100;
        tooltip.add(Component.translatable("item.tcc.blaze_storm_prime.effect", 
                String.format("%+.0f", explosionRadiusBoost), String.format("%+.0f", explosionDamageBoost))
            .withStyle(ChatFormatting.WHITE));

        tooltip.add(Component.literal(""));

    }

}