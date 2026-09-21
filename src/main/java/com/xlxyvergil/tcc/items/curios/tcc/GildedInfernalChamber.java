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
public class GildedInfernalChamber extends TccCurioItem {
    private static final ResourceLocation BASE_BULLET_COUNT_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "gilded_infernal_chamber_e109d5c2_cfc1");

    public GildedInfernalChamber(Properties properties) {
        super(properties);
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        // 登记该饰品施加的修饰符 ID → 来源饰品，供客户端属性面板显示来源图标。
        AttributeHelper.registerSourceItem(BASE_BULLET_COUNT_ID, stack.getItem());
        if (matchesRestriction(livingEntity)) {
            double baseBulletCount = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.gildedInfernalChamberBulletCountBase.get());
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.BULLET_COUNT, baseBulletCount, BASE_BULLET_COUNT_ID, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        } else {
            AttributeHelper.removeModifier(livingEntity, AttributeHelper.BULLET_COUNT, BASE_BULLET_COUNT_ID);
        }
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.BULLET_COUNT, BASE_BULLET_COUNT_ID);
    }

    @Override
    public java.util.List<String> getWeaponTypeRestriction() {
        return java.util.List.of("shotgun");
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        Level level = context.level();
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.literal(""));
        double baseBulletCount = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.gildedInfernalChamberBulletCountBase.get()) * 100;
        int fusionLevel = FusionData.from(stack).level();
        double buffBulletCount = TaczCuriosConfig.COMMON.gildedInfernalChamberBulletCountPerLevel.get() * 100 * (fusionLevel + 1);
        int duration = TaczCuriosConfig.COMMON.gildedInfernalChamberDuration.get();
        int maxStacks = TaczCuriosConfig.COMMON.gildedInfernalChamberMaxStacks.get() / TaczCuriosConfig.COMMON.fusionMaxLevelEpic.get();
        tooltip.add(Component.translatable("item.tcc.gilded_infernal_chamber.effect_base",
                String.format("%+.0f", baseBulletCount))
            .withStyle(ChatFormatting.WHITE));
        tooltip.add(Component.translatable("item.tcc.gilded_infernal_chamber.effect_kill",
                String.format("%+.0f", buffBulletCount), maxStacks, duration)
            .withStyle(ChatFormatting.WHITE));
        tooltip.add(Component.literal(""));
        
    }


}
