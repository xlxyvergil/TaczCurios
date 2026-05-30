package com.xlxyvergil.tcc.registries;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.effect.HeavenFireApocalypseBuffEffect;
import com.xlxyvergil.tcc.effect.HeavenFireApocalypseDelayEffect;
import com.xlxyvergil.tcc.effect.HeavenFireBleedingEffect;
import com.xlxyvergil.tcc.effect.ImaginaryCollapseEffect;
import com.xlxyvergil.tcc.effect.ImaginaryInfectionEffect;

import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class TccMobEffects {

    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, TaczCurios.MODID);

    public static final RegistryObject<MobEffect> HEAVEN_FIRE_APOCALYPSE_BUFF = MOB_EFFECTS.register(
            "heaven_fire_apocalypse_buff",
            HeavenFireApocalypseBuffEffect::new);
    
    /**
     * 天火流血效果 - 基于最大生命值的百分比伤害
     */
    public static final RegistryObject<MobEffect> HEAVEN_FIRE_BLEEDING = MOB_EFFECTS.register(
            "heaven_fire_bleeding",
            HeavenFireBleedingEffect::new);
    
    /**
     * 虚数侵染效果 - 纯标记效果，降低虚数抗性，伤害由虚数崩解处理
     */
    public static final RegistryObject<MobEffect> IMAGINARY_INFECTION = MOB_EFFECTS.register(
            "imaginary_infection",
            ImaginaryInfectionEffect::new);
    
    /**
     * 虚数崩解效果 - 基于虚数侵染等级的百分比流血伤害
     */
    public static final RegistryObject<MobEffect> IMAGINARY_COLLAPSE = MOB_EFFECTS.register(
            "imaginary_collapse",
            ImaginaryCollapseEffect::new);
    
    /**
     * 天火劫灭延迟标记 - 用于在扣血后延迟施加流血效果
     */
    public static final RegistryObject<MobEffect> HEAVEN_FIRE_APOCALYPSE_DELAY = MOB_EFFECTS.register(
            "heaven_fire_apocalypse_delay",
            HeavenFireApocalypseDelayEffect::new);
}