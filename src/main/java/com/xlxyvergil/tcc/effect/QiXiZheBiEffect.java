package com.xlxyvergil.tcc.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

/**
 * 气息遮蔽。效果本身只是标记，逻辑分别在 QiXiZheBiHandler（脱战、破隐）、
 * QiXiZheBiMixin（isVisible、护甲侦测）和 QiXiZheBiRenderHandler（渲染）里。
 */
public class QiXiZheBiEffect extends MobEffect {

    public QiXiZheBiEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x6E7FA8);
    }

    // 1.21.1 起 Forge 的 isDurationEffectTick 更名为 shouldApplyEffectTickThisTick；
    // 本效果无可 tick 逻辑，恒返回 false。
    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return false;
    }
}
