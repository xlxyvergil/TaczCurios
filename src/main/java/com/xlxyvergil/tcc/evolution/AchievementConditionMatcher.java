package com.xlxyvergil.tcc.evolution;

import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.util.EntityConditionHelper;
import com.xlxyvergil.tcc.util.GunTypeChecker;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.stats.Stats;

import java.util.Optional;

/**
 * 将成就条件与游戏内事件进行匹配，供各触发器处理器判断玩家是否满足成就进度条件。
 */
public final class AchievementConditionMatcher {
    private AchievementConditionMatcher() {}

    /**
     * 检查给定击杀事件是否满足成就的全部条件。
     */
    public static boolean matchesKillConditions(Player player, LivingEntity killed,
                                                 ResourceLocation gunId, AchievementDefinitions.AchievementDef def) {
        AchievementDefinitions.AchievementConditions c = def.conditions();
        if (c == null) return true;

        if (c.equippedCurios() != null) {
            for (String curio : c.equippedCurios()) {
                if (!LivingDeathEventHandler.hasEquipped(player, curio)) return false;
            }
        }

        // 检查玩家身上的效果（Buff）
        if (c.requiredEffects() != null) {
            for (String effectId : c.requiredEffects()) {
                ResourceLocation effectRl = ResourceLocation.tryParse(effectId);
                if (effectRl == null) return false;
                var effect = net.minecraftforge.registries.ForgeRegistries.MOB_EFFECTS.getValue(effectRl);
                if (effect == null || !player.hasEffect(effect)) return false;
            }
        }

        if (c.holdingGunTypes() != null && !c.holdingGunTypes().isEmpty()) {
            if (gunId == null) return false;
            if (!GunTypeChecker.matchesGunTypes(gunId, c.holdingGunTypes())) return false;
        }

        if (c.minDistance() != null && killed != null) {
            double min = c.minDistance();
            if (player.distanceToSqr(killed) < min * min) return false;
        }

        if (c.kills() != null && !c.kills().isEmpty() && killed != null) {
            if (findMatchingKillCondition(killed, c).isEmpty()) return false;
        }

        // 按被击杀实体所在维度过滤击杀目标
        if (!matchesKillDimension(killed, c)) return false;

        if (c.attributes() != null) {
            for (AchievementDefinitions.AttributeCondition ac : c.attributes()) {
                Attribute attr = AttributeHelper.resolveAttribute(ac.attribute());
                if (attr == null) return false;
                double value = player.getAttributeValue(attr);
                if (!compare(value, ac.comparator(), ac.value())) return false;
            }
        }

        if (c.healthMax() != null && player.getHealth() > c.healthMax()) return false;

        if (c.minHeight() != null && killed != null) {
            double minH = c.minHeight();
            if (player.getY() < minH || killed.getY() < minH) return false;
        }

        return true;
    }

    /**
     * 检查死亡事件（无 gunId）是否满足成就条件。
     */
    public static boolean matchesDeathConditions(Player player, LivingEntity killed,
                                                  Entity otherEntity, AchievementDefinitions.AchievementDef def) {
        AchievementDefinitions.AchievementConditions c = def.conditions();
        if (c == null) return true;

        if (c.equippedCurios() != null) {
            for (String curio : c.equippedCurios()) {
                boolean has = LivingDeathEventHandler.hasEquipped(player, curio);
                if (!has) return false;
            }
        }

        if (c.killer() != null) {
            if (otherEntity == null) { return false; }
            if (!EntityConditionHelper.matchesEntityKey(c.killer(), otherEntity)) { return false; }
        }

        if (c.kills() != null && !c.kills().isEmpty() && killed != null) {
            boolean matched = false;
            for (AchievementDefinitions.KillCondition kc : c.kills()) {
                if (EntityConditionHelper.matchesEntityKey(kc.entity(), killed)) {
                    matched = true;
                    break;
                }
            }
            if (!matched) { return false; }
        }

        // 按被击杀实体所在维度过滤击杀目标
        if (!matchesKillDimension(killed, c)) return false;

        if (c.extraStats() != null && player instanceof ServerPlayer sp2) {
            for (AchievementDefinitions.StatCondition sc : c.extraStats()) {
                ResourceLocation key = ResourceLocation.tryParse(sc.stat());
                if (key == null) return false;
                ResourceLocation canonicalId = BuiltInRegistries.CUSTOM_STAT.get(key);
                if (canonicalId == null) return false;
                var s = Stats.CUSTOM.get(canonicalId);
                if (s == null) return false;
                if (sp2.getStats().getValue(s) < sc.criteriaCount()) return false;
            }
        }

        if (c.attributes() != null) {
            for (AchievementDefinitions.AttributeCondition ac : c.attributes()) {
                Attribute attr = AttributeHelper.resolveAttribute(ac.attribute());
                if (attr == null) return false;
                double value = player.getAttributeValue(attr);
                if (!compare(value, ac.comparator(), ac.value())) return false;
            }
        }

        if (c.healthMax() != null && player.getHealth() > c.healthMax()) return false;

        return true;
    }

    /**
     * 检查 stat_polling / biome_visit 事件（无击杀/死亡上下文）的条件：佩戴饰品、属性阈值、维度。
     */
    public static boolean matchesStatBiomeConditions(Player player, AchievementDefinitions.AchievementDef def) {
        AchievementDefinitions.AchievementConditions c = def.conditions();
        if (c == null) return true;

        if (c.equippedCurios() != null) {
            for (String curio : c.equippedCurios()) {
                if (!LivingDeathEventHandler.hasEquipped(player, curio)) return false;
            }
        }

        if (c.attributes() != null) {
            for (AchievementDefinitions.AttributeCondition ac : c.attributes()) {
                Attribute attr = AttributeHelper.resolveAttribute(ac.attribute());
                if (attr == null) return false;
                double value = player.getAttributeValue(attr);
                if (!compare(value, ac.comparator(), ac.value())) return false;
            }
        }

        if (c.dimension() != null) {
            ResourceLocation rl = ResourceLocation.tryParse(c.dimension());
            if (rl == null) return false;
            ResourceKey<Level> target = ResourceKey.create(Registries.DIMENSION, rl);
            if (player.level().dimension() != target) return false;
        }

        return true;
    }

    /**
     * 检查被击杀实体所在维度是否满足条件的 dimension 要求。
     * killed 为 null（部分事件无击杀实体）时维度检查自动放行。
     */
    private static boolean matchesKillDimension(LivingEntity killed, AchievementDefinitions.AchievementConditions c) {
        if (c.dimension() == null || killed == null) return true;
        ResourceLocation rl = ResourceLocation.tryParse(c.dimension());
        if (rl == null) return false;
        ResourceKey<Level> target = ResourceKey.create(Registries.DIMENSION, rl);
        return killed.level().dimension() == target;
    }

    private static boolean compare(double current, String comparator, double expected) {
        return switch (comparator) {
            case "gt" -> current > expected;
            case "gte" -> current >= expected;
            case "lt" -> current < expected;
            case "lte" -> current <= expected;
            case "eq" -> Double.compare(current, expected) == 0;
            case "ne" -> Double.compare(current, expected) != 0;
            default -> false;
        };
    }

    /**
     * 在成就的击杀条件列表中查找匹配的击杀条件，同时检查实体类型和 NBT 标签。
     */
    public static Optional<AchievementDefinitions.KillCondition> findMatchingKillCondition(
            LivingEntity killed, AchievementDefinitions.AchievementConditions conditions) {
        if (killed == null || conditions == null || conditions.kills() == null || conditions.kills().isEmpty()) {
            return Optional.empty();
        }
        for (AchievementDefinitions.KillCondition kc : conditions.kills()) {
            if (!EntityConditionHelper.matchesEntityKey(kc.entity(), killed)) {
                continue;
            }
            if (!EntityConditionHelper.matchesNbtFilters(killed, kc.nbt())) {
                continue;
            }
            return Optional.of(kc);
        }
        return Optional.empty();
    }
}
