package com.xlxyvergil.tcc.network;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.items.curios.bound.ZhenWo;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

/**
 * 客户端 → 服务端：玩家按下真我结界技能键，请求开启结界。
 *
 * 包体为空——佩戴判定与"当前是否已开启"全部在服务端按饰品 NBT 决定，
 * 客户端只负责把"我按了键"告诉服务端；已开启时服务端静默丢弃。
 *
 * 1.21.1 形态：走 NeoForge Payload 系统（CustomPacketPayload + StreamCodec），
 * 注册处见 NetworkHandler#registerPayloads（registrar.playToServer）。
 */
public record ActivateZhenWoBarrierC2SPacket() implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ActivateZhenWoBarrierC2SPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "activate_zhen_wo_barrier"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ActivateZhenWoBarrierC2SPacket> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public ActivateZhenWoBarrierC2SPacket decode(RegistryFriendlyByteBuf buf) {
            return new ActivateZhenWoBarrierC2SPacket();
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, ActivateZhenWoBarrierC2SPacket packet) {
            // 无字段
        }
    };

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ActivateZhenWoBarrierC2SPacket packet, IPayloadContext context) {
        if (context.player() instanceof ServerPlayer player) {
            context.enqueueWork(() -> ZhenWo.activateByKey(player));
        }
    }
}
