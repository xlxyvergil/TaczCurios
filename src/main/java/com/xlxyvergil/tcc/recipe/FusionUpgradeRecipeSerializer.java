package com.xlxyvergil.tcc.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

/**
 * 饰品升级配方的序列化器；配方无固定参数，逻辑由配方类动态判断。
 */
public class FusionUpgradeRecipeSerializer implements RecipeSerializer<FusionUpgradeRecipe> {

    private final MapCodec<FusionUpgradeRecipe> codec;
    private final StreamCodec<RegistryFriendlyByteBuf, FusionUpgradeRecipe> streamCodec;

    public FusionUpgradeRecipeSerializer() {
        this.codec = RecordCodecBuilder.mapCodec(instance -> instance.group(
                        CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC)
                                .forGetter(CraftingRecipe::category))
                .apply(instance, FusionUpgradeRecipe::new));
        this.streamCodec = StreamCodec.composite(CraftingBookCategory.STREAM_CODEC,
                CraftingRecipe::category, FusionUpgradeRecipe::new);
    }

    @Override
    public MapCodec<FusionUpgradeRecipe> codec() {
        return this.codec;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, FusionUpgradeRecipe> streamCodec() {
        return this.streamCodec;
    }
}
