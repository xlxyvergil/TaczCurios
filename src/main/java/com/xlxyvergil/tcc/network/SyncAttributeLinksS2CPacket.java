package com.xlxyvergil.tcc.network;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.link.AttributeLinkRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

/**
 * 服务端 → 客户端的属性联动规则同步包。
 * 规则表只在服务端生成，客户端连接服务器时为空，故登录时下发，客户端覆盖本地副本。
 */
public record SyncAttributeLinksS2CPacket(String json) implements CustomPacketPayload {

    /** JSON 文本长度上限，防止异常数据导致解码越界。 */
    private static final int MAX_LENGTH = 1 << 20;

    public static final CustomPacketPayload.Type<SyncAttributeLinksS2CPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "sync_attribute_links"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncAttributeLinksS2CPacket> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public SyncAttributeLinksS2CPacket decode(RegistryFriendlyByteBuf buf) {
            return new SyncAttributeLinksS2CPacket(buf.readUtf(MAX_LENGTH));
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, SyncAttributeLinksS2CPacket packet) {
            buf.writeUtf(packet.json(), MAX_LENGTH);
        }
    };

    /** 服务端调用：抓取当前生效的联动规则并打包。 */
    public static SyncAttributeLinksS2CPacket capture() {
        return new SyncAttributeLinksS2CPacket(AttributeLinkRegistry.toJson());
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SyncAttributeLinksS2CPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> ClientPacketHandler.handleSyncAttributeLinks(packet));
    }
}
