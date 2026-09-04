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
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.RegisterEvent;
import top.theillusivec4.curios.api.SlotTypeMessage;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.InterModComms;

@Mod(TaczCurios.MODID)
public class TaczCurios
{
    public static final String MODID = "tcc";

    public TaczCurios() throws ClassNotFoundException
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::intermodStuff);

        modEventBus.addListener((RegisterCapabilitiesEvent e) -> {
            e.register(CurioAdaptationCapability.Handler.class);
            e.register(GunKillDataCapability.Handler.class);
            e.register(TccPlayerDataCapability.Handler.class);
        });

        
        modEventBus.addListener((RegisterEvent event) -> {
            if (!event.getRegistryKey().equals(Registries.BLOCK)) {
                return;
            }
            TccBlocks.init(ForgeRegistries.BLOCKS);
            TccItems.init(ForgeRegistries.ITEMS);
            TccPoiTypes.init();
            TccVillagers.init();
        });

        TccCreativeTab.CREATIVE_MODE_TABS.register(modEventBus);
        TccMobEffects.MOB_EFFECTS.register(modEventBus);
        TccAttributes.register(modEventBus);
        TccRecipeSerializers.RECIPE_SERIALIZERS.register(modEventBus);

        
        MinecraftForge.EVENT_BUS.register(this);
        
        MinecraftForge.EVENT_BUS.addListener((PlayerEvent.PlayerLoggedInEvent event) -> {
            if (event.getEntity() instanceof ServerPlayer sp) {
                NetworkHandler.syncAllForPlayer(sp);
            }
        });
        
        
        MinecraftForge.EVENT_BUS.addListener((PlayerEvent.Clone event) -> {
            Player original = event.getOriginal();
            Player entity = event.getEntity();
            original.reviveCaps();
            
            var oldHandler = original.getCapability(TccPlayerDataCapability.CAPABILITY).orElse(null);
            if (oldHandler != null) {
                entity.getCapability(TccPlayerDataCapability.CAPABILITY).ifPresent(newHandler ->
                    newHandler.copyFrom(oldHandler));
            }
            
            if (entity instanceof ServerPlayer sp) {
                sp.server.execute(() ->
                    NetworkHandler.syncAllForPlayer(sp));
            }
        });
        
        MinecraftForge.EVENT_BUS.addListener((PlayerEvent.PlayerChangedDimensionEvent event) -> {
            if (event.getEntity() instanceof ServerPlayer sp) {
                
                sp.server.execute(() ->
                    NetworkHandler.syncAllForPlayer(sp));
            }
        });
        MinecraftForge.EVENT_BUS.register(new HeavenFireSettlementHandler());
        
        TaczCuriosConfig.registerConfigs();
        
        registerClientEventsSafely();
        
    }

    
    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            this.loadClass(LootTableEventHandler.class);
            MinecraftForge.EVENT_BUS.addListener(LootTableEventHandler::onLootTableLoad);
            EvolutionRegistry.loadOnce();
            AchievementDefinitions.loadOnce();
        });

        NetworkHandler.init();
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
        if (FMLEnvironment.dist == Dist.CLIENT) {
            Class.forName("com.xlxyvergil.tcc.client.ClientEventHandler");
            Class.forName("com.xlxyvergil.tcc.client.renderer.ZhenWoBarrierLevelRenderer");
            Class.forName("com.xlxyvergil.tcc.client.renderer.LootrHighlightsRenderer");
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
