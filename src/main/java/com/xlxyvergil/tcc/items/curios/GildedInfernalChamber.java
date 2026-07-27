package com.xlxyvergil.tcc.items.curios;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.util.BaseCurioItem;
import com.xlxyvergil.tcc.util.FusionUpgradeUtil;
import com.xlxyvergil.tcc.util.GunTypeChecker;
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
 * 镀层地狱弹膛 - 霰弹枪饰品（击杀触发Buff，可叠加
 * 基础：弹头数量+110%，击杀→Buff额外+30%弹头数量持续10s，可叠加5层）
 */
public class GildedInfernalChamber extends BaseCurioItem {

    private static final UUID BASE_BULLET_COUNT_UUID = UUID.fromString("b1c2d3e4-7008-4000-8000-000000000001");
    private static final String BASE_BULLET_COUNT_NAME = "tcc.gilded_infernal_chamber.base_bullet_count";

    public GildedInfernalChamber(Properties properties) {
        super(properties);
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        if (GunTypeChecker.isHoldingShotgun(livingEntity)) {
            double baseBulletCount = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.gildedInfernalChamberBulletCountBase.get());
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
        return java.util.List.of("shotgun");
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.literal(""));
        int fusionLevel = FusionData.from(stack).level();
        double baseBulletCount = TaczCuriosConfig.COMMON.gildedInfernalChamberBulletCountBase.get() * 100 * fusionLevel;
        double buffBulletCount = TaczCuriosConfig.COMMON.gildedInfernalChamberBulletCountPerLevel.get() * 100 * fusionLevel;
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
