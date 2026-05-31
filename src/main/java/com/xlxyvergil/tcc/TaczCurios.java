package com.xlxyvergil.tcc;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.event.HeavenFireSettlementHandler;
import com.xlxyvergil.tcc.event.TccEventHandler;
import com.xlxyvergil.tcc.registries.*;
import com.xlxyvergil.tcc.villagers.TaczVillagers;
import com.xlxyvergil.tcc.creativetab.TaczCreativeTab;
import com.xlxyvergil.tcc.network.TccNetwork;
import com.xlxyvergil.tcc.integration.ApothicCuriosIntegration;
import com.xlxyvergil.tcc.attribute.TccAttributes;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(TaczCurios.MODID)
public class TaczCurios
{
    public static final String MODID = "tcc";

    public TaczCurios() throws ClassNotFoundException
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
        TccMobEffects.MOB_EFFECTS.register(modEventBus);
        TccAttributes.register(modEventBus);

        
        MinecraftForge.EVENT_BUS.register(this);
        // 注册战利品表事件处理器
        MinecraftForge.EVENT_BUS.register(TccEventHandler.getInstance());
        // 注册天火流血结算事件处理器
        MinecraftForge.EVENT_BUS.register(new HeavenFireSettlementHandler());
        
        // 注册配置文件
        TaczCuriosConfig.registerConfigs();
        
        // 注册网络包
        TccNetwork.register();
        
        // 注册玩家登录事件
        MinecraftForge.EVENT_BUS.addListener(this::onPlayerLogin);
        
        // 安全地注册客户端事件处理器
        registerClientEventsSafely();
        
        // 必须在构造函数中初始化 Apotheosis 集成，确保在词缀数据加载前完成注册
        ApothicCuriosIntegration.init();
    }
    
    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            TaczVillagers.registerTrades();
        });
    }
    
    private void registerClientEventsSafely() throws ClassNotFoundException {
        // 仅在客户端环境中注册客户端事件处理器
        if (net.minecraftforge.fml.loading.FMLEnvironment.dist == net.minecraftforge.api.distmarker.Dist.CLIENT) {
            Class.forName("com.xlxyvergil.tcc.client.ClientEventHandler");
        }
    }
    
    private void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        // 在玩家登录时发送配置同步包
        if (!event.getEntity().level().isClientSide) {
            net.minecraft.server.level.ServerPlayer player = (net.minecraft.server.level.ServerPlayer) event.getEntity();
            TccNetwork.INSTANCE.sendTo(
                com.xlxyvergil.tcc.network.ConfigSyncPacket.fromServer(),
                player.connection.connection,
                net.minecraftforge.network.NetworkDirection.PLAY_TO_CLIENT
            );
        }
    }
}