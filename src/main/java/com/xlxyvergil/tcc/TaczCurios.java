package com.xlxyvergil.tcc;

import com.mojang.logging.LogUtils;
import com.xlxyvergil.tcc.handlers.TccEventHandler;
import com.xlxyvergil.tcc.registries.*;
import com.xlxyvergil.tcc.villagers.TaczVillagers;
import com.xlxyvergil.tcc.creativetab.TaczCreativeTab;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// 这里的值应该与META-INF/mods.toml文件中的条目匹配
@Mod(TaczCurios.MODID)
public class TaczCurios
{
    // 在一个公共位置定义mod id，供所有地方引用
    public static final String MODID = "tcc";
    // 直接引用slf4j日志记录器
    public static final Logger LOGGER = LogUtils.getLogger();

    public TaczCurios()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // 注册commonSetup方法用于模组加载
        modEventBus.addListener(this::commonSetup);

        // 注册Deferred Register
        TaczItems.ITEMS.register(modEventBus);
        TaczBlocks.BLOCKS.register(modEventBus);
        TaczBlocks.ITEMS.register(modEventBus);
        TaczPoiTypes.POI_TYPES.register(modEventBus);
        TaczVillagers.PROFESSIONS.register(modEventBus);
        TaczCreativeTab.CREATIVE_MODE_TABS.register(modEventBus);
        //TaczAttributes.ATTRIBUTES.register(modEventBus);
        //TaczSounds.SOUNDS.register(modEventBus);

        // 为我们感兴趣的服务器和其他游戏事件注册自己
        MinecraftForge.EVENT_BUS.register(this);
        // 注册战利品表事件处理器
        MinecraftForge.EVENT_BUS.register(TccEventHandler.getInstance());
        
        // 安全地注册客户端事件处理器
        registerClientEventsSafely();
    }
    
    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            TaczVillagers.registerTrades();
        });
    }
    
    private void registerClientEventsSafely() {
        // 仅在客户端环境中注册客户端事件处理器
        if (net.minecraftforge.fml.loading.FMLEnvironment.dist == net.minecraftforge.api.distmarker.Dist.CLIENT) {
            try {
                // 尝试注册客户端事件，如果在服务器上会因为缺少客户端类而失败
                Class<?> clientEventHandlerClass = Class.forName("com.xlxyvergil.tcc.client.ClientEventHandler");
                MinecraftForge.EVENT_BUS.register(clientEventHandlerClass);
            } catch (ClassNotFoundException e) {
                // 在服务器环境中忽略，因为客户端类不可用
                LOGGER.info("未找到ClientEventHandler，正在专用服务器上运行");
            } catch (Exception e) {
                // 其他异常也忽略
                LOGGER.info("注册ClientEventHandler失败: " + e.getMessage());
            }
        }
    }
}