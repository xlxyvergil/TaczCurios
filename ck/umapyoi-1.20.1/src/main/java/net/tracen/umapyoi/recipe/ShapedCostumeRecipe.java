package net.tracen.umapyoi.recipe;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraftforge.registries.ForgeRegistries;
import net.tracen.umapyoi.item.ItemRegistry;

public class ShapedCostumeRecipe extends ShapedRecipe {

    public static final RecipeSerializer<ShapedCostumeRecipe> SERIALIZER = new CostumeRecipeSerializer<>(
            RecipeSerializer.SHAPED_RECIPE, ShapedCostumeRecipe::new);

    private final ResourceLocation output;

    public ShapedCostumeRecipe(ShapedRecipe compose, ResourceLocation outputBlade) {
        super(compose.getId(), compose.getGroup(), compose.category(), compose.getWidth(), compose.getHeight(),
                compose.getIngredients(), getResultItem(outputBlade));
        this.output = outputBlade;
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
        ItemStack result = ShapedCostumeRecipe.getResultItem(this.getOutput()).copy();
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
