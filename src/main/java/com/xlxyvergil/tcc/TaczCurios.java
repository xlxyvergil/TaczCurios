package com.xlxyvergil.tcc;

import com.mojang.logging.LogUtils;
import com.xlxyvergil.tcc.affix.GunMobEffectAffix;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.handlers.HeavenFireSettlementHandler;
import com.xlxyvergil.tcc.handlers.TccEventHandler;
import com.xlxyvergil.tcc.registries.*;
import com.xlxyvergil.tcc.villagers.TaczVillagers;
import com.xlxyvergil.tcc.creativetab.TaczCreativeTab;
import com.xlxyvergil.tcc.network.TccNetwork;
import com.xlxyvergil.tcc.integration.ApothicCuriosIntegration;
import com.xlxyvergil.tcc.core.TccAttributes;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(TaczCurios.MODID)
public class TaczCurios
{
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
    }
    
    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            TaczVillagers.registerTrades();
            // 检查是否启用了Apotheosis集成，然后初始化 Apotheosis 神化属性集成
            if (com.xlxyvergil.tcc.config.TaczCuriosConfig.COMMON.enableApotheosisIntegration.get()) {
                ApothicCuriosIntegration.init();
                // 注册 TCC 自定义枪械词缀类型
                AffixRegistry.INSTANCE.registerCodec(
                    new ResourceLocation(MODID, "gun_mob_effect"), GunMobEffectAffix.CODEC);
            }
        });
    }
    
    private void registerClientEventsSafely() {
        // 仅在客户端环境中注册客户端事件处理器
        if (net.minecraftforge.fml.loading.FMLEnvironment.dist == net.minecraftforge.api.distmarker.Dist.CLIENT) {
            try {
                // 尝试注册客户端事件，如果在服务器上会因为缺少客户端类而失败
                Class.forName("com.xlxyvergil.tcc.client.ClientEventHandler");
            } catch (ClassNotFoundException e) {
                // 在服务器环境中忽略，因为客户端类不可用
                LOGGER.info("未找到ClientEventHandler，正在专用服务器上运行");
            } catch (Exception e) {
                // 其他异常也忽略
                LOGGER.info("注册ClientEventHandler失败: " + e.getMessage());
            }
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
            LOGGER.info("已发送配置同步到玩家: " + event.getEntity().getName().getString());
        }
    }
}