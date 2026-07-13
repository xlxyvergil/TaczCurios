package com.xlxyvergil.tcc.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 玩家减伤 Mixin，在 {@link LivingEntity#setHealth(float)} 层面使用
 * {@link ModifyVariable} 修改传入的 health 参数，不会被任何事件层绕过。
 *
 * <p>提供三个独立机制，由不同饰品按需调用：
 * <ul>
 *   <li>{@link #setDamageCooldown(LivingEntity, int)} — 受伤冷却，受击后 N tick 内所有伤害归零</li>
 *   <li>{@link #setDamageCap(LivingEntity, float)} — 单次受伤上限，限制每次受伤最大值</li>
 *   <li>{@link #triggerHitCooldownIfEquipped(LivingEntity, Item, int)} — 受击触发冷却（装备检查+冷却一站式）</li>
 * </ul>
 *
 * <p>所有机制均采用<strong>受击触发式</strong>（参考 GoetyRevelation / RevelationFix 的 Apostle 减伤模式），
 * 饰品类无需在 {@code curioTick} 中每 tick 调用，只需在 {@code LivingHurtEvent} 中调用一次即可。
 *
 * <h3>饰品使用示例</h3>
 * <pre>{@code
 * // 模式一：独立 LivingHurtEvent 监听器（推荐）
 * @SubscribeEvent
 * public static void onLivingHurt(LivingHurtEvent event) {
 *     LivingEntity entity = event.getEntity();
 *     // 冷却型：受击后 30 tick 内伤害归零
 *     DamageResistanceMixin.setDamageCooldown(entity, 30);
 *     // 限伤型：单次受伤不超过 20.0
 *     DamageResistanceMixin.setDamageCap(entity, 20.0F);
 * }
 *
 * // 模式二：在饰品类 curioTick 中调用（也支持，但不如受击式高效）
 * // 最终建议统一在 LivingHurtEvent 中处理
 * }</pre>
 *
 * <p>冷却由 {@code tick()} Mixin 自动递减，降至 0 自动移除，无需手动清理。
 */
@Mixin(value = LivingEntity.class, priority = 2000)
public abstract class DamageResistanceMixin {

    @Unique
    private static final Map<UUID, Integer> COOLDOWN_MAP = new ConcurrentHashMap<>();
    @Unique
    private static final Map<UUID, Float> DAMAGE_CAP_MAP = new ConcurrentHashMap<>();

    // ==================== 饰品调用的公开方法 ====================

    /**
     * 设置受伤冷却（受击触发式）。
     * <p>调用后，该实体在 {@code cooldownTicks} 内受到的所有血量下降都会被拦截归零。
     * 冷却由 tick() Mixin 自动递减，归零后自动移除，无需手动清理。
     * <p>典型用法：在 {@code LivingHurtEvent} 中调用一次，受击后给一段无敌窗口。
     *
     * @param entity        目标实体（玩家）
     * @param cooldownTicks 冷却 tick 数（如 30 = 1.5秒）
     */
    @Unique
    public static void setDamageCooldown(LivingEntity entity, int cooldownTicks) {
        if (entity == null || cooldownTicks <= 0) return;
        COOLDOWN_MAP.put(entity.getUUID(), cooldownTicks);
    }

    /**
     * 设置单次受伤上限（受击触发式）。
     * <p>调用后，该实体每次血量下降不会超过 {@code maxDamage}。
     * 需在 {@code LivingHurtEvent} 或 {@code curioTick} 中持续调用以维持上限，
     * 或在卸下饰品时调用 {@link #clearDamageCap} 清除。
     *
     * @param entity    目标实体（玩家）
     * @param maxDamage 单次受伤上限（如 20.0 = 10颗心）
     */
    @Unique
    public static void setDamageCap(LivingEntity entity, float maxDamage) {
        if (entity == null || maxDamage <= 0) return;
        DAMAGE_CAP_MAP.put(entity.getUUID(), maxDamage);
    }

    /**
     * 移除受伤冷却状态。饰品卸下时调用。
     */
    @Unique
    public static void clearDamageCooldown(LivingEntity entity) {
        if (entity != null) {
            COOLDOWN_MAP.remove(entity.getUUID());
        }
    }

    /**
     * 移除受伤上限状态。饰品卸下时调用。
     */
    @Unique
    public static void clearDamageCap(LivingEntity entity) {
        if (entity != null) {
            DAMAGE_CAP_MAP.remove(entity.getUUID());
        }
    }

    /**
     * 移除所有减伤状态。
     */
    @Unique
    public static void clearAll(LivingEntity entity) {
        if (entity != null) {
            UUID id = entity.getUUID();
            COOLDOWN_MAP.remove(id);
            DAMAGE_CAP_MAP.remove(id);
        }
    }

    // ==================== tick 中的冷却递减 ====================

    @Inject(method = "tick", at = @At("TAIL"))
    private void tcc$tickCooldown(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self.level().isClientSide) return;
        UUID id = self.getUUID();
        Integer cooldown = COOLDOWN_MAP.get(id);
        if (cooldown != null) {
            int newVal = cooldown - 1;
            if (newVal <= 0) {
                COOLDOWN_MAP.remove(id);
            } else {
                COOLDOWN_MAP.put(id, newVal);
            }
        }
    }

    // ==================== setHealth 拦截 ====================

    /**
     * 在 setHealth 执行前修改 health 参数。
     * 使用 {@link ModifyVariable} 直接修改入参，不存在递归调用问题。
     * 优先级低于 Apollyon 自身的 Mixin，不会被其影响。
     */
    @ModifyVariable(method = "setHealth", at = @At("HEAD"), argsOnly = true)
    private float tcc$modifySetHealth(float health) {
        LivingEntity self = (LivingEntity) (Object) this;

        // 仅对玩家生效
        if (!(self instanceof Player)) return health;

        float current = self.getHealth();
        float delta = health - current;

        // 仅拦截受伤
        if (delta >= 0.0F) return health;

        UUID id = self.getUUID();

        // --- 冷却：伤害归零 ---
        Integer cooldown = COOLDOWN_MAP.get(id);
        if (cooldown != null && cooldown > 0) {
            return current; // 血量不变
        }

        // --- 限伤：裁剪 delta ---
        Float cap = DAMAGE_CAP_MAP.get(id);
        if (cap != null && cap > 0 && -delta > cap) {
            return current - cap;
        }

        return health;
    }
}
