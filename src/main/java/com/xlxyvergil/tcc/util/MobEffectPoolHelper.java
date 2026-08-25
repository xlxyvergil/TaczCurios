package com.xlxyvergil.tcc.util;

import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 随机 buff / debuff 池工具。
 * <p>
 * 运行时动态枚举当前游戏注册的全部效果（ForgeRegistries.MOB_EFFECTS），
 * 按 {@link MobEffect#isBeneficial()} 分为正面池 / 负面池（中性效果不进入正面池）。
 */
public final class MobEffectPoolHelper {

    private MobEffectPoolHelper() {
    }

    /** 全部正面效果池 */
    public static List<MobEffect> getAllBeneficialEffects() {
        return ForgeRegistries.MOB_EFFECTS.getValues().stream()
                .filter(MobEffect::isBeneficial)
                .toList();
    }

    /** 全部非正面效果池（负面，用于随机 debuff） */
    public static List<MobEffect> getAllHarmfulEffects() {
        return ForgeRegistries.MOB_EFFECTS.getValues().stream()
                .filter(effect -> !effect.isBeneficial())
                .toList();
    }

    /** 从正面池随机抽取 1 个效果，池为空返回 null */
    @Nullable
    public static MobEffect randomBeneficial(RandomSource random) {
        List<MobEffect> pool = getAllBeneficialEffects();
        return pool.isEmpty() ? null : pool.get(random.nextInt(pool.size()));
    }

    /** 从负面池随机抽取 1 个效果，池为空返回 null */
    @Nullable
    public static MobEffect randomHarmful(RandomSource random) {
        List<MobEffect> pool = getAllHarmfulEffects();
        return pool.isEmpty() ? null : pool.get(random.nextInt(pool.size()));
    }

    /**
     * 向目标施加效果（隐藏粒子）。
     *
     * @param durationTicks 时长（tick）
     * @param amplifier     等级（0 = I 级）
     * @param source        施加来源（可为 null）
     */
    public static void applyEffect(LivingEntity target, MobEffect effect, int durationTicks, int amplifier, @Nullable LivingEntity source) {
        if (target == null || effect == null) {
            return;
        }
        target.addEffect(new MobEffectInstance(effect, durationTicks, amplifier, false, false, true), source);
    }
}
