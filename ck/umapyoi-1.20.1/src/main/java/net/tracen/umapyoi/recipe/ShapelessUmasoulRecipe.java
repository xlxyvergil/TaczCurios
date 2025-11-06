package net.tracen.umapyoi.recipe;

import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraftforge.registries.ForgeRegistries;
import net.tracen.umapyoi.item.ItemRegistry;
import net.tracen.umapyoi.registry.umadata.UmaData;

public class ShapelessUmasoulRecipe extends ShapelessRecipe {

    public static final RecipeSerializer<ShapelessUmasoulRecipe> SERIALIZER = new UmasoulRecipeSerializer<>(
            RecipeSerializer.SHAPELESS_RECIPE, ShapelessUmasoulRecipe::new);

    private final ResourceLocation outputUma;

    public ShapelessUmasoulRecipe(ShapelessRecipe compose, ResourceLocation output) {
        super(compose.getId(), compose.getGroup(), compose.category(), 
                getResultItem(output), compose.getIngredients());
        this.outputUma = output;
    }

    private static ItemStack getResultItem(ResourceLocation output) {
        Item bladeItem = ForgeRegistries.ITEMS.containsKey(output) ? ForgeRegistries.ITEMS.getValue(output)
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
        ItemStack result = ShapelessUmasoulRecipe.getResultItem(this.getOutputUma()).copy();
        if(access == RegistryAccess.EMPTY)
        	return result;
        Registry<UmaData> registry = access.registryOrThrow(UmaData.REGISTRY_KEY);
        if (!ForgeRegistries.ITEMS.getKey(result.getItem()).equals(getOutputUma())) 
            result = ItemRegistry.BLANK_UMA_SOUL.get().getDefaultInstance();
        UmaData data = registry.get(outputUma);
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
