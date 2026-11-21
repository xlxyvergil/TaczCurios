package com.xlxyvergil.tcc.network;

import com.xlxyvergil.tcc.TaczCurios;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

/**
 * 服务端 → 客户端：购买成功，清空购物车（失败不需要特别通知，客户端会自己按余额置灰按钮）。
 */
public record ShopResultS2CPacket(boolean success) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ShopResultS2CPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "shop_result"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ShopResultS2CPacket> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public ShopResultS2CPacket decode(RegistryFriendlyByteBuf buf) {
            return new ShopResultS2CPacket(buf.readBoolean());
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, ShopResultS2CPacket packet) {
            buf.writeBoolean(packet.success());
        }
    };

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ShopResultS2CPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> ClientPacketHandler.handleShopResult(packet));
    }
}
