package com.xlxyvergil.tcc.network;

import com.xlxyvergil.tcc.shop.TccShopCatalog;
import com.xlxyvergil.tcc.shop.TccShopEntry;
import com.xlxyvergil.tcc.shop.TccShopMenu;
import com.xlxyvergil.tcc.shop.ShopCurrency;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 服务端购买事务：校验 → 算总价 → 数废料 → 模拟背包容量 → 扣料 → 发货。
 * <p>全程在同一 tick 内完成。扣料一定发生在发货之前，且扣料只会让背包更空，
 * 因此「模拟时放得下」在扣料之后依然成立；不会出现扣了料却发不出货的情况。
 */
public final class ShopPurchaseHandler {

    private ShopPurchaseHandler() {
    }

    public static void buy(ServerPlayer player, List<ShopBuyC2SPacket.Line> lines) {
        if (!(player.containerMenu instanceof TccShopMenu menu) || !menu.stillValid(player)) {
            return;
        }
        if (lines == null || lines.isEmpty() || lines.size() > ShopBuyC2SPacket.MAX_LINES) {
            return;
        }

        // 同一下标合并，并夹紧数量；任何越界都整单拒绝
        Map<Integer, Integer> merged = new TreeMap<>();
        for (ShopBuyC2SPacket.Line line : lines) {
            int index = line.index();
            int count = line.count();
            if (count <= 0 || count > ShopBuyC2SPacket.MAX_COUNT_PER_LINE) {
                return;
            }
            if (index < 0 || index >= TccShopCatalog.size()) {
                return;
            }
            int sum = merged.merge(index, count, Integer::sum);
            if (sum > ShopBuyC2SPacket.MAX_COUNT_PER_LINE) {
                return;
            }
        }
        if (merged.isEmpty()) {
            return;
        }

        long total = 0L;
        for (Map.Entry<Integer, Integer> entry : merged.entrySet()) {
            total += (long) TccShopCatalog.entries().get(entry.getKey()).cost() * entry.getValue();
        }
        if (total <= 0L || total > Integer.MAX_VALUE) {
            return;
        }
        int totalCost = (int) total;

        if (countScrap(player) < totalCost) {
            fail(player, Component.translatable("tcc.shop.not_enough", ShopCurrency.name(), totalCost));
            return;
        }

        List<ItemStack> results = new ArrayList<>(merged.size());
        for (Map.Entry<Integer, Integer> entry : merged.entrySet()) {
            TccShopEntry shopEntry = TccShopCatalog.entries().get(entry.getKey());
            results.add(new ItemStack(shopEntry.item(), entry.getValue()));
        }

        if (!canFit(player, results)) {
            fail(player, Component.translatable("tcc.shop.full"));
            return;
        }

        removeScrap(player, totalCost);
        Inventory inventory = player.getInventory();
        for (ItemStack stack : results) {
            inventory.add(stack);
        }
        player.containerMenu.broadcastChanges();
        inventory.setChanged();

        player.displayClientMessage(Component.translatable("tcc.shop.bought", totalCost, ShopCurrency.name()),
                true);
        NetworkHandler.sendShopResult(player);
    }

    private static void fail(ServerPlayer player, Component message) {
        player.displayClientMessage(message, true);
    }

    private static int countScrap(ServerPlayer player) {
        Inventory inventory = player.getInventory();
        int total = 0;
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (ShopCurrency.isCurrency(stack)) {
                total += stack.getCount();
            }
        }
        return total;
    }

    private static void removeScrap(ServerPlayer player, int amount) {
        Inventory inventory = player.getInventory();
        int remaining = amount;
        for (int i = 0; i < inventory.getContainerSize() && remaining > 0; i++) {
            ItemStack stack = inventory.getItem(i);
            if (ShopCurrency.isCurrency(stack)) {
                int take = Math.min(stack.getCount(), remaining);
                stack.shrink(take);
                remaining -= take;
            }
        }
    }

    /**
     * 在背包副本上模拟 {@link Inventory#add(ItemStack)}：先并入未满的同类堆，再占空槽。
     * 模拟只会比真实更保守，不会更乐观。
     */
    private static boolean canFit(ServerPlayer player, List<ItemStack> results) {
        Inventory inventory = player.getInventory();
        List<ItemStack> slots = new ArrayList<>(inventory.getContainerSize());
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            slots.add(inventory.getItem(i).copy());
        }
        for (ItemStack result : results) {
            int remaining = result.getCount();
            for (ItemStack slot : slots) {
                if (remaining <= 0) {
                    break;
                }
                if (!slot.isEmpty() && ItemStack.isSameItemSameTags(slot, result)) {
                    int space = slot.getMaxStackSize() - slot.getCount();
                    if (space > 0) {
                        int moved = Math.min(space, remaining);
                        slot.grow(moved);
                        remaining -= moved;
                    }
                }
            }
            while (remaining > 0) {
                int free = -1;
                for (int i = 0; i < slots.size(); i++) {
                    if (slots.get(i).isEmpty()) {
                        free = i;
                        break;
                    }
                }
                if (free < 0) {
                    return false;
                }
                int moved = Math.min(result.getMaxStackSize(), remaining);
                slots.set(free, result.copyWithCount(moved));
                remaining -= moved;
            }
        }
        return true;
    }
}
