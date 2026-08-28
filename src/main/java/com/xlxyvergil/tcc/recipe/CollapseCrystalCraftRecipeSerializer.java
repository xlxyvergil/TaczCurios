package com.xlxyvergil.tcc.recipe;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;

/**
 * 崩坏结晶合成配方的序列化器；配方无固定参数，逻辑由配方类动态判断。
 */
public class CollapseCrystalCraftRecipeSerializer implements RecipeSerializer<CollapseCrystalCraftRecipe> {

    @Override
    public CollapseCrystalCraftRecipe fromJson(ResourceLocation id, JsonObject json) {
        return new CollapseCrystalCraftRecipe(id);
    }

    @Override
    public CollapseCrystalCraftRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
        return new CollapseCrystalCraftRecipe(id);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buf, CollapseCrystalCraftRecipe recipe) {
        // 无额外数据
    }
}
