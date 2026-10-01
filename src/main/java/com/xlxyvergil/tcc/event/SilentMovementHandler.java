package com.xlxyvergil.tcc.event;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.items.curios.bound.IslandBoomRaven;
import com.xlxyvergil.tcc.items.curios.bound.Raven;
import com.xlxyvergil.tcc.items.curios.bound.Xiora;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.event.VanillaGameEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 静音移动统一监听器。
 * 希奥拉 / 渡鸦 / 小岛爆爆鸦 三件饰品佩戴后常驻生效（不判断手持武器）：
 * 移动不再产生振动，因此不会触发幽匿尖啸体、幽匿传感器与监守者。
 */
@Mod.EventBusSubscriber(modid = TaczCurios.MODID)
public class SilentMovementHandler {

    private static boolean hasSilentMovement(LivingEntity entity) {
        return Xiora.isEquipped(entity)
                || Raven.isEquipped(entity)
                || IslandBoomRaven.isEquipped(entity);
    }

    @SubscribeEvent
    public static void onVanillaGameEvent(VanillaGameEvent event) {
        GameEvent gameEvent = event.getVanillaEvent();
        if (gameEvent != GameEvent.STEP
                && gameEvent != GameEvent.HIT_GROUND
                && gameEvent != GameEvent.SWIM
                && gameEvent != GameEvent.SPLASH) {
            return;
        }
        Entity cause = event.getCause();
        if (!(cause instanceof LivingEntity living)) {
            return;
        }
        if (hasSilentMovement(living)) {
            event.setCanceled(true);
        }
    }
}
