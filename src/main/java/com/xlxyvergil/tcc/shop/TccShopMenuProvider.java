package com.xlxyvergil.tcc.shop;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

/**
 * 打开泰辛商店；方块坐标由调用方通过 {@code NetworkHooks.openScreen(player, provider, pos)}
 * 写进高级开界面包（1.20.1 的 MenuProvider 没有 writeClientSideData）。
 */
public record TccShopMenuProvider(BlockPos pos) implements MenuProvider {

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.tcc.teshin_workbench");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new TccShopMenu(containerId, playerInventory, this.pos);
    }
}
