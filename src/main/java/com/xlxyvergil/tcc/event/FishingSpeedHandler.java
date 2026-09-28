package com.xlxyvergil.tcc.event;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.items.curios.bound.Kongmeng;
import com.xlxyvergil.tcc.items.curios.bound.LuejiZhiShou;
import com.xlxyvergil.tcc.items.curios.bound.PadoPhilipis;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 钓鱼加速统一监听器。
 * 帕朵菲利斯 / 掠集之兽 / 空梦三件饰品佩戴后，等待鱼咬钩的时间减半。
 * 只改 {@code timeUntilLured}（等待咬钩）这一个可写字段，不动 {@code lureSpeed}，
 * 因此与 Aquaculture 等渔业 mod 的加速机制各改各的、可叠加且不冲突。
 */
@Mod.EventBusSubscriber(modid = TaczCurios.MODID)
public class FishingSpeedHandler {

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Player player = event.player;
        if (player.level().isClientSide) {
            return;
        }
        FishingHook hook = player.fishing;
        // 至少保留 1 tick：
        if (hook == null || hook.timeUntilLured <= 1) {
            return;
        }
        if (!PadoPhilipis.isEquipped(player) && !LuejiZhiShou.isEquipped(player) && !Kongmeng.isEquipped(player)) {
            return;
        }
        // 每 tick 额外扣减 1 tick，相当于等待时间减半
        hook.timeUntilLured = Math.max(1, hook.timeUntilLured - 1);
    }
}
