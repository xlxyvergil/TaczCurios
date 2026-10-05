package com.xlxyvergil.tcc.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * 服务端 → 客户端：命中造成伤害后清零渡鸦神之键的开镜蓄力。
 *
 * <p>服务端在 {@code EntityHurtByGunEvent.Post} 清除蓄力计数后下发本包，令客户端 HUD
 * 进度条同步归零，避免"显示仍在涨、实际增幅已归 0"的不一致。</p>
 */
public record ResetRavenChargeS2CPacket() {

    public void encode(FriendlyByteBuf buf) {
    }

    public static ResetRavenChargeS2CPacket decode(FriendlyByteBuf buf) {
        return new ResetRavenChargeS2CPacket();
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ResetRavenChargeS2CPacket packet = this;
        ctx.get().enqueueWork(() ->
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandler.handleResetRavenCharge(packet))
        );
        ctx.get().setPacketHandled(true);
    }
}
