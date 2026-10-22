package com.xlxyvergil.tcc.event;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.compat.lootr.LootrCompat;
import com.xlxyvergil.tcc.network.NetworkHandler;
import com.xlxyvergil.tcc.util.HighlightSkillCurios;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * 高亮技能的<strong>服务端</strong>部分。
 *
 * 佩戴 pado_philipis / lueji_zhi_shou / kongmeng 之一后，按下技能键（默认 V）即扫描一次
 * 周围 RADIUS 格内"自身尚未开启的 Lootr 战利品箱子/运输矿车"与"刷怪笼"，
 * 坐标定向下发给该玩家；客户端保留并渲染 30 秒，之后自动收掉
 * （绿 = 战利品箱子，蓝 = 战利品矿车，红 = 刷怪笼）。
 *
 * 与旧实现的关键差别：<strong>没有周期轮询</strong>。旧版每 200 tick 全量扫描并下发一次，
 * 不管玩家在不在看；现在只有玩家按键才扫描，服务端在没人按键时零开销。
 *
 * 冷却为 0（玩家要求"按一次显示一次周边"），这里只保留 MIN_INTERVAL
 * 这个防连按门槛，用来挡住按住键/宏连发造成的重复全量扫描——它不是玩法冷却，
 * 正常手动按是碰不到的。
 *
 * 按键的<strong>佩戴门禁在客户端</strong>：没戴饰品时客户端根本不发包，按键等于不存在。
 * 本类的 hasHighlightingCurio 是同一判定的第二次校验，只在客户端与服务端不同步时才会拦下请求。
 */
@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class HighlightSkillHandler {

    /** 玩家周围扫描半径（格）。 */
    private static final int RADIUS = 64;
    private static final int RADIUS_SQ = RADIUS * RADIUS;
    /** 防连按门槛（tick）：0.5 秒内重复按键只处理一次。 */
    private static final int MIN_INTERVAL = 10;

    /** 上次发动技能的 gameTime，按玩家记录；登出/换维度时清理。 */
    private static final Map<UUID, Long> LAST_ACTIVATE = new HashMap<>();
    /** 已发动过技能的玩家：之后打开容器会自动重扫，使刚被开启的箱子高亮即时消失。 */
    private static final Set<UUID> ACTIVE_PLAYERS = new HashSet<>();

    private HighlightSkillHandler() {}

    /** 客户端按键后调用（此时已在服务端线程）。 */
    public static void activate(@Nullable ServerPlayer player) {
        if (player == null) {
            return;
        }
        long now = player.level().getGameTime();
        Long last = LAST_ACTIVATE.get(player.getUUID());
        if (last != null && now - last < MIN_INTERVAL) {
            return;
        }
        LAST_ACTIVATE.put(player.getUUID(), now);

        // 没戴饰品：正常情况下客户端已经拦掉这个包，走到这里说明两端饰品栏不同步，
        // 于是顺手清掉显示并撤销记录，避免残留辉光照旧挂着。
        if (!hasHighlightingCurio(player)) {
            ACTIVE_PLAYERS.remove(player.getUUID());
            NetworkHandler.sendLootrHighlights(player, List.of());
            NetworkHandler.sendSpawnerHighlights(player, List.of());
            return;
        }
        ACTIVE_PLAYERS.add(player.getUUID());
        refresh(player);
    }

    /** 重新扫描并下发高亮坐标（战利品目标 + 刷怪笼）；箱子与矿车的颜色区分在客户端做。 */
    private static void refresh(ServerPlayer player) {
        ServerLevel level = (ServerLevel) player.level();
        List<BlockPos> loot = new ArrayList<>();
        if (LootrCompat.isLoaded()) {
            scanUnopenedLootChests(level, player, loot);
            // 运输矿车是实体而非方块实体，需单独按实体遍历（Lootr 会把它替换为 LootrChestMinecartEntity）。
            LootrCompat.collectUnopenedCarts(level, player.blockPosition(), RADIUS, player.getUUID(), loot);
        }
        NetworkHandler.sendLootrHighlights(player, loot);
        NetworkHandler.sendSpawnerHighlights(player, scanSpawners(level, player));
    }

    /** 玩家打开容器（含 Lootr 箱子）时重扫一次，让刚被开启的箱子高亮即时消失。 */
    @SubscribeEvent
    public static void onOpenContainer(PlayerContainerEvent.Open event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        if (!ACTIVE_PLAYERS.contains(player.getUUID()) || !hasHighlightingCurio(player)) {
            return;
        }
        refresh(player);
    }

    /** 换维度：旧维度的坐标留在客户端会变成幽灵高亮，这里清空。 */
    @SubscribeEvent
    public static void onChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        clearPlayer(player);
        NetworkHandler.sendLootrHighlights(player, List.of());
        NetworkHandler.sendSpawnerHighlights(player, List.of());
    }

    /** 登出：清掉服务端为这名玩家保存的状态。 */
    @SubscribeEvent
    public static void onLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            clearPlayer(player);
        }
    }

    private static void clearPlayer(ServerPlayer player) {
        ACTIVE_PLAYERS.remove(player.getUUID());
        LAST_ACTIVATE.remove(player.getUUID());
    }

    /**
     * 玩家是否佩戴了授予高亮技能的三件饰品之一。
     *
     * 与客户端的按键门禁共用 HighlightSkillCurios.isWorn 这一份判定：
     * 客户端负责"没戴就什么都不做"，这里只是防止客户端与服务端不同步时的越权请求。
     */
    private static boolean hasHighlightingCurio(ServerPlayer player) {
        return HighlightSkillCurios.isWorn(player);
    }

    /** 收集玩家周围未开启的 Lootr 战利品箱坐标，追加到 out（矿车由调用方单独追加）。 */
    private static void scanUnopenedLootChests(ServerLevel level, ServerPlayer player, List<BlockPos> out) {
        BlockPos base = player.blockPosition();

        int minCX = (base.getX() - RADIUS) >> 4;
        int maxCX = (base.getX() + RADIUS) >> 4;
        int minCZ = (base.getZ() - RADIUS) >> 4;
        int maxCZ = (base.getZ() + RADIUS) >> 4;

        for (int cx = minCX; cx <= maxCX; cx++) {
            for (int cz = minCZ; cz <= maxCZ; cz++) {
                if (!level.getChunkSource().hasChunk(cx, cz)) {
                    continue;
                }
                LevelChunk chunk = level.getChunk(cx, cz);
                for (BlockEntity be : chunk.getBlockEntities().values()) {
                    BlockPos pos = be.getBlockPos();
                    if (pos.distSqr(base) > RADIUS_SQ) {
                        continue;
                    }
                    if (LootrCompat.isUnopened(level, pos, be, player.getUUID())) {
                        out.add(pos);
                    }
                }
            }
        }
    }

    /** 收集玩家周围的刷怪笼坐标。 */
    private static List<BlockPos> scanSpawners(ServerLevel level, ServerPlayer player) {
        BlockPos base = player.blockPosition();
        List<BlockPos> result = new ArrayList<>();

        int minCX = (base.getX() - RADIUS) >> 4;
        int maxCX = (base.getX() + RADIUS) >> 4;
        int minCZ = (base.getZ() - RADIUS) >> 4;
        int maxCZ = (base.getZ() + RADIUS) >> 4;

        for (int cx = minCX; cx <= maxCX; cx++) {
            for (int cz = minCZ; cz <= maxCZ; cz++) {
                if (!level.getChunkSource().hasChunk(cx, cz)) {
                    continue;
                }
                LevelChunk chunk = level.getChunk(cx, cz);
                for (BlockEntity be : chunk.getBlockEntities().values()) {
                    BlockPos pos = be.getBlockPos();
                    if (pos.distSqr(base) > RADIUS_SQ) {
                        continue;
                    }
                    if (be instanceof SpawnerBlockEntity) {
                        result.add(pos);
                    }
                }
            }
        }
        return result;
    }
}
