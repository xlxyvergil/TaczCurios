package com.xlxyvergil.tcc.client.shop;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.registries.TccMenus;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

/**
 * 把泰辛商店菜单绑定到客户端界面。整类只在物理客户端加载。
 * <p>1.20.1 没有 RegisterMenuScreensEvent，注册要放在 FMLClientSetupEvent 里。
 */
@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class TccShopScreens {

    private TccShopScreens() {
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> MenuScreens.register(TccMenus.TESHIN_SHOP.get(), TeshinShopScreen::new));
    }
}
