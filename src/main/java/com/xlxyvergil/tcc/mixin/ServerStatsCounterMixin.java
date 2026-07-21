package com.xlxyvergil.tcc.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.ServerStatsCounter;
import net.minecraft.stats.Stat;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 拦截 ServerStatsCounter.setValue()，在每次统计数据变动后主动同步给客户端。
 * 这样 stat_polling 类型的成就进度（以及所有其他统计）在客户端实时可见，
 * 无需等待玩家打开统计信息屏幕触发 REQUEST_STATS。
 */
@Mixin(ServerStatsCounter.class)
public abstract class ServerStatsCounterMixin {

    @Inject(method = "setValue", at = @At("TAIL"))
    private void tcc$onSetValue(Player player, Stat<?> stat, int value, CallbackInfo ci) {
        if (player instanceof ServerPlayer serverPlayer) {
            ((ServerStatsCounter) (Object) this).sendStats(serverPlayer);
        }
    }
}
