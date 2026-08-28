package com.xlxyvergil.tcc.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

/**
 * 真我结界标记：无实际效果，仅由服务端在结界激活期间施加/续期（时长 = 结界剩余 tick）；
 * 客户端据此在本地玩家位置渲染脚下地面特效。
 */
public class ZhenWoBarrierEffect extends MobEffect {

    public ZhenWoBarrierEffect() {
        super(MobEffectCategory.NEUTRAL, 0xFF8CCC);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return false;
    }
}
