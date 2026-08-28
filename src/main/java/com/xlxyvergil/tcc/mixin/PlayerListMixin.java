package com.xlxyvergil.tcc.mixin;

import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 玩家加入世界后主动调用 sendStats() 同步初始统计，配合 ServerStatsCounterMixin
 * 让服务端在登录时及每次 stat 变动后都主动推送给客户端，无需等待客户端的 REQUEST_STATS。
 */
@Mixin(PlayerList.class)
public abstract class PlayerListMixin {

    @Inject(
            method = "placeNewPlayer",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/stats/ServerStatsCounter;markAllDirty()V",
                    shift = At.Shift.AFTER
            )
    )
    private void tcc$afterMarkAllDirty(Connection connection, ServerPlayer player, CallbackInfo ci) {
        player.getStats().sendStats(player);
    }
}
