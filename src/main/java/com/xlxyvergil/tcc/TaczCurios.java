package com.xlxyvergil.tcc;

import com.xlxyvergil.tcc.capability.CurioAdaptationCapability;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.event.HeavenFireSettlementHandler;
import com.xlxyvergil.tcc.event.TccEventHandler;
import com.xlxyvergil.tcc.registries.*;
import com.xlxyvergil.tcc.villagers.TccVillagers;
import com.xlxyvergil.tcc.creativetab.TccCreativeTab;
import com.xlxyvergil.tcc.attribute.TccAttributes;
import com.xlxyvergil.tcc.evolution.AchievementDefinitions;
import com.xlxyvergil.tcc.evolution.EvolutionRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraft.resources.ResourceLocation;
import top.theillusivec4.curios.api.SlotTypeMessage;
import net.minecraftforge.fml.InterModComms;

@Mod(TaczCurios.MODID)
public class TaczCurios
{
    public static final String MODID = "tcc";

    public TaczCurios() throws ClassNotFoundException
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // 注册commonSetup方法用于模组加载
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::intermodStuff);

        // 注册 Capability
        modEventBus.addListener((RegisterCapabilitiesEvent e) ->
            e.register(CurioAdaptationCapability.Handler.class));

        // 注册Deferred Register
        TccItems.ITEMS.register(modEventBus);
        TccBlocks.BLOCKS.register(modEventBus);
        TccBlocks.ITEMS.register(modEventBus);
        TccPoiTypes.POI_TYPES.register(modEventBus);
        TccVillagers.PROFESSIONS.register(modEventBus);
        TccCreativeTab.CREATIVE_MODE_TABS.register(modEventBus);
        TccMobEffects.MOB_EFFECTS.register(modEventBus);
        TccAttributes.register(modEventBus);

        
        MinecraftForge.EVENT_BUS.register(this);
        // 注册战利品表事件处理器
        MinecraftForge.EVENT_BUS.register(TccEventHandler.getInstance());
        // 注册天火流血结算事件处理器
        MinecraftForge.EVENT_BUS.register(new HeavenFireSettlementHandler());
        
        // 注册配置文件
        TaczCuriosConfig.registerConfigs();
        
        // 安全地注册客户端事件处理器
        registerClientEventsSafely();
        
    }
    
    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            TccVillagers.registerTrades();
            EvolutionRegistry.loadOnce();
            AchievementDefinitions.loadOnce();
        });
    }

    private void intermodStuff(InterModEnqueueEvent event) {
        registerCurioType("tcc_slot", 8, false, new ResourceLocation(MODID, "slot/tcc_slot"));
        registerCurioType("tcc_3rd", 1, false, new ResourceLocation(MODID, "slot/tcc_3rd"));
        registerCurioType("tcc_tdk", 1, false, new ResourceLocation(MODID, "slot/tcc_tdk"));
    }

    private void registerCurioType(final String identifier, final int slots, final boolean isHidden, final ResourceLocation icon) {
        final SlotTypeMessage.Builder message = new SlotTypeMessage.Builder(identifier);
        message.size(slots);
        if (isHidden) {
            message.hide();
        }
        if (icon != null) {
            message.icon(icon);
        }
        InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> message.build());
    }
    
    private void registerClientEventsSafely() throws ClassNotFoundException {
        // 仅在客户端环境中注册客户端事件处理器
        if (net.minecraftforge.fml.loading.FMLEnvironment.dist == net.minecraftforge.api.distmarker.Dist.CLIENT) {
            Class.forName("com.xlxyvergil.tcc.client.ClientEventHandler");
        }
    }
}
