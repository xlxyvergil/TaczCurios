package com.xlxyvergil.tcc.event;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.items.curios.bound.ZhenWo;
import com.xlxyvergil.tcc.util.ImmunityHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.EnumSet;
import java.util.Set;

/**
 * 真我（ZhenWo）佩戴者的额外免疫统一监听器。
 * 仅对玩家与女仆生效，佩戴真我后常驻：
 * 原版弹射物根本无法命中（如同佩戴者不存在般穿过）、弹射物伤害、
 * 火焰伤害与火焰效果、窒息（卡方块）与溺水伤害、实体挤压伤害。
 */
@Mod.EventBusSubscriber(modid = TaczCurios.MODID)
public class ZhenWoImmunityProtectionHandler {

    /** 真我提供的免疫类别。 */
    private static final Set<ImmunityHelper.Type> IMMUNITIES = EnumSet.of(
            ImmunityHelper.Type.PROJECTILE,
            ImmunityHelper.Type.FIRE,
            ImmunityHelper.Type.SUFFOCATION,
            ImmunityHelper.Type.DROWNING,
            ImmunityHelper.Type.CRAMMING);

    /** 是否佩戴真我（调用方已先过滤玩家/女仆）。 */
    private static boolean isProtected(LivingEntity entity) {
        return ZhenWo.isEquipped(entity);
    }

    /** 弹射物无法命中：命中判定指向佩戴者时，让弹射物如同该实体不存在般穿过。 */
    @SubscribeEvent
    public static void onProjectileImpact(ProjectileImpactEvent event) {
        ImmunityHelper.skipProjectileHit(event, ZhenWoImmunityProtectionHandler::isProtected);
    }

    /** 免疫弹射物伤害、火焰伤害、窒息/溺水伤害、实体挤压伤害。 */
    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event) {
        ImmunityHelper.cancelDamage(event, IMMUNITIES, ZhenWoImmunityProtectionHandler::isProtected);
    }

    /** 免疫火焰效果：持续熄灭佩戴者身上的火。 */
    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        ImmunityHelper.extinguishFire(event.getEntity(), ZhenWoImmunityProtectionHandler::isProtected);
    }
}
