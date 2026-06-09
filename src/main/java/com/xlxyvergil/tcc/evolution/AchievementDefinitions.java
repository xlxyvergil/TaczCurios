package com.xlxyvergil.tcc.evolution;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;
import com.mojang.logging.LogUtils;
import com.xlxyvergil.tcc.util.CurioGrantHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.locale.Language;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.fml.loading.FMLPaths;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;

/**
 * Loads and manages achievement definitions from achievement_definitions.json.
 * <p>
 * This replaces the hardcoded mapping in RuleAdvancementMapping with a fully
 * JSON-configurable system. Each achievement defines its display (bilingual),
 * trigger conditions, criteria count, prerequisites, and reward (grant/evolve).
 */
public final class AchievementDefinitions {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String FILE_NAME = "achievement_definitions.json";
    private static final String DEFAULT_RESOURCE = "/tcc_defaults/achievement_definitions.json";

    private static volatile boolean loaded;
    private static volatile boolean loadAttempted;
    private static final Map<String, AchievementDef> ACHIEVEMENTS = new LinkedHashMap<>();
    private static final Map<String, List<AchievementDef>> BY_TRIGGER = new HashMap<>();

    private AchievementDefinitions() {}

    // ===== Public API =====

    public static Optional<AchievementDef> get(String achievementId) {
        loadOnce();
        return Optional.ofNullable(ACHIEVEMENTS.get(achievementId));
    }

    public static List<AchievementDef> getByTrigger(String trigger) {
        loadOnce();
        return BY_TRIGGER.getOrDefault(trigger, Collections.emptyList());
    }

    public static Collection<AchievementDef> all() {
        loadOnce();
        return Collections.unmodifiableCollection(ACHIEVEMENTS.values());
    }

    public static int count() {
        loadOnce();
        return ACHIEVEMENTS.size();
    }

    // ===== Loading =====

    public static void loadOnce() {
        if (loadAttempted) return;
        synchronized (AchievementDefinitions.class) {
            if (loadAttempted) return;
            Path file = FMLPaths.CONFIGDIR.get().resolve("tcc").resolve(FILE_NAME);
            try { Files.createDirectories(file.getParent()); } catch (IOException e) {
                LOGGER.error("Failed to create config directory for achievement definitions", e);
                loadAttempted = true;
                return;
            }
            if (!ensureDefaults(file)) {
                LOGGER.error("Failed to ensure default achievement_definitions.json exists");
                loadAttempted = true;
                return;
            }
            if (!readAll(file)) {
                LOGGER.error("Failed to load achievement definitions — no achievements will be processed");
                loadAttempted = true;
                return;
            }
            loaded = true;
            loadAttempted = true;
        }
    }

    public static boolean isLoaded() { return loaded; }

    private static boolean ensureDefaults(Path file) {
        if (Files.exists(file)) return true;
        try (InputStream in = AchievementDefinitions.class.getResourceAsStream(DEFAULT_RESOURCE)) {
            if (in == null) {
                LOGGER.error("Default achievement definitions resource not found: {}", DEFAULT_RESOURCE);
                return false;
            }
            Files.copy(in, file, StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            LOGGER.error("Failed to copy default achievement definitions to {}", file, e);
            return false;
        }
    }

    private static boolean readAll(Path file) {
        ACHIEVEMENTS.clear();
        BY_TRIGGER.clear();
        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            JsonElement el = root.get("achievements");
            if (el == null || !el.isJsonObject()) {
                LOGGER.error("Missing or invalid 'achievements' key in achievement_definitions.json");
                return false;
            }
            for (var entry : el.getAsJsonObject().entrySet()) {
                try {
                    AchievementDef def = GSON.fromJson(entry.getValue(), AchievementDef.class);
                    if (def == null) {
                        LOGGER.warn("Failed to deserialize achievement {}", entry.getKey());
                        continue;
                    }
                    String id = entry.getKey();
                    AchievementDef fixed = new AchievementDef(id, def);
                    ACHIEVEMENTS.put(id, fixed);
                } catch (Exception e) {
                    LOGGER.error("Failed to parse achievement definition '{}'", entry.getKey(), e);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Failed to read achievement_definitions.json", e);
            return false;
        }

        if (ACHIEVEMENTS.isEmpty()) {
            LOGGER.warn("No achievement definitions loaded from {}", file);
            return false;
        }

        for (AchievementDef def : ACHIEVEMENTS.values()) {
            if (def.trigger == null) {
                LOGGER.warn("Achievement '{}' has null trigger — skipping trigger index", def.id());
                continue;
            }
            BY_TRIGGER.computeIfAbsent(def.trigger, k -> new ArrayList<>()).add(def);
        }
        return true;
    }

    // ===== Data Model =====

    public record AchievementDef(
        String id,
        Display display,
        String parent,
        String trigger,
        @SerializedName("playerKilled") Boolean playerKilled,
        @SerializedName("criteria_count") int criteriaCount,
        AchievementConditions conditions,
        List<String> prerequisites,
        Reward reward
    ) {
        /** Construct with explicit id (key from JSON map) */
        AchievementDef(String explicitId, AchievementDef fromJson) {
            this(
                explicitId,
                fromJson.display,
                fromJson.parent,
                fromJson.trigger,
                fromJson.playerKilled,
                fromJson.criteriaCount,
                fromJson.conditions,
                fromJson.prerequisites,
                fromJson.reward
            );
        }

        public boolean isPlayerKilled() { return playerKilled != null && playerKilled; }
        public ResourceLocation idRL() { return new ResourceLocation(id); }

        /** Get display title for a locale */
        public String title(String locale) {
            if (display == null || display.title == null) return id;
            String t = display.title.get(locale);
            if (t == null) t = display.title.get("en_us");
            return t != null ? t : id;
        }

        /** Get display description for a locale, with %d and {entity}/{killer} placeholders filled */
        public String description(String locale, int current, int total) {
            if (display == null || display.description == null) return "";
            String fmt = display.description.get(locale);
            if (fmt == null) fmt = display.description.get("en_us");
            if (fmt == null) return "";

            // Resolve {entity} placeholder
            fmt = resolveEntityPlaceholder(fmt, locale);
            // Resolve {killer} placeholder
            fmt = resolveKillerPlaceholder(fmt, locale);

            return String.format(fmt, current, total);
        }

        private String resolveEntityPlaceholder(String fmt, String locale) {
            if (!fmt.contains("{entity}")) return fmt;
            String name = resolveFirstKillEntityName(locale);
            return fmt.replace("{entity}", name);
        }

        private String resolveKillerPlaceholder(String fmt, String locale) {
            if (!fmt.contains("{killer}")) return fmt;
            String name = conditions != null && conditions.killer != null
                    ? entityDisplayName(conditions.killer)
                    : "?";
            return fmt.replace("{killer}", name);
        }

        private String resolveFirstKillEntityName(String locale) {
            if (conditions == null || conditions.kills == null || conditions.kills.isEmpty())
                return "?";
            String entityKey = conditions.kills.get(0).entity();
            if ("*".equals(entityKey)) {
                return "zh_cn".equals(locale) ? "任意实体" : "any entity";
            }
            return entityDisplayName(entityKey);
        }
    }

    public record Display(
        Map<String, String> title,
        Map<String, String> description,
        String icon,
        String frame,
        boolean hidden
    ) {}

    public record AchievementConditions(
        List<KillCondition> kills,
        List<String> equippedCurios,
        List<String> requiredEffects,
        List<String> holdingGunTypes,
        @SerializedName("minDistance") Double minDistance,
        List<AttributeCondition> attributes,
        String killer
    ) {}

    public record KillCondition(
        String entity
    ) {}

    public record AttributeCondition(
        String attribute,
        String comparator,
        double value
    ) {}

    public record Reward(
        String type,
        String item,
        String to,
        @SerializedName("overflowMode") String overflowMode,
        @SerializedName("bindToPlayer") Boolean bindToPlayer,
        @SerializedName("linkedEvolves") List<LinkedEvolveRef> linkedEvolves,
        @SerializedName("autoAchievements") List<String> autoAchievements
    ) {
        public boolean isGrant() { return "grant".equals(type); }
        public boolean isEvolve() { return "evolve".equals(type); }
        public boolean shouldBind() { return bindToPlayer != null && bindToPlayer; }
        public CurioGrantHelper.OverflowMode getOverflow() {
            if (overflowMode == null) return CurioGrantHelper.OverflowMode.INVENTORY_THEN_DROP;
            try { return CurioGrantHelper.OverflowMode.valueOf(overflowMode.toUpperCase(Locale.ROOT)); }
            catch (Exception e) { return CurioGrantHelper.OverflowMode.INVENTORY_THEN_DROP; }
        }
    }

    public record LinkedEvolveRef(
        String item,
        String to
    ) {}

    // ===== Entity name resolution =====

    /**
     * Resolve an entity registry key to its localized display name.
     * Uses Minecraft's built-in translation system so it respects the current Language instance.
     */
    public static String entityDisplayName(String entityKey) {
        if (entityKey == null || entityKey.isBlank()) return "?";
        try {
            ResourceLocation rl = new ResourceLocation(entityKey);
            EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(rl);
            if (type == null) return entityKey;
            String translationKey = type.getDescriptionId();
            return Language.getInstance().getOrDefault(translationKey, entityKey);
        } catch (Exception e) {
            return entityKey;
        }
    }
}
