package com.xlxyvergil.tcc.effect;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.core.TccDamageSources;
import com.xlxyvergil.tcc.event.HeavenFireBleedingSettlementEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * 天火流血效果 - 基于最大生命值的百分比伤害
 * 每级造成配置比例的 maxHP 伤害，每2秒触发一次
 */
public class HeavenFireBleedingEffect extends MobEffect {

    public HeavenFireBleedingEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFF4500); // 橙红色
        MinecraftForge.EVENT_BUS.register(this);
    }

    
    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        // 获取配置的每级伤害比例（负值，需要取反）
        double damagePerLevel = -TaczCuriosConfig.COMMON.heavenFireBleedingDamagePerLevel.get();
        
        // 计算实际伤害：maxHP * 伤害比例 * (等级+1)
        float maxHealth = entity.getMaxHealth();
        float damage = (float) (maxHealth * damagePerLevel * (amplifier + 1));
        
        // 直接造成伤害，可触发不死图腾且绕过一切防御
        DamageSource imaginarySource = TccDamageSources.imaginaryDamage(entity);
        entity.hurt(imaginarySource, damage);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        // 每40 tick（2秒）触发一次扣血
        return duration % 40 == 0;
    }
    
    @SubscribeEvent
    public void onExpired(MobEffectEvent.Expired event) {
        if (event.getEffectInstance().getEffect() != this) {
            return;
        }
        
        LivingEntity entity = event.getEntity();
        boolean isDead = entity.isDeadOrDying();
        
        // 发布结算事件
        HeavenFireBleedingSettlementEvent settlementEvent = new HeavenFireBleedingSettlementEvent(entity, isDead);
        MinecraftForge.EVENT_BUS.post(settlementEvent);
    }
}
