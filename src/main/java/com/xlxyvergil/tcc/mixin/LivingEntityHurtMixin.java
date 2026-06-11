package com.xlxyvergil.tcc.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.xlxyvergil.tcc.event.TccAttributeEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * 虚数伤害防减免：在 actuallyHurt() 吸收环节拦截 amount 值。
 * <p>
 * priority=999，在 L2DamageTracker (priority=1000) 内层执行：
 *   L2D 将 LivingDamageEvent 提前触发（某 mod 把 amount 减到 0）
 *   → 我们拿到 L2D 结果后检测 intended → 覆写为意图值。
 *   确保 amount > 0，通过 if (amount != 0) 检查，setHealth 正常走。
 */
@Mixin(value = LivingEntity.class, priority = 999)
public abstract class LivingEntityHurtMixin {

    @WrapOperation(method = "actuallyHurt",
        at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;" +
                "getDamageAfterMagicAbsorb(Lnet/minecraft/world/damagesource/DamageSource;F)F"))
    private float tcc$wrapMagicAbsorb(LivingEntity self, DamageSource source, float damage,
                                      Operation<Float> original) {
        Float intended = TccAttributeEvents.peekIntendedDamage(self);
        float result = original.call(self, source, damage);
        if (intended != null && intended > 0) {
            return intended;
        }
        return result;
    }
}
