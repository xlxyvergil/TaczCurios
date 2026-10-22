package com.xlxyvergil.tcc.client;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.items.curios.bound.ZhenWo;
import com.xlxyvergil.tcc.network.NetworkHandler;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 真我结界的客户端输入与本地计时（游戏总线，仅客户端）。
 *
 * 按键只做一件事：请求服务端开启结界。服务端确认后会回一包剩余 tick，
 * 客户端据此自己倒计时（见 {@link ZhenWoBarrierClientData}），
 * 因此渲染不需要任何效果（buff）承载，也不会被外部塞进来的同名效果误触发。
 *
 * 技能属于饰品：<strong>没戴真我时按键完全没有任何反应</strong>，不发包、不打扰服务端。
 * 结界已在持续中时客户端照常发包，由服务端按饰品 NBT 判定后静默丢弃。
 *
 * 排队的点击无论条件是否成立都要当 tick 抽干，否则没戴饰品时按的那一下会攒在
 * clickCount 里，等下次戴上时突然补触发一次。
 */
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ZhenWoBarrierInput {

    private ZhenWoBarrierInput() {}

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            ZhenWoBarrierClientData.clear();
        } else {
            ZhenWoBarrierClientData.tick();
        }

        // 一次按键只发一包，且不留残余点击
        boolean activated = false;
        while (ZhenWoBarrierKeys.ACTIVATE.consumeClick()) {
            activated = true;
        }

        if (minecraft.level == null || minecraft.player == null || !activated) {
            return;
        }
        // 没戴真我：按键就当没按过
        if (!ZhenWo.isEquipped(minecraft.player)) {
            return;
        }
        NetworkHandler.sendActivateZhenWoBarrier();
    }

    /** 退出世界：清空本地结界计时，避免残留。 */
    @SubscribeEvent
    public static void onLoggedOut(ClientPlayerNetworkEvent.LoggingOut event) {
        ZhenWoBarrierClientData.clear();
    }
}
