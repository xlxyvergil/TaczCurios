package com.xlxyvergil.tcc.event;

import com.xlxyvergil.tcc.registries.TccMobEffects;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

/**
 * 气息遮蔽的服务端逻辑：施加时脱战，攻击时把剩余时长压到 5 秒。
 */
@EventBusSubscriber(modid = "tcc")
public final class QiXiZheBiHandler {

    private static final double AGGRO_CLEAR_RADIUS = 64.0D;

    // 命中后剩余的宽限时长：留出击杀事件结算的窗口（TACZ 击杀事件在伤害同 tick 触发，
    // 但多段命中时致死那一下可能落在破隐之后）
    private static final int BREAK_GRACE_TICKS = 100;

    private QiXiZheBiHandler() {
    }

    @SubscribeEvent
    public static void onEffectAdded(MobEffectEvent.Added event) {
        if (!event.getEffectInstance().is(TccMobEffects.QI_XI_ZHE_BI)) {
            return;
        }
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide) {
            return;
        }
        // 不等下一次 updateInvisibilityStatus，先立刻隐身
        entity.setInvisible(true);
        clearAggro(entity);
    }

    // 只清一次，之后靠原版可见度缩放拦住重新锁定
    private static void clearAggro(LivingEntity entity) {
        if (!(entity.level() instanceof ServerLevel level)) {
            return;
        }
        level.getEntitiesOfClass(Mob.class, entity.getBoundingBox().inflate(AGGRO_CLEAR_RADIUS))
                .forEach(mob -> {
                    if (mob.getTarget() != entity && mob.getLastHurtByMob() != entity) {
                        return;
                    }
                    mob.setTarget(null);
                    mob.setLastHurtMob(null);
                    mob.setLastHurtByMob(null);
                    mob.targetSelector.getAvailableGoals().forEach(WrappedGoal::stop);
                    mob.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
                });
    }

    /**
     * 1.21.1 迁移说明：MobEffectInstance#duration 自 1.20.5 起改为私有且无公开 setter，
     * 因此改为「构造一个同参数、时长为 BREAK_GRACE_TICKS 的新实例 + forceAddEffect 覆盖」。
     * 仍不使用 removeEffect：这个效果受 TccEffectGuard 保护，直接移除会被拦下。
     */
    @SubscribeEvent
    public static void onLivingHurt(LivingIncomingDamageEvent event) {
        if (!(event.getSource().getEntity() instanceof LivingEntity attacker)) {
            return;
        }
        MobEffectInstance instance = attacker.getEffect(TccMobEffects.QI_XI_ZHE_BI);
        if (instance == null || instance.getDuration() <= BREAK_GRACE_TICKS) {
            return;
        }
        MobEffectInstance shortened = new MobEffectInstance(
                TccMobEffects.QI_XI_ZHE_BI, BREAK_GRACE_TICKS, instance.getAmplifier(),
                instance.isAmbient(), instance.isVisible(), instance.showIcon());
        attacker.forceAddEffect(shortened, null);
    }
}
