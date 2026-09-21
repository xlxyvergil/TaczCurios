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
public class DeadlySurge extends TccCurioItem {
    private static final ResourceLocation ROUNDS_PER_MINUTE_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "deadly_surge_d8e4852c_ae93");
    private static final ResourceLocation BULLET_COUNT_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "deadly_surge_b00e1320_91fc");


    public DeadlySurge(Properties properties) {
        super(properties);
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        // 登记该饰品施加的修饰符 ID → 来源饰品，供客户端属性面板显示来源图标。
        AttributeHelper.registerSourceItem(ROUNDS_PER_MINUTE_ID, stack.getItem());
        AttributeHelper.registerSourceItem(BULLET_COUNT_ID, stack.getItem());
        if (matchesRestriction(livingEntity)) {
            double roundsPerMinuteBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.deadlySurgeFireRateBoost.get());
            double bulletCountBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.deadlySurgeBulletCountBoost.get());
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.ROUNDS_PER_MINUTE, roundsPerMinuteBoost, ROUNDS_PER_MINUTE_ID, AttributeModifier.Operation.ADD_VALUE);
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.BULLET_COUNT, bulletCountBoost, BULLET_COUNT_ID, AttributeModifier.Operation.ADD_VALUE);
        }
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.ROUNDS_PER_MINUTE, ROUNDS_PER_MINUTE_ID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.BULLET_COUNT, BULLET_COUNT_ID);
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

        double roundsPerMinuteBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.deadlySurgeFireRateBoost.get() ) * 100;
        double bulletCountBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.deadlySurgeBulletCountBoost.get() ) * 100;
        tooltip.add(Component.translatable("item.tcc.deadly_surge.effect", String.format("%+.0f", roundsPerMinuteBoost), String.format("%+.0f", bulletCountBoost))
            .withStyle(ChatFormatting.GOLD));

        tooltip.add(Component.literal(""));

    }
    
}