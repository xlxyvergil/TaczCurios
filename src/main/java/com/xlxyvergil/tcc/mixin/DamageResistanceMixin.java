package com.xlxyvergil.tcc.mixin;

import com.xlxyvergil.tcc.util.DamageResistanceHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 玩家减伤 Mixin。
 * <p>
 * 常驻比例减伤采用「每 tick 血量对账」的通用实现（见
 * {@link DamageResistanceHelper#reconcileHealth(LivingEntity)}），该方法每服务端 tick 调用，
 * 对任意来源（含绕过 setHealth 的直接写入）的血量下降统一按保留因子削减。
 * <p>
 * 这里的 {@code setHealth} 拦截仅保留「受伤冷却」与「单次上限」两类受击触发逻辑，
 * 与常驻比例减伤互不干扰（对账逻辑会读取 setHealth 之后的血量变化）。
 */
@Mixin(value = LivingEntity.class, priority = 2000)
public abstract class DamageResistanceMixin {

    /** 直接访问合并目标 {@link LivingEntity#dead} 字段，供「完全免伤」时强制复活清除死亡标记。 */
    @Accessor("dead")
    abstract void tcc$setDead(boolean dead);

    // ==================== tick：冷却递减 + 常驻比例减伤对账 + 完全免伤兜底复活 ====================

    @Inject(method = "tick", at = @At("TAIL"))
    private void tcc$tickCooldown(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self.level().isClientSide) return;
        UUID id = self.getUUID();

        Integer cooldown = DamageResistanceHelper.COOLDOWN_MAP.get(id);
        if (cooldown != null) {
            int newVal = cooldown - 1;
            if (newVal <= 0) {
                DamageResistanceHelper.COOLDOWN_MAP.remove(id);
            } else {
                DamageResistanceHelper.COOLDOWN_MAP.put(id, newVal);
            }
        }

        // 常驻比例减伤：每 tick 对账一次，统一削减任意来源的血量下降
        DamageResistanceHelper.reconcileHealth(self);

        // 佩戴真我且有常驻减伤保护（retain map 存在）的实体：对「绕过死亡事件取消」的强制死亡仍复活。
        // 普通可取消死亡已在 LivingDeathEvent 中被 ZhenWo.onLivingDeath 取消并回满血（dead 不会置真），
        // 因此这里的 isDeadOrDying() 只会命中「忽略事件取消、直接写血/置死」的绕过实现
        // （如亚波伦 RevelationFix 的 special die：catchSetTrueHealth(0) + setDead(true)）。
        // 这也顺带兜住了结界期间（retain <= 0，100% 免伤）经 catchSetTrueHealth 直接写血致死的场景，
        // 使真我佩戴者在面对这类必杀时真正不死。
        Float retain = DamageResistanceHelper.DAMAGE_RETAIN_MAP.get(id);
        if (retain != null && self.isDeadOrDying()) {
            reviveFully(self);
        }
    }

    /**
     * 完全免伤实体强制复活：清除死亡标记、复位死亡计时、恢复站立姿态并不再受击。
     * 用于兜住绕过 {@code setHealth} 直接写血量的第三方伤害（每次都会把血打到 0 并置死）。
     */
    private void reviveFully(LivingEntity self) {
        self.setHealth(self.getMaxHealth());
        self.hurtTime = 0;
        self.deathTime = 0;
        self.invulnerableTime = 100;
        this.tcc$setDead(false);
        self.setPose(Pose.STANDING);
        self.setDeltaMovement(self.getDeltaMovement().multiply(0, 0, 0));
    }

    // ==================== setHealth 拦截（仅冷却 / 单次上限） ====================

    @ModifyVariable(method = "setHealth", at = @At("HEAD"), argsOnly = true)
    private float tcc$modifySetHealth(float health) {
        LivingEntity self = (LivingEntity) (Object) this;

        float current = self.getHealth();
        float delta = health - current;

        // 仅拦截受伤
        if (delta >= 0.0F) return health;

        UUID id = self.getUUID();

        // --- 完全免伤（retain <= 0，如真我结界期间 100%）：setHealth 层任何扣血都保留当前血量 ---
        // 此时血不允许被 setHealth 打低；绕过 setHealth 直接写血量的第三方伤害（如亚波伦
        // catchSetTrueHealth）由 tick 末的强制复活（reviveFully）兜底。
        Float retain = DamageResistanceHelper.DAMAGE_RETAIN_MAP.get(id);
        if (retain != null && retain <= 0.0F) {
            logDamageLimit(self, -delta, 0.0F, true);
            return current;
        }

        // --- 冷却：伤害归零 ---
        Integer cooldown = DamageResistanceHelper.COOLDOWN_MAP.get(id);
        if (cooldown != null && cooldown > 0) {
            logDamageLimit(self, -delta, 0.0F, true);
            return current; // 血量不变
        }

        float reducedDelta = delta;

        // --- 单次上限：裁剪 reducedDelta ---
        Float cap = DamageResistanceHelper.DAMAGE_CAP_MAP.get(id);
        if (cap != null && cap > 0 && -reducedDelta > cap) {
            reducedDelta = -cap;
        }

        // 实际扣血较原始扣血有削减，说明限伤生效
        if (Math.abs(reducedDelta - delta) > 0.0001F) {
            logDamageLimit(self, -delta, -(reducedDelta), false);
            return current + reducedDelta;
        }

        return health;
    }

    /** 限伤诊断最近一次输出 tick（节流，防高频刷屏） */
    private static final Map<UUID, Integer> LAST_LOG_TICK = new ConcurrentHashMap<>();

    /**
     * 限伤诊断：在聊天框向佩戴者（玩家）输出本次限伤情况。
     * 节流——距上次输出不足 5 tick 则跳过，避免高频伤害刷屏。仅服务端向客户端发送。
     */
    private static void logDamageLimit(LivingEntity self, float rawDamage, float appliedDamage, boolean cooldown) {
        if (!(self instanceof Player player) || player.level().isClientSide) return;

        int now = self.tickCount;
        Integer last = LAST_LOG_TICK.get(self.getUUID());
        if (last != null && now - last < 5) return;
        LAST_LOG_TICK.put(self.getUUID(), now);

        String msg;
        if (cooldown) {
            msg = String.format("§e[TCC限伤] 冷却中，原伤害 %.1f 已归零", rawDamage);
        } else {
            msg = String.format("§e[TCC限伤] 原伤害 %.1f → 实际扣血 %.1f", rawDamage, appliedDamage);
        }
        player.displayClientMessage(Component.literal(msg), false);
    }
}
