package com.xlxyvergil.tcc.effect;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.registries.TccMobEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * 虚数崩解 - 虚数侵染的流血效果。
 * 伤害公式: 目标最大血量 × percentPerLevel × 侵染等级 × (1 + min(debuff数, maxDebuff) × percentPerDebuff)
 * 仅可由天火劫灭/无烬终焉触发。
 * 负面效果数量增益仅在目标同时带有侵蚀效果时生效。
 */
public class ImaginaryCollapseEffect extends MobEffect {

    public ImaginaryCollapseEffect() {
        super(MobEffectCategory.NEUTRAL, 0x4B0082);
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return new ArrayList<>();
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide) return;

        int infectionLevel = 0;
        MobEffectInstance infection = entity.getEffect(TccMobEffects.IMAGINARY_INFECTION.get());
        if (infection != null) {
            infectionLevel = infection.getAmplifier() + 1;
        }
        if (infectionLevel <= 0) return;

        double percentPerLevel = TaczCuriosConfig.COMMON.collapsePercentPerLevel.get();
        double debuffMultiplier = 1.0;

        // 仅当目标带有侵蚀效果时，才统计负面效果数量增益
        if (entity.hasEffect(TccMobEffects.EROSION.get())) {
            int debuffCount = 0;
            for (MobEffectInstance instance : entity.getActiveEffects()) {
                if (instance.getEffect().getCategory() == MobEffectCategory.HARMFUL) {
                    debuffCount++;
                }
            }
            int maxDebuff = TaczCuriosConfig.COMMON.collapseMaxDebuffCount.get();
            double percentPerDebuff = TaczCuriosConfig.COMMON.collapsePercentPerDebuff.get();
            int effectiveDebuffs = Math.min(debuffCount, maxDebuff);
            debuffMultiplier = 1.0 + (1.0 + effectiveDebuffs) * percentPerDebuff;
        }

        float finalDamage = (float) (entity.getMaxHealth() * percentPerLevel * infectionLevel * debuffMultiplier);

        if (finalDamage > 0) {
            entity.setHealth(Math.max(0, entity.getHealth() - finalDamage));
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % 20 == 0;
    }
}