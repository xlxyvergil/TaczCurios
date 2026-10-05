package com.xlxyvergil.tcc.event;

import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.tacz.guns.api.event.common.EntityKillByGunEvent;
import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.core.TccDamageSources;
import com.xlxyvergil.tcc.items.BaseCurioItem;
import com.xlxyvergil.tcc.items.curios.bound.JudgementKey;
import com.xlxyvergil.tcc.items.curios.bound.SevenThunders;
import com.xlxyvergil.tcc.items.curios.bound.SevenThundersThunderSeen;
import com.xlxyvergil.tcc.network.NetworkHandler;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import com.xlxyvergil.tcc.util.GunTypeChecker;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 渡鸦的神之键系列（涤罪七雷 / 涤罪七雷·雷鸣见 / 裁决之键）开镜蓄力增伤：
 * 从按下开镜开始自定义计时，持续开镜经过 timeToMax 秒达到满增伤，松镜立即清零重置。
 * 增幅同时作用于本枪构造伤害与饰品附加的虚数伤害。
 */
@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class RavenKeyAimHandler {

    /** 单件饰品的开镜蓄力参数 */
    public record AimParams(double timeToMax, double maxAmp) {
    }

    /** 玩家 UUID → 连续开镜 tick 数（仅服务端维护，松镜/未持枪/未佩戴时移除，命中结算后清除） */
    private static final Map<UUID, Integer> AIM_TICKS = new ConcurrentHashMap<>();

    private RavenKeyAimHandler() {
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Player player = event.player;
        if (player.level().isClientSide()) {
            return;
        }
        UUID id = player.getUUID();
        boolean aiming = player.isAlive()
                && resolveParams(player) != null
                && GunTypeChecker.isHoldingSniper(player)
                && IGunOperator.fromLivingEntity(player).getSynIsAiming();
        if (aiming) {
            AIM_TICKS.merge(id, 1, Integer::sum);
        } else {
            AIM_TICKS.remove(id);
        }
    }

    /** 离线时清理计时记录，避免残留蓄力状态。 */
    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        AIM_TICKS.remove(event.getEntity().getUUID());
    }

    /** 当前开镜增幅（0 ~ 最高增伤）；未开镜、未持枪或未佩戴神之键时为 0。 */
    public static double getAmp(@Nullable LivingEntity livingEntity) {
        if (!(livingEntity instanceof Player player)) {
            return 0.0;
        }
        AimParams params = resolveParams(player);
        Integer ticks = AIM_TICKS.get(player.getUUID());
        if (params == null || ticks == null) {
            return 0.0;
        }
        return params.maxAmp() * chargeRatio(params, ticks);
    }

    /** 是否佩戴渡鸦神之键（客户端 HUD 判断是否显示蓄力条，不依赖服务端状态）。 */
    public static boolean hasRavenKeyEquipped(@Nullable LivingEntity livingEntity) {
        return getAimParams(livingEntity) != null;
    }

    /** 连续开镜 tick 数 → 蓄力进度 0~1；未佩戴神之键时为 0。客户端与服务端共用同一换算。 */
    public static double getChargeRatio(@Nullable LivingEntity livingEntity, int aimTicks) {
        AimParams params = getAimParams(livingEntity);
        return params == null ? 0.0 : chargeRatio(params, aimTicks);
    }

    /** 按整秒阶梯递增：每满 1 秒提升一档，timeToMax 秒达到满增伤。 */
    private static double chargeRatio(AimParams params, int ticks) {
        double elapsedSeconds = ticks / 20;
        double ratio = params.timeToMax() <= 0.0 ? 1.0 : elapsedSeconds / params.timeToMax();
        return Math.min(1.0, Math.max(0.0, ratio));
    }

    /** 开镜增幅作用于本枪构造伤害：在虚数转换前调整基础伤害。 */
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onGunHurtPre(EntityHurtByGunEvent.Pre event) {
        if (event.getLogicalSide().isClient()) {
            return;
        }
        LivingEntity attacker = event.getAttacker();
        if (attacker == null || !GunTypeChecker.isHoldingSniper(attacker)) {
            return;
        }
        double amp = getAmp(attacker);
        if (amp <= 0.0) {
            return;
        }
        event.setBaseAmount((float) (event.getBaseAmount() * (1.0 + amp)));
    }

    /** 造成伤害后清除蓄力计数：本发已享受增幅，之后需重新蓄力。 */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onGunHurtPost(EntityHurtByGunEvent.Post event) {
        if (event.getLogicalSide().isClient()) {
            return;
        }
        clearCharge(event.getAttacker());
    }

    /** 致死命中走的是 EntityKillByGunEvent（与 Post 互斥），同样需要清除蓄力并同步客户端。 */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onGunKill(EntityKillByGunEvent event) {
        if (event.getLogicalSide().isClient()) {
            return;
        }
        clearCharge(event.getAttacker());
    }

    /** 清除攻击者的蓄力计数，并把清零结果同步给对应客户端，避免 HUD 显示与实际增幅不一致。 */
    private static void clearCharge(@Nullable LivingEntity attacker) {
        if (attacker == null) {
            return;
        }
        if (AIM_TICKS.remove(attacker.getUUID()) == null) {
            return;
        }
        if (attacker instanceof ServerPlayer serverPlayer) {
            NetworkHandler.resetRavenCharge(serverPlayer);
        }
    }

    /**
     * 以受击者为中心，对球型范围内的生物（含受击者本身）施加虚数伤害。
     * 用于渡鸦神之键附加伤害的范围溅射。
     */
    public static void applySplashImaginary(@Nullable LivingEntity attacker, @Nullable LivingEntity victim,
                                            float amount, double radius) {
        if (attacker == null || victim == null || amount <= 0.0F || radius <= 0.0) {
            return;
        }
        if (!(victim.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        double radiusSq = radius * radius;
        AABB box = victim.getBoundingBox().inflate(radius);
        List<Mob> targets = serverLevel.getEntitiesOfClass(Mob.class, box,
                mob -> mob != attacker && mob.isAlive() && mob.distanceToSqr(victim) <= radiusSq);
        for (Mob mob : targets) {
            TccAttributeEvents.applyImaginaryDamage(mob,
                    TccDamageSources.imaginaryDamage(serverLevel, attacker), amount);
        }
    }

    /** 服务端：仅在服务端世界解析参数。 */
    @Nullable
    private static AimParams resolveParams(@Nullable LivingEntity attacker) {
        if (attacker == null || !(attacker.level() instanceof ServerLevel)) {
            return null;
        }
        return getAimParams(attacker);
    }

    /** 取佩戴中最高阶（裁决之键 > 雷鸣见 > 七雷）的开镜蓄力参数；未佩戴时为 null。客户端亦可用。 */
    @Nullable
    public static AimParams getAimParams(@Nullable LivingEntity attacker) {
        if (attacker == null) {
            return null;
        }
        AimParams params = paramsFor(attacker, JudgementKey.class);
        if (params != null) {
            return params;
        }
        params = paramsFor(attacker, SevenThundersThunderSeen.class);
        if (params != null) {
            return params;
        }
        return paramsFor(attacker, SevenThunders.class);
    }

    @Nullable
    private static AimParams paramsFor(LivingEntity attacker, Class<? extends BaseCurioItem> type) {
        ItemStack stack = CurioSearchHelper.findFirstEquippedStack(attacker, s -> type.isInstance(s.getItem()));
        if (stack.isEmpty() || !((BaseCurioItem) stack.getItem()).matchesRestriction(attacker)) {
            return null;
        }
        if (type == JudgementKey.class) {
            return new AimParams(TaczCuriosConfig.COMMON.judgementKeyAimTimeToMax.get(),
                    TaczCuriosConfig.COMMON.judgementKeyAimMaxAmp.get());
        }
        if (type == SevenThundersThunderSeen.class) {
            return new AimParams(TaczCuriosConfig.COMMON.sevenThundersThunderSeenAimTimeToMax.get(),
                    TaczCuriosConfig.COMMON.sevenThundersThunderSeenAimMaxAmp.get());
        }
        return new AimParams(TaczCuriosConfig.COMMON.sevenThundersAimTimeToMax.get(),
                TaczCuriosConfig.COMMON.sevenThundersAimMaxAmp.get());
    }
}
