package com.xlxyvergil.tcc.evolution;

import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.capability.GunKillDataCapability;
import com.xlxyvergil.tcc.compat.maid.MaidCompat;
import com.xlxyvergil.tcc.core.TccDamageSources;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.LogicalSide;



@EventBusSubscriber(modid = TaczCurios.MODID)
public final class GunKillDebugFallbackHandler {

    private GunKillDebugFallbackHandler() {
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
        if (!(event.getEntity().level() instanceof ServerLevel)) {
            return;
        }
        LivingEntity killed = event.getEntity();

        // 致命来源必须为枪械伤害：TACZ 子弹（tacz:bullets）或枪械虚数伤害（精确 tcc:imaginary_damage，
        // 不含近战专属 tcc:imaginary_damage_melee）；以此取代旧的命中后时间窗口判定。
        DamageSource source = event.getSource();
        if (!isGunKillSource(source)) {
            return;
        }

        
        GunKillDataCapability.GunKillData data = GunKillDataCapability.getData(killed);
        if (data == null) {
            return;
        }

        
        if (!killed.getStringUUID().equals(data.victim)) {
            return;
        }

        
        String attackerUuid = data.attacker;
        Entity sourceEntity = source.getEntity();
        if (sourceEntity == null || !sourceEntity.getUUID().toString().equals(attackerUuid)) {
            return;
        }
        
        Player player = MaidCompat.resolveOwnerPlayer(sourceEntity);
        if (!(player instanceof ServerPlayer)) {
            return;
        }

        
        ResourceLocation gunId = null;
        if (!data.gunId.isBlank()) {
            try {
                gunId = ResourceLocation.parse(data.gunId);
            } catch (Exception ignored) {
                gunId = null;
            }
        }

        GunKillEventHandler.handleGunKill(player, killed, gunId);
    }

    /** 致命来源是否为枪械：TACZ 子弹伤害，或枪械虚数伤害（精确匹配，排除近战虚数类型）。 */
    private static boolean isGunKillSource(DamageSource source) {
        return source.is(TccDamageSources.TACZ_BULLETS_TAG)
            || source.is(TccDamageSources.IMAGINARY_DAMAGE);
    }

    private static LivingEntity resolveHurtEntity(EntityHurtByGunEvent.Pre event) {
        if (event.getHurtEntity() instanceof LivingEntity living) {
            return living;
        }
        if (event.getHurtEntity() instanceof net.neoforged.neoforge.entity.PartEntity<?> part) {
            if (part.getParent() instanceof LivingEntity living) {
                return living;
            }
        }
        return null;
    }
}
