package net.tracen.umapyoi.recipe;

import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraftforge.registries.ForgeRegistries;
import net.tracen.umapyoi.item.ItemRegistry;
import net.tracen.umapyoi.registry.umadata.UmaData;

public class ShapedUmasoulRecipe extends ShapedRecipe {

    public static final RecipeSerializer<ShapedUmasoulRecipe> SERIALIZER = new UmasoulRecipeSerializer<>(
            RecipeSerializer.SHAPED_RECIPE, ShapedUmasoulRecipe::new);

    private final ResourceLocation outputUma;

    public ShapedUmasoulRecipe(ShapedRecipe compose, ResourceLocation outputBlade) {
        super(compose.getId(), compose.getGroup(), compose.category(), compose.getWidth(), compose.getHeight(),
                compose.getIngredients(), getResultItem(outputBlade));
        this.outputUma = outputBlade;
    }

    private static ItemStack getResultItem(ResourceLocation outputBlade) {
        Item bladeItem = ForgeRegistries.ITEMS.containsKey(outputBlade) ? ForgeRegistries.ITEMS.getValue(outputBlade)
                : ItemRegistry.BLANK_UMA_SOUL.get();

        return bladeItem.getDefaultInstance();
    }

    public ResourceLocation getOutputUma() {
        return outputUma;
    }
    
    @Override
    public ItemStack assemble(CraftingContainer pContainer, RegistryAccess pRegistryAccess) {
    	return this.getResultItem(pRegistryAccess).copy();
    }

    @Override
    public ItemStack getResultItem(RegistryAccess access) {
        ItemStack result = ShapedUmasoulRecipe.getResultItem(this.getOutputUma()).copy();
        if(access == RegistryAccess.EMPTY)
        	return result;
        Registry<UmaData> registry = access.registryOrThrow(UmaData.REGISTRY_KEY);
        if (!ForgeRegistries.ITEMS.getKey(result.getItem()).equals(getOutputUma())) 
            result = ItemRegistry.BLANK_UMA_SOUL.get().getDefaultInstance();
        UmaData data = registry.get(getOutputUma());
        if(data == null)
        	return ItemStack.EMPTY;
        result.getOrCreateTag().putString("name", this.outputUma.toString());
        result.getOrCreateTag().putString("identifier", data.getIdentifier().toString());
        result.getOrCreateTag().putString("ranking", data.getGachaRanking().toString().toLowerCase());
        return result;
    }
    @Override
    public RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

}
