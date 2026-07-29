package com.xlxyvergil.tcc.network;

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
 * 客户端收到后将数据写入 Capability，供 tooltip 显示时读取。
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
        SyncProgressS2CPacket packet = this;
        ctx.get().enqueueWork(() ->
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandler.handleSyncProgress(packet))
        );
        ctx.get().setPacketHandled(true);
    }
}
