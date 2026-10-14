package com.xlxyvergil.tcc.util;

import net.minecraft.world.entity.LivingEntity;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 受伤冷却表：受击后进入的短暂无敌窗口（唯一需要保留的瞬时状态）。
 * <p>
 * 以「到期时刻（世界游戏 tick）」记录而非剩余 tick，因此无需每 tick 递减；过期记录在查询时惰性清除。
 * 由于绝对时间按世界计数，换存档后世界 tick 原点不同，故在服务器停止时清空，避免旧存档的时间戳
 * 在新存档里被误判为「仍在冷却」。
 */
public final class HurtCooldownTracker {

    /** UUID → 冷却到期时刻（世界游戏 tick）：当前时间小于该值时伤害归零。 */
    private static final Map<UUID, Long> COOLDOWN_END_MAP = new ConcurrentHashMap<>();

    private HurtCooldownTracker() {
    }

    /** 清空全部冷却记录。 */
    public static void clearAll() {
        COOLDOWN_END_MAP.clear();
    }

    /** 实体当前是否处于受伤冷却；顺带清除已过期的记录。 */
    public static boolean isInCooldown(LivingEntity entity) {
        if (entity == null) return false;
        UUID id = entity.getUUID();
        Long end = COOLDOWN_END_MAP.get(id);
        if (end == null) return false;
        if (entity.level().getGameTime() < end) {
            return true;
        }
        COOLDOWN_END_MAP.remove(id, end);
        return false;
    }

    /** 令实体立即进入受伤冷却：此后 cooldownTicks 内受到的伤害归零。 */
    public static void start(LivingEntity entity, int cooldownTicks) {
        if (entity == null || cooldownTicks <= 0) return;
        COOLDOWN_END_MAP.put(entity.getUUID(), entity.level().getGameTime() + cooldownTicks);
    }
}
