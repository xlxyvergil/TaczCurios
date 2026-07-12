package com.xlxyvergil.tcc.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 临时内存缓存：在伤害前事件（Pre-hurt）中快照攻击者身上的 Buff 列表，
 * 供击杀后成就条件检查使用。
 * <p>
 * 用于解决某些 Buff（如 Iron's Spellbooks 的 true_invisibility）
 * 在攻击后自动移除，导致击杀时无法通过实时检查的问题。
 * </p>
 */
public final class EffectCacheHelper {

    /** target UUID → (attacker UUID string → effects CompoundTag) */
    private static final Map<UUID, Map<String, CompoundTag>> CACHE = new ConcurrentHashMap<>();

    private EffectCacheHelper() {}

    /**
     * 将攻击者当前活跃的 MobEffect 列表快照到内存缓存中。
     * 应在伤害前事件中调用，此时攻击破隐效果尚未触发。
     *
     * @param attacker 攻击者（玩家）
     * @param target   被攻击的目标实体
     */
    public static void snapshotAttackerEffects(Player attacker, LivingEntity target) {
        if (attacker == null || target == null) return;

        Collection<MobEffectInstance> activeEffects = attacker.getActiveEffects();
        CompoundTag effectsTag = new CompoundTag();
        for (MobEffectInstance instance : activeEffects) {
            ResourceLocation key = ForgeRegistries.MOB_EFFECTS.getKey(instance.getEffect());
            if (key != null) {
                effectsTag.putBoolean(key.toString(), true);
            }
        }

        CACHE.computeIfAbsent(target.getUUID(), k -> new ConcurrentHashMap<>())
             .put(attacker.getStringUUID(), effectsTag);
    }

    /**
     * 检查指定玩家在攻击指定目标时是否拥有某个 Buff。
     *
     * @param target     被击杀的目标实体
     * @param playerUuid 攻击者 UUID 字符串
     * @param effectId   效果 ResourceLocation 字符串（如 "irons_spellbooks:true_invisibility"）
     * @return 如果缓存中存在该 Buff，返回 true
     */
    public static boolean hadEffectCached(LivingEntity target, String playerUuid, String effectId) {
        if (target == null || playerUuid == null || effectId == null) return false;

        Map<String, CompoundTag> attackerMap = CACHE.get(target.getUUID());
        if (attackerMap == null) return false;

        CompoundTag effects = attackerMap.get(playerUuid);
        return effects != null && effects.contains(effectId);
    }

    /**
     * 清理指定目标实体的所有缓存条目（实体死亡或检查完成后调用）。
     */
    public static void clearTarget(LivingEntity target) {
        if (target != null) {
            CACHE.remove(target.getUUID());
        }
    }
}
