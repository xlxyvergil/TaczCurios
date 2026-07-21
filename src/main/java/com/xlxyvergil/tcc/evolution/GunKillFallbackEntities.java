package com.xlxyvergil.tcc.evolution;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 管理需要走 LivingDeathEvent 回退路径的实体列表。
 * 这些实体（如末影龙）在被枪械击杀时不会可靠地触发 EntityKillByGunEvent，
 * 因此需要通过 GunKillDebugFallbackHandler 来处理。
 */
public final class GunKillFallbackEntities {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String FILE_NAME = "gun_kill_fallback_entities.json";
    private static final String DEFAULT_RESOURCE = "/tcc_defaults/gun_kill_fallback_entities.json";

    private static volatile boolean loaded;
    private static Set<String> FALLBACK_ENTITIES = Collections.emptySet();

    private GunKillFallbackEntities() {}

    public static boolean contains(String entityKey) {
        loadOnce();
        return FALLBACK_ENTITIES.contains(entityKey);
    }

    public static void loadOnce() {
        if (loaded) return;
        synchronized (GunKillFallbackEntities.class) {
            if (loaded) return;
            Path file = FMLPaths.CONFIGDIR.get().resolve("tcc").resolve(FILE_NAME);
            try {
                Files.createDirectories(file.getParent());
            } catch (IOException e) {
                loaded = true;
                return;
            }
            if (!ensureDefaults(file)) {
                loaded = true;
                return;
            }
            readAll(file);
            loaded = true;
        }
    }

    private static boolean ensureDefaults(Path file) {
        if (Files.exists(file)) {
            return true;
        }
        try (InputStream in = GunKillFallbackEntities.class.getResourceAsStream(DEFAULT_RESOURCE)) {
            if (in == null) {
                return false;
            }
            Files.copy(in, file, StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private static void readAll(Path file) {
        Set<String> entities = new HashSet<>();
        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            var el = root.getAsJsonArray("entities");
            if (el != null) {
                for (var elem : el) {
                    String key = elem.getAsString();
                    if (key != null && !key.isBlank()) {
                        entities.add(key.trim());
                    }
                }
            }
        } catch (Exception e) {
            // ignore
        }
        FALLBACK_ENTITIES = Set.copyOf(entities);
    }
}
