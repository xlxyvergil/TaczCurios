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
 * 服务端 → 客户端：同步"该玩家周围的刷怪笼"坐标集合。
 * 仅发给触发检测的玩家本人，确保光柱只在该玩家客户端渲染。
 */
public record SyncSpawnerHighlightsS2CPacket(List<BlockPos> positions) {

    public SyncSpawnerHighlightsS2CPacket {
        positions = positions == null ? List.of() : positions;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(positions.size());
        for (BlockPos pos : positions) {
            buf.writeLong(pos.asLong());
        }
    }

    public static SyncSpawnerHighlightsS2CPacket decode(FriendlyByteBuf buf) {
        int size = buf.readVarInt();
        List<BlockPos> positions = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            positions.add(BlockPos.of(buf.readLong()));
        }
        return new SyncSpawnerHighlightsS2CPacket(positions);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        SyncSpawnerHighlightsS2CPacket packet = this;
        ctx.get().enqueueWork(() ->
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandler.handleSpawnerHighlights(packet))
        );
        ctx.get().setPacketHandled(true);
    }
}
