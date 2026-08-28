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
 * 运行时枚举全部注册效果（ForgeRegistries.MOB_EFFECTS），按 isBeneficial() 分正面/负面池（中性效果不进正面池）；正面池支持配置 golden_buff_blacklist 黑名单过滤。
 */
public final class MobEffectPoolHelper {

    private MobEffectPoolHelper() {
    }

    public static List<MobEffect> getAllBeneficialEffects() {
        return ForgeRegistries.MOB_EFFECTS.getValues().stream()
                .filter(MobEffect::isBeneficial)
                .toList();
    }

    public static List<MobEffect> getAllHarmfulEffects() {
        return ForgeRegistries.MOB_EFFECTS.getValues().stream()
                .filter(effect -> !effect.isBeneficial())
                .toList();
    }

    /** 正面效果池（已过滤 golden_buff_blacklist 黑名单）。 */
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
    public static MobEffect randomBeneficial(RandomSource random) {
        List<MobEffect> pool = getAllowedBeneficialEffects();
        return pool.isEmpty() ? null : pool.get(random.nextInt(pool.size()));
    }

    /** 负面效果池（已过滤 discipline_buff_blacklist 黑名单）。 */
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
