package com.xlxyvergil.tcc.event;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.items.curios.kongbai.KongbaiZhijian;
import com.xlxyvergil.tcc.util.CurioEffectRefresher;
import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 依赖其它属性的饰品重算入口：空白之键在攻击时对齐，进度类饰品在击杀结算时重算。
 */
@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DerivedAttributeEventHandler {

    /** 原版攻击时重算空白之键。 */
    @SubscribeEvent
    public static void onLivingHurtKongbaiRecalc(LivingHurtEvent event) {
        if (event.getEntity().level().isClientSide) return;
        if (event.getSource().getEntity() instanceof LivingEntity attacker) {
            KongbaiZhijian.recalculate(attacker);
        }
    }

    /** TACZ 枪击时重算空白之键。 */
    @SubscribeEvent
    public static void onGunHurtKongbaiRecalc(EntityHurtByGunEvent.Pre event) {
        if (event.getLogicalSide().isClient()) return;
        KongbaiZhijian.recalculate(event.getAttacker());
    }

    /** 击杀结算时重算击败者身上依赖其它属性的饰品（进度奖励在此增长）。 */
    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        Entity source = event.getSource().getEntity();
        if (source instanceof LivingEntity killer) {
            CurioEffectRefresher.refreshDerivedAttributes(killer);
        }
    }
}
