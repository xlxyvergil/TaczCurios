package net.tracen.umapyoi.recipe;

import java.util.function.BiFunction;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

public record UmasoulRecipeSerializer<T extends Recipe<?>, U extends T> (RecipeSerializer<T> compose,
        BiFunction<T, @Nullable ResourceLocation, U> converter) implements RecipeSerializer<U> {
    @Override
    @NotNull
    public U fromJson(@NotNull ResourceLocation id, @NotNull JsonObject json) {
        if (!json.has("result")) {
            JsonObject object = new JsonObject();
            object.addProperty("item", "umapyoi:blank_uma_soul");
            json.add("result", object);
        }
        T recipe = compose().fromJson(id, json);
        if (json.has("umasoul")) {
            ResourceLocation output = new ResourceLocation(GsonHelper.getAsString(json, "umasoul"));
            return converter().apply(recipe, output);
        }
        return converter().apply(recipe,
                new ResourceLocation(GsonHelper.getAsString(json.getAsJsonObject("result"), "item")));
    }

    @Override
    @NotNull
    public U fromNetwork(@NotNull ResourceLocation id, @NotNull FriendlyByteBuf buf) {
        T recipe = compose().fromNetwork(id, buf);
        if (buf.readBoolean())
            return converter().apply(recipe, buf.readResourceLocation());
        return converter().apply(recipe, null);
    }

    @Override
    public void toNetwork(@NotNull FriendlyByteBuf buf, @NotNull U recipe) {
        compose().toNetwork(buf, recipe);
        if (recipe instanceof ShapedUmasoulRecipe umaRecipe) {
            boolean hasName = umaRecipe.getOutputUma() != null;
            buf.writeBoolean(hasName);
            if (hasName)
                buf.writeResourceLocation(umaRecipe.getOutputUma());
        } else if (recipe instanceof ShapelessUmasoulRecipe umaRecipe) {
            boolean hasName = umaRecipe.getOutputUma() != null;
            buf.writeBoolean(hasName);
            if (hasName)
                buf.writeResourceLocation(umaRecipe.getOutputUma());
        }else
            buf.writeBoolean(false);
    }
}