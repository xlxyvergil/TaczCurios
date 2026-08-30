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

        // 在 RegisterEvent<BLOCK> 时统一注册方块、物品、POI 类型和村民职业
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
        // 注册玩家登录事件（用于同步成就进度到客户端）
        MinecraftForge.EVENT_BUS.addListener((PlayerEvent.PlayerLoggedInEvent event) -> {
            if (event.getEntity() instanceof ServerPlayer sp) {
                NetworkHandler.syncAllForPlayer(sp);
            }
        });
        // 注册玩家死亡复活/维度切换事件（复制 Capability + 延迟同步到客户端）
        // PlayerEvent.Clone 在死亡复活与维度切换时都会触发，两种情况下都需复制数据并同步客户端
        MinecraftForge.EVENT_BUS.addListener((PlayerEvent.Clone event) -> {
            Player original = event.getOriginal();
            Player entity = event.getEntity();
            original.reviveCaps();
            // 显式复制数据（Forge NBT 持久化在客户端切换/复活流程中不一定可靠）
            var oldHandler = original.getCapability(TccPlayerDataCapability.CAPABILITY).orElse(null);
            if (oldHandler != null) {
                entity.getCapability(TccPlayerDataCapability.CAPABILITY).ifPresent(newHandler ->
                    newHandler.copyFrom(oldHandler));
            }
            // 延迟同步到下一 tick，确保 respawn/维度切换 packet 先到客户端创建新玩家
            if (entity instanceof ServerPlayer sp) {
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
