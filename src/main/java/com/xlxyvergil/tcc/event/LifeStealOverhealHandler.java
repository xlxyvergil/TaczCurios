package com.xlxyvergil.tcc.event;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.attribute.TccAttributes;
import com.xlxyvergil.tcc.core.TccDamageSources;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * tcc:life_steal / tcc:overheal 的结算。
 *
 * <p>语义：
 * <ul>
 *   <li>LIFE_STEAL：伤害的 x% 转化为攻击者治疗；</li>
 *   <li>OVERHEAL：伤害的 x% 转化为攻击者的伤害吸收（金心），上限为攻击者最大生命的一半。</li>
 * </ul>
 * 基数取 {@code min(本次伤害, 目标剩余血量)}，避免溢出倒杀的虚高转化。
 *
 * <p>对所有伤害类型生效，分两个入口：
 * <ul>
 *   <li>除虚数伤害外的所有伤害（物理、魔法、弹体、爆炸、火焰等）走 onLivingHurt；</li>
 *   <li>虚数伤害走 applyImaginary，由 TccAttributeEvents.imaginaryDamageOnAttack 在抗性结算后调用，
 *       既不使用未结算的数值，也不会与 onLivingHurt 重复结算。</li>
 * </ul>
 */
@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class LifeStealOverhealHandler {

    private LifeStealOverhealHandler() {
    }

    /**
     * 除虚数伤害外的所有伤害入口：近战/生物攻击取 {@code directEntity}；
     * 弹体、投掷物、法术等直接来源不是生物的，退回到 {@code getEntity()}（射手 / 施法者）。
     * 虚数伤害走 applyImaginary，在抗性结算后处理。
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurt(LivingHurtEvent event) {
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
        apply(attacker, target, event.getAmount());
    }

    /** 近战/生物攻击取直接来源；弹体、投掷物、法术等退回伤害归属实体（射手 / 施法者）。 */
    private static LivingEntity resolveAttacker(DamageSource source) {
        if (source.getDirectEntity() instanceof LivingEntity direct) {
            return direct;
        }
        return source.getEntity() instanceof LivingEntity owner ? owner : null;
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

        double lifeSteal = attacker.getAttributeValue(TccAttributes.LIFE_STEAL.get());
        if (lifeSteal > 0.001D) {
            attacker.heal((float) (base * lifeSteal));
        }

        double overheal = attacker.getAttributeValue(TccAttributes.OVERHEAL.get());
        double cap = attacker.getMaxHealth() * 0.5D;
        if (overheal > 0.0D && attacker.getAbsorptionAmount() < cap) {
            attacker.setAbsorptionAmount((float) Math.min(cap, attacker.getAbsorptionAmount() + base * overheal));
        }
    }
}
