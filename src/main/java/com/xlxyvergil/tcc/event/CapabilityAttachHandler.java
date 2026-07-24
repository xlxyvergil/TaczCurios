package com.xlxyvergil.tcc.event;

import com.xlxyvergil.tcc.capability.CurioAdaptationCapability;
import com.xlxyvergil.tcc.capability.GunKillDataCapability;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Capability 挂载事件处理。
 * 将所有 TCC 所需的自定义 Capability 挂载到 LivingEntity 上。
 */
@Mod.EventBusSubscriber(modid = "tcc", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CapabilityAttachHandler {

    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof LivingEntity living) {
            event.addCapability(
                CurioAdaptationCapability.ID,
                new CurioAdaptationCapability.Provider(living)
            );
            event.addCapability(
                GunKillDataCapability.ID,
                new GunKillDataCapability.Provider()
            );
        }
    }
}
