package artifacts.item.wearable.hands;

import artifacts.item.wearable.WearableArtifactItem;
import artifacts.registry.ModGameRules;
import artifacts.registry.ModItems;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class PickaxeHeaterItem extends WearableArtifactItem {

    private static final Container container = new SimpleContainer(3);

    @Nullable
    private static ResourceLocation lastRecipe;

    @Override
    public boolean hasNonCosmeticEffects() {
        return ModGameRules.PICKAXE_HEATER_ENABLED.get();
    }

    @Override
    public SoundEvent getEquipSound() {
        return SoundEvents.ARMOR_EQUIP_IRON;
    }

    public static ObjectArrayList<ItemStack> getModifiedBlockDrops(ObjectArrayList<ItemStack> items, LootContext context, TagKey<Block> ores, TagKey<Item> rawOres) {
        if (context.hasParam(LootContextParams.BLOCK_STATE)
                && context.hasParam(LootContextParams.THIS_ENTITY)
                && context.hasParam(LootContextParams.ORIGIN)
                && context.getParam(LootContextParams.THIS_ENTITY) instanceof LivingEntity entity
                && entity.level() instanceof ServerLevel level
                && ModItems.PICKAXE_HEATER.get().isEquippedBy(entity)
                && ModGameRules.PICKAXE_HEATER_ENABLED.get()
                && context.getParam(LootContextParams.BLOCK_STATE).is(ores)
        ) {
            ObjectArrayList<ItemStack> result = new ObjectArrayList<>(items.size());
            float experience = 0;
            for (ItemStack item : items) {
                ItemStack resultItem = item;
                if (item.is(rawOres)) {
                    Optional<AbstractCookingRecipe> recipe = getRecipeFor(item, level);
                    if (recipe.isPresent()) {
                        resultItem = recipe.get().assemble(container, level.registryAccess());
                        resultItem.setCount(resultItem.getCount() * item.getCount());
                        experience += recipe.get().getExperience();
                    }
                }
                result.add(resultItem);
            }
            awardExperience(level, context.getParam(LootContextParams.ORIGIN), experience);
            return result;
        }

        return items;
    }

    private static void awardExperience(ServerLevel level, Vec3 position, float experience) {
        int amount = Mth.floor(experience);
        if (Math.random() < Mth.frac(experience)) {
            amount++;
        }
        ExperienceOrb.award(level, position, amount);
    }

    public static Optional<AbstractCookingRecipe> getRecipeFor(ItemStack item, Level level) {
        container.clearContent();
        container.setItem(0, item);
        RecipeManager recipeManager = level.getRecipeManager();
        Optional<Pair<ResourceLocation, SmeltingRecipe>> optional = recipeManager.getRecipeFor(RecipeType.SMELTING, container, level, lastRecipe);
        if (optional.isPresent()) {
            Pair<ResourceLocation, SmeltingRecipe> pair = optional.get();
            lastRecipe = pair.getFirst();
            return Optional.of(pair.getSecond());
        } else {
            return Optional.empty();
        }
    }
}
