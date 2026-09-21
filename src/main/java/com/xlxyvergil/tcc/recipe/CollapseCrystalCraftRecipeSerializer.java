package com.xlxyvergil.tcc.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

/**
 * 崩坏结晶合成配方的序列化器；配方无固定参数，逻辑由配方类动态判断。
 */
public class CollapseCrystalCraftRecipeSerializer implements RecipeSerializer<CollapseCrystalCraftRecipe> {

    private final MapCodec<CollapseCrystalCraftRecipe> codec;
    private final StreamCodec<RegistryFriendlyByteBuf, CollapseCrystalCraftRecipe> streamCodec;

    public CollapseCrystalCraftRecipeSerializer() {
        this.codec = RecordCodecBuilder.mapCodec(instance -> instance.group(
                        CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC)
                                .forGetter(CraftingRecipe::category))
                .apply(instance, CollapseCrystalCraftRecipe::new));
        this.streamCodec = StreamCodec.composite(CraftingBookCategory.STREAM_CODEC,
                CraftingRecipe::category, CollapseCrystalCraftRecipe::new);
    }

    @Override
    public MapCodec<CollapseCrystalCraftRecipe> codec() {
        return this.codec;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, CollapseCrystalCraftRecipe> streamCodec() {
        return this.streamCodec;
    }
}
