package com.xlxyvergil.tcc.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.logging.LogUtils;
import com.xlxyvergil.tcc.event.TccAttributeEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * 虚数伤害防减免：在 actuallyHurt() 吸收环节拦截 amount 值。
 * <p>
 * L2DamageTracker 在 getDamageAfterMagicAbsorb 之后注入了 ForgeHooks.onLivingDamage，
 * 某个 mod 通过 LivingDamageEvent 把虚数伤害减到 0，导致 MC 后续
 * {@code if (amount != 0.0F)} 条件失败，setHealth 整段被跳过。
 * <p>
 * 我们用 priority=999 在外层 wrap getDamageAfterMagicAbsorb，
 * 让 L2D 的 LivingDamageEvent 正常触发但不影响最终的 amount 值。
 * 有虚数意图时直接返回意图值，否则透传 L2D 的结果。
 */
@Mixin(value = LivingEntity.class, priority = 999)
public abstract class LivingEntityHurtMixin {

    private static final Logger LOGGER = LogUtils.getLogger();

    @WrapOperation(method = "actuallyHurt",
        at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;" +
                "getDamageAfterMagicAbsorb(Lnet/minecraft/world/damagesource/DamageSource;F)F"))
    private float tcc$wrapMagicAbsorb(LivingEntity self, DamageSource source, float damage,
                                      Operation<Float> original) {
        Float intended = TccAttributeEvents.takeIntendedDamage(self);
        float result = original.call(self, source, damage);
        if (intended != null && intended > 0) {
            LOGGER.info("[TCC-MIXIN:WRAP-MAGIC] entity={}, l2dResult={}, overrideTo={}",
                self.getName().getString(), result, intended);
            return intended;
        }
        return result;
    }
}
