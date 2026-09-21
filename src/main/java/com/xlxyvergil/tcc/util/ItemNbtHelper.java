package com.xlxyvergil.tcc.util;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

import java.util.function.Consumer;

/**
 * 物品自定义 NBT 的统一读写入口，数据存放于 {@link DataComponents#CUSTOM_DATA} 组件。
 */
public final class ItemNbtHelper {

    private ItemNbtHelper() {
    }

    /** 读取自定义 NBT 的副本；组件不存在时返回空 CompoundTag（永不返回 null）。 */
    public static CompoundTag getTag(ItemStack stack) {
        return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
    }

    /** 是否存有自定义 NBT。 */
    public static boolean hasTag(ItemStack stack) {
        return !getTag(stack).isEmpty();
    }

    /** 覆写自定义 NBT；tag 为空时移除该组件。 */
    public static void setTag(ItemStack stack, CompoundTag tag) {
        CustomData.set(DataComponents.CUSTOM_DATA, stack, tag);
    }

    /** 就地修改自定义 NBT。 */
    public static void updateTag(ItemStack stack, Consumer<CompoundTag> updater) {
        stack.update(DataComponents.CUSTOM_DATA, CustomData.EMPTY, data -> data.update(updater));
    }
}
