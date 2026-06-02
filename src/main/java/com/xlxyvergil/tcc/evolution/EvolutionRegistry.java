package com.xlxyvergil.tcc.evolution;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.xlxyvergil.tcc.util.CurioGrantHelper;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.InputStream;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class EvolutionRegistry {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String DIR_NAME = "tcc";
    private static final String FILE_NAME = "evolution_rules.json";
    private static final String DEFAULT_RESOURCE = "/tcc_defaults/evolution_rules.json";

    private static volatile boolean loaded;
    private static final Map<String, Rule> RULES = new HashMap<>();
    private static final Map<String, List<Rule>> RULES_BY_TRIGGER = new HashMap<>();

    private EvolutionRegistry() {
    }

    public static String ruleId(String fromItemId, String toItemId) {
        return sanitizeId(fromItemId) + "_to_" + sanitizeId(toItemId);
    }

    private static String sanitizeId(String id) {
        return id.replace(':', '_');
    }

    public static void loadOnce() {
        if (loaded) {
            return;
        }
        synchronized (EvolutionRegistry.class) {
            if (loaded) {
                return;
            }
            Path file = FMLPaths.CONFIGDIR.get().resolve(DIR_NAME).resolve(FILE_NAME);
            try {
                Files.createDirectories(file.getParent());
            } catch (IOException e) {
                loaded = true;
                return;
            }

            ensureDefaults(file);
            readAll(file);
            loaded = true;
        }
    }

    public static Optional<Rule> getRule(String ruleId) {
        loadOnce();
        return Optional.ofNullable(RULES.get(ruleId));
    }

    public static List<Rule> getAllRules() {
        loadOnce();
        return List.copyOf(RULES.values());
    }

    public static List<Rule> getRulesByTriggerOrEmpty(String trigger) {
        loadOnce();
        return RULES_BY_TRIGGER.getOrDefault(trigger, Collections.emptyList());
    }

    public static List<Rule> getRulesByTypeAndItemOrEmpty(RuleType type, String itemId) {
        loadOnce();
        if (itemId == null || itemId.isBlank()) {
            return Collections.emptyList();
        }
        List<Rule> out = new ArrayList<>();
        for (Rule rule : RULES.values()) {
            if (rule.type == type && itemId.equals(rule.item)) {
                out.add(rule);
            }
        }
        return List.copyOf(out);
    }

    public static List<KillRequirement> getKillRequirementsOrEmpty(String ruleId) {
        loadOnce();
        return getRule(ruleId).map(r -> r.requirements.kills).orElse(Collections.emptyList());
    }

    public static Optional<Double> getAttributeRequirementValue(String ruleId, String attributeId, String comparator) {
        loadOnce();
        for (AttributeRequirement req : getRule(ruleId).map(r -> r.requirements.attributes).orElse(Collections.emptyList())) {
            if (!attributeId.equals(req.attribute)) {
                continue;
            }
            if (comparator.equals(req.comparator)) {
                return Optional.of(req.value);
            }
        }
        return Optional.empty();
    }

    private static void readAll(Path file) {
        RULES.clear();
        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            JsonElement rulesEl = root.get("rules");
            if (rulesEl != null && rulesEl.isJsonObject()) {
                JsonObject obj = rulesEl.getAsJsonObject();
                for (var entry : obj.entrySet()) {
                    String ruleId = entry.getKey();
                    RuleJson json = GSON.fromJson(entry.getValue(), RuleJson.class);
                    Rule rule = toRule(ruleId, json);
                    if (rule != null) {
                        RULES.put(rule.ruleId, rule);
                    }
                }
            } else if (rulesEl != null && rulesEl.isJsonArray()) {
                for (JsonElement el : rulesEl.getAsJsonArray()) {
                    RuleJson json = GSON.fromJson(el, RuleJson.class);
                    if (json == null || json.ruleId == null || json.ruleId.isBlank()) {
                        continue;
                    }
                    Rule rule = toRule(json.ruleId, json);
                    if (rule != null) {
                        RULES.put(rule.ruleId, rule);
                    }
                }
            }
        } catch (Exception ignored) {
        }

        RULES_BY_TRIGGER.clear();
        for (Rule rule : RULES.values()) {
            if (rule.trigger == null || rule.trigger.isBlank()) {
                continue;
            }
            RULES_BY_TRIGGER.computeIfAbsent(rule.trigger, k -> new ArrayList<>()).add(rule);
        }
        for (var entry : RULES_BY_TRIGGER.entrySet()) {
            entry.setValue(List.copyOf(entry.getValue()));
        }
    }

    private static void ensureDefaults(Path file) {
        copyDefaultIfMissing(file);
    }

    private static void copyDefaultIfMissing(Path file) {
        if (Files.exists(file)) {
            return;
        }
        try (InputStream in = EvolutionRegistry.class.getResourceAsStream(DEFAULT_RESOURCE)) {
            if (in == null) {
                return;
            }
            Files.copy(in, file, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ignored) {
        }
    }

    private static Rule toRule(String ruleId, RuleJson json) {
        if (json == null || ruleId == null || ruleId.isBlank()) {
            return null;
        }
        RuleType type = parseRuleType(json.type);
        if (type == RuleType.EVOLVE && (json.item == null || json.to == null)) {
            return null;
        }
        if (type == RuleType.ATTRIBUTE && (json.item == null || json.progress == null)) {
            return null;
        }
        boolean enabled = json.enabled == null || json.enabled;
        String trigger = normalize(json.trigger);
        boolean playerKilled = json.playerKilled != null && json.playerKilled;
        String item = normalize(json.item);
        String to = normalize(json.to);
        Requirements req = toRequirements(json.requirements);
        Progress progress = toProgress(json.progress);
        List<KillGain> killGains = toKillGains(json.kills);
        Grant grant = toGrant(json.grant);
        EntityRef killer = toEntityRef(json.killer);
        List<LinkedEvolve> linked = toLinkedEvolves(json.requirementsRef);
        List<String> damageSourceTags = toStringList(json.damageSourceTags);
        List<String> excludeKeys = json.excludeNbtKeys == null ? Collections.emptyList() : List.copyOf(json.excludeNbtKeys);
        return new Rule(ruleId, type, enabled, trigger, playerKilled, item, to, req, killGains, progress, linked, grant, killer, damageSourceTags, excludeKeys);
    }

    private static List<String> toStringList(List<String> json) {
        if (json == null || json.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> out = new ArrayList<>();
        for (String s : json) {
            String v = normalize(s);
            if (v != null) {
                out.add(v);
            }
        }
        return List.copyOf(out);
    }

    private static Requirements toRequirements(RequirementsJson json) {
        if (json == null) {
            return new Requirements(Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
        }
        List<KillRequirement> kills = new ArrayList<>();
        if (json.kills != null) {
            for (KillRequirementJson k : json.kills) {
                if (k == null || k.entity == null || k.count <= 0) {
                    continue;
                }
                EntityRef entity = toEntityRef(k.entity);
                if (entity == null) {
                    continue;
                }
                kills.add(new KillRequirement(entity, k.count));
            }
        }
        List<AttributeRequirement> attrs = new ArrayList<>();
        if (json.attributes != null) {
            for (AttributeRequirementJson a : json.attributes) {
                if (a == null || a.attribute == null || a.comparator == null) {
                    continue;
                }
                attrs.add(new AttributeRequirement(a.attribute, normalizeComparator(a.comparator), a.value));
            }
        }
        List<String> equippedCurios = new ArrayList<>();
        if (json.equippedCurios != null) {
            for (String id : json.equippedCurios) {
                String v = normalize(id);
                if (v != null) {
                    equippedCurios.add(v);
                }
            }
        }
        return new Requirements(List.copyOf(kills), List.copyOf(attrs), List.copyOf(equippedCurios));
    }

    private static List<KillGain> toKillGains(List<KillGainJson> json) {
        if (json == null || json.isEmpty()) {
            return Collections.emptyList();
        }
        List<KillGain> out = new ArrayList<>();
        for (KillGainJson k : json) {
            if (k == null || k.entity == null) {
                continue;
            }
            EntityRef entity = toEntityRef(k.entity);
            if (entity == null) {
                continue;
            }
            out.add(new KillGain(entity, k.value));
        }
        return List.copyOf(out);
    }

    private static Progress toProgress(ProgressJson json) {
        if (json == null || json.attribute == null || json.operation == null) {
            return null;
        }
        String attribute = normalize(json.attribute);
        if (attribute == null) {
            return null;
        }
        String nbtKey = normalize(json.nbtKey);
        if (nbtKey == null) {
            nbtKey = progressKeyFromAttribute(attribute);
        }
        String capCounterKey = normalize(json.capCounterKey);
        if (capCounterKey == null) {
            capCounterKey = capCounterKeyFromAttribute(attribute);
        }
        return new Progress(attribute, parseOperation(json.operation), nbtKey, capCounterKey, json.cap);
    }

    private static String progressKeyFromAttribute(String attributeId) {
        String s = attributeId.replace(':', '_');
        s = s.replaceAll("[^a-zA-Z0-9_]", "_");
        return "Progress_" + s;
    }

    private static String capCounterKeyFromAttribute(String attributeId) {
        String s = attributeId.replace(':', '_');
        s = s.replaceAll("[^a-zA-Z0-9_]", "_");
        return "CapCounter_" + s;
    }

    private static List<LinkedEvolve> toLinkedEvolves(List<LinkedEvolveJson> json) {
        if (json == null || json.isEmpty()) {
            return Collections.emptyList();
        }
        List<LinkedEvolve> out = new ArrayList<>();
        for (LinkedEvolveJson r : json) {
            if (r == null || r.item == null || r.to == null) {
                continue;
            }
            String item = normalize(r.item);
            String to = normalize(r.to);
            if (item == null || to == null) {
                continue;
            }
            out.add(new LinkedEvolve(item, to));
        }
        return List.copyOf(out);
    }

    private static RuleType parseRuleType(String type) {
        if (type == null || type.isBlank()) {
            return RuleType.EVOLVE;
        }
        String v = type.trim().toLowerCase(Locale.ROOT);
        return switch (v) {
            case "grant", "obtain" -> RuleType.GRANT;
            case "attribute", "gain_attribute", "modifier" -> RuleType.ATTRIBUTE;
            default -> RuleType.EVOLVE;
        };
    }

    private static Grant toGrant(GrantJson json) {
        if (json == null || json.item == null) {
            return null;
        }
        String item = normalize(json.item);
        if (item == null) {
            return null;
        }
        CurioGrantHelper.OverflowMode overflow = parseOverflowMode(json.overflowMode);
        boolean oncePerPlayer = json.oncePerPlayer != null && json.oncePerPlayer;
        boolean bindToPlayer = json.bindToPlayer != null && json.bindToPlayer;
        return new Grant(item, overflow, oncePerPlayer, bindToPlayer);
    }

    private static CurioGrantHelper.OverflowMode parseOverflowMode(String value) {
        if (value == null || value.isBlank()) {
            return CurioGrantHelper.OverflowMode.INVENTORY_THEN_DROP;
        }
        String v = value.trim().toUpperCase(Locale.ROOT);
        try {
            return CurioGrantHelper.OverflowMode.valueOf(v);
        } catch (Exception ignored) {
            return CurioGrantHelper.OverflowMode.INVENTORY_THEN_DROP;
        }
    }

    private static EntityRef toEntityRef(EntityRefJson json) {
        if (json == null || json.key == null) {
            return null;
        }
        String key = normalize(json.key);
        if (key == null) {
            return null;
        }
        List<String> nbt = toStringList(json.nbt);
        String name = normalize(json.name);
        if (nbt.isEmpty()) {
            name = null;
        } else if (name == null) {
            return null;
        }
        return new EntityRef(key, nbt, name);
    }

    private static AttributeModifier.Operation parseOperation(String op) {
        String v = op.toUpperCase(Locale.ROOT);
        return switch (v) {
            case "ADDITION", "ADD", "ADD_VALUE" -> AttributeModifier.Operation.ADDITION;
            case "MULTIPLY_BASE", "ADD_MULTIPLIED_BASE" -> AttributeModifier.Operation.MULTIPLY_BASE;
            case "MULTIPLY_TOTAL", "ADD_MULTIPLIED_TOTAL" -> AttributeModifier.Operation.MULTIPLY_TOTAL;
            default -> AttributeModifier.Operation.ADDITION;
        };
    }

    private static String normalize(String s) {
        if (s == null) {
            return null;
        }
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    private static String normalizeComparator(String s) {
        String v = s.trim().toLowerCase(Locale.ROOT);
        return switch (v) {
            case ">", "gt" -> "gt";
            case ">=", "gte" -> "gte";
            case "<", "lt" -> "lt";
            case "<=", "lte" -> "lte";
            case "==", "eq" -> "eq";
            case "!=", "ne" -> "ne";
            default -> v;
        };
    }

    public static final class Rule {
        public final String ruleId;
        public final RuleType type;
        public final boolean enabled;
        public final String trigger;
        public final boolean playerKilled;
        public final String item;
        public final String to;
        public final Requirements requirements;
        public final List<KillGain> kills;
        public final Progress progress;
        public final List<LinkedEvolve> requirementsRef;
        public final Grant grant;
        public final EntityRef killer;
        public final List<String> damageSourceTags;
        public final List<String> excludeNbtKeys;

        public Rule(String ruleId, RuleType type, boolean enabled, String trigger, boolean playerKilled, String item, String to,
                    Requirements requirements, List<KillGain> kills, Progress progress, List<LinkedEvolve> requirementsRef,
                    Grant grant, EntityRef killer, List<String> damageSourceTags, List<String> excludeNbtKeys) {
            this.ruleId = Objects.requireNonNull(ruleId);
            this.type = Objects.requireNonNull(type);
            this.enabled = enabled;
            this.trigger = trigger;
            this.playerKilled = playerKilled;
            this.item = item;
            this.to = to;
            this.requirements = Objects.requireNonNull(requirements);
            this.kills = Objects.requireNonNull(kills);
            this.progress = progress;
            this.requirementsRef = Objects.requireNonNull(requirementsRef);
            this.grant = grant;
            this.killer = killer;
            this.damageSourceTags = Objects.requireNonNull(damageSourceTags);
            this.excludeNbtKeys = Objects.requireNonNull(excludeNbtKeys);
        }
    }

    public enum RuleType {
        EVOLVE,
        ATTRIBUTE,
        GRANT
    }

    public static final class Requirements {
        public final List<KillRequirement> kills;
        public final List<AttributeRequirement> attributes;
        public final List<String> equippedCurios;

        public Requirements(List<KillRequirement> kills, List<AttributeRequirement> attributes, List<String> equippedCurios) {
            this.kills = Objects.requireNonNull(kills);
            this.attributes = Objects.requireNonNull(attributes);
            this.equippedCurios = Objects.requireNonNull(equippedCurios);
        }
    }

    public static final class EntityRef {
        public final String key;
        public final List<String> nbt;
        public final String name;

        public EntityRef(String key, List<String> nbt, String name) {
            this.key = key;
            this.nbt = Objects.requireNonNull(nbt);
            this.name = name;
        }
    }

    public static final class KillRequirement {
        public final EntityRef entity;
        public final int count;

        public KillRequirement(EntityRef entity, int count) {
            this.entity = entity;
            this.count = count;
        }
    }

    public static final class AttributeRequirement {
        public final String attribute;
        public final String comparator;
        public final double value;

        public AttributeRequirement(String attribute, String comparator, double value) {
            this.attribute = attribute;
            this.comparator = comparator;
            this.value = value;
        }
    }

    public static final class KillGain {
        public final EntityRef entity;
        public final double value;

        public KillGain(EntityRef entity, double value) {
            this.entity = entity;
            this.value = value;
        }
    }

    public static final class Progress {
        public final String attribute;
        public final AttributeModifier.Operation operation;
        public final String nbtKey;
        public final String capCounterKey;
        public final double cap;

        public Progress(String attribute, AttributeModifier.Operation operation, String nbtKey, String capCounterKey, double cap) {
            this.attribute = attribute;
            this.operation = operation;
            this.nbtKey = nbtKey;
            this.capCounterKey = capCounterKey;
            this.cap = cap;
        }
    }

    public static final class LinkedEvolve {
        public final String item;
        public final String to;

        public LinkedEvolve(String item, String to) {
            this.item = item;
            this.to = to;
        }
    }

    public static final class Grant {
        public final String item;
        public final CurioGrantHelper.OverflowMode overflowMode;
        public final boolean oncePerPlayer;
        public final boolean bindToPlayer;

        public Grant(String item, CurioGrantHelper.OverflowMode overflowMode, boolean oncePerPlayer, boolean bindToPlayer) {
            this.item = item;
            this.overflowMode = overflowMode;
            this.oncePerPlayer = oncePerPlayer;
            this.bindToPlayer = bindToPlayer;
        }
    }

    private static final class RuleJson {
        Boolean enabled;
        String trigger;
        Boolean playerKilled;
        List<String> damageSourceTags;
        String type;
        String item;
        String to;
        List<LinkedEvolveJson> requirementsRef;
        GrantJson grant;
        EntityRefJson killer;
        RequirementsJson requirements;
        List<KillGainJson> kills;
        ProgressJson progress;
        List<String> excludeNbtKeys;
        String ruleId;
    }

    private static final class RequirementsJson {
        List<KillRequirementJson> kills;
        List<AttributeRequirementJson> attributes;
        List<String> equippedCurios;
    }

    private static final class KillRequirementJson {
        EntityRefJson entity;
        int count;
    }

    private static final class KillGainJson {
        EntityRefJson entity;
        double value;
    }

    private static final class LinkedEvolveJson {
        String item;
        String to;
    }

    private static final class AttributeRequirementJson {
        String attribute;
        String comparator;
        double value;
    }

    private static final class GrantJson {
        String item;
        String overflowMode;
        Boolean oncePerPlayer;
        Boolean bindToPlayer;
    }

    private static final class EntityRefJson {
        String key;
        List<String> nbt;
        String name;
    }

    private static final class ProgressJson {
        String attribute;
        String operation;
        String nbtKey;
        String capCounterKey;
        double cap;
    }
}
