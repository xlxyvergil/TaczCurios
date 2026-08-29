package com.xlxyvergil.tcc.jei;

import net.minecraft.world.item.ItemStack;

/**
 * 一条进化路径：from (原饰品) → to (进化后的饰品)。
 * achievementId 用于借助 mod 已有的 lang 键渲染成就标题（advancement.<namespace>.<path>.title）。
 */
public record EvolutionRecipe(ItemStack from, ItemStack to, String achievementId) {
}
