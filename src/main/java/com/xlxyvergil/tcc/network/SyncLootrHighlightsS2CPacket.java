package com.xlxyvergil.tcc.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * 服务端 → 客户端：同步"该玩家尚未开启过的 Lootr 箱子"坐标集合。
 * 仅发给触发检测的玩家本人，确保光柱只在该玩家客户端渲染。
 */
public record SyncLootrHighlightsS2CPacket(List<BlockPos> positions) {

    public SyncLootrHighlightsS2CPacket {
        positions = positions == null ? List.of() : positions;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(positions.size());
        for (BlockPos pos : positions) {
            buf.writeLong(pos.asLong());
        }
    }

    public static SyncLootrHighlightsS2CPacket decode(FriendlyByteBuf buf) {
        int size = buf.readVarInt();
        List<BlockPos> positions = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            positions.add(BlockPos.of(buf.readLong()));
        }
        return new SyncLootrHighlightsS2CPacket(positions);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        SyncLootrHighlightsS2CPacket packet = this;
        ctx.get().enqueueWork(() ->
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandler.handleLootrHighlights(packet))
        );
        ctx.get().setPacketHandled(true);
    }
}
