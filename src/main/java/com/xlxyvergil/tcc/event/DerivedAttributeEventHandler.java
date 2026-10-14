package com.xlxyvergil.tcc.event;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.compat.maid.MaidCompat;
import com.xlxyvergil.tcc.items.curios.kongbai.KongbaiZhijian;
import com.xlxyvergil.tcc.util.CurioEffectRefresher;
import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

/**
 * 依赖其它属性的饰品重算入口：空白之键在攻击时对齐，进度类饰品在击杀结算时重算。
 */
@EventBusSubscriber(modid = TaczCurios.MODID)
public class DerivedAttributeEventHandler {

    /** 原版攻击时重算空白之键。 */
    @SubscribeEvent
    public static void onLivingHurtKongbaiRecalc(LivingIncomingDamageEvent event) {
        if (event.getEntity().level().isClientSide) return;
        // 空白之键仅玩家/女仆可佩戴，先廉价短路其它攻击者
        if (event.getSource().getEntity() instanceof LivingEntity attacker
                && MaidCompat.isPlayerOrMaid(attacker)) {
            KongbaiZhijian.recalculate(attacker);
        }
    }

    /** TACZ 枪击时重算空白之键。 */
    @SubscribeEvent
    public static void onGunHurtKongbaiRecalc(EntityHurtByGunEvent.Pre event) {
        if (event.getLogicalSide().isClient()) return;
        // 空白之键仅玩家/女仆可佩戴，先廉价短路其它攻击者
        LivingEntity attacker = event.getAttacker();
        if (attacker == null || !MaidCompat.isPlayerOrMaid(attacker)) return;
        KongbaiZhijian.recalculate(attacker);
    }

    /** 击杀结算时重算击败者身上依赖其它属性的饰品（进度奖励在此增长）。 */
    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        // 依赖其它属性的饰品仅玩家/女仆可佩戴，先廉价短路其它击杀者
        if (event.getSource().getEntity() instanceof LivingEntity killer
                && MaidCompat.isPlayerOrMaid(killer)) {
            CurioEffectRefresher.refreshDerivedAttributes(killer);
        }
    }
}
