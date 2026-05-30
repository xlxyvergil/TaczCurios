package com.xlxyvergil.tcc.effect;

import com.xlxyvergil.tcc.core.TccAttributes;
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
 * 每tick对目标造成 0.5%最大生命值 × 虚数侵染等级 的伤害，
 * 虚数抗性以 1:1 的比例降低此伤害。
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

        float baseDamage = entity.getMaxHealth() * 0.005F;
        float scaledDamage = baseDamage * infectionLevel;

        double resistance = entity.getAttributeValue(TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get());
        float finalDamage = Math.max(0, scaledDamage - (float) resistance);

        if (finalDamage > 0) {
            // 直接扣除生命值，绕过 hurt() 方法。
            // hurt() 无法对 Apostle 等具有自定义无敌机制（moddedInvul）的实体造成伤害。
            // 死亡由 LivingEntity.aiStep() 中 getHealth() <= 0 的检查处理。
            entity.setHealth(Math.max(0, entity.getHealth() - finalDamage));
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % 20 == 0;
    }
}
