package com.xlxyvergil.tcc.evolution;

import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.xlxyvergil.tcc.capability.GunKillDataCapability;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.lang.reflect.Method;

@Mod.EventBusSubscriber(modid = "tcc", bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class GunHeadshotEventHandler {
    public static final String TRIGGER_GUN_HEADSHOT_HIT = "gun_headshot_hit";
    public static final String TRIGGER_GUN_HEADSHOT_KILL = "gun_headshot_kill";

    /** 爆头 → 死亡的时间窗口（tick）。虚数伤害在 Post 事件同 tick 触发，2 tick 足够。 */
    private static final long DEATH_WINDOW_TICKS = 2L;

    private GunHeadshotEventHandler() {}

    /**
     * 监听 {@link EntityHurtByGunEvent.Pre}：
     * 1. 爆头命中时触发 gun_headshot_hit 成就判定；
     * 2. 将爆头标记（attacker / time / gunId）写入 GunKillDataCapability，供 {@link #onLivingDeath} 使用。
     */
    @SubscribeEvent
    public static void onGunHeadshotHit(EntityHurtByGunEvent.Pre event) {
        if (!event.isHeadShot()) return;
        LivingEntity attacker = event.getAttacker();
        if (!(attacker instanceof Player player)) return;
        if (player.level().isClientSide) return;

        LivingEntity hurt = resolveHurt(event);
        if (hurt != null) {
            GunKillDataCapability.setHeadshotData(hurt,
                player.getStringUUID(),
                player.level().getGameTime(),
                event.getGunId() != null ? event.getGunId().toString() : "");
        }

        handleTrigger(player, hurt, event.getGunId(), TRIGGER_GUN_HEADSHOT_HIT);
    }

    /**
     * 统一的爆头击杀判定：监听 {@link LivingDeathEvent}，对所有实体处理。
     *
     * 流程：
     * 1. 死亡源 attacker 必须是玩家；
     * 2. 从 GunKillDataCapability 读取爆头标记：attacker 匹配，且在 2 tick 窗口内；
     * 3. 读取 gunId，触发 gun_headshot_kill 成就判定。
     *
     * 使用 Capability 而非 NBT，以兼容 {@code getPersistentData()} 被重写返回空 NBT 的实体
     * （如 RevelationFix 的 Apollyon）。
     */
    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity killed = event.getEntity();
        if (killed.level().isClientSide) return;

        // ① 死亡源 attacker 必须是玩家
        DamageSource source = event.getSource();
        Entity sourceEntity = source.getEntity();
        if (!(sourceEntity instanceof Player player)) return;

        // ② 从 Capability 读取爆头标记
        var cap = killed.getCapability(GunKillDataCapability.CAPABILITY);
        if (!cap.isPresent()) return;
        var data = cap.orElse(null).data();
        if (!player.getStringUUID().equals(data.headshotAttacker)) return;
        if (player.level().getGameTime() - data.headshotTime > DEATH_WINDOW_TICKS) return;

        // ③ 读取 gunId 并触发爆头击杀判定
        net.minecraft.resources.ResourceLocation gunId = null;
        if (!data.headshotGunId.isBlank()) {
            try {
                gunId = new net.minecraft.resources.ResourceLocation(data.headshotGunId);
            } catch (Exception ignored) {
                gunId = null;
            }
        }

        triggerHeadshotKill(player, killed, source, gunId);
    }

    private static void triggerHeadshotKill(Player player, LivingEntity killed, DamageSource source,
                                             net.minecraft.resources.ResourceLocation gunId) {
        handleTrigger(player, killed, gunId, TRIGGER_GUN_HEADSHOT_KILL);
    }

    /**
     * Achievement-driven handler:
     * For each achievement with this trigger, check conditions,
     * award a criterion, and execute reward on completion.
     */
    private static void handleTrigger(Player player, LivingEntity other,
                                       net.minecraft.resources.ResourceLocation gunId, String trigger) {
        // gun_headshot_kill 必须是枪械击杀，没有 gunId 说明不是枪杀，直接跳过
        if (TRIGGER_GUN_HEADSHOT_KILL.equals(trigger) && gunId == null) return;
        ServerPlayer serverPlayer = player instanceof ServerPlayer sp ? sp : null;
        if (serverPlayer == null) return;

        for (AchievementDefinitions.AchievementDef def : AchievementDefinitions.getByTrigger(trigger)) {
            // Skip disabled achievements
            if (!def.isEnabled()) continue;

            // Check prerequisites
            if (!RuleAdvancementMapping.arePrerequisitesMet(serverPlayer, def)) continue;

            // Already completed?
            if (RuleAdvancementMapping.isAdvancementDone(serverPlayer, def.id())) continue;

            // Check kill conditions
            if (!AchievementConditionMatcher.matchesKillConditions(player, other, gunId, def)) continue;

            // Award criterion(s) based on kill value
            var matchedKill = AchievementConditionMatcher.findMatchingKillCondition(
                    other, def.conditions());
            int killValue = matchedKill.map(AchievementDefinitions.KillCondition::value).orElse(1);
            RuleAdvancementMapping.awardSteps(
                    serverPlayer, def.id(), def.criteriaCount(), killValue);
        }
    }

    private static LivingEntity resolveHurt(EntityHurtByGunEvent.Pre event) {
        Object out = callGetter(event, "getHurtEntity");
        if (out instanceof LivingEntity living) return living;
        out = callGetter(event, "getEntity");
        if (out instanceof LivingEntity living2) return living2;
        return null;
    }

    private static Object callGetter(Object obj, String name) {
        try {
            Method m = obj.getClass().getMethod(name);
            return m.invoke(obj);
        } catch (Exception ignored) {
            return null;
        }
    }
}
