package com.xlxyvergil.tcc.mixin.apothiccurios;

import com.xlxyvergil.tcc.integration.ApothicCuriosIntegration;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 为 Apotheosis 的 LootCategory 添加 Curios 插槽类别注册支持
 * 参考 Apothic-Curios 的实现
 */
@Mixin(value = LootCategory.class, remap = false)
public class LootCategoryMixin {
    
    @Inject(method = "byId", at = @At("HEAD"))
    private static void registerCurioCategory(
            String id, CallbackInfoReturnable<LootCategory> callbackInfo) {
        // 当查询 curios: 开头的类别时，自动注册
        if (id.startsWith("curios:") && LootCategory.BY_ID.get(id) == null) {
            ApothicCuriosIntegration.init();
        }
    }
}
