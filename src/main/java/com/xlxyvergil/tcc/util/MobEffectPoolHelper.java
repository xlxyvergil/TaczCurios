package com.xlxyvergil.tcc.util;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 运行时枚举全部注册效果（BuiltInRegistries.MOB_EFFECT），按 MobEffectCategory 严格分为正面/负面/中性三类池；正面池支持配置 golden_buff_blacklist 黑名单过滤，负面池支持配置 discipline_buff_blacklist 黑名单过滤。
 */
public final class MobEffectPoolHelper {

    private MobEffectPoolHelper() {
    }

    public static List<Holder.Reference<MobEffect>> getAllBeneficialEffects() {
        return BuiltInRegistries.MOB_EFFECT.holders()
                .filter(holder -> holder.value().getCategory() == MobEffectCategory.BENEFICIAL)
                .toList();
    }

    public static List<Holder.Reference<MobEffect>> getAllHarmfulEffects() {
        return BuiltInRegistries.MOB_EFFECT.holders()
                .filter(holder -> holder.value().getCategory() == MobEffectCategory.HARMFUL)
                .toList();
    }

    public static List<Holder.Reference<MobEffect>> getAllNeutralEffects() {
        return BuiltInRegistries.MOB_EFFECT.holders()
                .filter(holder -> holder.value().getCategory() == MobEffectCategory.NEUTRAL)
                .toList();
    }

    /** 正面效果池（已过滤 golden_buff_blacklist 黑名单）。 */
    private static List<Holder.Reference<MobEffect>> getAllowedBeneficialEffects() {
        Set<ResourceLocation> blacklist = goldenBeneficialBlacklist();
        return BuiltInRegistries.MOB_EFFECT.holders()
                .filter(holder -> holder.value().getCategory() == MobEffectCategory.BENEFICIAL)
                .filter(holder -> !blacklist.contains(holder.key().location()))
                .toList();
    }

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

    /** 从正面池（含黑名单过滤）随机抽 1 个效果，池为空返回 null。 */
    @Nullable
    public static Holder<MobEffect> randomBeneficial(RandomSource random) {
        List<Holder.Reference<MobEffect>> pool = getAllowedBeneficialEffects();
        return pool.isEmpty() ? null : pool.get(random.nextInt(pool.size()));
    }

    /** 负面效果池（已过滤 discipline_buff_blacklist 黑名单）。 */
    private static List<Holder.Reference<MobEffect>> getAllowedHarmfulEffects() {
        Set<ResourceLocation> blacklist = disciplineHarmfulBlacklist();
        return BuiltInRegistries.MOB_EFFECT.holders()
                .filter(holder -> holder.value().getCategory() == MobEffectCategory.HARMFUL)
                .filter(holder -> !blacklist.contains(holder.key().location()))
                .toList();
    }

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

    /** 从负面池（含黑名单过滤）随机抽 1 个效果，池为空返回 null。 */
    @Nullable
    public static Holder<MobEffect> randomHarmful(RandomSource random) {
        List<Holder.Reference<MobEffect>> pool = getAllowedHarmfulEffects();
        return pool.isEmpty() ? null : pool.get(random.nextInt(pool.size()));
    }

    /**
     * 向目标施加效果（隐藏粒子）。
     */
    public static void applyEffect(LivingEntity target, Holder<MobEffect> effect, int durationTicks, int amplifier, @Nullable LivingEntity source) {
        if (target == null || effect == null) {
            return;
        }
        target.addEffect(new MobEffectInstance(effect, durationTicks, amplifier, false, false, true), source);
    }
}
