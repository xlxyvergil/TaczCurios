package com.xlxyvergil.tcc.event;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.items.curios.bound.BrahmaBeasts;
import com.xlxyvergil.tcc.items.curios.bound.Salvation;
import com.xlxyvergil.tcc.items.curios.bound.SummerBeach;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 火焰/岩浆免疫统一监听器。
 * 凯文 / 无烬之剑 / 救世 三件饰品佩戴后常驻生效（不判断手持武器）：
 * 免疫火焰与岩浆伤害，并持续熄灭自身火焰。
 */
@Mod.EventBusSubscriber(modid = TaczCurios.MODID)
public class FireImmunityProtectionHandler {

    private static boolean hasFireImmunity(LivingEntity entity) {
        return SummerBeach.isEquipped(entity)
                || BrahmaBeasts.isEquipped(entity)
                || Salvation.isEquipped(entity);
    }

    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide) {
            return;
        }
        if (!event.getSource().is(DamageTypeTags.IS_FIRE)) {
            return;
        }
        if (hasFireImmunity(entity)) {
            entity.clearFire();
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide || !entity.isOnFire()) {
            return;
        }
        if (hasFireImmunity(entity)) {
            entity.clearFire();
        }
    }
}
