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
 * Mixin to fix Refined Storage's {@code onCraftedShift} method so that
 * {@link net.minecraftforge.event.entity.player.PlayerEvent.ItemCraftedEvent}
 * fires per craft operation (matching vanilla / AE2 behavior) instead of
 * once for the entire batch.
 * <p>
 * Required = false: silently skipped when RS is not loaded.
 */
@Mixin(targets = "com.refinedmods.refinedstorage.apiimpl.network.grid.CraftingGridBehavior", remap = false)
public abstract class RSCraftingGridBehaviorMixin {

    /**
     * Shadow stub for the private {@code filterDuplicateStacks} method on the target class.
     * At runtime the bytecode from this {@code @Overwrite} method runs inside
     * {@code CraftingGridBehavior}, so this call resolves to the real private method.
     */
    @Shadow
    private void filterDuplicateStacks(INetwork network, CraftingContainer matrix, IStackList<ItemStack> availableItems) {
        throw new UnsupportedOperationException("Shadow method");
    }

    /**
     * Rewrites {@code onCraftedShift} to fire {@code ItemCraftedEvent}
     * once per craft operation instead of once for the entire batch.
     */
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

            // Fire event per craft operation (matches vanilla/AE2 behavior)
            craftedItem.onCraftedBy(player.level(), player, craftedItem.getCount());
            ForgeEventFactory.firePlayerCraftingEvent(player, craftedItem.copy(), grid.getCraftingMatrix());
        }

        ForgeHooks.setCraftingPlayer(null);
    }
}
