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
 * 监听 {@link LivingConversionEvent.Post}，在僵尸村民成功转化为村民后
 * 递增 {@link TccStats#ZOMBIE_VILLAGER_CURED} 统计。
 * <p>
 * 该事件的触发点（{@code ForgeEventFactory.onLivingConvert()}）位于
 * {@code ZombieVillager.finishConversion()} 末尾，与原版"僵尸医生"成就判定（
 * {@code CriteriaTriggers.CURED_ZOMBIE_VILLAGER.trigger()}）共享完全相同的执行条件，
 * 不受整合包环境中其他模组异常调用 {@code finishConversion} 的影响。
 * <p>
 * 依赖 Access Transformer 暴露了 {@code ZombieVillager.conversionStarter} 字段。
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
