package com.xlxyvergil.tcc.network;

import com.xlxyvergil.tcc.capability.TccPlayerDataCapability;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * 仅在客户端加载，避免服务端触发 dist 检查错误。
 */
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
}
