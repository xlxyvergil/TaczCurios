package net.tracen.umapyoi.recipe;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraftforge.registries.ForgeRegistries;
import net.tracen.umapyoi.item.ItemRegistry;

public class ShapelessCostumeRecipe extends ShapelessRecipe {

    public static final RecipeSerializer<ShapelessCostumeRecipe> SERIALIZER = new CostumeRecipeSerializer<>(
            RecipeSerializer.SHAPELESS_RECIPE, ShapelessCostumeRecipe::new);

    private final ResourceLocation output;

    public ShapelessCostumeRecipe(ShapelessRecipe compose, ResourceLocation output) {
        super(compose.getId(), compose.getGroup(), compose.category(), 
                getResultItem(output), compose.getIngredients());
        this.output = output;
    }

    private static ItemStack getResultItem(ResourceLocation output) {
        Item bladeItem = ForgeRegistries.ITEMS.containsKey(output) ? ForgeRegistries.ITEMS.getValue(output)
                : ItemRegistry.UMA_COSTUME.get();

        return bladeItem.getDefaultInstance();
    }

    public ResourceLocation getOutput() {
        return output;
    }
    
    @Override
    public ItemStack assemble(CraftingContainer pContainer, RegistryAccess pRegistryAccess) {
    	return this.getResultItem(pRegistryAccess).copy();
    }
    
    @Override
    public ItemStack getResultItem(RegistryAccess access) {
        ItemStack result = ShapelessCostumeRecipe.getResultItem(this.getOutput()).copy();
        if(access == RegistryAccess.EMPTY)
        	return result;
        if (!ForgeRegistries.ITEMS.getKey(result.getItem()).equals(getOutput())) 
            result = ItemRegistry.UMA_COSTUME.get().getDefaultInstance();
        result.getOrCreateTag().putString("cosmetic", this.output.toString());
        return result;
    }
    
    @Override
    public RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

}
