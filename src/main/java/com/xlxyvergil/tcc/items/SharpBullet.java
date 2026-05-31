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
 * 尖刃弹头 - 步枪饰品（击杀触发Buff）
 * 基础：暴击伤害+120%，击杀→Buff期间暴击伤害提升（9s，不叠加）
 */
public class SharpBullet extends BaseCurioItem {

    private static final UUID BASE_CRIT_DMG_UUID = UUID.fromString("b1c2d3e4-7005-4000-8000-000000000001");
    private static final String BASE_CRIT_DMG_NAME = "tcc.sharp_bullet.base_crit_damage";

    public SharpBullet(Properties properties) {
        super(properties);
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity) {
        if (GunTypeChecker.isHoldingDmgBoostGunType(livingEntity)) {
            double baseCritDmg = TaczCuriosConfig.COMMON.sharpBulletBaseCritDamage.get();
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.CRIT_DAMAGE, baseCritDmg, BASE_CRIT_DMG_UUID, BASE_CRIT_DMG_NAME, AttributeModifier.Operation.MULTIPLY_BASE);
        } else {
            AttributeHelper.removeModifier(livingEntity, AttributeHelper.CRIT_DAMAGE, BASE_CRIT_DMG_UUID);
        }
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.CRIT_DAMAGE, BASE_CRIT_DMG_UUID);
    }

    @Override
    public void curioTick(top.theillusivec4.curios.api.SlotContext slotContext, ItemStack stack) {
        applyEffects(slotContext.entity());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.literal(""));
        double baseCritDmg = TaczCuriosConfig.COMMON.sharpBulletBaseCritDamage.get() * 100;
        double buffCritDmg = TaczCuriosConfig.COMMON.sharpBulletCritDamagePerLevel.get() * 100;
        int duration = TaczCuriosConfig.COMMON.sharpBulletDuration.get();
        tooltip.add(Component.translatable("item.tcc.sharp_bullet.effect",
                String.format("%+.0f", baseCritDmg), String.format("%+.0f", buffCritDmg), duration)
            .withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable("tcc.tooltip.slot"));
        tooltip.add(Component.translatable("tcc.tooltip.rarity.uncommon"));
    }

    @Override
    public void applyGunSwitchEffect(LivingEntity livingEntity) {
        applyEffects(livingEntity);
    }
}
