package com.xlxyvergil.tcc.mixin;

import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.network.grid.INetworkAwareGrid;
import com.refinedmods.refinedstorage.api.util.Action;
import com.refinedmods.refinedstorage.api.util.IStackList;
import com.refinedmods.refinedstorage.apiimpl.API;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.PlayerMainInvWrapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.List;

/**
 * 修复 RS 的 onCraftedShift：使 ItemCraftedEvent 每次合成操作触发一次（与 vanilla/AE2 一致），
 * 而非整个批次仅一次；RS 未加载时静默跳过。
 */
@Mixin(targets = "com.refinedmods.refinedstorage.apiimpl.network.grid.CraftingGridBehavior", remap = false)
public abstract class RSCraftingGridBehaviorMixin {

    /**
     * 目标类私有方法 filterDuplicateStacks 的 Shadow 存根；此 @Overwrite 在目标类内执行，调用会解析到真正的私有方法。
     */
    @Shadow
    private void filterDuplicateStacks(INetwork network, CraftingContainer matrix, IStackList<ItemStack> availableItems) {
        throw new UnsupportedOperationException("Shadow method");
    }

    @Overwrite(remap = false)
    public void onCraftedShift(INetworkAwareGrid grid, Player player) {
        CraftingContainer matrix = grid.getCraftingMatrix();
        INetwork network = grid.getNetwork();
        List<ItemStack> craftedItemsList = new ArrayList<>();
        ItemStack crafted = grid.getCraftingResult().getItem(0);

        int maxCrafted = crafted.getMaxStackSize();
        int amountCrafted = 0;
        boolean useNetwork = network != null && grid.isGridActive();

        IStackList<ItemStack> availableItems = API.instance().createItemStackList();
        if (useNetwork) {
            filterDuplicateStacks(network, matrix, availableItems);
        }

        IStackList<ItemStack> usedItems = API.instance().createItemStackList();

        ForgeHooks.setCraftingPlayer(player);
        do {
            grid.onCrafted(player, availableItems, usedItems);
            craftedItemsList.add(crafted.copy());
            amountCrafted += crafted.getCount();
        } while (API.instance().getComparer().isEqual(crafted, grid.getCraftingResult().getItem(0)) && amountCrafted < maxCrafted && amountCrafted + crafted.getCount() <= maxCrafted);

        if (useNetwork) {
            usedItems.getStacks().forEach(stack -> network.extractItem(stack.getStack(), stack.getStack().getCount(), Action.PERFORM));
        }

        for (ItemStack craftedItem : craftedItemsList) {
            ItemStack remainder = ItemHandlerHelper.insertItem(
                    new PlayerMainInvWrapper(player.getInventory()),
                    craftedItem.copy(),
                    false
            );

            if (!remainder.isEmpty() && useNetwork) {
                remainder = network.insertItem(remainder, remainder.getCount(), Action.PERFORM);
            }

            if (!remainder.isEmpty()) {
                Containers.dropItemStack(player.getCommandSenderWorld(), player.getX(), player.getY(), player.getZ(), remainder);
            }

            // 每次合成操作触发事件（与 vanilla/AE2 行为一致）
            craftedItem.onCraftedBy(player.level(), player, craftedItem.getCount());
            ForgeEventFactory.firePlayerCraftingEvent(player, craftedItem.copy(), grid.getCraftingMatrix());
        }

        ForgeHooks.setCraftingPlayer(null);
    }
}
