package com.xlxyvergil.tcc.mixin;

import com.xlxyvergil.tcc.evolution.RaidVictoryEventHandler;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.raid.Raid;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;
import java.util.UUID;

/**
 * 在 Raid.status 被写为 VICTORY 后，遍历原版维护的 heroesOfTheVillage 集合逐人派发袭击胜利；
 * 注入点与同版本已验证的 Mod 一致，仅以 isVictory() 守卫。
 */
@Mixin(Raid.class)
public abstract class RaidMixin {
    @Shadow @Final private ServerLevel level;
    @Shadow @Final private Set<UUID> heroesOfTheVillage;

    @Shadow public abstract boolean isVictory();

    @Inject(
            method = {"tick"},
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/world/entity/raid/Raid;status:Lnet/minecraft/world/entity/raid/Raid$RaidStatus;",
                    opcode = 181,
                    shift = At.Shift.AFTER
            )
    )
    private void tcc$onRaidVictory(CallbackInfo ci) {
        if (!this.isVictory()) return;
        for (UUID uuid : this.heroesOfTheVillage) {
            Entity entity = this.level.getEntity(uuid);
            if (entity instanceof ServerPlayer player && !player.isSpectator()) {
                RaidVictoryEventHandler.handleRaidVictory(player);
            }
        }
    }
}
