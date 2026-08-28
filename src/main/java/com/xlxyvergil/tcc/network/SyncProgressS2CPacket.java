package com.xlxyvergil.tcc.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * 服务端 → 客户端的进度同步数据包。key 约定：成就进度「progress_<id>」、维度访问
 * 「visited_tcc_visited_dimensions#<id>」、群系访问「visited_tcc_visited_biomes#<id>」；客户端写入 Capability 供 tooltip 读取。
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
