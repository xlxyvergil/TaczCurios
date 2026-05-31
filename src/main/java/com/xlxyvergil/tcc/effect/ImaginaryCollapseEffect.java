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
 */
public class ImaginaryCollapseEffect extends MobEffect {

    public ImaginaryCollapseEffect() {
        super(MobEffectCategory.HARMFUL, 0x4B0082);
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return new ArrayList<>();
    }

    /**
     * 返回true以绕过Goety Apostle等boss的负面效果拦截。
     * Apostle的addEffect()仅允许isBeneficial()=true的效果。
     */
    @Override
    public boolean isBeneficial() {
        return true;
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

        // 统计目标身上的负面效果数量
        int debuffCount = 0;
        for (MobEffectInstance instance : entity.getActiveEffects()) {
            if (instance.getEffect().getCategory() == MobEffectCategory.HARMFUL) {
                debuffCount++;
            }
        }

        double percentPerLevel = TaczCuriosConfig.COMMON.collapsePercentPerLevel.get();
        int maxDebuff = TaczCuriosConfig.COMMON.collapseMaxDebuffCount.get();
        double percentPerDebuff = TaczCuriosConfig.COMMON.collapsePercentPerDebuff.get();

        int effectiveDebuffs = Math.min(debuffCount, maxDebuff);
        double debuffMultiplier = 1.0 + effectiveDebuffs * percentPerDebuff;

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