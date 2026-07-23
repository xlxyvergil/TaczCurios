package com.xlxyvergil.tcc.evolution;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.network.NetworkHandler;
import com.xlxyvergil.tcc.util.BaseCurioItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import java.util.List;
import java.util.function.Predicate;

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
    private static final String APPLIED_NBT_PREFIX = "StatEvoApplied_";

    // Cached after first access (lists don't change at runtime)
    private static List<AchievementDefinitions.AchievementDef> statDefs;
    private static List<AchievementDefinitions.AchievementDef> biomeDefs;
    private static List<EvolutionRegistry.Rule> statAttrRules;
    private static List<EvolutionRegistry.Rule> biomeAttrRules;
    private static boolean cacheBuilt;

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.player instanceof ServerPlayer player)) return;
        if (player.isSpectator()) return;

        buildCache();

        long t = player.level().getGameTime();

        // stat_polling: every 3 ticks
        if (t % 3 == 0) {
            if (statDefs != null) {
                for (var def : statDefs) {
                    checkStat(player, def);
                }
            }
            if (statAttrRules != null) {
                for (var rule : statAttrRules) {
                    checkStatAttribute(player, rule);
                }
            }
        }

        // biome_visit: every 20 ticks
        if (t % 20 == 0) {
            // 记录当前维度/群系到玩家 NBT（通用，与成就/规则解耦）
            recordCurrentBiome(player);

            if (biomeDefs != null) {
                for (var def : biomeDefs) {
                    checkBiome(player, def);
                }
            }
            if (biomeAttrRules != null) {
                for (var rule : biomeAttrRules) {
                    checkBiomeAttribute(player, rule);
                }
            }
        }
    }

    // ===================== stat_polling =====================

    private static void checkStat(ServerPlayer player, AchievementDefinitions.AchievementDef def) {
        if (!def.isEnabled()) return;
        if (RuleAdvancementMapping.isAdvancementDone(player, def.id())) return;
        if (!RuleAdvancementMapping.arePrerequisitesMet(player, def)) return;

        if (!AchievementConditionMatcher.matchesStatBiomeConditions(player, def)) return;

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
        int criteriaCount = def.criteriaCount();

        if (current >= criteriaCount) {
            // Stat value meets or exceeds criteria_count, complete the achievement
            RuleAdvancementMapping.awardAll(player, def.id(), criteriaCount);
        }
    }

    // ===================== biome_visit =====================

    private static final String VISITED_DIMENSIONS_KEY = "tcc_visited_dimensions";
    private static final String VISITED_BIOMES_KEY = "tcc_visited_biomes";

    /**
     * 将玩家当前所在的维度和群系记录到玩家 NBT 列表中。
     * 每 20 tick 调用一次，写入 tcc_visited_dimensions / tcc_visited_biomes。
     */
    private static void recordCurrentBiome(ServerPlayer player) {
        ResourceLocation dimId = player.level().dimension().location();
        recordVisit(player, VISITED_DIMENSIONS_KEY, dimId.toString());

        var biomeHolder = player.level().getBiome(player.blockPosition());
        biomeHolder.unwrapKey().ifPresent(key ->
                recordVisit(player, VISITED_BIOMES_KEY, key.location().toString()));
    }

    /**
     * Check if the target biome/dimension is in the player's visited NBT list.
     * 对 biome tag（#前缀）无法通过 NBT 判断，回退到实时检测。
     */
    private static void checkBiome(ServerPlayer player, AchievementDefinitions.AchievementDef def) {
        if (!def.isEnabled()) return;
        if (RuleAdvancementMapping.isAdvancementDone(player, def.id())) return;
        if (!RuleAdvancementMapping.arePrerequisitesMet(player, def)) return;

        if (!AchievementConditionMatcher.matchesStatBiomeConditions(player, def)) return;

        AchievementDefinitions.AchievementConditions conds = def.conditions();
        if (conds == null) return;

        boolean matched = false;
        if (conds.biome() != null) {
            matched = isInNbtList(player, VISITED_BIOMES_KEY, conds.biome());
        }
        // Dimension-only: no biome field but has dimension
        if (!matched && conds.biome() == null && conds.dimension() != null) {
            matched = isInNbtList(player, VISITED_DIMENSIONS_KEY, conds.dimension());
        }

        if (matched) {
            RuleAdvancementMapping.awardAll(player, def.id(), def.criteriaCount());
        }
    }

    /**
     * 将访问记录写入玩家 NBT 列表，避免重复。
     */
    private static void recordVisit(ServerPlayer player, String nbtKey, String id) {
        CompoundTag data = player.getPersistentData();
        net.minecraft.nbt.ListTag list;
        if (data.contains(nbtKey, net.minecraft.nbt.Tag.TAG_LIST)) {
            list = data.getList(nbtKey, net.minecraft.nbt.Tag.TAG_STRING);
            for (net.minecraft.nbt.Tag tag : list) {
                if (tag.getAsString().equals(id)) return; // 已记录
            }
        } else {
            list = new net.minecraft.nbt.ListTag();
        }
        list.add(net.minecraft.nbt.StringTag.valueOf(id));
        data.put(nbtKey, list);

        // 同步到客户端
        NetworkHandler.syncVisited(player, nbtKey, id);
    }

    private static boolean isInNbtList(ServerPlayer player, String nbtKey, String target) {
        CompoundTag data = player.getPersistentData();
        if (!data.contains(nbtKey, net.minecraft.nbt.Tag.TAG_LIST)) return false;
        var list = data.getList(nbtKey, net.minecraft.nbt.Tag.TAG_STRING);
        for (var tag : list) {
            if (tag.getAsString().equals(target)) return true;
        }
        return false;
    }

    // ===================== ATTRIBUTE rules (evolution_rules.json) =====================

    /**
     * Handle stat_polling ATTRIBUTE rule: step-based accumulation.
     * <p>
     * {@code statThreshold} acts as the interval between steps (e.g., 48000 ticks = 2 in-game days).
     * Each step adds {@code value} to progress NBT, up to {@code progress.cap}.
     * <p>
     * Per-rule cap is tracked via {@code progress.capCounterKey} (unique per rule),
     * while the shared {@code progress.nbtKey} accumulates total from all rules (inheritance support).
     * Step count is stored as int in {@code StatEvoSteps_<ruleId>} NBT key.
     */
    private static void checkStatAttribute(ServerPlayer player, EvolutionRegistry.Rule rule) {
        if (!rule.enabled) return;
        if (rule.playerKilled) return;
        if (rule.type != EvolutionRegistry.RuleType.ATTRIBUTE) return;
        if (rule.stat == null || rule.statThreshold <= 0) return;
        if (rule.item == null || rule.progress == null) return;

        ResourceLocation statId = ResourceLocation.tryParse(rule.stat);
        if (statId == null) return;

        ResourceLocation registered = BuiltInRegistries.CUSTOM_STAT.get(statId);
        if (registered == null) {
            registered = BuiltInRegistries.CUSTOM_STAT.get(new ResourceLocation(statId.getPath()));
        }
        if (registered == null) return;

        int current = player.getStats().getValue(Stats.CUSTOM.get(registered));
        if (current < rule.statThreshold) return;

        if (!LivingDeathEventHandler.passesExtraRequirements(player, null, rule.requirements)) return;

        ItemStack tracked = findFirstEquippedStack(player, stack -> rule.item.equals(itemId(stack)));
        if (tracked.isEmpty()) return;

        CompoundTag tag = tracked.getOrCreateTag();

        // Already capped for this rule?
        double perRuleCap = rule.progress.cap > 0 ? rule.progress.cap : Double.MAX_VALUE;
        if (tag.getDouble(rule.progress.capCounterKey) >= perRuleCap) return;

        // Available steps from current stat value
        int availableSteps = current / rule.statThreshold;
        String stepKey = APPLIED_NBT_PREFIX + "Steps_" + rule.ruleId.replace(':', '_');
        int appliedSteps = tag.getInt(stepKey);
        if (availableSteps <= appliedSteps) return;

        double valuePerStep = rule.statValue > 0 ? rule.statValue : 1.0;
        double remaining = perRuleCap - tag.getDouble(rule.progress.capCounterKey);
        int stepsToAdd = Math.min(availableSteps - appliedSteps, (int) (remaining / valuePerStep));
        if (stepsToAdd <= 0) return;

        double totalToAdd = stepsToAdd * valuePerStep;
        tag.putDouble(rule.progress.nbtKey, tag.getDouble(rule.progress.nbtKey) + totalToAdd);
        tag.putDouble(rule.progress.capCounterKey,
                tag.getDouble(rule.progress.capCounterKey) + totalToAdd);
        tag.putInt(stepKey, appliedSteps + stepsToAdd);

        if (tracked.getItem() instanceof BaseCurioItem curio) {
            curio.refreshEffects(player);
        }
    }

    /**
     * Handle biome_visit ATTRIBUTE rule: when player enters the target biome,
     * grant a one-time progress increment to the tracked curio.
     */
    private static void checkBiomeAttribute(ServerPlayer player, EvolutionRegistry.Rule rule) {
        if (!rule.enabled) return;
        if (rule.playerKilled) return;
        if (rule.type != EvolutionRegistry.RuleType.ATTRIBUTE) return;
        if (rule.biome == null) return;
        if (rule.item == null || rule.progress == null) return;

        if (!isInNbtList(player, VISITED_BIOMES_KEY, rule.biome)) return;

        if (!LivingDeathEventHandler.passesExtraRequirements(player, null, rule.requirements)) return;

        ItemStack tracked = findFirstEquippedStack(player, stack -> rule.item.equals(itemId(stack)));
        if (tracked.isEmpty()) return;

        String appliedKey = APPLIED_NBT_PREFIX + rule.ruleId.replace(':', '_');
        CompoundTag tag = tracked.getOrCreateTag();
        if (tag.getBoolean(appliedKey)) return;

        double value = rule.statValue > 0 ? rule.statValue : 1.0;
        double oldProgress = tag.getDouble(rule.progress.nbtKey);
        double newProgress = Math.min(oldProgress + value, rule.progress.cap);
        tag.putDouble(rule.progress.nbtKey, newProgress);
        tag.putDouble(rule.progress.capCounterKey,
                tag.getDouble(rule.progress.capCounterKey) + value);
        tag.putBoolean(appliedKey, true);

        if (tracked.getItem() instanceof BaseCurioItem curio) {
            curio.refreshEffects(player);
        }
    }

    // ===================== Helpers =====================

    private static ItemStack findFirstEquippedStack(Player player, Predicate<ItemStack> predicate) {
        if (player == null) return ItemStack.EMPTY;
        ICuriosItemHandler inv = CuriosApi.getCuriosInventory(player).orElse(null);
        if (inv == null) return ItemStack.EMPTY;
        for (var entry : inv.getCurios().entrySet()) {
            ICurioStacksHandler stacksHandler = entry.getValue();
            if (stacksHandler == null) continue;
            var handler = stacksHandler.getStacks();
            for (int i = 0; i < handler.getSlots(); i++) {
                ItemStack stack = handler.getStackInSlot(i);
                if (!stack.isEmpty() && predicate.test(stack)) return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    private static String itemId(ItemStack stack) {
        if (stack.isEmpty()) return "";
        ResourceLocation key = ForgeRegistries.ITEMS.getKey(stack.getItem());
        return key != null ? key.toString() : "";
    }

    // ===================== Cache =====================

    private static void buildCache() {
        if (cacheBuilt) return;
        AchievementDefinitions.loadOnce();
        if (!AchievementDefinitions.isLoaded()) return;

        statDefs = AchievementDefinitions.getByTrigger(TRIGGER_STAT);
        biomeDefs = AchievementDefinitions.getByTrigger(TRIGGER_BIOME);

        // Also cache ATTRIBUTE rules with stat_polling / biome_visit triggers
        var allAttrRules = EvolutionRegistry.getRulesByType(EvolutionRegistry.RuleType.ATTRIBUTE);
        statAttrRules = allAttrRules.stream()
                .filter(r -> TRIGGER_STAT.equals(r.trigger))
                .toList();
        biomeAttrRules = allAttrRules.stream()
                .filter(r -> TRIGGER_BIOME.equals(r.trigger))
                .toList();

        cacheBuilt = true;
    }

    /** Reset cache after config reload — call from reload listener */
    public static void invalidateCache() {
        cacheBuilt = false;
        statDefs = null;
        biomeDefs = null;
        statAttrRules = null;
        biomeAttrRules = null;
    }
}
