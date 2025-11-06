package artifacts.forge;

import artifacts.Artifacts;
import artifacts.config.ModConfig;
import artifacts.forge.capability.SwimDataCapability;
import artifacts.forge.curio.WearableArtifactCurio;
import artifacts.forge.event.ArtifactEventsForge;
import artifacts.forge.event.SwimEventsForge;
import artifacts.forge.registry.ModItemsForge;
import artifacts.forge.registry.ModLootModifiers;
import artifacts.item.wearable.WearableArtifactItem;
import dev.architectury.platform.forge.EventBuses;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.common.capability.CurioItemCapability;

@Mod(Artifacts.MOD_ID)
public class ArtifactsForge {

    public ArtifactsForge() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        EventBuses.registerModEventBus(Artifacts.MOD_ID, modBus);

        Artifacts.init();
        if (FMLEnvironment.dist == Dist.CLIENT) {
            new ArtifactsForgeClient(modBus);
        }

        ModLootModifiers.LOOT_MODIFIERS.register(modBus);
        modBus.addListener(this::onCommonSetup);

        MinecraftForge.EVENT_BUS.addGenericListener(ItemStack.class, this::onAttachCapabilities);

        registerConfig();
        SwimDataCapability.setup();
        ArtifactEventsForge.register();
        SwimEventsForge.register();
    }

    private void registerConfig() {
        ModLoadingContext.get().registerExtensionPoint(
                ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory(
                        (client, parent) -> AutoConfig.getConfigScreen(ModConfig.class, parent).get()
                )
        );
    }

    private void onAttachCapabilities(AttachCapabilitiesEvent<ItemStack> event) {
        if (event.getObject().getItem() instanceof WearableArtifactItem item) {
            event.addCapability(CuriosCapability.ID_ITEM, CurioItemCapability.createProvider(new WearableArtifactCurio(item, event.getObject())));
        }
    }

    private void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(ModItemsForge::register);
    }
}
