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
import net.tracen.umapyoi.registry.training.card.SupportCard;

public class ShapedSupportCardRecipe extends ShapedRecipe {

    public static final RecipeSerializer<ShapedSupportCardRecipe> SERIALIZER = new SupportCardRecipeSerializer<>(
            RecipeSerializer.SHAPED_RECIPE, ShapedSupportCardRecipe::new);

    private final ResourceLocation outputUma;

    public ShapedSupportCardRecipe(ShapedRecipe compose, ResourceLocation outputBlade) {
        super(compose.getId(), compose.getGroup(), compose.category(), compose.getWidth(), compose.getHeight(),
                compose.getIngredients(), getResultItem(outputBlade));
        this.outputUma = outputBlade;
    }

    private static ItemStack getResultItem(ResourceLocation outputBlade) {
        Item bladeItem = ForgeRegistries.ITEMS.containsKey(outputBlade) ? ForgeRegistries.ITEMS.getValue(outputBlade)
                : ItemRegistry.SUPPORT_CARD.get();

        return bladeItem.getDefaultInstance();
    }

    public ResourceLocation getOutput() {
        return outputUma;
    }
    
    @Override
    public ItemStack assemble(CraftingContainer pContainer, RegistryAccess pRegistryAccess) {
    	return this.getResultItem(pRegistryAccess).copy();
    }

    @Override
    public ItemStack getResultItem(RegistryAccess access) {
        ItemStack result = ShapedSupportCardRecipe.getResultItem(this.getOutput()).copy();
        if(access == RegistryAccess.EMPTY)
        	return result;
        Registry<SupportCard> registry = access.registryOrThrow(SupportCard.REGISTRY_KEY);
        if (!ForgeRegistries.ITEMS.getKey(result.getItem()).equals(getOutput())) 
            result = ItemRegistry.SUPPORT_CARD.get().getDefaultInstance();
        SupportCard supportCard = registry.get(this.outputUma);
        if(supportCard == null)
        	return ItemStack.EMPTY;
        result.getOrCreateTag().putString("support_card", this.outputUma.toString());
        result.getOrCreateTag().putString("ranking", registry.get(this.outputUma).getGachaRanking().name().toLowerCase());
        result.getOrCreateTag().putInt("maxDamage", registry.get(this.outputUma).getMaxDamage());
        return result;
    }
    @Override
    public RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

}
