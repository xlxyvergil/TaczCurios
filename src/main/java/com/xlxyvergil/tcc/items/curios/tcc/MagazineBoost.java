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
public class MagazineBoost extends TccCurioItem {
    private static final ResourceLocation MAGAZINE_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "magazine_boost_1b4da84f_d6ea");


    public MagazineBoost(Properties properties) {
        super(properties
            .stacksTo(1));
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        // 登记该饰品施加的修饰符 ID → 来源饰品，供客户端属性面板显示来源图标。
        AttributeHelper.registerSourceItem(MAGAZINE_ID, stack.getItem());
        if (matchesRestriction(livingEntity)) {
            double magazineBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.magazineBoostCapacityBoost.get());
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.MAGAZINE_CAPACITY, magazineBoost, MAGAZINE_ID, AttributeModifier.Operation.ADD_VALUE);
        }
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.MAGAZINE_CAPACITY, MAGAZINE_ID);
    }


    @Override
    public java.util.List<String> getWeaponTypeRestriction() {
        return java.util.List.of("rifle", "sniper", "smg", "mg", "rpg");
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        Level level = context.level();
        super.appendHoverText(stack, context, tooltip, flag);

        tooltip.add(Component.literal(""));

        double magazineBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.magazineBoostCapacityBoost.get() ) * 100;
        tooltip.add(Component.translatable("item.tcc.magazine_boost.effect", String.format("%+.0f", magazineBoost))
            .withStyle(ChatFormatting.BLUE));

        tooltip.add(Component.literal(""));

    }
    
}