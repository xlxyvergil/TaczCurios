package com.xlxyvergil.tcc.client;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.network.NetworkHandler;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

/**
 * 高亮技能的客户端输入（游戏总线，仅客户端）：按键 → 发一次 C2S 激活包。
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
        if (minecraft.level == null || minecraft.player == null) {
            LootrHighlightClientData.clear();
            SpawnerHighlightClientData.setHighlights(List.of());
            return;
        }
        // 一次按键只发一包：consumeClick 取走本 tick 排队的按下事件
        boolean activated = false;
        while (HighlightSkillKeys.ACTIVATE.consumeClick()) {
            activated = true;
        }
        if (activated) {
            NetworkHandler.sendActivateHighlight();
        }
    }
}
