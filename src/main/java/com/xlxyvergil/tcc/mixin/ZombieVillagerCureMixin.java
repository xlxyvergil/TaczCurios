package com.xlxyvergil.tcc.mixin;

import com.xlxyvergil.tcc.registries.TccStats;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.monster.ZombieVillager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

/**
 * 在 ZombieVillager.finishConversion() 头部注入，检测并记录治愈僵尸村民的统计。
 * 与原版 CriterionTriggers.CURED_ZOMBIE_VILLAGER.trigger() 在同一位置触发，
 * 确保每次治疗只计数一次。
 * <p>
 * 注意：Forge 1.20.1 将 finishConversion 参数从 DifficultyInstance 改为 ServerLevel。
 */
@Mixin(ZombieVillager.class)
public abstract class ZombieVillagerCureMixin {

    @Shadow
    private UUID conversionStarter;

    @Inject(method = "finishConversion", at = @At("HEAD"))
    private void tcc$onCure(ServerLevel serverLevel, CallbackInfo ci) {
        if (conversionStarter == null) return;

        ServerPlayer player = serverLevel.getServer().getPlayerList().getPlayer(conversionStarter);
        if (player == null) return;

        ResourceLocation statKey = TccStats.ZOMBIE_VILLAGER_CURED;
        int current = player.getStats().getValue(Stats.CUSTOM.get(statKey));
        player.getStats().setValue(player, Stats.CUSTOM.get(statKey), current + 1);
    }
}
