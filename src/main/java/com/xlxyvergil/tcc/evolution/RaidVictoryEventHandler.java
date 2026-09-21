package com.xlxyvergil.tcc.evolution;

import net.minecraft.server.level.ServerPlayer;

/**
 * raid_victory 触发器的成就处理器，由 RaidMixin 在袭击胜利时为每个参与玩家调用。
 * 每次胜利 +1 进度，达到 criteria_count 后发放成就；佩戴校验在胜利瞬间进行；结构与 GunKillEventHandler 一致。
 */
public final class RaidVictoryEventHandler {
    public static final String TRIGGER_RAID_VICTORY = "raid_victory";

    private RaidVictoryEventHandler() {}

    public static void handleRaidVictory(ServerPlayer serverPlayer) {
        if (serverPlayer == null || serverPlayer.isSpectator()) return;

        // 成就驱动的发放 / 进化
        for (AchievementDefinitions.AchievementDef def : AchievementDefinitions.getByTrigger(TRIGGER_RAID_VICTORY)) {
            if (def.reward() == null) continue;

            if (!def.isEnabled()) continue;

            if (!RuleAdvancementMapping.arePrerequisitesMet(serverPlayer, def)) continue;

            if (RuleAdvancementMapping.isAdvancementDone(serverPlayer, def.id())) continue;

            if (!AchievementConditionMatcher.matchesStatBiomeConditions(serverPlayer, def)) continue;

            // 每次袭击胜利授予 1 步进度
            RuleAdvancementMapping.awardNextCriterion(serverPlayer, def.id(), def.targetCount());
        }
    }
}
