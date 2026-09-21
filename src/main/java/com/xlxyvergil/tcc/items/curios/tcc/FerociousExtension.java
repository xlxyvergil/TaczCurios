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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.resources.ResourceLocation;
import com.xlxyvergil.tcc.TaczCurios;
public class FerociousExtension extends TccCurioItem {
    private static final ResourceLocation EFFECTIVE_RANGE_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "ferocious_extension_2774a4d4_264f");
    
    
    public FerociousExtension(Properties properties) {
        super(properties);
    }
    
    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        // 登记该饰品施加的修饰符 ID → 来源饰品，供客户端属性面板显示来源图标。
        AttributeHelper.registerSourceItem(EFFECTIVE_RANGE_ID, stack.getItem());
        if (matchesRestriction(livingEntity)) {
            double rangeBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.ferociousExtensionRangeBoost.get());
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.EFFECTIVE_RANGE, rangeBoost, EFFECTIVE_RANGE_ID, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        }
    }
    
    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.EFFECTIVE_RANGE, EFFECTIVE_RANGE_ID);
    }
    
    @Override
    public java.util.List<String> getWeaponTypeRestriction() {
        return GunTypeChecker.ALL_GUN_TYPES_LIST;
    }
    

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        Level level = context.level();
        super.appendHoverText(stack, context, tooltip, flag);
        
        tooltip.add(Component.literal(""));
        
        double rangeBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.ferociousExtensionRangeBoost.get() ) * 100;
        tooltip.add(Component.translatable("item.tcc.ferocious_extension.effect", String.format("%+.0f", rangeBoost))
            .withStyle(ChatFormatting.AQUA));
        
        tooltip.add(Component.literal(""));
        
    }
    

}