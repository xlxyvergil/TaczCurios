package com.xlxyvergil.tcc.util;

import com.xlxyvergil.tcc.compat.maid.MaidCompat;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

import java.util.Set;
import java.util.function.Predicate;

/**
 * 通用免疫工具。
 * <p>
 * 把「弹射物无法命中 / 免疫指定伤害 / 免疫火焰效果」抽成与具体饰品无关的静态方法，
 * 「谁免疫」由调用方传入的 protector 判定，便于各饰品按需组合复用。
 * <p>
 * 相关饰品仅玩家/女仆可佩戴，故三个入口统一先做玩家/女仆判定：非玩家/女仆直接短路，
 * 不进入后续伤害匹配与饰品槽扫描。
 */
public final class ImmunityHelper {

    private ImmunityHelper() {
    }

    /** 可免疫的伤害与效果类别。 */
    public enum Type {
        /** 原版弹射物伤害。 */
        PROJECTILE,
        /** 火焰伤害：对应 is_fire 标签，已涵盖岩浆（lava）。 */
        FIRE,
        /** 窒息：头卡在方块内。 */
        SUFFOCATION,
        /** 溺水。 */
        DROWNING,
        /** 实体挤压。 */
        CRAMMING
    }

    /** 伤害源是否命中给定类别中的任意一项。 */
    public static boolean matches(DamageSource source, Set<Type> types) {
        for (Type type : types) {
            if (matches(source, type)) {
                return true;
            }
        }
        return false;
    }

    /** 伤害源是否命中给定类别。 */
    public static boolean matches(DamageSource source, Type type) {
        return switch (type) {
            case PROJECTILE -> source.is(DamageTypeTags.IS_PROJECTILE);
            case FIRE -> source.is(DamageTypeTags.IS_FIRE);
            case SUFFOCATION -> source.is(DamageTypes.IN_WALL);
            case DROWNING -> source.is(DamageTypeTags.IS_DROWNING);
            case CRAMMING -> source.is(DamageTypes.CRAMMING);
        };
    }

    /**
     * 弹射物无法命中：命中判定指向受保护实体时，让弹射物如同该实体不存在般穿过，不做任何命中处理。
     * 返回是否已跳过本次命中。
     */
    public static boolean skipProjectileHit(ProjectileImpactEvent event, Predicate<LivingEntity> protector) {
        Projectile projectile = event.getProjectile();
        if (projectile.level().isClientSide) {
            return false;
        }
        if (!(event.getRayTraceResult() instanceof EntityHitResult entityHit)) {
            return false;
        }
        if (!(entityHit.getEntity() instanceof LivingEntity target)) {
            return false;
        }
        // 先廉价短路非玩家/女仆，免去后续槽位扫描
        if (!MaidCompat.isPlayerOrMaid(target)) {
            return false;
        }
        if (!protector.test(target)) {
            return false;
        }
        event.setImpactResult(ProjectileImpactEvent.ImpactResult.SKIP_ENTITY);
        return true;
    }

    /**
     * 取消受保护实体受到的指定类别伤害；类别含 FIRE（已涵盖岩浆）时顺带灭火。
     * 返回是否已取消本次伤害。
     */
    public static boolean cancelDamage(LivingAttackEvent event, Set<Type> types, Predicate<LivingEntity> protector) {
        LivingEntity entity = event.getEntity();
        // 先廉价短路非玩家/女仆，免去后续伤害匹配与槽位扫描
        if (!MaidCompat.isPlayerOrMaid(entity)) {
            return false;
        }
        if (entity.level().isClientSide) {
            return false;
        }
        DamageSource source = event.getSource();
        if (!matches(source, types)) {
            return false;
        }
        if (!protector.test(entity)) {
            return false;
        }
        if (types.contains(Type.FIRE) && source.is(DamageTypeTags.IS_FIRE)) {
            entity.clearFire();
        }
        event.setCanceled(true);
        return true;
    }

    /**
     * 免疫火焰效果：受保护实体着火时立即熄灭。
     * 返回是否已熄灭。
     */
    public static boolean extinguishFire(LivingEntity entity, Predicate<LivingEntity> protector) {
        // 先廉价短路非玩家/女仆，免去后续判定与槽位扫描
        if (!MaidCompat.isPlayerOrMaid(entity)) {
            return false;
        }
        if (entity.level().isClientSide || !entity.isOnFire()) {
            return false;
        }
        if (!protector.test(entity)) {
            return false;
        }
        entity.clearFire();
        return true;
    }
}
