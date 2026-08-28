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
import com.xlxyvergil.tcc.util.GunTypeChecker;
import java.util.UUID;


/**
 * 合金钻头：提升护甲穿透能力
 */
public class AlloyDrill extends TccCurioItem {
    
    // 属性修饰符UUID - 用于唯一标识这些修饰符
    private static final UUID ARMOR_IGNORE_UUID = UUID.fromString("06d45b6d-c8d2-4372-bdfd-b427651a2366");
    
    // 修饰符名称
    private static final String ARMOR_IGNORE_NAME = "tcc.alloy_drill.armor_ignore";
    
    public AlloyDrill(Properties properties) {
        super(properties);
    }
    

    
    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        if (matchesRestriction(livingEntity)) {
            double armorIgnoreBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.alloyDrillArmorPenetrationBoost.get());
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.ARMOR_IGNORE, armorIgnoreBoost, ARMOR_IGNORE_UUID, ARMOR_IGNORE_NAME, AttributeModifier.Operation.MULTIPLY_BASE);
        }
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.ARMOR_IGNORE, ARMOR_IGNORE_UUID);
    }

    @Override
    public java.util.List<String> getWeaponTypeRestriction() {
        return GunTypeChecker.ALL_GUN_TYPES_LIST;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        tooltip.add(Component.literal(""));

        double armorIgnoreBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.alloyDrillArmorPenetrationBoost.get() ) * 100;
        tooltip.add(Component.translatable("item.tcc.alloy_drill.effect", String.format("%+.0f", armorIgnoreBoost))
            .withStyle(ChatFormatting.GOLD));

        tooltip.add(Component.literal(""));

    }
    
}