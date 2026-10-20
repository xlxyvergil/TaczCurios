package com.xlxyvergil.tcc.network;

import com.xlxyvergil.tcc.event.HighlightSkillHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * 客户端 → 服务端：玩家按下高亮技能键，请求扫描并刷新周围的高亮目标。
 *
 * 包体为空——判定全部在服务端完成（是否佩戴对应饰品、按压频率、扫描半径），
 * 客户端只负责把"我按了键"告诉服务端，拿不到任何可以让它自己伪造结果的信息。
 */
public record ActivateHighlightC2SPacket() {

    public void encode(FriendlyByteBuf buf) {
        // 无字段
    }

    public static ActivateHighlightC2SPacket decode(FriendlyByteBuf buf) {
        return new ActivateHighlightC2SPacket();
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        ServerPlayer player = context.getSender();
        context.enqueueWork(() -> HighlightSkillHandler.activate(player));
        context.setPacketHandled(true);
    }
}
