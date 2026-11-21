package com.xlxyvergil.tcc.client.shop;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.registries.TccMenus;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

/**
 * 把泰辛商店菜单绑定到客户端界面。整类只在物理客户端加载。
 */
@EventBusSubscriber(modid = TaczCurios.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class TccShopScreens {

    private TccShopScreens() {
    }

    @SubscribeEvent
    public static void onRegisterMenuScreens(RegisterMenuScreensEvent event) {
        event.register(TccMenus.TESHIN_SHOP, TeshinShopScreen::new);
    }
}
