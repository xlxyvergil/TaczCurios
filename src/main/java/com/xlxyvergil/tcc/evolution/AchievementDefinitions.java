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
 * 从 achievement_definitions.json 加载并管理成就定义。
 * 每个定义含显示文案（双语）、触发条件、进度、前置条件与奖励（发放/进化），以完全可配置的 JSON 取代硬编码映射。
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

    // 公共 API

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

    // 加载

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
     * 合并默认定义到已有配置：仅追加新 key，保留用户修改与排序，mod 新增成就自动追加。
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
                    // 跳过格式错误的条目
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

    // 数据模型

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
        /** 使用 JSON map 的 key 作为显式 id 构造。 */
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

        /** 该成就是否启用（默认 true）。 */
        public boolean isEnabled() { return enabled == null || enabled; }

        public boolean isPlayerKilled() { return playerKilled != null && playerKilled; }
        public ResourceLocation idRL() { return new ResourceLocation(id); }

        /**
         * 目标计数：击杀类为各击杀条件计数之和（AND 多类型另用于显示）；stat_polling / raid_victory 为条件计数（默认 1）；其余为 1。
         */
        public int targetCount() {
            if (conditions != null && conditions.kills() != null && !conditions.kills().isEmpty()) {
                int total = 0;
                for (KillCondition kc : conditions.kills()) {
                    total += kc.criteriaCount();
                }
                return total;
            }
            if (conditions != null && (conditions.stat() != null || RaidVictoryEventHandler.TRIGGER_RAID_VICTORY.equals(trigger))) {
                return conditions.criteriaCount();
            }
            return 1;
        }

        /** 是否为 AND 多类型击杀：conditions.mode == "and" 且 kills.size > 1 */
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

        /** 获取指定语言下的成就描述，%d 由调用方以（当前, 总数）填充。 */
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
        // --- 额外统计（击杀/死亡成就需要多项统计检查时用） ---
        @SerializedName("extraStats") List<StatCondition> extraStats,
        // --- 生命值区间（当前 HP 检查，如 healthMin: 0, healthMax: 4） ---
        @SerializedName("healthMin") Double healthMin,
        @SerializedName("healthMax") Double healthMax,
        // --- Y 坐标下限（击杀者与被击杀者都需高于该值） ---
        @SerializedName("minHeight") Double minHeight
    ) {
        /** 统计目标计数，若 JSON 未设置则默认 1 */
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
        /** 该击杀条件要求的总计数（默认 1）。 */
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
    /**
     * 解析实体注册键为本地化显示名（走原版翻译系统以尊重当前语言实例）。
     * 以 # 开头且对应原版 MobType 的键（如 #minecraft:undead）显示为本地化名称，其余 # 标签按原样显示。
     */
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

    /**
     * 将名称映射为原版 MobType 的本地化翻译键（大小写不敏感）；不认识返回 null，由调用方按普通 # 标签显示。
     */
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
