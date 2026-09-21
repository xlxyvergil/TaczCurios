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
import com.xlxyvergil.tcc.util.GunTypeChecker;


import net.minecraft.resources.ResourceLocation;
import com.xlxyvergil.tcc.TaczCurios;
public class AlloyDrill extends TccCurioItem {
    // 属性修饰符UUID - 用于唯一标识这些修饰符
    private static final ResourceLocation ARMOR_IGNORE_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "alloy_drill_06d45b6d_2366");
    
    // 修饰符名称
    
    public AlloyDrill(Properties properties) {
        super(properties);
    }
    

    
    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        // 登记该饰品施加的修饰符 ID → 来源饰品，供客户端属性面板显示来源图标。
        AttributeHelper.registerSourceItem(ARMOR_IGNORE_ID, stack.getItem());
        if (matchesRestriction(livingEntity)) {
            double armorIgnoreBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.alloyDrillArmorPenetrationBoost.get());
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.ARMOR_IGNORE, armorIgnoreBoost, ARMOR_IGNORE_ID, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        }
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.ARMOR_IGNORE, ARMOR_IGNORE_ID);
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

        double armorIgnoreBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.alloyDrillArmorPenetrationBoost.get() ) * 100;
        tooltip.add(Component.translatable("item.tcc.alloy_drill.effect", String.format("%+.0f", armorIgnoreBoost))
            .withStyle(ChatFormatting.GOLD));

        tooltip.add(Component.literal(""));

    }
    
}