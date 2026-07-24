package com.xlxyvergil.tcc.effect;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.core.TccDamageSources;
import com.xlxyvergil.tcc.evolution.GunKillDebugFallbackHandler;
import com.xlxyvergil.tcc.event.TccAttributeEvents;
import com.xlxyvergil.tcc.registries.TccMobEffects;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 虚数崩解 - 虚数侵染的流血效果。
 * 伤害公式: 目标最大血量 × percentPerLevel × 侵染等级 × (1 + min(debuff数, maxDebuff) × percentPerDebuff)
 * 仅可由天火劫灭/无烬终焉触发。
 * 负面效果数量增益仅在目标同时带有侵蚀效果时生效。
 */
public class ImaginaryCollapseEffect extends MobEffect {

    public ImaginaryCollapseEffect() {
        super(MobEffectCategory.NEUTRAL, 0x4B0082);
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return new ArrayList<>();
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide) return;

        int infectionLevel = 0;
        MobEffectInstance infection = entity.getEffect(TccMobEffects.IMAGINARY_INFECTION.get());
        if (infection != null) {
            infectionLevel = infection.getAmplifier() + 1;
        }
        if (infectionLevel <= 0) return;

        double percentPerLevel = TaczCuriosConfig.COMMON.collapsePercentPerLevel.get();
        double debuffMultiplier = 1.0;

        // 仅当目标带有侵蚀效果时，才统计负面效果数量增益
        if (entity.hasEffect(TccMobEffects.EROSION.get())) {
            int debuffCount = 0;
            for (MobEffectInstance instance : entity.getActiveEffects()) {
                if (instance.getEffect().getCategory() == MobEffectCategory.HARMFUL) {
                    debuffCount++;
                }
            }
            int maxDebuff = TaczCuriosConfig.COMMON.collapseMaxDebuffCount.get();
            double percentPerDebuff = TaczCuriosConfig.COMMON.collapsePercentPerDebuff.get();
            int effectiveDebuffs = Math.min(debuffCount, maxDebuff);
            debuffMultiplier = Math.round((1.0 + (1.0 + effectiveDebuffs) * percentPerDebuff) * 100.0) / 100.0;
        }

        float finalDamage = (float) ((float) Math.round(entity.getMaxHealth() * percentPerLevel * infectionLevel * debuffMultiplier * 100.0) / 100.0);

        if (finalDamage > 0) {
            // 从 NBT 读取侵染来源 attacker（由 TccAttributeEvents.applyImaginaryInfection 写入）
            LivingEntity attacker = resolveInfectionAttacker(entity);
            // 刷新枪杀判定窗口，确保虚数崩 DoT 击杀时能通过 onLivingDeath 的时间窗口校验
            if (attacker instanceof ServerPlayer sp) {
                GunKillDebugFallbackHandler.refreshGunKillWindow(entity, sp);
            }
            TccAttributeEvents.applyImaginaryDamage(
                entity,
                TccDamageSources.imaginaryDamage(entity.level(), attacker),
                finalDamage);
        }
    }

    /**
     * 从目标 NBT 读取虚数侵染来源 attacker。
     * 这样虚数崩击杀时，DamageSource.getEntity() 能返回正确的玩家，
     * 让 GunKillDebugFallbackHandler.onLivingDeath 的击杀者匹配校验通过。
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
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % 20 == 0;
    }
}