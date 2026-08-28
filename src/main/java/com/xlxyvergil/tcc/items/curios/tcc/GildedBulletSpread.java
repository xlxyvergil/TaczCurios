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
 * 镀层弹头扩散：基础弹头数量+110%
 * 击杀→Buff额外+30%弹头数量持续10s，可叠加4层
 */
public class GildedBulletSpread extends TccCurioItem {

    private static final UUID BASE_BULLET_COUNT_UUID = UUID.fromString("55457bc2-72cb-42d8-8918-b9854105386c");
    private static final String BASE_BULLET_COUNT_NAME = "tcc.gilded_bullet_spread.base_bullet_count";

    public GildedBulletSpread(Properties properties) {
        super(properties);
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        if (matchesRestriction(livingEntity)) {
            double baseBulletCount = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.gildedBulletSpreadBulletCountBase.get());
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.BULLET_COUNT, baseBulletCount, BASE_BULLET_COUNT_UUID, BASE_BULLET_COUNT_NAME, AttributeModifier.Operation.MULTIPLY_BASE);
        } else {
            AttributeHelper.removeModifier(livingEntity, AttributeHelper.BULLET_COUNT, BASE_BULLET_COUNT_UUID);
        }
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.BULLET_COUNT, BASE_BULLET_COUNT_UUID);
    }

    @Override
    public java.util.List<String> getWeaponTypeRestriction() {
        return java.util.List.of("pistol");
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.literal(""));
        double baseBulletCount = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.gildedBulletSpreadBulletCountBase.get()) * 100;
        int fusionLevel = FusionData.from(stack).level();
        double buffBulletCount = TaczCuriosConfig.COMMON.gildedBulletSpreadBulletCountPerLevel.get() * 100 * (fusionLevel + 1);
        int duration = TaczCuriosConfig.COMMON.gildedBulletSpreadDuration.get();
        int maxStacks = TaczCuriosConfig.COMMON.gildedBulletSpreadMaxStacks.get() / TaczCuriosConfig.COMMON.fusionMaxLevelEpic.get();
        tooltip.add(Component.translatable("item.tcc.gilded_bullet_spread.effect_base",
                String.format("%+.0f", baseBulletCount))
            .withStyle(ChatFormatting.WHITE));
        tooltip.add(Component.translatable("item.tcc.gilded_bullet_spread.effect_kill",
                String.format("%+.0f", buffBulletCount), maxStacks, duration)
            .withStyle(ChatFormatting.WHITE));
        tooltip.add(Component.literal(""));
        
    }


}
