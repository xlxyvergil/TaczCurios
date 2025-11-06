package artifacts.forge;

import artifacts.Artifacts;
import artifacts.ArtifactsClient;
import artifacts.client.item.ArtifactRenderers;
import artifacts.forge.client.ArmRenderHandler;
import artifacts.forge.client.ArtifactCooldownOverlayRenderer;
import artifacts.forge.client.HeliumFlamingoOverlayRenderer;
import artifacts.forge.client.UmbrellaArmPoseHandler;
import artifacts.mixin.accessors.client.LivingEntityRendererAccessor;
import artifacts.registry.ModItems;
import artifacts.registry.ModLootTables;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import top.theillusivec4.curios.client.render.CuriosLayer;

import java.util.Set;

public class ArtifactsForgeClient {

    public ArtifactsForgeClient(IEventBus modBus) {
        ArtifactsClient.init();

        modBus.addListener(this::onClientSetup);
        modBus.addListener(this::onRegisterGuiOverlays);
        modBus.addListener(this::onAddLayers);

        ArmRenderHandler.setup();
    }

    public void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(
                () -> ItemProperties.register(
                        ModItems.UMBRELLA.get(),
                        Artifacts.id("blocking"),
                        (stack, level, entity, seed) -> entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1 : 0
                )
        );
        ArtifactRenderers.register();
        UmbrellaArmPoseHandler.setup();
    }

    public void onRegisterGuiOverlays(RegisterGuiOverlaysEvent event) {
        event.registerAbove(VanillaGuiOverlay.AIR_LEVEL.id(), "helium_flamingo_charge", HeliumFlamingoOverlayRenderer::render);
        event.registerAbove(VanillaGuiOverlay.HOTBAR.id(), "artifact_cooldowns", ArtifactCooldownOverlayRenderer::render);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void onAddLayers(EntityRenderersEvent.AddLayers event) {
        Set<EntityType<?>> entities = ModLootTables.ENTITY_EQUIPMENT.keySet();
        loop:
        for (EntityType<?> entity : entities) {
            EntityRenderer<?> renderer = Minecraft.getInstance().getEntityRenderDispatcher().renderers.get(entity);
            if (renderer == null) {
                continue;
            }
            LivingEntityRenderer livingEntityRenderer = (LivingEntityRenderer<?, ?>) renderer;
            for (RenderLayer<?, ?> layer : ((LivingEntityRendererAccessor<?, ?>) livingEntityRenderer).getLayers()) {
                if (layer instanceof CuriosLayer<?, ?>) {
                    continue loop;
                }
            }
            livingEntityRenderer.addLayer(new CuriosLayer<>(livingEntityRenderer));
        }
    }
}
