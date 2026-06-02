package com.xlxyvergil.tcc.util;

import com.tacz.guns.resource.modifier.AttachmentPropertyManager;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import java.util.Optional;

public final class CurioGrantHelper {
    private CurioGrantHelper() {
    }

    public enum OverflowMode {
        SKIP,
        INVENTORY_THEN_DROP,
        DROP,
        REPLACE_FIRST
    }

    public static boolean give(Player player, ItemStack stack, OverflowMode overflowMode) {
        if (stack.isEmpty()) {
            return false;
        }

        return CuriosApi.getCuriosInventory(player)
                .map(inv -> give(inv, player, stack, overflowMode))
                .orElse(false);
    }

    private static boolean give(ICuriosItemHandler inv, Player player, ItemStack stack, OverflowMode overflowMode) {
        Optional<String> slotIdOpt = findSlotId(inv, stack);
        if (slotIdOpt.isEmpty()) {
            return false;
        }

        String slotId = slotIdOpt.get();
        ICurioStacksHandler stacksHandler = inv.getCurios().get(slotId);
        if (stacksHandler == null) {
            return false;
        }

        top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler handler = stacksHandler.getStacks();
        for (int i = 0; i < handler.getSlots(); i++) {
            if (!handler.getStackInSlot(i).isEmpty()) {
                continue;
            }
            handler.setStackInSlot(i, stack);
            onEquip(player, slotId, stacksHandler, i, stack);
            return true;
        }

        return switch (overflowMode) {
            case SKIP -> false;
            case DROP -> {
                player.drop(stack, false);
                yield true;
            }
            case INVENTORY_THEN_DROP -> {
                player.getInventory().placeItemBackInInventory(stack);
                yield true;
            }
            case REPLACE_FIRST -> {
                if (handler.getSlots() <= 0) {
                    yield false;
                }
                ItemStack old = handler.getStackInSlot(0);
                handler.setStackInSlot(0, stack);
                onEquip(player, slotId, stacksHandler, 0, stack);
                if (!old.isEmpty()) {
                    player.getInventory().placeItemBackInInventory(old);
                }
                yield true;
            }
        };
    }

    private static Optional<String> findSlotId(ICuriosItemHandler inv, ItemStack stack) {
        for (String slotId : inv.getCurios().keySet()) {
            TagKey<Item> slotTag = TagKey.create(Registries.ITEM, new ResourceLocation("curios", slotId));
            if (stack.is(slotTag)) {
                return Optional.of(slotId);
            }
        }
        return Optional.empty();
    }

    private static void onEquip(Player player, String slotId, ICurioStacksHandler stacksHandler, int index, ItemStack stack) {
        boolean hasRenderer = stacksHandler.getRenders().size() > index && stacksHandler.getRenders().get(index);
        SlotContext slotContext = new SlotContext(slotId, player, index, false, hasRenderer);
        if (stack.getItem() instanceof ICurioItem curio) {
            curio.onEquip(slotContext, ItemStack.EMPTY, stack);
        }
        AttachmentPropertyManager.postChangeEvent(player, player.getMainHandItem());
    }
}
