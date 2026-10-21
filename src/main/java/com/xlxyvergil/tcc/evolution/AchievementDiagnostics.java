package com.xlxyvergil.tcc.evolution;

import com.xlxyvergil.tcc.util.CurioSearchHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * 成就判定的诊断日志。
 * <p>
 * 成就链是「前置成就未完成 → 静默跳过」的设计，玩家侧没有任何反馈，
 * 出问题时无法区分「条件没满足」和「前置没完成」。这里在玩家登录时把所有
 * 未完成、且带前置的成就打印出来，逐条列出前置的完成状态，便于定位。
 */
public final class AchievementDiagnostics {

    private static final Logger LOGGER = LogManager.getLogger();

    private AchievementDiagnostics() {}

    /** 登录时打印：未完成且带前置的成就 + 各前置的完成状态 + 当前佩戴饰品。 */
    public static void logGateState(ServerPlayer player) {
        LOGGER.info("[tcc][gate] 玩家 {} 成就前置检查（佩戴饰品={}）",
                player.getName().getString(), equippedIds(player));

        for (AchievementDefinitions.AchievementDef def : AchievementDefinitions.all()) {
            if (def.reward() == null) continue;
            if (!def.isEnabled()) continue;
            if (def.prerequisites() == null || def.prerequisites().isEmpty()) continue;
            if (RuleAdvancementMapping.isAdvancementDone(player, def.id())) continue;

            StringBuilder sb = new StringBuilder();
            for (String prereq : def.prerequisites()) {
                if (sb.length() > 0) sb.append(' ');
                sb.append(prereq).append('=')
                        .append(RuleAdvancementMapping.isAdvancementDone(player, prereq));
            }
            LOGGER.info("[tcc][gate] {} 未完成 前置: {} 目标: {} 进度: {}/{}",
                    def.id(), sb, String.valueOf(def.conditions()),
                    RuleAdvancementMapping.getProgress(player, def.id()), def.targetCount());
        }
    }

    private static List<String> equippedIds(ServerPlayer player) {
        List<String> ids = new ArrayList<>();
        CurioSearchHelper.forEachEquippedStack(player, stack -> ids.add(itemId(stack)));
        return ids;
    }

    private static String itemId(ItemStack stack) {
        if (stack.isEmpty()) return "";
        ResourceLocation key = ForgeRegistries.ITEMS.getKey(stack.getItem());
        return key != null ? key.toString() : "";
    }
}
