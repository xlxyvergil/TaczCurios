package com.xlxyvergil.tcc.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;


public record PacketSyncPlayTime(long griseo, long huishiZhijuan, long fanxing, long qishiZhijian) {

    public void encode(FriendlyByteBuf buf) {
        buf.writeLong(griseo);
        buf.writeLong(huishiZhijuan);
        buf.writeLong(fanxing);
        buf.writeLong(qishiZhijian);
    }

    public static PacketSyncPlayTime decode(FriendlyByteBuf buf) {
        return new PacketSyncPlayTime(buf.readLong(), buf.readLong(), buf.readLong(), buf.readLong());
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        PacketSyncPlayTime packet = this;
        ctx.get().enqueueWork(() ->
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandler.handlePlayTime(packet))
        );
        ctx.get().setPacketHandled(true);
    }
}
