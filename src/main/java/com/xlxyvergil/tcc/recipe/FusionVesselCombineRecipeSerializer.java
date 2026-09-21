package com.xlxyvergil.tcc.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

/**
 * 融合容器合并配方的序列化器；无额外参数，逻辑由配方类动态判断。
 */
public class FusionVesselCombineRecipeSerializer implements RecipeSerializer<FusionVesselCombineRecipe> {

    private final MapCodec<FusionVesselCombineRecipe> codec;
    private final StreamCodec<RegistryFriendlyByteBuf, FusionVesselCombineRecipe> streamCodec;

    public FusionVesselCombineRecipeSerializer() {
        this.codec = RecordCodecBuilder.mapCodec(instance -> instance.group(
                        CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC)
                                .forGetter(CraftingRecipe::category))
                .apply(instance, FusionVesselCombineRecipe::new));
        this.streamCodec = StreamCodec.composite(CraftingBookCategory.STREAM_CODEC,
                CraftingRecipe::category, FusionVesselCombineRecipe::new);
    }

    @Override
    public MapCodec<FusionVesselCombineRecipe> codec() {
        return this.codec;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, FusionVesselCombineRecipe> streamCodec() {
        return this.streamCodec;
    }
}
