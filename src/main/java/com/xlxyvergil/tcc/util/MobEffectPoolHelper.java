package com.xlxyvergil.tcc.util;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 随机 buff / debuff 池工具。
 * 运行时枚举当前游戏注册的全部效果（ForgeRegistries.MOB_EFFECTS），按 isBeneficial() 分为正面/负面池（中性效果不进正面池）。
 * 正面池支持黑名单过滤（配置 golden_buff_blacklist），命中的效果不会被随机施加。
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

    /**
     * 正面效果池（已过滤黄金系列黑名单）。
     * 黑名单读取自配置 golden_buff_blacklist，命中的效果不会参与随机抽取。
     */
    private static List<MobEffect> getAllowedBeneficialEffects() {
        Set<ResourceLocation> blacklist = goldenBeneficialBlacklist();
        return ForgeRegistries.MOB_EFFECTS.getValues().stream()
                .filter(MobEffect::isBeneficial)
                .filter(effect -> {
                    ResourceLocation key = ForgeRegistries.MOB_EFFECTS.getKey(effect);
                    return key == null || !blacklist.contains(key);
                })
                .toList();
    }

    /** 读取黄金系列正面 buff 黑名单（效果注册名集合） */
    private static Set<ResourceLocation> goldenBeneficialBlacklist() {
        Set<ResourceLocation> set = new HashSet<>();
        List<? extends String> list = TaczCuriosConfig.COMMON.goldenBeneficialBuffBlacklist.get();
        if (list == null) {
            return set;
        }
        for (String id : list) {
            ResourceLocation loc = ResourceLocation.tryParse(id);
            if (loc != null) {
                set.add(loc);
            }
        }
        return set;
    }

    /** 从正面池（含黑名单过滤）随机抽取 1 个效果，池为空返回 null */
    @Nullable
    public static MobEffect randomBeneficial(RandomSource random) {
        List<MobEffect> pool = getAllowedBeneficialEffects();
        return pool.isEmpty() ? null : pool.get(random.nextInt(pool.size()));
    }

    /**
     * 负面效果池（已过滤戒律系列黑名单）。
     * 黑名单读取自配置 discipline_buff_blacklist，命中的效果不会参与随机抽取。
     */
    private static List<MobEffect> getAllowedHarmfulEffects() {
        Set<ResourceLocation> blacklist = disciplineHarmfulBlacklist();
        return ForgeRegistries.MOB_EFFECTS.getValues().stream()
                .filter(effect -> !effect.isBeneficial())
                .filter(effect -> {
                    ResourceLocation key = ForgeRegistries.MOB_EFFECTS.getKey(effect);
                    return key == null || !blacklist.contains(key);
                })
                .toList();
    }

    /** 读取戒律系列负面效果黑名单（效果注册名集合） */
    private static Set<ResourceLocation> disciplineHarmfulBlacklist() {
        Set<ResourceLocation> set = new HashSet<>();
        List<? extends String> list = TaczCuriosConfig.COMMON.disciplineHarmfulBuffBlacklist.get();
        if (list == null) {
            return set;
        }
        for (String id : list) {
            ResourceLocation loc = ResourceLocation.tryParse(id);
            if (loc != null) {
                set.add(loc);
            }
        }
        return set;
    }

    /** 从负面池（含黑名单过滤）随机抽取 1 个效果，池为空返回 null */
    @Nullable
    public static MobEffect randomHarmful(RandomSource random) {
        List<MobEffect> pool = getAllowedHarmfulEffects();
        return pool.isEmpty() ? null : pool.get(random.nextInt(pool.size()));
    }

    /**
     * 向目标施加效果（隐藏粒子）。
     */
    public static void applyEffect(LivingEntity target, MobEffect effect, int durationTicks, int amplifier, @Nullable LivingEntity source) {
        if (target == null || effect == null) {
            return;
        }
        target.addEffect(new MobEffectInstance(effect, durationTicks, amplifier, false, false, true), source);
    }
}
