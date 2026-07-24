package com.xlxyvergil.tcc.compat.apollyon;

import com.mega.revelationfix.safe.entity.Apollyon2Interface;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.fml.ModList;

/**
 * 亚波伦（Apollyon）兼容工具类。
 * 用于绕过 RevelationFix 对 Apostle 的限伤机制，直接读写实际健康值。
 * <p>
 * 所有方法都内置了 {@code ModList.get().isLoaded()} 检查，
 * 确保 RevelationFix 未安装时不会引发类加载错误。
 */
public final class ApollyonCompat {

    private static final String REVELATION_FIX_MODID = "revelationfix";

    private ApollyonCompat() {}

    /**
     * 判断目标是否为安装了 RevelationFix 的 Apostle 实体。
     */
    public static boolean isRevelationFixApostle(LivingEntity target) {
        return isLoaded() && target instanceof Apollyon2Interface;
    }

    /**
     * 清除 RevelationFix 的受击冷却，使下一次伤害不受冷却拦截。
     */
    public static void clearHitCooldown(LivingEntity target) {
        if (isLoaded() && target instanceof Apollyon2Interface apollyon) {
            apollyon.revelaionfix$setHitCooldown(0);
        }
    }

    /**
     * 直接对 Apostle 造成伤害，绕过 RevelationFix 的全部限伤拦截。
     * <p>
     * 用 {@code target.getHealth()} 读取当前血量（Nether 下 RevelationFix 已重写此方法返回自定义健康值），
     * 再用 {@code revelaionfix$setApollyonHealth} 写入扣除后的血量
     *（{@code setTheData} 已被 RevelationFix 的 mixin plugin 替换为实际实现的字节码）。
     * 调用前需确保 {@link #isRevelationFixApostle(LivingEntity)} 返回 true。
     *
     * @return 扣除后剩余健康值
     */
    public static float applyDirectDamage(LivingEntity target, float damage) {
        // 重要：用 Minecraft 的 getHealth() 而非接口的 getApollyonHealth()
        // getApollyonHealth 底层 getTheData 未被 plugin 替换，永远返回 0
        float currentHealth = target.getHealth();
        float newHealth = Math.max(0, currentHealth - damage);
        ((Apollyon2Interface) target).revelaionfix$setApollyonHealth(newHealth);
        return newHealth;
    }

    private static boolean isLoaded() {
        return ModList.get().isLoaded(REVELATION_FIX_MODID);
    }
}
