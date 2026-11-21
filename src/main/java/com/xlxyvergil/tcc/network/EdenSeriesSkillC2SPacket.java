package com.xlxyvergil.tcc.network;

import com.xlxyvergil.tcc.event.EdenSeriesSkillHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * 客户端 → 服务端：玩家按下伊甸系列技能键，请求结算一次光环。
 *
 * 包体为空——判定全部在服务端完成（是否佩戴三件之一、武器类型限制、技能冷却），
 * 客户端只负责把"我按了键"告诉服务端，拿不到任何可以让它自己伪造结果的信息。
 */
public record EdenSeriesSkillC2SPacket() {

    public void encode(FriendlyByteBuf buf) {
        // 无字段
    }

    public static EdenSeriesSkillC2SPacket decode(FriendlyByteBuf buf) {
        return new EdenSeriesSkillC2SPacket();
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        ServerPlayer player = context.getSender();
        context.enqueueWork(() -> {
            if (player != null) {
                EdenSeriesSkillHandler.activate(player);
            }
        });
        context.setPacketHandled(true);
    }
}
