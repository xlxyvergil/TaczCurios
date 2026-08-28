package com.xlxyvergil.tcc.recipe;

import com.xlxyvergil.tcc.items.materials.FusionVesselItem;
import com.xlxyvergil.tcc.registries.TccItems;
import com.xlxyvergil.tcc.registries.TccRecipeSerializers;
import com.xlxyvergil.tcc.util.FusionData;
import com.xlxyvergil.tcc.util.FusionUpgradeUtil;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

/**
 * 饰品升级合成配方：饰品 + 融合容器 → 升级后的饰品 + 融合容器（返还）。
 * 此配方依赖容器 NBT 中的 CoreFusion 数量，合成后扣除对应数量并返还容器。
 */
public class FusionUpgradeRecipe extends CustomRecipe {

    private static final TagKey<Item> TCC_SLOT = TagKey.create(Registries.ITEM,
            new ResourceLocation("curios", "tcc_slot"));

    public FusionUpgradeRecipe(ResourceLocation id) {
        super(id, CraftingBookCategory.MISC);
    }

    /**
     * 检查合成格是否满足条件：
     * 1. 恰好有 1 个可升级饰品 + 1 个融合容器
     * 2. 融合容器内 CoreFusion ≥ 升下一级所需
     * 3. 饰品未达到封顶等级
     */
    @Override
    public boolean matches(CraftingContainer container, Level level) {
        ItemStack curio = ItemStack.EMPTY;
        ItemStack vessel = ItemStack.EMPTY;

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.isEmpty()) continue;

            if (stack.getItem() == TccItems.FUSION_VESSEL) {
                if (!vessel.isEmpty()) return false; // 只能有 1 个容器
                vessel = stack;
            } else if (isUpgradeableCurio(stack)) {
                if (!curio.isEmpty()) return false; // 只能有 1 个饰品
                curio = stack;
            } else {
                return false; // 有不认识的物品
            }
        }

        if (curio.isEmpty() || vessel.isEmpty()) return false;

        FusionData data = FusionData.from(curio);
        int curioLevel = data.level();
        int maxLevel = data.maxLevel();
        if (curioLevel >= maxLevel) return false; // 已满级

        // 只要容器内 CoreFusion 至少够升 1 级就算匹配
        int vesselCount = FusionVesselItem.getFusionCount(vessel);
        int costForOne = getCostForLevel(curioLevel, data.rarity());
        return vesselCount >= costForOne;
    }

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess registryAccess) {
        ItemStack curio = ItemStack.EMPTY;
        ItemStack vessel = ItemStack.EMPTY;

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.isEmpty()) continue;
            if (stack.getItem() == TccItems.FUSION_VESSEL) {
                vessel = stack;
            } else if (isUpgradeableCurio(stack)) {
                curio = stack;
            }
        }

        if (curio.isEmpty() || vessel.isEmpty()) return ItemStack.EMPTY;

        FusionData data = FusionData.from(curio);
        int curioLevel = data.level();
        int maxLevel = data.maxLevel();
        if (curioLevel >= maxLevel) return ItemStack.EMPTY;

        int vesselCount = FusionVesselItem.getFusionCount(vessel);
        // 计算当前容器内融合核心最多能升到多少级
        int targetLevel = getMaxAffordableLevel(curioLevel, maxLevel, data.rarity(), vesselCount);
        if (targetLevel <= curioLevel) return ItemStack.EMPTY;

        // 创建升级后的饰品（复制原有 NBT + 直接升到目标等级）
        ItemStack result = curio.copy();
        result.setCount(1);
        FusionUpgradeUtil.setLevel(result, targetLevel);

        return result;
    }

    /**
     * 能否在合成台中展示（给 JEI / 合成指南用）
     */
    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return TccRecipeSerializers.FUSION_UPGRADE.get();
    }

    /**
     * 合成后返还融合容器并扣除消耗的 CoreFusion。
     */
    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingContainer container) {
        NonNullList<ItemStack> remaining = NonNullList.withSize(container.getContainerSize(), ItemStack.EMPTY);

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.isEmpty()) continue;

            if (stack.getItem() == TccItems.FUSION_VESSEL) {
                // 找到饰品计算消耗
                ItemStack curio = ItemStack.EMPTY;
                for (int j = 0; j < container.getContainerSize(); j++) {
                    ItemStack s = container.getItem(j);
                    if (!s.isEmpty() && s.getItem() != TccItems.FUSION_VESSEL && isUpgradeableCurio(s)) {
                        curio = s;
                        break;
                    }
                }

                if (!curio.isEmpty()) {
                    FusionData data = FusionData.from(curio);
                    int curioLevel = data.level();
                    int maxLevel = data.maxLevel();
                    int vesselCount = FusionVesselItem.getFusionCount(stack);
                    int targetLevel = getMaxAffordableLevel(curioLevel, maxLevel, data.rarity(), vesselCount);
                    int cost = FusionUpgradeUtil.getUpgradeCost(targetLevel, data.rarity()) - FusionUpgradeUtil.getUpgradeCost(curioLevel, data.rarity());
                    ItemStack vesselLeft = stack.copy();
                    vesselLeft.setCount(1);
                    FusionVesselItem.setFusionCount(vesselLeft, FusionVesselItem.getFusionCount(vesselLeft) - cost);
                    remaining.set(i, vesselLeft);
                } else {
                    remaining.set(i, stack.copy());
                }
            }
            // 其他物品不留剩余
        }

        return remaining;
    }

    // 工具方法

    /**
     * 判断物品是否为可升级的 tcc_slot 饰品。
     */
    private static boolean isUpgradeableCurio(ItemStack stack) {
        if (stack.isEmpty()) return false;
        if (!stack.is(TCC_SLOT)) return false;
        return FusionData.from(stack).isUpgradeable();
    }

    /**
     * 从当前等级升到下一级所需的 CoreFusion 数量。
     */
    public static int getCostForLevel(int currentLevel, Rarity rarity) {
        return FusionUpgradeUtil.getUpgradeCost(currentLevel + 1, rarity) - FusionUpgradeUtil.getUpgradeCost(currentLevel, rarity);
    }

    /**
     * 计算当前容器内融合核心最多能升到多少级（不会超过 maxLevel）。
     */
    public static int getMaxAffordableLevel(int currentLevel, int maxLevel, Rarity rarity, int vesselCount) {
        int totalCost = 0;
        int level = currentLevel;
        while (level < maxLevel) {
            int stepCost = getCostForLevel(level, rarity);
            if (totalCost + stepCost > vesselCount) break;
            totalCost += stepCost;
            level++;
        }
        return level;
    }
}
