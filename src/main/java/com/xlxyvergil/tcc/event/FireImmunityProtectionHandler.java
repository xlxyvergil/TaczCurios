package com.xlxyvergil.tcc.event;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.items.curios.bound.BrahmaBeasts;
import com.xlxyvergil.tcc.items.curios.bound.Salvation;
import com.xlxyvergil.tcc.items.curios.bound.SummerBeach;
import com.xlxyvergil.tcc.util.ImmunityHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Set;

/**
 * 火焰/岩浆免疫统一监听器。
 * 凯文 / 无烬之剑 / 救世 三件饰品佩戴后常驻生效（不判断手持武器）：
 * 免疫火焰与岩浆伤害，并持续熄灭自身火焰。
 */
@Mod.EventBusSubscriber(modid = TaczCurios.MODID)
public class FireImmunityProtectionHandler {

    private static final Set<ImmunityHelper.Type> IMMUNITIES = Set.of(ImmunityHelper.Type.FIRE);

    private static boolean hasFireImmunity(LivingEntity entity) {
        return SummerBeach.isEquipped(entity)
                || BrahmaBeasts.isEquipped(entity)
                || Salvation.isEquipped(entity);
    }

    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event) {
        ImmunityHelper.cancelDamage(event, IMMUNITIES, FireImmunityProtectionHandler::hasFireImmunity);
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        ImmunityHelper.extinguishFire(event.getEntity(), FireImmunityProtectionHandler::hasFireImmunity);
    }
}
