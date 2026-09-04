package com.xlxyvergil.tcc.network;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.capability.TccPlayerDataCapability;
import com.xlxyvergil.tcc.evolution.AchievementDefinitions;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.List;
import java.util.Optional;

public final class NetworkHandler {

    private static final String PROTOCOL_VERSION = "1";
    private static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(TaczCurios.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    static final String VISITED_DIMENSIONS_KEY = "tcc_visited_dimensions";
    static final String VISITED_BIOMES_KEY = "tcc_visited_biomes";

    private static int packetId;

    private NetworkHandler() {}

    public static void init() {
        CHANNEL.registerMessage(packetId++, SyncProgressS2CPacket.class,
                SyncProgressS2CPacket::encode,
                SyncProgressS2CPacket::decode,
                SyncProgressS2CPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        CHANNEL.registerMessage(packetId++, PacketSyncPlayTime.class,
                PacketSyncPlayTime::encode,
                PacketSyncPlayTime::decode,
                PacketSyncPlayTime::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        CHANNEL.registerMessage(packetId++, SyncLootrHighlightsS2CPacket.class,
                SyncLootrHighlightsS2CPacket::encode,
                SyncLootrHighlightsS2CPacket::decode,
                SyncLootrHighlightsS2CPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

    public static void sendToPlayer(ServerPlayer player, Object packet) {
        CHANNEL.sendTo(packet, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    public static void sendLootrHighlights(ServerPlayer player, List<BlockPos> positions) {
        sendToPlayer(player, new SyncLootrHighlightsS2CPacket(positions));
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

        var handler = player.getCapability(TccPlayerDataCapability.CAPABILITY).orElse(null);
        if (handler != null) {
            for (String dim : handler.getVisitedDimensions()) {
                syncVisited(player, VISITED_DIMENSIONS_KEY, dim);
            }
            for (String biome : handler.getVisitedBiomes()) {
                syncVisited(player, VISITED_BIOMES_KEY, biome);
            }
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
