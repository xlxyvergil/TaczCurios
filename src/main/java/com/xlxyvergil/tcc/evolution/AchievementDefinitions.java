package com.xlxyvergil.tcc.evolution;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;
import com.xlxyvergil.tcc.util.CurioGrantHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.locale.Language;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.fml.loading.FMLPaths;
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
                loadAttempted = true;
                return;
            }
            if (!ensureDefaults(file)) {
                loadAttempted = true;
                return;
            }
            if (!readAll(file)) {
                loadAttempted = true;
                return;
            }
            loaded = true;
            loadAttempted = true;
        }
    }

    public static boolean isLoaded() { return loaded; }

    private static boolean ensureDefaults(Path file) {
        if (Files.exists(file)) {
            return mergeDefaults(file);
        }
        return copyDefaults(file);
    }

    private static boolean copyDefaults(Path file) {
        try (InputStream in = AchievementDefinitions.class.getResourceAsStream(DEFAULT_RESOURCE)) {
            if (in == null) {
                return false;
            }
            Files.copy(in, file, StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 合并默认成就定义到已有配置文件：仅追加新 key，不修改已存在的条目。
     * 用户自定义的修改和排序得到保留，mod 更新带来的新成就自动追加。
     */
    private static boolean mergeDefaults(Path file) {
        try {
            JsonObject defaultRoot;
            try (InputStream in = AchievementDefinitions.class.getResourceAsStream(DEFAULT_RESOURCE)) {
                if (in == null) {
                    return false;
                }
                defaultRoot = JsonParser.parseReader(new java.io.InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject();
            }

            String userJson = Files.readString(file, StandardCharsets.UTF_8);
            JsonObject userRoot = JsonParser.parseString(userJson).getAsJsonObject();

            JsonElement defaultAchievements = defaultRoot.get("achievements");
            if (defaultAchievements == null || !defaultAchievements.isJsonObject()) return true;

            JsonObject userAchievements = userRoot.getAsJsonObject("achievements");
            if (userAchievements == null) {
                userAchievements = new JsonObject();
                userRoot.add("achievements", userAchievements);
            }

            boolean added = false;
            for (var entry : defaultAchievements.getAsJsonObject().entrySet()) {
                if (!userAchievements.has(entry.getKey())) {
                    userAchievements.add(entry.getKey(), entry.getValue());
                    added = true;
                }
            }

            if (added) {
                Files.writeString(file, GSON.toJson(userRoot), StandardCharsets.UTF_8);
            }
            return true;
        } catch (IOException e) {
            return true; // 已有文件仍然可用
        }
    }

    private static boolean readAll(Path file) {
        ACHIEVEMENTS.clear();
        BY_TRIGGER.clear();
        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            JsonElement el = root.get("achievements");
            if (el == null || !el.isJsonObject()) {
                return false;
            }
            for (var entry : el.getAsJsonObject().entrySet()) {
                try {
                    AchievementDef def = GSON.fromJson(entry.getValue(), AchievementDef.class);
                    if (def == null) {
                        continue;
                    }
                    String id = entry.getKey();
                    AchievementDef fixed = new AchievementDef(id, def);
                    ACHIEVEMENTS.put(id, fixed);
                } catch (Exception e) {
                    // skip malformed entry
                }
            }
        } catch (Exception e) {
            return false;
        }

        if (ACHIEVEMENTS.isEmpty()) {
            return false;
        }

        for (AchievementDef def : ACHIEVEMENTS.values()) {
            if (def.trigger == null) {
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
        Reward reward,
        @SerializedName("enabled") Boolean enabled
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
                fromJson.reward,
                fromJson.enabled
            );
        }

        /** @return 该成就是否启用，默认为 true */
        public boolean isEnabled() { return enabled == null || enabled; }

        public boolean isPlayerKilled() { return playerKilled != null && playerKilled; }
        public ResourceLocation idRL() { return new ResourceLocation(id); }

        /** Get display title for a locale */
        public String title(String locale) {
            if (display == null || display.title == null) return id;
            String t = display.title.get(locale);
            if (t == null) t = display.title.get("en_us");
            return t != null ? t : id;
        }

        /** Get display description for a locale, from display.description map.
         *  The %d placeholders are filled by caller with (current, total). */
        public String description(String locale, int current, int total) {
            if (display == null || display.description == null) return id;
            String fmt = display.description.get(locale);
            if (fmt == null) fmt = display.description.get("en_us");
            if (fmt == null) return id;
            return String.format(fmt, current, total);
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
        String killer,
        // --- stat_polling ---
        String stat,
        @SerializedName("statThreshold") int statThreshold,
        // --- biome_visit ---
        String biome,
        String dimension,
        // --- extra stats (for kill/death achievements that require multiple stat checks) ---
        @SerializedName("extraStats") List<StatCondition> extraStats,
        // --- health range (current HP check, e.g. healthMin: 0, healthMax: 4) ---
        @SerializedName("healthMin") Double healthMin,
        @SerializedName("healthMax") Double healthMax
    ) {
        /** stat 阈值，若 JSON 未设置则默认 1 */
        public int statThreshold() { return statThreshold > 0 ? statThreshold : 1; }
    }

    public record StatCondition(
        String stat,
        @SerializedName("statThreshold") int statThreshold
    ) {
        public int statThreshold() { return statThreshold > 0 ? statThreshold : 1; }
    }

    public record KillCondition(
        String entity,
        List<String> nbt,
        int value
    ) {
        /** @return 该击杀条件贡献的进度步骤数，默认为 1 */
        public int value() { return value > 0 ? value : 1; }
    }

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
