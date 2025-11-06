package artifacts.fabric;

import artifacts.Artifacts;
import artifacts.fabric.event.SwimEventsFabric;
import artifacts.fabric.extensions.ClientConfigFabric;
import artifacts.fabric.integration.CompatHandler;
import artifacts.fabric.registry.ModFeatures;
import artifacts.fabric.registry.ModLootTablesFabric;
import artifacts.fabric.trinket.WearableArtifactTrinket;
import artifacts.item.wearable.WearableArtifactItem;
import dev.emi.trinkets.api.TrinketsApi;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.registries.BuiltInRegistries;

public class ArtifactsFabric implements ModInitializer {

    @SuppressWarnings("ConstantConditions")
    public static ClientConfigFabric getClientConfig() {
        return (ClientConfigFabric) (Object) Artifacts.CONFIG.client;
    }

    @Override
    public void onInitialize() {
        Artifacts.init();
        registerTrinkets();

        SwimEventsFabric.register();
        ModFeatures.register();

        LootTableEvents.MODIFY.register((rm, lt, id, supplier, s) ->
                ModLootTablesFabric.onLootTableLoad(id, supplier));

        runCompatibilityHandlers();
    }

    private void registerTrinkets() {
        BuiltInRegistries.ITEM.stream()
                .filter(item -> item instanceof WearableArtifactItem)
                .forEach(item -> TrinketsApi.registerTrinket(item, new WearableArtifactTrinket((WearableArtifactItem) item)));
    }

    private void runCompatibilityHandlers() {
        FabricLoader.getInstance().getEntrypoints("artifacts:compat_handlers", CompatHandler.class).stream()
                .filter(handler -> FabricLoader.getInstance().isModLoaded(handler.getModId()))
                .forEach(handler -> {
                    String modName = FabricLoader.getInstance().getModContainer(handler.getModId())
                            .map(container -> container.getMetadata().getName())
                            .orElse(handler.getModId());
                    Artifacts.LOGGER.info("Running compat handler for " + modName);

                    handler.run();
                });
    }
}
