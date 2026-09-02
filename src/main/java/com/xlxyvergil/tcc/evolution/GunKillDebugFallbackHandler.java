package com.xlxyvergil.tcc.evolution;

import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.capability.GunKillDataCapability;
import com.xlxyvergil.tcc.compat.maid.MaidCompat;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.LogicalSide;



@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class GunKillDebugFallbackHandler {

    
    private static final long DEATH_WINDOW_TICKS = 40L;

    private GunKillDebugFallbackHandler() {
    }

    
    public static void refreshGunKillWindow(LivingEntity target, ServerPlayer attacker) {
        // 复用已有 gunId，避免虚数崩解 DoT 刷新窗口时丢失枪械类型判定（如 pistol）
        String gunId = "";
        GunKillDataCapability.GunKillData existing = GunKillDataCapability.getData(target);
        if (existing != null && existing.gunId != null) {
            gunId = existing.gunId;
        }
        GunKillDataCapability.setGunData(target,
            attacker.getStringUUID(), gunId, attacker.level().getGameTime(), target.getStringUUID());
    }

    @SubscribeEvent
    public static void onGunHurtPre(EntityHurtByGunEvent.Pre event) {
        if (event.getLogicalSide() != LogicalSide.SERVER) {
            return;
        }
        LivingEntity attacker = event.getAttacker();
        if (attacker == null) {
            return;
        }
        
        if (!(attacker instanceof ServerPlayer) && !MaidCompat.isMaid(attacker)) {
            return;
        }
        if (!(attacker.level() instanceof ServerLevel)) {
            return;
        }

        LivingEntity hurt = resolveHurtEntity(event);
        if (hurt == null) {
            return;
        }

        
        GunKillDataCapability.setGunData(hurt,
            MaidCompat.resolveAttackerUuid(attacker),
            event.getGunId() != null ? event.getGunId().toString() : "",
            attacker.level().getGameTime(),
            hurt.getStringUUID());
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getEntity().level() instanceof ServerLevel level)) {
            return;
        }
        LivingEntity killed = event.getEntity();

        
        GunKillDataCapability.GunKillData data = GunKillDataCapability.getData(killed);
        if (data == null) {
            return;
        }

        
        if (!killed.getStringUUID().equals(data.victim)) {
            return;
        }

        
        String attackerUuid = data.attacker;
        DamageSource source = event.getSource();
        Entity sourceEntity = source.getEntity();
        if (sourceEntity == null || !sourceEntity.getUUID().toString().equals(attackerUuid)) {
            return;
        }
        
        Player player = MaidCompat.resolveOwnerPlayer(sourceEntity);
        if (!(player instanceof ServerPlayer)) {
            return;
        }

        
        long now = level.getGameTime();
        if (now - data.tick > DEATH_WINDOW_TICKS) {
            return;
        }

        
        ResourceLocation gunId = null;
        if (!data.gunId.isBlank()) {
            try {
                gunId = new ResourceLocation(data.gunId);
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
