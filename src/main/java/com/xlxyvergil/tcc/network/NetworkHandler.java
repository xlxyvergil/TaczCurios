package com.xlxyvergil.tcc.network;

import com.xlxyvergil.tcc.capability.TccPlayerDataCapability;
import com.xlxyvergil.tcc.evolution.AchievementDefinitions;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.List;

/**
 * 网络消息注册中心（NeoForge Payload 系统）。由 TaczCurios 构造函数通过 modEventBus.addListener 注册。
 */
public final class NetworkHandler {

    private static final String PROTOCOL_VERSION = "1";

    static final String VISITED_DIMENSIONS_KEY = "tcc_visited_dimensions";
    static final String VISITED_BIOMES_KEY = "tcc_visited_biomes";

    private NetworkHandler() {}

    public static void registerPayloads(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(PROTOCOL_VERSION);
        registrar.playToClient(SyncProgressS2CPacket.TYPE, SyncProgressS2CPacket.STREAM_CODEC, SyncProgressS2CPacket::handle);
        registrar.playToClient(PacketSyncPlayTime.TYPE, PacketSyncPlayTime.STREAM_CODEC, PacketSyncPlayTime::handle);
        registrar.playToClient(SyncLootrHighlightsS2CPacket.TYPE, SyncLootrHighlightsS2CPacket.STREAM_CODEC, SyncLootrHighlightsS2CPacket::handle);
        registrar.playToClient(SyncSpawnerHighlightsS2CPacket.TYPE, SyncSpawnerHighlightsS2CPacket.STREAM_CODEC, SyncSpawnerHighlightsS2CPacket::handle);
    }

    private static void sendToPlayer(ServerPlayer player, CustomPacketPayload packet) {
        PacketDistributor.sendToPlayer(player, packet);
    }

    public static void sendLootrHighlights(ServerPlayer player, List<BlockPos> positions) {
        sendToPlayer(player, new SyncLootrHighlightsS2CPacket(positions));
    }

    public static void sendSpawnerHighlights(ServerPlayer player, List<BlockPos> positions) {
        sendToPlayer(player, new SyncSpawnerHighlightsS2CPacket(positions));
    }

    public static void syncAchievementProgress(ServerPlayer player, String achievementId, int progress) {
        sendToPlayer(player, new SyncProgressS2CPacket("progress_" + achievementId, progress));
    }

    public static void syncCustomStat(ServerPlayer player, String statKey, int value) {
        sendToPlayer(player, new SyncProgressS2CPacket("stat_" + statKey, value));
    }


    public static void syncPlayTime(ServerPlayer player) {
        sendToPlayer(player, new PacketSyncPlayTime(
                TccPlayerDataCapability.getPlayTimeGriseo(player),
                TccPlayerDataCapability.getPlayTimeHuishiZhijuan(player),
                TccPlayerDataCapability.getPlayTimeFanxing(player),
                TccPlayerDataCapability.getPlayTimeQishiZhijian(player)
        ));
    }

    public static void syncVisited(ServerPlayer player, String nbtKey, String id) {
        sendToPlayer(player, new SyncProgressS2CPacket("visited_" + nbtKey + "#" + id, 1));
    }

    public static void syncAllForPlayer(ServerPlayer player) {
        for (AchievementDefinitions.AchievementDef def : AchievementDefinitions.all()) {
            int progress = TccPlayerDataCapability.getAchievementProgress(player, def.id());
            if (progress > 0) {
                syncAchievementProgress(player, def.id(), progress);
            }
        }

        TccPlayerDataCapability.Handler handler = TccPlayerDataCapability.of(player);
        for (String dim : handler.getVisitedDimensions()) {
            syncVisited(player, VISITED_DIMENSIONS_KEY, dim);
        }
        for (String biome : handler.getVisitedBiomes()) {
            syncVisited(player, VISITED_BIOMES_KEY, biome);
        }

        syncEventStat(player, "tcc:zombie_villager_cured");
        syncEventStat(player, "tcc:items_crafted");
        syncPlayTime(player);
    }


    private static void syncEventStat(ServerPlayer player, String statKey) {
        int value = TccPlayerDataCapability.getCustomStat(player, statKey);
        if (value > 0) {
            syncCustomStat(player, statKey, value);
        }
    }
}
