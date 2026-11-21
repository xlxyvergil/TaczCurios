package com.xlxyvergil.tcc.client;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.items.curios.bound.ZhenWo;
import com.xlxyvergil.tcc.network.NetworkHandler;
import com.xlxyvergil.tcc.util.EdenSeriesSkillCurios;
import com.xlxyvergil.tcc.util.HighlightSkillCurios;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 饰品技能的客户端输入（游戏总线，仅客户端）。
 *
 * 三套技能各有独立的键位（{@link HighlightSkillKeys}、{@link ZhenWoBarrierKeys}、
 * {@link EdenSeriesSkillKeys}，默认都是 V，可各自改键）：本类只负责把三个键的点击
 * 分别抽干，再按"当前戴了哪件饰品"分别发包。没戴的那一路完全静默——不发包、
 * 不让服务端扫描、也不去动已有状态。
 *
 * 排队的点击无论条件是否成立都要当 tick 抽干，否则没戴饰品时按的那一下会攒在
 * clickCount 里，等下次戴上（甚至下次进世界）时突然补触发一次。
 *
 * 同时承接真我结界与高亮结果的本地计时清理：退出世界、断线时清空，避免残留。
 */
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class SkillInput {

    private SkillInput() {}

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            ZhenWoBarrierClientData.clear();
            LootrHighlightClientData.clear();
            SpawnerHighlightClientData.clear();
        } else {
            ZhenWoBarrierClientData.tick();
        }

        // 三个键各自抽干本 tick 的排队点击：一次按键每个技能最多发一包，且不留残余点击
        boolean highlightPressed = false;
        while (HighlightSkillKeys.ACTIVATE.consumeClick()) {
            highlightPressed = true;
        }
        boolean zhenWoPressed = false;
        while (ZhenWoBarrierKeys.ACTIVATE.consumeClick()) {
            zhenWoPressed = true;
        }
        boolean edenPressed = false;
        while (EdenSeriesSkillKeys.ACTIVATE.consumeClick()) {
            edenPressed = true;
        }

        if (minecraft.level == null || minecraft.player == null) {
            return;
        }

        // 三套技能互不影响：按了哪个键、戴了哪件饰品，才发哪件的包
        Player player = minecraft.player;
        if (highlightPressed && HighlightSkillCurios.isWorn(player)) {
            NetworkHandler.sendActivateHighlight();
        }
        if (zhenWoPressed && ZhenWo.isEquipped(player)) {
            NetworkHandler.sendActivateZhenWoBarrier();
        }
        if (edenPressed && EdenSeriesSkillCurios.isWorn(player)) {
            NetworkHandler.sendActivateEdenSeriesSkill();
        }
    }

    /** 退出世界：清空本地结界计时，避免残留。 */
    @SubscribeEvent
    public static void onLoggedOut(ClientPlayerNetworkEvent.LoggingOut event) {
        ZhenWoBarrierClientData.clear();
    }
}
