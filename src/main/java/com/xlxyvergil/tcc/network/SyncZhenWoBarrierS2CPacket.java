package com.xlxyvergil.tcc.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * 服务端 → 客户端：告知"某个玩家刚开启了真我结界，还剩多少 tick"。
 *
 * 结界不再用效果（buff）承载状态：客户端收到后自己按 tick 倒计时，
 * 因此渲染只由"按键开启"这一件事驱动，任何外部塞进来的同名效果都不会误触发特效。
 * 发给佩戴者本人与所有追踪他的玩家，多人环境下别人也能看到结界。
 */
public record SyncZhenWoBarrierS2CPacket(int entityId, int remainingTicks) {

    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(entityId);
        buf.writeVarInt(Math.max(remainingTicks, 0));
    }

    public static SyncZhenWoBarrierS2CPacket decode(FriendlyByteBuf buf) {
        int entityId = buf.readVarInt();
        int remainingTicks = buf.readVarInt();
        return new SyncZhenWoBarrierS2CPacket(entityId, remainingTicks);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        SyncZhenWoBarrierS2CPacket packet = this;
        ctx.get().enqueueWork(() ->
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandler.handleZhenWoBarrier(packet))
        );
        ctx.get().setPacketHandled(true);
    }
}
