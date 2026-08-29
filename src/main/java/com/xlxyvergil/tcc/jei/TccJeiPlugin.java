package com.xlxyvergil.tcc.jei;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.evolution.AchievementDefinitions;
import com.xlxyvergil.tcc.registries.TccItems;
import com.xlxyvergil.tcc.util.CollapseCrystalData;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * TaczCurios 的 JEI 集成：展示饰品进化路径（A → B）与崩坏结晶合成（真我 / 黑渊白花）。
 * JEI 为可选依赖，未安装 JEI 时此插件不会被加载，模组照常运行。
 */
@JeiPlugin
public class TccJeiPlugin implements IModPlugin {
    private List<EvolutionRecipe> evolutionRecipes;
    private List<CollapseCrystalRecipe> collapseRecipes;

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(TaczCurios.MODID, "jei");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IJeiHelpers jeiHelpers = registration.getJeiHelpers();
        registration.addRecipeCategories(
                new EvolutionRecipeCategory(jeiHelpers.getGuiHelper()),
                new CollapseCrystalRecipeCategory(jeiHelpers.getGuiHelper())
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(EvolutionRecipeCategory.TYPE, getEvolutionRecipes());
        registration.addRecipes(CollapseCrystalRecipeCategory.TYPE, getCollapseRecipes());
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(TccItems.COLLAPSE_CRYSTAL), CollapseCrystalRecipeCategory.TYPE);
        // 进化路径只注册一个代表性催化剂，避免左侧催化剂栏堆叠过多物品
        registration.addRecipeCatalyst(new ItemStack(TccItems.SHIJIE_FANYAN), EvolutionRecipeCategory.TYPE);
    }

    private List<EvolutionRecipe> getEvolutionRecipes() {
        if (evolutionRecipes != null) {
            return evolutionRecipes;
        }
        List<EvolutionRecipe> recipes = new ArrayList<>();
        for (AchievementDefinitions.AchievementDef def : AchievementDefinitions.all()) {
            if (!def.isEnabled() || def.reward() == null || !def.reward().isEvolve()) {
                continue;
            }
            AchievementDefinitions.Reward reward = def.reward();
            addEvolutionIfValid(recipes, reward.item(), reward.to(), def.id());
            if (reward.linkedEvolves() != null) {
                for (AchievementDefinitions.LinkedEvolveRef ref : reward.linkedEvolves()) {
                    addEvolutionIfValid(recipes, ref.item(), ref.to(), def.id());
                }
            }
        }
        evolutionRecipes = recipes;
        return recipes;
    }

    private void addEvolutionIfValid(List<EvolutionRecipe> recipes, String fromId, String toId, String achievementId) {
        if (fromId == null || toId == null) {
            return;
        }
        Optional<Item> from = BuiltInRegistries.ITEM.getOptional(ResourceLocation.tryParse(fromId));
        Optional<Item> to = BuiltInRegistries.ITEM.getOptional(ResourceLocation.tryParse(toId));
        if (from.isPresent() && to.isPresent()) {
            recipes.add(new EvolutionRecipe(new ItemStack(from.get()), new ItemStack(to.get()), achievementId));
        }
    }

    private List<CollapseCrystalRecipe> getCollapseRecipes() {
        if (collapseRecipes != null) {
            return collapseRecipes;
        }
        List<CollapseCrystalRecipe> recipes = new ArrayList<>();
        ItemStack crystal = new ItemStack(TccItems.COLLAPSE_CRYSTAL);
        recipes.add(new CollapseCrystalRecipe(crystal,
                collectTagItems(CollapseCrystalData.TRUE_SELF_MATERIALS),
                new ItemStack(TccItems.ZEN_WO)));
        recipes.add(new CollapseCrystalRecipe(crystal.copy(),
                collectTagItems(CollapseCrystalData.HEIYUAN_BAIHUA_MATERIALS),
                new ItemStack(TccItems.HEIYUAN_BAIHUA)));
        collapseRecipes = recipes;
        return recipes;
    }

    private List<ItemStack> collectTagItems(TagKey<Item> tag) {
        List<ItemStack> items = new ArrayList<>();
        for (Item item : BuiltInRegistries.ITEM) {
            if (new ItemStack(item).is(tag)) {
                items.add(new ItemStack(item));
            }
        }
        return items;
    }
}
