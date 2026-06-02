package com.xlxyvergil.tcc.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;
import java.util.List;

public final class EntityConditionHelper {
    private EntityConditionHelper() {
    }

    public static String formatNbtFilterForDisplay(@Nullable String nbtFilter) {
        if (nbtFilter == null || nbtFilter.isEmpty()) {
            return "";
        }
        String[] conditions = nbtFilter.split(",");
        StringBuilder sb = new StringBuilder();
        for (String condition : conditions) {
            String[] kv = condition.split("=", 2);
            if (kv.length != 2) {
                continue;
            }
            String key = kv[0].trim();
            String expectedValue = kv[1].trim();
            String token;
            if ("true".equals(expectedValue)) {
                token = simplifyKey(key);
            } else if ("false".equals(expectedValue)) {
                token = "!" + simplifyKey(key);
            } else {
                token = stripQuotes(expectedValue);
            }
            if (token.isEmpty()) {
                continue;
            }
            if (!sb.isEmpty()) {
                sb.append(' ');
            }
            sb.append(token);
        }
        return sb.toString();
    }

    public static boolean matchesNbtFilter(LivingEntity entity, @Nullable String nbtFilter) {
        if (nbtFilter == null || nbtFilter.isEmpty()) return true;
        CompoundTag data = entity.getPersistentData();
        String[] conditions = nbtFilter.split(",");
        for (String condition : conditions) {
            String[] kv = condition.split("=", 2);
            if (kv.length != 2) continue;
            String key = kv[0].trim();
            String expectedValue = kv[1].trim();
            if (!data.contains(key)) return false;
            if ("true".equals(expectedValue)) {
                if (!data.getBoolean(key)) return false;
            } else if ("false".equals(expectedValue)) {
                if (data.getBoolean(key)) return false;
            } else {
                if (!expectedValue.equals(data.getString(key))) return false;
            }
        }
        return true;
    }

    public static boolean matchesNbtFilters(LivingEntity entity, @Nullable List<String> nbtFilters) {
        if (nbtFilters == null || nbtFilters.isEmpty()) {
            return true;
        }
        return matchesNbtFilter(entity, String.join(",", nbtFilters));
    }

    public static String getMatchKey(String entityId, @Nullable String nbtFilter) {
        if (nbtFilter == null || nbtFilter.isEmpty()) return entityId;
        return entityId + "[" + nbtFilter + "]";
    }

    public static String getMatchKey(String entityId, @Nullable List<String> nbtFilters) {
        if (nbtFilters == null || nbtFilters.isEmpty()) {
            return entityId;
        }
        return entityId + "[" + String.join(",", nbtFilters) + "]";
    }

    public static String getBaseEntityId(String key) {
        int bracketIdx = key.indexOf('[');
        return bracketIdx > 0 ? key.substring(0, bracketIdx) : key;
    }

    public static String extractNbtFilter(String key) {
        int bracketIdx = key.indexOf('[');
        return bracketIdx > 0 ? key.substring(bracketIdx + 1, key.length() - 1) : "";
    }

    private static String simplifyKey(String key) {
        int idx = key.lastIndexOf('.');
        String v = idx >= 0 ? key.substring(idx + 1) : key;
        return v.trim();
    }

    private static String stripQuotes(String value) {
        String v = value.trim();
        if (v.length() >= 2 && ((v.startsWith("\"") && v.endsWith("\"")) || (v.startsWith("'") && v.endsWith("'")))) {
            return v.substring(1, v.length() - 1);
        }
        return v;
    }
}
