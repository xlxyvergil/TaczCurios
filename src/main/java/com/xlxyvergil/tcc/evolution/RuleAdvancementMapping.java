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

        // 当到达 criteriaCount 时，确保 advancement 存在并能成功 award
        if (newProgress >= criteriaCount) {
            Advancement adv = player.server.getAdvancements().getAdvancement(new ResourceLocation(achievementId));
            if (adv == null) {
                // 无法 award → 不标记完成，保持在 criteriaCount - 1
                newProgress = Math.min(current + steps, criteriaCount - 1);
                if (newProgress <= current) return;
            } else {
                player.getAdvancements().award(adv, "step_1");
            }
        }

        setProgress(player, achievementId, newProgress);
        NetworkHandler.syncAchievementProgress(player, achievementId, newProgress);
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

        if (newProgress >= criteriaCount) {
            Advancement adv = player.server.getAdvancements().getAdvancement(new ResourceLocation(achievementId));
            if (adv == null) {
                // 无法 award → 不标记完成
                newProgress = Math.min(current + 1, criteriaCount - 1);
                if (newProgress <= current) return false;
            } else {
                player.getAdvancements().award(adv, "step_1");
            }
        }

        setProgress(player, achievementId, newProgress);
        NetworkHandler.syncAchievementProgress(player, achievementId, newProgress);

        return newProgress >= criteriaCount;
    }

    /** Award all criteria at once (for one-time triggers like biome_visit). */
    public static void awardAll(ServerPlayer player, String achievementId, int criteriaCount) {
        if (criteriaCount <= 0) return;
        if (player.server == null) return;
        if (isAdvancementDone(player, achievementId)) return;

        // 先获取 Advancement 并 award，成功后更新 NBT progress。
        // 避免在 adv 不存在时错误地将 progress 设为 criteriaCount，
        // 导致客户端 tooltip 以为已完成而隐藏显示，但成就实际未达成。
        Advancement adv = player.server.getAdvancements().getAdvancement(new ResourceLocation(achievementId));
        if (adv == null) return;

        player.getAdvancements().award(adv, "step_1");

        setProgress(player, achievementId, criteriaCount);
        NetworkHandler.syncAchievementProgress(player, achievementId, criteriaCount);
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
