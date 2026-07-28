package com.xlxyvergil.tcc.recipe;

import com.xlxyvergil.tcc.items.materials.FusionVesselItem;
import com.xlxyvergil.tcc.registries.TccItems;
import com.xlxyvergil.tcc.registries.TccRecipeSerializers;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

/**
 * 融合容器合并配方：将多个融合容器在工作台合并为一个，
 * 合成后的容器内融核心数量为参与合成的容器数量之和。
 */
public class FusionVesselCombineRecipe extends CustomRecipe {

    public FusionVesselCombineRecipe(ResourceLocation id) {
        super(id, CraftingBookCategory.MISC);
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        int vesselCount = 0;
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.isEmpty()) continue;
            if (stack.getItem() == TccItems.FUSION_VESSEL) {
                vesselCount++;
            } else {
                return false; // 有不认识的物品
            }
        }
        return vesselCount >= 2;
    }

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess registryAccess) {
        int total = 0;
        int count = 0;
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.isEmpty()) continue;
            if (stack.getItem() == TccItems.FUSION_VESSEL) {
                total += FusionVesselItem.getFusionCount(stack);
                count++;
            }
        }
        if (count < 2) return ItemStack.EMPTY;

        ItemStack result = new ItemStack(TccItems.FUSION_VESSEL, 1);
        FusionVesselItem.setFusionCount(result, total);
        return result;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return TccRecipeSerializers.FUSION_VESSEL_COMBINE.get();
    }

    /** 所有融合容器全部消耗 */
    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingContainer container) {
        return NonNullList.withSize(container.getContainerSize(), ItemStack.EMPTY);
    }
}
