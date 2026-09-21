package com.xlxyvergil.tcc.items.curios.tcc;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.items.TccCurioItem;
import com.xlxyvergil.tcc.util.FusionData;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.resources.ResourceLocation;
import com.xlxyvergil.tcc.TaczCurios;
public class CarefulHeart extends TccCurioItem {
    // 属性修饰符UUID - 用于唯一标识这些修饰符
    private static final ResourceLocation LAUNCHER_DAMAGE_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "careful_heart_81343572_53c1");
    private static final ResourceLocation EXPLOSION_DAMAGE_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "careful_heart_2fd98843_5c86");
    private static final ResourceLocation EXPLOSION_RADIUS_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "careful_heart_e966cd29_4409");
    private static final ResourceLocation EXPLOSION_ENABLED_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "careful_heart_248f06a5_21b9");
    
    // 修饰符名称
    
    public CarefulHeart(Properties properties) {
        super(properties);
    }
    
    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        // 登记该饰品施加的修饰符 ID → 来源饰品，供客户端属性面板显示来源图标。
        AttributeHelper.registerSourceItem(LAUNCHER_DAMAGE_ID, stack.getItem());
        AttributeHelper.registerSourceItem(EXPLOSION_DAMAGE_ID, stack.getItem());
        AttributeHelper.registerSourceItem(EXPLOSION_RADIUS_ID, stack.getItem());
        AttributeHelper.registerSourceItem(EXPLOSION_ENABLED_ID, stack.getItem());
        if (matchesRestriction(livingEntity)) {
            double launcherDamageBoost = FusionData.from(stack).getActualValue(
                    TaczCuriosConfig.COMMON.carefulHeartLauncherDamageBoost.get());
            double explosionDamageBoost = FusionData.from(stack).getActualValue(
                    TaczCuriosConfig.COMMON.carefulHeartExplosionDamageBoost.get());
            double explosionRadiusBoost = FusionData.from(stack).getActualValue(
                    TaczCuriosConfig.COMMON.carefulHeartExplosionRadiusBoost.get());
            double explosionEnabled = TaczCuriosConfig.COMMON.carefulHeartExplosionEnabled.get();

            AttributeHelper.applyModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE_LAUNCHER, launcherDamageBoost, LAUNCHER_DAMAGE_ID, AttributeModifier.Operation.ADD_VALUE);
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.EXPLOSION_DAMAGE, explosionDamageBoost, EXPLOSION_DAMAGE_ID, AttributeModifier.Operation.ADD_VALUE);
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.EXPLOSION_RADIUS, explosionRadiusBoost, EXPLOSION_RADIUS_ID, AttributeModifier.Operation.ADD_VALUE);
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.EXPLOSION_ENABLED, explosionEnabled, EXPLOSION_ENABLED_ID, AttributeModifier.Operation.ADD_VALUE);
        }
    }
    
    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE_LAUNCHER, LAUNCHER_DAMAGE_ID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.EXPLOSION_DAMAGE, EXPLOSION_DAMAGE_ID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.EXPLOSION_RADIUS, EXPLOSION_RADIUS_ID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.EXPLOSION_ENABLED, EXPLOSION_ENABLED_ID);
    }

    @Override
    public java.util.List<String> getWeaponTypeRestriction() {
        return java.util.List.of("rpg", "mg");
    }
    

    
    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        Level level = context.level();
        super.appendHoverText(stack, context, tooltip, flag);

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