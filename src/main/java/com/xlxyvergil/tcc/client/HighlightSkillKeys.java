package com.xlxyvergil.tcc.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.xlxyvergil.tcc.TaczCurios;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 高亮技能的按键注册（mod 总线，仅客户端）。
 *
 * 默认键位 V，玩家可在"控制 → 按键绑定"里改；按键定义同时提供了语言键，
 * 名称与分类分别见 lang 中的 key.tcc.activate_highlight / key.categories.tcc。
 */
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class HighlightSkillKeys {

    /** 发动键：按下后请服务端扫描周围并刷新高亮。 */
    public static final KeyMapping ACTIVATE = new KeyMapping(
            "key.tcc.activate_highlight",
            InputConstants.Type.KEYSYM,
            InputConstants.KEY_V,
            "key.categories.tcc");

    private HighlightSkillKeys() {}

    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(ACTIVATE);
    }
}
