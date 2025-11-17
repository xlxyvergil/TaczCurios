package com.xlxyvergil.tcc;

import com.mojang.logging.LogUtils;
import com.xlxyvergil.tcc.creativetab.TaczCreativeTab;
import com.xlxyvergil.tcc.handlers.TaczEventHandler;
import com.xlxyvergil.tcc.registries.TaczBlocks;
import com.xlxyvergil.tcc.registries.TaczItems;
import com.xlxyvergil.tcc.villagers.TaczVillagers;
import com.xlxyvergil.tcc.registries.TaczPoiTypes;
import com.xlxyvergil.tcc.registries.ModLootModifiers;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

// 这里的值应该与META-INF/mods.toml文件中的条目匹配
@Mod(TaczCurios.MODID)
public class TaczCurios
{
    // 在一个公共位置定义模组ID供所有地方引用
    public static final String MODID = "tcc";
    // 直接引用slf4j日志记录器
    private static final Logger LOGGER = LogUtils.getLogger();
    // 创建一个延迟注册器来保存方块，所有方块都将注册在"tcc"命名空间下
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    // 创建一个延迟注册器来保存物品，所有物品都将注册在"tcc"命名空间下
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    // 创建一个延迟注册器来保存创造模式标签页，所有标签页都将注册在"tcc"命名空间下
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public TaczCurios()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // 注册commonSetup方法用于模组加载
        modEventBus.addListener(this::commonSetup);

        // 注册方块和物品
        TaczBlocks.BLOCKS.register(modEventBus);
        TaczBlocks.ITEMS.register(modEventBus);

        // 注册饰品
        TaczItems.ITEMS.register(modEventBus);
        
        // 注册创造模式标签页
        TaczCreativeTab.CREATIVE_MODE_TABS.register(modEventBus);

        // 注册村民职业
        TaczVillagers.PROFESSIONS.register(modEventBus);
        
        // 注册POI类型
        TaczPoiTypes.POI_TYPES.register(modEventBus);
        
        // 注册战利品修饰符
        ModLootModifiers.LOOT_MODIFIERS.register(modEventBus);

        // 为我们自己注册服务器和其他感兴趣的游戏事件
        MinecraftForge.EVENT_BUS.register(this);
        
        // 注册事件处理程序
        MinecraftForge.EVENT_BUS.register(TaczEventHandler.class);
        
        // 在客户端环境中注册客户端事件处理程序
        registerClientEventsSafely();
    }

    private void registerClientEventsSafely() {
        try {
            // 尝试注册客户端事件，如果在服务器上会因为缺少客户端类而失败
            Class<?> clientEventHandlerClass = Class.forName("com.xlxyvergil.tcc.client.ClientEventHandler");
            MinecraftForge.EVENT_BUS.register(clientEventHandlerClass);
        } catch (ClassNotFoundException e) {
            // 在服务器环境中忽略，因为客户端类不可用
            LOGGER.info("ClientEventHandler not found, running on dedicated server");
        } catch (Exception e) {
            // 其他异常也忽略
            LOGGER.info("Failed to register ClientEventHandler: " + e.getMessage());
        }
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        // 一些通用设置代码
        LOGGER.info("HELLO FROM COMMON SETUP");
        LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));

        // 初始化村民交易
        event.enqueueWork(TaczVillagers::registerTrades);
    }

    // 您可以使用SubscribeEvent让事件总线发现要调用的方法
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        // 服务器启动时执行某些操作
        LOGGER.info("HELLO from server starting");
    }

    // 您可以使用EventBusSubscriber自动注册带有@SubscribeEvent注解的类中的所有静态方法
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            // 一些客户端设置代码
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }
}