package com.xlxyvergil.tcc.evolution;

import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.tacz.guns.api.event.common.EntityKillByGunEvent;
import net.minecraft.nbt.CompoundTag;
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

    private static final String LAST_HEADSHOT_ATTACKER_KEY = "tcc_last_headshot_attacker";
    private static final String LAST_HEADSHOT_TIME_KEY = "tcc_last_headshot_time";
    private static final String LAST_PROCESSED_KILL_UUID_KEY = "tcc_last_processed_headshot_kill_uuid";
    private static final String LAST_PROCESSED_KILL_TIME_KEY = "tcc_last_processed_headshot_kill_time";

    private GunHeadshotEventHandler() {}

    @SubscribeEvent
    public static void onGunHeadshotHit(EntityHurtByGunEvent.Pre event) {
        if (!event.isHeadShot()) return;
        LivingEntity attacker = event.getAttacker();
        if (!(attacker instanceof Player player)) return;
        if (player.level().isClientSide) return;

        LivingEntity hurt = resolveHurt(event);
        if (hurt != null) {
            CompoundTag tag = hurt.getPersistentData();
            tag.putString(LAST_HEADSHOT_ATTACKER_KEY, player.getStringUUID());
            tag.putLong(LAST_HEADSHOT_TIME_KEY, player.level().getGameTime());
        }

        handleTrigger(player, hurt, null, TRIGGER_GUN_HEADSHOT_HIT);
    }

    @SubscribeEvent
    public static void onGunHeadshotKill(EntityKillByGunEvent event) {
        LivingEntity attacker = event.getAttacker();
        if (!(attacker instanceof Player player)) return;
        if (player.level().isClientSide) return;

        LivingEntity killed = resolveKilled(event);
        if (killed == null) return;

        if (event.isHeadShot()) {
            triggerHeadshotKill(player, killed, null, event.getGunId());
            return;
        }
        CompoundTag tag = killed.getPersistentData();
        if (!player.getStringUUID().equals(tag.getString(LAST_HEADSHOT_ATTACKER_KEY))) return;
        long lastHeadshotTime = tag.getLong(LAST_HEADSHOT_TIME_KEY);
        if (player.level().getGameTime() - lastHeadshotTime > 2) return;
        triggerHeadshotKill(player, killed, null, event.getGunId());
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity killed = event.getEntity();
        if (killed.level().isClientSide) return;
        Entity sourceEntity = event.getSource().getEntity();
        if (!(sourceEntity instanceof Player player)) return;

        CompoundTag tag = killed.getPersistentData();
        if (!player.getStringUUID().equals(tag.getString(LAST_HEADSHOT_ATTACKER_KEY))) return;
        if (player.level().getGameTime() - tag.getLong(LAST_HEADSHOT_TIME_KEY) > 2) return;

        triggerHeadshotKill(player, killed, event.getSource(), null);
    }

    private static void triggerHeadshotKill(Player player, LivingEntity killed, DamageSource source,
                                             net.minecraft.resources.ResourceLocation gunId) {
        if (alreadyProcessedKill(player, killed)) return;
        handleTrigger(player, killed, gunId, TRIGGER_GUN_HEADSHOT_KILL);
    }

    private static boolean alreadyProcessedKill(Player player, LivingEntity killed) {
        CompoundTag data = player.getPersistentData();
        String uuid = killed.getStringUUID();
        long now = player.level().getGameTime();
        if (uuid.equals(data.getString(LAST_PROCESSED_KILL_UUID_KEY))
                && data.getLong(LAST_PROCESSED_KILL_TIME_KEY) == now) {
            return true;
        }
        data.putString(LAST_PROCESSED_KILL_UUID_KEY, uuid);
        data.putLong(LAST_PROCESSED_KILL_TIME_KEY, now);
        return false;
    }

    /**
     * Achievement-driven handler:
     * For each achievement with this trigger, check conditions,
     * award a criterion, and execute reward on completion.
     */
    private static void handleTrigger(Player player, LivingEntity other,
                                       net.minecraft.resources.ResourceLocation gunId, String trigger) {
        ServerPlayer serverPlayer = player instanceof ServerPlayer sp ? sp : null;
        if (serverPlayer == null) return;

        for (AchievementDefinitions.AchievementDef def : AchievementDefinitions.getByTrigger(trigger)) {
            // Check prerequisites
            if (!RuleAdvancementMapping.arePrerequisitesMet(serverPlayer, def)) continue;

            // Already completed?
            if (RuleAdvancementMapping.isAdvancementDone(serverPlayer, def.id())) continue;

            // Check kill conditions
            if (!AchievementConditionMatcher.matchesKillConditions(player, other, gunId, def)) continue;

            // Award criterion
            RuleAdvancementMapping.awardNextCriterion(
                    serverPlayer, def.id(), def.criteriaCount());
        }
    }

    private static LivingEntity resolveKilled(EntityKillByGunEvent event) {
        Object out = callGetter(event, "getKilledEntity");
        if (out instanceof LivingEntity living) return living;
        out = callGetter(event, "getHurtEntity");
        if (out instanceof LivingEntity living2) return living2;
        out = callGetter(event, "getEntity");
        if (out instanceof LivingEntity living3) return living3;
        return null;
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
