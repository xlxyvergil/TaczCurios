package com.xlxyvergil.tcc.jei;

import java.util.List;
import net.minecraft.world.item.ItemStack;

/**
 * 崩坏结晶合成路径：崩坏结晶 + 同组 12 种素材 → 真我 / 黑渊白花。
 */
public record CollapseCrystalRecipe(ItemStack crystal, List<ItemStack> materials, ItemStack output) {
}
