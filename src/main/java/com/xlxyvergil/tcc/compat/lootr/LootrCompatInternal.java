package com.xlxyvergil.tcc.compat.lootr;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import noobanidus.mods.lootr.api.blockentity.ILootBlockEntity;
import noobanidus.mods.lootr.data.ChestData;
import noobanidus.mods.lootr.data.DataStorage;
import noobanidus.mods.lootr.entity.LootrChestMinecartEntity;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * Lootr 类型隔离层。
 *
 * 本类是唯一允许出现 noobanidus.mods.lootr.* 类型的地方，且必须是包私有（包外无从引用）。
 * 它只会被 LootrCompat 在 ModList.get().isLoaded("lootr") 为真时调用，
 * 因此 Lootr 缺失时本类永远不会被加载/校验，也就不会解析到缺失的类型。
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

    /**
     * 收集以 center 为中心、radius 格范围内该玩家尚未开启的 Lootr 运输矿车坐标，追加到 out。
     *
     * Lootr 会把带战利品表的原版运输矿车替换为 LootrChestMinecartEntity（实现 ILootCart），
     * 它是实体而非方块实体，因此无法通过方块实体遍历扫描到；这里单独按实体遍历，
     * 并用其 getOpeners()（关闭容器时记录）判断该玩家是否开过。
     */
    static void collectUnopenedCarts(ServerLevel level, BlockPos center, int radius, UUID playerId, List<BlockPos> out) {
        if (playerId == null) {
            return;
        }
        AABB area = new AABB(
                center.getX() - radius, center.getY() - radius, center.getZ() - radius,
                center.getX() + radius, center.getY() + radius, center.getZ() + radius);
        int radiusSq = radius * radius;
        for (LootrChestMinecartEntity cart : level.getEntitiesOfClass(LootrChestMinecartEntity.class, area)) {
            BlockPos pos = cart.blockPosition();
            if (pos.distSqr(center) > radiusSq) {
                continue;
            }
            if (!cart.getOpeners().contains(playerId)) {
                out.add(pos);
            }
        }
    }
}
