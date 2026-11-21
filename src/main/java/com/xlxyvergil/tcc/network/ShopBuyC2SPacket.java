package com.xlxyvergil.tcc.network;

import com.xlxyvergil.tcc.shop.TccShopCatalog;
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

    /**
     * 单行数量上限：纯粹是玩法层面的夹紧（正常交易到不了这个数），
     * 和包体积无关——每行只有 index(1B) + count(2B)，37 行一共一百多字节。
     */
    public static final int MAX_COUNT_PER_LINE = 9999;

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
        // 目录里一共只有 size() 件商品，行数不可能更多：按目录长度做结构校验，不设人为上限
        if (size < 0 || size > TccShopCatalog.size()) {
            throw new IllegalArgumentException(
                    "shop buy packet has " + size + " lines, catalog has " + TccShopCatalog.size());
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
