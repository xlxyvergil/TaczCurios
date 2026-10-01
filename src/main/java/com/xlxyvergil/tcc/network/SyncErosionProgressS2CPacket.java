package com.xlxyvergil.tcc.network;

import com.xlxyvergil.tcc.TaczCurios;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

/**
 * 服务端 → 客户端的侵蚀进度同步数据包。只在造成伤害的玩家本人客户端显示：
 * name 为被伤害实体显示名，progress 为其剩余进度（0.0~1.0，1.0 = 满值，0.0 = 归零消亡）。
 */
public record SyncErosionProgressS2CPacket(String name, float progress) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SyncErosionProgressS2CPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "sync_erosion_progress"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncErosionProgressS2CPacket> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public SyncErosionProgressS2CPacket decode(RegistryFriendlyByteBuf buf) {
            return new SyncErosionProgressS2CPacket(buf.readUtf(), buf.readFloat());
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, SyncErosionProgressS2CPacket packet) {
            buf.writeUtf(packet.name());
            buf.writeFloat(packet.progress());
        }
    };

    public SyncErosionProgressS2CPacket {
        name = name == null ? "" : name;
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SyncErosionProgressS2CPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> ClientPacketHandler.handleErosionProgress(packet));
    }
}
