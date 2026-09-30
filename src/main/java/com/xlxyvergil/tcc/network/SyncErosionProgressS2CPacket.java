package com.xlxyvergil.tcc.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * 服务端 → 客户端的侵蚀进度同步数据包。只在造成伤害的玩家本人客户端显示：
 * name 为被伤害实体显示名，progress 为其剩余进度（0.0~1.0，1.0 = 满值，0.0 = 归零消亡）。
 */
public record SyncErosionProgressS2CPacket(String name, float progress) {

    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(name);
        buf.writeFloat(progress);
    }

    public static SyncErosionProgressS2CPacket decode(FriendlyByteBuf buf) {
        return new SyncErosionProgressS2CPacket(buf.readUtf(), buf.readFloat());
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        SyncErosionProgressS2CPacket packet = this;
        ctx.get().enqueueWork(() ->
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandler.handleErosionProgress(packet))
        );
        ctx.get().setPacketHandled(true);
    }
}
