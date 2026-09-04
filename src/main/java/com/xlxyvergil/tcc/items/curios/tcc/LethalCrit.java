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

public class LethalCrit extends TccCurioItem {
    private static final UUID CRIT_CHANCE_UUID = UUID.fromString("632a84b6-dcca-41e3-8746-92d783c0d0fe");

    private static final String CRIT_CHANCE_NAME = "tcc.lethal_crit.crit_chance";

    public LethalCrit(Properties properties) {
        super(properties);
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        // 登记该饰品施加的修饰符 UUID → 来源饰品，供客户端属性面板显示来源图标。
        AttributeHelper.registerSourceItem(CRIT_CHANCE_UUID, stack.getItem());
        if (matchesRestriction(livingEntity)) {
            double critChanceBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.lethalCritCritChance.get());
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.CRIT_CHANCE, critChanceBoost, CRIT_CHANCE_UUID, CRIT_CHANCE_NAME, AttributeModifier.Operation.MULTIPLY_BASE);
        } else {
            AttributeHelper.removeModifier(livingEntity, AttributeHelper.CRIT_CHANCE, CRIT_CHANCE_UUID);
        }
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.CRIT_CHANCE, CRIT_CHANCE_UUID);
    }

    @Override
    public java.util.List<String> getWeaponTypeRestriction() {
        return java.util.List.of("rifle", "sniper", "smg", "mg", "rpg");
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        tooltip.add(Component.literal(""));

        double critChanceBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.lethalCritCritChance.get() ) * 100;
        tooltip.add(Component.translatable("item.tcc.lethal_crit.effect",
                String.format("%+.0f", critChanceBoost))
            .withStyle(ChatFormatting.BLUE));

        tooltip.add(Component.literal(""));
        
    }

}
