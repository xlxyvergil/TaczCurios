package com.xlxyvergil.tcc.network;

import com.xlxyvergil.tcc.TaczCurios;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

/**
 * 服务端 → 客户端：告知"某个玩家刚开启了真我结界，还剩多少 tick"。
 *
 * 结界不再用效果（buff）承载状态：客户端收到后自己按 tick 倒计时，
 * 因此渲染只由"按键开启"这一件事驱动，任何外部塞进来的同名效果都不会误触发特效。
 * 发给佩戴者本人与所有追踪他的玩家，多人环境下别人也能看到结界。
 *
 * 1.21.1 形态：NeoForge Payload（CustomPacketPayload + StreamCodec），
 * 注册处见 NetworkHandler#registerPayloads（registrar.playToClient）。
 */
public record SyncZhenWoBarrierS2CPacket(int entityId, int remainingTicks) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SyncZhenWoBarrierS2CPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "sync_zhen_wo_barrier"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncZhenWoBarrierS2CPacket> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public SyncZhenWoBarrierS2CPacket decode(RegistryFriendlyByteBuf buf) {
            return new SyncZhenWoBarrierS2CPacket(buf.readVarInt(), buf.readVarInt());
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, SyncZhenWoBarrierS2CPacket packet) {
            buf.writeVarInt(packet.entityId());
            buf.writeVarInt(Math.max(packet.remainingTicks(), 0));
        }
    };

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SyncZhenWoBarrierS2CPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> ClientPacketHandler.handleZhenWoBarrier(packet));
    }
}
