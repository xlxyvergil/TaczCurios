package com.xlxyvergil.tcc.recipe;

import com.xlxyvergil.tcc.registries.TccItems;
import com.xlxyvergil.tcc.registries.TccRecipeSerializers;
import com.xlxyvergil.tcc.util.CollapseCrystalData;
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
 * 纠缠之缘合成：1 纠缠之缘 + 1 任意神之键/逐火之蛾饰品 → 该饰品的完全副本。
 * 仅消耗纠缠之缘；真我、黑渊白花不在目标 Tag 内，天然无法参与。
 */
public class JiuChanZhiYuanCraftRecipe extends CustomRecipe {

    public JiuChanZhiYuanCraftRecipe(ResourceLocation id) {
        super(id, CraftingBookCategory.MISC);
    }

    /** 恰好 1 个纠缠之缘 + 1 个神之键/逐火之蛾饰品，且无其他物品 */
    @Override
    public boolean matches(CraftingContainer container, Level level) {
        ItemStack entanglement = ItemStack.EMPTY;
        ItemStack target = ItemStack.EMPTY;
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack s = container.getItem(i);
            if (s.isEmpty()) continue;
            if (s.getItem() == TccItems.JIU_CHAN_ZHI_YUAN) {
                if (!entanglement.isEmpty()) return false; // 只允许 1 个纠缠之缘
                entanglement = s;
            } else if (CollapseCrystalData.groupOf(s) != null) {
                if (!target.isEmpty()) return false; // 只允许 1 个目标饰品
                target = s;
            } else {
                return false; // 有不认识的物品
            }
        }
        return !entanglement.isEmpty() && !target.isEmpty();
    }

    /** 返回目标饰品的副本（保留 NBT） */
    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess registryAccess) {
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack s = container.getItem(i);
            if (!s.isEmpty() && CollapseCrystalData.groupOf(s) != null) {
                ItemStack result = s.copy();
                result.setCount(1);
                return result;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return TccRecipeSerializers.JIU_CHAN_ZHI_YUAN_CRAFT.get();
    }

    /** 纠缠之缘消耗 1 个；目标饰品不消耗，原样保留在合成台 */
    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingContainer container) {
        NonNullList<ItemStack> remaining = NonNullList.withSize(container.getContainerSize(), ItemStack.EMPTY);
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack s = container.getItem(i);
            if (s.isEmpty()) continue;
            if (s.getItem() == TccItems.JIU_CHAN_ZHI_YUAN) {
                ItemStack left = s.copy();
                left.shrink(1);
                remaining.set(i, left);
            } else {
                remaining.set(i, s.copy());
            }
        }
        return remaining;
    }
}
