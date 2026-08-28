package com.xlxyvergil.tcc.recipe;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;

/**
 * 纠缠之缘合成配方的序列化器；配方无固定参数，逻辑由配方类动态判断。
 */
public class JiuChanZhiYuanCraftRecipeSerializer implements RecipeSerializer<JiuChanZhiYuanCraftRecipe> {

    @Override
    public JiuChanZhiYuanCraftRecipe fromJson(ResourceLocation id, JsonObject json) {
        return new JiuChanZhiYuanCraftRecipe(id);
    }

    @Override
    public JiuChanZhiYuanCraftRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
        return new JiuChanZhiYuanCraftRecipe(id);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buf, JiuChanZhiYuanCraftRecipe recipe) {
        // 无额外数据
    }
}
