package com.xlxyvergil.tcc.event;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.capability.TccPlayerDataCapability;
import com.xlxyvergil.tcc.evolution.AchievementConditionMatcher;
import com.xlxyvergil.tcc.evolution.AchievementDefinitions;
import com.xlxyvergil.tcc.evolution.RuleAdvancementMapping;
import com.xlxyvergil.tcc.network.NetworkHandler;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;


@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class CurioPlaytimeTracker {

    
    private static final int SYNC_INTERVAL = 100;
    
    private static final int ACHIEVEMENT_CHECK_INTERVAL = 20;

    // 需要累计佩戴时长的饰品 ID（用 ResourceLocation 比较，避免每 tick 生成字符串）
    private static final ResourceLocation GRISEO = ResourceLocation.tryParse("tcc:griseo");
    private static final ResourceLocation HUISHI_ZHIJUAN = ResourceLocation.tryParse("tcc:huishi_zhijuan");
    private static final ResourceLocation FANXING = ResourceLocation.tryParse("tcc:fanxing");
    private static final ResourceLocation QISHI_ZHIJIAN = ResourceLocation.tryParse("tcc:qishi_zhijian");

    private CurioPlaytimeTracker() {}

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.player instanceof ServerPlayer player)) return;

        long t = player.level().getGameTime();

        // 单次遍历识别所佩戴的计时饰品，避免原先每个饰品各扫一遍饰品槽（每 tick 4 次全槽扫描）
        int[] worn = {0};
        CurioSearchHelper.forEachEquippedStack(player, stack -> {
            ResourceLocation id = ForgeRegistries.ITEMS.getKey(stack.getItem());
            if (id == null) return;
            if (GRISEO.equals(id)) worn[0] |= 1;
            else if (HUISHI_ZHIJUAN.equals(id)) worn[0] |= 2;
            else if (FANXING.equals(id)) worn[0] |= 4;
            else if (QISHI_ZHIJIAN.equals(id)) worn[0] |= 8;
        });

        boolean wearing = false;
        if ((worn[0] & 1) != 0) {
            TccPlayerDataCapability.incrementPlayTimeGriseo(player);
            wearing = true;
        }
        if ((worn[0] & 2) != 0) {
            TccPlayerDataCapability.incrementPlayTimeHuishiZhijuan(player);
            wearing = true;
        }
        if ((worn[0] & 4) != 0) {
            TccPlayerDataCapability.incrementPlayTimeFanxing(player);
            wearing = true;
        }
        if ((worn[0] & 8) != 0) {
            TccPlayerDataCapability.incrementPlayTimeQishiZhijian(player);
            wearing = true;
        }

        if (wearing && t % SYNC_INTERVAL == 0) {
            NetworkHandler.syncPlayTime(player);
        }
        if (t % ACHIEVEMENT_CHECK_INTERVAL == 0) {
            awardPlaytimeAchievements(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            resetQishiPlaytime(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.isWasDeath() && event.getEntity() instanceof ServerPlayer player) {
            resetQishiPlaytime(player);
        }
    }

    private static void resetQishiPlaytime(ServerPlayer player) {
        TccPlayerDataCapability.setPlayTimeQishiZhijian(player, 0);
        NetworkHandler.syncPlayTime(player);
    }

    private static void awardPlaytimeAchievements(ServerPlayer player) {
        for (AchievementDefinitions.AchievementDef def : AchievementDefinitions.getByTrigger(AchievementDefinitions.TRIGGER_PLAY_TIME)) {
            if (!def.isEnabled()) continue;
            if (RuleAdvancementMapping.isAdvancementDone(player, def.id())) continue;
            if (!RuleAdvancementMapping.arePrerequisitesMet(player, def)) continue;
            if (!AchievementConditionMatcher.matchesStatBiomeConditions(player, def)) continue;

            AchievementDefinitions.AchievementConditions conds = def.conditions();
            if (conds == null || conds.stat() == null) continue;
            if (TccPlayerDataCapability.getCustomStat(player, conds.stat()) >= def.targetCount()) {
                RuleAdvancementMapping.awardAll(player, def.id(), def.targetCount());
            }
        }
    }
}
