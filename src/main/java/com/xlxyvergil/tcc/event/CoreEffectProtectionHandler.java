package com.xlxyvergil.tcc.event;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.registries.TccMobEffects;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.bus.api.SubscribeEvent;

@net.neoforged.fml.common.EventBusSubscriber(modid = TaczCurios.MODID)
public class CoreEffectProtectionHandler {

    @SubscribeEvent
    public static void onEffectRemove(MobEffectEvent.Remove event) {
        MobEffect effect = event.getEffect().value();
        if (effect == TccMobEffects.HEAVEN_FIRE_BLEEDING.get()
                || effect == TccMobEffects.HEAVEN_FIRE_APOCALYPSE_BUFF.get()
                || effect == TccMobEffects.HEAVEN_FIRE_APOCALYPSE_DELAY.get()
                || effect == TccMobEffects.IMAGINARY_INFECTION.get()
                || effect == TccMobEffects.IMAGINARY_COLLAPSE.get()
                || effect == TccMobEffects.EROSION.get()) {
            event.setCanceled(true);
        }
    }
}
