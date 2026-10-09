package com.xlxyvergil.tcc.mixin;

import com.xlxyvergil.tcc.util.DamageResistanceHelper;
import com.xlxyvergil.tcc.util.ZhenWoGuard;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

/**
 * 减伤与结界免伤 Mixin：
 * - 常驻比例减伤采用每 tick 血量对账（DamageResistanceHelper.reconcileHealth）对任意来源（含绕过
 *   setHealth 的直接写入）统一按保留因子削减；
 * - 真我结界激活期间在 hurt 入口取消一切伤害；
 * - setHealth 拦截处理四类受击触发逻辑：结界激活期扣血归零、完全免伤（保留因子 &lt;= 0）、受伤冷却与单次上限；
 *   其中"受击自动冷却"（HURT_COOLDOWN_MAP）在未冷却时放行首次扣血并顺带开启冷却，实现"先扣一次血、随后无敌一小段"。
 */
@Mixin(value = LivingEntity.class, priority = 2000)
public abstract class DamageResistanceMixin {

    // ---- tick：冷却递减 + 常驻比例减伤对账 ----

    @Inject(method = "tick", at = @At("TAIL"))
    private void tcc$tickCooldown(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self.level().isClientSide) return;

        // 仅对已登记实体（佩戴了相关饰品）生效：本钩子对所有生物每 tick 触发，未登记实体直接跳过，
        // 避免为绝大多数生物计算 UUID 并查询多个哈希表。
        if (!DamageResistanceHelper.isTracked(self)) return;

        UUID id = self.getUUID();

        Integer cooldown = DamageResistanceHelper.COOLDOWN_MAP.get(id);
        if (cooldown != null) {
            int newVal = cooldown - 1;
            if (newVal <= 0) {
                DamageResistanceHelper.COOLDOWN_MAP.remove(id);
                // 冷却结束，若该实体已无任何登记状态则移出登记集合
                DamageResistanceHelper.refreshTracked(self);
            } else {
                DamageResistanceHelper.COOLDOWN_MAP.put(id, newVal);
            }
        }

        // 常驻比例减伤：每 tick 对账一次，统一削减任意来源的血量下降
        DamageResistanceHelper.reconcileHealth(self);
    }

    // ---- 真我结界伤害免疫 ----

    /**
     * 真我结界激活期间免疫一切伤害：在 hurt 入口直接取消，近战/弹射物/爆炸/第三方伤害均不生效。
     */
    @Inject(method = "hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z", at = @At("HEAD"), cancellable = true)
    private void tcc$barrierImmune(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        // 登记判断优先：服务端未佩戴相关饰品的实体直接放行（结界佩戴者在服务端必然已登记）。
        // 客户端不维护登记状态，跳过该早退并按同步的结界 buff 兜底判定。
        if (!self.level().isClientSide && !DamageResistanceHelper.isTracked(self)) {
            return;
        }
        if (ZhenWoGuard.isBarrierActiveWearer(self)) {
            cir.setReturnValue(false);
        }
    }

    // ---- setHealth 拦截（结界归零 / 完全免伤 / 冷却 / 单次上限） ----

    @ModifyVariable(method = "setHealth", at = @At("HEAD"), argsOnly = true)
    private float tcc$modifySetHealth(float health) {
        LivingEntity self = (LivingEntity) (Object) this;

        float current = self.getHealth();
        float delta = health - current;

        // 仅拦截受伤
        if (delta >= 0.0F) return health;

        // 登记判断优先：服务端未佩戴相关饰品的实体直接放行（结界佩戴者在服务端必然已登记）。
        // 客户端不维护登记状态，跳过该早退以保留下面的结界兜底判定。
        if (!self.level().isClientSide && !DamageResistanceHelper.isTracked(self)) return health;

        // 真我结界激活期间：扣血一律归零，兜住绕过 hurt 的直接写血
        if (ZhenWoGuard.isBarrierActiveWearer(self)) {
            return current;
        }

        // 客户端仅承担结界兜底判定，减伤 / 冷却 / 单次上限均为服务端权威
        if (self.level().isClientSide) return health;

        UUID id = self.getUUID();

        // 完全免伤（retain <= 0）：setHealth 层任何扣血都保留当前血量。
        Float retain = DamageResistanceHelper.DAMAGE_RETAIN_MAP.get(id);
        if (retain != null && retain <= 0.0F) {
            return current;
        }

        // --- 冷却中：伤害归零 ---
        Integer cooldown = DamageResistanceHelper.COOLDOWN_MAP.get(id);
        if (cooldown != null && cooldown > 0) {
            return current; // 血量不变
        }

        // --- 未在冷却中：登记了"受击自动冷却"的实体，本次扣血照常生效并立即进入冷却 ---
        // 注意：此处仅在本次扣血已经通过上面的拦截（即允许生效）后写入冷却，因此本次扣血不会被自己挡掉，
        // 从而得到"先扣一次血、随后进入冷却、冷却结束再次受击才扣血"的语义。
        Integer hurtCooldown = DamageResistanceHelper.HURT_COOLDOWN_MAP.get(id);
        if (hurtCooldown != null && hurtCooldown > 0) {
            DamageResistanceHelper.COOLDOWN_MAP.put(id, hurtCooldown);
        }

        float reducedDelta = delta;

        // --- 单次上限：裁剪 reducedDelta ---
        Float cap = DamageResistanceHelper.DAMAGE_CAP_MAP.get(id);
        if (cap != null && cap > 0 && -reducedDelta > cap) {
            reducedDelta = -cap;
        }

        // 实际扣血较原始扣血有削减，说明限伤生效
        if (Math.abs(reducedDelta - delta) > 0.0001F) {
            return current + reducedDelta;
        }

        return health;
    }
}
