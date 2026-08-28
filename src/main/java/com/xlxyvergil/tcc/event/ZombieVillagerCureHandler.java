package com.xlxyvergil.tcc.event;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.registries.TccStats;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraftforge.event.entity.living.LivingConversionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 监听 LivingConversionEvent.Post，僵尸村民转化成功后递增 ZOMBIE_VILLAGER_CURED 统计；
 * 触发点与原版"僵尸医生"成就判定共享同一条件，不受其他模组异常调用 finishConversion 影响。
 */
@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ZombieVillagerCureHandler {

    private ZombieVillagerCureHandler() {}

    @SubscribeEvent
    public static void onLivingConversion(LivingConversionEvent.Post event) {
        if (!(event.getEntity() instanceof ZombieVillager zombie)) return;
        if (zombie.conversionStarter == null) return;

        ServerLevel serverLevel = (ServerLevel) zombie.level();
        ServerPlayer player = serverLevel.getServer().getPlayerList().getPlayer(zombie.conversionStarter);
        if (player != null) {
            player.awardStat(Stats.CUSTOM.get(TccStats.ZOMBIE_VILLAGER_CURED));
        }
    }
}
