package com.xlxyvergil.tcc.network;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.shop.TccShopCatalog;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 客户端 → 服务端：购买购物车里的商品。
 * <p>只传「目录下标 + 数量」，价格与物品一律由服务端按 {@code TccShopCatalog} 重新查表，
 * 客户端改数字也买不到便宜货。
 */
public record ShopBuyC2SPacket(List<Line> lines) implements CustomPacketPayload {

    /**
     * 单行数量上限：纯粹是玩法层面的夹紧（正常交易到不了这个数），
     * 和包体积无关——每行只有 index + count，37 行一共一百多字节。
     */
    public static final int MAX_COUNT_PER_LINE = 9999;

    public record Line(int index, int count) {

        public static final StreamCodec<RegistryFriendlyByteBuf, Line> STREAM_CODEC = new StreamCodec<>() {
            @Override
            public Line decode(RegistryFriendlyByteBuf buf) {
                return new Line(buf.readVarInt(), buf.readVarInt());
            }

            @Override
            public void encode(RegistryFriendlyByteBuf buf, Line line) {
                buf.writeVarInt(line.index());
                buf.writeVarInt(line.count());
            }
        };
    }

    public static final CustomPacketPayload.Type<ShopBuyC2SPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "shop_buy"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ShopBuyC2SPacket> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public ShopBuyC2SPacket decode(RegistryFriendlyByteBuf buf) {
            int size = buf.readVarInt();
            // 目录里一共只有 size() 件商品，行数不可能更多：按目录长度做结构校验，不设人为上限
            if (size < 0 || size > TccShopCatalog.size()) {
                throw new IllegalArgumentException(
                        "shop buy packet has " + size + " lines, catalog has " + TccShopCatalog.size());
            }
            List<Line> lines = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                lines.add(Line.STREAM_CODEC.decode(buf));
            }
            return new ShopBuyC2SPacket(List.copyOf(lines));
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, ShopBuyC2SPacket packet) {
            buf.writeVarInt(packet.lines().size());
            for (Line line : packet.lines()) {
                Line.STREAM_CODEC.encode(buf, line);
            }
        }
    };

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ShopBuyC2SPacket packet, IPayloadContext context) {
        if (context.player() instanceof ServerPlayer player) {
            context.enqueueWork(() -> ShopPurchaseHandler.buy(player, packet.lines()));
        }
    }
}
