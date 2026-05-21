package com.xlxyvergil.tcc.mixin.apothiccurios;

import com.xlxyvergil.tcc.integration.ApothicCuriosIntegration;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 在附魔后处理事件中应用饰品词缀效果
 * 参考 Apothic-Curios 的实现
 */
@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {
    
    /**
     * Injection to {@link EnchantmentHelper#doPostDamageEffects(LivingEntity, Entity)}
     */
    @Inject(
            at = @At("TAIL"),
            method = "doPostDamageEffects(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/Entity;)V"
    )
    private static void affixPostDamageEffects(LivingEntity user, Entity target, CallbackInfo ci) {
        if (user == null) return;
        // 这里需要调用 Apotheosis 的词缀处理方法
        // 但由于是 compileOnly 依赖，需要通过反射或接口调用
        // 暂时留空，实际功能由 ApothicCuriosIntegration 处理
    }

    /**
     * Injection to {@link EnchantmentHelper#doPostHurtEffects(LivingEntity, Entity)}
     */
    @Inject(
            at = @At("TAIL"),
            method = "doPostHurtEffects(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/Entity;)V"
    )
    private static void affixPostHurtEffects(LivingEntity user, Entity attacker, CallbackInfo ci) {
        if (user == null) return;
        // 这里需要调用 Apotheosis 的词缀处理方法
        // 但由于是 compileOnly 依赖，需要通过反射或接口调用
        // 暂时留空，实际功能由 ApothicCuriosIntegration 处理
    }
}
