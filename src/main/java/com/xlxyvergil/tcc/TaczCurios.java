package com.xlxyvergil.tcc;

import com.xlxyvergil.tcc.capability.CurioAdaptationCapability;
import com.xlxyvergil.tcc.capability.GunKillDataCapability;
import com.xlxyvergil.tcc.capability.TccPlayerDataCapability;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.event.HeavenFireSettlementHandler;
import com.xlxyvergil.tcc.loot.LootTableEventHandler;
import com.xlxyvergil.tcc.network.NetworkHandler;
import com.xlxyvergil.tcc.registries.*;
import com.xlxyvergil.tcc.villagers.TccVillagers;
import com.xlxyvergil.tcc.creativetab.TccCreativeTab;
import com.xlxyvergil.tcc.attribute.TccAttributes;
import com.xlxyvergil.tcc.evolution.AchievementDefinitions;
import com.xlxyvergil.tcc.evolution.EvolutionRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.RegisterEvent;
import top.theillusivec4.curios.api.SlotTypeMessage;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.InterModComms;

@Mod(TaczCurios.MODID)
public class TaczCurios
{
    public static final String MODID = "tcc";

    public TaczCurios(IEventBus modEventBus, ModContainer container) throws ClassNotFoundException
    {
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::intermodStuff);

        CurioAdaptationCapability.ATTACHMENT_TYPES.register(modEventBus);
        GunKillDataCapability.ATTACHMENT_TYPES.register(modEventBus);
        TccPlayerDataCapability.ATTACHMENT_TYPES.register(modEventBus);

        modEventBus.addListener((RegisterEvent event) -> {
            event.register(Registries.BLOCK, TccBlocks::init);
            event.register(Registries.ITEM, TccItems::init);
            event.register(Registries.ITEM, TccBlocks::initItem);
            event.register(Registries.POINT_OF_INTEREST_TYPE, TccPoiTypes::init);
            event.register(Registries.VILLAGER_PROFESSION, TccVillagers::init);
        });

        // 村民交易必须走 VillagerTradesEvent：VillagerTradingManager 会在 SERVER_DATA_LOAD 时
        // 用「启动快照 + 本事件监听者」整体重建 VillagerTrades.TRADES，注册期直接写入会被清空。
        NeoForge.EVENT_BUS.addListener(TccVillagers::onVillagerTrades);

        modEventBus.addListener(NetworkHandler::registerPayloads);

        TccCreativeTab.CREATIVE_MODE_TABS.register(modEventBus);
        TccMobEffects.MOB_EFFECTS.register(modEventBus);
        TccAttributes.register(modEventBus);
        TccRecipeSerializers.RECIPE_SERIALIZERS.register(modEventBus);
        LootTableEventHandler.LOOT_FUNCTION_TYPES.register(modEventBus);

        NeoForge.EVENT_BUS.addListener((PlayerEvent.PlayerLoggedInEvent event) -> {
            if (event.getEntity() instanceof ServerPlayer sp) {
                NetworkHandler.syncAllForPlayer(sp);
                NetworkHandler.syncConfig(sp);
                NetworkHandler.syncDataFiles(sp);
            }
        });
        
        
        NeoForge.EVENT_BUS.addListener((PlayerEvent.Clone event) -> {
            // 数据附件已由 NeoForge 按 copyOnDeath 自动继承，这里只需重新同步给客户端
            if (event.getEntity() instanceof ServerPlayer sp) {
                sp.server.execute(() ->
                    NetworkHandler.syncAllForPlayer(sp));
            }
        });
        
        NeoForge.EVENT_BUS.addListener((PlayerEvent.PlayerChangedDimensionEvent event) -> {
            if (event.getEntity() instanceof ServerPlayer sp) {
                
                sp.server.execute(() ->
                    NetworkHandler.syncAllForPlayer(sp));
            }
        });
        NeoForge.EVENT_BUS.register(new HeavenFireSettlementHandler());
        
        container.registerConfig(ModConfig.Type.COMMON, TaczCuriosConfig.COMMON_SPEC);
        
        registerClientEventsSafely();
        
    }

    
    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            this.loadClass(LootTableEventHandler.class);
            NeoForge.EVENT_BUS.addListener(LootTableEventHandler::onLootTableLoad);
            EvolutionRegistry.loadOnce();
            AchievementDefinitions.loadOnce();
        });
    }

    private void intermodStuff(InterModEnqueueEvent event) {
        registerCurioType("tcc_slot", 8, false, ResourceLocation.fromNamespaceAndPath(MODID, "slot/tcc_slot"));
        registerCurioType("tcc_3rd", 1, false, ResourceLocation.fromNamespaceAndPath(MODID, "slot/tcc_3rd"));
        registerCurioType("tcc_tdk", 1, false, ResourceLocation.fromNamespaceAndPath(MODID, "slot/tcc_tdk"));
    }

    @SuppressWarnings("removal")
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
        if (FMLEnvironment.dist == Dist.CLIENT) {
            Class.forName("com.xlxyvergil.tcc.client.ClientEventHandler");
            Class.forName("com.xlxyvergil.tcc.client.renderer.ZhenWoBarrierLevelRenderer");
            Class.forName("com.xlxyvergil.tcc.client.renderer.LootrHighlightsRenderer");
            Class.forName("com.xlxyvergil.tcc.client.renderer.SpawnerHighlightsRenderer");
            Class.forName("com.xlxyvergil.tcc.client.ApothicCurioModifierSource");
        }
    }

    @SuppressWarnings("SameParameterValue")
    private void loadClass(Class<?> theClass) {
        try {
            Class.forName(theClass.getName());
        } catch (ClassNotFoundException ex) {
            throw new IllegalStateException("This can't be happening.", ex);
        }
    }
}
