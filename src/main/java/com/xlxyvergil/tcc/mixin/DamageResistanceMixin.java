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
 * <p>提供两个独立机制，由不同饰品按需调用：
 * <ul>
 *   <li>{@link #setDamageCooldown(LivingEntity, int)} — 受伤冷却，冷却期间所有伤害归零</li>
 *   <li>{@link #setDamageCap(LivingEntity, float)} — 单次受伤上限，限制每次受伤最大值</li>
 * </ul>
 *
 * <p>两种机制可同时生效。冷却优先于上限：冷却生效时，伤害直接归零，上限不参与计算。
 *
 * <h3>饰品使用示例</h3>
 * <pre>{@code
 * // 冷却型饰品：每 tick 刷新冷却值（例如 30 tick）
 * public void curioTick(SlotContext slotContext, ItemStack stack) {
 *     DamageResistanceMixin.setDamageCooldown(slotContext.entity(), 30);
 * }
 *
 * public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
 *     DamageResistanceMixin.clearDamageCooldown(slotContext.entity());
 * }
 *
 * // 限伤型饰品：每 tick 刷新上限值（例如 20.0）
 * public void curioTick(SlotContext slotContext, ItemStack stack) {
 *     DamageResistanceMixin.setDamageCap(slotContext.entity(), 20.0F);
 * }
 *
 * public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
 *     DamageResistanceMixin.clearDamageCap(slotContext.entity());
 * }
 * }</pre>
 */
@Mixin(value = LivingEntity.class, priority = 2000)
public abstract class DamageResistanceMixin {

    @Unique
    private static final Map<UUID, Integer> COOLDOWN_MAP = new ConcurrentHashMap<>();
    @Unique
    private static final Map<UUID, Float> DAMAGE_CAP_MAP = new ConcurrentHashMap<>();

    // ==================== 饰品调用的公开方法 ====================

    /**
     * 设置受伤冷却。冷却期间所有血量下降都会被拦截归零。
     * 由饰品在 {@code curioTick} 中每 tick 调用以维持冷却状态。
     *
     * @param entity        目标实体（玩家）
     * @param cooldownTicks 冷却 tick 数
     */
    public static void setDamageCooldown(LivingEntity entity, int cooldownTicks) {
        if (entity == null || cooldownTicks <= 0) return;
        COOLDOWN_MAP.put(entity.getUUID(), cooldownTicks);
    }

    /**
     * 设置单次受伤上限。玩家每次受到的血量下降不会超过此值。
     * 由饰品在 {@code curioTick} 中每 tick 调用以维持上限状态。
     *
     * @param entity    目标实体（玩家）
     * @param maxDamage 单次受伤上限
     */
    public static void setDamageCap(LivingEntity entity, float maxDamage) {
        if (entity == null || maxDamage <= 0) return;
        DAMAGE_CAP_MAP.put(entity.getUUID(), maxDamage);
    }

    /**
     * 移除受伤冷却状态。饰品卸下时调用。
     */
    public static void clearDamageCooldown(LivingEntity entity) {
        if (entity != null) {
            COOLDOWN_MAP.remove(entity.getUUID());
        }
    }

    /**
     * 移除受伤上限状态。饰品卸下时调用。
     */
    public static void clearDamageCap(LivingEntity entity) {
        if (entity != null) {
            DAMAGE_CAP_MAP.remove(entity.getUUID());
        }
    }

    /**
     * 移除所有减伤状态。
     */
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
