package com.xlxyvergil.tcc.shop;

import com.xlxyvergil.tcc.registries.TccBlocks;
import com.xlxyvergil.tcc.registries.TccMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * 泰辛商店菜单。
 * <p>商店本身是纯客户端的列表/购物车界面，服务端不保存任何库存状态；
 * 玩家背包槽位只是挂在这里，让 {@link net.minecraft.server.level.ServerPlayer#tick()} 的
 * {@code broadcastChanges()} 能把购买后的背包变化同步给客户端（界面里要显示「持有 N」）。
 * 这些槽位放在画面外，并且点击被 {@code TeshinShopScreen#slotClicked} 拦截。
 */
public class TccShopMenu extends AbstractContainerMenu {

    private final BlockPos pos;

    public TccShopMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf extraData) {
        this(containerId, playerInventory, extraData.readBlockPos());
    }

    public TccShopMenu(int containerId, Inventory playerInventory, BlockPos pos) {
        super(TccMenus.TESHIN_SHOP, containerId);
        this.pos = pos;
        // 只为同步背包内容，位置放到屏幕外
        for (int i = 0; i < playerInventory.getContainerSize(); i++) {
            this.addSlot(new Slot(playerInventory, i, -10000, -10000));
        }
    }

    public BlockPos getPos() {
        return this.pos;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return player.canInteractWithBlock(this.pos, 4.0D)
                && player.level().getBlockState(this.pos).is(TccBlocks.TESHIN_WORKBENCH);
    }
}
