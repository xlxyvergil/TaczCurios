package com.xlxyvergil.tcc.event;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.registries.TccMobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 崩坏病（易伤）增伤执行器 - 戒律神之键线。
 * <p>
 * 目标携带崩坏病效果时，受到的伤害提升：amplifier 0/1/2 → 20%/40%/60%。
 */
@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class HonkaiDiseaseHandler {

    private HonkaiDiseaseHandler() {
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        LivingEntity target = event.getEntity();
        if (target.level().isClientSide) {
            return;
        }
        MobEffectInstance disease = target.getEffect(TccMobEffects.HONKAI_DISEASE.get());
        if (disease != null) {
            float vulnerability = 0.2f * (disease.getAmplifier() + 1);
            event.setAmount(event.getAmount() * (1.0f + vulnerability));
        }
    }
}
