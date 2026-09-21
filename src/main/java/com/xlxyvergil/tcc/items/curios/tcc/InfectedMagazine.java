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
public class InfectedMagazine extends TccCurioItem {
    // 属性修饰符UUID - 用于唯一标识修饰
    private static final ResourceLocation MAGAZINE_CAPACITY_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "infected_magazine_f7d6ce3b_0fbc");
    private static final ResourceLocation RELOAD_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "infected_magazine_fa325acb_242c");

    // 修饰符名

    public InfectedMagazine(Properties properties) {
        super(properties);
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        // 登记该饰品施加的修饰符 ID → 来源饰品，供客户端属性面板显示来源图标。
        AttributeHelper.registerSourceItem(MAGAZINE_CAPACITY_ID, stack.getItem());
        AttributeHelper.registerSourceItem(RELOAD_ID, stack.getItem());
        if (matchesRestriction(livingEntity)) {
            double magazineCapacityBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.infectedMagazineCapacityBoost.get());
            double reloadDebuff = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.infectedMagazineReloadSpeedReduction.get());

            AttributeHelper.applyModifier(livingEntity, AttributeHelper.MAGAZINE_CAPACITY, magazineCapacityBoost, MAGAZINE_CAPACITY_ID, AttributeModifier.Operation.ADD_VALUE);
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.RELOAD_TIME, reloadDebuff, RELOAD_ID, AttributeModifier.Operation.ADD_VALUE);
        }
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.MAGAZINE_CAPACITY, MAGAZINE_CAPACITY_ID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.RELOAD_TIME, RELOAD_ID);
    }


    @Override
    public java.util.List<String> getWeaponTypeRestriction() {
        return java.util.List.of("pistol");
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        Level level = context.level();
        super.appendHoverText(stack, context, tooltip, flag);

        tooltip.add(Component.literal(""));

        double magazineCapacityBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.infectedMagazineCapacityBoost.get() ) * 100;
        double reloadDebuff = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.infectedMagazineReloadSpeedReduction.get() ) * 100;
        tooltip.add(Component.translatable("item.tcc.infected_magazine.effect", 
                String.format("%+.0f", magazineCapacityBoost), String.format("%+.0f", reloadDebuff))
            .withStyle(ChatFormatting.GOLD));

        tooltip.add(Component.literal(""));

    }
    
}