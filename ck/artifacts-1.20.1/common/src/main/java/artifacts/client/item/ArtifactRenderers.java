package artifacts.client.item;

import artifacts.client.item.model.*;
import artifacts.client.item.renderer.*;
import artifacts.item.wearable.WearableArtifactItem;
import artifacts.platform.PlatformServices;
import artifacts.registry.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;

import java.util.function.Supplier;

public class ArtifactRenderers {

    public static void register() {
        // head
        register(ModItems.PLASTIC_DRINKING_HAT.get(), () -> new GenericArtifactRenderer("plastic_drinking_hat", new HeadModel(bakeLayer(ArtifactLayers.DRINKING_HAT))));
        register(ModItems.NOVELTY_DRINKING_HAT.get(), () -> new GenericArtifactRenderer("novelty_drinking_hat", new HeadModel(bakeLayer(ArtifactLayers.DRINKING_HAT))));
        register(ModItems.SNORKEL.get(), () -> new GenericArtifactRenderer("snorkel", new HeadModel(bakeLayer(ArtifactLayers.SNORKEL), RenderType::entityTranslucent)));
        register(ModItems.NIGHT_VISION_GOGGLES.get(), () -> new GlowingArtifactRenderer("night_vision_goggles", new HeadModel(bakeLayer(ArtifactLayers.NIGHT_VISION_GOGGLES))));
        register(ModItems.SUPERSTITIOUS_HAT.get(), () -> new GenericArtifactRenderer("superstitious_hat", new HeadModel(bakeLayer(ArtifactLayers.SUPERSTITIOUS_HAT), RenderType::entityCutoutNoCull)));
        register(ModItems.VILLAGER_HAT.get(), () -> new GenericArtifactRenderer("villager_hat", new HeadModel(bakeLayer(ArtifactLayers.BRIMMED_HAT))));
        register(ModItems.COWBOY_HAT.get(), () -> new GenericArtifactRenderer("cowboy_hat", new HeadModel(bakeLayer(ArtifactLayers.COWBOY_HAT))));
        register(ModItems.ANGLERS_HAT.get(), () -> new GenericArtifactRenderer("anglers_hat", new HeadModel(bakeLayer(ArtifactLayers.ANGLERS_HAT))));

        // necklace
        register(ModItems.LUCKY_SCARF.get(), () -> new GenericArtifactRenderer("lucky_scarf", new ScarfModel(bakeLayer(ArtifactLayers.SCARF), RenderType::entityCutoutNoCull)));
        register(ModItems.SCARF_OF_INVISIBILITY.get(), () -> new GenericArtifactRenderer("scarf_of_invisibility",  new ScarfModel(bakeLayer(ArtifactLayers.SCARF), RenderType::entityTranslucent)));
        register(ModItems.CROSS_NECKLACE.get(), () -> new GenericArtifactRenderer("cross_necklace", new NecklaceModel(bakeLayer(ArtifactLayers.CROSS_NECKLACE))));
        register(ModItems.PANIC_NECKLACE.get(), () -> new GenericArtifactRenderer("panic_necklace", new NecklaceModel(bakeLayer(ArtifactLayers.PANIC_NECKLACE))));
        register(ModItems.SHOCK_PENDANT.get(), () -> new GenericArtifactRenderer("shock_pendant", new NecklaceModel(bakeLayer(ArtifactLayers.PENDANT))));
        register(ModItems.FLAME_PENDANT.get(), () -> new GenericArtifactRenderer("flame_pendant", new NecklaceModel(bakeLayer(ArtifactLayers.PENDANT))));
        register(ModItems.THORN_PENDANT.get(), () -> new GenericArtifactRenderer("thorn_pendant", new NecklaceModel(bakeLayer(ArtifactLayers.PENDANT))));
        register(ModItems.CHARM_OF_SINKING.get(), () -> new GenericArtifactRenderer("charm_of_sinking", new NecklaceModel(bakeLayer(ArtifactLayers.CHARM_OF_SINKING))));

        // belt
        register(ModItems.CLOUD_IN_A_BOTTLE.get(), () -> new BeltArtifactRenderer("cloud_in_a_bottle", BeltModel.createCloudInABottleModel()));
        register(ModItems.OBSIDIAN_SKULL.get(), () -> new BeltArtifactRenderer("obsidian_skull", BeltModel.createObsidianSkullModel()));
        register(ModItems.ANTIDOTE_VESSEL.get(), () -> new BeltArtifactRenderer("antidote_vessel", BeltModel.createAntidoteVesselModel()));
        register(ModItems.UNIVERSAL_ATTRACTOR.get(), () -> new BeltArtifactRenderer("universal_attractor", BeltModel.createUniversalAttractorModel()));
        register(ModItems.CRYSTAL_HEART.get(), () -> new BeltArtifactRenderer("crystal_heart", BeltModel.createCrystalHeartModel()));
        register(ModItems.HELIUM_FLAMINGO.get(), () -> new GenericArtifactRenderer("helium_flamingo", BeltModel.createHeliumFlamingoModel()));
        register(ModItems.CHORUS_TOTEM.get(), () -> new BeltArtifactRenderer("chorus_totem", BeltModel.createChorusTotemModel()));

        // hands
        register(ModItems.DIGGING_CLAWS.get(), () -> new GloveArtifactRenderer("digging_claws", "digging_claws", ArmsModel::createClawsModel));
        register(ModItems.FERAL_CLAWS.get(), () -> new GloveArtifactRenderer("feral_claws", "feral_claws", ArmsModel::createClawsModel));
        register(ModItems.POWER_GLOVE.get(), () -> new GloveArtifactRenderer("power_glove", ArmsModel::createGloveModel));
        register(ModItems.FIRE_GAUNTLET.get(), () -> new GlowingGloveArtifactRenderer("fire_gauntlet", ArmsModel::createGloveModel));
        register(ModItems.POCKET_PISTON.get(), () -> new GloveArtifactRenderer("pocket_piston", ArmsModel::createPocketPistonModel));
        register(ModItems.VAMPIRIC_GLOVE.get(), () -> new GloveArtifactRenderer("vampiric_glove", ArmsModel::createGloveModel));
        register(ModItems.GOLDEN_HOOK.get(), () -> new GloveArtifactRenderer("golden_hook", ArmsModel::createGoldenHookModel));
        register(ModItems.ONION_RING.get(), () -> new GloveArtifactRenderer("onion_ring", ArmsModel::createOnionRingModel));
        register(ModItems.PICKAXE_HEATER.get(), () -> new GlowingGloveArtifactRenderer("pickaxe_heater", ArmsModel::createPickaxeHeaterModel));

        // feet
        register(ModItems.AQUA_DASHERS.get(), () -> new BootArtifactRenderer("aqua_dashers", hasArmor -> new LegsModel(bakeLayer(hasArmor ? ArtifactLayers.AQUA_DASHERS_LARGE : ArtifactLayers.AQUA_DASHERS_SMALL))));
        register(ModItems.BUNNY_HOPPERS.get(), () -> new GenericArtifactRenderer("bunny_hoppers", new LegsModel(bakeLayer(ArtifactLayers.BUNNY_HOPPERS))));
        register(ModItems.KITTY_SLIPPERS.get(), () -> new GenericArtifactRenderer("kitty_slippers", new LegsModel(bakeLayer(ArtifactLayers.KITTY_SLIPPERS))));
        register(ModItems.RUNNING_SHOES.get(), () -> new BootArtifactRenderer("running_shoes", hasArmor -> new LegsModel(bakeLayer(hasArmor ? ArtifactLayers.BOOTS_LARGE : ArtifactLayers.BOOTS_SMALL))));
        register(ModItems.SNOWSHOES.get(), () -> new GenericArtifactRenderer("snowshoes", new LegsModel(bakeLayer(ArtifactLayers.SNOWSHOES))));
        register(ModItems.STEADFAST_SPIKES.get(), () -> new GenericArtifactRenderer("steadfast_spikes", new LegsModel(bakeLayer(ArtifactLayers.STEADFAST_SPIKES))));
        register(ModItems.FLIPPERS.get(), () -> new GenericArtifactRenderer("flippers", new LegsModel(bakeLayer(ArtifactLayers.FLIPPERS))));
        register(ModItems.ROOTED_BOOTS.get(), () -> new BootArtifactRenderer("rooted_boots", hasArmor -> new LegsModel(bakeLayer(hasArmor ? ArtifactLayers.BOOTS_LARGE : ArtifactLayers.BOOTS_SMALL))));

        // curio
        register(ModItems.WHOOPEE_CUSHION.get(), () -> new GenericArtifactRenderer("whoopee_cushion", new HeadModel(bakeLayer(ArtifactLayers.WHOOPEE_CUSHION))));
    }

    public static ModelPart bakeLayer(ModelLayerLocation layerLocation) {
        return Minecraft.getInstance().getEntityModels().bakeLayer(layerLocation);
    }

    public static void register(WearableArtifactItem item, Supplier<ArtifactRenderer> rendererSupplier) {
        PlatformServices.platformHelper.registerArtifactRenderer(item, rendererSupplier);
    }
}
