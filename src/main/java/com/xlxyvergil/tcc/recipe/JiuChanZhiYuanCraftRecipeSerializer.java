package com.xlxyvergil.tcc.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

/**
 * 纠缠之缘合成配方的序列化器；配方无固定参数，逻辑由配方类动态判断。
 */
public class JiuChanZhiYuanCraftRecipeSerializer implements RecipeSerializer<JiuChanZhiYuanCraftRecipe> {

    private final MapCodec<JiuChanZhiYuanCraftRecipe> codec;
    private final StreamCodec<RegistryFriendlyByteBuf, JiuChanZhiYuanCraftRecipe> streamCodec;

    public JiuChanZhiYuanCraftRecipeSerializer() {
        this.codec = RecordCodecBuilder.mapCodec(instance -> instance.group(
                        CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC)
                                .forGetter(CraftingRecipe::category))
                .apply(instance, JiuChanZhiYuanCraftRecipe::new));
        this.streamCodec = StreamCodec.composite(CraftingBookCategory.STREAM_CODEC,
                CraftingRecipe::category, JiuChanZhiYuanCraftRecipe::new);
    }

    @Override
    public MapCodec<JiuChanZhiYuanCraftRecipe> codec() {
        return this.codec;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, JiuChanZhiYuanCraftRecipe> streamCodec() {
        return this.streamCodec;
    }
}
