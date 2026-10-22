package com.xlxyvergil.tcc.client;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.network.NetworkHandler;
import com.xlxyvergil.tcc.util.HighlightSkillCurios;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 高亮技能的客户端输入（游戏总线，仅客户端）：按键 → 发一次 C2S 激活包。
 *
 * 技能属于饰品而不是玩家：<strong>没戴对应饰品时按键完全没有任何反应</strong>——
 * 不发包、不让服务端扫描、也不去动已有高亮。门禁用客户端同步过来的饰品栏判定，
 * 服务端收到包之后仍会再验一次（那只是保险，不是主逻辑）。
 *
 * 排队的点击无论条件是否成立都要当 tick 抽干，否则没戴饰品时按的那一下会攒在
 * clickCount 里，等下次戴上（甚至下次进世界）时突然补触发一次扫描。
 *
 * 只在结果真正到达时刷新画面（服务端负责判定饰品、扫描、下发），客户端不自行推测。
 * 退出世界时清空残留坐标，避免下次进世界时看到上一个世界的幽灵辉光。
 */
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class HighlightSkillInput {

    private HighlightSkillInput() {}

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();

        // 先取走本 tick 排队的全部按下事件：一次按键只发一包，且不留下残余点击
        boolean activated = false;
        while (HighlightSkillKeys.ACTIVATE.consumeClick()) {
            activated = true;
        }

        if (minecraft.level == null || minecraft.player == null) {
            LootrHighlightClientData.clear();
            SpawnerHighlightClientData.clear();
            return;
        }
        if (!activated) {
            return;
        }
        // 没戴三件饰品之一：按键就当没按过，什么都不发生
        if (!HighlightSkillCurios.isWorn(minecraft.player)) {
            return;
        }
        NetworkHandler.sendActivateHighlight();
    }
}
