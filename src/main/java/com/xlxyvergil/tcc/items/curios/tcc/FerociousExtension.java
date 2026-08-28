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
 * 凶恶延伸：提高子弹射程（乘算）
 */
public class FerociousExtension extends TccCurioItem {
    
    private static final UUID EFFECTIVE_RANGE_UUID = UUID.fromString("2774a4d4-b53c-4799-bb4c-fd7dc117264f");
    
    private static final String EFFECTIVE_RANGE_NAME = "tcc.ferocious_extension.effective_range";
    
    public FerociousExtension(Properties properties) {
        super(properties);
    }
    
    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        if (matchesRestriction(livingEntity)) {
            double rangeBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.ferociousExtensionRangeBoost.get());
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.EFFECTIVE_RANGE, rangeBoost, EFFECTIVE_RANGE_UUID, EFFECTIVE_RANGE_NAME, AttributeModifier.Operation.MULTIPLY_BASE);
        }
    }
    
    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.EFFECTIVE_RANGE, EFFECTIVE_RANGE_UUID);
    }
    
    @Override
    public java.util.List<String> getWeaponTypeRestriction() {
        return GunTypeChecker.ALL_GUN_TYPES_LIST;
    }
    

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        
        tooltip.add(Component.literal(""));
        
        double rangeBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.ferociousExtensionRangeBoost.get() ) * 100;
        tooltip.add(Component.translatable("item.tcc.ferocious_extension.effect", String.format("%+.0f", rangeBoost))
            .withStyle(ChatFormatting.AQUA));
        
        tooltip.add(Component.literal(""));
        
    }
    

}