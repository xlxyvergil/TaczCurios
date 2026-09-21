package com.xlxyvergil.tcc.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.EffectCure;

import java.util.Set;

/**
 * 侵蚀标记：虚数崩解结算负面数量增益时据此判定。
 */
public class ErosionEffect extends MobEffect {

    public ErosionEffect() {
        super(MobEffectCategory.NEUTRAL, 0x6A0DAD);
    }

    /**
     * 不提供任何 cure，阻止 Goety/Warlock/Codger 等 boss 的剥离机制。
     */
    @Override
    public void fillEffectCures(Set<EffectCure> cures, MobEffectInstance effectInstance) {
        // 留空即表示不可被任何 cure 解除
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        // 纯标记效果，无 tick 逻辑
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return false;
    }
}
