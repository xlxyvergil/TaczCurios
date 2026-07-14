package com.xlxyvergil.tcc.event;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.registries.TccStats;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraftforge.event.entity.living.LivingConversionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class LivingConversionEventHandler {

    private LivingConversionEventHandler() {}

    @SubscribeEvent
    public static void onLivingConvert(LivingConversionEvent.Post event) {
        if (!(event.getEntity() instanceof ZombieVillager zombieVillager)) return;
        if (zombieVillager.level().isClientSide) return;

        UUID converterUUID = zombieVillager.conversionStarter;

        if (converterUUID == null) return;

        if (!(zombieVillager.level() instanceof ServerLevel serverLevel)) return;
        ServerPlayer player = serverLevel.getServer().getPlayerList().getPlayer(converterUUID);
        if (player == null) return;

        ResourceLocation statKey = TccStats.ZOMBIE_VILLAGER_CURED;
        int current = player.getStats().getValue(Stats.CUSTOM.get(statKey));
        player.getStats().setValue(player, Stats.CUSTOM.get(statKey), current + 1);
    }
}
