package com.xlxyvergil.tcc.effect;

import com.xlxyvergil.tcc.registries.TccMobEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

/**
 * 天火劫灭延迟标记：扣血后延迟施加流血，最后 1 秒时施加天火流血。
 */
public class HeavenFireApocalypseDelayEffect extends MobEffect {

    public HeavenFireApocalypseDelayEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFF8800);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        var effectInstance = entity.getEffect(this);
        if (effectInstance == null) return;
        
        int remainingDuration = effectInstance.getDuration();
        
        // 剩余时间恰好 1 秒（20 tick）时施加天火流血（每轮只施加一次）。
        // 不能用 <= 20 并依赖 removeEffect 移除自身：CoreEffectProtectionHandler 会阻止本效果被移除，
        // 否则最后 1 秒内每 tick 重复施加流血并刷新持续时间为 200，使流血伤害每 tick 触发。
        if (remainingDuration == 20) {
            entity.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                TccMobEffects.HEAVEN_FIRE_BLEEDING.get(),
                200,  // 流血持续10秒(200tick)
                0,    // 固定0级(显示为1级)
                false,
                false,
                true
            ));
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
}
