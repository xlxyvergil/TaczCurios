package com.xlxyvergil.tcc.config;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.xlxyvergil.tcc.TaczCurios;
import net.neoforged.fml.config.ConfigTracker;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.config.ModConfigs;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class TaczCuriosConfig {
    public static final ModConfigSpec COMMON_SPEC;
    public static final Common COMMON;
    
    static {
        final Pair<Common, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(Common::new);
        COMMON_SPEC = specPair.getRight();
        COMMON = specPair.getLeft();
    }

    /**
     * 安全读取配置值：配置尚未加载时返回默认值。
     *
     * <p>MOB_EFFECT 注册发生在 {@code registerConfig} 排队加载之前，而
     * {@code MobEffect.addAttributeModifier(Holder, ResourceLocation, Operation, Int2DoubleFunction)}
     * 会在注册当场以 amplifier = 0 试算一次曲线，此时直接调用 {@code get()} 会抛
     * "Cannot get config value before config is loaded."。
     * <p>该试算结果只写入 {@code AttributeTemplate.amount}，运行期曲线非空时该字段不参与取值，
     * 因此以默认值兜底不影响实际效果；运行期曲线求值仍会读到这里返回的真实配置值。
     */
    public static double getOrDefault(ModConfigSpec.ConfigValue<Double> value) {
        return COMMON_SPEC.isLoaded() ? value.get() : value.getDefault();
    }
    
    public static class Common {
        
        
        public final ModConfigSpec.ConfigValue<List<? extends String>> fishFoodItems;

        
        public final ModConfigSpec.DoubleValue heavenFireJudgmentDamageBoost;
        public final ModConfigSpec.DoubleValue heavenFireJudgmentHealthCost;
        
        
        public final ModConfigSpec.DoubleValue heavenFireBleedingDamagePerLevel;
        public final ModConfigSpec.IntValue heavenFireBleedingMaxLevel;
        public final ModConfigSpec.IntValue heavenFireBleedingDuration;
        public final ModConfigSpec.IntValue heavenFireApocalypseDelayDuration;
        
        
        public final ModConfigSpec.DoubleValue imaginaryDamageAttackAmplification;
        public final ModConfigSpec.BooleanValue imaginaryDamageUseSetHealth;
        public final ModConfigSpec.DoubleValue imaginaryInfectionAmpPerLevel;
        public final ModConfigSpec.IntValue imaginaryInfectionMaxLevel;
        public final ModConfigSpec.IntValue imaginaryInfectionDuration;
        public final ModConfigSpec.DoubleValue imaginaryInfectionResistanceReduction;
        
        
        public final ModConfigSpec.IntValue tier1ImaginaryInfectionMaxLevel;
        public final ModConfigSpec.IntValue tier2ImaginaryInfectionMaxLevel;
        public final ModConfigSpec.IntValue tier3ImaginaryInfectionMaxLevel;
        public final ModConfigSpec.IntValue specialImaginaryInfectionMaxLevel;
        public final ModConfigSpec.DoubleValue heiyuanBaihuaImaginaryDamageScale;

        
        public final ModConfigSpec.BooleanValue tier1CurioRemovable;
        public final ModConfigSpec.BooleanValue tier2CurioRemovable;
        public final ModConfigSpec.BooleanValue tier3CurioRemovable;
        public final ModConfigSpec.BooleanValue specialCurioRemovable;

        
        public final ModConfigSpec.DoubleValue judgementProcChance;
        public final ModConfigSpec.DoubleValue judgementDirectDamagePercent;
        public final ModConfigSpec.DoubleValue judgementCollapseProcChance;
        public final ModConfigSpec.DoubleValue judgementKeyCritChance;
        public final ModConfigSpec.DoubleValue judgementKeyCritDamage;

        
        public final ModConfigSpec.DoubleValue sevenThundersHeadshotMultiplier;
        public final ModConfigSpec.DoubleValue sevenThundersCritChance;
        public final ModConfigSpec.DoubleValue sevenThundersCritDamage;

        
        public final ModConfigSpec.DoubleValue sevenThundersThunderSeenHeadshotMultiplier;
        public final ModConfigSpec.DoubleValue sevenThundersThunderSeenCritChance;
        public final ModConfigSpec.DoubleValue sevenThundersThunderSeenCritDamage;
        public final ModConfigSpec.DoubleValue sevenThundersThunderSeenProcChance;
        public final ModConfigSpec.DoubleValue sevenThundersThunderSeenExtraHpDamage;

        
        public final ModConfigSpec.DoubleValue heavenFireApocalypseDamageBoost;
        public final ModConfigSpec.DoubleValue heavenFireApocalypseExplosionRadius;
        public final ModConfigSpec.DoubleValue heavenFireApocalypseExplosionDamage;
        public final ModConfigSpec.DoubleValue heavenFireApocalypseHealthCost;
        public final ModConfigSpec.DoubleValue brahmaBeastsHealthCostReduction;
        public final ModConfigSpec.DoubleValue heavenFireApocalypseNearbyPlayerDamageBoost;
        public final ModConfigSpec.IntValue heavenFireApocalypseNearbyPlayerPotionAmplifier;
        public final ModConfigSpec.IntValue heavenFireApocalypseNearbyPlayerDuration;
        public final ModConfigSpec.DoubleValue heavenFireApocalypseNearbyPlayerRadius;
        
        
        public final ModConfigSpec.DoubleValue riflingDamageBoost;
        
        
        public final ModConfigSpec.DoubleValue splitChamberBulletCountBoost;
        
        
        public final ModConfigSpec.DoubleValue despicableAccelerationFireRateBoost;
        public final ModConfigSpec.DoubleValue despicableAccelerationDamageReduction;
        
        
        public final ModConfigSpec.DoubleValue mergedRiflingDamageBoost;
        public final ModConfigSpec.DoubleValue mergedRiflingMovementSpeedBoost;
        
        
        public final ModConfigSpec.DoubleValue alloyDrillArmorPenetrationBoost;
        
        
        public final ModConfigSpec.DoubleValue carefulHeartLauncherDamageBoost;
        public final ModConfigSpec.DoubleValue carefulHeartExplosionDamageBoost;
        public final ModConfigSpec.DoubleValue carefulHeartExplosionRadiusBoost;
        public final ModConfigSpec.DoubleValue carefulHeartExplosionEnabled;
        
        
        public final ModConfigSpec.DoubleValue blazeStormExplosionRadiusBoost;
        public final ModConfigSpec.DoubleValue blazeStormExplosionDamageBoost;
        public final ModConfigSpec.DoubleValue blazeStormExplosionEnabled;
        
        
        public final ModConfigSpec.DoubleValue blazeStormPrimeExplosionRadiusBoost;
        public final ModConfigSpec.DoubleValue blazeStormPrimeExplosionDamageBoost;
        public final ModConfigSpec.DoubleValue blazeStormPrimeExplosionEnabled;
        
        
        
        public final ModConfigSpec.DoubleValue rippingPrimeFireRateBoost;
        public final ModConfigSpec.DoubleValue rippingPrimePenetrationBoost;
        
        
        public final ModConfigSpec.DoubleValue closeCombatPrimeShotgunDamageBoost;
        
        
        public final ModConfigSpec.DoubleValue evilAccuracyRecoilReduction;
        public final ModConfigSpec.DoubleValue evilAccuracyFireRateReduction;
        
        
        public final ModConfigSpec.DoubleValue limitSpeedBulletSpeedBoost;
        
        
        public final ModConfigSpec.DoubleValue ferociousExtensionRangeBoost;
        
        
        public final ModConfigSpec.DoubleValue closeRangeShotDamageBoost;
        
        
        public final ModConfigSpec.DoubleValue heavyFirepowerDamageBoost;
        public final ModConfigSpec.DoubleValue heavyFirepowerAccuracyReduction;
        
        
        public final ModConfigSpec.DoubleValue waspStingerDamageBoost;
        
        
        public final ModConfigSpec.DoubleValue prophecyPactDamageBoost;
        
        
        public final ModConfigSpec.DoubleValue malignantSpreadDamageBoost;
        public final ModConfigSpec.DoubleValue malignantSpreadAccuracyReduction;
        
        
        public final ModConfigSpec.DoubleValue chamberSniperDamageBoost;
        
        
        public final ModConfigSpec.DoubleValue chamberPrimeSniperDamageBoost;
        
        
        public final ModConfigSpec.DoubleValue tacticalReloadSpeedBoost;
        
        
        public final ModConfigSpec.DoubleValue overloadedMagazineCapacityBoost;
        public final ModConfigSpec.DoubleValue overloadedMagazineReloadSpeedReduction;
        
        
        public final ModConfigSpec.DoubleValue infernalChamberBulletCountBoost;
        
        
        public final ModConfigSpec.DoubleValue sustainedFireReloadSpeedBoost;
        
        
        public final ModConfigSpec.DoubleValue infectedMagazineCapacityBoost;
        public final ModConfigSpec.DoubleValue infectedMagazineReloadSpeedReduction;
        
        
        public final ModConfigSpec.DoubleValue deadlySurgeFireRateBoost;
        public final ModConfigSpec.DoubleValue deadlySurgeBulletCountBoost;
        
        
        public final ModConfigSpec.DoubleValue bulletSpreadBulletCountBoost;
        
        
        public final ModConfigSpec.DoubleValue oppressionPointMeleeDamageBoost;
        
        
        public final ModConfigSpec.DoubleValue oppressionPointPrimeMeleeDamageBoost;
        
        
        public final ModConfigSpec.DoubleValue burstReloadReloadSpeedBoost;
        
        
        public final ModConfigSpec.DoubleValue swordWindMeleeRangeBoost;
        
        
        public final ModConfigSpec.DoubleValue swordWindPrimeMeleeRangeBoost;
        
        
        public final ModConfigSpec.DoubleValue corruptMagazineCapacityBoost;
        public final ModConfigSpec.DoubleValue corruptMagazineReloadSpeedReduction;
        
        
        public final ModConfigSpec.DoubleValue heavyCaliberTagDamageBoost;
        public final ModConfigSpec.DoubleValue heavyCaliberTagInaccuracyBoost;
        
        
        public final ModConfigSpec.DoubleValue magazineBoostReloadSpeedBoost;
        
        
        public final ModConfigSpec.DoubleValue redMovementTagSpeedBoost;
        
        
        public final ModConfigSpec.DoubleValue xioraArmorMultiplier;
        public final ModConfigSpec.DoubleValue xioraSpeedMultiplier;

        
        public final ModConfigSpec.DoubleValue ravenArmorMultiplier;
        public final ModConfigSpec.DoubleValue ravenSpeedMultiplier;
        public final ModConfigSpec.IntValue ravenInvisRefreshInterval;
        public final ModConfigSpec.IntValue ravenInvisDuration;
        public final ModConfigSpec.IntValue ravenInvisBreakDelay;

        
        public final ModConfigSpec.DoubleValue islandBoomRavenArmorMultiplier;
        public final ModConfigSpec.DoubleValue islandBoomRavenSpeedMultiplier;
        public final ModConfigSpec.IntValue islandBoomRavenInvisRefreshInterval;
        public final ModConfigSpec.IntValue islandBoomRavenInvisDuration;
        public final ModConfigSpec.IntValue islandBoomRavenInvisBreakDelay;
        public final ModConfigSpec.IntValue islandBoomRavenRegenAmplifier;
        public final ModConfigSpec.IntValue islandBoomRavenRegenRefreshThreshold;
        public final ModConfigSpec.IntValue islandBoomRavenRegenDuration;
        
        
        public final ModConfigSpec.DoubleValue salvationDamageReduction;
        public final ModConfigSpec.IntValue salvationResistanceLevel;
        
        
        public final ModConfigSpec.DoubleValue endlessDamageBoost;
        public final ModConfigSpec.DoubleValue endlessExplosionDamage;
        public final ModConfigSpec.DoubleValue endlessImaginaryResistanceDamagePerPoint;
        public final ModConfigSpec.DoubleValue endlessNearbyPlayerDamageBoost;
        public final ModConfigSpec.IntValue endlessNearbyPlayerPotionAmplifier;
        public final ModConfigSpec.IntValue endlessNearbyPlayerDuration;
        public final ModConfigSpec.DoubleValue endlessNearbyPlayerRadius;

        
        
        public final ModConfigSpec.DoubleValue curioAbsorptionTriggerHp;
        
        public final ModConfigSpec.IntValue curioAbsorptionLevel;
        
        public final ModConfigSpec.DoubleValue curioAbsorptionDuration;
        
        public final ModConfigSpec.DoubleValue curioAbsorptionCooldown;

        
        public final ModConfigSpec.DoubleValue soldierBasicTagDamageBoost;
        
        
        public final ModConfigSpec.DoubleValue soldierSpecificTagDamageBoost;
        
        
        public final ModConfigSpec.DoubleValue uralWolfTagHeadshotMultiplierBoost;
        
        
        public final ModConfigSpec.DoubleValue depletedReloadMagazineCapacityPenalty;
        public final ModConfigSpec.DoubleValue depletedReloadReloadSpeedBoost;
        
        
        public final ModConfigSpec.DoubleValue burstReloadPrimeReloadSpeedBoost;
        
        
        public final ModConfigSpec.DoubleValue tacticalReloadPrimeReloadSpeedBoost;
        
        
        public final ModConfigSpec.DoubleValue shotgunExpansionPrimeCapacityBoost;
        
        
        public final ModConfigSpec.DoubleValue magazineBoostPrimeCapacityBoost;
        
        
        public final ModConfigSpec.DoubleValue tandemMagazinePrimeCapacityBoost;
        
        
        public final ModConfigSpec.DoubleValue shotgunExpansionCapacityBoost;
        
        
        public final ModConfigSpec.DoubleValue magazineBoostCapacityBoost;
        
        
        public final ModConfigSpec.DoubleValue tandemMagazineCapacityBoost;
        
        
        public final ModConfigSpec.DoubleValue kikakuIchijinHealthMultiplier;
        public final ModConfigSpec.BooleanValue kikakuIchijinDestroyUnbreakableBlocks;
        public final ModConfigSpec.BooleanValue kikakuIchijinDestroyNormalBlocks;
        


        

        
        public final ModConfigSpec.IntValue griseoHurtCooldownTicks;

        
        public final ModConfigSpec.IntValue qianjieYichengLuck;

        
        public final ModConfigSpec.IntValue huishiZhijuanBaseCooldown;
        public final ModConfigSpec.IntValue huishiZhijuanLuckPerTick;
        public final ModConfigSpec.IntValue huishiZhijuanMaxCooldown;

        
        public final ModConfigSpec.IntValue yongjieZhijianLuck;
        public final ModConfigSpec.DoubleValue yongjieZhijianCritChancePerLuck;
        public final ModConfigSpec.DoubleValue yongjieZhijianCritDamagePerLuck;

        
        public final ModConfigSpec.IntValue fanxingBaseCooldown;
        public final ModConfigSpec.IntValue fanxingLuckPerTick;
        public final ModConfigSpec.IntValue fanxingMaxCooldown;
        public final ModConfigSpec.DoubleValue fanxingLuckPerResistance;

        
        public final ModConfigSpec.IntValue shijieFanyanLuck;
        public final ModConfigSpec.DoubleValue shijieFanyanCritChancePerLuck;
        public final ModConfigSpec.DoubleValue shijieFanyanCritDamagePerLuck;
        public final ModConfigSpec.DoubleValue shijieFanyanCollapseBaseChance;
        public final ModConfigSpec.DoubleValue shijieFanyanCollapsePerLuck;

        

        
        public final ModConfigSpec.DoubleValue villVTriggerHpRatio;
        public final ModConfigSpec.IntValue villVAbsorptionLevel;
        public final ModConfigSpec.DoubleValue villVAbsorptionDuration;
        public final ModConfigSpec.DoubleValue villVCooldownSeconds;

        
        public final ModConfigSpec.DoubleValue xukongWancangImaginaryDamage;
        public final ModConfigSpec.DoubleValue xukongWancangAmmoRegenPercent;
        public final ModConfigSpec.DoubleValue xukongWancangHeatMax;
        public final ModConfigSpec.DoubleValue xukongWancangHeatCooling;

        
        public final ModConfigSpec.DoubleValue yuxiZhixiaTriggerHpRatio;
        public final ModConfigSpec.IntValue yuxiZhixiaAbsorptionLevel;
        public final ModConfigSpec.DoubleValue yuxiZhixiaAbsorptionDuration;
        public final ModConfigSpec.DoubleValue yuxiZhixiaCooldownSeconds;

        
        public final ModConfigSpec.DoubleValue qishiZhijianImaginaryDamage;
        public final ModConfigSpec.DoubleValue qishiZhijianAmmoRegenPercent;
        public final ModConfigSpec.DoubleValue qishiZhijianHeatMax;
        public final ModConfigSpec.DoubleValue qishiZhijianHeatCooling;

        
        public final ModConfigSpec.IntValue luoxuanAbsorptionInterval;
        public final ModConfigSpec.IntValue luoxuanAbsorptionLevel;
        public final ModConfigSpec.IntValue luoxuanAbsorptionDuration;

        
        public final ModConfigSpec.DoubleValue xukongWancangYZTHImaginaryDamageScale;
        public final ModConfigSpec.DoubleValue xukongWancangYZTHAmmoRegenPercent;
        public final ModConfigSpec.IntValue xukongWancangYZTHInfectionDuration;
        public final ModConfigSpec.DoubleValue xukongWancangYZTHHeatMax;
        public final ModConfigSpec.DoubleValue xukongWancangYZTHHeatCooling;

        
        public final ModConfigSpec.IntValue adaptationMaxCount;

        
        public final ModConfigSpec.IntValue kalpasMaxSlots;
        public final ModConfigSpec.DoubleValue kalpasAdaptFactor;
        public final ModConfigSpec.IntValue kalpasDecaySeconds;

        
        public final ModConfigSpec.DoubleValue imerAttackDamageBonus;

        
        public final ModConfigSpec.IntValue huajieZhiyanMaxSlots;
        public final ModConfigSpec.DoubleValue huajieZhiyanAdaptFactor;
        public final ModConfigSpec.IntValue huajieZhiyanDecaySeconds;
        public final ModConfigSpec.DoubleValue huajieZhiyanHealthPerResistance;

        


        
        public final ModConfigSpec.IntValue aoMieMaxSlots;
        public final ModConfigSpec.DoubleValue aoMieAdaptFactor;
        public final ModConfigSpec.IntValue aoMieDecaySeconds;
        public final ModConfigSpec.DoubleValue aoMieHealthPerResistance;

        
        public final ModConfigSpec.DoubleValue metaMorphLifeStealPerResistance;
        public final ModConfigSpec.DoubleValue metaMorphImaginaryDamageScale;

        
        public final ModConfigSpec.DoubleValue suMaxHealthReduction;
        public final ModConfigSpec.DoubleValue suDamageTakenFactor;

        
        public final ModConfigSpec.DoubleValue wanwuXiumianOverheal;
        public final ModConfigSpec.DoubleValue wanwuXiumianAmmoRegenPercent;

        
        public final ModConfigSpec.DoubleValue juezheMaxHealthReduction;
        public final ModConfigSpec.DoubleValue juezheDamageTakenFactor;

        
        public final ModConfigSpec.DoubleValue tingzhiZhijianOverheal;
        public final ModConfigSpec.DoubleValue tingzhiZhijianAmmoBasePercent;
        public final ModConfigSpec.DoubleValue tingzhiZhijianAmmoResistanceScale;

        
        public final ModConfigSpec.DoubleValue tianhuiMaxHealthReduction;
        public final ModConfigSpec.DoubleValue tianhuiResistanceScale;
        public final ModConfigSpec.DoubleValue tianhuiMinDamageFactor;

        
        public final ModConfigSpec.DoubleValue yinguoZhuanlunOverheal;
        public final ModConfigSpec.DoubleValue yinguoZhuanlunAmmoResistanceScale;
        public final ModConfigSpec.DoubleValue yinguoZhuanlunImaginaryDamageScale;

        
        public final ModConfigSpec.DoubleValue zhenWoImaginaryResistance;
        public final ModConfigSpec.DoubleValue zhenWoAllAttributesPercent;
        public final ModConfigSpec.DoubleValue zhenWoTriggerHpRatio;
        public final ModConfigSpec.DoubleValue zhenWoBarrierRadius;
        public final ModConfigSpec.IntValue zhenWoSlownessAmplifier;
        public final ModConfigSpec.IntValue zhenWoSlownessDurationSeconds;
        public final ModConfigSpec.IntValue zhenWoBarrierDurationSeconds;
        public final ModConfigSpec.IntValue zhenWoCooldownSeconds;
        public final ModConfigSpec.DoubleValue zhenWoDamageTakenFactor;

        


        

        
        public final ModConfigSpec.DoubleValue aponiaDebuffChance;
        public final ModConfigSpec.IntValue aponiaDebuffDurationSeconds;
        public final ModConfigSpec.IntValue aponiaDebuffCount;
        public final ModConfigSpec.DoubleValue shenzuiZhijianDebuffChance;
        public final ModConfigSpec.IntValue shenzuiZhijianDebuffDurationSeconds;
        public final ModConfigSpec.IntValue shenzuiZhijianDebuffCount;
        public final ModConfigSpec.DoubleValue jielvDebuffChance;
        public final ModConfigSpec.IntValue jielvDebuffDurationSeconds;
        public final ModConfigSpec.IntValue jielvDebuffCount;
        
        public final ModConfigSpec.ConfigValue<List<? extends String>> disciplineHarmfulBuffBlacklist;

        
        public final ModConfigSpec.DoubleValue wangshiDeKuqiuAuraRadius;
        public final ModConfigSpec.IntValue wangshiDeKuqiuInfectionLevel;
        public final ModConfigSpec.IntValue wangshiDeKuqiuInfectionDurationSeconds;
        public final ModConfigSpec.DoubleValue wangshiDeKuqiuMingzhiqiAuraRadius;
        public final ModConfigSpec.IntValue wangshiDeKuqiuMingzhiqiInfectionLevel;
        public final ModConfigSpec.IntValue wangshiDeKuqiuMingzhiqiInfectionDurationSeconds;
        public final ModConfigSpec.DoubleValue shenenJiejieAuraRadius;
        public final ModConfigSpec.IntValue shenenJiejieInfectionLevel;
        public final ModConfigSpec.IntValue shenenJiejieInfectionDurationSeconds;

        
        public final ModConfigSpec.DoubleValue edenAuraRange;
        public final ModConfigSpec.IntValue edenIntervalSeconds;
        public final ModConfigSpec.IntValue edenBuffDurationSeconds;
        public final ModConfigSpec.IntValue edenBuffAmplifier;
        public final ModConfigSpec.DoubleValue edenGunDamageReduction;
        public final ModConfigSpec.DoubleValue cuiyaoZhiGeAuraRange;
        public final ModConfigSpec.IntValue cuiyaoZhiGeIntervalSeconds;
        public final ModConfigSpec.IntValue cuiyaoZhiGeBuffDurationSeconds;
        public final ModConfigSpec.IntValue cuiyaoZhiGeBuffAmplifier;
        public final ModConfigSpec.DoubleValue cuiyaoZhiGeGunDamageReduction;
        public final ModConfigSpec.DoubleValue huangjinAuraRange;
        public final ModConfigSpec.IntValue huangjinIntervalSeconds;
        public final ModConfigSpec.IntValue huangjinBuffDurationSeconds;
        public final ModConfigSpec.IntValue huangjinBuffAmplifier;
        public final ModConfigSpec.DoubleValue huangjinGunDamageReduction;
        
        public final ModConfigSpec.ConfigValue<List<? extends String>> goldenBeneficialBuffBlacklist;

        
        public final ModConfigSpec.DoubleValue edenStarTeleportRange;
        public final ModConfigSpec.DoubleValue tuntianZhijianTeleportRange;
        public final ModConfigSpec.DoubleValue qidianChonggouTeleportRange;

        
        public final ModConfigSpec.DoubleValue kosmaAttackSpeedPercent;
        public final ModConfigSpec.DoubleValue kosmaAttackDamagePercent;
        public final ModConfigSpec.DoubleValue limingZhiShaoAttackSpeedPercent;
        public final ModConfigSpec.DoubleValue limingZhiShaoAttackDamagePercent;
        public final ModConfigSpec.DoubleValue limingZhiShaoCritChancePercent;
        public final ModConfigSpec.DoubleValue xuguangAttackSpeedPercent;
        public final ModConfigSpec.DoubleValue xuguangAttackDamagePercent;
        public final ModConfigSpec.DoubleValue xuguangCritDamagePercent;

        
        public final ModConfigSpec.DoubleValue dizangYuhunStripPercent;
        public final ModConfigSpec.DoubleValue qinshiZhijianStripPercent;

        
        public final ModConfigSpec.DoubleValue mebiusPerTypeBonus;
        public final ModConfigSpec.DoubleValue shijieZhiShePerTypeBonus;
        public final ModConfigSpec.DoubleValue wuxianPerTypeBonus;

        /** 梅比乌斯/噬界之蛇/无限 统一击杀类型记录上限 */
        public final ModConfigSpec.IntValue sheshaLineKillTypeRecordLimit;

        /** 舍沙移除目标有益 buff 的概率系数 */
        public final ModConfigSpec.DoubleValue sheshaBuffRemovalFactor;

        
        public final ModConfigSpec.ConfigValue<List<? extends String>> attributeBonusBlacklist;

        
        public final ModConfigSpec.DoubleValue wangshiDeSheyingRemoveChance;
        public final ModConfigSpec.DoubleValue siZhiYiRemoveChance;

        
        public final ModConfigSpec.DoubleValue huaArmorPercent;
        public final ModConfigSpec.DoubleValue duchenZhiYuArmorPercent;

        
        public final ModConfigSpec.DoubleValue yuduchenStopChance;
        public final ModConfigSpec.IntValue yuduchenStopDurationSeconds;
        public final ModConfigSpec.DoubleValue yuduchenArmorImaginaryScale;
        public final ModConfigSpec.DoubleValue fanchenNanduStopChance;
        public final ModConfigSpec.IntValue fanchenNanduStopDurationSeconds;
        public final ModConfigSpec.DoubleValue fanchenNanduArmorImaginaryScale;
        public final ModConfigSpec.IntValue bushiShiwuStopDurationSeconds;
        public final ModConfigSpec.DoubleValue bushiShiwuArmorImaginaryScale;

        
        public final ModConfigSpec.DoubleValue padoPhilipisSpecialFishChance;
        public final ModConfigSpec.DoubleValue luejiZhiShouSpecialFishChance;
        public final ModConfigSpec.DoubleValue kongmengSpecialFishChance;

        
        public final ModConfigSpec.DoubleValue wangshiDeHuanmengDamageMultiplier;
        public final ModConfigSpec.DoubleValue laZhiYanDamageMultiplier;
        public final ModConfigSpec.DoubleValue yeZhiTongDamageMultiplier;

        
        public final ModConfigSpec.ConfigValue<List<? extends String>> curioConflicts;

        
        public final ModConfigSpec.DoubleValue collapsePercentPerLevel;
        public final ModConfigSpec.DoubleValue collapsePercentPerDebuff;
        public final ModConfigSpec.IntValue collapseMaxDebuffCount;

        public final ModConfigSpec.DoubleValue criticalDelayCritChanceBoost;
        public final ModConfigSpec.DoubleValue criticalDelayFireRateReduction;
        public final ModConfigSpec.DoubleValue lethalCritCritChance;
        public final ModConfigSpec.DoubleValue weaknessSenseCritDamage;
        public final ModConfigSpec.DoubleValue argonScopeBaseCritChance;
        public final ModConfigSpec.IntValue argonScopeDuration;
        public final ModConfigSpec.DoubleValue gildedArgonScopeBaseCritChance;
        public final ModConfigSpec.DoubleValue gildedArgonScopeCritChancePerLevel;
        public final ModConfigSpec.DoubleValue gildedArgonScopeHeadshotKillExtra;
        public final ModConfigSpec.IntValue gildedArgonScopeDuration;
        public final ModConfigSpec.IntValue gildedArgonScopeMaxStacks;
        public final ModConfigSpec.DoubleValue sharpBulletBaseCritDamage;
        public final ModConfigSpec.IntValue sharpBulletDuration;
        public final ModConfigSpec.DoubleValue gildedRifleAptitudePerHarmful;
        public final ModConfigSpec.DoubleValue gildedSplitChamberBulletCountBase;
        public final ModConfigSpec.DoubleValue gildedSplitChamberBulletCountPerLevel;
        public final ModConfigSpec.IntValue gildedSplitChamberDuration;
        public final ModConfigSpec.IntValue gildedSplitChamberMaxStacks;
        public final ModConfigSpec.DoubleValue destructionCritDamage;
        public final ModConfigSpec.DoubleValue destructionPrimeCritDamage;
        public final ModConfigSpec.DoubleValue thunderBarrelCritChance;
        public final ModConfigSpec.DoubleValue thunderBarrelPrimeCritChance;
        public final ModConfigSpec.DoubleValue laserScopeBaseCritChance;
        public final ModConfigSpec.IntValue laserScopeDuration;
        public final ModConfigSpec.DoubleValue fragmentShotBaseCritDamage;
        public final ModConfigSpec.IntValue fragmentShotDuration;
        public final ModConfigSpec.DoubleValue gildedShotgunSavvyPerHarmful;
        public final ModConfigSpec.DoubleValue gildedInfernalChamberBulletCountBase;
        public final ModConfigSpec.DoubleValue gildedInfernalChamberBulletCountPerLevel;
        public final ModConfigSpec.IntValue gildedInfernalChamberDuration;
        public final ModConfigSpec.IntValue gildedInfernalChamberMaxStacks;
        public final ModConfigSpec.DoubleValue weaknessMasteryCritDamage;
        public final ModConfigSpec.DoubleValue weaknessMasteryPrimeCritDamage;
        public final ModConfigSpec.DoubleValue hollowPointCritDamage;
        public final ModConfigSpec.DoubleValue hollowPointPistolDamageReduction;
        public final ModConfigSpec.DoubleValue pistolMasteryCritChance;
        public final ModConfigSpec.DoubleValue pistolMasteryPrimeCritChance;
        public final ModConfigSpec.DoubleValue hydraulicCrosshairBaseCritChance;
        public final ModConfigSpec.IntValue hydraulicCrosshairDuration;
        public final ModConfigSpec.DoubleValue gildedHydraulicCrosshairBaseCritChance;
        public final ModConfigSpec.DoubleValue gildedHydraulicCrosshairCritChancePerLevel;
        public final ModConfigSpec.DoubleValue gildedHydraulicCrosshairHeadshotKillExtra;
        public final ModConfigSpec.IntValue gildedHydraulicCrosshairDuration;
        public final ModConfigSpec.IntValue gildedHydraulicCrosshairMaxStacks;
        public final ModConfigSpec.DoubleValue sharpAmmoBaseCritDamage;
        public final ModConfigSpec.IntValue sharpAmmoDuration;
        public final ModConfigSpec.DoubleValue gildedMarksmanPerHarmful;
        public final ModConfigSpec.DoubleValue gildedBulletSpreadBulletCountBase;
        public final ModConfigSpec.DoubleValue gildedBulletSpreadBulletCountPerLevel;
        public final ModConfigSpec.IntValue gildedBulletSpreadDuration;
        public final ModConfigSpec.IntValue gildedBulletSpreadMaxStacks;
        public final ModConfigSpec.DoubleValue steelSlashCritChance;
        public final ModConfigSpec.DoubleValue dismembermentCritDamage;
        public final ModConfigSpec.DoubleValue sacrificeOppressionMeleeDamage;
        public final ModConfigSpec.DoubleValue sacrificeSteelCritChance;
        public final ModConfigSpec.DoubleValue gildedSteelSlashCritChanceBase;
        public final ModConfigSpec.DoubleValue gildedSteelSlashCritDamagePerLevel;
        public final ModConfigSpec.IntValue gildedSteelSlashDuration;
        public final ModConfigSpec.IntValue gildedSteelSlashMaxStacks;
        public final ModConfigSpec.DoubleValue conditionOverloadPerHarmful;
        public final ModConfigSpec.DoubleValue sacrificeSetBonus;

        public final ModConfigSpec.DoubleValue fusionGrowthCoefficient;
        public final ModConfigSpec.IntValue fusionEbcCommon;
        public final ModConfigSpec.IntValue fusionEbcUncommon;
        public final ModConfigSpec.IntValue fusionEbcRare;
        public final ModConfigSpec.IntValue fusionEbcEpic;
        public final ModConfigSpec.IntValue fusionMaxLevelCommon;
        public final ModConfigSpec.IntValue fusionMaxLevelUncommon;
        public final ModConfigSpec.IntValue fusionMaxLevelRare;
        public final ModConfigSpec.IntValue fusionMaxLevelEpic;
        public final ModConfigSpec.IntValue fusionVesselCapacity;

        
        public final ModConfigSpec.IntValue fusionVesselNetherMin;
        public final ModConfigSpec.IntValue fusionVesselNetherMax;
        public final ModConfigSpec.IntValue fusionVesselEndMin;
        public final ModConfigSpec.IntValue fusionVesselEndMax;

        
        public final ModConfigSpec.DoubleValue fusionVesselNetherChance;
        public final ModConfigSpec.DoubleValue fusionVesselEndChance;

        
        public Common(ModConfigSpec.Builder builder) {
            builder.comment("TACZ Curios 饰品配置").push("tcc_curios");

            
            builder.comment("统计为食用鱼类的物品注册名列表（如 minecraft:cod），吃到该列表中的物品后 tcc:fish_food_eaten +1").push("fish_food_items");
            fishFoodItems = builder
                    .comment("鱼类物品注册名列表")
                    .defineList("items", List.of(
                            "minecraft:cod", "minecraft:cooked_cod",
                            "minecraft:salmon", "minecraft:cooked_salmon",
                            "minecraft:pufferfish", "minecraft:tropical_fish",
                            // Aquaculture
                            "aquaculture:atlantic_cod", "aquaculture:blackfish",
                            "aquaculture:pacific_halibut", "aquaculture:atlantic_halibut",
                            "aquaculture:atlantic_herring", "aquaculture:pink_salmon",
                            "aquaculture:pollock", "aquaculture:rainbow_trout",
                            "aquaculture:bayad", "aquaculture:boulti",
                            "aquaculture:capitaine", "aquaculture:synodontis",
                            "aquaculture:smallmouth_bass", "aquaculture:bluegill",
                            "aquaculture:brown_trout", "aquaculture:carp",
                            "aquaculture:catfish", "aquaculture:gar",
                            "aquaculture:minnow", "aquaculture:muskellunge",
                            "aquaculture:perch", "aquaculture:arapaima",
                            "aquaculture:piranha", "aquaculture:tambaqui",
                            "aquaculture:brown_shrooma", "aquaculture:red_shrooma",
                            "aquaculture:jellyfish", "aquaculture:red_grouper",
                            "aquaculture:tuna", "aquaculture:box_turtle",
                            "aquaculture:arrau_turtle", "aquaculture:starshell_turtle",
                            // Miner's Delight
                            "miners_delight:squid", "miners_delight:glow_squid",
                            "miners_delight:tentacles", "miners_delight:baked_squid",
                            "miners_delight:baked_tentacles", "miners_delight:squid_sandwich",
                            "miners_delight:takoyaki", "miners_delight:tentacles_on_a_stick",
                            "miners_delight:bowl_of_stuffed_squid", "miners_delight:glow_ink_pasta",
                            "miners_delight:fish_stew_cup", "miners_delight:baked_cod_stew_cup",
                            // Crabber's Delight
                            "crabbersdelight:crab", "crabbersdelight:cooked_crab",
                            "crabbersdelight:clawster", "crabbersdelight:cooked_clawster",
                            "crabbersdelight:shrimp", "crabbersdelight:cooked_shrimp",
                            "crabbersdelight:clam", "crabbersdelight:raw_clam_meat",
                            "crabbersdelight:cooked_clam_meat", "crabbersdelight:raw_squid_tentacles",
                            "crabbersdelight:cooked_squid_tentacles", "crabbersdelight:raw_glow_squid_tentacles",
                            "crabbersdelight:cooked_glow_squid_tentacles", "crabbersdelight:cooked_tropical_fish",
                            "crabbersdelight:tropical_fish_slice", "crabbersdelight:cooked_tropical_fish_slice",
                            "crabbersdelight:pufferfish_slice", "crabbersdelight:cooked_pufferfish_slice",
                            "crabbersdelight:squid_kebab", "crabbersdelight:fish_stick",
                            "crabbersdelight:shrimp_skewer", "crabbersdelight:crab_cakes",
                            "crabbersdelight:crab_legs", "crabbersdelight:clam_bake",
                            "crabbersdelight:clam_chowder", "crabbersdelight:bisque",
                            "crabbersdelight:seafood_gumbo", "crabbersdelight:surf_and_turf",
                            "crabbersdelight:shrimp_fried_rice", "crabbersdelight:stuffed_nautilus_shell",
                            // Farmer's Delight
                            "farmersdelight:cod_slice", "farmersdelight:cooked_cod_slice",
                            "farmersdelight:salmon_slice", "farmersdelight:cooked_salmon_slice",
                            "farmersdelight:fish_stew", "farmersdelight:baked_cod_stew",
                            "farmersdelight:grilled_salmon", "farmersdelight:squid_ink_pasta",
                            "farmersdelight:salmon_roll", "farmersdelight:cod_roll",
                            // Youkai's Feasts
                            "youkaisfeasts:raw_lamprey", "youkaisfeasts:raw_lamprey_fillet",
                            "youkaisfeasts:roasted_lamprey", "youkaisfeasts:roasted_lamprey_fillet",
                            "youkaisfeasts:raw_tuna", "youkaisfeasts:raw_tuna_slice",
                            "youkaisfeasts:seared_tuna", "youkaisfeasts:seared_tuna_slice",
                            "youkaisfeasts:otoro", "youkaisfeasts:roe",
                            "youkaisfeasts:lamprey_skewer", "youkaisfeasts:kabayaki",
                            "youkaisfeasts:grilled_eel_over_rice", "youkaisfeasts:tuna_nigiri",
                            "youkaisfeasts:otoro_nigiri", "youkaisfeasts:tobiko_gunkan",
                            "youkaisfeasts:tuscan_salmon", "youkaisfeasts:salmon_futomaki",
                            "youkaisfeasts:salmon_futomaki_slice", "youkaisfeasts:salmon_lover_roll",
                            "youkaisfeasts:salmon_lover_roll_slice", "youkaisfeasts:tekka_maki",
                            "youkaisfeasts:tekka_maki_slice", "youkaisfeasts:california_roll",
                            "youkaisfeasts:california_roll_slice", "youkaisfeasts:rainbow_roll",
                            "youkaisfeasts:rainbow_roll_slice", "youkaisfeasts:rainbow_futomaki",
                            "youkaisfeasts:rainbow_futomaki_slice", "youkaisfeasts:volcano_roll",
                            "youkaisfeasts:volcano_roll_slice", "youkaisfeasts:roe_california_roll",
                            "youkaisfeasts:roe_california_roll_slice"
                    ), o -> o instanceof String);
            builder.pop();

            
            builder.comment("天火圣裁饰品配置").push("heaven_fire_judgment");
            heavenFireJudgmentDamageBoost = builder
                    .comment("通用枪械伤害加成 (默认: 0.5 = 50%)")
                    .defineInRange("damageBoost", 0.5, -1, 100);
            heavenFireJudgmentHealthCost = builder
                    .comment("触发时扣除的当前生命值比例 (默认: -0.3 = -30%)")
                    .defineInRange("healthCost", -0.3, -1, 1);
            builder.pop();
            
            
            builder.comment("天火流血效果配置（两个饰品共用）").push("heaven_fire_bleeding");
            heavenFireBleedingDamagePerLevel = builder
                    .comment("每级流血效果造成的最大生命值比例伤害 (默认: -0.1 = -10% maxHP/级)")
                    .defineInRange("damagePerLevel", -0.1, -1, 0);
            heavenFireBleedingMaxLevel = builder
                    .comment("流血效果最大等级 (默认: 5)")
                    .defineInRange("maxLevel", 5, 1, 10);
            heavenFireBleedingDuration = builder
                    .comment("流血效果持续时间(秒) (默认: 5)")
                    .defineInRange("duration", 5, 1, 60);
            heavenFireApocalypseDelayDuration = builder
                    .comment("天火劫灭扣血后延迟施加流血的时长(秒) (默认: 3, 最小: 1)")
                    .defineInRange("delayDuration", 3, 1, 60);
            builder.pop();
            
            
            builder.comment("附加伤害基于攻击力的增幅（分母）：附加虚数伤害 = 基础伤害 × (1 + 攻击力 / 该值)").push("imaginary_damage");
            imaginaryDamageAttackAmplification = builder
                    .comment("附加伤害基于攻击力的增幅 (默认: 1000) —— 攻击力越高，附加虚数伤害越高")
                    .defineInRange("attackAmplification", 1000.0, 1.0, 100000.0);
            imaginaryDamageUseSetHealth = builder
                    .comment("非崩解附加虚数伤害的结算方式：true = 直接 setHealth（绕过护甲/吸收），false = 走 hurt 常规结算（默认）")
                    .define("useSetHealth", false);
            builder.pop();
            
            
            builder.comment("虚数侵染效果配置（纯标记，不再直接造成流血。流血由虚数崩解处理。增伤计算公式：最终伤害 = 伤害 × (1 + 层数 × ampPerLevel)）").push("imaginary_infection");
            imaginaryInfectionAmpPerLevel = builder
                    .comment("每层虚数侵染的增伤比例 (默认: 0.1 = 10%/层)")
                    .defineInRange("ampPerLevel", 0.1, 0.01, 1.0);
            imaginaryInfectionMaxLevel = builder
                    .comment("虚数侵染效果最大等级上限 (默认: 99)")
                    .defineInRange("maxLevel", 99, 1, 99);
            imaginaryInfectionDuration = builder
                    .comment("虚数侵染效果持续时间(秒) (默认: 15)")
                    .defineInRange("duration", 15, 1, 300);
            imaginaryInfectionResistanceReduction = builder
                    .comment("虚数侵染降低的虚数抗性值 (默认: 5)")
                    .defineInRange("resistanceReduction", 5.0, 0, 100);
            builder.pop();
            

            
            builder.comment("虚数崩解配置（虚数崩解基于虚数侵染层数和负面效果种数造成额外伤害）").push("imaginary_collapse");
            collapsePercentPerLevel = builder
                    .comment("崩解基础每秒造成的最大生命值伤害比例（不再随侵染等级线性放大；层数由通用侵染增伤体现） (默认: 0.025 ≈ 2.5%/秒)")
                    .defineInRange("percentPerLevel", 0.025, 0, 1);
            collapsePercentPerDebuff = builder
                    .comment("每种负面效果的崩解增伤比例 (默认: 0.1 = 10%/种)")
                    .defineInRange("percentPerDebuff", 0.1, 0, 1);
            collapseMaxDebuffCount = builder
                    .comment("崩解计入的负面效果种数上限 (默认: 5)")
                    .defineInRange("maxDebuffCount", 5, 1, 20);
            builder.pop();

            
            builder.comment("按神之键阶位分级的虚数侵染上限（1阶/2阶/3阶；特殊神之键：黑渊白花、天火劫灭·无烬终焉、第零额定功率·神恩结界可到9级）").push("imaginary_infection_by_tier");
            tier1ImaginaryInfectionMaxLevel = builder
                    .comment("1阶神之键的虚数侵染上限 (默认: 2)")
                    .defineInRange("tier1MaxLevel", 2, 1, 99);
            tier2ImaginaryInfectionMaxLevel = builder
                    .comment("2阶神之键的虚数侵染上限 (默认: 4)")
                    .defineInRange("tier2MaxLevel", 4, 1, 99);
            tier3ImaginaryInfectionMaxLevel = builder
                    .comment("3阶神之键的虚数侵染上限 (默认: 6)")
                    .defineInRange("tier3MaxLevel", 6, 1, 99);
            specialImaginaryInfectionMaxLevel = builder
                    .comment("特殊神之键(黑渊白花/天火劫灭·无烬终焉/神恩结界)的虚数侵染上限 (默认: 9)")
                    .defineInRange("specialMaxLevel", 9, 1, 99);
            builder.pop();
            
            
            builder.comment("按阶位控制神之键/逐火之蛾饰品能否直接卸下（阶位由 config/tcc/key_tiers.json 决定；true = 可直接卸下且不消耗崩坏结晶，false = 维持原限制）").push("curio_removable_by_tier");
            tier1CurioRemovable = builder
                    .comment("1阶饰品是否可直接卸下 (默认: true)")
                    .define("tier1", true);
            tier2CurioRemovable = builder
                    .comment("2阶饰品是否可直接卸下 (默认: false)")
                    .define("tier2", false);
            tier3CurioRemovable = builder
                    .comment("3阶饰品是否可直接卸下 (默认: false)")
                    .define("tier3", false);
            specialCurioRemovable = builder
                    .comment("特殊饰品(黑渊白花/天火劫灭·无烬终焉/神恩结界/真我)是否可直接卸下 (默认: false)")
                    .define("special", false);
            builder.pop();
            
            
            builder.comment("黑渊白花饰品配置").push("heiyuan_baihua");
            heiyuanBaihuaImaginaryDamageScale = builder
                    .comment("附加虚数伤害系数 (默认: 1.0；最终伤害 = 攻击者总血量 × 虚数抗性/100 × 系数)")
                    .defineInRange("imaginaryDamageScale", 1.0, 0.0, 1000.0);
            builder.pop();
            
            
            builder.comment("裁决之键饰品配置").push("judgement_key");
            judgementProcChance = builder
                    .comment("爆头时触发额外伤害的几率 (默认: 0.1 = 10%)")
                    .defineInRange("procChance", 0.1, 0, 1);
            judgementDirectDamagePercent = builder
                    .comment("直接真实伤害的比例（对无限伤实体） (默认: 0.3 = 30%)")
                    .defineInRange("directDamagePercent", 0.3, 0, 1);
            judgementCollapseProcChance = builder
                    .comment("爆头时触发虚数崩解效果的几率 (默认: 0.5 = 50%)")
                    .defineInRange("collapseProcChance", 0.1, 0, 1);
            judgementKeyCritChance = builder
                    .comment("暴击几率加成 (默认: 1.0 = +100%)")
                    .defineInRange("critChance", 1.0, -1, 100);
            judgementKeyCritDamage = builder
                    .comment("暴击伤害加成 (默认: 1.5 = +150%)")
                    .defineInRange("critDamage", 1.5, -1, 100);
            builder.pop();

            
            builder.comment("涤罪七雷饰品配置").push("seven_thunders");
            sevenThundersHeadshotMultiplier = builder
                    .comment("爆头倍率加成 (默认: 2.0 = +200%)")
                    .defineInRange("headshotMultiplier", 2.0, -1, 100);
            sevenThundersCritChance = builder
                    .comment("暴击几率加成 (默认: 0.2 = +20%)")
                    .defineInRange("critChance", 0.2, -1, 100);
            sevenThundersCritDamage = builder
                    .comment("暴击伤害加成 (默认: 0.5 = +50%)")
                    .defineInRange("critDamage", 0.5, -1, 100);
            builder.pop();

            
            builder.comment("雷鸣见（涤罪七雷·雷见）饰品配置").push("seven_thunders_thunder_seen");
            sevenThundersThunderSeenHeadshotMultiplier = builder
                    .comment("爆头倍率加成 (默认: 2.0 = +200%)")
                    .defineInRange("headshotMultiplier", 2.0, -1, 100);
            sevenThundersThunderSeenCritChance = builder
                    .comment("暴击几率加成 (默认: 0.5 = +50%)")
                    .defineInRange("critChance", 0.5, -1, 100);
            sevenThundersThunderSeenCritDamage = builder
                    .comment("暴击伤害加成 (默认: 1.0 = +100%)")
                    .defineInRange("critDamage", 1.0, -1, 100);
            sevenThundersThunderSeenProcChance = builder
                    .comment("爆头时触发额外伤害的几率 (默认: 0.3 = 30%)")
                    .defineInRange("procChance", 0.3, 0, 1);
            sevenThundersThunderSeenExtraHpDamage = builder
                    .comment("触发时造成目标最大生命值的比例 (默认: 0.05 = 5%)")
                    .defineInRange("extraHpDamage", 0.05, 0, 1);
            builder.pop();
            
            
            builder.comment("天火劫灭饰品配置").push("heaven_fire_apocalypse");
            heavenFireApocalypseDamageBoost = builder
                    .comment("通用枪械伤害加成 (默认: 1.0 = 100%)")
                    .defineInRange("damageBoost", 1.0, -1, 1000);
            heavenFireApocalypseExplosionRadius = builder
                    .comment("爆炸范围加成 (默认: 1)")
                    .defineInRange("explosionRadius", 1.0, -1, 100);
            heavenFireApocalypseExplosionDamage = builder
                    .comment("爆炸伤害加成 (默认: 1.0 = 100%)")
                    .defineInRange("explosionDamage", 1.0, -1, 100);
            heavenFireApocalypseHealthCost = builder
                    .comment("触发时扣除的当前生命值比例 (默认: -1.0 = -100%)")
                    .defineInRange("healthCost", -1.0, -1, 1);
            brahmaBeastsHealthCostReduction = builder
                    .comment("装备梵天百兽时天火劫灭扣血比例的减少值 (默认: 0.6 = 从扣100%变为扣40%，即保留60%血量)")
                    .defineInRange("brahmaBeastsHealthCostReduction", 0.6, 0, 1);
            heavenFireApocalypseNearbyPlayerDamageBoost = builder
                    .comment("附近玩家获得的 bullet_gundamage 每级伤害加成 (默认: 0.5 = 50%/级)")
                    .defineInRange("nearbyPlayerDamageBoost", 0.5, -1, 100);
            heavenFireApocalypseNearbyPlayerPotionAmplifier = builder
                    .comment("附近玩家获得的药水效果等级 (0=1级, 默认: 0)")
                    .defineInRange("nearbyPlayerPotionAmplifier", 0, 0, 999);
            heavenFireApocalypseNearbyPlayerDuration = builder
                    .comment("附近玩家获得伤害加成的持续时间(秒) (默认: 15)")
                    .defineInRange("nearbyPlayerDuration", 15, -1, 300);
            heavenFireApocalypseNearbyPlayerRadius = builder
                    .comment("影响附近玩家的范围 (默认: 32)")
                    .defineInRange("nearbyPlayerRadius", 32.0, -1, 100);
            builder.pop();
            
            
            builder.comment("膛线饰品配置").push("rifling");
            riflingDamageBoost = builder
                    .comment("特定枪械伤害加成 (默认: 1.65 = 165%)")
                    .defineInRange("damageBoost", 1.65, -1, 100);
            builder.pop();
            
            
            builder.comment("分裂膛室饰品配置").push("split_chamber");
            splitChamberBulletCountBoost = builder
                    .comment("弹头数量加成 (默认: 0.9 = 90%)")
                    .defineInRange("bulletCountBoost", 0.9, -1, 100);
            builder.pop();
            
            
            builder.comment("卑劣加速饰品配置").push("despicable_acceleration");
            despicableAccelerationFireRateBoost = builder
                    .comment("射击速度加成 (默认: +0.9 = +90%)")
                    .defineInRange("fireRateBoost", 0.9, -1, 100);
            despicableAccelerationDamageReduction = builder
                    .comment("伤害降低 (默认: -0.15 = -15%)")
                    .defineInRange("damageReduction", -0.15, -1, 0);
            builder.pop();
            
            
            builder.comment("并合膛线饰品配置").push("merged_rifling");
            mergedRiflingDamageBoost = builder
                    .comment("特定枪械伤害加成 (默认: 1.55 = 155%)")
                    .defineInRange("damageBoost", 1.55, -1, 100);
            mergedRiflingMovementSpeedBoost = builder
                    .comment("持枪移动速度加成 (默认: 0.25 = 25%)")
                    .defineInRange("movementSpeedBoost", 0.25, -1, 100);
            builder.pop();
            
            
            builder.comment("合金钻头饰品配置").push("alloy_drill");
            alloyDrillArmorPenetrationBoost = builder
                    .comment("护甲穿透加成 (默认: 2.0 = 200%)")
                    .defineInRange("armorPenetrationBoost", 2.0, -1, 100);
            builder.pop();
            
            
            builder.comment("我小心海也绝非鳝类饰品配置").push("careful_heart");
            carefulHeartLauncherDamageBoost = builder
                    .comment("重型武器伤害加成 (默认: 1.5 = 150%)")
                    .defineInRange("launcherDamageBoost", 1.5, -1, 100);
            carefulHeartExplosionDamageBoost = builder
                    .comment("爆炸伤害加成 (默认: 1.5 = 150%)")
                    .defineInRange("explosionDamageBoost", 1.5, -1, 100);
            carefulHeartExplosionRadiusBoost = builder
                    .comment("爆炸范围加成 (默认: 1.5 = 150%)")
                    .defineInRange("explosionRadiusBoost", 1.5, -1, 100);
            carefulHeartExplosionEnabled = builder
                    .comment("爆炸启用属性 (默认: 2.0)")
                    .defineInRange("explosionEnabled", 2.0, -1, 100);
            builder.pop();
            


            
            builder.comment("关键延迟饰品配置").push("critical_delay");
            criticalDelayCritChanceBoost = builder.comment("暴击几率加成 (默认: 2.0)").defineInRange("critChanceBoost", 2.0, -1, 100);
            criticalDelayFireRateReduction = builder.comment("射速降低 (默认: -0.2)").defineInRange("fireRateReduction", -0.2, -1, 0);
            builder.pop();
            
            
            builder.comment("致命一击饰品配置").push("lethal_crit");
            lethalCritCritChance = builder.comment("暴击几率加成 (默认: 1.5)").defineInRange("critChance", 1.5, -1, 100);
            builder.pop();
            
            
            builder.comment("弱点感应饰品配置").push("weakness_sense");
            weaknessSenseCritDamage = builder.comment("暴击伤害加成 (默认: 1.2)").defineInRange("critDamage", 1.2, -1, 100);
            builder.pop();
            
            
            builder.comment("氩晶瞄具饰品配置").push("argon_scope");
            argonScopeBaseCritChance = builder.comment("基础暴击几率 (基础值: 0.15)").defineInRange("baseCritChance", 0.15, -1, 100);
            argonScopeDuration = builder.comment("Buff持续时间(秒) (基础值: 9)").defineInRange("duration", 9, 1, 300);
            builder.pop();
            
            
            builder.comment("镀层氩晶瞄具饰品配置").push("gilded_argon_scope");
            gildedArgonScopeBaseCritChance = builder.comment("基础暴击几率 (基础值: 0.11)").defineInRange("baseCritChance", 0.11, -1, 100);
            gildedArgonScopeCritChancePerLevel = builder.comment("叠层Buff每级暴击几率 (基础值: 0.033, 满级: +40%/级)").defineInRange("critChancePerLevel", 0.033, -1, 100);
            gildedArgonScopeHeadshotKillExtra = builder.comment("爆头击杀暴击率额外加成 (基础值: 0.4)").defineInRange("headshotKillExtra", 0.4, -1, 100);
            gildedArgonScopeDuration = builder.comment("Buff持续时间(秒) (基础值: 12)").defineInRange("duration", 12, 1, 300);
            gildedArgonScopeMaxStacks = builder.comment("最大buff等级 (基础值: 60)").defineInRange("maxStacks", 60, 1, 200);
            builder.pop();
            
            
            builder.comment("尖刃弹头饰品配置").push("sharp_bullet");
            sharpBulletBaseCritDamage = builder.comment("基础暴击伤害 (基础值: 0.13)").defineInRange("baseCritDamage", 0.13, -1, 100);
            sharpBulletDuration = builder.comment("Buff持续时间(秒) (默认: 9)").defineInRange("duration", 9, 1, 300);
            builder.pop();
            
            
            builder.comment("镀层步枪才能饰品配置").push("gilded_rifle_aptitude");
            gildedRifleAptitudePerHarmful = builder.comment("每负面效果种数增伤比例 (默认: 0.4)").defineInRange("perHarmful", 0.4, -1, 100);
            builder.pop();
            
            
            builder.comment("镀层分裂膛室饰品配置").push("gilded_split_chamber");
            gildedSplitChamberBulletCountBase = builder.comment("基础弹头数量加成 (基础值: 0.8)").defineInRange("bulletCountBase", 0.8, -1, 100);
            gildedSplitChamberBulletCountPerLevel = builder.comment("叠层Buff每级额外弹头数量 (基础值: 0.025, 满级: +30%/级)").defineInRange("bulletCountPerLevel", 0.025, -1, 100);
            gildedSplitChamberDuration = builder.comment("Buff持续时间(秒) (基础值: 20)").defineInRange("duration", 20, 1, 300);
            gildedSplitChamberMaxStacks = builder.comment("最大buff等级 (基础值: 60)").defineInRange("maxStacks", 60, 1, 200);
            builder.pop();
            
            
            builder.comment("破灭饰品配置").push("destruction");
            destructionCritDamage = builder.comment("暴击伤害加成 (默认: 0.6)").defineInRange("critDamage", 0.6, -1, 100);
            builder.pop();
            
            
            builder.comment("破灭Prime饰品配置").push("destruction_prime");
            destructionPrimeCritDamage = builder.comment("暴击伤害加成 (默认: 1.1)").defineInRange("critDamage", 1.1, -1, 100);
            builder.pop();
            
            
            builder.comment("雷筒饰品配置").push("thunder_barrel");
            thunderBarrelCritChance = builder.comment("暴击几率加成 (默认: 0.9)").defineInRange("critChance", 0.9, -1, 100);
            builder.pop();
            
            
            builder.comment("雷筒Prime饰品配置").push("thunder_barrel_prime");
            thunderBarrelPrimeCritChance = builder.comment("暴击几率加成 (默认: 1.65)").defineInRange("critChance", 1.65, -1, 100);
            builder.pop();
            
            
            builder.comment("雷射瞄具饰品配置").push("laser_scope");
            laserScopeBaseCritChance = builder.comment("基础暴击几率 (基础值: 0.13)").defineInRange("baseCritChance", 0.13, -1, 100);
            laserScopeDuration = builder.comment("Buff持续时间(秒) (默认: 9)").defineInRange("duration", 9, 1, 300);
            builder.pop();
            
            
            builder.comment("破片射击饰品配置").push("fragment_shot");
            fragmentShotBaseCritDamage = builder.comment("基础暴击伤害 (基础值: 0.11)").defineInRange("baseCritDamage", 0.11, -1, 100);
            fragmentShotDuration = builder.comment("Buff持续时间(秒) (默认: 9)").defineInRange("duration", 9, 1, 300);
            builder.pop();
            
            
            builder.comment("镀层通晓霰弹枪饰品配置").push("gilded_shotgun_savvy");
            gildedShotgunSavvyPerHarmful = builder.comment("每负面效果种数增伤比例 (默认: 0.4)").defineInRange("perHarmful", 0.4, -1, 100);
            builder.pop();
            
            
            builder.comment("镀层地狱弹膛饰品配置").push("gilded_infernal_chamber");
            gildedInfernalChamberBulletCountBase = builder.comment("基础弹头数量加成 (基础值: 1.1)").defineInRange("bulletCountBase", 1.1, -1, 100);
            gildedInfernalChamberBulletCountPerLevel = builder.comment("叠层Buff每级额外弹头数量 (基础值: 0.025, 满级: +30%/级)").defineInRange("bulletCountPerLevel", 0.025, -1, 100);
            gildedInfernalChamberDuration = builder.comment("Buff持续时间(秒) (基础值: 20)").defineInRange("duration", 20, 1, 300);
            gildedInfernalChamberMaxStacks = builder.comment("最大buff等级 (基础值: 60)").defineInRange("maxStacks", 60, 1, 200);
            builder.pop();
            
            
            builder.comment("弱点专精饰品配置").push("weakness_mastery");
            weaknessMasteryCritDamage = builder.comment("暴击伤害加成 (默认: 0.6)").defineInRange("critDamage", 0.6, -1, 100);
            builder.pop();
            
            
            builder.comment("弱点专精Prime饰品配置").push("weakness_mastery_prime");
            weaknessMasteryPrimeCritDamage = builder.comment("暴击伤害加成 (默认: 1.1)").defineInRange("critDamage", 1.1, -1, 100);
            builder.pop();
            
            
            builder.comment("空尖弹饰品配置").push("hollow_point");
            hollowPointCritDamage = builder.comment("暴击伤害加成 (默认: 0.6)").defineInRange("critDamage", 0.6, -1, 100);
            hollowPointPistolDamageReduction = builder.comment("手枪伤害降低 (默认: -0.15)").defineInRange("pistolDamageReduction", -0.15, -1, 0);
            builder.pop();
            
            
            builder.comment("手枪精通饰品配置").push("pistol_mastery");
            pistolMasteryCritChance = builder.comment("暴击几率加成 (默认: 1.2)").defineInRange("critChance", 1.2, -1, 100);
            builder.pop();
            
            
            builder.comment("手枪精通Prime饰品配置").push("pistol_mastery_prime");
            pistolMasteryPrimeCritChance = builder.comment("暴击几率加成 (默认: 1.87)").defineInRange("critChance", 1.87, -1, 100);
            builder.pop();
            
            
            builder.comment("液压准心饰品配置").push("hydraulic_crosshair");
            hydraulicCrosshairBaseCritChance = builder.comment("基础暴击几率 (基础值: 0.15)").defineInRange("baseCritChance", 0.15, -1, 100);
            hydraulicCrosshairDuration = builder.comment("Buff持续时间(秒) (默认: 9)").defineInRange("duration", 9, 1, 300);
            builder.pop();
            
            
            builder.comment("镀层液压准心饰品配置").push("gilded_hydraulic_crosshair");
            gildedHydraulicCrosshairBaseCritChance = builder.comment("基础暴击几率 (基础值: 0.11)").defineInRange("baseCritChance", 0.11, -1, 100);
            gildedHydraulicCrosshairCritChancePerLevel = builder.comment("叠层Buff每级暴击几率 (基础值: 0.033, 满级: +40%/级)").defineInRange("critChancePerLevel", 0.033, -1, 100);
            gildedHydraulicCrosshairHeadshotKillExtra = builder.comment("爆头击杀暴击率额外加成 (基础值: 0.4)").defineInRange("headshotKillExtra", 0.4, -1, 100);
            gildedHydraulicCrosshairDuration = builder.comment("Buff持续时间(秒) (基础值: 12)").defineInRange("duration", 12, 1, 300);
            gildedHydraulicCrosshairMaxStacks = builder.comment("最大buff等级 (基础值: 60)").defineInRange("maxStacks", 60, 1, 200);
            builder.pop();
            
            
            builder.comment("尖锐子弹饰品配置").push("sharp_ammo");
            sharpAmmoBaseCritDamage = builder.comment("基础暴击伤害 (基础值: 0.08)").defineInRange("baseCritDamage", 0.08, -1, 100);
            sharpAmmoDuration = builder.comment("Buff持续时间(秒) (默认: 9)").defineInRange("duration", 9, 1, 300);
            builder.pop();
            
            
            builder.comment("镀层准确射手饰品配置").push("gilded_marksman");
            gildedMarksmanPerHarmful = builder.comment("每负面效果种数增伤比例 (默认: 0.4)").defineInRange("perHarmful", 0.4, -1, 100);
            builder.pop();
            
            
            builder.comment("镀层弹头扩散饰品配置").push("gilded_bullet_spread");
            gildedBulletSpreadBulletCountBase = builder.comment("基础弹头数量加成 (基础值: 1.1)").defineInRange("bulletCountBase", 1.1, -1, 100);
            gildedBulletSpreadBulletCountPerLevel = builder.comment("叠层Buff每级额外弹头数量 (基础值: 0.025, 满级: +30%/级)").defineInRange("bulletCountPerLevel", 0.025, -1, 100);
            gildedBulletSpreadDuration = builder.comment("Buff持续时间(秒) (基础值: 20)").defineInRange("duration", 20, 1, 300);
            gildedBulletSpreadMaxStacks = builder.comment("最大buff等级 (基础值: 48)").defineInRange("maxStacks", 48, 1, 200);
            builder.pop();
            
            
            builder.comment("斩铁饰品配置").push("steel_slash");
            steelSlashCritChance = builder.comment("暴击几率加成 (默认: 1.2)").defineInRange("critChance", 1.2, -1, 100);
            builder.pop();
            
            
            builder.comment("肢解饰品配置").push("dismemberment");
            dismembermentCritDamage = builder.comment("暴击伤害加成 (默认: 0.9)").defineInRange("critDamage", 0.9, -1, 100);
            builder.pop();
            
            
            builder.comment("牺牲压迫点饰品配置").push("sacrifice_oppression");
            sacrificeOppressionMeleeDamage = builder.comment("近战伤害加成 (默认: 1.1)").defineInRange("meleeDamage", 1.1, -1, 100);
            builder.pop();
            
            
            builder.comment("牺牲斩铁饰品配置").push("sacrifice_steel");
            sacrificeSteelCritChance = builder.comment("暴击几率加成 (默认: 2.2)").defineInRange("critChance", 2.2, -1, 100);
            builder.pop();
            
            
            builder.comment("镀层斩铁饰品配置").push("gilded_steel_slash");
            gildedSteelSlashCritChanceBase = builder.comment("基础暴击几率 (基础值: 1.1)").defineInRange("critChanceBase", 1.1, -1, 100);
            gildedSteelSlashCritDamagePerLevel = builder.comment("叠层Buff每级暴击伤害 (基础值: 0.025, 满级: +30%/级)").defineInRange("critDamagePerLevel", 0.025, -1, 100);
            gildedSteelSlashDuration = builder.comment("Buff持续时间(秒) (基础值: 20)").defineInRange("duration", 20, 1, 300);
            gildedSteelSlashMaxStacks = builder.comment("最大buff等级 (基础值: 48)").defineInRange("maxStacks", 48, 1, 200);
            builder.pop();
            
            
            builder.comment("异况超量饰品配置").push("condition_overload");
            conditionOverloadPerHarmful = builder.comment("每负面效果种数增伤比例 (默认: 0.8)").defineInRange("perHarmful", 0.8, -1, 100);
            builder.pop();
            
            
            builder.comment("牺牲套装组合配置").push("sacrifice_set");
            sacrificeSetBonus = builder.comment("套装加成倍率 (默认: 1.25)").defineInRange("setBonus", 1.25, -1, 100);
            builder.pop();

            
        builder.comment("烈焰风暴饰品配置").push("blaze_storm");
        blazeStormExplosionRadiusBoost = builder
                .comment("爆炸范围加成 (默认: 0.24 = 24%)")
                .defineInRange("explosionRadiusBoost", 0.24, -1, 100);
        blazeStormExplosionDamageBoost = builder
                .comment("爆炸伤害加成 (默认: 0.24 = 24%)")
                .defineInRange("explosionDamageBoost", 0.24, -1, 100);
        blazeStormExplosionEnabled = builder
                .comment("爆炸启用属性 (默认: 2.0)")
                .defineInRange("explosionEnabled", 2.0, -1, 100);
        builder.pop();
        
        
        builder.comment("烈焰风暴Prime饰品配置").push("blaze_storm_prime");
        blazeStormPrimeExplosionRadiusBoost = builder
                .comment("爆炸范围加成 (默认: 0.66 = 66%)")
                .defineInRange("explosionRadiusBoost", 0.66, -1, 100);
        blazeStormPrimeExplosionDamageBoost = builder
                .comment("爆炸伤害加成 (默认: 0.66 = 66%)")
                .defineInRange("explosionDamageBoost", 0.66, -1, 100);
        blazeStormPrimeExplosionEnabled = builder
                .comment("爆炸启用属性 (默认: 2.0)")
                .defineInRange("explosionEnabled", 2.0, -1, 100);
        builder.pop();
            
            
            builder.comment("撕裂Prime饰品配置").push("ripping_prime");
            rippingPrimeFireRateBoost = builder
                    .comment("射速加成 (默认: 0.55 = 55%)")
                    .defineInRange("fireRateBoost", 0.55, -1, 100);
            rippingPrimePenetrationBoost = builder
                    .comment("穿透加成 (默认: 2.2)")
                    .defineInRange("penetrationBoost", 2.2, -1, 100);
            builder.pop();
            
            
            builder.comment("抵近射击Prime饰品配置").push("close_combat_prime");
            closeCombatPrimeShotgunDamageBoost = builder
                    .comment("霰弹枪伤害加成 (默认: 1.65 = 165%)")
                    .defineInRange("shotgunDamageBoost", 1.65, -1, 100);
            builder.pop();
            
            
            builder.comment("极恶精准饰品配置").push("evil_accuracy");
            evilAccuracyRecoilReduction = builder
                    .comment("后坐力降低 (默认: -0.9 = -90%)")
                    .defineInRange("recoilReduction", -0.9, -1, 1);
            evilAccuracyFireRateReduction = builder
                    .comment("射速降低 (默认: -0.36 = -36%)")
                    .defineInRange("fireRateReduction", -0.36, -1, 0);
            builder.pop();
            
            
            builder.comment("极限速度饰品配置").push("limit_speed");
            limitSpeedBulletSpeedBoost = builder
                    .comment("弹药速度加成 (默认: 0.6 = 60%)")
                    .defineInRange("bulletSpeedBoost", 0.6, -1, 100);
            builder.pop();
            
            
            builder.comment("凶恶延伸饰品配置").push("ferocious_extension");
            ferociousExtensionRangeBoost = builder
                    .comment("子弹射程加成 (默认: 1.2 = 120%)")
                    .defineInRange("rangeBoost", 1.2, -1, 100);
            builder.pop();
            
            
            builder.comment("抵近射击饰品配置").push("close_range_shot");
            closeRangeShotDamageBoost = builder
                    .comment("霰弹枪伤害加成 (默认: 0.9 = 90%)")
                    .defineInRange("damageBoost", 0.9, -1, 100);
            builder.pop();
            
            
            builder.comment("重装火力饰品配置").push("heavy_firepower");
            heavyFirepowerDamageBoost = builder
                    .comment("手枪伤害加成 (默认: +1.65 = +165%)")
                    .defineInRange("damageBoost", 1.65, -1, 100);
            heavyFirepowerAccuracyReduction = builder
                    .comment("扩散程度增加 (默认: +0.55 = +55%)")
                    .defineInRange("accuracyReduction", 0.55, -1, 1);
            builder.pop();
            
            
            builder.comment("黄蜂蜇刺饰品配置").push("wasp_stinger");
            waspStingerDamageBoost = builder
                    .comment("手枪伤害加成 (默认: 2.2 = 220%)")
                    .defineInRange("damageBoost", 2.2, -1, 100);
            builder.pop();
            
            
            builder.comment("预言契约饰品配置").push("prophecy_pact");
            prophecyPactDamageBoost = builder
                    .comment("手枪伤害加成 (默认: 0.9 = 90%)")
                    .defineInRange("damageBoost", 0.9, -1, 100);
            builder.pop();
            
            
            builder.comment("恶性扩散饰品配置").push("malignant_spread");
            malignantSpreadDamageBoost = builder
                    .comment("霰弹枪伤害加成 (默认: +1.65 = +165%)")
                    .defineInRange("damageBoost", 1.65, -1, 100);
            malignantSpreadAccuracyReduction = builder
                    .comment("扩散程度增加 (默认: +0.55 = +55%)")
                    .defineInRange("accuracyReduction", 0.55, -1, 1);
            builder.pop();
            
            
            builder.comment("膛室饰品配置").push("chamber");
            chamberSniperDamageBoost = builder
                    .comment("狙击枪伤害加成 (默认: 0.4 = 40%)")
                    .defineInRange("sniperDamageBoost", 0.4, -1, 100);
            builder.pop();
            
            
            builder.comment("膛室Prime饰品配置").push("chamber_prime");
            chamberPrimeSniperDamageBoost = builder
                    .comment("狙击枪伤害加成 (默认: 1.0 = 100%)")
                    .defineInRange("sniperDamageBoost", 1.0, -1, 100);
            builder.pop();
            
            
            builder.comment("战术上膛饰品配置").push("tactical_reload");
            tacticalReloadSpeedBoost = builder
                    .comment("霰弹枪装填时间加成 (默认: -0.6 = -60%)")
                    .defineInRange("reloadSpeedBoost", -0.6, -1, 100);
            builder.pop();
            
            
            builder.comment("过载弹匣饰品配置").push("overloaded_magazine");
            overloadedMagazineCapacityBoost = builder
                    .comment("霰弹枪弹匣容量加成 (默认: +0.6 = +60%)")
                    .defineInRange("capacityBoost", 0.6, -1, 100);
            overloadedMagazineReloadSpeedReduction = builder
                    .comment("装填时间增加 (默认: +0.18 = +18%)")
                    .defineInRange("reloadSpeedReduction", 0.18, -1, 1);
            builder.pop();
            
            
            builder.comment("地狱弹膛饰品配置").push("infernal_chamber");
            infernalChamberBulletCountBoost = builder
                    .comment("霰弹枪弹头数量加成 (默认: 1.2 = 120%)")
                    .defineInRange("bulletCountBoost", 1.2, -1, 100);
            builder.pop();
            
            
            builder.comment("持续火力饰品配置").push("sustained_fire");
            sustainedFireReloadSpeedBoost = builder
                    .comment("手枪装填时间加成 (默认: -0.48 = -48%)")
                    .defineInRange("reloadSpeedBoost", -0.48, -1, 100);
            builder.pop();
            
            
            builder.comment("感染弹匣饰品配置").push("infected_magazine");
            infectedMagazineCapacityBoost = builder
                    .comment("手枪弹匣容量加成 (默认: +0.6 = +60%)")
                    .defineInRange("capacityBoost", 0.6, -1, 100);
            infectedMagazineReloadSpeedReduction = builder
                    .comment("装填时间增加 (默认: +0.3 = +30%)")
                    .defineInRange("reloadSpeedReduction", 0.3, -1, 1);
            builder.pop();
            
            
            builder.comment("致命洪流饰品配置").push("deadly_surge");
            deadlySurgeFireRateBoost = builder
                    .comment("手枪射速加成 (默认: 0.6 = 60%)")
                    .defineInRange("fireRateBoost", 0.6, -1, 100);
            deadlySurgeBulletCountBoost = builder
                    .comment("弹头数量加成 (默认: 0.6 = 60%)")
                    .defineInRange("bulletCountBoost", 0.6, -1, 100);
            builder.pop();
            
            
            builder.comment("弹头扩散饰品配置").push("bullet_spread");
            bulletSpreadBulletCountBoost = builder
                    .comment("手枪弹头数量加成 (默认: 1.2 = 120%)")
                    .defineInRange("bulletCountBoost", 1.2, -1, 100);
            builder.pop();
            
            
            builder.comment("压迫点饰品配置").push("oppression_point");
            oppressionPointMeleeDamageBoost = builder
                    .comment("近战伤害加成 (默认: 1.2 = 120%)")
                    .defineInRange("meleeDamageBoost", 1.2, -1, 100);
            builder.pop();
            
            
            builder.comment("压迫点Prime饰品配置").push("oppression_point_prime");
            oppressionPointPrimeMeleeDamageBoost = builder
                    .comment("近战伤害加成 (默认: 1.65 = 165%)")
                    .defineInRange("meleeDamageBoost", 1.65, -1, 100);
            builder.pop();
            
            
            builder.comment("爆发装填饰品配置").push("burst_reload");
            burstReloadReloadSpeedBoost = builder
                    .comment("装填时间加成 (默认: -0.3 = -30%)")
                    .defineInRange("reloadSpeedBoost",-0.3, -1, 100);
            builder.pop();
            
            
            builder.comment("剑风饰品配置").push("sword_wind");
            swordWindMeleeRangeBoost = builder
                    .comment("近战距离加成 (默认: 1.1)")
                    .defineInRange("meleeRangeBoost", 1.1, -1, 100);
            builder.pop();
            
            
            builder.comment("剑风Prime饰品配置").push("sword_wind_prime");
            swordWindPrimeMeleeRangeBoost = builder
                    .comment("近战距离加成 (默认: 3)")
                    .defineInRange("meleeRangeBoost", 3.0, -1, 100);
            builder.pop();
            
            
            builder.comment("腐败弹匣饰品配置").push("corrupt_magazine");
            corruptMagazineCapacityBoost = builder
                    .comment("弹匣容量加成 (默认: +0.66 = +66%)")
                    .defineInRange("capacityBoost", 0.66, -1, 100);
            corruptMagazineReloadSpeedReduction = builder
                    .comment("装填时间增加 (默认: +0.33 = +33%)")
                    .defineInRange("reloadSpeedReduction", 0.33, -1, 1);
            builder.pop();
            
            
            builder.comment("重口径饰品配置").push("heavy_caliber_tag");
            heavyCaliberTagDamageBoost = builder
                    .comment("特定枪械伤害加成 (默认: +1.65 = +165%)")
                    .defineInRange("damageBoost", 1.65, -1, 100);
            heavyCaliberTagInaccuracyBoost = builder
                    .comment("扩散程度加成 (默认: +0.55 = +55%)")
                    .defineInRange("inaccuracyBoost", 0.55, -1, 100);
            builder.pop();
            
            
            builder.comment("红-有-三饰品配置").push("red_movement_tag");
            redMovementTagSpeedBoost = builder
                    .comment("移动速度加成 (默认: 1.5 = 150%)")
                    .defineInRange("speedBoost", 1.5, -1, 100);
            builder.pop();
            
            
            builder.comment("希奥拉饰品配置").push("xiora");
            xioraArmorMultiplier = builder
                    .comment("护甲乘数 (默认: -0.2 = 护甲降低20%)")
                    .defineInRange("armorMultiplier", -0.2, -1, 100);
            xioraSpeedMultiplier = builder
                    .comment("移速乘数 (默认: 0.5 = +50%)")
                    .defineInRange("speedMultiplier", 0.5, -1, 100);
            builder.pop();

            
            builder.comment("夜袭渡鸦饰品配置").push("raven");
            ravenArmorMultiplier = builder
                    .comment("护甲乘数 (默认: -0.4 = 护甲降低40%)")
                    .defineInRange("armorMultiplier", -0.4, -1, 100);
            ravenSpeedMultiplier = builder
                    .comment("移速乘数 (默认: 1.0 = +100%)")
                    .defineInRange("speedMultiplier", 1.0, -1, 100);
            ravenInvisRefreshInterval = builder
                    .comment("隐身刷新间隔 (tick, 默认: 200 = 10秒)")
                    .defineInRange("invisRefreshInterval", 200, 1, 12000);
            ravenInvisDuration = builder
                    .comment("隐身效果持续时间 (tick, 默认: 600 = 30秒)")
                    .defineInRange("invisDuration", 600, 1, 12000);
            ravenInvisBreakDelay = builder
                    .comment("攻击后隐身破除延迟 (tick, 默认: 100 = 5秒)")
                    .defineInRange("invisBreakDelay", 100, 0, 12000);
            builder.pop();

            
            builder.comment("岛爆渡鸦饰品配置").push("island_boom_raven");
            islandBoomRavenArmorMultiplier = builder
                    .comment("护甲乘数 (默认: -0.4 = 护甲降低40%)")
                    .defineInRange("armorMultiplier", -0.4, -1, 100);
            islandBoomRavenSpeedMultiplier = builder
                    .comment("移速乘数 (默认: 1.0 = +100%)")
                    .defineInRange("speedMultiplier", 1.0, -1, 100);
            islandBoomRavenInvisRefreshInterval = builder
                    .comment("隐身刷新间隔 (tick, 默认: 200 = 10秒)")
                    .defineInRange("invisRefreshInterval", 200, 1, 12000);
            islandBoomRavenInvisDuration = builder
                    .comment("隐身效果持续时间 (tick, 默认: 600 = 30秒)")
                    .defineInRange("invisDuration", 600, 1, 12000);
            islandBoomRavenInvisBreakDelay = builder
                    .comment("攻击后隐身破除延迟 (tick, 默认: 100 = 5秒)")
                    .defineInRange("invisBreakDelay", 100, 0, 12000);
            islandBoomRavenRegenAmplifier = builder
                    .comment("生命恢复等级 (默认: 1 = 再生 II)")
                    .defineInRange("regenAmplifier", 1, 0, 10);
            islandBoomRavenRegenRefreshThreshold = builder
                    .comment("生命恢复刷新阈值 (tick, 低于此时长重新施加, 默认: 40 = 2秒)")
                    .defineInRange("regenRefreshThreshold", 40, 1, 12000);
            islandBoomRavenRegenDuration = builder
                    .comment("生命恢复持续时间 (tick, 默认: 120 = 6秒)")
                    .defineInRange("regenDuration", 120, 1, 12000);
            builder.pop();
            
            
            builder.comment("夏日沙滩饰品配置").push("summer_beach");
            builder.pop();
            
            
            builder.comment("救世饰品配置").push("salvation");
            salvationDamageReduction = builder
                    .comment("救世伤害减免比例 (默认: 0.5 = 减免50%)")
                    .defineInRange("damageReduction", 0.5, 0, 1);
            salvationResistanceLevel = builder
                    .comment("救世抗性提升等级 (默认: 2 = 抗性III)")
                    .defineInRange("resistanceLevel", 2, 0, 10);
            builder.pop();
            
            
            builder.comment("无烬终焉饰品配置").push("endless");
            endlessDamageBoost = builder
                    .comment("无烬终焉通用枪械伤害加成 (默认: 1.0 = 100%)")
                    .defineInRange("damageBoost", 1.0, -1, 100);
            endlessImaginaryResistanceDamagePerPoint = builder
                    .comment("每点虚数抗性提升的枪械伤害百分比 (默认: 1.0 = 每点+1%)")
                    .defineInRange("imaginaryResistanceDamagePerPoint", 1.0, 0.0, 100.0);
            endlessExplosionDamage = builder
                    .comment("无烬终焉爆炸伤害加成 (默认: 1.0 = 100%，与天火劫灭一致)")
                    .defineInRange("explosionDamage", 1.0, -1, 100);
            endlessNearbyPlayerDamageBoost = builder
                    .comment("附近玩家获得的 bullet_gundamage 每级伤害加成 (默认: 1.0 = 100%/级)")
                    .defineInRange("nearbyPlayerDamageBoost", 1.0, -1, 100);
            endlessNearbyPlayerPotionAmplifier = builder
                    .comment("附近玩家获得的药水效果等级 (0=1级, 默认: 2 = 3级 = 3×100%% = 300%%)")
                    .defineInRange("nearbyPlayerPotionAmplifier", 2, 0, 999);
            endlessNearbyPlayerDuration = builder
                    .comment("附近玩家获得伤害加成的持续时间(秒) (默认: 15)")
                    .defineInRange("nearbyPlayerDuration", 15, -1, 300);
            endlessNearbyPlayerRadius = builder
                    .comment("影响附近玩家的范围 (默认: 32)")
                    .defineInRange("nearbyPlayerRadius", 32.0, -1, 100);
            builder.pop();
            
            
            builder.comment("士兵基础挂牌饰品配置").push("soldier_basic_tag");
            soldierBasicTagDamageBoost = builder
                    .comment("通用枪械伤害加成 (默认: 0.3 = 30%)")
                    .defineInRange("damageBoost", 0.3, -1, 100);
            builder.pop();
            
            
            builder.comment("弹匣增幅饰品配置").push("magazine_boost");
            magazineBoostReloadSpeedBoost = builder
                    .comment("装填时间加成 (默认: -0.3 = -30%)")
                    .defineInRange("reloadSpeedBoost",-0.3, -1, 100);
            magazineBoostCapacityBoost = builder
                    .comment("弹匣容量加成 (默认: 0.3 = 30%)")
                    .defineInRange("capacityBoost", 0.3, -1, 100);
            builder.pop();
            
            
            builder.comment("士兵特定挂牌饰品配置").push("soldier_specific_tag");
            soldierSpecificTagDamageBoost = builder
                    .comment("通用枪械伤害加成 (默认: 0.55 = 55%)")
                    .defineInRange("damageBoost", 0.55, -1, 100);
            builder.pop();
            
            
            builder.comment("乌拉尔银狼饰品配置").push("ural_wolf_tag");
            uralWolfTagHeadshotMultiplierBoost = builder
                    .comment("爆头倍率加成 (默认: 1.5 = 150%)")
                    .defineInRange("headshotMultiplierBoost", 1.5, -1, 100);
            builder.pop();
            
            
            builder.comment("耗竭装填饰品配置").push("depleted_reload");
            depletedReloadMagazineCapacityPenalty = builder
                    .comment("弹匣容量减少 (默认: -0.6 = -60%)")
                    .defineInRange("magazineCapacityPenalty", -0.6, -1, 0);
            depletedReloadReloadSpeedBoost = builder
                    .comment("装填时间加成 (默认: -0.48 = -48%)")
                    .defineInRange("reloadSpeedBoost", -0.48, -1, 100);
            builder.pop();
            
            
            builder.comment("爆发装填Prime饰品配置").push("burst_reload_prime");
            burstReloadPrimeReloadSpeedBoost = builder
                    .comment("装填时间加成 (默认: -0.55 = -55%)")
                    .defineInRange("reloadSpeedBoost", -0.55, -1, 100);
            builder.pop();
            
            
            builder.comment("战术上膛Prime饰品配置").push("tactical_reload_prime");
            tacticalReloadPrimeReloadSpeedBoost = builder
                    .comment("装填时间加成 (默认: -0.9 = -90%)")
                    .defineInRange("reloadSpeedBoost", -0.9, -1, 100);
            builder.pop();
            
            
            builder.comment("霰弹扩充Prime饰品配置").push("shotgun_expansion_prime");
            shotgunExpansionPrimeCapacityBoost = builder
                    .comment("弹匣容量加成 (默认: 1.1 = 110%)")
                    .defineInRange("capacityBoost", 1.1, -1, 100);
            builder.pop();
            
            
            builder.comment("弹匣增幅Prime饰品配置").push("magazine_boost_prime");
            magazineBoostPrimeCapacityBoost = builder
                    .comment("弹匣容量加成 (默认: 0.55 = 55%)")
                    .defineInRange("capacityBoost", 0.55, -1, 100);
            builder.pop();
            
            
            builder.comment("串联弹匣Prime饰品配置").push("tandem_magazine_prime");
            tandemMagazinePrimeCapacityBoost = builder
                    .comment("弹匣容量加成 (默认: 0.55 = 55%)")
                    .defineInRange("capacityBoost", 0.55, -1, 100);
            builder.pop();
            
            
            builder.comment("霰弹扩充饰品配置").push("shotgun_expansion");
            shotgunExpansionCapacityBoost = builder
                    .comment("弹匣容量加成 (默认: 0.6 = 60%)")
                    .defineInRange("capacityBoost", 0.6, -1, 100);
            builder.pop();
            

            
            builder.comment("串联弹匣饰品配置").push("tandem_magazine");
            tandemMagazineCapacityBoost = builder
                    .comment("弹匣容量加成 (默认: 0.3 = 30%)")
                    .defineInRange("capacityBoost", 0.3, -1, 100);
            builder.pop();
            
            
            builder.comment("掎角一阵饰品配置").push("kikaku_ichijin");
            kikakuIchijinHealthMultiplier = builder
                    .comment("伤害倍率，基于祭品总血量的比例 (默认: 0.2 = 20%，范围: 0.2-1.0)")
                    .defineInRange("healthMultiplier", 0.2, 0.2, 1.0);
            kikakuIchijinDestroyUnbreakableBlocks = builder
                    .comment("是否破坏不可破坏方块（如基岩） (默认: false)")
                    .define("destroyUnbreakableBlocks", false);
            kikakuIchijinDestroyNormalBlocks = builder
                    .comment("是否破坏普通方块 (默认: false)")
                    .define("destroyNormalBlocks", false);
            builder.pop();
            
            
            builder.comment("吸收饰品通用配置（低血量触发黄心）").push("curio_absorption");
            curioAbsorptionTriggerHp = builder
                    .comment("触发血量阈值 (0~1, 默认: 0.25 = 25%)")
                    .defineInRange("triggerHp", 0.25, 0, 1);
            curioAbsorptionLevel = builder
                    .comment("吸收效果等级 (默认: 4 = ABSORPTION IV)")
                    .defineInRange("absorptionLevel", 4, 1, 255);
            curioAbsorptionDuration = builder
                    .comment("吸收效果持续时间(秒) (默认: 60)")
                    .defineInRange("absorptionDuration", 60.0, 1, 2147483647);
            curioAbsorptionCooldown = builder
                    .comment("吸收效果冷却(秒) (默认: 180)")
                    .defineInRange("cooldown", 180.0, 0, 2147483647);
            builder.pop();

            
            builder.comment("格蕾修饰品配置").push("griseo");
            griseoHurtCooldownTicks = builder
                    .comment("受伤冷却基础冷却(tick) (默认: 10 = 0.5秒)")
                    .defineInRange("hurtCooldownTicks", 10, 1, 12000);
            builder.pop();

            
            builder.comment("千界一乘饰品配置").push("qianjie_yicheng");
            qianjieYichengLuck = builder
                    .comment("幸运值加成 (默认: 20)")
                    .defineInRange("luck", 20, 0, 1000);
            builder.pop();

            
            builder.comment("绘世之卷饰品配置").push("huishi_zhijuan");
            huishiZhijuanBaseCooldown = builder
                    .comment("受伤冷却基础冷却(tick) (默认: 10)")
                    .defineInRange("baseCooldown", 10, 1, 12000);
            huishiZhijuanLuckPerTick = builder
                    .comment("每2点幸运值增加的冷却(tick) (默认: 1)")
                    .defineInRange("luckPerTick", 1, 0, 100);
            huishiZhijuanMaxCooldown = builder
                    .comment("受伤冷却上限(tick) (默认: 40)")
                    .defineInRange("maxCooldown", 40, 1, 12000);
            builder.pop();

            
            builder.comment("永劫之键饰品配置").push("yongjie_zhijian");
            yongjieZhijianLuck = builder
                    .comment("幸运值加成 (默认: 20)")
                    .defineInRange("luck", 20, 0, 1000);
            yongjieZhijianCritChancePerLuck = builder
                    .comment("每点幸运值增加的暴击率(add) (默认: 0.01)")
                    .defineInRange("critChancePerLuck", 0.01, 0, 100);
            yongjieZhijianCritDamagePerLuck = builder
                    .comment("每点幸运值增加的暴击伤害(add) (默认: 0.02)")
                    .defineInRange("critDamagePerLuck", 0.02, 0, 100);
            builder.pop();

            
            builder.comment("繁星饰品配置").push("fanxing");
            fanxingBaseCooldown = builder
                    .comment("受伤冷却基础冷却(tick) (默认: 20)")
                    .defineInRange("baseCooldown", 20, 1, 12000);
            fanxingLuckPerTick = builder
                    .comment("每2点幸运值增加的冷却(tick) (默认: 1)")
                    .defineInRange("luckPerTick", 1, 0, 100);
            fanxingMaxCooldown = builder
                    .comment("受伤冷却上限(tick) (默认: 120)")
                    .defineInRange("maxCooldown", 120, 1, 12000);
            fanxingLuckPerResistance = builder
                    .comment("每点虚数抗性提供的幸运值 (默认: 1.0)")
                    .defineInRange("luckPerResistance", 1.0, 0.0, 100.0);
            builder.pop();

            
            builder.comment("视界反演饰品配置").push("shijie_fanyan");
            shijieFanyanLuck = builder
                    .comment("幸运值加成 (默认: 40)")
                    .defineInRange("luck", 40, 0, 1000);
            shijieFanyanCritChancePerLuck = builder
                    .comment("每点幸运值增加的暴击率(add) (默认: 0.01)")
                    .defineInRange("critChancePerLuck", 0.01, 0, 100);
            shijieFanyanCritDamagePerLuck = builder
                    .comment("每点幸运值增加的暴击伤害(add) (默认: 0.02)")
                    .defineInRange("critDamagePerLuck", 0.02, 0, 100);
            shijieFanyanCollapseBaseChance = builder
                    .comment("虚数崩解基础触发概率 (默认: 0.05 = 5%)")
                    .defineInRange("collapseBaseChance", 0.05, 0, 1);
            shijieFanyanCollapsePerLuck = builder
                    .comment("每10点幸运值增加的崩解触发概率 (默认: 0.01 = 1%)")
                    .defineInRange("collapsePerLuck", 0.01, 0, 1);
            builder.pop();

            
            builder.comment("维尔薇饰品配置").push("vill_v");
            villVTriggerHpRatio = builder
                    .comment("触发吸收效果的血量阈值 (默认: 0.2 = 20%)")
                    .defineInRange("triggerHpRatio", 0.2, 0, 1);
            villVAbsorptionLevel = builder
                    .comment("吸收效果等级 (默认: 3 = ABSORPTION III)")
                    .defineInRange("absorptionLevel", 3, 1, 255);
            villVAbsorptionDuration = builder
                    .comment("吸收效果持续时间(秒) (默认: 60)")
                    .defineInRange("absorptionDuration", 60.0, 1, 3600);
            villVCooldownSeconds = builder
                    .comment("吸收触发冷却时间(秒) (默认: 60)")
                    .defineInRange("cooldownSeconds", 60.0, 1, 3600);
            builder.pop();

            
            builder.comment("虚空万藏饰品配置").push("xukong_wancang");
            xukongWancangImaginaryDamage = builder
                    .comment("攻击触发额外虚数伤害 (默认: 10.0)")
                    .defineInRange("imaginaryDamage", 10.0, 0, 10000);
            xukongWancangAmmoRegenPercent = builder
                    .comment("每秒弹药恢复比例 (默认: 0.05 = 5%)")
                    .defineInRange("ammoRegenPercent", 0.05, 0, 1);
            xukongWancangHeatMax = builder
                    .comment("过热上限倍率（基础乘法） (默认: 0.5)")
                    .defineInRange("heatMax", 0.5, 0.01, 1024.0);
            xukongWancangHeatCooling = builder
                    .comment("散热速度倍率（基础乘法） (默认: 0.2)")
                    .defineInRange("heatCooling", 0.2, 0.01, 1024.0);
            builder.pop();

            
            builder.comment("愚戏之匣饰品配置").push("yuxi_zhixia");
            yuxiZhixiaTriggerHpRatio = builder
                    .comment("触发吸收效果的血量阈值 (默认: 0.3 = 30%)")
                    .defineInRange("triggerHpRatio", 0.3, 0, 1);
            yuxiZhixiaAbsorptionLevel = builder
                    .comment("吸收效果等级 (默认: 6 = ABSORPTION VI)")
                    .defineInRange("absorptionLevel", 6, 1, 255);
            yuxiZhixiaAbsorptionDuration = builder
                    .comment("吸收效果持续时间(秒) (默认: 60)")
                    .defineInRange("absorptionDuration", 60.0, 1, 3600);
            yuxiZhixiaCooldownSeconds = builder
                    .comment("吸收触发冷却时间(秒) (默认: 60)")
                    .defineInRange("cooldownSeconds", 60.0, 1, 3600);
            builder.pop();

            
            builder.comment("启示之键饰品配置").push("qishi_zhijian");
            qishiZhijianImaginaryDamage = builder
                    .comment("攻击触发额外虚数伤害 (默认: 20.0)")
                    .defineInRange("imaginaryDamage", 20.0, 0, 10000);
            qishiZhijianAmmoRegenPercent = builder
                    .comment("每秒弹药恢复比例 (默认: 0.1 = 10%)")
                    .defineInRange("ammoRegenPercent", 0.1, 0, 1);
            qishiZhijianHeatMax = builder
                    .comment("过热上限倍率（基础乘法） (默认: 0.9)")
                    .defineInRange("heatMax", 0.9, 0.01, 1024.0);
            qishiZhijianHeatCooling = builder
                    .comment("散热速度倍率（基础乘法） (默认: 0.4)")
                    .defineInRange("heatCooling", 0.4, 0.01, 1024.0);
            builder.pop();

            
            builder.comment("螺旋饰品配置").push("luoxuan");
            luoxuanAbsorptionInterval = builder
                    .comment("吸收效果施加间隔(秒) (默认: 30)")
                    .defineInRange("absorptionInterval", 30, 1, 3600);
            luoxuanAbsorptionLevel = builder
                    .comment("吸收效果等级 (默认: 9 = ABSORPTION Ⅸ )")
                    .defineInRange("absorptionLevel", 9, 1, 255);
            luoxuanAbsorptionDuration = builder
                    .comment("吸收效果持续时间(秒) (默认: 30)")
                    .defineInRange("absorptionDuration", 30, 1, 3600);
            builder.pop();

            
            builder.comment("虚空万藏·雨众天华饰品配置").push("xukong_wancang_yzth");
            xukongWancangYZTHImaginaryDamageScale = builder
                    .comment("附加虚数伤害系数 (默认: 1.0；最终伤害 = 子弹基础伤害 × 虚数抗性/100 × 系数)")
                    .defineInRange("imaginaryDamageScale", 1.0, 0.0, 1000.0);
            xukongWancangYZTHAmmoRegenPercent = builder
                    .comment("每秒弹药恢复比例 (默认: 0.2 = 20%)")
                    .defineInRange("ammoRegenPercent", 0.2, 0, 1);
            xukongWancangYZTHInfectionDuration = builder
                    .comment("侵染效果持续时间(秒) (默认: 10)")
                    .defineInRange("infectionDuration", 10, 1, 3600);
            xukongWancangYZTHHeatMax = builder
                    .comment("过热上限倍率（基础乘法） (默认: 1.5)")
                    .defineInRange("heatMax", 1.5, 0.01, 1024.0);
            xukongWancangYZTHHeatCooling = builder
                    .comment("散热速度倍率（基础乘法） (默认: 0.8)")
                    .defineInRange("heatCooling", 0.8, 0.01, 1024.0);
            builder.pop();

            
            builder.comment("适应系统通用配置").push("adaptation");
            adaptationMaxCount = builder
                    .comment("同类型伤害适应最大叠加次数 (默认: 4，范围: 1~1000)")
                    .defineInRange("maxCount", 4, 1, 1000);
            builder.pop();

            
            builder.comment("千劫饰品配置").push("kalpas");
            kalpasMaxSlots = builder
                    .comment("适应最大槽位 (默认: 3)")
                    .defineInRange("maxSlots", 3, 1, 100);
            kalpasAdaptFactor = builder
                    .comment("每层减伤比例 (默认: 0.2 = 20%)")
                    .defineInRange("adaptFactor", 0.2, 0.0, 1.0);
            kalpasDecaySeconds = builder
                    .comment("适应衰减时间(秒) (默认: 20)")
                    .defineInRange("decaySeconds", 20, 1, 3600);
            builder.pop();

            
            builder.comment("伊默尔饰品配置").push("imer");
            imerAttackDamageBonus = builder
                    .comment("攻击伤害加成 (默认: 0.1 = 10%，MULTIPLY_BASE)")
                    .defineInRange("attackDamageBonus", 0.1, 0.0, 10.0);
            builder.pop();

            
            builder.comment("坏劫之焱饰品配置").push("huajie_zhiyan");
            huajieZhiyanMaxSlots = builder
                    .comment("适应最大槽位 (默认: 4)")
                    .defineInRange("maxSlots", 4, 1, 100);
            huajieZhiyanAdaptFactor = builder
                    .comment("每层减伤比例 (默认: 0.3 = 30%)")
                    .defineInRange("adaptFactor", 0.3, 0.0, 1.0);
            huajieZhiyanDecaySeconds = builder
                    .comment("适应衰减时间(秒) (默认: 20)")
                    .defineInRange("decaySeconds", 20, 1, 3600);
            huajieZhiyanHealthPerResistance = builder
                    .comment("每点虚数抗性提升的最大生命值 (默认: 1.0)")
                    .defineInRange("healthPerResistance", 1.0, 0.0, 1000.0);
            builder.pop();

            


            
            builder.comment("鏖灭饰品配置").push("aomie");
            aoMieMaxSlots = builder
                    .comment("适应最大槽位 (默认: 6)")
                    .defineInRange("maxSlots", 6, 1, 100);
            aoMieAdaptFactor = builder
                    .comment("每层减伤比例 (默认: 0.5 = 50%)")
                    .defineInRange("adaptFactor", 0.5, 0.0, 1.0);
            aoMieDecaySeconds = builder
                    .comment("适应衰减时间(秒) (默认: 20)")
                    .defineInRange("decaySeconds", 20, 1, 3600);
            aoMieHealthPerResistance = builder
                    .comment("每点虚数抗性提升的最大生命值 (默认: 2.0)")
                    .defineInRange("healthPerResistance", 2.0, 0.0, 1000.0);
            builder.pop();

            
            builder.comment("Meta-Morph饰品配置").push("meta_morph");
            metaMorphLifeStealPerResistance = builder
                    .comment("每点虚数抗性提供的生命偷取 (默认: 0.01)")
                    .defineInRange("lifeStealPerResistance", 0.01, 0.0, 1.0);
            metaMorphImaginaryDamageScale = builder
                    .comment("附加虚数伤害系数 (默认: 1.0；最终伤害 = 虚数抗性/100 × 攻击伤害 × 系数)")
                    .defineInRange("imaginaryDamageScale", 1.0, 0.0, 1000.0);
            builder.pop();

            
            builder.comment("苏饰品配置").push("su");
            suMaxHealthReduction = builder
                    .comment("最大生命值减少比例 (默认: -0.3)")
                    .defineInRange("maxHealthReduction", -0.3, -1.0, 0.0);
            suDamageTakenFactor = builder
                    .comment("受到伤害降低比例 (默认: 0.1 = 降低10%)")
                    .defineInRange("damageTakenFactor", 0.1, 0.0, 1.0);
            builder.pop();

            
            builder.comment("万物休眠饰品配置").push("wanwu_xiumian");
            wanwuXiumianOverheal = builder
                    .comment("超量治疗值 (默认: 0.3)")
                    .defineInRange("overheal", 0.3, 0.0, 10.0);
            wanwuXiumianAmmoRegenPercent = builder
                    .comment("每秒弹药恢复百分比 (默认: 0.05 = 5%)")
                    .defineInRange("ammoRegenPercent", 0.05, 0.0, 1.0);
            builder.pop();

            
            builder.comment("觉者饰品配置").push("juezhe");
            juezheMaxHealthReduction = builder
                    .comment("最大生命值减少比例 (默认: -0.4)")
                    .defineInRange("maxHealthReduction", -0.4, -1.0, 0.0);
            juezheDamageTakenFactor = builder
                    .comment("受到伤害降低比例 (默认: 0.3 = 降低30%)")
                    .defineInRange("damageTakenFactor", 0.3, 0.0, 1.0);
            builder.pop();

            
            builder.comment("停滞之键饰品配置").push("tingzhi_zhijian");
            tingzhiZhijianOverheal = builder
                    .comment("超量治疗值 (默认: 0.5)")
                    .defineInRange("overheal", 0.5, 0.0, 10.0);
            tingzhiZhijianAmmoBasePercent = builder
                    .comment("基础弹药恢复百分比 (默认: 0.1 = 10%)")
                    .defineInRange("ammoBasePercent", 0.1, 0.0, 1.0);
            tingzhiZhijianAmmoResistanceScale = builder
                    .comment("每点虚数抗性提供的弹药恢复系数 (默认: 0.005)")
                    .defineInRange("ammoResistanceScale", 0.005, 0.0, 1.0);
            builder.pop();

            
            builder.comment("天慧饰品配置").push("tianhui");
            tianhuiMaxHealthReduction = builder
                    .comment("最大生命值减少比例 (默认: -0.5)")
                    .defineInRange("maxHealthReduction", -0.5, -1.0, 0.0);
            tianhuiResistanceScale = builder
                    .comment("每点虚数抗性提供的伤害减免系数 (默认: 0.01 = 每点抗性减免1%伤害)")
                    .defineInRange("resistanceScale", 0.01, 0.0, 1.0);
            tianhuiMinDamageFactor = builder
                    .comment("最低伤害乘算因子 (默认: 0.1 = 10%)")
                    .defineInRange("minDamageFactor", 0.1, 0.0, 1.0);
            builder.pop();

            
            builder.comment("因果转轮饰品配置").push("yinguo_zhuanlun");
            yinguoZhuanlunOverheal = builder
                    .comment("超量治疗值 (默认: 1.0)")
                    .defineInRange("overheal", 1.0, 0.0, 10.0);
            yinguoZhuanlunAmmoResistanceScale = builder
                    .comment("每点虚数抗性提供的弹药恢复系数 (默认: 0.01)")
                    .defineInRange("ammoResistanceScale", 0.01, 0.0, 1.0);
            yinguoZhuanlunImaginaryDamageScale = builder
                    .comment("附加虚数伤害系数 (默认: 1.0；最终伤害 = 子弹基础伤害 × 虚数抗性/100 × 系数)")
                    .defineInRange("imaginaryDamageScale", 1.0, 0.0, 1000.0);
            builder.pop();

            
            builder.comment("逐火之蛾「真我」饰品配置").push("zhen_wo");
            zhenWoImaginaryResistance = builder
                    .comment("虚数抗性加成 (默认: 60)")
                    .defineInRange("imaginaryResistance", 60.0, -100, 100);
            zhenWoAllAttributesPercent = builder
                    .comment("全属性提升比例（乘法） (默认: 0.5 = +50%)")
                    .defineInRange("allAttributesPercent", 0.5, -1, 100);
            zhenWoTriggerHpRatio = builder
                    .comment("触发结界时的血量比例阈值 (默认: 0.05 = 5%)")
                    .defineInRange("triggerHpRatio", 0.05, 0.0, 1.0);
            zhenWoBarrierRadius = builder
                    .comment("结界影响半径（格） (默认: 64)")
                    .defineInRange("barrierRadius", 64.0, 1.0, 512.0);
            zhenWoSlownessAmplifier = builder
                    .comment("缓慢效果等级（0=缓慢I，默认: 8 = 缓慢IX）")
                    .defineInRange("slownessAmplifier", 8, 0, 255);
            zhenWoSlownessDurationSeconds = builder
                    .comment("缓慢效果持续时间（秒） (默认: 60)")
                    .defineInRange("slownessDurationSeconds", 60, 1, 600);
            zhenWoBarrierDurationSeconds = builder
                    .comment("结界持续时间（秒） (默认: 30)")
                    .defineInRange("barrierDurationSeconds", 30, 1, 600);
            zhenWoCooldownSeconds = builder
                    .comment("结界触发后的冷却时间（秒） (默认: 60)")
                    .defineInRange("cooldownSeconds", 60, 1, 3600);
            zhenWoDamageTakenFactor = builder
                    .comment("减伤（苏同款）：受到伤害降低比例 (默认: 0.8 = 降低80%)")
                    .defineInRange("damageTakenFactor", 0.8, 0.0, 1.0);
            builder.pop();

            


            

            
            builder.comment("阿波尼亚饰品配置").push("aponia");
            aponiaDebuffChance = builder
                    .comment("施加随机 debuff 概率 (默认: 0.05 = 5%)")
                    .defineInRange("debuffChance", 0.05, 0.0, 1.0);
            aponiaDebuffDurationSeconds = builder
                    .comment("debuff 时长（秒） (默认: 3)")
                    .defineInRange("debuffDurationSeconds", 3, 1, 3600);
            aponiaDebuffCount = builder
                    .comment("施加 debuff 数量 (默认: 1)")
                    .defineInRange("debuffCount", 1, 1, 100);
            builder.pop();

            builder.comment("深罪之槛饰品配置").push("shenzui_zhijian");
            shenzuiZhijianDebuffChance = builder
                    .comment("施加随机 debuff 概率 (默认: 0.1 = 10%)")
                    .defineInRange("debuffChance", 0.1, 0.0, 1.0);
            shenzuiZhijianDebuffDurationSeconds = builder
                    .comment("debuff 时长（秒） (默认: 3)")
                    .defineInRange("debuffDurationSeconds", 3, 1, 3600);
            shenzuiZhijianDebuffCount = builder
                    .comment("施加 debuff 数量 (默认: 1)")
                    .defineInRange("debuffCount", 1, 1, 100);
            builder.pop();

            builder.comment("戒律饰品配置").push("jielv");
            jielvDebuffChance = builder
                    .comment("施加随机 debuff 概率 (默认: 0.15 = 15%)")
                    .defineInRange("debuffChance", 0.15, 0.0, 1.0);
            jielvDebuffDurationSeconds = builder
                    .comment("debuff 时长（秒） (默认: 3)")
                    .defineInRange("debuffDurationSeconds", 3, 1, 3600);
            jielvDebuffCount = builder
                    .comment("施加 debuff 数量 (默认: 1)")
                    .defineInRange("debuffCount", 1, 1, 100);
            builder.pop();

            
            builder.comment("戒律系列（阿波尼亚/深罪之槛/戒律）负面效果黑名单：命中的效果不会被随机施加，填入效果注册名（如 minecraft:poison）").push("discipline_buff_blacklist");
            disciplineHarmfulBuffBlacklist = builder
                    .comment("黑名单中的负面效果注册名列表")
                    .defineList("effects", List.of(), o -> o instanceof String);
            builder.pop();

            
            builder.comment("往世的苦囚饰品配置").push("wangshi_de_kuqiu");
            wangshiDeKuqiuAuraRadius = builder
                    .comment("光环影响半径（格） (默认: 64)")
                    .defineInRange("auraRadius", 64.0, 1.0, 512.0);
            wangshiDeKuqiuInfectionLevel = builder
                    .comment("虚数侵染等级（1 级 = amplifier 0，默认: 3）")
                    .defineInRange("infectionLevel", 3, 1, 99);
            wangshiDeKuqiuInfectionDurationSeconds = builder
                    .comment("虚数侵染时长（秒） (默认: 15)")
                    .defineInRange("infectionDurationSeconds", 15, 1, 3600);
            builder.pop();

            builder.comment("往世的苦囚·命之契饰品配置").push("wangshi_de_kuqiu_mingzhiqi");
            wangshiDeKuqiuMingzhiqiAuraRadius = builder
                    .comment("光环影响半径（格） (默认: 64)")
                    .defineInRange("auraRadius", 64.0, 1.0, 512.0);
            wangshiDeKuqiuMingzhiqiInfectionLevel = builder
                    .comment("虚数侵染等级（1 级 = amplifier 0，默认: 6）")
                    .defineInRange("infectionLevel", 6, 1, 99);
            wangshiDeKuqiuMingzhiqiInfectionDurationSeconds = builder
                    .comment("虚数侵染时长（秒） (默认: 15)")
                    .defineInRange("infectionDurationSeconds", 15, 1, 3600);
            builder.pop();

            builder.comment("第零额定功率·神恩结界饰品配置").push("shenen_jiejie");
            shenenJiejieAuraRadius = builder
                    .comment("光环影响半径（格） (默认: 64)")
                    .defineInRange("auraRadius", 64.0, 1.0, 512.0);
            shenenJiejieInfectionLevel = builder
                    .comment("虚数侵染等级（1 级 = amplifier 0，默认: 9）")
                    .defineInRange("infectionLevel", 9, 1, 99);
            shenenJiejieInfectionDurationSeconds = builder
                    .comment("虚数侵染时长（秒） (默认: 15)")
                    .defineInRange("infectionDurationSeconds", 15, 1, 3600);
            builder.pop();

            
            builder.comment("伊甸饰品配置").push("eden");
            edenAuraRange = builder
                    .comment("光环范围（格） (默认: 36)")
                    .defineInRange("auraRange", 36.0, 1.0, 512.0);
            edenIntervalSeconds = builder
                    .comment("攻击触发光环的冷却（秒） (默认: 20)")
                    .defineInRange("intervalSeconds", 20, 1, 3600);
            edenBuffDurationSeconds = builder
                    .comment("buff 持续时长（秒） (默认: 30)")
                    .defineInRange("buffDurationSeconds", 30, 1, 3600);
            edenBuffAmplifier = builder
                    .comment("buff 等级（0=I 级，默认: 0）")
                    .defineInRange("buffAmplifier", 0, 0, 255);
            edenGunDamageReduction = builder
                    .comment("基乘算法：枪械伤害倍率降低比例 (默认: -0.8 = 降低80%)")
                    .defineInRange("gunDamageReduction", -0.8, -1.0, 0.0);
            builder.pop();

            builder.comment("璀耀之歌饰品配置").push("cuiyao_zhi_ge");
            cuiyaoZhiGeAuraRange = builder
                    .comment("光环范围（格） (默认: 36)")
                    .defineInRange("auraRange", 36.0, 1.0, 512.0);
            cuiyaoZhiGeIntervalSeconds = builder
                    .comment("攻击触发光环的冷却（秒） (默认: 20)")
                    .defineInRange("intervalSeconds", 20, 1, 3600);
            cuiyaoZhiGeBuffDurationSeconds = builder
                    .comment("buff 持续时长（秒） (默认: 30)")
                    .defineInRange("buffDurationSeconds", 30, 1, 3600);
            cuiyaoZhiGeBuffAmplifier = builder
                    .comment("buff 等级（0=I 级，默认: 1 = II 级）")
                    .defineInRange("buffAmplifier", 1, 0, 255);
            cuiyaoZhiGeGunDamageReduction = builder
                    .comment("基乘算法：枪械伤害倍率降低比例 (默认: -0.8 = 降低80%)")
                    .defineInRange("gunDamageReduction", -0.8, -1.0, 0.0);
            builder.pop();

            builder.comment("黄金饰品配置").push("huangjin");
            huangjinAuraRange = builder
                    .comment("光环范围（格） (默认: 36)")
                    .defineInRange("auraRange", 36.0, 1.0, 512.0);
            huangjinIntervalSeconds = builder
                    .comment("攻击触发光环的冷却（秒） (默认: 20)")
                    .defineInRange("intervalSeconds", 20, 1, 3600);
            huangjinBuffDurationSeconds = builder
                    .comment("buff 持续时长（秒） (默认: 30)")
                    .defineInRange("buffDurationSeconds", 30, 1, 3600);
            huangjinBuffAmplifier = builder
                    .comment("buff 等级（0=I 级，默认: 2 = III 级）")
                    .defineInRange("buffAmplifier", 2, 0, 255);
            huangjinGunDamageReduction = builder
                    .comment("基乘算法：枪械伤害倍率降低比例 (默认: -0.8 = 降低80%)")
                    .defineInRange("gunDamageReduction", -0.8, -1.0, 0.0);
            builder.pop();

            
            builder.comment("黄金系列（伊甸/璀耀之歌/黄金）正面 buff 黑名单：命中的效果不会被随机施加，填入效果注册名（如 minecraft:speed）").push("golden_buff_blacklist");
            goldenBeneficialBuffBlacklist = builder
                    .comment("黑名单中的正面效果注册名列表（默认包含天火流血：tcc:heaven_fire_bleeding）")
                    .defineList("effects", List.of("tcc:heaven_fire_bleeding"), o -> o instanceof String);
            builder.pop();

            
            builder.comment("伊甸之星饰品配置").push("eden_star");
            edenStarTeleportRange = builder
                    .comment("瞬移失效范围（格） (默认: 16)")
                    .defineInRange("teleportRange", 16.0, 1.0, 512.0);
            builder.pop();

            builder.comment("吞噬之键饰品配置").push("tuntian_zhijian");
            tuntianZhijianTeleportRange = builder
                    .comment("瞬移失效范围（格） (默认: 32)")
                    .defineInRange("teleportRange", 32.0, 1.0, 512.0);
            builder.pop();

            builder.comment("第三额定功率·奇点重构饰品配置").push("qidian_chonggou");
            qidianChonggouTeleportRange = builder
                    .comment("瞬移失效范围（格） (默认: 64)")
                    .defineInRange("teleportRange", 64.0, 1.0, 512.0);
            builder.pop();

            
            builder.comment("科斯魔饰品配置").push("kosma");
            kosmaAttackSpeedPercent = builder
                    .comment("攻击速度加成（小数，默认: 0.08 = +8%）")
                    .defineInRange("attackSpeedPercent", 0.08, -10.0, 100.0);
            kosmaAttackDamagePercent = builder
                    .comment("攻击伤害加成（小数，默认: 0.05 = +5%）")
                    .defineInRange("attackDamagePercent", 0.05, -10.0, 100.0);
            builder.pop();

            builder.comment("黎明之哨饰品配置").push("liming_zhi_shao");
            limingZhiShaoAttackSpeedPercent = builder
                    .comment("攻击速度加成（小数，默认: 0.15 = +15%）")
                    .defineInRange("attackSpeedPercent", 0.15, -10.0, 100.0);
            limingZhiShaoAttackDamagePercent = builder
                    .comment("攻击伤害加成（小数，默认: 0.10 = +10%）")
                    .defineInRange("attackDamagePercent", 0.10, -10.0, 100.0);
            limingZhiShaoCritChancePercent = builder
                    .comment("暴击率加成（小数，默认: 0.05 = +5%）")
                    .defineInRange("critChancePercent", 0.05, -10.0, 100.0);
            builder.pop();

            builder.comment("旭光饰品配置").push("xuguang");
            xuguangAttackSpeedPercent = builder
                    .comment("攻击速度加成（小数，默认: 0.25 = +25%）")
                    .defineInRange("attackSpeedPercent", 0.25, -10.0, 100.0);
            xuguangAttackDamagePercent = builder
                    .comment("攻击伤害加成（小数，默认: 0.20 = +20%）")
                    .defineInRange("attackDamagePercent", 0.20, -10.0, 100.0);
            xuguangCritDamagePercent = builder
                    .comment("暴击伤害加成（小数，默认: 0.30 = +30%）")
                    .defineInRange("critDamagePercent", 0.30, -10.0, 100.0);
            builder.pop();

            
            builder.comment("地藏御魂饰品配置").push("dizang_yuhun");
            dizangYuhunStripPercent = builder
                    .comment("攻击削减目标当前护甲/韧性比例 (默认: 0.05 = 5%)")
                    .defineInRange("stripPercent", 0.05, 0.0, 1.0);
            builder.pop();

            builder.comment("侵蚀之键饰品配置").push("qinshi_zhijian");
            qinshiZhijianStripPercent = builder
                    .comment("攻击削减目标当前护甲/韧性比例 (默认: 0.10 = 10%)")
                    .defineInRange("stripPercent", 0.10, 0.0, 1.0);
            builder.pop();

            
            builder.comment("梅比乌斯饰品配置").push("mebius");
            mebiusPerTypeBonus = builder
                    .comment("每击杀一种实体类型的全属性加成（小数，默认: 0.001 = +0.1%）")
                    .defineInRange("perTypeBonus", 0.001, 0.0, 100.0);
            builder.pop();

            builder.comment("噬界之蛇饰品配置").push("shijie_zhi_she");
            shijieZhiShePerTypeBonus = builder
                    .comment("每击杀一种实体类型的全属性加成（小数，默认: 0.005 = +0.5%）")
                    .defineInRange("perTypeBonus", 0.005, 0.0, 100.0);
            builder.pop();

            builder.comment("无限饰品配置").push("wuxian");
            wuxianPerTypeBonus = builder
                    .comment("每击杀一种实体类型的全属性加成（小数，默认: 0.01 = +1%）")
                    .defineInRange("perTypeBonus", 0.01, 0.0, 100.0);
            builder.pop();

            
            builder.comment("梅比乌斯/噬界之蛇/无限 统一击杀类型记录上限").push("shesha_line_kill_type");
            sheshaLineKillTypeRecordLimit = builder
                    .comment("梅比乌斯、噬界之蛇、无限 每击杀一种实体类型的记录上限（默认: 100）")
                    .defineInRange("killTypeRecordLimit", 100, 1, 10000);
            builder.pop();

            
            builder.comment("舍沙配置").push("shesha");
            sheshaBuffRemovalFactor = builder
                    .comment("舍沙移除目标有益 buff 的概率系数：概率 = 佩戴者虚数抗性 × 本系数，封顶 100%（默认: 0.01 = 抗性/100）")
                    .defineInRange("buffRemovalFactor", 0.01, 0.0, 1.0);
            builder.pop();

            
            builder.comment("饰品加成属性黑名单：这些属性不会收到饰品的全属性加成增益（如无限系列、真我等）。因为有些属性是越小越好（如后坐力、扩散、击退），有些是布尔/阈值属性（如点燃、爆炸启用），不应被增益。").push("attribute_bonus_blacklist");
            attributeBonusBlacklist = builder
                    .comment("黑名单中的属性注册名列表（格式：命名空间:属性名，如 taa:recoil）")
                    .defineList("attributes", List.of(
                            "gunsmithlib:bullet_damage",
                            "tcc:imaginary_damage_resistance",
                            "minecraft:generic.movement_speed", "minecraft:bounciness", "minecraft:burning_time", "minecraft:gravity",
                            "taa:explosion_knockbacknew", "taa:explosion_enabled",
                            "taa:ignitefire", "taa:inaccuracy", "taa:inaccuracy_stand",
                            "taa:inaccuracy_move", "taa:inaccuracy_sneak", "taa:inaccuracy_lie",
                            "taa:inaccuracy_aim", "taa:knockback", "taa:recoil",
                            "taa:recoil_pitch", "taa:recoil_yaw", "taa:silencenew",
                            "taa:weight", "taa:bounciness", "taa:burning_time", "taa:gravity",
                            "taa:ads_time", "taa:heat_overheat_time", "taa:reload_time",
                            "taa:explosion_destroy_blocknew", "taa:explosion_delay",
                            "gunsmithlib:vert_recoil", "gunsmithlib:horz_recoil", "gunsmithlib:reload_speed",
                            "sometaczaddon:gun_recoil_effect"
                    ), o -> o instanceof String);
            builder.pop();

            
            builder.comment("往世的蛇影饰品配置").push("wangshi_de_sheying");
            wangshiDeSheyingRemoveChance = builder
                    .comment("造成伤害移除目标正面 buff 概率 (默认: 0.01 = 1%)")
                    .defineInRange("removeChance", 0.01, 0.0, 1.0);
            builder.pop();

            builder.comment("往世的蛇影·死之衣饰品配置").push("si_zhi_yi");
            siZhiYiRemoveChance = builder
                    .comment("造成伤害移除目标正面 buff 概率 (默认: 0.05 = 5%)")
                    .defineInRange("removeChance", 0.05, 0.0, 1.0);
            builder.pop();

            
            builder.comment("华饰品配置").push("hua");
            huaArmorPercent = builder
                    .comment("护甲/护甲韧性加成（小数，默认: 0.20 = +20%）")
                    .defineInRange("armorPercent", 0.20, -10.0, 100.0);
            builder.pop();

            builder.comment("渡尘之羽饰品配置").push("duchen_zhi_yu");
            duchenZhiYuArmorPercent = builder
                    .comment("护甲/护甲韧性加成（小数，默认: 0.50 = +50%）")
                    .defineInRange("armorPercent", 0.50, -10.0, 100.0);
            builder.pop();

            
            builder.comment("羽渡尘饰品配置").push("yuduchen");
            yuduchenStopChance = builder
                    .comment("攻击停止目标 AI 概率 (默认: 0.05 = 5%)")
                    .defineInRange("stopChance", 0.05, 0.0, 1.0);
            yuduchenStopDurationSeconds = builder
                    .comment("停止 AI 时长（秒） (默认: 5)")
                    .defineInRange("stopDurationSeconds", 5, 1, 3600);
            yuduchenArmorImaginaryScale = builder
                    .comment("攻击时附加（护甲值 × 该比例）的虚数伤害 (默认: 0.30 = 30%)")
                    .defineInRange("armorImaginaryScale", 0.30, 0.0, 100.0);
            builder.pop();

            builder.comment("凡尘难渡饰品配置").push("fanchen_nandu");
            fanchenNanduStopChance = builder
                    .comment("攻击停止目标 AI 概率 (默认: 0.15 = 15%)")
                    .defineInRange("stopChance", 0.15, 0.0, 1.0);
            fanchenNanduStopDurationSeconds = builder
                    .comment("停止 AI 时长（秒） (默认: 5)")
                    .defineInRange("stopDurationSeconds", 5, 1, 3600);
            fanchenNanduArmorImaginaryScale = builder
                    .comment("攻击时附加（护甲值 × 该比例）的虚数伤害 (默认: 0.50 = 50%)")
                    .defineInRange("armorImaginaryScale", 0.50, 0.0, 100.0);
            builder.pop();

            builder.comment("不识时务饰品配置").push("bushi_shiwu");
            bushiShiwuStopDurationSeconds = builder
                    .comment("停止 AI 时长（秒） (默认: 5)")
                    .defineInRange("stopDurationSeconds", 5, 1, 3600);
            bushiShiwuArmorImaginaryScale = builder
                    .comment("攻击时附加（虚数抗性值/100 × 护甲值 × 该比例）的虚数伤害 (默认: 1.0)")
                    .defineInRange("armorImaginaryScale", 1.0, 0.0, 100.0);
            builder.pop();

            
            builder.comment("帕朵菲利斯饰品配置").push("pado_philipis");
            padoPhilipisSpecialFishChance = builder
                    .comment("钓鱼获得下界之星/龙蛋概率 (默认: 0.0001 = 0.01%)")
                    .defineInRange("specialFishChance", 0.0001, 0.0, 1.0);
            builder.pop();

            builder.comment("掠集之兽饰品配置").push("lueji_zhi_shou");
            luejiZhiShouSpecialFishChance = builder
                    .comment("钓鱼获得下界之星/龙蛋概率 (默认: 0.0001 = 0.01%)")
                    .defineInRange("specialFishChance", 0.0001, 0.0, 1.0);
            builder.pop();

            builder.comment("空梦饰品配置").push("kongmeng");
            kongmengSpecialFishChance = builder
                    .comment("钓鱼获得下界之星/龙蛋概率 (默认: 0.0001 = 0.01%)")
                    .defineInRange("specialFishChance", 0.0001, 0.0, 1.0);
            builder.pop();

            
            builder.comment("往世的幻梦饰品配置").push("wangshi_de_huanmeng");
            wangshiDeHuanmengDamageMultiplier = builder
                    .comment("造成伤害概率造成额外伤害的倍率 (默认: 1.5 倍)")
                    .defineInRange("damageMultiplier", 1.5, 0.1, 100.0);
            builder.pop();

            builder.comment("拉之眼饰品配置").push("la_zhi_yan");
            laZhiYanDamageMultiplier = builder
                    .comment("造成伤害概率造成额外伤害的倍率 (默认: 2.0 倍)")
                    .defineInRange("damageMultiplier", 2.0, 0.1, 100.0);
            builder.pop();

            builder.comment("往世的幻梦·夜之瞳饰品配置").push("ye_zhi_tong");
            yeZhiTongDamageMultiplier = builder
                    .comment("造成伤害概率造成额外伤害的倍率 (默认: 1.8 倍)")
                    .defineInRange("damageMultiplier", 1.8, 0.1, 100.0);
            builder.pop();

            
            builder.comment("饰品互斥配置（格式：物品1,物品2 表示互斥）").push("curio_conflicts");
            curioConflicts = builder
                    .comment("互斥饰品组列表，每组用逗号分隔的物品注册名表示互斥关系")
                    .defineList("conflictGroups", 
                        List.of(
                            "tcc:heaven_fire_judgment,tcc:heaven_fire_apocalypse",
                            "tcc:soldier_basic_tag,tcc:soldier_specific_tag",
                            "tcc:tactical_reload,tcc:tactical_reload_prime",
                            "tcc:burst_reload,tcc:burst_reload_prime",
                            "tcc:tandem_magazine,tcc:tandem_magazine_prime",
                            "tcc:shotgun_expansion,tcc:shotgun_expansion_prime",
                            "tcc:magazine_boost,tcc:magazine_boost_prime",
                            "tcc:rifling,tcc:merged_rifling",
                            "tcc:sword_wind,tcc:sword_wind_prime",
                            "tcc:blaze_storm,tcc:blaze_storm_prime",
                            "tcc:oppression_point,tcc:oppression_point_prime",
                            "tcc:chamber,tcc:chamber_prime",
                            "tcc:close_range_shot,tcc:close_combat_prime",
                            "tcc:lethal_crit,tcc:critical_delay",
                            "tcc:thunder_barrel,tcc:thunder_barrel_prime,tcc:critical_delay",
                            "tcc:pistol_mastery,tcc:pistol_mastery_prime,tcc:critical_delay",
                            "tcc:argon_scope,tcc:gilded_argon_scope",
                            "tcc:destruction,tcc:destruction_prime",
                            "tcc:weakness_mastery,tcc:weakness_mastery_prime",
                            "tcc:hydraulic_crosshair,tcc:gilded_hydraulic_crosshair",
                            "tcc:steel_slash,tcc:sacrifice_steel,tcc:gilded_steel_slash",
                            "tcc:gilded_split_chamber,tcc:split_chamber",
                            "tcc:gilded_infernal_chamber,tcc:infernal_chamber",
                            "tcc:gilded_bullet_spread,tcc:bullet_spread"
                        ), 
                        o -> o instanceof String);
            builder.pop();

            
            builder.comment("融合升级系统配置（饰品等级升级）").push("fusion_upgrade");
            fusionGrowthCoefficient = builder
                    .comment("属性增长系数 C。（1）正向饰品（buff/Effect）：实际值 = 基础值 × (1 + 等级 × C)，满级值受 C 影响。（2）反向饰品（属性修饰符）：配置存满级值，公式 = 满级值 × (1 + 等级 × C) / (1 + 最大等级 × C)，满级值锁定为配置值（默认: 0.6)")
                    .defineInRange("growthCoefficient", 0.6, 0.01, 100.0);
            fusionEbcCommon = builder
                    .comment("COMMON 稀有度 EBC（基础内融核心消耗）(默认: 10)")
                    .defineInRange("ebc_common", 10, 1, 10000);
            fusionEbcUncommon = builder
                    .comment("UNCOMMON 稀有度 EBC (默认: 20)")
                    .defineInRange("ebc_uncommon", 20, 1, 10000);
            fusionEbcRare = builder
                    .comment("RARE 稀有度 EBC (默认: 30)")
                    .defineInRange("ebc_rare", 30, 1, 10000);
            fusionEbcEpic = builder
                    .comment("EPIC 稀有度 EBC (默认: 40)")
                    .defineInRange("ebc_epic", 40, 1, 10000);
            fusionMaxLevelCommon = builder
                    .comment("COMMON 稀有度封顶等级 (默认: 10)")
                    .defineInRange("maxLevel_common", 10, 0, 100);
            fusionMaxLevelUncommon = builder
                    .comment("UNCOMMON 稀有度封顶等级 (默认: 8)")
                    .defineInRange("maxLevel_uncommon", 10, 0, 100);
            fusionMaxLevelRare = builder
                    .comment("RARE 稀有度封顶等级 (默认: 10)")
                    .defineInRange("maxLevel_rare", 12, 0, 100);
            fusionMaxLevelEpic = builder
                    .comment("EPIC 稀有度封顶等级 (默认: 12)")
                    .defineInRange("maxLevel_epic", 12, 0, 100);
            fusionVesselCapacity = builder
                    .comment("融合容器容量上限（默认: 655200 = 4 × EPIC满级消耗）")
                    .defineInRange("fusionVesselCapacity", 655200, 1, Integer.MAX_VALUE);
            builder.pop();

            
            builder.comment("融合容器战利品生成配置").push("fusion_vessel_loot");
            fusionVesselNetherMin = builder
                    .comment("下界要塞/堡垒遗迹中融合容器包含的内融核心最小数量（默认: 10000）")
                    .defineInRange("netherMin", 10000, 1, Integer.MAX_VALUE);
            fusionVesselNetherMax = builder
                    .comment("下界要塞/堡垒遗迹中融合容器包含的内融核心最大数量（默认: 22760）")
                    .defineInRange("netherMax", 22760, 1, Integer.MAX_VALUE);
            fusionVesselEndMin = builder
                    .comment("末地城中融合容器包含的内融核心最小数量（默认: 10000）")
                    .defineInRange("endMin", 10000, 1, Integer.MAX_VALUE);
            fusionVesselEndMax = builder
                    .comment("末地城中融合容器包含的内融核心最大数量（默认: 55520）")
                    .defineInRange("endMax", 55520, 1, Integer.MAX_VALUE);
            fusionVesselNetherChance = builder
                    .comment("下界要塞/堡垒遗迹中出现融合容器的几率（0~1，默认: 0.05 = 5%）")
                    .defineInRange("netherChance", 0.05, 0.0, 1.0);
            fusionVesselEndChance = builder
                    .comment("末地城战利品箱中出现融合容器的几率（0~1，默认: 0.05 = 5%）")
                    .defineInRange("endChance", 0.05, 0.0, 1.0);
            builder.pop();
        }
    }

    /**
     * 客户端收到服务端配置后调用：把服务端 TOML 写入本地配置文件，并让配置值立即生效。
     *
     * <p>先写盘再交给 FML 的 {@link ConfigTracker#acceptSyncedConfig}（与 SERVER 配置同步同一条路径），
     * 使本地文件内容与内存中的配置值都与服务端一致，下次启动读取到的也是服务端这份配置。
     * 任一步失败都保留本地配置。</p>
     */
    public static void applySyncedConfig(String toml) {
        if (toml == null || toml.isBlank()) {
            return;
        }
        ModConfig config = findCommonConfig();
        if (config == null) {
            return;
        }
        try {
            Files.createDirectories(FMLPaths.CONFIGDIR.get());
            Files.writeString(configPath(config), toml, StandardCharsets.UTF_8);
            ConfigTracker.acceptSyncedConfig(config, toml.getBytes(StandardCharsets.UTF_8));
        } catch (Exception ignored) {
            // 同步失败时保持本地配置
        }
    }

    /** COMMON 配置固定落在 config 目录下，按 FML 的默认命名规则定位文件。 */
    private static Path configPath(ModConfig config) {
        return FMLPaths.CONFIGDIR.get().resolve(config.getFileName());
    }

    /** 取出本 mod 的 COMMON 配置，客户端同步时用它定位配置。 */
    private static ModConfig findCommonConfig() {
        for (ModConfig config : ModConfigs.getModConfigs(TaczCurios.MODID)) {
            if (config.getType() == ModConfig.Type.COMMON) {
                return config;
            }
        }
        return null;
    }
}
