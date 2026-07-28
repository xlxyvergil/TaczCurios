package com.xlxyvergil.tcc.network;

import com.xlxyvergil.tcc.capability.TccPlayerDataCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * 服务端 → 客户端的进度同步数据包。
 * key 格式：
 *   - 成就进度: "progress_tcc:my_island"
 *   - 维度访问: "visited_tcc_visited_dimensions#minecraft:the_end"
 *   - 群系访问: "visited_tcc_visited_biomes#minecraft:plains"
 * <p>
 * 客户端收到后将数据写入 {@link TccPlayerDataCapability}，
 * 供 tooltip 显示时从 Capability 读取。
 */
public record SyncProgressS2CPacket(String key, int value) {

    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(key);
        buf.writeInt(value);
    }

    public static SyncProgressS2CPacket decode(FriendlyByteBuf buf) {
        return new SyncProgressS2CPacket(buf.readUtf(), buf.readInt());
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() ->
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                var player = Minecraft.getInstance().player;
                if (player == null) return;

                if (key.startsWith("progress_")) {
                    // 成就进度写入 Capability
                    String achievementId = key.substring("progress_".length());
                    TccPlayerDataCapability.setAchievementProgress(player, achievementId, value);
                } else if (key.startsWith("visited_")) {
                    // 维度/群系访问记录写入 Capability
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
            })
        );
        ctx.get().setPacketHandled(true);
    }
}
