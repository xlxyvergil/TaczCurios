package com.xlxyvergil.tcc.network;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.event.EdenSeriesSkillHandler;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

/**
 * 客户端 → 服务端：玩家按下伊甸系列技能键，请求结算一次光环。
 *
 * 包体为空——判定全部在服务端完成（是否佩戴三件之一、武器类型限制、技能冷却），
 * 客户端只负责把"我按了键"告诉服务端，拿不到任何可以让它自己伪造结果的信息。
 *
 * 1.21.1 形态：走 NeoForge Payload 系统（CustomPacketPayload + StreamCodec），
 * 注册处见 NetworkHandler#registerPayloads（registrar.playToServer）。
 */
public record EdenSeriesSkillC2SPacket() implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<EdenSeriesSkillC2SPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "activate_eden_series_skill"));

    public static final StreamCodec<RegistryFriendlyByteBuf, EdenSeriesSkillC2SPacket> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public EdenSeriesSkillC2SPacket decode(RegistryFriendlyByteBuf buf) {
            return new EdenSeriesSkillC2SPacket();
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, EdenSeriesSkillC2SPacket packet) {
            // 无字段
        }
    };

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(EdenSeriesSkillC2SPacket packet, IPayloadContext context) {
        if (context.player() instanceof ServerPlayer player) {
            context.enqueueWork(() -> EdenSeriesSkillHandler.activate(player));
        }
    }
}
