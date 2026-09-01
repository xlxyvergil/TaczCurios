package com.xlxyvergil.tcc.network;

import com.xlxyvergil.tcc.capability.TccPlayerDataCapability;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


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
}
