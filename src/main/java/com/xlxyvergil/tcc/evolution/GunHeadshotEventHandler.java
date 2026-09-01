package com.xlxyvergil.tcc.evolution;

import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.xlxyvergil.tcc.capability.GunKillDataCapability;
import com.xlxyvergil.tcc.compat.maid.MaidCompat;
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

    
    private static final long DEATH_WINDOW_TICKS = 2L;

    private GunHeadshotEventHandler() {}

    
    @SubscribeEvent
    public static void onGunHeadshotHit(EntityHurtByGunEvent.Pre event) {
        if (!event.isHeadShot()) return;
        LivingEntity attacker = event.getAttacker();
        
        Player player = MaidCompat.resolveOwnerPlayer(attacker);
        if (player == null) return;
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

    
    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity killed = event.getEntity();
        if (killed.level().isClientSide) return;

        DamageSource source = event.getSource();
        Entity sourceEntity = source.getEntity();
        
        Player player = MaidCompat.resolveOwnerPlayer(sourceEntity);
        if (player == null) return;

        var cap = killed.getCapability(GunKillDataCapability.CAPABILITY);
        if (!cap.isPresent()) return;
        var data = cap.orElse(null).data();
        if (!player.getStringUUID().equals(data.headshotAttacker)) return;
        if (player.level().getGameTime() - data.headshotTime > DEATH_WINDOW_TICKS) return;

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

    private static void handleTrigger(Player player, LivingEntity other,
                                       net.minecraft.resources.ResourceLocation gunId, String trigger) {
        
        if (TRIGGER_GUN_HEADSHOT_KILL.equals(trigger) && gunId == null) return;
        ServerPlayer serverPlayer = player instanceof ServerPlayer sp ? sp : null;
        if (serverPlayer == null) return;

        for (AchievementDefinitions.AchievementDef def : AchievementDefinitions.getByTrigger(trigger)) {
            if (!def.isEnabled()) continue;

            if (!RuleAdvancementMapping.arePrerequisitesMet(serverPlayer, def)) continue;

            if (RuleAdvancementMapping.isAdvancementDone(serverPlayer, def.id())) continue;

            if (!AchievementConditionMatcher.matchesKillConditions(player, other, gunId, def)) continue;

            
            var kills = def.conditions() != null ? def.conditions().kills() : null;
            if (kills != null && kills.size() > 1) {
                if (other == null) continue;
                RuleAdvancementMapping.awardMultiTypeKill(
                        serverPlayer, def.id(), def, other, 1);
            } else {
                RuleAdvancementMapping.awardSteps(
                        serverPlayer, def.id(), def.targetCount(), 1);
            }
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
