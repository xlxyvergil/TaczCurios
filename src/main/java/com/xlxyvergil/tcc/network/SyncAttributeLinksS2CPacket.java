package com.xlxyvergil.tcc.network;

import com.xlxyvergil.tcc.link.AttributeLinkRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * 服务端 → 客户端的属性联动规则同步包。
 * 规则表只在服务端生成，客户端连接服务器时为空，故登录时下发，客户端覆盖本地副本。
 */
public record SyncAttributeLinksS2CPacket(String json) {

    /** JSON 文本长度上限，防止异常数据导致解码越界。 */
    private static final int MAX_LENGTH = 1 << 20;

    /** 服务端调用：抓取当前生效的联动规则并打包。 */
    public static SyncAttributeLinksS2CPacket capture() {
        return new SyncAttributeLinksS2CPacket(AttributeLinkRegistry.toJson());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(json, MAX_LENGTH);
    }

    public static SyncAttributeLinksS2CPacket decode(FriendlyByteBuf buf) {
        return new SyncAttributeLinksS2CPacket(buf.readUtf(MAX_LENGTH));
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        SyncAttributeLinksS2CPacket packet = this;
        ctx.get().enqueueWork(() ->
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandler.handleSyncAttributeLinks(packet))
        );
        ctx.get().setPacketHandled(true);
    }
}
