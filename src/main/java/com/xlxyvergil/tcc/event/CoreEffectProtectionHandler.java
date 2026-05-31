package com.xlxyvergil.tcc.event;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.registries.TccMobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@net.minecraftforge.fml.common.Mod.EventBusSubscriber(modid = TaczCurios.MODID)
public class CoreEffectProtectionHandler {

    @SubscribeEvent
    public static void onEffectRemove(MobEffectEvent.Remove event) {
        MobEffectInstance effect = event.getEffectInstance();
        if (effect != null && (
            effect.getEffect() == TccMobEffects.HEAVEN_FIRE_BLEEDING.get() ||
            effect.getEffect() == TccMobEffects.HEAVEN_FIRE_APOCALYPSE_BUFF.get() ||
            effect.getEffect() == TccMobEffects.HEAVEN_FIRE_APOCALYPSE_DELAY.get() ||
            effect.getEffect() == TccMobEffects.IMAGINARY_INFECTION.get() ||
            effect.getEffect() == TccMobEffects.IMAGINARY_COLLAPSE.get() ||
            effect.getEffect() == TccMobEffects.EROSION.get()
        )) {
            event.setResult(Event.Result.DENY);
        }
    }
}
