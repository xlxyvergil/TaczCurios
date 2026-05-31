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
 * 雷射瞄具 - 霰弹枪饰品（爆头触发Buff）
 * 基础：暴击几率+120%，爆头命中→Buff期间暴击几率提升（9s，不叠加）
 */
public class LaserScope extends BaseCurioItem {

    private static final UUID BASE_CRIT_UUID = UUID.fromString("b1c2d3e4-7003-4000-8000-000000000001");
    private static final String BASE_CRIT_NAME = "tcc.laser_scope.base_crit";

    public LaserScope(Properties properties) {
        super(properties);
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity) {
        if (GunTypeChecker.isHoldingShotgun(livingEntity)) {
            double baseCrit = TaczCuriosConfig.COMMON.laserScopeBaseCritChance.get();
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.CRIT_CHANCE, baseCrit, BASE_CRIT_UUID, BASE_CRIT_NAME, AttributeModifier.Operation.MULTIPLY_BASE);
        } else {
            AttributeHelper.removeModifier(livingEntity, AttributeHelper.CRIT_CHANCE, BASE_CRIT_UUID);
        }
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.CRIT_CHANCE, BASE_CRIT_UUID);
    }

    @Override
    public void curioTick(top.theillusivec4.curios.api.SlotContext slotContext, ItemStack stack) {
        applyEffects(slotContext.entity());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.literal(""));
        double baseCrit = TaczCuriosConfig.COMMON.laserScopeBaseCritChance.get() * 100;
        double buffCrit = TaczCuriosConfig.COMMON.laserScopeCritChancePerLevel.get() * 100;
        int duration = TaczCuriosConfig.COMMON.laserScopeDuration.get();
        tooltip.add(Component.translatable("item.tcc.laser_scope.effect",
                String.format("%+.0f", baseCrit), String.format("%+.0f", buffCrit), duration)
            .withStyle(ChatFormatting.AQUA));
        tooltip.add(Component.literal(""));
        
        tooltip.add(Component.translatable("tcc.tooltip.rarity.uncommon"));
    }

    @Override
    public void applyGunSwitchEffect(LivingEntity livingEntity) {
        applyEffects(livingEntity);
    }
}
