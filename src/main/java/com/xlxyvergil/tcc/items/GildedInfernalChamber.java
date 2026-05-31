package com.xlxyvergil.tcc.items;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.util.BaseCurioItem;
import com.xlxyvergil.tcc.util.GunTypeChecker;

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
 * 镀层地狱弹膛 - 霰弹枪饰品（击杀触发Buff，可叠加）
 * 基础：弹头数量+110%，击杀→Buff额外+30%弹头数量（20s，可叠加5层）
 */
public class GildedInfernalChamber extends BaseCurioItem {

    private static final UUID BASE_BULLET_COUNT_UUID = UUID.fromString("b1c2d3e4-7008-4000-8000-000000000001");
    private static final String BASE_BULLET_COUNT_NAME = "tcc.gilded_infernal_chamber.base_bullet_count";

    public GildedInfernalChamber(Properties properties) {
        super(properties);
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity) {
        if (GunTypeChecker.isHoldingShotgun(livingEntity)) {
            double baseBulletCount = TaczCuriosConfig.COMMON.gildedInfernalChamberBulletCountBase.get();
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
    public void curioTick(top.theillusivec4.curios.api.SlotContext slotContext, ItemStack stack) {
        applyEffects(slotContext.entity());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.literal(""));
        double baseBulletCount = TaczCuriosConfig.COMMON.gildedInfernalChamberBulletCountBase.get() * 100;
        double buffBulletCount = TaczCuriosConfig.COMMON.gildedInfernalChamberBulletCountPerLevel.get() * 100;
        int duration = TaczCuriosConfig.COMMON.gildedInfernalChamberDuration.get();
        int maxStacks = TaczCuriosConfig.COMMON.gildedInfernalChamberMaxStacks.get();
        tooltip.add(Component.translatable("item.tcc.gilded_infernal_chamber.effect",
                String.format("%+.0f", baseBulletCount), String.format("%+.0f", buffBulletCount), duration, maxStacks)
            .withStyle(ChatFormatting.WHITE));
        tooltip.add(Component.literal(""));
        
        tooltip.add(Component.translatable("tcc.tooltip.rarity.epic"));
    }

    @Override
    public void applyGunSwitchEffect(LivingEntity livingEntity) {
        applyEffects(livingEntity);
    }
}
