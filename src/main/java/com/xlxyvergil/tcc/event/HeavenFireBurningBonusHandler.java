package com.xlxyvergil.tcc.event;

import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.items.curios.bound.HeavenFireApocalypse;
import com.xlxyvergil.tcc.items.curios.bound.HeavenFireApocalypseEndless;
import com.xlxyvergil.tcc.items.curios.bound.HeavenFireJudgment;
import com.xlxyvergil.tcc.util.GunTypeChecker;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

/**
 * 天火系列神之键：攻击处于着火状态的目标时提供额外伤害。
 * <p>
 * 使用 {@link EntityHurtByGunEvent.Pre} 而不是 {@code LivingHurtEvent}：Pre 在子弹点燃目标之前发布，
 * 因此本次命中自身造成的点燃不会给自己加伤，只有目标在命中前就已着火才吃加成。
 * 三把神之键共用一份加成，同时佩戴多件也只结算一次，避免倍率叠乘。
 */
@Mod.EventBusSubscriber(modid = "tcc", bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class HeavenFireBurningBonusHandler {
    private static final List<String> PISTOL_TYPES = List.of("pistol");

    private HeavenFireBurningBonusHandler() {
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onGunHurtPre(EntityHurtByGunEvent.Pre event) {
        LivingEntity attacker = event.getAttacker();
        if (attacker == null || attacker.level().isClientSide) {
            return;
        }

        Entity target = event.getHurtEntity();
        if (target == null || !target.isOnFire()) {
            return;
        }

        double bonus = TaczCuriosConfig.COMMON.heavenFireBurningDamageBonus.get();
        if (bonus <= 0) {
            return;
        }

        if (!isHeavenFireEquipped(attacker)) {
            return;
        }
        if (!GunTypeChecker.isHoldingConfiguredGunTypes(attacker, PISTOL_TYPES)) {
            return;
        }

        event.setBaseAmount(event.getBaseAmount() * (float) (1.0 + bonus));
    }

    private static boolean isHeavenFireEquipped(LivingEntity entity) {
        return HeavenFireJudgment.isEquipped(entity)
                || HeavenFireApocalypse.isEquipped(entity)
                || HeavenFireApocalypseEndless.isEquipped(entity);
    }
}
