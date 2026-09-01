package com.xlxyvergil.tcc.evolution;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.capability.TccPlayerDataCapability;
import com.xlxyvergil.tcc.network.NetworkHandler;
import com.xlxyvergil.tcc.items.BaseCurioItem;
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


@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class StatPollingEventHandler {

    private static final String TRIGGER_STAT = "stat_polling";
    private static final String TRIGGER_BIOME = "biome_visit";
    private static final String APPLIED_NBT_PREFIX = "StatEvoApplied_";

    
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

        
        if (t % 20 == 0) {
            
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

    // stat_polling

    private static void checkStat(ServerPlayer player, AchievementDefinitions.AchievementDef def) {
        if (!def.isEnabled()) return;
        if (RuleAdvancementMapping.isAdvancementDone(player, def.id())) return;
        if (!RuleAdvancementMapping.arePrerequisitesMet(player, def)) return;

        if (!AchievementConditionMatcher.matchesStatBiomeConditions(player, def)) return;

        AchievementDefinitions.AchievementConditions conds = def.conditions();
        if (conds == null || conds.stat() == null) return;

        ResourceLocation statId = ResourceLocation.tryParse(conds.stat());
        if (statId == null) return;

        int current = readStatValue(player, statId);
        int target = def.targetCount();

        if (current >= target) {
            RuleAdvancementMapping.awardAll(player, def.id(), target);
        }
    }

    // biome_visit

    private static final String VISITED_DIMENSIONS_KEY = "tcc_visited_dimensions";
    private static final String VISITED_BIOMES_KEY = "tcc_visited_biomes";

    
    private static void recordCurrentBiome(ServerPlayer player) {
        ResourceLocation dimId = player.level().dimension().location();
        recordVisit(player, VISITED_DIMENSIONS_KEY, dimId.toString());

        var biomeHolder = player.level().getBiome(player.blockPosition());
        biomeHolder.unwrapKey().ifPresent(key ->
                recordVisit(player, VISITED_BIOMES_KEY, key.location().toString()));
    }

    
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
        
        if (!matched && conds.biome() == null && conds.dimension() != null) {
            matched = isInNbtList(player, VISITED_DIMENSIONS_KEY, conds.dimension());
        }

        if (matched) {
            RuleAdvancementMapping.awardAll(player, def.id(), def.targetCount());
        }
    }

    
    private static void recordVisit(ServerPlayer player, String nbtKey, String id) {
        boolean added;
        if (VISITED_BIOMES_KEY.equals(nbtKey)) {
            added = TccPlayerDataCapability.addVisitedBiome(player, id);
        } else if (VISITED_DIMENSIONS_KEY.equals(nbtKey)) {
            added = TccPlayerDataCapability.addVisitedDimension(player, id);
        } else {
            return;
        }
        if (added) {
            
            NetworkHandler.syncVisited(player, nbtKey, id);
        }
    }

    private static boolean isInNbtList(ServerPlayer player, String nbtKey, String target) {
        if (VISITED_BIOMES_KEY.equals(nbtKey)) {
            return TccPlayerDataCapability.hasVisitedBiome(player, target);
        } else if (VISITED_DIMENSIONS_KEY.equals(nbtKey)) {
            return TccPlayerDataCapability.hasVisitedDimension(player, target);
        }
        return false;
    }

    

    
    private static void checkStatAttribute(ServerPlayer player, EvolutionRegistry.Rule rule) {
        if (!rule.enabled) return;
        if (rule.playerKilled) return;
        if (rule.type != EvolutionRegistry.RuleType.ATTRIBUTE) return;
        if (rule.stat == null || rule.statThreshold <= 0) return;
        if (rule.item == null || rule.progress == null) return;

        ResourceLocation statId = ResourceLocation.tryParse(rule.stat);
        if (statId == null) return;

        int current = readStatValue(player, statId);
        if (current < rule.statThreshold) return;

        if (!LivingDeathEventHandler.passesExtraRequirements(player, null, rule.requirements)) return;

        ItemStack tracked = findFirstEquippedStack(player, stack -> rule.item.equals(itemId(stack)));
        if (tracked.isEmpty()) return;

        CompoundTag tag = tracked.getOrCreateTag();

        
        double perRuleCap = rule.progress.cap > 0 ? rule.progress.cap : Double.MAX_VALUE;
        if (tag.getDouble(rule.progress.capCounterKey) >= perRuleCap) return;

        
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
            curio.refreshEffects(player, tracked);
        }
    }

    
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
            curio.refreshEffects(player, tracked);
        }
    }

    

    
    private static int readStatValue(ServerPlayer player, ResourceLocation statId) {
        if (TaczCurios.MODID.equals(statId.getNamespace())) {
            return TccPlayerDataCapability.getCustomStat(player, statId.toString());
        }
        ResourceLocation registered = BuiltInRegistries.CUSTOM_STAT.get(statId);
        if (registered == null) {
            registered = BuiltInRegistries.CUSTOM_STAT.get(new ResourceLocation(statId.getPath()));
        }
        if (registered == null) return 0;
        return player.getStats().getValue(Stats.CUSTOM.get(registered));
    }

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

    // Cache

    private static void buildCache() {
        if (cacheBuilt) return;
        AchievementDefinitions.loadOnce();
        if (!AchievementDefinitions.isLoaded()) return;

        statDefs = AchievementDefinitions.getByTrigger(TRIGGER_STAT);
        biomeDefs = AchievementDefinitions.getByTrigger(TRIGGER_BIOME);

        
        var allAttrRules = EvolutionRegistry.getRulesByType(EvolutionRegistry.RuleType.ATTRIBUTE);
        statAttrRules = allAttrRules.stream()
                .filter(r -> TRIGGER_STAT.equals(r.trigger))
                .toList();
        biomeAttrRules = allAttrRules.stream()
                .filter(r -> TRIGGER_BIOME.equals(r.trigger))
                .toList();

        cacheBuilt = true;
    }

    
    public static void invalidateCache() {
        cacheBuilt = false;
        statDefs = null;
        biomeDefs = null;
        statAttrRules = null;
        biomeAttrRules = null;
    }
}
