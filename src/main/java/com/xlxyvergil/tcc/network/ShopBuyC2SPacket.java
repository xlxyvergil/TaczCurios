package com.xlxyvergil.tcc.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * 客户端 → 服务端：购买购物车里的商品。
 * <p>只传「目录下标 + 数量」，价格与物品一律由服务端按 {@code TccShopCatalog} 重新查表，
 * 客户端改数字也买不到便宜货。
 */
public record ShopBuyC2SPacket(List<Line> lines) {

    /** 一次最多 64 行（目录只有 37 条，够用）。 */
    public static final int MAX_LINES = 64;
    /** 单行最多 64 个。 */
    public static final int MAX_COUNT_PER_LINE = 64;

    public record Line(int index, int count) {

        public void encode(FriendlyByteBuf buf) {
            buf.writeVarInt(index);
            buf.writeVarInt(count);
        }

        public static Line decode(FriendlyByteBuf buf) {
            return new Line(buf.readVarInt(), buf.readVarInt());
        }
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(this.lines.size());
        for (Line line : this.lines) {
            line.encode(buf);
        }
    }

    public static ShopBuyC2SPacket decode(FriendlyByteBuf buf) {
        int size = buf.readVarInt();
        if (size < 0 || size > MAX_LINES) {
            throw new IllegalArgumentException("shop buy packet has " + size + " lines, max " + MAX_LINES);
        }
        List<Line> decoded = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            decoded.add(Line.decode(buf));
        }
        return new ShopBuyC2SPacket(List.copyOf(decoded));
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ShopBuyC2SPacket packet = this;
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                ShopPurchaseHandler.buy(player, packet.lines());
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
