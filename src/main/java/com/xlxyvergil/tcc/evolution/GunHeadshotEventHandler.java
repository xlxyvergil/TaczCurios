package com.xlxyvergil.tcc.evolution;

import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.tacz.guns.api.event.common.EntityKillByGunEvent;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.util.EntityConditionHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.advancements.Advancement;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.lang.reflect.Method;

@Mod.EventBusSubscriber(modid = "tcc", bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class GunHeadshotEventHandler {
    public static final String TRIGGER_GUN_HEADSHOT_HIT = "gun_headshot_hit";
    public static final String TRIGGER_GUN_HEADSHOT_KILL = "gun_headshot_kill";

    private static final String COUNTER_PREFIX = "tcc_rule_counter_";
    private static final ResourceLocation MY_ISLAND_ADVANCEMENT_ID = new ResourceLocation("tcc", "my_island");
    private static final int MY_ISLAND_STEPS = 30;
    private static final String MY_ISLAND_STEP_PREFIX = "step_";

    private static final String LAST_HEADSHOT_ATTACKER_KEY = "tcc_last_headshot_attacker";
    private static final String LAST_HEADSHOT_TIME_KEY = "tcc_last_headshot_time";
    private static final String LAST_PROCESSED_KILL_UUID_KEY = "tcc_last_processed_headshot_kill_uuid";
    private static final String LAST_PROCESSED_KILL_TIME_KEY = "tcc_last_processed_headshot_kill_time";

    private GunHeadshotEventHandler() {
    }

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

        handleTrigger(player, hurt, null, TRIGGER_GUN_HEADSHOT_HIT, false);
    }

    @SubscribeEvent
    public static void onGunHeadshotKill(EntityKillByGunEvent event) {
        LivingEntity attacker = event.getAttacker();
        if (!(attacker instanceof Player player)) return;
        if (player.level().isClientSide) return;

        LivingEntity killed = resolveKilled(event);
        if (killed == null) {
            return;
        }
        if (event.isHeadShot()) {
            triggerHeadshotKill(player, killed, null);
            return;
        }
        CompoundTag tag = killed.getPersistentData();
        if (!player.getStringUUID().equals(tag.getString(LAST_HEADSHOT_ATTACKER_KEY))) {
            return;
        }
        long lastHeadshotTime = tag.getLong(LAST_HEADSHOT_TIME_KEY);
        long now = player.level().getGameTime();
        if (now - lastHeadshotTime > 2) {
            return;
        }
        triggerHeadshotKill(player, killed, null);
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity killed = event.getEntity();
        if (killed.level().isClientSide) {
            return;
        }
        Entity sourceEntity = event.getSource().getEntity();
        if (!(sourceEntity instanceof Player player)) {
            return;
        }

        CompoundTag tag = killed.getPersistentData();
        if (!player.getStringUUID().equals(tag.getString(LAST_HEADSHOT_ATTACKER_KEY))) {
            return;
        }
        long lastHeadshotTime = tag.getLong(LAST_HEADSHOT_TIME_KEY);
        long now = player.level().getGameTime();
        if (now - lastHeadshotTime > 2) {
            return;
        }

        triggerHeadshotKill(player, killed, event.getSource());
    }

    private static void triggerHeadshotKill(Player player, LivingEntity killed, DamageSource source) {
        if (alreadyProcessedKill(player, killed)) {
            return;
        }
        progressMyIslandAdvancement(player);
        handleTrigger(player, killed, source, TRIGGER_GUN_HEADSHOT_KILL, true);
    }

    private static boolean alreadyProcessedKill(Player player, LivingEntity killed) {
        CompoundTag data = player.getPersistentData();
        String uuid = killed.getStringUUID();
        long now = player.level().getGameTime();
        if (uuid.equals(data.getString(LAST_PROCESSED_KILL_UUID_KEY)) && data.getLong(LAST_PROCESSED_KILL_TIME_KEY) == now) {
            return true;
        }
        data.putString(LAST_PROCESSED_KILL_UUID_KEY, uuid);
        data.putLong(LAST_PROCESSED_KILL_TIME_KEY, now);
        return false;
    }

    private static void handleTrigger(Player player, LivingEntity other, DamageSource source, String trigger, boolean allowCounters) {
        for (EvolutionRegistry.Rule rule : EvolutionRegistry.getRulesByTriggerOrEmpty(trigger)) {
            if (!rule.enabled) continue;
            if (rule.playerKilled) continue;
            if (!LivingDeathEventHandler.matchesDamageSource(rule, source)) continue;
            if (!matchesCommonRequirements(player, other, rule)) continue;

            if (rule.type == EvolutionRegistry.RuleType.GRANT) {
                if (!passesOncePerPlayerTag(player, rule)) {
                    continue;
                }
                if (!allowCounters || rule.requirements.kills.isEmpty()) {
                    LivingDeathEventHandler.executeGrant(player, rule);
                    continue;
                }
                if (applyAndCheckCounters(player, other, rule)) {
                    LivingDeathEventHandler.executeGrant(player, rule);
                }
            }
        }
    }

    private static void progressMyIslandAdvancement(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        Advancement adv = serverPlayer.server.getAdvancements().getAdvancement(MY_ISLAND_ADVANCEMENT_ID);
        if (adv == null) {
            return;
        }
        for (int i = 1; i <= MY_ISLAND_STEPS; i++) {
            String criterion = MY_ISLAND_STEP_PREFIX + i;
            if (serverPlayer.getAdvancements().award(adv, criterion)) {
                return;
            }
        }
    }

    private static boolean matchesCommonRequirements(Player player, LivingEntity other, EvolutionRegistry.Rule rule) {
        if (rule.grant == null) {
            return false;
        }

        if (rule.item != null && !rule.item.isBlank()) {
            if (!LivingDeathEventHandler.hasEquipped(player, rule.item)) {
                return false;
            }
        }

        for (String requiredCurio : rule.requirements.equippedCurios) {
            if (!LivingDeathEventHandler.hasEquipped(player, requiredCurio)) {
                return false;
            }
        }

        if (!LivingDeathEventHandler.passesExtraRequirements(player, other, rule.requirements)) {
            return false;
        }

        for (EvolutionRegistry.AttributeRequirement req : rule.requirements.attributes) {
            Attribute attr = AttributeHelper.resolveAttribute(req.attribute);
            if (attr == null) {
                return false;
            }
            double value = player.getAttributeValue(attr);
            if (!compare(value, req.comparator, req.value)) {
                return false;
            }
        }

        return true;
    }

    private static boolean applyAndCheckCounters(Player player, LivingEntity killed, EvolutionRegistry.Rule rule) {
        CompoundTag data = player.getPersistentData();
        for (EvolutionRegistry.KillRequirement req : rule.requirements.kills) {
            if (req == null || req.entity == null) continue;
            if (!matchesKilled(req, killed)) continue;
            String matchKey = "*".equals(req.entity.key) ? "*" : EntityConditionHelper.getMatchKey(req.entity.key, req.entity.nbt);
            String key = COUNTER_PREFIX + rule.ruleId + "_" + matchKey;
            data.putInt(key, data.getInt(key) + 1);
        }

        for (EvolutionRegistry.KillRequirement req : rule.requirements.kills) {
            if (req == null || req.entity == null) continue;
            String matchKey = "*".equals(req.entity.key) ? "*" : EntityConditionHelper.getMatchKey(req.entity.key, req.entity.nbt);
            String key = COUNTER_PREFIX + rule.ruleId + "_" + matchKey;
            if (data.getInt(key) < req.count) {
                return false;
            }
        }
        return true;
    }

    private static boolean matchesKilled(EvolutionRegistry.KillRequirement req, LivingEntity killed) {
        if ("*".equals(req.entity.key)) {
            return true;
        }
        if (killed == null) {
            return false;
        }
        String killedKey = BuiltInRegistries.ENTITY_TYPE.getKey(killed.getType()).toString();
        if (!killedKey.equals(req.entity.key)) {
            return false;
        }
        return EntityConditionHelper.matchesNbtFilters(killed, req.entity.nbt);
    }

    private static boolean passesOncePerPlayerTag(Player player, EvolutionRegistry.Rule rule) {
        String key = oncePerPlayerKey(rule);
        return key == null || !player.getPersistentData().getBoolean(key);
    }

    private static String oncePerPlayerKey(EvolutionRegistry.Rule rule) {
        if (rule == null || rule.grant == null) {
            return null;
        }
        return rule.grant.oncePerPlayer ? rule.ruleId : null;
    }

    private static boolean compare(double current, String comparator, double expected) {
        return switch (comparator) {
            case "gt" -> current > expected;
            case "gte" -> current >= expected;
            case "lt" -> current < expected;
            case "lte" -> current <= expected;
            case "eq" -> Double.compare(current, expected) == 0;
            case "ne" -> Double.compare(current, expected) != 0;
            default -> current >= expected;
        };
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
