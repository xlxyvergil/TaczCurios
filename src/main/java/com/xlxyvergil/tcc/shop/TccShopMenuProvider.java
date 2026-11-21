package com.xlxyvergil.tcc.shop;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

/**
 * 打开泰辛商店；把方块坐标写进高级开界面包，客户端因此知道 stillValid 要校验哪个方块。
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

    @Override
    public void writeClientSideData(AbstractContainerMenu menu, RegistryFriendlyByteBuf buffer) {
        buffer.writeBlockPos(this.pos);
    }
}
