package com.xlxyvergil.tcc.mixin;

import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 在玩家加入世界后，markAllDirty() 标记所有统计数据为"脏"后，
 * 主动调用 sendStats() 将初始统计数据同步到客户端。
 * <p>
 * 配合 ServerStatsCounterMixin，确保服务端在登录时和每次 stat 变动后
 * 都主动推送给客户端，无需等待客户端的 REQUEST_STATS 请求。
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
