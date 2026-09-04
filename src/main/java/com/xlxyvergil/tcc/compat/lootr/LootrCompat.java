package com.xlxyvergil.tcc.compat.lootr;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fml.ModList;
import noobanidus.mods.lootr.api.blockentity.ILootBlockEntity;
import noobanidus.mods.lootr.data.ChestData;
import noobanidus.mods.lootr.data.DataStorage;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Lootr 兼容封装。
 *
 * <p>本 mod 对 Lootr 是<strong>软依赖（compileOnly）</strong>：只有在 Lootr 已安装时该功能才生效。
 * 因此所有直接引用 Lootr 类（{@link ILootBlockEntity}、{@link ChestData}、{@link DataStorage}）的方法，
 * 调用方都必须先经过 {@link #isLoaded()} 短路判断，避免在 Lootr 缺失时触发 {@link NoClassDefFoundError}。</p>
 */
public final class LootrCompat {

    private LootrCompat() {}

    public static boolean isLoaded() {
        return ModList.get().isLoaded("lootr");
    }

    public static boolean isLootrContainer(@Nullable BlockEntity be) {
        return be instanceof ILootBlockEntity;
    }

    @Nullable
    public static UUID getTileId(@Nullable BlockEntity be) {
        if (be instanceof ILootBlockEntity loot) {
            return loot.getTileId();
        }
        return null;
    }

    /**
     * 判断指定玩家是否<strong>尚未开过</strong>这个 Lootr 箱子。
     *
     * <p>只有当前相（{@link ChestData}）存在、且该玩家还没有生成过独立库存（{@link ChestData#getInventory(java.util.UUID)} 为空）
     * 时才返回 {@code true}。</p>
     */
    public static boolean isUnopened(ServerLevel level, BlockPos pos, @Nullable BlockEntity be, UUID playerId) {
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
