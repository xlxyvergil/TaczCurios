package com.xlxyvergil.tcc.client;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.registries.TccMobEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 客户端取消整个 RenderLivingEvent，模型、盔甲和手持物一起不渲染。
 * 原版隐身对玩家只让皮肤半透明，甲还是会飘着，所以这里得整个取消。
 */
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class QiXiZheBiRenderHandler {

    // 第三人称是否看得见自己。默认 false，要看得见改成 true
    private static final boolean SHOW_SELF_IN_THIRD_PERSON = false;

    private QiXiZheBiRenderHandler() {
    }

    @SubscribeEvent
    public static void onRenderLivingPre(RenderLivingEvent.Pre<?, ?> event) {
        if (!TccMobEffects.QI_XI_ZHE_BI.isPresent()) {
            return;
        }
        LivingEntity entity = event.getEntity();
        if (!entity.hasEffect(TccMobEffects.QI_XI_ZHE_BI.get())) {
            return;
        }
        if (SHOW_SELF_IN_THIRD_PERSON && entity == Minecraft.getInstance().player) {
            return;
        }
        event.setCanceled(true);
    }
}
