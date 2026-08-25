package com.xlxyvergil.tcc.client;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.client.renderer.ZhenWoBarrierRenderer;
import com.xlxyvergil.tcc.registries.TccEntities;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 客户端 Mod 总线事件（渲染器注册等）。
 */
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientModEvents {

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(TccEntities.ZHEN_WO_BARRIER.get(), ZhenWoBarrierRenderer::new);
    }
}
