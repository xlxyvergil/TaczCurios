package com.xlxyvergil.tcc.evolution;

import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.capability.GunKillDataCapability;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.LogicalSide;


/**
 * 统一的枪杀判定处理器。
 *
 * 架构说明：
 * - {@link #onGunHurtPre} 监听 {@link EntityHurtByGunEvent.Pre}，将枪伤信息
 *   (attacker UUID / gunId / tick / victim UUID) 写入 GunKillDataCapability。
 * - {@link #onLivingDeath} 监听 {@link LivingDeathEvent}，对所有实体统一判定：
 *     1. 死亡源校验：只接受 tacz:bullets（枪械直伤）或 tcc:imaginary_damage（虚数伤害）；
 *     2. GunKillDataCapability 校验：确认死亡前曾被枪械伤害，且 victim 一致；
 *     3. 时间窗口（40 tick）：枪伤与死亡的时间差。
 * - 使用 Capability 而非 NBT，以兼容 {@code getPersistentData()} 被重写返回空 NBT 的实体
 *   （如 RevelationFix 的 Apollyon）。
 */
@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class GunKillDebugFallbackHandler {

    /** 枪伤 → 死亡的时间窗口（tick）。需要覆盖枪伤残血 → 近战/虚数伤害收割的场景。 */
    private static final long DEATH_WINDOW_TICKS = 40L;

    private GunKillDebugFallbackHandler() {
    }

    /**
     * 刷新枪杀判定窗口。用于虚数崩等 DoT 效果，确保 DoT 击杀时仍能通过时间窗口校验。
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

        GunKillDataCapability.setGunData(hurt,
            player.getStringUUID(),
            event.getGunId() != null ? event.getGunId().toString() : "",
            player.level().getGameTime(),
            hurt.getStringUUID());
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getEntity().level() instanceof ServerLevel level)) {
            return;
        }
        LivingEntity killed = event.getEntity();

        // ① 检查 Capability 中的枪伤记录（由 onGunHurtPre 写入）
        GunKillDataCapability.GunKillData data = GunKillDataCapability.getData(killed);
        if (data == null) {
            return;
        }

        // ② victim 一致性校验
        if (!killed.getStringUUID().equals(data.victim)) {
            return;
        }

        // ③ 死亡源校验：击杀者必须是 Capability 中记录的枪伤来源玩家
        String attackerUuid = data.attacker;
        DamageSource source = event.getSource();
        Entity sourceEntity = source.getEntity();
        if (sourceEntity == null || !sourceEntity.getUUID().toString().equals(attackerUuid)) {
            return;
        }
        if (!(sourceEntity instanceof ServerPlayer player)) {
            return;
        }

        // ④ 时间窗口校验
        long now = level.getGameTime();
        if (now - data.tick > DEATH_WINDOW_TICKS) {
            return;
        }

        // ⑤ 解析 gunId
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
