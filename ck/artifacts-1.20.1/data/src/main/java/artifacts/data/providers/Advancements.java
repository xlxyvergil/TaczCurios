package artifacts.data.providers;

import artifacts.Artifacts;
import artifacts.registry.ModEntityTypes;
import artifacts.registry.ModItems;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeAdvancementProvider;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class Advancements extends ForgeAdvancementProvider {

    public Advancements(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, ExistingFileHelper existingFileHelper) {
        super(output, registries, existingFileHelper, List.of(Advancements::generate));
    }

    private static void generate(HolderLookup.Provider registries, Consumer<Advancement> saver, ExistingFileHelper existingFileHelper) {
        ResourceLocation amateurArcheologist = Artifacts.id("amateur_archaeologist");
        Advancement parent = advancement(amateurArcheologist, ModItems.FLAME_PENDANT.get())
                .parent(new ResourceLocation("adventure/root"))
                .addCriterion("find_artifact", InventoryChangeTrigger.TriggerInstance.hasItems(
                        ItemPredicate.Builder.item().of(ItemTags.ARTIFACTS).build()
                )).save(saver, amateurArcheologist, existingFileHelper);

        ResourceLocation chestSlayer = Artifacts.id("chest_slayer");
        advancement(chestSlayer, ModItems.MIMIC_SPAWN_EGG.get())
                .parent(parent)
                .addCriterion("kill_mimic", KilledTrigger.TriggerInstance.playerKilledEntity(
                        EntityPredicate.Builder.entity().of(ModEntityTypes.MIMIC.get())
                )).save(saver, chestSlayer, existingFileHelper);

        ResourceLocation adventurousEater = Artifacts.id("adventurous_eater");
        advancement(adventurousEater, ModItems.ONION_RING.get(), true)
                .parent(parent)
                .addCriterion("eat_artifact", ConsumeItemTrigger.TriggerInstance.usedItem(
                        ItemPredicate.Builder.item().of(ModItems.ONION_RING.get()).build()
                )).save(saver, adventurousEater, existingFileHelper);
    }

    private static Advancement.Builder advancement(ResourceLocation id, ItemLike icon) {
        return advancement(id, icon, false);
    }

    private static Advancement.Builder advancement(ResourceLocation id, ItemLike icon, boolean hidden) {
        return Advancement.Builder.advancement().display(display(id.getPath(), icon, hidden));
    }

    private static DisplayInfo display(String title, ItemLike icon, boolean hidden) {
        return new DisplayInfo(
                new ItemStack(icon),
                Component.translatable("%s.advancements.%s.title".formatted(Artifacts.MOD_ID, title)),
                Component.translatable("%s.advancements.%s.description".formatted(Artifacts.MOD_ID, title)),
                null,
                FrameType.TASK,
                true,
                true,
                hidden
        );
    }
}
