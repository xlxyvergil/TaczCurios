package com.xlxyvergil.tcc.event;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.evolution.AchievementConditionMatcher;
import com.xlxyvergil.tcc.evolution.AchievementDefinitions;
import com.xlxyvergil.tcc.evolution.RuleAdvancementMapping;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.entity.player.ItemFishedEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;

/**
 * 钓鱼次数统计。
 */
@EventBusSubscriber(modid = TaczCurios.MODID)
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
