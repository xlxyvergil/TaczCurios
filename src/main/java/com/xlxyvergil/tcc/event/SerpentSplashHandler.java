package com.xlxyvergil.tcc.event;

import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.tacz.guns.api.event.common.EntityKillByGunEvent;
import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.core.TccDamageSources;
import com.xlxyvergil.tcc.items.BaseCurioItem;
import com.xlxyvergil.tcc.items.curios.bound.Shesha;
import com.xlxyvergil.tcc.items.curios.bound.SiZhiYi;
import com.xlxyvergil.tcc.items.curios.bound.WangshiDeSheying;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 神之键·梅比乌斯线（往世的蛇影 / 死之衣 / 舍沙）枪击命中溅射：
 * 命中后以受击者为中心，对球型范围内的生物额外施加「本次实际伤害 × 百分比」的虚数伤害。
 * 实际伤害 = Pre 记录的总血量（含吸收） - 结算后的总血量，即过护甲/抗性后的最终值。
 */
@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class SerpentSplashHandler {

    /** 记录受击者命中前总血量的 NBT 前缀；按子弹 id 区分，避免穿透/连击串扰 */
    private static final String PRE_TOTAL_HEALTH_KEY_PREFIX = "tcc_serpent_splash_total_";

    /** 溅射重入防护：枪击事件本身不会因溅射再次触发，这里作为异常再入的双保险 */
    private static final ThreadLocal<Boolean> IN_SPLASH = ThreadLocal.withInitial(() -> Boolean.FALSE);

    private SerpentSplashHandler() {
    }

    /** 单件饰品的溅射参数 */
    private record SplashParams(double radius, double percent) {
    }

    @SubscribeEvent
    public static void onGunHurtPre(EntityHurtByGunEvent.Pre event) {
        if (event.getLogicalSide().isClient()) {
            return;
        }
        if (resolveParams(event.getAttacker()) == null) {
            return;
        }
        Entity bullet = event.getBullet();
        if (bullet == null || !(event.getHurtEntity() instanceof LivingEntity target)) {
            return;
        }
        target.getPersistentData().putFloat(healthKey(bullet),
                target.getHealth() + target.getAbsorptionAmount());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onGunHurtPost(EntityHurtByGunEvent.Post event) {
        applySplash(event.getAttacker(), event.getHurtEntity(), event.getBullet());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onGunKill(EntityKillByGunEvent event) {
        applySplash(event.getAttacker(), event.getKilledEntity(), event.getBullet());
    }

    private static void applySplash(@Nullable LivingEntity attacker, @Nullable Entity hurtEntity, @Nullable Entity bullet) {
        if (attacker == null || bullet == null || !(hurtEntity instanceof LivingEntity victim)) {
            return;
        }
        SplashParams params = resolveParams(attacker);
        if (params == null) {
            return;
        }
        if (!(victim.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        var data = victim.getPersistentData();
        String key = healthKey(bullet);
        if (!data.contains(key)) {
            return;
        }
        float preTotal = data.getFloat(key);
        data.remove(key);

        float actualDamage = Math.max(0.0F, preTotal - (victim.getHealth() + victim.getAbsorptionAmount()));
        float splashDamage = (float) (actualDamage * params.percent());
        if (splashDamage <= 0.0F || IN_SPLASH.get()) {
            return;
        }

        double radius = params.radius();
        double radiusSq = radius * radius;
        AABB box = victim.getBoundingBox().inflate(radius);
        List<Mob> targets = serverLevel.getEntitiesOfClass(Mob.class, box,
                mob -> mob != victim && mob != attacker && mob.isAlive()
                        && mob.distanceToSqr(victim) <= radiusSq);

        IN_SPLASH.set(Boolean.TRUE);
        try {
            for (Mob mob : targets) {
                TccAttributeEvents.applyImaginaryDamage(mob,
                        TccDamageSources.imaginaryDamage(serverLevel, attacker), splashDamage);
            }
        } finally {
            IN_SPLASH.set(Boolean.FALSE);
        }
    }

    @Nullable
    private static SplashParams resolveParams(@Nullable LivingEntity attacker) {
        if (attacker == null || !(attacker.level() instanceof ServerLevel)) {
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

    private static String healthKey(Entity bullet) {
        return PRE_TOTAL_HEALTH_KEY_PREFIX + bullet.getId();
    }
}
