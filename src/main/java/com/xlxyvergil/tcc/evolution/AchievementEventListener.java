package com.xlxyvergil.tcc.evolution;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.registries.TccItems;
import net.minecraft.advancements.Advancement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 监听 AdvancementEarnEvent，执行 achievement_definitions.json 中对应的奖励（发放/进化）。
 * 奖励执行与触发处理器解耦：处理器只授予条件，奖励在成就自然达成时触发；
 * reward 的 autoAchievements 列表会在主奖励执行后自动授予相应子成就。
 */
@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class AchievementEventListener {

    /** 「真我」：达成 12 个系列成就的最终成就后自动授予。 */
    private static final String ZHEN_WO_ACHIEVEMENT = "tcc:zhen_wo";

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

        if (def.reward() != null) {
            AchievementRewards.execute(player, def);

            if (def.reward().autoAchievements() != null) {
                for (String autoId : def.reward().autoAchievements()) {
                    awardAutoAchievement(player, autoId);
                }
            }
        }

        // 「真我」：当 12 个系列成就的最终成就全部完成时自动授予
        awardAutoAchievement(player, ZHEN_WO_ACHIEVEMENT);
    }

    private static void awardAutoAchievement(ServerPlayer player, String achievementId) {
        AchievementDefinitions.AchievementDef def =
                AchievementDefinitions.get(achievementId).orElse(null);
        if (def == null || !"auto".equals(def.trigger())) return;

        if (RuleAdvancementMapping.isAdvancementDone(player, def.id())) return;

        if (!RuleAdvancementMapping.arePrerequisitesMet(player, def)) return;

        RuleAdvancementMapping.awardAll(player, def.id(), def.targetCount());

        // 「真我」达成后直接授予达成者 2 个纠缠之缘（普通物品，放到背包）
        if (ZHEN_WO_ACHIEVEMENT.equals(achievementId)) {
            ItemStack reward = new ItemStack(TccItems.JIU_CHAN_ZHI_YUAN, 2);
            if (!player.getInventory().add(reward)) {
                player.drop(reward, false);
            }
        }
    }
}
