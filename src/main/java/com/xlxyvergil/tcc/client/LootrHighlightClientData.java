package com.xlxyvergil.tcc.client;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 客户端保存的"当前玩家尚未开启的战利品目标"坐标，并按方块/实体区分渲染颜色：
 * 箱子（方块实体）绿、运输矿车（实体）蓝。
 *
 * 服务端只下发一组坐标，区分在客户端完成——矿车是实体，按坐标查一下有没有矿车实体即可。
 * 分类在收到下发时做一次（此时客户端 level 可用），而不是每帧查询，避免逐帧的实体检索开销。
 *
 * 每次收到下发改写 expiresAt：轮廓只在收到的 DURATION_TICKS tick 内显示。
 */
@OnlyIn(Dist.CLIENT)
public final class LootrHighlightClientData {

    /** 高亮渲染持续时间（tick）：30 秒。 */
    private static final long DURATION_TICKS = 30L * 20L;

    private static final Set<BlockPos> CHESTS = new HashSet<>();
    private static final Set<BlockPos> CARTS = new HashSet<>();

    /** 本批坐标的到期 gameTime；0 表示从未收到过。 */
    private static long expiresAt;

    private LootrHighlightClientData() {}

    /** 用服务端下发的最新坐标覆盖旧集合，按"该位置有没有矿车实体"分成箱子组与矿车组，并重新计时。 */
    public static void setHighlights(List<BlockPos> positions) {
        CHESTS.clear();
        CARTS.clear();
        if (positions != null) {
            Level level = Minecraft.getInstance().level;
            for (BlockPos pos : positions) {
                if (isMinecartAt(level, pos)) {
                    CARTS.add(pos);
                } else {
                    CHESTS.add(pos);
                }
            }
        }
        expiresAt = nowTicks() + DURATION_TICKS;
    }

    /** 清空全部坐标（退出世界时用）。 */
    public static void clear() {
        CHESTS.clear();
        CARTS.clear();
        expiresAt = 0L;
    }

    /** 战利品箱子坐标（方块实体，绿色）。 */
    public static Set<BlockPos> getChests() {
        return CHESTS;
    }

    /** 战利品运输矿车坐标（实体，蓝色）。 */
    public static Set<BlockPos> getCarts() {
        return CARTS;
    }

    /** 是否还有未到期的坐标可画。 */
    public static boolean isActive() {
        return !(CHESTS.isEmpty() && CARTS.isEmpty()) && nowTicks() < expiresAt;
    }

    /** 该方块位置是否站着矿车（Lootr 把带战利品的运输矿车替换成自己的矿车实体，仍属于 AbstractMinecart）。 */
    private static boolean isMinecartAt(Level level, BlockPos pos) {
        if (level == null) {
            return false;
        }
        AABB search = new AABB(pos).inflate(0.5D);
        for (Entity entity : level.getEntitiesOfClass(AbstractMinecart.class, search)) {
            return true;
        }
        return false;
    }

    private static long nowTicks() {
        Minecraft minecraft = Minecraft.getInstance();
        return minecraft.level == null ? 0L : minecraft.level.getGameTime();
    }
}
