package com.xlxyvergil.tcc.event;

import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.tacz.guns.api.event.common.EntityKillByGunEvent;
import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.compat.maid.MaidCompat;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.core.TccDamageSources;
import com.xlxyvergil.tcc.items.BaseCurioItem;
import com.xlxyvergil.tcc.items.curios.bound.Shesha;
import com.xlxyvergil.tcc.items.curios.bound.SiZhiYi;
import com.xlxyvergil.tcc.items.curios.bound.WangshiDeSheying;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import com.xlxyvergil.tcc.util.GunTypeChecker;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.entity.PartEntity;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 神之键·梅比乌斯线（往世的蛇影 / 死之衣 / 舍沙）枪击命中溅射：
 * 命中后以受击者为中心，对球型范围内的其它生物（不含受击者本身）额外施加
 * 「主手枪械面板伤害 × 百分比」的魔法伤害。
 */
@EventBusSubscriber(modid = TaczCurios.MODID)
public final class SerpentSplashHandler {

    /** 溅射重入防护：避免溅射伤害结算过程中再次进入本处理器 */
    private static final ThreadLocal<Boolean> IN_SPLASH = ThreadLocal.withInitial(() -> Boolean.FALSE);

    private SerpentSplashHandler() {
    }

    /** 单件饰品的溅射参数 */
    private record SplashParams(double radius, double percent) {
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onGunHurtPost(EntityHurtByGunEvent.Post event) {
        applySplash(event.getAttacker(), event.getHurtEntity());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onGunKill(EntityKillByGunEvent event) {
        applySplash(event.getAttacker(), event.getKilledEntity());
    }

    private static void applySplash(@Nullable LivingEntity attacker, @Nullable Entity hurtEntity) {
        if (attacker == null || IN_SPLASH.get()) {
            return;
        }
        LivingEntity victim = resolveLivingEntity(hurtEntity);
        if (victim == null) {
            return;
        }
        SplashParams params = resolveParams(attacker);
        if (params == null) {
            return;
        }
        if (!(victim.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        // 伤害基准为主手枪械面板伤害（与裁决之键一致），不再依赖命中前后血量差
        double panelDamage = GunTypeChecker.getMainHandGunDamage(attacker, GunTypeChecker.SNIPER_GUN_TYPES);
        float splashDamage = (float) (panelDamage * params.percent());
        if (splashDamage <= 0.0F) {
            return;
        }

        double radius = params.radius();
        double radiusSq = radius * radius;
        AABB box = victim.getBoundingBox().inflate(radius);
        // 排除攻击者与受击者本身：溅射为魔法伤害，若抢到致死一击会让 TACZ 不再发出
        // EntityKillByGunEvent，导致 gun_kill 类成就（击杀末影龙等）永远无法达成。
        List<Mob> targets = serverLevel.getEntitiesOfClass(Mob.class, box,
                mob -> mob != attacker && mob != victim && mob.isAlive()
                        && mob.distanceToSqr(victim) <= radiusSq);

        DamageSource source = TccDamageSources.magicDamage(serverLevel, attacker);
        IN_SPLASH.set(Boolean.TRUE);
        try {
            for (Mob mob : targets) {
                TccAttributeEvents.applyMagicDamage(mob, source, splashDamage);
            }
        } finally {
            IN_SPLASH.set(Boolean.FALSE);
        }
    }

    /** 受击者可能为多部位实体（如末影龙部位），解析到其本体后再计算。 */
    @Nullable
    private static LivingEntity resolveLivingEntity(@Nullable Entity hurtEntity) {
        if (hurtEntity instanceof LivingEntity living) {
            return living;
        }
        if (hurtEntity instanceof PartEntity<?> part && part.getParent() instanceof LivingEntity living) {
            return living;
        }
        return null;
    }

    @Nullable
    private static SplashParams resolveParams(@Nullable LivingEntity attacker) {
        // 溅射仅玩家/女仆佩戴者可触发，先廉价短路其它实体，再做后续判断
        if (attacker == null || !MaidCompat.isPlayerOrMaid(attacker)
                || !(attacker.level() instanceof ServerLevel)) {
            return null;
        }
        SplashParams params = equippedParams(attacker, Shesha.class);
        if (params != null) {
            return params;
        }
        params = equippedParams(attacker, SiZhiYi.class);
        if (params != null) {
            return params;
        }
        return equippedParams(attacker, WangshiDeSheying.class);
    }

    @Nullable
    private static SplashParams equippedParams(LivingEntity attacker, Class<? extends BaseCurioItem> type) {
        ItemStack stack = CurioSearchHelper.findFirstEquippedStack(attacker, s -> type.isInstance(s.getItem()));
        if (stack.isEmpty() || !((BaseCurioItem) stack.getItem()).matchesRestriction(attacker)) {
            return null;
        }
        if (type == Shesha.class) {
            return new SplashParams(TaczCuriosConfig.COMMON.sheshaSplashRadius.get(),
                    TaczCuriosConfig.COMMON.sheshaSplashPercent.get());
        }
        if (type == SiZhiYi.class) {
            return new SplashParams(TaczCuriosConfig.COMMON.siZhiYiSplashRadius.get(),
                    TaczCuriosConfig.COMMON.siZhiYiSplashPercent.get());
        }
        return new SplashParams(TaczCuriosConfig.COMMON.wangshiDeSheyingSplashRadius.get(),
                TaczCuriosConfig.COMMON.wangshiDeSheyingSplashPercent.get());
    }
}
