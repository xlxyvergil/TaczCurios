package com.xlxyvergil.tcc.event;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.compat.lootr.LootrCompat;
import com.xlxyvergil.tcc.items.curios.bound.Kongmeng;
import com.xlxyvergil.tcc.items.curios.bound.LuejiZhiShou;
import com.xlxyvergil.tcc.items.curios.bound.PadoPhilipis;
import com.xlxyvergil.tcc.network.NetworkHandler;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * 服务端扫描：对<strong>佩戴 pado_philipis / lueji_zhi_shou / kongmeng 之一</strong>的玩家，
 * 每 {@link #SCAN_INTERVAL} tick 检测其周围 {@link #RADIUS} 格内"自己尚未开启的 Lootr 战利品箱子"，
 * 并将这些箱子的坐标通过 {@link SyncLootrHighlightsS2CPacket} 定向发给该玩家。
 *
 * <p>由于"某玩家是否开过"由 Lootr 的 {@code ChestData}（服务端 SavedData）记录，客户端无法读取，
 * 因此判断全部在服务端完成，客户端只负责把收到的坐标渲染成光柱。</p>
 */
@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class LootrLootBoxHighlightHandler {

    /** 扫描间隔（tick）。每 200 tick = 10 秒。 */
    private static final int SCAN_INTERVAL = 200;
    /** 玩家周围扫描半径（格）。 */
    private static final int RADIUS = 64;
    private static final int RADIUS_SQ = RADIUS * RADIUS;

    /** 记录当前"仍处于高亮状态（上一轮佩戴着饰品）"的玩家 UUID，用于在摘除时发一次空包清除残留光柱。 */
    private static final Set<UUID> ACTIVE_HIGHLIGHT_PLAYERS = new HashSet<>();

    private LootrLootBoxHighlightHandler() {}

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        // Lootr 未安装则完全跳过，避免触发 NoClassDefFoundError
        if (!LootrCompat.isLoaded()) {
            return;
        }
        MinecraftServer server = event.getServer();
        if (server.getTickCount() % SCAN_INTERVAL != 0) {
            return;
        }
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            UUID uuid = player.getUUID();
            if (hasHighlightingCurio(player)) {
                // 仅佩戴者才扫描
                ServerLevel level = (ServerLevel) player.level();
                List<BlockPos> highlighted = scanUnopenedChests(level, player);
                NetworkHandler.sendLootrHighlights(player, highlighted);
                ACTIVE_HIGHLIGHT_PLAYERS.add(uuid);
            } else if (ACTIVE_HIGHLIGHT_PLAYERS.remove(uuid)) {
                // 上一轮还佩戴、本轮已摘除：发一次空包清除客户端残留的光柱
                NetworkHandler.sendLootrHighlights(player, List.of());
            }
        }
    }

    /** 玩家是否佩戴了会触发高亮的三件饰品之一。 */
    private static boolean hasHighlightingCurio(ServerPlayer player) {
        ItemStack found = CurioSearchHelper.findFirstEquippedStack(player, stack ->
                stack.getItem() instanceof PadoPhilipis
                        || stack.getItem() instanceof LuejiZhiShou
                        || stack.getItem() instanceof Kongmeng);
        return !found.isEmpty();
    }

    /** 收集玩家周围未开启的 Lootr 箱子坐标。 */
    private static List<BlockPos> scanUnopenedChests(ServerLevel level, ServerPlayer player) {
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
                    if (LootrCompat.isUnopened(level, pos, be, player.getUUID())) {
                        result.add(pos);
                    }
                }
            }
        }
        return result;
    }
}
