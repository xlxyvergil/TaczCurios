package com.xlxyvergil.tcc.effect;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.core.TccDamageSources;
import com.xlxyvergil.tcc.event.HeavenFireBleedingSettlementEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class HeavenFireBleedingEffect extends MobEffect {

    public HeavenFireBleedingEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFF4500);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide) return;
        double damagePerLevel = -TaczCuriosConfig.COMMON.heavenFireBleedingDamagePerLevel.get();
        float maxHealth = entity.getMaxHealth();
        float damage = (float) (maxHealth * damagePerLevel * (amplifier + 1));
        entity.hurt(TccDamageSources.imaginaryDamage(entity.level(), entity.getLastAttacker()), damage);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % 40 == 0;
    }

    @SubscribeEvent
    public void onExpired(MobEffectEvent.Expired event) {
        if (event.getEffectInstance().getEffect() != this) {
            return;
        }
        LivingEntity entity = event.getEntity();
        boolean isDead = entity.isDeadOrDying();
        HeavenFireBleedingSettlementEvent settlementEvent = new HeavenFireBleedingSettlementEvent(entity, isDead);
        MinecraftForge.EVENT_BUS.post(settlementEvent);
    }
}
