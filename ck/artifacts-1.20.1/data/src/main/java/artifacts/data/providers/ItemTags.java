package artifacts.data.providers;

import artifacts.Artifacts;
import artifacts.item.wearable.WearableArtifactItem;
import artifacts.registry.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class ItemTags extends ItemTagsProvider {

    public static final TagKey<Item>
            ARTIFACTS = createTag("artifacts"),
            HEAD = createTag("slot/head"),
            NECKLACE = createTag("slot/necklace"),
            HANDS = createTag("slot/hands"),
            BELT = createTag("slot/belt"),
            FEET = createTag("slot/feet"),
            ALL = createTag("slot/all");

    public static final TagKey<Item> ORIGINS_MEAT = TagKey.create(Registries.ITEM, new ResourceLocation("origins", "meat"));
    public static final TagKey<Item> ORIGINS_SHIELDS = TagKey.create(Registries.ITEM, new ResourceLocation("origins", "shields"));

    private static TagKey<Item> createTag(String name) {
        return TagKey.create(Registries.ITEM, Artifacts.id(name));
    }

    public ItemTags(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagsProvider.TagLookup<Block>> blockTags, @Nullable ExistingFileHelper existingFileHelper) {
        super(packOutput, lookupProvider, blockTags, Artifacts.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(ARTIFACTS).add(BuiltInRegistries.ITEM.stream()
                .filter(item -> BuiltInRegistries.ITEM.getKey(item).getNamespace().equals(Artifacts.MOD_ID))
                .filter(item -> item != ModItems.MIMIC_SPAWN_EGG.get()).toList().toArray(new Item[]{})
        );
        tag(HEAD).add(
                ModItems.PLASTIC_DRINKING_HAT.get(),
                ModItems.NOVELTY_DRINKING_HAT.get(),
                ModItems.SNORKEL.get(),
                ModItems.NIGHT_VISION_GOGGLES.get(),
                ModItems.VILLAGER_HAT.get(),
                ModItems.SUPERSTITIOUS_HAT.get(),
                ModItems.COWBOY_HAT.get(),
                ModItems.ANGLERS_HAT.get()
        );
        tag(NECKLACE).add(
                ModItems.LUCKY_SCARF.get(),
                ModItems.SCARF_OF_INVISIBILITY.get(),
                ModItems.CROSS_NECKLACE.get(),
                ModItems.PANIC_NECKLACE.get(),
                ModItems.SHOCK_PENDANT.get(),
                ModItems.FLAME_PENDANT.get(),
                ModItems.THORN_PENDANT.get(),
                ModItems.CHARM_OF_SINKING.get()
        );
        tag(HANDS).add(
                ModItems.DIGGING_CLAWS.get(),
                ModItems.FERAL_CLAWS.get(),
                ModItems.POWER_GLOVE.get(),
                ModItems.FIRE_GAUNTLET.get(),
                ModItems.POCKET_PISTON.get(),
                ModItems.VAMPIRIC_GLOVE.get(),
                ModItems.GOLDEN_HOOK.get(),
                ModItems.ONION_RING.get(),
                ModItems.PICKAXE_HEATER.get()
        );
        tag(BELT).add(
                ModItems.CLOUD_IN_A_BOTTLE.get(),
                ModItems.OBSIDIAN_SKULL.get(),
                ModItems.ANTIDOTE_VESSEL.get(),
                ModItems.UNIVERSAL_ATTRACTOR.get(),
                ModItems.CRYSTAL_HEART.get(),
                ModItems.HELIUM_FLAMINGO.get(),
                ModItems.CHORUS_TOTEM.get()
        );
        tag(FEET).add(
                ModItems.AQUA_DASHERS.get(),
                ModItems.BUNNY_HOPPERS.get(),
                ModItems.KITTY_SLIPPERS.get(),
                ModItems.RUNNING_SHOES.get(),
                ModItems.SNOWSHOES.get(),
                ModItems.STEADFAST_SPIKES.get(),
                ModItems.FLIPPERS.get(),
                ModItems.ROOTED_BOOTS.get()
        );
        tag(ALL).add(
                ModItems.WHOOPEE_CUSHION.get()
        );

        tag(ORIGINS_MEAT).add(
                ModItems.EVERLASTING_BEEF.get(),
                ModItems.ETERNAL_STEAK.get()
        );
        tag(ORIGINS_SHIELDS).add(
                ModItems.UMBRELLA.get()
        );

        tag(net.minecraft.tags.ItemTags.PIGLIN_LOVED).add(BuiltInRegistries.ITEM.stream()
                .filter(item -> item instanceof WearableArtifactItem artifactItem && artifactItem.makesPiglinsNeutral())
                .toArray(Item[]::new)
        );
    }
}
