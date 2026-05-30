package com.xlxyvergil.tcc.effect;

import com.xlxyvergil.tcc.registries.TccMobEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

/**
 * 天火劫灭延迟标记 - 用于在扣血后延迟施加流血效果
 * 持续时间可配置(默认3秒), 最后1秒时施加天火流血
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
        
        // 剩余时间 ≤ 1秒(20tick)时施加天火流血
        if (remainingDuration <= 20 && remainingDuration > 0) {
            entity.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                TccMobEffects.HEAVEN_FIRE_BLEEDING.get(),
                200,  // 流血持续10秒(200tick)
                0,    // 固定0级(显示为1级)
                false,
                false,
                true
            ));
            
            entity.removeEffect(this);
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
}
