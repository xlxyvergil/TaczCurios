package com.xlxyvergil.tcc.event;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.evolution.AchievementConditionMatcher;
import com.xlxyvergil.tcc.evolution.AchievementDefinitions;
import com.xlxyvergil.tcc.evolution.RuleAdvancementMapping;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 钓鱼次数统计。
 */
@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class FishCaughtStatHandler {

    private FishCaughtStatHandler() {
    }

    @SubscribeEvent
    public static void onItemFished(ItemFishedEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        for (AchievementDefinitions.AchievementDef def : AchievementDefinitions.getByTrigger(AchievementDefinitions.TRIGGER_FISH_CAUGHT)) {
            if (!def.isEnabled()) continue;
            if (RuleAdvancementMapping.isAdvancementDone(player, def.id())) continue;
            if (!RuleAdvancementMapping.arePrerequisitesMet(player, def)) continue;
            if (!AchievementConditionMatcher.matchesStatBiomeConditions(player, def)) continue;

            RuleAdvancementMapping.awardSteps(player, def.id(), def.targetCount(), 1);
        }
    }
}
