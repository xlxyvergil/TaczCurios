package com.xlxyvergil.tcc.effect;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.attribute.TccAttributes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

/**
 * 怪物虚数抗性Buff - 非玩家实体生成时自带的正向效果。
 * 为实体提供可配置的虚数抗性加成，持续9999小时。
 */
public class MobBuffEffect extends MobEffect {

    private static final String RESISTANCE_UUID = "a0b1c2d3-e4f5-6789-abcd-ef0123456789";

    public MobBuffEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x9370DB);
        // 占位值，实际值由 getAttributeModifierValue 动态返回
        this.addAttributeModifier(
            TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get(),
            RESISTANCE_UUID,
            0,
            AttributeModifier.Operation.ADDITION
        );
    }

    @Override
    public double getAttributeModifierValue(int amplifier, AttributeModifier modifier) {
        return TaczCuriosConfig.COMMON.mobBuffImaginaryResistance.get();
    }
}
