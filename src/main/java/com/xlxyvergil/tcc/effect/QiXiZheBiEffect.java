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

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return false;
    }
}
