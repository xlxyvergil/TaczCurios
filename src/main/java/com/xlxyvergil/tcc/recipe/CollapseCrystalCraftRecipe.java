package com.xlxyvergil.tcc.recipe;

import com.xlxyvergil.tcc.registries.TccItems;
import com.xlxyvergil.tcc.registries.TccRecipeSerializers;
import com.xlxyvergil.tcc.util.CollapseCrystalData;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

/**
 * 崩坏结晶合成配方：1 崩坏结晶 + 1 素材 3 阶饰品 → 记录或产出真我/黑渊白花。
 * <p>
 * 真我（逐火之蛾）与黑渊白花（神之键）各自需要收集 {@link CollapseCrystalData#REQUIRED_COUNT}
 * 种不同的 3 阶饰品。每放入一个未被记录的素材，水晶就记录该类型；
 * 已记录的素材类型无法再次合成（去重）。当某组收集满后，本次合成直接产出真我/黑渊白花。
 */
public class CollapseCrystalCraftRecipe extends CustomRecipe {

    public CollapseCrystalCraftRecipe(ResourceLocation id) {
        super(id, CraftingBookCategory.MISC);
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        ItemStack crystal = ItemStack.EMPTY;
        ItemStack material = ItemStack.EMPTY;
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack s = container.getItem(i);
            if (s.isEmpty()) continue;
            if (CollapseCrystalData.isCrystal(s)) {
                if (!crystal.isEmpty()) return false; // 只能有 1 颗水晶
                crystal = s;
            } else if (CollapseCrystalData.groupOf(s) != null) {
                if (!material.isEmpty()) return false; // 只能有 1 个素材
                material = s;
            } else {
                return false; // 有不认识的物品
            }
        }
        if (crystal.isEmpty() || material.isEmpty()) return false;

        TagKey<Item> group = CollapseCrystalData.groupOf(material);
        if (group == null) return false;
        // 已记录过的素材类型无法再次合成
        return !CollapseCrystalData.isRecorded(crystal, group, material.getItem());
    }

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess registryAccess) {
        ItemStack crystal = ItemStack.EMPTY;
        ItemStack material = ItemStack.EMPTY;
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack s = container.getItem(i);
            if (s.isEmpty()) continue;
            if (CollapseCrystalData.isCrystal(s)) {
                crystal = s;
            } else if (CollapseCrystalData.groupOf(s) != null) {
                material = s;
            }
        }
        if (crystal.isEmpty() || material.isEmpty()) return ItemStack.EMPTY;

        TagKey<Item> group = CollapseCrystalData.groupOf(material);
        if (group == null) return ItemStack.EMPTY;
        if (CollapseCrystalData.isRecorded(crystal, group, material.getItem())) return ItemStack.EMPTY;

        if (group == CollapseCrystalData.TRUE_SELF_MATERIALS) {
            return craftFor(crystal, group, material.getItem(), TccItems.ZEN_WO);
        }
        return craftFor(crystal, group, material.getItem(), TccItems.HEIYUAN_BAIHUA);
    }

    /**
     * 记录素材到水晶副本；若该组收集满则产出成品饰品，否则返回记录后的水晶。
     */
    private ItemStack craftFor(ItemStack crystal, TagKey<Item> group, Item material, Item product) {
        ItemStack updated = crystal.copy();
        updated.setCount(1);
        CollapseCrystalData.record(updated, group, material);
        int count = CollapseCrystalData.getRecordedCount(updated, group);
        if (count >= CollapseCrystalData.REQUIRED_COUNT) {
            return new ItemStack(product, 1);
        }
        return updated;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return TccRecipeSerializers.COLLAPSE_CRYSTAL_CRAFT.get();
    }

    /** 水晶与素材在合成台均被消耗；结果（记录后的水晶或成品）放入结果槽 */
    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingContainer container) {
        return NonNullList.withSize(container.getContainerSize(), ItemStack.EMPTY);
    }
}
