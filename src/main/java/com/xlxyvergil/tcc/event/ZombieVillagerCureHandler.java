package com.xlxyvergil.tcc.event;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.capability.TccPlayerDataCapability;
import com.xlxyvergil.tcc.evolution.AchievementConditionMatcher;
import com.xlxyvergil.tcc.evolution.AchievementDefinitions;
import com.xlxyvergil.tcc.evolution.RuleAdvancementMapping;
import com.xlxyvergil.tcc.network.NetworkHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.neoforged.neoforge.event.entity.living.LivingConversionEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;

import java.util.UUID;


@EventBusSubscriber(modid = TaczCurios.MODID)
public final class ZombieVillagerCureHandler {

    private ZombieVillagerCureHandler() {}

    @SubscribeEvent
    public static void onLivingConversion(LivingConversionEvent.Post event) {
        if (!(event.getEntity() instanceof ZombieVillager zombie)) return;
        CompoundTag saved = zombie.saveWithoutId(new CompoundTag());
        if (!saved.hasUUID("ConversionPlayer")) return;
        UUID starter = saved.getUUID("ConversionPlayer");

        ServerLevel serverLevel = (ServerLevel) zombie.level();
        ServerPlayer player = serverLevel.getServer().getPlayerList().getPlayer(starter);
        if (player == null) return;

        String statKey = "tcc:zombie_villager_cured";
        TccPlayerDataCapability.incrementCustomStat(player, statKey, 1);
        NetworkHandler.syncCustomStat(player, statKey, TccPlayerDataCapability.getCustomStat(player, statKey));

        for (AchievementDefinitions.AchievementDef def : AchievementDefinitions.getByTrigger(AchievementDefinitions.TRIGGER_ZOMBIE_VILLAGER_CURED)) {
            if (!def.isEnabled()) continue;
            if (RuleAdvancementMapping.isAdvancementDone(player, def.id())) continue;
            if (!RuleAdvancementMapping.arePrerequisitesMet(player, def)) continue;
            if (!AchievementConditionMatcher.matchesStatBiomeConditions(player, def)) continue;

            RuleAdvancementMapping.awardSteps(player, def.id(), def.targetCount(), 1);
        }
    }
}
