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

public class MagazineBoost extends TccCurioItem {
    private static final UUID MAGAZINE_UUID = UUID.fromString("1b4da84f-2d2a-44e4-bed2-986811a6d6ea");

    private static final String MAGAZINE_NAME = "tcc.magazine_boost.magazine_capacity";

    public MagazineBoost(Properties properties) {
        super(properties
            .stacksTo(1));
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        // 登记该饰品施加的修饰符 UUID → 来源饰品，供客户端属性面板显示来源图标。
        AttributeHelper.registerSourceItem(MAGAZINE_UUID, stack.getItem());
        if (matchesRestriction(livingEntity)) {
            double magazineBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.magazineBoostCapacityBoost.get());
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.MAGAZINE_CAPACITY, magazineBoost, MAGAZINE_UUID, MAGAZINE_NAME, AttributeModifier.Operation.ADDITION);
        }
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.MAGAZINE_CAPACITY, MAGAZINE_UUID);
    }


    @Override
    public java.util.List<String> getWeaponTypeRestriction() {
        return java.util.List.of("rifle", "sniper", "smg", "mg", "rpg");
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        tooltip.add(Component.literal(""));

        double magazineBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.magazineBoostCapacityBoost.get() ) * 100;
        tooltip.add(Component.translatable("item.tcc.magazine_boost.effect", String.format("%+.0f", magazineBoost))
            .withStyle(ChatFormatting.BLUE));

        tooltip.add(Component.literal(""));

    }
    
}