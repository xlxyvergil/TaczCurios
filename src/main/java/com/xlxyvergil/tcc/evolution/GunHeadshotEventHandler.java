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
    private static final String LAST_HEADSHOT_GUN_ID_KEY = "tcc_last_headshot_gun_id";

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
            tag.putString(LAST_HEADSHOT_GUN_ID_KEY, event.getGunId() != null ? event.getGunId().toString() : "");
        }

        handleTrigger(player, hurt, event.getGunId(), TRIGGER_GUN_HEADSHOT_HIT);
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

        net.minecraft.resources.ResourceLocation gunId = null;
        String gunIdStr = tag.getString(LAST_HEADSHOT_GUN_ID_KEY);
        if (gunIdStr != null && !gunIdStr.isBlank()) {
            try {
                gunId = new net.minecraft.resources.ResourceLocation(gunIdStr);
            } catch (Exception ignored) {
                gunId = null;
            }
        }

        triggerHeadshotKill(player, killed, event.getSource(), gunId);
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
