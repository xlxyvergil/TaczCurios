package com.xlxyvergil.tcc.effect;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.core.TccDamageSources;
import com.xlxyvergil.tcc.event.TccAttributeEvents;
import com.xlxyvergil.tcc.registries.TccMobEffects;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.EffectCure;

import java.util.Set;
import java.util.UUID;

/**
 * 虚数崩解流血效果：每秒造成 目标最大血量 × percentPerLevel 的虚数伤害；
 * 侵染等级仅用于判定是否触发崩解，不参与伤害放大。抗性结算统一由虚数伤害通用入口处理。
 */
public class ImaginaryCollapseEffect extends MobEffect {

    public ImaginaryCollapseEffect() {
        super(MobEffectCategory.NEUTRAL, 0x4B0082);
    }

    /**
     * 不提供任何 cure，阻止 Goety/Warlock/Codger 等 boss 的剥离机制。
     */
    @Override
    public void fillEffectCures(Set<EffectCure> cures, MobEffectInstance effectInstance) {
        // 留空即表示不可被任何 cure 解除
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide) return true;

        int infectionLevel = 0;
        MobEffectInstance infection = entity.getEffect(TccMobEffects.IMAGINARY_INFECTION);
        if (infection != null) {
            infectionLevel = infection.getAmplifier() + 1;
        }
        if (infectionLevel <= 0) return true;

        double percentPerLevel = TaczCuriosConfig.COMMON.collapsePercentPerLevel.get();

        float finalDamage = (float) ((float) Math.round(entity.getMaxHealth() * percentPerLevel * 10000.0) / 10000.0);

        if (finalDamage > 0) {
            // 从 NBT 读取侵染来源 attacker（由 TccAttributeEvents.applyImaginaryInfection 写入）
            LivingEntity attacker = resolveInfectionAttacker(entity);
            TccAttributeEvents.applyCollapseDamage(
                entity,
                TccDamageSources.imaginaryDamage(entity.level(), attacker),
                finalDamage);
        }
        return true;
    }

    /**
     * 从目标 NBT 读取虚数侵染来源，使击杀结算时 DamageSource.getEntity() 能返回正确玩家。
     */
    private static LivingEntity resolveInfectionAttacker(LivingEntity entity) {
        if (!(entity.level() instanceof ServerLevel sl)) return null;
        String uuidStr = entity.getPersistentData().getString(TccAttributeEvents.INFECTION_ATTACKER_KEY);
        if (uuidStr.isEmpty()) return null;
        try {
            UUID uuid = UUID.fromString(uuidStr);
            ServerPlayer player = sl.getServer().getPlayerList().getPlayer(uuid);
            return player;
        } catch (Exception ignored) {
            return null;
        }
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % 20 == 0;
    }
}