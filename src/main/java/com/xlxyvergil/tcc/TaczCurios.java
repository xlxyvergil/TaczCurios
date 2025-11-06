package com.xlxyvergil.tcc;

import com.xlxyvergil.tcc.creativetab.TaczCreativeTab;
import com.xlxyvergil.tcc.registries.TaczItems;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(TaczCurios.MODID)
public class TaczCurios {
    public static final String MODID = "tcc";
    public static final Logger LOGGER = LogManager.getLogger();

    public TaczCurios() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        // 注册物品
        TaczItems.ITEMS.register(modEventBus);
        
        // 注册创造模式标签页
        TaczCreativeTab.CREATIVE_MODE_TABS.register(modEventBus);
        
        // 注册事件处理器
        modEventBus.addListener(this::setup);
        
        // 注册Forge事件总线
        MinecraftForge.EVENT_BUS.register(this);
        
        LOGGER.info("TaczCurios mod initialized successfully");
    }

    private void setup(final FMLCommonSetupEvent event) {
        LOGGER.info("TaczCurios mod setup completed");
        LOGGER.info("Using data-driven Curios slot registration (data/tcc/curios/slots/tcc_slot.json)");
    }
}