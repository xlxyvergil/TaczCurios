package com.xlxyvergil.tcc.evolution;

import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.tacz.guns.api.event.common.EntityKillByGunEvent;
import com.xlxyvergil.tcc.TaczCurios;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.LogicalSide;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class GunKillDebugFallbackHandler {
    private static final String PD_ATTACKER = "tcc_last_gun_attacker";
    private static final String PD_GUN_ID = "tcc_last_gun_id";
    private static final String PD_TICK = "tcc_last_gun_tick";
    private static final String PD_VICTIM = "tcc_last_gun_victim";
    private static final String PD_HANDLED_TICK = "tcc_gun_kill_handled_tick";

    private GunKillDebugFallbackHandler() {
    }

    @SubscribeEvent
    public static void onGunHurtPre(EntityHurtByGunEvent.Pre event) {
        if (event.getLogicalSide() != LogicalSide.SERVER) {
            return;
        }
        LivingEntity attacker = event.getAttacker();
        if (!(attacker instanceof ServerPlayer player)) {
            return;
        }
        if (!(player.level() instanceof ServerLevel)) {
            return;
        }

        LivingEntity hurt = resolveHurtEntity(event);
        if (hurt == null) {
            return;
        }

        var pd = hurt.getPersistentData();
        pd.putString(PD_ATTACKER, player.getStringUUID());
        pd.putString(PD_GUN_ID, event.getGunId() != null ? event.getGunId().toString() : "");
        pd.putLong(PD_TICK, player.level().getGameTime());
        pd.putString(PD_VICTIM, hurt.getStringUUID());
    }

    @SubscribeEvent
    public static void onGunKill(EntityKillByGunEvent event) {
        LivingEntity killed = event.getKilledEntity();
        if (killed == null) {
            return;
        }
        if (!(killed.level() instanceof ServerLevel level)) {
            return;
        }
        killed.getPersistentData().putLong(PD_HANDLED_TICK, level.getGameTime());
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getEntity().level() instanceof ServerLevel level)) {
            return;
        }
        LivingEntity killed = event.getEntity();

        var pd = killed.getPersistentData();
        if (!pd.contains(PD_ATTACKER) || !pd.contains(PD_TICK)) {
            return;
        }
        if (pd.contains(PD_VICTIM) && !killed.getStringUUID().equals(pd.getString(PD_VICTIM))) {
            return;
        }

        long tick = pd.getLong(PD_TICK);
        long now = level.getGameTime();
        if (now - tick > 40) {
            return;
        }
        if (pd.contains(PD_HANDLED_TICK) && now - pd.getLong(PD_HANDLED_TICK) <= 1) {
            return;
        }

        String attackerUuid = pd.getString(PD_ATTACKER);
        String gunIdStr = pd.getString(PD_GUN_ID);
        UUID uuid;
        try {
            uuid = UUID.fromString(attackerUuid);
        } catch (Exception ignored) {
            return;
        }
        ServerPlayer player = level.getServer().getPlayerList().getPlayer(uuid);
        if (player == null) {
            return;
        }

        ResourceLocation gunId = null;
        if (gunIdStr != null && !gunIdStr.isBlank()) {
            try {
                gunId = new ResourceLocation(gunIdStr);
            } catch (Exception ignored) {
                gunId = null;
            }
        }

        GunKillEventHandler.handleGunKill(player, killed, gunId);
    }

    private static LivingEntity resolveHurtEntity(EntityHurtByGunEvent.Pre event) {
        if (event.getHurtEntity() instanceof LivingEntity living) {
            return living;
        }
        if (event.getHurtEntity() instanceof net.minecraftforge.entity.PartEntity<?> part) {
            if (part.getParent() instanceof LivingEntity living) {
                return living;
            }
        }
        return null;
    }
}
