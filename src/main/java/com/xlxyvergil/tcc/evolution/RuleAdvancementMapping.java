package com.xlxyvergil.tcc.evolution;

import com.xlxyvergil.tcc.network.NetworkHandler;
import net.minecraft.advancements.Advancement;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

/**
 * Thin wrapper around AchievementDefinitions for the event handlers.
 * <p>
 * All mapping and logic is driven by achievement_definitions.json.
 * This class provides convenience methods for:
 * - Checking if an achievement is done
 * - Awarding criteria (advancing progress via NBT tracking)
 * - Checking prerequisites
 * <p>
 * Progress is tracked in the player's persistent NBT data, not in the advancement
 * criteria. Achievements only have a single {@code step_1} criterion.
 * When the accumulated NBT progress reaches {@code criteriaCount},
 * {@code step_1} is awarded, completing the achievement.
 */
public final class RuleAdvancementMapping {
    private static final String PROGRESS_PREFIX = "tcc_ach_progress_";

    private RuleAdvancementMapping() {}

    /** Check if the player has completed the achievement for this achievement ID. */
    public static boolean isAdvancementDone(ServerPlayer player, String achievementId) {
        if (player.server == null) return false;
        Advancement adv = player.server.getAdvancements().getAdvancement(new ResourceLocation(achievementId));
        if (adv == null) return false;
        return player.getAdvancements().getOrStartProgress(adv).isDone();
    }

    /** Get current progress from player NBT. */
    public static int getProgress(ServerPlayer player, String achievementId) {
        CompoundTag data = player.getPersistentData();
        return data.getInt(PROGRESS_PREFIX + achievementId.replace(':', '_'));
    }

    /** Set progress in player NBT. */
    private static void setProgress(ServerPlayer player, String achievementId, int progress) {
        CompoundTag data = player.getPersistentData();
        data.putInt(PROGRESS_PREFIX + achievementId.replace(':', '_'), progress);
    }

    /**
     * Award multiple "steps" by accumulating NBT progress.
     * When progress reaches criteriaCount, {@code step_1} is awarded,
     * completing the advancement and triggering {@code AdvancementEarnEvent}.
     */
    public static void awardSteps(ServerPlayer player, String achievementId, int criteriaCount, int steps) {
        if (steps <= 0 || criteriaCount <= 0) return;
        if (player.server == null) return;
        if (isAdvancementDone(player, achievementId)) return;

        int current = getProgress(player, achievementId);
        int newProgress = Math.min(current + steps, criteriaCount);
        setProgress(player, achievementId, newProgress);

        // 同步到客户端
        NetworkHandler.syncAchievementProgress(player, achievementId, newProgress);

        if (newProgress >= criteriaCount) {
            Advancement adv = player.server.getAdvancements().getAdvancement(new ResourceLocation(achievementId));
            if (adv != null) {
                player.getAdvancements().award(adv, "step_1");
            }
        }
    }

    /**
     * Award the next criterion (1 step).
     * @return true if the achievement is now fully complete
     */
    public static boolean awardNextCriterion(ServerPlayer player, String achievementId, int criteriaCount) {
        if (criteriaCount <= 0) return false;
        if (player.server == null) return false;
        if (isAdvancementDone(player, achievementId)) return false;

        int current = getProgress(player, achievementId);
        int newProgress = Math.min(current + 1, criteriaCount);
        setProgress(player, achievementId, newProgress);

        // 同步到客户端
        NetworkHandler.syncAchievementProgress(player, achievementId, newProgress);

        if (newProgress >= criteriaCount) {
            Advancement adv = player.server.getAdvancements().getAdvancement(new ResourceLocation(achievementId));
            if (adv != null) {
                player.getAdvancements().award(adv, "step_1");
                return true;
            }
        }
        return false;
    }

    /** Award all criteria at once (for one-time triggers like biome_visit). */
    public static void awardAll(ServerPlayer player, String achievementId, int criteriaCount) {
        if (criteriaCount <= 0) return;
        if (player.server == null) return;
        if (isAdvancementDone(player, achievementId)) return;

        setProgress(player, achievementId, criteriaCount);

        // 同步到客户端
        NetworkHandler.syncAchievementProgress(player, achievementId, criteriaCount);

        Advancement adv = player.server.getAdvancements().getAdvancement(new ResourceLocation(achievementId));
        if (adv != null) {
            player.getAdvancements().award(adv, "step_1");
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
}
