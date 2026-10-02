package com.xlxyvergil.tcc.network;

import com.xlxyvergil.tcc.capability.TccPlayerDataCapability;
import com.xlxyvergil.tcc.client.LootrHighlightClientData;
import com.xlxyvergil.tcc.client.SpawnerHighlightClientData;
import com.xlxyvergil.tcc.client.TaczCuriosClientTooltip;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.evolution.AchievementDefinitions;
import com.xlxyvergil.tcc.evolution.EvolutionRegistry;
import com.xlxyvergil.tcc.evolution.KeyTierRegistry;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;


@OnlyIn(Dist.CLIENT)
final class ClientPacketHandler {

    private ClientPacketHandler() {}

    static void handleSyncProgress(SyncProgressS2CPacket packet) {
        var player = Minecraft.getInstance().player;
        if (player == null) return;

        String key = packet.key();
        int value = packet.value();

        if (key.startsWith("progress_")) {
            String achievementId = key.substring("progress_".length());
            TccPlayerDataCapability.setAchievementProgress(player, achievementId, value);
        } else if (key.startsWith("stat_")) {
            String statKey = key.substring("stat_".length());
            TccPlayerDataCapability.setCustomStat(player, statKey, value);
        } else if (key.startsWith("visited_")) {
            String rest = key.substring("visited_".length());
            int hashIdx = rest.indexOf('#');
            if (hashIdx >= 0) {
                String listKey = rest.substring(0, hashIdx);
                String id = rest.substring(hashIdx + 1);
                if (NetworkHandler.VISITED_BIOMES_KEY.equals(listKey)) {
                    TccPlayerDataCapability.addVisitedBiome(player, id);
                } else if (NetworkHandler.VISITED_DIMENSIONS_KEY.equals(listKey)) {
                    TccPlayerDataCapability.addVisitedDimension(player, id);
                }
            }
        }
    }

    static void handlePlayTime(PacketSyncPlayTime packet) {
        var player = Minecraft.getInstance().player;
        if (player == null) return;
        TccPlayerDataCapability.setPlayTimeGriseo(player, packet.griseo());
        TccPlayerDataCapability.setPlayTimeHuishiZhijuan(player, packet.huishiZhijuan());
        TccPlayerDataCapability.setPlayTimeFanxing(player, packet.fanxing());
        TccPlayerDataCapability.setPlayTimeQishiZhijian(player, packet.qishiZhijian());
    }

    static void handleLootrHighlights(SyncLootrHighlightsS2CPacket packet) {
        LootrHighlightClientData.setHighlights(packet.positions());
    }

    static void handleSpawnerHighlights(SyncSpawnerHighlightsS2CPacket packet) {
        SpawnerHighlightClientData.setHighlights(packet.positions());
    }

    /**
     * 用服务端下发的 TOML 覆盖本地配置值，覆盖后客户端所有 {@code COMMON_SPEC} 读取
     * 都会返回服务端值。失败时保留本地配置，不影响正常游玩。
     */
    static void handleSyncConfig(SyncConfigS2CPacket packet) {
        TaczCuriosConfig.applySyncedConfig(packet.toml());
    }

    /**
     * 用服务端下发的 JSON 覆盖本地数据文件并立即重新加载，
     * 使客户端的成就显示、进化提示、阶位判定与服务端一致。
     */
    static void handleSyncDataFiles(SyncDataFilesS2CPacket packet) {
        AchievementDefinitions.applySyncedText(packet.achievements());
        EvolutionRegistry.applySyncedText(packet.evolution());
        KeyTierRegistry.applySyncedText(packet.tiers());
        TaczCuriosClientTooltip.invalidateCache();
    }
}
