package com.xlxyvergil.tcc.network;

import com.xlxyvergil.tcc.TaczCurios;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 服务端 → 客户端：同步"该玩家周围的刷怪笼"坐标集合。
 * 仅发给触发检测的玩家本人，确保光柱只在该玩家客户端渲染。
 */
public record SyncSpawnerHighlightsS2CPacket(List<BlockPos> positions) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SyncSpawnerHighlightsS2CPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "sync_spawner_highlights"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncSpawnerHighlightsS2CPacket> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public SyncSpawnerHighlightsS2CPacket decode(RegistryFriendlyByteBuf buf) {
            int size = buf.readVarInt();
            List<BlockPos> positions = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                positions.add(BlockPos.of(buf.readLong()));
            }
            return new SyncSpawnerHighlightsS2CPacket(positions);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, SyncSpawnerHighlightsS2CPacket packet) {
            List<BlockPos> list = packet.positions();
            buf.writeVarInt(list.size());
            for (BlockPos pos : list) {
                buf.writeLong(pos.asLong());
            }
        }
    };

    public SyncSpawnerHighlightsS2CPacket {
        positions = positions == null ? List.of() : positions;
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SyncSpawnerHighlightsS2CPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> ClientPacketHandler.handleSpawnerHighlights(packet));
    }
}
