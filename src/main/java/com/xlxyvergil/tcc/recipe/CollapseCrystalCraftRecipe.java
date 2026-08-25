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

import java.util.ArrayList;
import java.util.List;

/**
 * 崩坏结晶合成配方：1 崩坏结晶 + 若干同组 3 阶饰品 → 记录或产出真我/黑渊白花（无序）。
 * <p>
 * 真我（逐火之蛾）与黑渊白花（神之键）各自需要收集 {@link CollapseCrystalData#REQUIRED_COUNT}
 * 种不同的 3 阶饰品。水晶初次合成时绑定目标组，之后只能与该组继续合成（防止两组混记录）。
 * 每次可放入任意数量的同组素材，未记录的会被记录（去重）；已记录过的素材无法再次贡献进度。
 * 当某组收集满后，本次合成直接产出真我/黑渊白花。
 */
public class CollapseCrystalCraftRecipe extends CustomRecipe {

    public CollapseCrystalCraftRecipe(ResourceLocation id) {
        super(id, CraftingBookCategory.MISC);
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        ItemStack crystal = ItemStack.EMPTY;
        List<ItemStack> materials = new ArrayList<>();
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack s = container.getItem(i);
            if (s.isEmpty()) continue;
            if (CollapseCrystalData.isCrystal(s)) {
                if (!crystal.isEmpty()) return false; // 只能有 1 颗水晶
                crystal = s;
            } else if (CollapseCrystalData.groupOf(s) != null) {
                materials.add(s);
            } else {
                return false; // 有不认识的物品
            }
        }
        if (crystal.isEmpty() || materials.isEmpty()) return false;

        // 无序多素材：所有素材必须属于同一组（神之键 或 逐火之蛾），不允许混搭
        TagKey<Item> group = CollapseCrystalData.groupOf(materials.get(0));
        for (int i = 1; i < materials.size(); i++) {
            if (CollapseCrystalData.groupOf(materials.get(i)) != group) return false;
        }

        // 若水晶已绑定组，素材必须属于该组
        TagKey<Item> bound = CollapseCrystalData.getBoundGroup(crystal);
        if (bound != null && bound != group) return false;

        // 至少需要一个未记录的素材才可合成
        for (ItemStack m : materials) {
            if (!CollapseCrystalData.isRecorded(crystal, group, m.getItem())) return true;
        }
        return false;
    }

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess registryAccess) {
        ItemStack crystal = ItemStack.EMPTY;
        List<ItemStack> materials = new ArrayList<>();
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack s = container.getItem(i);
            if (s.isEmpty()) continue;
            if (CollapseCrystalData.isCrystal(s)) {
                crystal = s;
            } else if (CollapseCrystalData.groupOf(s) != null) {
                materials.add(s);
            }
        }
        if (crystal.isEmpty() || materials.isEmpty()) return ItemStack.EMPTY;

        TagKey<Item> group = CollapseCrystalData.groupOf(materials.get(0));
        if (group == null) return ItemStack.EMPTY;
        TagKey<Item> bound = CollapseCrystalData.getBoundGroup(crystal);
        if (bound != null && bound != group) return ItemStack.EMPTY;

        ItemStack updated = crystal.copy();
        updated.setCount(1);

        // 初次合成时绑定目标组，后续只能与该组继续合成
        if (bound == null) {
            CollapseCrystalData.bindGroup(updated, group);
        }

        // 记录所有未记录的素材（去重）
        for (ItemStack m : materials) {
            if (!CollapseCrystalData.isRecorded(updated, group, m.getItem())) {
                CollapseCrystalData.record(updated, group, m.getItem());
            }
        }

        // 若该组收集满则产出成品饰品，否则返回记录后的水晶
        int count = CollapseCrystalData.getRecordedCount(updated, group);
        if (count >= CollapseCrystalData.REQUIRED_COUNT) {
            Item product = group == CollapseCrystalData.TRUE_SELF_MATERIALS ? TccItems.ZEN_WO : TccItems.HEIYUAN_BAIHUA;
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
