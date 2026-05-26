package com.xlxyvergil.tcc.effect;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.core.TccDamageSources;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

/**
 * 虚数流血效果 - 基于最大生命值的百分比伤害
 * 每级造成配置比例的 maxHP 伤害，每2秒触发一次
 * 虚数伤害会根据目标的流血层数增伤
 */
public class ImaginaryBleedingEffect extends MobEffect {

    public ImaginaryBleedingEffect() {
        super(MobEffectCategory.HARMFUL, 0x8B0000); // 深红色
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        // 获取配置的每级伤害比例（负值，需要取反）
        double damagePerLevel = -TaczCuriosConfig.COMMON.imaginaryBleedingDamagePerLevel.get();
        
        // 计算实际伤害：maxHP * 伤害比例 * (等级+1)
        float maxHealth = entity.getMaxHealth();
        float damage = (float) (maxHealth * damagePerLevel * (amplifier + 1));
        
        // 使用虚数伤害源，可触发不死图腾且绕过一切防御
        DamageSource imaginarySource = TccDamageSources.imaginaryDamage(entity);
        entity.hurt(imaginarySource, damage);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        // 每40 tick（2秒）触发一次
        return duration % 40 == 0;
    }
}
