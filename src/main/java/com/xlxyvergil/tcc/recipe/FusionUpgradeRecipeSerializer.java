package com.xlxyvergil.tcc.recipe;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;

/**
 * 饰品升级配方的序列化器。
 * 配方无固定参数，所有逻辑在 {@link FusionUpgradeRecipe} 中动态判断。
 */
public class FusionUpgradeRecipeSerializer implements RecipeSerializer<FusionUpgradeRecipe> {

    @Override
    public FusionUpgradeRecipe fromJson(ResourceLocation id, JsonObject json) {
        return new FusionUpgradeRecipe(id);
    }

    @Override
    public FusionUpgradeRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
        return new FusionUpgradeRecipe(id);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buf, FusionUpgradeRecipe recipe) {
        // 无额外数据需要写入
    }
}
