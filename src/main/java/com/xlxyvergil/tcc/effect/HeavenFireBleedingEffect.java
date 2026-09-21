package com.xlxyvergil.tcc.effect;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.core.TccDamageSources;
import com.xlxyvergil.tcc.event.HeavenFireBleedingSettlementEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.bus.api.SubscribeEvent;

public class HeavenFireBleedingEffect extends MobEffect {

    public HeavenFireBleedingEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFF4500);
        NeoForge.EVENT_BUS.register(this);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide) return true;
        double damagePerLevel = -TaczCuriosConfig.COMMON.heavenFireBleedingDamagePerLevel.get();
        float maxHealth = entity.getMaxHealth();
        float damage = (float) ((float) Math.round(maxHealth * damagePerLevel * (amplifier + 1) * 10000.0) / 10000.0);
        entity.hurt(TccDamageSources.imaginaryDamage(entity.level(), entity.getLastAttacker()), damage);
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % 40 == 0;
    }

    @SubscribeEvent
    public void onExpired(MobEffectEvent.Expired event) {
        if (event.getEffectInstance().getEffect().value() != this) {
            return;
        }
        LivingEntity entity = event.getEntity();
        boolean isDead = entity.isDeadOrDying();
        HeavenFireBleedingSettlementEvent settlementEvent = new HeavenFireBleedingSettlementEvent(entity, isDead);
        NeoForge.EVENT_BUS.post(settlementEvent);
    }
}
