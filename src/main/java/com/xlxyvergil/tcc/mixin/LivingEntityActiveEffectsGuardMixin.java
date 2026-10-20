package com.xlxyvergil.tcc.mixin;

import com.xlxyvergil.tcc.util.TccEffectGuard;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.Map;

/**
 * tcc 效果保护入口 3：让 getActiveEffectsMap() / getActiveEffects() 返回受控视图。
 *
 * 不替换 activeEffects 字段，vanilla 的批量移除、到期、死亡、NBT 读档都按原版跑。
 * 覆盖 getActiveEffectsMap().remove(...)、getActiveEffects().clear() 这类直接改表。
 *
 * 1.21 起 activeEffects 以 Holder<MobEffect> 为键（1.20 是 MobEffect 键），因此受控视图也按 Holder 键包装。
 * 1.21.1 用官方映射名，不需要 refmap 反查 SRG。
 */
@Mixin(value = LivingEntity.class, priority = 1500)
@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class LivingEntityActiveEffectsGuardMixin {

    @Shadow
    @Final
    private Map<Holder<MobEffect>, MobEffectInstance> activeEffects;

    @Inject(method = "getActiveEffectsMap", at = @At("RETURN"), cancellable = true)
    private void tcc$guardEffectsMap(CallbackInfoReturnable<Map> cir) {
        cir.setReturnValue(TccEffectGuard.guarded((LivingEntity) (Object) this, this.activeEffects));
    }

    @Inject(method = "getActiveEffects", at = @At("HEAD"), cancellable = true)
    private void tcc$guardEffectsView(CallbackInfoReturnable<Collection> cir) {
        cir.setReturnValue(TccEffectGuard.guarded((LivingEntity) (Object) this, this.activeEffects).values());
    }
}
