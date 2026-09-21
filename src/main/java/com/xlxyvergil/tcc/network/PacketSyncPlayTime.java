package com.xlxyvergil.tcc.network;

import com.xlxyvergil.tcc.TaczCurios;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record PacketSyncPlayTime(long griseo, long huishiZhijuan, long fanxing, long qishiZhijian) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<PacketSyncPlayTime> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "sync_play_time"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PacketSyncPlayTime> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public PacketSyncPlayTime decode(RegistryFriendlyByteBuf buf) {
            return new PacketSyncPlayTime(buf.readLong(), buf.readLong(), buf.readLong(), buf.readLong());
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, PacketSyncPlayTime packet) {
            buf.writeLong(packet.griseo());
            buf.writeLong(packet.huishiZhijuan());
            buf.writeLong(packet.fanxing());
            buf.writeLong(packet.qishiZhijian());
        }
    };

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(PacketSyncPlayTime packet, IPayloadContext context) {
        context.enqueueWork(() -> ClientPacketHandler.handlePlayTime(packet));
    }
}
