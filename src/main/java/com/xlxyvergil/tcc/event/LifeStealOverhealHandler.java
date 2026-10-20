package com.xlxyvergil.tcc.event;

import com.xlxyvergil.tcc.attribute.TccAttributes;
import com.xlxyvergil.tcc.core.TccDamageSources;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

/**
 * tcc:life_steal / tcc:overheal 的结算。
 *
 * <p>语义与原 Apothic Attributes 的实现保持一致：
 * <ul>
 *   <li>LIFE_STEAL：伤害的 x% 转化为攻击者治疗；</li>
 *   <li>OVERHEAL：伤害的 x% 转化为攻击者的伤害吸收（金心），上限为攻击者最大生命的一半。</li>
 * </ul>
 * 基数取 {@code min(本次伤害, 目标剩余血量)}，避免溢出倒杀的虚高转化。
 *
 * <p>两个入口：普通物理伤害走 {@link #onLivingHurt}；虚数伤害（含枪械子弹转换来的虚数伤害）
 * 走 {@link #applyImaginary}，由 {@link TccAttributeEvents#imaginaryDamageOnAttack} 在抗性结算后调用。
 *
 * <p>1.21.1 迁移说明：Forge 的 {@code LivingHurtEvent} 在 NeoForge 1.21 已由
 * {@link LivingIncomingDamageEvent} 取代（同为护甲减免前的受击入口，{@code getAmount()} 语义一致）。
 */
@EventBusSubscriber(modid = "tcc")
public final class LifeStealOverhealHandler {

    private LifeStealOverhealHandler() {
    }

    /**
     * 普通物理伤害入口：近战/生物攻击取 {@code directEntity}；TACZ 枪械子弹的 {@code directEntity}
     * 是弹体，退回到 {@code getEntity()}（射手）。箭等其它投掷物不在支持范围。
     * 虚数伤害（含枪械转换来的）走 {@link #applyImaginary}，在抗性结算后处理。
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurt(LivingIncomingDamageEvent event) {
        LivingEntity target = event.getEntity();
        if (target.level().isClientSide) {
            return;
        }
        DamageSource source = event.getSource();
        if (source.is(TccDamageSources.IMAGINARY_DAMAGE_TAG)) {
            return; // 虚数伤害在抗性结算后统一处理，避免用未结算的数值计算
        }
        LivingEntity attacker = resolveAttacker(source);
        if (attacker == null || attacker == target) {
            return;
        }
        if (!isPhysicalDamage(source)) {
            return;
        }
        apply(attacker, target, event.getAmount());
    }

    /** 近战/生物攻击取直接来源；TACZ 枪械子弹的直接来源是弹体，退回到射手。 */
    private static LivingEntity resolveAttacker(DamageSource source) {
        if (source.getDirectEntity() instanceof LivingEntity direct) {
            return direct;
        }
        if (TccDamageSources.isTaczBulletSource(source) && source.getEntity() instanceof LivingEntity shooter) {
            return shooter;
        }
        return null;
    }

    /**
     * 虚数伤害结算入口：覆盖近战虚数（directEntity 是攻击者）与枪械虚数（directEntity 是子弹）。
     * 必须在虚数抗性结算之后调用，否则会用未减伤的数值计算。
     */
    public static void applyImaginary(LivingEntity attacker, LivingEntity target, float finalDamage) {
        if (attacker == null || target == null || attacker == target) {
            return;
        }
        if (attacker.level().isClientSide) {
            return;
        }
        apply(attacker, target, finalDamage);
    }

    private static void apply(LivingEntity attacker, LivingEntity target, float damage) {
        if (damage <= 0) {
            return;
        }
        double base = Math.min(damage, target.getHealth());
        if (base <= 0) {
            return;
        }

        double lifeSteal = attacker.getAttributeValue(TccAttributes.LIFE_STEAL);
        if (lifeSteal > 0.001D) {
            attacker.heal((float) (base * lifeSteal));
        }

        double overheal = attacker.getAttributeValue(TccAttributes.OVERHEAL);
        double cap = attacker.getMaxHealth() * 0.5D;
        if (overheal > 0.0D && attacker.getAbsorptionAmount() < cap) {
            attacker.setAbsorptionAmount((float) Math.min(cap, attacker.getAbsorptionAmount() + base * overheal));
        }
    }

    /** 物理伤害判定：排除魔法、间接魔法、火焰与爆炸（与 Apothic Attributes 一致）。 */
    private static boolean isPhysicalDamage(DamageSource source) {
        return !source.is(DamageTypes.MAGIC)
                && !source.is(DamageTypes.INDIRECT_MAGIC)
                && !source.is(DamageTypeTags.IS_FIRE)
                && !source.is(DamageTypeTags.IS_EXPLOSION);
    }
}
