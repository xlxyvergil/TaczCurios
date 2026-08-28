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
 * 剑风：提升实体交互范围（加算）
 */
public class SwordWind extends TccCurioItem {
    
    private static final UUID ENTITY_INTERACTION_RANGE_UUID = UUID.fromString("3f7ed736-62d3-4835-bc94-2834d4b91832");
    
    private static final String ENTITY_INTERACTION_RANGE_NAME = "tcc.sword_wind.entity_interaction_range";
    
    // 加成数值现从配置文件读取
    
    public SwordWind(Properties properties) {
        super(properties);
    }
    

    
    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        if (matchesRestriction(livingEntity)) {
            double rangeBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.swordWindMeleeRangeBoost.get());
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.ENTITY_REACH, rangeBoost, ENTITY_INTERACTION_RANGE_UUID, ENTITY_INTERACTION_RANGE_NAME, AttributeModifier.Operation.ADDITION);
        }
    }
    
    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.ENTITY_REACH, ENTITY_INTERACTION_RANGE_UUID);
    }
    
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        tooltip.add(Component.literal(""));

        double meleeDistanceBoost = TaczCuriosConfig.COMMON.swordWindMeleeRangeBoost.get();
        tooltip.add(Component.translatable("item.tcc.sword_wind.effect", String.format("%.1f", meleeDistanceBoost))
            .withStyle(ChatFormatting.BLUE));

        tooltip.add(Component.literal(""));

    }

    @Override
    public java.util.List<String> getWeaponTypeRestriction() {
        return java.util.List.of("melee");
    }
}

