package com.xlxyvergil.tcc.mixin;

import com.xlxyvergil.tcc.util.ZhenWoGuard;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

/**
 * 真我结界激活期间把佩戴者从服务端玩家列表中剔除。
 * 玩家索敌走 NearestAttackableTargetGoal -> Level.getNearestPlayer -> players()，
 * 不经过 Level.getEntities，因此需要单独在这一层过滤。
 */
@Mixin(ServerLevel.class)
public abstract class ServerLevelPlayersMixin {

    @Inject(method = "players", at = @At("RETURN"), cancellable = true)
    private void tcc$excludeBarrierWearer(CallbackInfoReturnable<List<ServerPlayer>> cir) {
        List<ServerPlayer> result = cir.getReturnValue();
        if (result == null || result.isEmpty()) {
            return;
        }

        List<ServerPlayer> filtered = null;
        for (ServerPlayer player : result) {
            if (ZhenWoGuard.isBarrierActiveWearer(player)) {
                if (filtered == null) {
                    filtered = new ArrayList<>(result);
                }
                filtered.remove(player);
            }
        }

        if (filtered != null) {
            cir.setReturnValue(filtered);
        }
    }
}
