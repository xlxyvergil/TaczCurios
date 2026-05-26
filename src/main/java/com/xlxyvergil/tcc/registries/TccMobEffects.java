package com.xlxyvergil.tcc.registries;

import com.xlxyvergil.taa.attribute.EntityAttributeRegistry;
import com.xlxyvergil.taa.effect.TaaAttributeMobEffect;
import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.effect.HeavenFireApocalypseDelayEffect;
import com.xlxyvergil.tcc.effect.HeavenFireBleedingEffect;
import com.xlxyvergil.tcc.effect.ImaginaryBleedingEffect;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class TccMobEffects {

    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, TaczCurios.MODID);

    public static final RegistryObject<MobEffect> HEAVEN_FIRE_APOCALYPSE_BUFF = MOB_EFFECTS.register(
            "heaven_fire_apocalypse_buff",
            () -> {
                // 从配置读取加成百分比，转换为每级值
                double configPercent = TaczCuriosConfig.COMMON.heavenFireApocalypseNearbyPlayerPotionAmplifier.get() + 1;
                return TaaAttributeMobEffect.builder()
                        .attribute(EntityAttributeRegistry.BULLET_GUNDAMAGE.get())
                        .uuid("ba764054-9ad7-493e-b1bd-a6def116012d")
                        .perLevelValue(configPercent / 100.0)  // 转换为小数（如100% → 1.0）
                        .category(MobEffectCategory.BENEFICIAL)
                        .color(0xFF5555)
                        .icon(new ResourceLocation(TaczCurios.MODID, "heaven_fire_apocalypse_buff"))
                        .build();
            });
    
    /**
     * 天火流血效果 - 基于最大生命值的百分比伤害
     */
    public static final RegistryObject<MobEffect> HEAVEN_FIRE_BLEEDING = MOB_EFFECTS.register(
            "heaven_fire_bleeding",
            HeavenFireBleedingEffect::new);
    
    /**
     * 虚数流血效果 - 基于最大生命值的百分比伤害，根据目标流血层数增伤
     */
    public static final RegistryObject<MobEffect> IMAGINARY_BLEEDING = MOB_EFFECTS.register(
            "imaginary_bleeding",
            ImaginaryBleedingEffect::new);
    
    /**
     * 天火劫灭延迟标记 - 用于在扣血后延迟施加流血效果
     */
    public static final RegistryObject<MobEffect> HEAVEN_FIRE_APOCALYPSE_DELAY = MOB_EFFECTS.register(
            "heaven_fire_apocalypse_delay",
            HeavenFireApocalypseDelayEffect::new);
}
