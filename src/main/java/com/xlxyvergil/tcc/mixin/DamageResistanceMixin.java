package com.xlxyvergil.tcc.mixin;

import com.xlxyvergil.tcc.util.DamageResistanceHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

/**
 * 玩家减伤 Mixin，在 {@link LivingEntity#setHealth(float)} 层面拦截血量下降。
 * <p>公共 API 在 {@link com.xlxyvergil.tcc.util.DamageResistanceHelper} 中。
 */
@Mixin(value = LivingEntity.class, priority = 2000)
public abstract class DamageResistanceMixin {

    // ==================== tick 中的冷却递减 ====================

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
    }

    // ==================== setHealth 拦截 ====================

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
        Integer cooldown = DamageResistanceHelper.COOLDOWN_MAP.get(id);
        if (cooldown != null && cooldown > 0) {
            return current; // 血量不变
        }

        // --- 限伤：裁剪 delta ---
        Float cap = DamageResistanceHelper.DAMAGE_CAP_MAP.get(id);
        if (cap != null && cap > 0 && -delta > cap) {
            return current - cap;
        }

        return health;
    }
}
