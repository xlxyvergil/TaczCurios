package com.xlxyvergil.tcc.evolution;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Uses Minecraft's SavedData system to reliably track which players have received
 * once-per-player grant items. This is more reliable than entity persistent data
 * because SavedData is explicitly marked dirty and saved to world data on disk.
 */
public class GrantHistoryData extends SavedData {
    private static final String DATA_NAME = "tcc_grant_history";

    private final Map<String, Set<UUID>> received = new HashMap<>();

    public boolean hasReceived(String ruleId, UUID playerId) {
        Set<UUID> set = received.get(ruleId);
        return set != null && set.contains(playerId);
    }

    public void markReceived(String ruleId, UUID playerId) {
        received.computeIfAbsent(ruleId, k -> new HashSet<>()).add(playerId);
        setDirty();
    }

    public static GrantHistoryData load(CompoundTag tag) {
        GrantHistoryData data = new GrantHistoryData();
        for (String ruleId : tag.getAllKeys()) {
            CompoundTag playersTag = tag.getCompound(ruleId);
            Set<UUID> uuids = new HashSet<>();
            for (String key : playersTag.getAllKeys()) {
                try {
                    uuids.add(UUID.fromString(key));
                } catch (IllegalArgumentException ignored) {
                }
            }
            if (!uuids.isEmpty()) {
                data.received.put(ruleId, uuids);
            }
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        for (Map.Entry<String, Set<UUID>> entry : received.entrySet()) {
            CompoundTag playersTag = new CompoundTag();
            for (UUID uuid : entry.getValue()) {
                playersTag.putBoolean(uuid.toString(), true);
            }
            tag.put(entry.getKey(), playersTag);
        }
        return tag;
    }

    /**
     * Gets or creates the GrantHistoryData for the given server.
     * Uses the overworld's data storage since it always exists.
     */
    public static GrantHistoryData get(MinecraftServer server) {
        ServerLevel overworld = server.getLevel(Level.OVERWORLD);
        if (overworld == null) {
            return null;
        }
        return overworld.getDataStorage().computeIfAbsent(GrantHistoryData::load, GrantHistoryData::new, DATA_NAME);
    }
}
