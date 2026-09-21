package com.xlxyvergil.tcc.network;

import com.xlxyvergil.tcc.TaczCurios;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

/**
 * 服务端 → 客户端的进度同步数据包。key 约定：成就进度「progress_<id>」、维度访问
 * 「visited_tcc_visited_dimensions#<id>」、群系访问「visited_tcc_visited_biomes#<id>」；客户端写入 Capability 供 tooltip 读取。
 */
public record SyncProgressS2CPacket(String key, int value) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SyncProgressS2CPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "sync_progress"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncProgressS2CPacket> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public SyncProgressS2CPacket decode(RegistryFriendlyByteBuf buf) {
            return new SyncProgressS2CPacket(buf.readUtf(), buf.readInt());
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, SyncProgressS2CPacket packet) {
            buf.writeUtf(packet.key());
            buf.writeInt(packet.value());
        }
    };

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SyncProgressS2CPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> ClientPacketHandler.handleSyncProgress(packet));
    }
}
