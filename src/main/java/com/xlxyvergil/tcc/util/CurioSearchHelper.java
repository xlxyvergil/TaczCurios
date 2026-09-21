package com.xlxyvergil.tcc.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import java.util.function.Predicate;

public final class CurioSearchHelper {
    private CurioSearchHelper() {
    }

    public static ItemStack findFirstEquippedStack(LivingEntity livingEntity, Predicate<ItemStack> predicate) {
        if (livingEntity == null) {
            return ItemStack.EMPTY;
        }

        ICuriosItemHandler inv = CuriosApi.getCuriosInventory(livingEntity).orElse(null);
        if (inv == null) {
            return ItemStack.EMPTY;
        }

        for (var entry : inv.getCurios().entrySet()) {
            ICurioStacksHandler stacksHandler = entry.getValue();
            if (stacksHandler == null) {
                continue;
            }
            var handler = stacksHandler.getStacks();
            for (int i = 0; i < handler.getSlots(); i++) {
                ItemStack stack = handler.getStackInSlot(i);
                if (!stack.isEmpty() && predicate.test(stack)) {
                    return stack;
                }
            }
        }

        return ItemStack.EMPTY;
    }

    /** 遍历实体所有饰品槽位中的物品，对每个非空 ItemStack 执行回调。 */
    public static void forEachEquippedStack(LivingEntity livingEntity, java.util.function.Consumer<ItemStack> consumer) {
        if (livingEntity == null || consumer == null) {
            return;
        }
        ICuriosItemHandler inv = CuriosApi.getCuriosInventory(livingEntity).orElse(null);
        if (inv == null) {
            return;
        }
        for (var entry : inv.getCurios().entrySet()) {
            ICurioStacksHandler stacksHandler = entry.getValue();
            if (stacksHandler == null) {
                continue;
            }
            var handler = stacksHandler.getStacks();
            for (int i = 0; i < handler.getSlots(); i++) {
                ItemStack stack = handler.getStackInSlot(i);
                if (!stack.isEmpty()) {
                    consumer.accept(stack);
                }
            }
        }
    }
}
