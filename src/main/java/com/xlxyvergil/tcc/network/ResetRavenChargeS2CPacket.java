package com.xlxyvergil.tcc.network;

import com.xlxyvergil.tcc.TaczCurios;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

/**
 * 服务端 → 客户端：命中造成伤害后清零渡鸦神之键的开镜蓄力。
 *
 * <p>服务端在 {@code EntityHurtByGunEvent.Post} 清除蓄力计数后下发本包，令客户端 HUD
 * 进度条同步归零，避免"显示仍在涨、实际增幅已归 0"的不一致。</p>
 */
public record ResetRavenChargeS2CPacket() implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ResetRavenChargeS2CPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "reset_raven_charge"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ResetRavenChargeS2CPacket> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public ResetRavenChargeS2CPacket decode(RegistryFriendlyByteBuf buf) {
            return new ResetRavenChargeS2CPacket();
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, ResetRavenChargeS2CPacket packet) {
        }
    };

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ResetRavenChargeS2CPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> ClientPacketHandler.handleResetRavenCharge(packet));
    }
}
