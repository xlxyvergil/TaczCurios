package com.xlxyvergil.tcc.evolution;

import com.xlxyvergil.tcc.TaczCurios;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.Optional;

/**
 * Low-frequency polling handler for stat_polling and biome_visit achievements.
 * <p>
 * stat_polling: reads Minecraft's built-in Stats (player.getStats().getValue())
 * and awards criteria when the stat value reaches the configured threshold.
 * <p>
 * biome_visit: checks the player's current biome (player.level().getBiome())
 * and awards the achievement when the player stands in the target biome.
 * <p>
 * Polling interval:
 * - stat_polling: every 3 ticks (same as FTB Quests)
 * - biome_visit: every 20 ticks (1 second)
 */
@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class StatPollingEventHandler {

    private static final String TRIGGER_STAT = "stat_polling";
    private static final String TRIGGER_BIOME = "biome_visit";

    // Cached after first access (lists don't change at runtime)
    private static List<AchievementDefinitions.AchievementDef> statDefs;
    private static List<AchievementDefinitions.AchievementDef> biomeDefs;
    private static boolean cacheBuilt;

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.player instanceof ServerPlayer player)) return;
        if (player.isSpectator()) return;

        buildCache();

        long t = player.level().getGameTime();

        // stat_polling: every 3 ticks
        if (t % 3 == 0 && statDefs != null) {
            for (var def : statDefs) {
                checkStat(player, def);
            }
        }

        // biome_visit: every 20 ticks → 玩家每 tick 只占 一次 getBiome()
        if (t % 20 == 0 && biomeDefs != null) {
            for (var def : biomeDefs) {
                checkBiome(player, def);
            }
        }
    }

    // ===================== stat_polling =====================

    private static void checkStat(ServerPlayer player, AchievementDefinitions.AchievementDef def) {
        if (!def.isEnabled()) return;
        if (RuleAdvancementMapping.isAdvancementDone(player, def.id())) return;
        if (!RuleAdvancementMapping.arePrerequisitesMet(player, def)) return;

        AchievementDefinitions.AchievementConditions conds = def.conditions();
        if (conds == null || conds.stat() == null) return;

        ResourceLocation statId = ResourceLocation.tryParse(conds.stat());
        if (statId == null) return;

        // Look up in BuiltInRegistries.CUSTOM_STAT
        ResourceLocation registered = BuiltInRegistries.CUSTOM_STAT.get(statId);
        if (registered == null) {
            // Try path-only fallback (for mod stats registered in vanilla namespace)
            registered = BuiltInRegistries.CUSTOM_STAT.get(new ResourceLocation(statId.getPath()));
        }
        if (registered == null) return;

        int current = player.getStats().getValue(Stats.CUSTOM.get(registered));
        int threshold = conds.statThreshold();
        int set = Math.min(threshold, current);

        if (set > 0) {
            // Award all steps up to current value
            // For criteria_count = threshold, award steps based on progress
            awardStatProgress(player, def, set, threshold);
        }
    }

    private static void awardStatProgress(ServerPlayer player, AchievementDefinitions.AchievementDef def,
                                           int current, int threshold) {
        if (def.criteriaCount() <= 0) return;

        // Align: if criteriaCount == 1, award all when current >= threshold
        // If criteriaCount > 1, use it as step count
        if (def.criteriaCount() == 1) {
            if (current >= threshold) {
                RuleAdvancementMapping.awardAll(player, def.id(), def.criteriaCount());
            }
        } else {
            // Multi-step: criteriaCount is the threshold, each step = 1 stat point
            int steps = Math.min(current, def.criteriaCount());
            // Re-award all steps up to current (idempotent, already-done steps are skipped)
            var rl = new ResourceLocation(def.id());
            var adv = player.server.getAdvancements().getAdvancement(rl);
            if (adv == null) return;
            for (int i = 1; i <= steps; i++) {
                var cp = player.getAdvancements().getOrStartProgress(adv).getCriterion("step_" + i);
                if (cp != null && !cp.isDone()) {
                    player.getAdvancements().award(adv, "step_" + i);
                }
            }
        }
    }

    // ===================== biome_visit =====================

    /**
     * Check if the player is currently standing in the target biome.
     */
    private static void checkBiome(ServerPlayer player, AchievementDefinitions.AchievementDef def) {
        if (!def.isEnabled()) return;
        if (RuleAdvancementMapping.isAdvancementDone(player, def.id())) return;
        if (!RuleAdvancementMapping.arePrerequisitesMet(player, def)) return;

        AchievementDefinitions.AchievementConditions conds = def.conditions();
        if (conds == null || conds.biome() == null) return;

        if (isInBiome(player, conds.biome())) {
            RuleAdvancementMapping.awardAll(player, def.id(), def.criteriaCount());
        }
    }

    /**
     * Check if the player is in the given biome or biome tag.
     * Supports "#prefix" for biome tags.
     */
    public static boolean isInBiome(ServerPlayer player, String biomeStr) {
        var biomeHolder = player.level().getBiome(player.blockPosition());

        if (biomeStr.startsWith("#")) {
            // Biome tag check
            TagKey<Biome> tagKey = TagKey.create(
                Registries.BIOME, new ResourceLocation(biomeStr.substring(1)));
            var reg = player.level().registryAccess().registry(Registries.BIOME);
            if (reg.isEmpty()) return false;
            return reg.get().getTag(tagKey)
                .map(holderSet -> holderSet.contains(biomeHolder))
                .orElse(false);
        } else {
            // Single biome check
            return biomeHolder.unwrapKey()
                .map(key -> key.location().toString().equals(biomeStr))
                .orElse(false);
        }
    }

    // ===================== Cache =====================

    private static void buildCache() {
        if (cacheBuilt) return;
        AchievementDefinitions.loadOnce();
        if (!AchievementDefinitions.isLoaded()) return;

        statDefs = AchievementDefinitions.getByTrigger(TRIGGER_STAT);
        biomeDefs = AchievementDefinitions.getByTrigger(TRIGGER_BIOME);
        cacheBuilt = true;
    }

    /** Reset cache after config reload — call from reload listener */
    public static void invalidateCache() {
        cacheBuilt = false;
        statDefs = null;
        biomeDefs = null;
    }
}
