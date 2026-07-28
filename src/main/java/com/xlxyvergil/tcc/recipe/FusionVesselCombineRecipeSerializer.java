package com.xlxyvergil.tcc.recipe;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;

/**
 * 融合容器合并配方的序列化器 — 无额外参数，所有逻辑在 {@link FusionVesselCombineRecipe} 中动态判断。
 */
public class FusionVesselCombineRecipeSerializer implements RecipeSerializer<FusionVesselCombineRecipe> {

    @Override
    public FusionVesselCombineRecipe fromJson(ResourceLocation id, JsonObject json) {
        return new FusionVesselCombineRecipe(id);
    }

    @Override
    public FusionVesselCombineRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
        return new FusionVesselCombineRecipe(id);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buf, FusionVesselCombineRecipe recipe) {
        // 无额外数据
    }
}
