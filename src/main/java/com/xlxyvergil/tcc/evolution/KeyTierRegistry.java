package com.xlxyvergil.tcc.evolution;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * 神之键阶位注册表：根据 item id 决定其属于 1/2/3 阶还是特殊（9 级）神之键。
 * 阶位标签由 tcc/key_tiers.json 驱动，新增神之键无需改动 Java 代码，只需在 json 中登记。
 */
public final class KeyTierRegistry {

    /** 神之键阶位。ordinal 越大阶位越高，SPECIAL 为最高（9 级）。 */
    public enum KeyTier {
        NONE,
        T1,
        T2,
        T3,
        SPECIAL
    }

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String DIR_NAME = "tcc";
    private static final String FILE_NAME = "key_tiers.json";
    private static final String DEFAULT_RESOURCE = "/tcc_defaults/key_tiers.json";

    private static volatile boolean loaded;
    private static final Map<String, KeyTier> TIERS = new HashMap<>();

    private KeyTierRegistry() {
    }

    public static void loadOnce() {
        if (loaded) {
            return;
        }
        synchronized (KeyTierRegistry.class) {
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

    /** 返回物品对应的阶位；未登记的神之键返回 NONE。 */
    public static KeyTier tierOf(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return KeyTier.NONE;
        }
        var key = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (key == null) {
            return KeyTier.NONE;
        }
        return tierOf(key.toString());
    }

    /** 返回 item id（如 tcc:seven_thunders）对应的阶位；未登记返回 NONE。 */
    public static KeyTier tierOf(String itemId) {
        loadOnce();
        if (itemId == null || itemId.isBlank()) {
            return KeyTier.NONE;
        }
        return TIERS.getOrDefault(itemId.trim(), KeyTier.NONE);
    }

    private static void readAll(Path file) {
        TIERS.clear();
        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            JsonObject tiers = root.getAsJsonObject("tiers");
            if (tiers == null) {
                return;
            }
            for (var entry : tiers.entrySet()) {
                KeyTier tier = parseTier(entry.getKey());
                if (tier == KeyTier.NONE) {
                    continue;
                }
                JsonElement arr = entry.getValue();
                if (!arr.isJsonArray()) {
                    continue;
                }
                for (JsonElement el : arr.getAsJsonArray()) {
                    String id = el.getAsString().trim();
                    if (!id.isEmpty()) {
                        TIERS.put(id, tier);
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }

    private static KeyTier parseTier(String key) {
        if (key == null) {
            return KeyTier.NONE;
        }
        switch (key.trim().toLowerCase(Locale.ROOT)) {
            case "1":
            case "t1":
            case "1阶":
                return KeyTier.T1;
            case "2":
            case "t2":
            case "2阶":
                return KeyTier.T2;
            case "3":
            case "t3":
            case "3阶":
                return KeyTier.T3;
            case "special":
            case "9":
            case "s":
                return KeyTier.SPECIAL;
            default:
                return KeyTier.NONE;
        }
    }

    private static void ensureDefaults(Path file) {
        if (Files.exists(file)) {
            mergeDefaults(file);
        } else {
            copyDefaults(file);
        }
    }

    private static void copyDefaults(Path file) {
        try (InputStream in = KeyTierRegistry.class.getResourceAsStream(DEFAULT_RESOURCE)) {
            if (in == null) {
                return;
            }
            Files.copy(in, file, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ignored) {
        }
    }

    /** 合并默认阶位到已有配置：仅追加未登记的 item，保留用户修改。 */
    private static void mergeDefaults(Path file) {
        try {
            JsonObject defaultRoot;
            try (InputStream in = KeyTierRegistry.class.getResourceAsStream(DEFAULT_RESOURCE)) {
                if (in == null) {
                    return;
                }
                defaultRoot = JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject();
            }

            String userJson = Files.readString(file, StandardCharsets.UTF_8);
            JsonObject userRoot = JsonParser.parseString(userJson).getAsJsonObject();

            JsonObject defaultTiers = defaultRoot.getAsJsonObject("tiers");
            if (defaultTiers == null) {
                return;
            }

            JsonObject userTiers = userRoot.getAsJsonObject("tiers");
            if (userTiers == null) {
                userTiers = new JsonObject();
                userRoot.add("tiers", userTiers);
            }

            boolean added = false;
            for (var entry : defaultTiers.entrySet()) {
                String group = entry.getKey();
                JsonArray defaultGroup = entry.getValue().getAsJsonArray();
                JsonArray userGroup = userTiers.has(group) ? userTiers.getAsJsonArray(group) : null;

                Set<String> existing = new HashSet<>();
                if (userGroup != null) {
                    for (JsonElement el : userGroup) {
                        existing.add(el.getAsString());
                    }
                }

                List<String> toAdd = new ArrayList<>();
                for (JsonElement el : defaultGroup) {
                    String id = el.getAsString();
                    if (!existing.contains(id)) {
                        toAdd.add(id);
                    }
                }

                if (!toAdd.isEmpty()) {
                    JsonArray newGroup = userGroup == null ? new JsonArray() : userGroup;
                    for (String id : toAdd) {
                        newGroup.add(id);
                    }
                    userTiers.add(group, newGroup);
                    added = true;
                }
            }

            if (added) {
                Files.writeString(file, GSON.toJson(userRoot), StandardCharsets.UTF_8);
            }
        } catch (IOException ignored) {
        }
    }
}
