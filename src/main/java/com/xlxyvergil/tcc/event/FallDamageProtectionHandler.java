package com.xlxyvergil.tcc.event;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.items.curios.bound.Kongmeng;
import com.xlxyvergil.tcc.items.curios.bound.LuejiZhiShou;
import com.xlxyvergil.tcc.items.curios.bound.PadoPhilipis;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

/**
 * 摔落伤害免疫统一监听器。
 * 帕朵菲利斯 / 掠集之兽 / 空梦三件饰品佩戴后均免疫摔落伤害。
 */
@EventBusSubscriber(modid = TaczCurios.MODID)
public final class FallDamageProtectionHandler {

    private FallDamageProtectionHandler() {
    }

    @SubscribeEvent
    public static void onLivingFall(LivingFallEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide) {
            return;
        }
        if (PadoPhilipis.isEquipped(entity) || LuejiZhiShou.isEquipped(entity) || Kongmeng.isEquipped(entity)) {
            event.setCanceled(true);
        }
    }
}
