package com.xlxyvergil.tcc.mixin;

import com.xlxyvergil.tcc.util.DamageResistanceHelper;
import com.xlxyvergil.tcc.util.ZhenWoGuard;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

/**
 * 玩家减伤 Mixin：常驻比例减伤采用每 tick 血量对账（DamageResistanceHelper.reconcileHealth）对任意来源
 * （含绕过 setHealth 的直接写入）统一按保留因子削减；此处的 setHealth 拦截仅保留受伤冷却与单次上限两类受击触发逻辑。
 */
@Mixin(value = LivingEntity.class, priority = 2000)
public abstract class DamageResistanceMixin {

    /** 直接访问合并目标 LivingEntity.dead 字段，供完全免伤时强制复活清除死亡标记。 */
    @Accessor("dead")
    abstract void tcc$setDead(boolean dead);

    // ---- tick：冷却递减 + 常驻比例减伤对账 + 完全免伤兜底复活 ----

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

        // 佩戴真我且免死可用（结界未在冷却中）的实体：对绕过死亡事件取消的强制死亡仍复活。
        // 普通可取消死亡已在 LivingDeathEvent 中被 ZhenWo.onLivingDeath 取消并回满血，因此这里的
        // isDeadOrDying() 只会命中忽略事件取消、直接写血/置死的绕过实现（如亚波伦 RevelationFix），
        // 也顺带兜住结界期间经 catchSetTrueHealth 直接写血致死的场景，使真我佩戴者真正不死。
        // 免死与结界共用同一冷却：冷却期间不再复活，与 ZhenWo.onLivingDeath 保持一致。
        Float retain = DamageResistanceHelper.DAMAGE_RETAIN_MAP.get(id);
        if (retain != null && self.isDeadOrDying() && ZhenWoGuard.canPreventDeath(self)) {
            reviveFully(self);
        }
    }

    /**
     * 完全免伤实体强制复活：清除死亡标记、复位死亡计时、恢复站立姿态；
     * 用于兜住绕过 setHealth 直接写血量的第三方伤害（每次把血打到 0 并置死）。
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

    // ---- 真我结界伤害免疫 ----

    /**
     * 真我结界激活期间免疫一切伤害：在 hurt 入口直接取消，近战/弹射物/爆炸/第三方伤害均不生效。
     */
    @Inject(method = "hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z", at = @At("HEAD"), cancellable = true)
    private void tcc$barrierImmune(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (ZhenWoGuard.isBarrierActiveWearer(self)) {
            cir.setReturnValue(false);
        }
    }

    /**
     * 处于免死保护中的真我佩戴者的血量出口兜底：判定血量被外部压到 0 以下时对外一律返回满血，
     * 避免下游死亡判定、AI 与客户端显示把佩戴者当作已死。结界冷却期间不兜底，佩戴者会正常死亡。
     */
    @Inject(method = "getHealth", at = @At("RETURN"), cancellable = true)
    private void tcc$barrierHealthFloor(CallbackInfoReturnable<Float> cir) {
        if (cir.getReturnValueF() > 0.0F) return;
        LivingEntity self = (LivingEntity) (Object) this;
        if (!ZhenWoGuard.canPreventDeath(self)) return;
        cir.setReturnValue(self.getMaxHealth());
    }

    // ---- setHealth 拦截（仅冷却 / 单次上限） ----

    @ModifyVariable(method = "setHealth", at = @At("HEAD"), argsOnly = true)
    private float tcc$modifySetHealth(float health) {
        LivingEntity self = (LivingEntity) (Object) this;

        float current = self.getHealth();
        float delta = health - current;

        // 仅拦截受伤
        if (delta >= 0.0F) return health;

        // 真我结界激活期间：扣血一律归零，兜住绕过 hurt 的直接写血
        if (ZhenWoGuard.isBarrierActiveWearer(self)) {
            return current;
        }

        UUID id = self.getUUID();

        // 完全免伤（retain <= 0）：setHealth 层任何扣血都保留当前血量；
        // 绕过 setHealth 直接写血的第三方伤害由 tick 末的强制复活（reviveFully）兜底。
        Float retain = DamageResistanceHelper.DAMAGE_RETAIN_MAP.get(id);
        if (retain != null && retain <= 0.0F) {
            return current;
        }

        // --- 冷却：伤害归零 ---
        Integer cooldown = DamageResistanceHelper.COOLDOWN_MAP.get(id);
        if (cooldown != null && cooldown > 0) {
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
            return current + reducedDelta;
        }

        return health;
    }
}
