package com.xlxyvergil.tcc.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * 服务端 → 客户端：购买成功，清空购物车（失败不需要特别通知，客户端会自己按余额置灰按钮）。
 */
public record ShopResultS2CPacket(boolean success) {

    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(this.success);
    }

    public static ShopResultS2CPacket decode(FriendlyByteBuf buf) {
        return new ShopResultS2CPacket(buf.readBoolean());
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ShopResultS2CPacket packet = this;
        ctx.get().enqueueWork(() ->
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandler.handleShopResult(packet))
        );
        ctx.get().setPacketHandled(true);
    }
}
