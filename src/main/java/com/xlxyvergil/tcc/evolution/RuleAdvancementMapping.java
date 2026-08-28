package com.xlxyvergil.tcc.evolution;

import com.xlxyvergil.tcc.capability.TccPlayerDataCapability;
import com.xlxyvergil.tcc.evolution.AchievementDefinitions.KillCondition;
import com.xlxyvergil.tcc.network.NetworkHandler;
import com.xlxyvergil.tcc.util.EntityConditionHelper;
import net.minecraft.advancements.Advancement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

/**
 * 事件处理器对 AchievementDefinitions 的薄封装，逻辑完全由 achievement_definitions.json 驱动。
 * 进度记录在玩家的 TccPlayerDataCapability（而非成就条件），成就只有一个 step_1 条件；
 * 当累计进度达到目标数时授予 step_1，从而完成成就。
 */
public final class RuleAdvancementMapping {

    /** 子进度 key 分隔符 */
    private static final String SUB_KEY_SEP = "|";
    private RuleAdvancementMapping() {}

    /** 检查玩家是否已完成该成就。 */
    public static boolean isAdvancementDone(ServerPlayer player, String achievementId) {
        if (player.server == null) return false;
        Advancement adv = player.server.getAdvancements().getAdvancement(new ResourceLocation(achievementId));
        if (adv == null) return false;
        return player.getAdvancements().getOrStartProgress(adv).isDone();
    }

    /** 从玩家 Capability 获取当前进度。 */
    public static int getProgress(ServerPlayer player, String achievementId) {
        return TccPlayerDataCapability.getAchievementProgress(player, achievementId);
    }

    /** 写入玩家 Capability 中的进度。 */
    private static void setProgress(ServerPlayer player, String achievementId, int progress) {
        TccPlayerDataCapability.setAchievementProgress(player, achievementId, progress);
    }

    /**
     * 通过累计 NBT 进度授予多个「步」。当进度达到目标时授予 step_1，完成进阶并触发 AdvancementEarnEvent。
     */
    public static void awardSteps(ServerPlayer player, String achievementId, int target, int steps) {
        if (steps <= 0 || target <= 0) return;
        if (player.server == null) return;
        if (isAdvancementDone(player, achievementId)) return;

        int current = getProgress(player, achievementId);
        int newProgress = Math.min(current + steps, target);

        // 当到达 target 时，确保 advancement 存在并能成功 award
        if (newProgress >= target) {
            Advancement adv = player.server.getAdvancements().getAdvancement(new ResourceLocation(achievementId));
            if (adv == null) {
                // 无法 award → 不标记完成，保持在 target - 1
                newProgress = Math.min(current + steps, target - 1);
                if (newProgress <= current) return;
            } else {
                player.getAdvancements().award(adv, "step_1");
            }
        }

        setProgress(player, achievementId, newProgress);
        NetworkHandler.syncAchievementProgress(player, achievementId, newProgress);
    }

    /**
     * 授予下一个条件（1 步）。返回该成就是否已完全完成。
     */
    public static boolean awardNextCriterion(ServerPlayer player, String achievementId, int target) {
        if (target <= 0) return false;
        if (player.server == null) return false;
        if (isAdvancementDone(player, achievementId)) return false;

        int current = getProgress(player, achievementId);
        int newProgress = Math.min(current + 1, target);

        if (newProgress >= target) {
            Advancement adv = player.server.getAdvancements().getAdvancement(new ResourceLocation(achievementId));
            if (adv == null) {
                // 无法 award → 不标记完成
                newProgress = Math.min(current + 1, target - 1);
                if (newProgress <= current) return false;
            } else {
                player.getAdvancements().award(adv, "step_1");
            }
        }

        setProgress(player, achievementId, newProgress);
        NetworkHandler.syncAchievementProgress(player, achievementId, newProgress);

        return newProgress >= target;
    }

    /** 一次性授予所有条件（用于 biome_visit 等一次性触发器）。 */
    public static void awardAll(ServerPlayer player, String achievementId, int target) {
        if (target <= 0) return;
        if (player.server == null) return;
        if (isAdvancementDone(player, achievementId)) return;

        // 先获取 Advancement 并 award，成功后更新 NBT progress。
        // 避免在 adv 不存在时错误地将 progress 设为 target，
        // 导致客户端 tooltip 以为已完成而隐藏显示，但成就实际未达成。
        Advancement adv = player.server.getAdvancements().getAdvancement(new ResourceLocation(achievementId));
        if (adv == null) return;

        player.getAdvancements().award(adv, "step_1");

        setProgress(player, achievementId, target);
        NetworkHandler.syncAchievementProgress(player, achievementId, target);
    }

    // 多类型击杀（AND 语义）

    /**
     * 判断该成就是否使用多类型击杀（AND 语义）。
     */
    public static boolean isMultiTypeKill(AchievementDefinitions.AchievementDef def) {
        return def.isMultiTypeKill();
    }

    /** 构建子进度 key：achievementId|entityType */
    private static String subKey(String achievementId, String entityType) {
        return achievementId + SUB_KEY_SEP + entityType;
    }

    /**
     * 处理多类型击杀的进度更新。每种实体类型的击杀数用复合 key <achievementId>|<entity> 分别追踪。
     * AND 语义：所有子目标均达到后 award；OR 语义：任一子目标达到后 award。同步总进度（各子进度之和）用于 tooltip 显示。
     */
    public static void awardMultiTypeKill(ServerPlayer player, String achievementId,
                                          AchievementDefinitions.AchievementDef def,
                                          LivingEntity killed, int steps) {
        if (player.server == null) return;
        if (isAdvancementDone(player, achievementId)) return;

        List<KillCondition> kills = def.conditions().kills();
        if (kills == null || kills.isEmpty()) return;

        for (KillCondition kc : kills) {
            if (!EntityConditionHelper.matchesEntityKey(kc.entity(), killed)) continue;

            String key = subKey(achievementId, kc.entity());
            int current = TccPlayerDataCapability.getAchievementProgress(player, key);
            int target = kc.criteriaCount();
            int newSub = Math.min(current + steps, target);
            TccPlayerDataCapability.setAchievementProgress(player, key, newSub);
        }

        // 判断是否达成：AND=全部达标，OR=任一达标
        boolean completed;
        if ("and".equals(def.conditions().mode())) {
            completed = true;
            for (KillCondition kc : kills) {
                int sub = TccPlayerDataCapability.getAchievementProgress(player, subKey(achievementId, kc.entity()));
                if (sub < kc.criteriaCount()) {
                    completed = false;
                    break;
                }
            }
        } else {
            // OR 模式
            completed = false;
            for (KillCondition kc : kills) {
                int sub = TccPlayerDataCapability.getAchievementProgress(player, subKey(achievementId, kc.entity()));
                if (sub >= kc.criteriaCount()) {
                    completed = true;
                    break;
                }
            }
        }

        if (completed) {
            Advancement adv = player.server.getAdvancements().getAdvancement(new ResourceLocation(achievementId));
            if (adv == null) return;
            player.getAdvancements().award(adv, "step_1");
        }

        // 同步子进度（用于 tooltip 逐类型显示）
        for (KillCondition kc : kills) {
            String key = subKey(achievementId, kc.entity());
            int sub = TccPlayerDataCapability.getAchievementProgress(player, key);
            NetworkHandler.syncAchievementProgress(player, key, sub);
        }
    }

    /** 检查成就的所有前置条件是否都已达成。 */
    public static boolean arePrerequisitesMet(ServerPlayer player, AchievementDefinitions.AchievementDef def) {
        if (def.prerequisites() == null || def.prerequisites().isEmpty()) return true;
        for (String prereq : def.prerequisites()) {
            if (!isAdvancementDone(player, prereq)) return false;
        }
        return true;
    }
}
