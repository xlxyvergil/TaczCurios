package com.xlxyvergil.tcc.evolution;

import com.xlxyvergil.tcc.TaczCurios;
import net.minecraft.advancements.Advancement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Listens for Minecraft's native AdvancementEarnEvent
 * and executes the corresponding reward (grant/evolve) from achievement_definitions.json.
 * <p>
 * This decouples reward execution from the trigger handlers:
 * handlers only award criteria; rewards fire when the advancement is naturally earned.
 * <p>
 * Also handles "auto" trigger achievements:
 * when a reward's autoAchievements list contains child achievement IDs,
 * those achievements are automatically awarded after the main reward executes.
 */
@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class AchievementEventListener {

    private AchievementEventListener() {}

    @SubscribeEvent
    public static void onAdvancementEarned(AdvancementEvent.AdvancementEarnEvent event) {
        Advancement advancement = event.getAdvancement();
        if (advancement == null) return;

        ResourceLocation id = advancement.getId();
        AchievementDefinitions.AchievementDef def =
                AchievementDefinitions.get(id.toString()).orElse(null);
        if (def == null) return;

        ServerPlayer player = (ServerPlayer) event.getEntity();

        // Execute reward (grant/evolve)
        if (def.reward() != null) {
            AchievementRewards.execute(player, def);

            // Award any auto achievements from reward
            if (def.reward().autoAchievements() != null) {
                for (String autoId : def.reward().autoAchievements()) {
                    awardAutoAchievement(player, autoId);
                }
            }
        }
    }

    private static void awardAutoAchievement(ServerPlayer player, String achievementId) {
        AchievementDefinitions.AchievementDef def =
                AchievementDefinitions.get(achievementId).orElse(null);
        if (def == null || !"auto".equals(def.trigger())) return;

        if (RuleAdvancementMapping.isAdvancementDone(player, def.id())) return;

        if (!RuleAdvancementMapping.arePrerequisitesMet(player, def)) return;

        RuleAdvancementMapping.awardAll(player, def.id(), def.criteriaCount());
    }
}
