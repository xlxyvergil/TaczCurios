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
import net.tracen.umapyoi.registry.training.card.SupportCard;

public class ShapelessSupportCardRecipe extends ShapelessRecipe {

    public static final RecipeSerializer<ShapelessSupportCardRecipe> SERIALIZER = new SupportCardRecipeSerializer<>(
            RecipeSerializer.SHAPELESS_RECIPE, ShapelessSupportCardRecipe::new);

    private final ResourceLocation outputUma;

    public ShapelessSupportCardRecipe(ShapelessRecipe compose, ResourceLocation outputBlade) {
        super(compose.getId(), compose.getGroup(), compose.category(), 
                getResultItem(outputBlade), compose.getIngredients());
        this.outputUma = outputBlade;
    }

    private static ItemStack getResultItem(ResourceLocation outputBlade) {
        Item bladeItem = ForgeRegistries.ITEMS.containsKey(outputBlade) ? ForgeRegistries.ITEMS.getValue(outputBlade)
                : ItemRegistry.BLANK_UMA_SOUL.get();

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
        ItemStack result = ShapelessSupportCardRecipe.getResultItem(this.getOutput()).copy();
        if(access == RegistryAccess.EMPTY)
        	return result;
        Registry<SupportCard> registry = access.registryOrThrow(SupportCard.REGISTRY_KEY);
        if (!ForgeRegistries.ITEMS.getKey(result.getItem()).equals(getOutput())) 
            result = ItemRegistry.SUPPORT_CARD.get().getDefaultInstance();
        SupportCard supportCard = registry.get(this.outputUma);
        if(supportCard == null)
        	return ItemStack.EMPTY;
        result.getOrCreateTag().putString("support_card", this.outputUma.toString());
		result.getOrCreateTag().putString("ranking", supportCard.getGachaRanking().name().toLowerCase());
        result.getOrCreateTag().putInt("maxDamage", supportCard.getMaxDamage());
        return result;
    }
    @Override
    public RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

}
