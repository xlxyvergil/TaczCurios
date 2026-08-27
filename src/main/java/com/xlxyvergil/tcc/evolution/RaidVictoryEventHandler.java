package com.xlxyvergil.tcc.evolution;

import net.minecraft.server.level.ServerPlayer;

/**
 * raid_victory 触发器的成就处理器。
 *
 * 由 {@code RaidMixin} 在袭击胜利时为每个参与玩家调用。
 * 每次袭击胜利一律 +1 进度，到达 conditions.criteria_count 后发放对应成就。
 * 佩戴校验在胜利瞬间进行（matchesStatBiomeConditions）。
 * 结构与 {@link GunKillEventHandler} 保持一致，仅将 kills 换成 criteria_count 代表所需袭击胜利次数。
 */
public final class RaidVictoryEventHandler {
    public static final String TRIGGER_RAID_VICTORY = "raid_victory";

    private RaidVictoryEventHandler() {}

    public static void handleRaidVictory(ServerPlayer serverPlayer) {
        if (serverPlayer == null || serverPlayer.isSpectator()) return;

        // ===== Achievement-driven GRANT / EVOLVE =====
        for (AchievementDefinitions.AchievementDef def : AchievementDefinitions.getByTrigger(TRIGGER_RAID_VICTORY)) {
            // Only handle grant and evolve types
            if (def.reward() == null) continue;

            // Skip disabled achievements
            if (!def.isEnabled()) continue;

            // Check prerequisites
            if (!RuleAdvancementMapping.arePrerequisitesMet(serverPlayer, def)) continue;

            // Already completed?
            if (RuleAdvancementMapping.isAdvancementDone(serverPlayer, def.id())) continue;

            // Check conditions (equipped curios / attributes / dimension)
            if (!AchievementConditionMatcher.matchesStatBiomeConditions(serverPlayer, def)) continue;

            // Award criterion(s) — 1 step per raid victory
            RuleAdvancementMapping.awardNextCriterion(serverPlayer, def.id(), def.targetCount());
        }
    }
}
