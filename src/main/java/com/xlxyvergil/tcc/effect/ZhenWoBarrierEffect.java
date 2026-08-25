package com.xlxyvergil.tcc.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

/**
 * 逐火之蛾「真我」结界标记效果（中性，纯标记）。
 * <p>
 * 无任何实际效果；由服务端在结界激活期间施加/续期（时长 = 结界剩余 tick），
 * 客户端检测到该效果后以本地玩家位置渲染脚下地面特效（贴图 + 粉色圆环），
 * 特效中心直接取玩家实时位置，无实体插值延迟。
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
