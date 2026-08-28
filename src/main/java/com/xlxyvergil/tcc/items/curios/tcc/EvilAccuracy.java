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

/**
 * 极恶精准：降低50%后坐力，降低36%射速（都加算）
 */
public class EvilAccuracy extends TccCurioItem {
    
    private static final UUID RECOIL_UUID = UUID.fromString("98a8e44a-7d8d-4d10-b934-7e1e1c1c8fca");
    private static final UUID ROUNDS_PER_MINUTE_UUID = UUID.fromString("7da86e2a-9c63-4d3f-8237-feda8559638e");
    
    private static final String RECOIL_NAME = "tcc.evil_accuracy.recoil";
    private static final String ROUNDS_PER_MINUTE_NAME = "tcc.evil_accuracy.rounds_per_minute";
    
    public EvilAccuracy(Properties properties) {
        super(properties);
    }
    
    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        if (matchesRestriction(livingEntity)) {
            double recoilReduction = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.evilAccuracyRecoilReduction.get());
            double fireRateReduction = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.evilAccuracyFireRateReduction.get());
            
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.RECOIL, recoilReduction, RECOIL_UUID, RECOIL_NAME, AttributeModifier.Operation.ADDITION);
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.ROUNDS_PER_MINUTE, fireRateReduction, ROUNDS_PER_MINUTE_UUID, ROUNDS_PER_MINUTE_NAME, AttributeModifier.Operation.ADDITION);
        }
    }
    
    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.RECOIL, RECOIL_UUID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.ROUNDS_PER_MINUTE, ROUNDS_PER_MINUTE_UUID);
    }
    
    @Override
    public java.util.List<String> getWeaponTypeRestriction() {
        return GunTypeChecker.ALL_GUN_TYPES_LIST;
    }
    

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        
        tooltip.add(Component.literal(""));
        
        double recoilReduction = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.evilAccuracyRecoilReduction.get() ) * 100;
        double fireRateReduction = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.evilAccuracyFireRateReduction.get() ) * 100;
        tooltip.add(Component.translatable("item.tcc.evil_accuracy.effect", 
                String.format("%+.0f", recoilReduction), String.format("%+.0f", fireRateReduction))
            .withStyle(ChatFormatting.GOLD));
        
        tooltip.add(Component.literal(""));
        
    }
    
}