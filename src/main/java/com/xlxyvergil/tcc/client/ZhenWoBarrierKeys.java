package com.xlxyvergil.tcc.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.xlxyvergil.tcc.TaczCurios;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

/**
 * 真我结界技能的按键注册（mod 总线，仅客户端）。
 *
 * 默认键位 X，玩家可在"控制 → 按键绑定 → TCC 饰品技能"里改；名称与分类分别见
 * lang 中的 key.tcc.activate_zhen_wo_barrier / key.categories.tcc。
 */
@EventBusSubscriber(value = Dist.CLIENT, modid = TaczCurios.MODID, bus = EventBusSubscriber.Bus.MOD)
public final class ZhenWoBarrierKeys {

    /** 开启结界键：按下后请服务端校验（是否佩戴、当前是否已开启）并展开真我结界。 */
    public static final KeyMapping ACTIVATE = new KeyMapping(
            "key.tcc.activate_zhen_wo_barrier",
            InputConstants.Type.KEYSYM,
            InputConstants.KEY_X,
            "key.categories.tcc");

    private ZhenWoBarrierKeys() {}

    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(ACTIVATE);
    }
}
