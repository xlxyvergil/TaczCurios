package com.xlxyvergil.tcc.effect;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.AttributeHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

/**
 * 镀层氩晶瞄具击杀Buff - 爆头击杀触发，暴击几率叠加（最多60层）
 */
public class GildedArgonScopeKillEffect extends MobEffect {
    public GildedArgonScopeKillEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFFBB44);
        double perLevelValue = TaczCuriosConfig.COMMON.gildedArgonScopeCritChancePerLevel.get();
        this.addAttributeModifier(AttributeHelper.CRIT_CHANCE,
            "3da691c2-0f73-48cc-938a-d8c9152ed1ee", perLevelValue, AttributeModifier.Operation.MULTIPLY_BASE);
    }

}
