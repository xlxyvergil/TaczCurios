package com.xlxyvergil.tcc.evolution;

import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.tacz.guns.api.event.common.EntityKillByGunEvent;
import com.xlxyvergil.tcc.compat.maid.MaidCompat;
import com.xlxyvergil.tcc.util.KillEventDedupe;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import java.lang.reflect.Method;

@EventBusSubscriber(modid = "tcc")
public final class GunHeadshotEventHandler {
    public static final String TRIGGER_GUN_HEADSHOT_HIT = "gun_headshot_hit";
    public static final String TRIGGER_GUN_HEADSHOT_KILL = "gun_headshot_kill";

    /** 多弹头去重：与 gun_kill 使用不同实例，保证两条触发器各自都能计到首次事件。 */
    private static final KillEventDedupe HEADSHOT_KILL_DEDUPE = new KillEventDedupe();

    private GunHeadshotEventHandler() {}

    
    @SubscribeEvent
    public static void onGunHeadshotHit(EntityHurtByGunEvent.Pre event) {
        if (!event.isHeadShot()) return;
        LivingEntity attacker = event.getAttacker();
        
        Player player = MaidCompat.resolveOwnerPlayer(attacker);
        if (player == null) return;
        if (player.level().isClientSide) return;

        LivingEntity hurt = resolveHurt(event);
        handleTrigger(player, hurt, event.getGunId(), TRIGGER_GUN_HEADSHOT_HIT);
    }

    /**
     * 爆头击杀主路径：TACZ 在致死只发 EntityKillByGunEvent，且事件自带 headshot 标记，
     * 因此直接判定，不再依赖爆头命中与死亡之间的时间窗。
     */
    @SubscribeEvent
    public static void onGunKill(EntityKillByGunEvent event) {
        if (event.getLogicalSide().isClient()) return;
        if (!event.isHeadShot()) return;

        LivingEntity attacker = event.getAttacker();
        LivingEntity killed = event.getKilledEntity();
        if (attacker == null || killed == null) return;

        Player player = MaidCompat.resolveOwnerPlayer(attacker);
        if (!(player instanceof ServerPlayer sp)) return;

        // 多弹头武器会在同一次击杀中重复触发该事件，保证一次爆头击杀只计一次
        if (HEADSHOT_KILL_DEDUPE.isDuplicate(killed.getUUID(), sp.level().getGameTime())) return;

        triggerHeadshotKill(sp, killed, event.getGunId());
    }

    private static void triggerHeadshotKill(ServerPlayer player, LivingEntity killed,
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
