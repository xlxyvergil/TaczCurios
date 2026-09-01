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


public final class AchievementDefinitions {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String FILE_NAME = "achievement_definitions.json";
    private static final String DEFAULT_RESOURCE = "/tcc_defaults/achievement_definitions.json";

    private static volatile boolean loaded;
    private static volatile boolean loadAttempted;
    private static final Map<String, AchievementDef> ACHIEVEMENTS = new LinkedHashMap<>();
    private static final Map<String, List<AchievementDef>> BY_TRIGGER = new HashMap<>();

    public static final String TRIGGER_FISH_CAUGHT = "fish_caught";
    public static final String TRIGGER_FISH_FOOD_EATEN = "fish_food_eaten";
    public static final String TRIGGER_ZOMBIE_VILLAGER_CURED = "zombie_villager_cured";
    public static final String TRIGGER_ITEMS_CRAFTED = "items_crafted";
    public static final String TRIGGER_PLAY_TIME = "play_time";

    private AchievementDefinitions() {}

    

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
            return true; 
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

    

    public record AchievementDef(
        String id,
        Display display,
        String parent,
        String trigger,
        @SerializedName("playerKilled") Boolean playerKilled,
        AchievementConditions conditions,
        List<String> prerequisites,
        Reward reward,
        @SerializedName("enabled") Boolean enabled
    ) {
        
        AchievementDef(String explicitId, AchievementDef fromJson) {
            this(
                explicitId,
                fromJson.display,
                fromJson.parent,
                fromJson.trigger,
                fromJson.playerKilled,
                fromJson.conditions,
                fromJson.prerequisites,
                fromJson.reward,
                fromJson.enabled
            );
        }

        
        public boolean isEnabled() { return enabled == null || enabled; }

        public boolean isPlayerKilled() { return playerKilled != null && playerKilled; }
        public ResourceLocation idRL() { return new ResourceLocation(id); }

        
        public int targetCount() {
            if (conditions != null && conditions.kills() != null && !conditions.kills().isEmpty()) {
                int total = 0;
                for (KillCondition kc : conditions.kills()) {
                    total += kc.criteriaCount();
                }
                return total;
            }
            if (conditions != null && (conditions.stat() != null
                    || RaidVictoryEventHandler.TRIGGER_RAID_VICTORY.equals(trigger)
                    || TRIGGER_FISH_CAUGHT.equals(trigger)
                    || TRIGGER_FISH_FOOD_EATEN.equals(trigger)
                    || TRIGGER_ZOMBIE_VILLAGER_CURED.equals(trigger)
                    || TRIGGER_ITEMS_CRAFTED.equals(trigger)
                    || TRIGGER_PLAY_TIME.equals(trigger))) {
                return conditions.criteriaCount();
            }
            return 1;
        }

        
        public boolean isMultiTypeKill() {
            return conditions != null
                    && "and".equals(conditions.mode())
                    && conditions.kills() != null
                    && conditions.kills().size() > 1;
        }

        public String title(String locale) {
            if (display == null || display.title == null) return id;
            String t = display.title.get(locale);
            if (t == null) t = display.title.get("en_us");
            return t != null ? t : id;
        }

        
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
        // --- and/or mode ---
        @SerializedName("mode") String mode,
        // --- stat_polling ---
        String stat,
        @SerializedName("criteria_count") int criteriaCount,
        // --- biome_visit ---
        String biome,
        String dimension,
        
        @SerializedName("extraStats") List<StatCondition> extraStats,
        
        @SerializedName("healthMin") Double healthMin,
        @SerializedName("healthMax") Double healthMax,
        
        @SerializedName("minHeight") Double minHeight
    ) {
        
        public int criteriaCount() { return criteriaCount > 0 ? criteriaCount : 1; }
    }

    public record StatCondition(
        String stat,
        @SerializedName("criteria_count") int criteriaCount
    ) {
        public int criteriaCount() { return criteriaCount > 0 ? criteriaCount : 1; }
    }

    public record KillCondition(
        String entity,
        List<String> nbt,
        @SerializedName("criteria_count") int criteriaCount
    ) {
        
        public int criteriaCount() { return criteriaCount > 0 ? criteriaCount : 1; }
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
    
    public static String entityDisplayName(String entityKey) {
        if (entityKey == null || entityKey.isBlank()) return "?";
        try {
            if (entityKey.startsWith("#")) {
                String mobTypeKey = mobTypeTranslationKey(entityKey.substring(1));
                if (mobTypeKey != null) {
                    return Language.getInstance().getOrDefault(mobTypeKey, entityKey);
                }
                return entityKey;
            }
            ResourceLocation rl = new ResourceLocation(entityKey);
            EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(rl);
            if (type == null) return entityKey;
            String translationKey = type.getDescriptionId();
            return Language.getInstance().getOrDefault(translationKey, entityKey);
        } catch (Exception e) {
            return entityKey;
        }
    }

    
    private static String mobTypeTranslationKey(String name) {
        if (name == null) return null;
        switch (name.toLowerCase()) {
            case "undead":
            case "minecraft:undead":
                return "tcc.mobtype.undead";
            case "arthropod":
            case "minecraft:arthropod":
                return "tcc.mobtype.arthropod";
            case "illager":
            case "minecraft:illager":
                return "tcc.mobtype.illager";
            case "water":
            case "minecraft:water":
                return "tcc.mobtype.water";
            case "undefined":
            case "minecraft:undefined":
                return "tcc.mobtype.undefined";
            default:
                return null;
        }
    }
}
