package com.xlxyvergil.tcc.util;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;

import javax.annotation.Nullable;
import java.util.List;

public final class EntityConditionHelper {
    private EntityConditionHelper() {
    }

    /**
     * 判断实体是否匹配指定的实体键。
     * "*" 匹配任意实体；"#<MobType>" 用 getMobType() 判定原版 MobType（与原版亡灵杀手等附魔语义一致）；
     * "#namespace:tag" 匹配实体类型 tag；其它值精确匹配实体类型 ID。
     */
    public static boolean matchesEntityKey(String entityKey, Entity entity) {
        if (entityKey == null || entityKey.isEmpty() || entity == null) return false;
        if ("*".equals(entityKey)) return true;
        if (entityKey.startsWith("#")) {
            String tagStr = entityKey.substring(1);
            // 仅支持原版 MobType，用 getMobType() 硬编码判定，与原版「亡灵杀手」等附魔语义一致。
            MobType mobType = parseMobType(tagStr);
            if (mobType != null) {
                return entity instanceof LivingEntity le && le.getMobType() == mobType;
            }
            // 非原版 MobType 名称按实体类型 tag 处理（如 #minecraft:skeletons、#goety:xxx）。
            ResourceLocation tagId = ResourceLocation.tryParse(tagStr);
            if (tagId == null) return false;
            TagKey<EntityType<?>> tag = TagKey.create(Registries.ENTITY_TYPE, tagId);
            return entity.getType().is(tag);
        }
        String entityTypeKey = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString();
        return entityTypeKey.equals(entityKey);
    }

    /**
     * 将字符串解析为原版 MobType（硬编码的实体分类），大小写不敏感。
     * 支持 undead、UNDEAD、minecraft:undead 等形式，以及 arthropod/illager/water/undefined。
     * 不认识的名称返回 null，交由调用方按实体类型 tag 处理。
     */
    @Nullable
    private static MobType parseMobType(String name) {
        if (name == null) return null;
        switch (name.toLowerCase()) {
            case "undead":
            case "minecraft:undead":
                return MobType.UNDEAD;
            case "arthropod":
            case "minecraft:arthropod":
                return MobType.ARTHROPOD;
            case "illager":
            case "minecraft:illager":
                return MobType.ILLAGER;
            case "water":
            case "minecraft:water":
                return MobType.WATER;
            case "undefined":
            case "minecraft:undefined":
                return MobType.UNDEFINED;
            default:
                return null;
        }
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
