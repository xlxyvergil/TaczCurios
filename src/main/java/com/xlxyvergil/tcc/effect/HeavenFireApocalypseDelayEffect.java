package com.xlxyvergil.tcc.effect;

import com.xlxyvergil.tcc.registries.TccMobEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

/**
 * 天火劫灭延迟标记 - 用于在扣血后延迟施加流血效果
 * 持续6秒,在第5秒时施加天火流血效果(固定1级)
 */
public class HeavenFireApocalypseDelayEffect extends MobEffect {

    public HeavenFireApocalypseDelayEffect() {
        super(MobEffectCategory.NEUTRAL, 0x000000); // 中性效果,黑色(实际不显示)
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        // 获取剩余持续时间
        var effectInstance = entity.getEffect(this);
        if (effectInstance == null) return;
        
        int remainingDuration = effectInstance.getDuration();
        
        // 在第5秒时(剩余duration约等于20tick时)施加天火流血
        // 总持续时间为6秒(120tick),第5秒即剩余约20tick时触发
        if (remainingDuration <= 20 && remainingDuration > 0) {
            // 施加固定1级的天火流血效果
            entity.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                TccMobEffects.HEAVEN_FIRE_BLEEDING.get(),
                200,  // 流血持续10秒(200tick)
                0,    // 固定0级(显示为1级)
                false,  // 不是药水
                false,  // 不显示粒子
                true    // 显示图标
            ));
            
            // 移除自身标记效果
            entity.removeEffect(this);
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        // 每tick都检查,确保准确在第5秒触发
        return true;
    }
}
