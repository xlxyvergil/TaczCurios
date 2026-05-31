package com.xlxyvergil.tcc.registries;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.effect.HeavenFireApocalypseBuffEffect;
import com.xlxyvergil.tcc.effect.HeavenFireApocalypseDelayEffect;
import com.xlxyvergil.tcc.effect.HeavenFireBleedingEffect;
import com.xlxyvergil.tcc.effect.ImaginaryCollapseEffect;
import com.xlxyvergil.tcc.effect.ImaginaryInfectionEffect;
import com.xlxyvergil.tcc.effect.ArgonScopeEffect;
import com.xlxyvergil.tcc.effect.GildedArgonScopeEffect;
import com.xlxyvergil.tcc.effect.LaserScopeEffect;
import com.xlxyvergil.tcc.effect.HydraulicCrosshairEffect;
import com.xlxyvergil.tcc.effect.GildedHydraulicCrosshairEffect;
import com.xlxyvergil.tcc.effect.SharpBulletEffect;
import com.xlxyvergil.tcc.effect.FragmentShotEffect;
import com.xlxyvergil.tcc.effect.SharpAmmoEffect;
import com.xlxyvergil.tcc.effect.GildedSteelSlashEffect;
import com.xlxyvergil.tcc.effect.GildedSplitChamberEffect;
import com.xlxyvergil.tcc.effect.GildedInfernalChamberEffect;
import com.xlxyvergil.tcc.effect.GildedBulletSpreadEffect;

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

    // ========== Phase 3: 新增Buff效果 ==========

    public static final RegistryObject<MobEffect> ARGON_SCOPE = MOB_EFFECTS.register(
            "argon_scope",
            ArgonScopeEffect::new);

    public static final RegistryObject<MobEffect> GILDED_ARGON_SCOPE = MOB_EFFECTS.register(
            "gilded_argon_scope",
            GildedArgonScopeEffect::new);

    public static final RegistryObject<MobEffect> LASER_SCOPE = MOB_EFFECTS.register(
            "laser_scope",
            LaserScopeEffect::new);

    public static final RegistryObject<MobEffect> HYDRAULIC_CROSSHAIR = MOB_EFFECTS.register(
            "hydraulic_crosshair",
            HydraulicCrosshairEffect::new);

    public static final RegistryObject<MobEffect> GILDED_HYDRAULIC_CROSSHAIR = MOB_EFFECTS.register(
            "gilded_hydraulic_crosshair",
            GildedHydraulicCrosshairEffect::new);

    public static final RegistryObject<MobEffect> SHARP_BULLET = MOB_EFFECTS.register(
            "sharp_bullet",
            SharpBulletEffect::new);

    public static final RegistryObject<MobEffect> FRAGMENT_SHOT = MOB_EFFECTS.register(
            "fragment_shot",
            FragmentShotEffect::new);

    public static final RegistryObject<MobEffect> SHARP_AMMO = MOB_EFFECTS.register(
            "sharp_ammo",
            SharpAmmoEffect::new);

    public static final RegistryObject<MobEffect> GILDED_STEEL_SLASH = MOB_EFFECTS.register(
            "gilded_steel_slash",
            GildedSteelSlashEffect::new);

    public static final RegistryObject<MobEffect> GILDED_SPLIT_CHAMBER = MOB_EFFECTS.register(
            "gilded_split_chamber",
            GildedSplitChamberEffect::new);

    public static final RegistryObject<MobEffect> GILDED_INFERNAL_CHAMBER = MOB_EFFECTS.register(
            "gilded_infernal_chamber",
            GildedInfernalChamberEffect::new);

    public static final RegistryObject<MobEffect> GILDED_BULLET_SPREAD = MOB_EFFECTS.register(
            "gilded_bullet_spread",
            GildedBulletSpreadEffect::new);

}