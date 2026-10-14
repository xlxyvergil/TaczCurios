package com.xlxyvergil.tcc.event;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.api.items.IDamageMitigation;
import com.xlxyvergil.tcc.compat.maid.MaidCompat;
import com.xlxyvergil.tcc.items.curios.bound.ZhenWo;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import com.xlxyvergil.tcc.util.HurtCooldownTracker;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 减伤与受伤冷却的统一事件层结算。
 * <p>
 * 减伤不预先登记到全局表，而在受击当刻扫描佩戴者的饰品槽、逐条求值：
 * 判定对象是受伤实体本身，对任意 LivingEntity（玩家、女仆等）通用；状态与「是否佩戴 + 当前条件」
 * 天然一致，卸下即失效，无需登记/清理，也不会跨存档残留。
 * <ul>
 *   <li>真我结界激活期：在 LivingAttackEvent 取消整次攻击（含击退等副作用）；</li>
 *   <li>常驻比例减伤 / 受伤冷却：在 LivingDamageEvent（护甲、吸收结算之后、实际扣血之前）结算。</li>
 * </ul>
 */
@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DamageResistanceHandler {

    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide) return;
        if (ZhenWo.isBarrierActiveWearer(entity)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide || entity.isDeadOrDying()) return;
        // 与旧登记路径一致：减伤仅对玩家与女仆生效（真我另有仅玩家的单独限制）
        if (!MaidCompat.isPlayerOrMaid(entity)) return;

        // 受伤冷却中：伤害归零
        if (HurtCooldownTracker.isInCooldown(entity)) {
            event.setAmount(0.0F);
            return;
        }

        float amount = event.getAmount();
        DamageSource source = event.getSource();
        Float combinedRetain = null;
        int cooldownTicks = 0;

        // 一次受击只扫一遍饰品槽，逐条规则现算，不再依赖任何预登记状态
        for (ItemStack stack : CurioSearchHelper.equippedStacks(entity)) {
            if (!(stack.getItem() instanceof IDamageMitigation rule)) continue;

            Float retain = rule.retainFactor(entity, source, amount);
            if (retain != null) {
                combinedRetain = combinedRetain == null ? retain : combinedRetain * retain;
            }
            if (cooldownTicks <= 0) {
                Integer ticks = rule.hurtCooldownTicks(entity);
                if (ticks != null && ticks > 0) {
                    cooldownTicks = ticks;
                }
            }
        }

        // 完全免伤：不触发受击冷却
        if (combinedRetain != null && combinedRetain <= 0.0F) {
            event.setAmount(0.0F);
            return;
        }

        // 未在冷却中：本次扣血照常生效并立即进入冷却，
        // 从而得到「先扣一次血、随后进入冷却、冷却结束再次受击才扣血」的语义。
        if (cooldownTicks > 0) {
            HurtCooldownTracker.start(entity, cooldownTicks);
        }

        // 常驻比例减伤：按保留因子削减本次实际扣血
        if (combinedRetain != null && combinedRetain < 1.0F) {
            event.setAmount(amount * combinedRetain);
        }
    }

    /** 服务器停止时清空受伤冷却表：其时间戳按世界 tick 计数，换存档后原点不同，需清空避免误判。 */
    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event) {
        HurtCooldownTracker.clearAll();
    }
}
