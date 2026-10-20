package com.xlxyvergil.tcc.network;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.event.HighlightSkillHandler;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

/**
 * 客户端 → 服务端：玩家按下高亮技能键，请求扫描并刷新周围的高亮目标。
 *
 * 包体为空——判定全部在服务端完成（是否佩戴对应饰品、按压频率、扫描半径），
 * 客户端只负责把"我按了键"告诉服务端，拿不到任何可以让它自己伪造结果的信息。
 *
 * 1.21.1 形态：走 NeoForge Payload 系统（CustomPacketPayload + StreamCodec），
 * 注册处见 NetworkHandler#registerPayloads（registrar.playToServer）。
 */
public record ActivateHighlightC2SPacket() implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ActivateHighlightC2SPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "activate_highlight"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ActivateHighlightC2SPacket> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public ActivateHighlightC2SPacket decode(RegistryFriendlyByteBuf buf) {
            return new ActivateHighlightC2SPacket();
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, ActivateHighlightC2SPacket packet) {
            // 无字段
        }
    };

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ActivateHighlightC2SPacket packet, IPayloadContext context) {
        if (context.player() instanceof ServerPlayer player) {
            context.enqueueWork(() -> HighlightSkillHandler.activate(player));
        }
    }
}
