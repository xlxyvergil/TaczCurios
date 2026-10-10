package com.xlxyvergil.tcc.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.xlxyvergil.tcc.util.DamageResistanceHelper;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

/** 写血出口（SynchedEntityData）强制免伤：血量落地前按 FORCED_RETAIN_MAP 的保留因子削减，仅服务端玩家。 */
@Mixin(value = SynchedEntityData.class, priority = 2000)
public abstract class SynchedEntityDataMixin {

    @Shadow @Final private Entity entity;

    @WrapOperation(
            method = "set(Lnet/minecraft/network/syncher/EntityDataAccessor;Ljava/lang/Object;Z)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/network/syncher/SynchedEntityData$DataItem;setValue(Ljava/lang/Object;)V"))
    private void tcc$forceDamageReduction(SynchedEntityData.DataItem<?> dataItem, Object incomingValue,
            Operation<Void> original,
            EntityDataAccessor<?> accessor, Object value, boolean force) {
        // 仅血量，避免误伤同为 Float 型的吸收心
        if (accessor != LivingEntity.DATA_HEALTH_ID || !(incomingValue instanceof Float incoming)) {
            original.call(dataItem, incomingValue);
            return;
        }

        // 仅服务端玩家
        if (!(this.entity instanceof Player player) || player.level().isClientSide) {
            original.call(dataItem, incomingValue);
            return;
        }

        Float retain = DamageResistanceHelper.FORCED_RETAIN_MAP.get(player.getUUID());
        if (retain == null || retain >= 1.0F) {
            original.call(dataItem, incomingValue);
            return;
        }

        // 治疗或血量持平不削减
        float current = player.getHealth();
        if (incoming >= current) {
            original.call(dataItem, incomingValue);
            return;
        }

        // 按保留因子削减本次扣血，并同步对账基线以免每 tick 对账重复削减
        float adjusted = Mth.clamp(current + (incoming - current) * retain, 0.0F, player.getMaxHealth());
        original.call(dataItem, adjusted);
        DamageResistanceHelper.updateForcedBaseline(player, adjusted);
    }
}
