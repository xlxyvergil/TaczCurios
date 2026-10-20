package com.xlxyvergil.tcc.event;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.registries.TccMobEffects;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 气息遮蔽的服务端逻辑：施加时脱战，攻击时把剩余时长压到 5 秒。
 */
@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class QiXiZheBiHandler {

    private static final double AGGRO_CLEAR_RADIUS = 64.0D;

    // 命中后剩余的宽限时长：留出击杀事件结算的窗口（TACZ 击杀事件在伤害同 tick 触发，
    // 但多段命中时致死那一下可能落在破隐之后）
    private static final int BREAK_GRACE_TICKS = 100;

    private QiXiZheBiHandler() {
    }

    private static MobEffect own() {
        return TccMobEffects.QI_XI_ZHE_BI.get();
    }

    @SubscribeEvent
    public static void onEffectAdded(MobEffectEvent.Added event) {
        if (!TccMobEffects.QI_XI_ZHE_BI.isPresent()) {
            return;
        }
        if (event.getEffectInstance().getEffect() != own()) {
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

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof LivingEntity attacker)) {
            return;
        }
        if (!TccMobEffects.QI_XI_ZHE_BI.isPresent()) {
            return;
        }
        // 压时长而不是 removeEffect：这个效果受 TccEffectGuard 保护，直接移除会被拦下
        MobEffectInstance instance = attacker.getEffect(own());
        if (instance != null && instance.duration > BREAK_GRACE_TICKS) {
            instance.duration = BREAK_GRACE_TICKS;
        }
    }
}
