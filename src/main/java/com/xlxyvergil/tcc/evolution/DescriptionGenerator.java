package com.xlxyvergil.tcc.evolution;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.locale.Language;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 根据 AchievementDef 的 conditions 动态生成描述文本（中英双语）。
 * 玩家修改 JSON 条件后，描述自动跟随变化，无需手动改 lang 文件。
 */
public final class DescriptionGenerator {

    private DescriptionGenerator() {}

    /**
     * 根据成就定义动态生成描述字符串，包含 %d/%d 进度占位符。
     */
    public static String generate(AchievementDefinitions.AchievementDef def, String locale) {
        AchievementDefinitions.AchievementConditions c = def.conditions();
        boolean zh = locale.startsWith("zh");
        List<String> parts = new ArrayList<>();

        // === 饰品佩戴 ===
        if (c != null && c.equippedCurios() != null && !c.equippedCurios().isEmpty()) {
            String names = c.equippedCurios().stream()
                    .map(id -> resolveItemName(id))
                    .collect(Collectors.joining(zh ? "和" : " and "));
            parts.add(zh ? "佩戴" + names : "while wearing " + names);
        }

        // === 药水效果 ===
        if (c != null && c.requiredEffects() != null && !c.requiredEffects().isEmpty()) {
            String names = c.requiredEffects().stream()
                    .map(id -> resolveEffectName(id))
                    .collect(Collectors.joining(zh ? "和" : " and "));
            parts.add(zh ? names + "状态下" : "while under " + names);
        }

        // === trigger-specific ===
        switch (def.trigger()) {
            case "stat_polling" -> addStatPollingParts(parts, c, zh);
            case "gun_kill" -> addGunKillParts(parts, c, zh, false);
            case "gun_headshot_kill" -> addGunKillParts(parts, c, zh, true);
            case "living_death" -> addLivingDeathParts(parts, c, def, zh);
            case "biome_visit" -> addBiomeVisitParts(parts, c, zh);
            case "bleeding_settlement" -> addBleedingSettlementParts(parts, c, zh);
            case "auto" -> {
                if (def.reward() != null && def.reward().item() != null) {
                    parts.add(zh ? "获得" + resolveItemName(def.reward().item())
                            : "Obtain " + resolveItemName(def.reward().item()));
                }
            }
            default -> parts.add(def.trigger());
        }

        String desc = String.join(zh ? "，" : ", ", parts);
        if (desc.isEmpty()) desc = def.id();

        // append progress placeholder
        return desc + " (%d/%d)";
    }

    // ========== Per-trigger builders ==========

    private static void addStatPollingParts(List<String> parts, AchievementDefinitions.AchievementConditions c, boolean zh) {
        addStatPart(parts, c.stat(), c.statThreshold(), zh);
        addExtraStatsParts(parts, c, zh);
        addAttributesParts(parts, c, zh);
    }

    private static void addGunKillParts(List<String> parts, AchievementDefinitions.AchievementConditions c, boolean zh, boolean headshot) {
        // weapon
        String guns = resolveGunTypes(c.holdingGunTypes(), zh);
        if (!guns.isEmpty()) {
            parts.add(zh ? "使用" + guns : "with " + guns);
        }
        // kill
        addKillParts(parts, c, zh, headshot);
        // distance
        if (c.minDistance() != null) {
            parts.add(zh ? String.format("%.0f米外", c.minDistance()) : String.format("from %.0fm+", c.minDistance()));
        }
        // extra conditions
        addStatPart(parts, c.stat(), c.statThreshold(), zh);
        addExtraStatsParts(parts, c, zh);
        addAttributesParts(parts, c, zh);
        addHealthParts(parts, c, zh);
    }

    private static void addLivingDeathParts(List<String> parts, AchievementDefinitions.AchievementConditions c,
                                             AchievementDefinitions.AchievementDef def, boolean zh) {
        if (def.isPlayerKilled()) {
            // 玩家被某实体击杀
            if (c.killer() != null) {
                String name = AchievementDefinitions.entityDisplayName(c.killer());
                parts.add(zh ? "被" + name + "击杀" : "be killed by " + name);
            }
        } else {
            // 玩家击杀实体
            addKillParts(parts, c, zh, false);
            addStatPart(parts, c.stat(), c.statThreshold(), zh);
            addExtraStatsParts(parts, c, zh);
            addHealthParts(parts, c, zh);
        }
        addAttributesParts(parts, c, zh);
    }

    private static void addBiomeVisitParts(List<String> parts, AchievementDefinitions.AchievementConditions c, boolean zh) {
        if (c.dimension() != null) {
            parts.add(zh ? "访问" + resolveDimensionName(c.dimension(), zh)
                    : "visit " + resolveDimensionName(c.dimension(), zh));
        }
    }

    private static void addBleedingSettlementParts(List<String> parts, AchievementDefinitions.AchievementConditions c, boolean zh) {
        addAttributesParts(parts, c, zh);
        parts.add(zh ? "流血结算未死亡" : "survive bleeding settlement");
    }

    // ========== Shared condition parts ==========

    private static void addKillParts(List<String> parts, AchievementDefinitions.AchievementConditions c, boolean zh, boolean headshot) {
        if (c == null || c.kills() == null || c.kills().isEmpty()) return;
        List<String> killParts = new ArrayList<>();
        for (AchievementDefinitions.KillCondition kc : c.kills()) {
            String name = "*".equals(kc.entity())
                    ? (zh ? "任意实体" : "any entity")
                    : AchievementDefinitions.entityDisplayName(kc.entity());
            if (kc.value() > 1) {
                killParts.add(zh ? name + "×" + kc.value() : name + " ×" + kc.value());
            } else {
                killParts.add(name);
            }
        }
        String verb = headshot ? (zh ? "爆头击杀" : "headshot kill ")
                : (zh ? "击杀" : "kill ");
        parts.add(verb + String.join(zh ? "、" : ", ", killParts));
    }

    private static void addStatPart(List<String> parts, String stat, int threshold, boolean zh) {
        if (stat == null) return;
        String name = resolveStatName(stat);
        parts.add(zh ? name + "达到" + threshold : "reach " + threshold + " " + name);
    }

    private static void addExtraStatsParts(List<String> parts, AchievementDefinitions.AchievementConditions c, boolean zh) {
        if (c == null || c.extraStats() == null) return;
        for (AchievementDefinitions.StatCondition sc : c.extraStats()) {
            addStatPart(parts, sc.stat(), sc.statThreshold(), zh);
        }
    }

    private static void addAttributesParts(List<String> parts, AchievementDefinitions.AchievementConditions c, boolean zh) {
        if (c == null || c.attributes() == null) return;
        for (AchievementDefinitions.AttributeCondition ac : c.attributes()) {
            String name = resolveAttributeName(ac.attribute());
            String cmp = resolveComparator(ac.comparator(), zh);
            String value = formatDouble(ac.value());
            parts.add(name + cmp + value);
        }
    }

    private static void addHealthParts(List<String> parts, AchievementDefinitions.AchievementConditions c, boolean zh) {
        if (c == null) return;
        if (c.healthMax() != null) {
            double hearts = c.healthMax() / 2.0;
            String heartsStr = formatDouble(hearts);
            parts.add(zh ? "血量低于" + heartsStr + "心" : "with less than " + heartsStr + " hearts");
        } else if (c.healthMin() != null && c.healthMin() > 0) {
            double hearts = c.healthMin() / 2.0;
            String heartsStr = formatDouble(hearts);
            parts.add(zh ? "血量高于" + heartsStr + "心" : "with more than " + heartsStr + " hearts");
        }
    }

    // ========== Name resolvers ==========

    private static String resolveItemName(String itemId) {
        try {
            ResourceLocation rl = new ResourceLocation(itemId);
            Item item = BuiltInRegistries.ITEM.get(rl);
            if (item != null && item != net.minecraft.world.item.Items.AIR) {
                return Language.getInstance().getOrDefault(item.getDescriptionId(), itemId);
            }
        } catch (Exception ignored) {}
        return itemId;
    }

    private static String resolveStatName(String statKey) {
        // Minecraft provides stat.<namespace>.<path> translation natively
        String tlKey = "stat." + statKey.replace(':', '.');
        String translated = Language.getInstance().getOrDefault(tlKey, null);
        if (translated != null && !translated.equals(tlKey)) return translated;

        // Fallback: extract path as readable name
        try {
            ResourceLocation rl = ResourceLocation.tryParse(statKey);
            if (rl != null) return rl.getPath().replace('_', ' ');
        } catch (Exception ignored) {}
        return statKey;
    }

    private static String resolveEffectName(String effectId) {
        try {
            ResourceLocation rl = ResourceLocation.tryParse(effectId);
            if (rl != null) {
                var effect = BuiltInRegistries.MOB_EFFECT.get(rl);
                if (effect != null) {
                    return Language.getInstance().getOrDefault(effect.getDescriptionId(), effectId);
                }
            }
        } catch (Exception ignored) {}
        return effectId;
    }

    private static String resolveAttributeName(String attrId) {
        // TCC custom attributes
        if ("tcc:imaginary_damage_resistance".equals(attrId)) {
            String zh = Language.getInstance().getOrDefault("attribute.tcc.imaginary_damage_resistance", null);
            if (zh != null) return zh;
        }
        // Vanilla attributes
        try {
            ResourceLocation rl = ResourceLocation.tryParse(attrId);
            if (rl != null) {
                var attr = BuiltInRegistries.ATTRIBUTE.get(rl);
                if (attr != null) {
                    return Language.getInstance().getOrDefault(attr.getDescriptionId(), attrId);
                }
            }
        } catch (Exception ignored) {}
        return attrId;
    }

    private static final Set<String> HEAVY = Set.of("rpg", "mg");

    private static String resolveGunTypes(List<String> types, boolean zh) {
        if (types == null || types.isEmpty()) return "";
        if (zh) {
            // Detect "rpg"+"mg" combo → "重型武器"
            if (types.size() == 2 && types.containsAll(HEAVY)) return "重型武器";
            return types.stream()
                    .map(t -> switch (t) {
                        case "rpg", "mg" -> "重型武器";
                        case "pistol" -> "手枪";
                        case "rifle" -> "步枪";
                        case "sniper" -> "狙击枪";
                        case "shotgun" -> "霰弹枪";
                        case "smg" -> "冲锋枪";
                        default -> t;
                    })
                    .distinct()
                    .collect(Collectors.joining("/"));
        } else {
            if (types.size() == 2 && types.containsAll(HEAVY)) return "a heavy weapon";
            return types.stream()
                    .map(t -> switch (t) {
                        case "rpg", "mg" -> "heavy weapon";
                        default -> t;
                    })
                    .distinct()
                    .collect(Collectors.joining("/"));
        }
    }

    private static String resolveDimensionName(String dimKey, boolean zh) {
        return switch (dimKey) {
            case "minecraft:the_nether" -> zh ? "下界" : "the Nether";
            case "minecraft:the_end" -> zh ? "末地" : "the End";
            case "minecraft:overworld" -> zh ? "主世界" : "the Overworld";
            default -> dimKey;
        };
    }

    private static String resolveComparator(String cmp, boolean zh) {
        return switch (cmp) {
            case "gt" -> zh ? "大于" : " > ";
            case "gte" -> zh ? "大于等于" : " ≥ ";
            case "lt" -> zh ? "小于" : " < ";
            case "lte" -> zh ? "小于等于" : " ≤ ";
            case "eq" -> zh ? "等于" : " = ";
            case "ne" -> zh ? "不等于" : " ≠ ";
            default -> cmp;
        };
    }

    private static String formatDouble(double v) {
        return v == Math.floor(v) && !Double.isInfinite(v)
                ? String.valueOf((long) v)
                : String.format("%.1f", v).replaceAll("\\.0$", "");
    }
}
