package com.xlxyvergil.tcc.network;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.evolution.AchievementDefinitions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;

public final class NetworkHandler {

    private static final String PROTOCOL_VERSION = "1";
    private static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(TaczCurios.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static final String PROGRESS_PREFIX = "tcc_ach_progress_";
    private static final String VISITED_DIMENSIONS_KEY = "tcc_visited_dimensions";
    private static final String VISITED_BIOMES_KEY = "tcc_visited_biomes";

    private static int packetId;

    private NetworkHandler() {}

    public static void init() {
        CHANNEL.registerMessage(packetId++, SyncProgressS2CPacket.class,
                SyncProgressS2CPacket::encode,
                SyncProgressS2CPacket::decode,
                SyncProgressS2CPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

    public static void sendToPlayer(ServerPlayer player, Object packet) {
        CHANNEL.sendTo(packet, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    /** 发送成就进度更新到客户端 */
    public static void syncAchievementProgress(ServerPlayer player, String achievementId, int progress) {
        sendToPlayer(player, new SyncProgressS2CPacket("progress_" + achievementId, progress));
    }

    /** 发送维度/群系访问记录到客户端 */
    public static void syncVisited(ServerPlayer player, String nbtKey, String id) {
        sendToPlayer(player, new SyncProgressS2CPacket("visited_" + nbtKey + "#" + id, 1));
    }

    /** 玩家登录时全量同步所有进度 */
    public static void syncAllForPlayer(ServerPlayer player) {
        CompoundTag data = player.getPersistentData();

        // 同步所有成就进度
        for (AchievementDefinitions.AchievementDef def : AchievementDefinitions.all()) {
            int progress = data.getInt(PROGRESS_PREFIX + def.id().replace(':', '_'));
            if (progress > 0) {
                syncAchievementProgress(player, def.id(), progress);
            }
        }

        // 同步已访问维度
        if (data.contains(VISITED_DIMENSIONS_KEY, net.minecraft.nbt.Tag.TAG_LIST)) {
            var list = data.getList(VISITED_DIMENSIONS_KEY, net.minecraft.nbt.Tag.TAG_STRING);
            for (var tag : list) {
                syncVisited(player, VISITED_DIMENSIONS_KEY, tag.getAsString());
            }
        }

        // 同步已访问群系
        if (data.contains(VISITED_BIOMES_KEY, net.minecraft.nbt.Tag.TAG_LIST)) {
            var list = data.getList(VISITED_BIOMES_KEY, net.minecraft.nbt.Tag.TAG_STRING);
            for (var tag : list) {
                syncVisited(player, VISITED_BIOMES_KEY, tag.getAsString());
            }
        }
    }
}
