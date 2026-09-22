package com.xlxyvergil.tcc.compat.lootr;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import noobanidus.mods.lootr.api.blockentity.ILootBlockEntity;
import noobanidus.mods.lootr.data.ChestData;
import noobanidus.mods.lootr.data.DataStorage;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Lootr 类型隔离层。
 *
 * <p>本类是唯一允许出现 {@code noobanidus.mods.lootr.*} 类型的地方，且必须是包私有（包外无从引用）。
 * 它只会被 {@link LootrCompat} 在 {@code ModList.get().isLoaded("lootr")} 为真时调用，
 * 因此 Lootr 缺失时本类永远不会被加载/校验，也就不会解析到缺失的类型。</p>
 */
final class LootrCompatInternal {

    private LootrCompatInternal() {}

    static boolean isLootrContainer(@Nullable BlockEntity be) {
        return be instanceof ILootBlockEntity;
    }

    @Nullable
    static UUID getTileId(@Nullable BlockEntity be) {
        if (be instanceof ILootBlockEntity loot) {
            return loot.getTileId();
        }
        return null;
    }

    static boolean isUnopened(ServerLevel level, BlockPos pos, @Nullable BlockEntity be, UUID playerId) {
        if (be == null || playerId == null) {
            return false;
        }
        if (!(be instanceof ILootBlockEntity loot)) {
            return false;
        }
        UUID tileId = loot.getTileId();
        if (tileId == null) {
            return false;
        }
        ChestData data = DataStorage.getContainerData(level, pos, tileId);
        return data != null && data.getInventory(playerId) == null;
    }
}
