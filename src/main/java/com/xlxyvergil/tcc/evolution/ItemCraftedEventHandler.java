package com.xlxyvergil.tcc.evolution;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.capability.TccPlayerDataCapability;
import com.xlxyvergil.tcc.network.NetworkHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 监听合成事件，每次合成递增 ITEMS_CRAFTED 计数并授予对应成就。
 * 替代原版 minecraft:interact_with_crafting_table（只统计打开合成台界面而非实际合成）；Refined Storage 的 shift 批量合成只触发一次，可能被低估。
 */
@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ItemCraftedEventHandler {

    private ItemCraftedEventHandler() {}

    @SubscribeEvent
    public static void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
        if (event.getEntity() == null) return;
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        String statKey = "tcc:items_crafted";
        TccPlayerDataCapability.incrementCustomStat(player, statKey, 1);
        NetworkHandler.syncCustomStat(player, statKey, TccPlayerDataCapability.getCustomStat(player, statKey));

        for (AchievementDefinitions.AchievementDef def : AchievementDefinitions.getByTrigger(AchievementDefinitions.TRIGGER_ITEMS_CRAFTED)) {
            if (!def.isEnabled()) continue;
            if (RuleAdvancementMapping.isAdvancementDone(player, def.id())) continue;
            if (!RuleAdvancementMapping.arePrerequisitesMet(player, def)) continue;
            if (!AchievementConditionMatcher.matchesStatBiomeConditions(player, def)) continue;

            RuleAdvancementMapping.awardSteps(player, def.id(), def.targetCount(), 1);
        }
    }
}
