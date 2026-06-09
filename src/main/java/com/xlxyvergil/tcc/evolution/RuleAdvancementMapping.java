package com.xlxyvergil.tcc.evolution;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

/**
 * Thin wrapper around AchievementDefinitions for the event handlers.
 * <p>
 * All mapping and logic is driven by achievement_definitions.json.
 * This class provides convenience methods for:
 * - Checking if an achievement is done
 * - Awarding criteria (advancing progress)
 * - Checking prerequisites
 */
public final class RuleAdvancementMapping {
    private RuleAdvancementMapping() {}

    /** Check if the player has completed the achievement for this achievement ID. */
    public static boolean isAdvancementDone(ServerPlayer player, String achievementId) {
        if (player.server == null) return false;
        Advancement adv = player.server.getAdvancements().getAdvancement(new ResourceLocation(achievementId));
        if (adv == null) return false;
        return player.getAdvancements().getOrStartProgress(adv).isDone();
    }

    /**
     * Award the next uncompleted criterion for an achievement.
     * @return true if the achievement is now fully complete
     */
    public static boolean awardNextCriterion(ServerPlayer player, String achievementId, int criteriaCount) {
        if (player.server == null) return false;
        ResourceLocation id = new ResourceLocation(achievementId);
        Advancement adv = player.server.getAdvancements().getAdvancement(id);
        if (adv == null) return false;
        var progress = player.getAdvancements().getOrStartProgress(adv);
        if (progress.isDone()) return false;

        int step = nextUndone(progress, criteriaCount);
        player.getAdvancements().award(adv, "step_" + step);
        return player.getAdvancements().getOrStartProgress(adv).isDone();
    }

    /** Award all criteria at once (for single-step grants). */
    public static void awardAll(ServerPlayer player, String achievementId, int criteriaCount) {
        if (player.server == null) return;
        ResourceLocation id = new ResourceLocation(achievementId);
        Advancement adv = player.server.getAdvancements().getAdvancement(id);
        if (adv == null) return;
        var progress = player.getAdvancements().getOrStartProgress(adv);
        if (progress.isDone()) return;

        for (int i = nextUndone(progress, criteriaCount); i <= criteriaCount; i++) {
            player.getAdvancements().award(adv, "step_" + i);
        }
    }

    /** Check if ALL prerequisites for an achievement are complete. */
    public static boolean arePrerequisitesMet(ServerPlayer player, AchievementDefinitions.AchievementDef def) {
        if (def.prerequisites() == null || def.prerequisites().isEmpty()) return true;
        for (String prereq : def.prerequisites()) {
            if (!isAdvancementDone(player, prereq)) return false;
        }
        return true;
    }

    private static int nextUndone(AdvancementProgress progress, int maxSteps) {
        for (int i = 1; i <= maxSteps; i++) {
            var cp = progress.getCriterion("step_" + i);
            if (cp == null || !cp.isDone()) return i;
        }
        return maxSteps;
    }
}
