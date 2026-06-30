package com.xlxyvergil.tcc.compat.apollyon;

import com.mega.revelationfix.safe.entity.Apollyon2Interface;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.fml.ModList;

/**
 * 亚波伦（Apollyon）兼容工具类。
 * 用于绕过 RevelationFix 对 Apostle 的限伤机制，直接读写实际健康值。
 */
public final class ApollyonCompat {

    private static final String REVELATION_FIX_MODID = "revelationfix";

    private ApollyonCompat() {}

    /**
     * 判断目标是否为安装了 RevelationFix 的 Apostle 实体。
     */
    public static boolean isRevelationFixApostle(LivingEntity target) {
        return ModList.get().isLoaded(REVELATION_FIX_MODID) && target instanceof Apollyon2Interface;
    }

    /**
     * 直接对 Apostle 造成伤害，绕过 RevelationFix 的全部限伤拦截。
     * 调用前需确保 {@link #isRevelationFixApostle(LivingEntity)} 返回 true。
     *
     * @return 扣除后剩余健康值
     */
    public static float applyDirectDamage(LivingEntity target, float damage) {
        Apollyon2Interface apollyon = (Apollyon2Interface) target;
        float currentHealth = apollyon.revelaionfix$getApollyonHealth();
        float newHealth = Math.max(0, currentHealth - damage);
        apollyon.revelaionfix$setApollyonHealth(newHealth);
        return newHealth;
    }
}
