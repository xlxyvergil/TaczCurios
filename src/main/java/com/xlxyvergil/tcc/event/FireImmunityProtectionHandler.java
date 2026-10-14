package com.xlxyvergil.tcc.event;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.items.curios.bound.BrahmaBeasts;
import com.xlxyvergil.tcc.items.curios.bound.Kosma;
import com.xlxyvergil.tcc.items.curios.bound.LimingZhiShao;
import com.xlxyvergil.tcc.items.curios.bound.Salvation;
import com.xlxyvergil.tcc.items.curios.bound.SummerBeach;
import com.xlxyvergil.tcc.items.curios.bound.Xuguang;
import com.xlxyvergil.tcc.util.ImmunityHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import java.util.Set;

/**
 * 火焰/岩浆免疫统一监听器。
 * 凯文 / 无烬之剑 / 救世 三件饰品佩戴后常驻生效（不判断手持武器）：
 * 免疫火焰与岩浆伤害，并持续熄灭自身火焰。
 * 科斯魔系列的 3 件饰品因持有岩浆行走，一并纳入以便站上岩浆表面。
 */
@EventBusSubscriber(modid = TaczCurios.MODID)
public final class FireImmunityProtectionHandler {

    private static final Set<ImmunityHelper.Type> IMMUNITIES = Set.of(ImmunityHelper.Type.FIRE);

    private FireImmunityProtectionHandler() {
    }

    private static boolean hasFireImmunity(LivingEntity entity) {
        return SummerBeach.isEquipped(entity)
                || BrahmaBeasts.isEquipped(entity)
                || Salvation.isEquipped(entity)
                || Kosma.isEquipped(entity)
                || LimingZhiShao.isEquipped(entity)
                || Xuguang.isEquipped(entity);
    }

    @SubscribeEvent
    public static void onLivingAttack(LivingIncomingDamageEvent event) {
        ImmunityHelper.cancelDamage(event, IMMUNITIES, FireImmunityProtectionHandler::hasFireImmunity);
    }

    @SubscribeEvent
    public static void onLivingTick(EntityTickEvent.Pre event) {
        Entity raw = event.getEntity();
        if (!(raw instanceof LivingEntity entity)) {
            return;
        }
        ImmunityHelper.extinguishFire(entity, FireImmunityProtectionHandler::hasFireImmunity);
    }
}
