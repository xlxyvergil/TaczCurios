package com.xlxyvergil.tcc.shop;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * 商店里的一条商品：物品原型 + 单价（下界合金废料）。
 */
public record TccShopEntry(Item item, int cost) {

    public static TccShopEntry of(Item item, int cost) {
        if (cost <= 0) {
            throw new IllegalArgumentException("cost must be positive: " + cost);
        }
        return new TccShopEntry(item, cost);
    }

    public ItemStack stack() {
        return new ItemStack(this.item);
    }
}
