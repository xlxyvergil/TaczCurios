package com.xlxyvergil.tcc.network;

import com.xlxyvergil.tcc.items.curios.bound.ZhenWo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * 客户端 → 服务端：玩家按下真我结界技能键，请求开启结界。
 *
 * 包体为空——佩戴判定与"当前是否已开启"全部在服务端按饰品 NBT 决定，
 * 客户端只负责把"我按了键"告诉服务端；已开启时服务端静默丢弃。
 */
public record ActivateZhenWoBarrierC2SPacket() {

    public void encode(FriendlyByteBuf buf) {
        // 无字段
    }

    public static ActivateZhenWoBarrierC2SPacket decode(FriendlyByteBuf buf) {
        return new ActivateZhenWoBarrierC2SPacket();
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        ServerPlayer player = context.getSender();
        context.enqueueWork(() -> {
            if (player != null) {
                ZhenWo.activateByKey(player);
            }
        });
        context.setPacketHandled(true);
    }
}
