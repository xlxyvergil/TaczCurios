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


/**
 * 统一的枪杀判定处理器（fallback）：onGunHurtPre 把枪伤信息写入 GunKillDataCapability，onLivingDeath 统一校验
 * （死亡源、victim 一致、40 tick 时间窗）。用 Capability 兼容 getPersistentData() 返回空 NBT 的实体。
 */
@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class GunKillDebugFallbackHandler {

    /** 枪伤 → 死亡的时间窗口（tick）。需要覆盖枪伤残血 → 近战/虚数伤害收割的场景。 */
    private static final long DEATH_WINDOW_TICKS = 40L;

    private GunKillDebugFallbackHandler() {
    }

    /**
     * 刷新枪杀判定窗口。用于虚数崩等 DoT 效果，确保 DoT 击杀仍能通过时间窗口校验。
     */
    public static void refreshGunKillWindow(LivingEntity target, ServerPlayer attacker) {
        GunKillDataCapability.setGunData(target,
            attacker.getStringUUID(), "", attacker.level().getGameTime(), target.getStringUUID());
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
        // 攻击者可以是玩家本体，也可以是佩戴者（主人）的女仆
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

        // 女仆击杀时记女仆 UUID（其本体即死亡源实体），玩家击杀时记玩家 UUID
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

        //  检查 Capability 中的枪伤记录（由 onGunHurtPre 写入）
        GunKillDataCapability.GunKillData data = GunKillDataCapability.getData(killed);
        if (data == null) {
            return;
        }

        //  victim 一致性校验
        if (!killed.getStringUUID().equals(data.victim)) {
            return;
        }

        //  死亡源校验：击杀者必须是 Capability 中记录的枪伤来源（玩家或佩戴者的女仆）
        String attackerUuid = data.attacker;
        DamageSource source = event.getSource();
        Entity sourceEntity = source.getEntity();
        if (sourceEntity == null || !sourceEntity.getUUID().toString().equals(attackerUuid)) {
            return;
        }
        //  女仆击杀时归属到其主人玩家，玩家击杀时归属玩家本体
        Player player = MaidCompat.resolveOwnerPlayer(sourceEntity);
        if (!(player instanceof ServerPlayer)) {
            return;
        }

        // 时间窗口校验
        long now = level.getGameTime();
        if (now - data.tick > DEATH_WINDOW_TICKS) {
            return;
        }

        //  解析 gunId
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
