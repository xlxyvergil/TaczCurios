package com.xlxyvergil.tcc.config;

import java.util.List;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

public class TaczCuriosConfig {
    public static final ForgeConfigSpec COMMON_SPEC;
    public static final Common COMMON;
    
    static {
        final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
        COMMON_SPEC = specPair.getRight();
        COMMON = specPair.getLeft();
    }
    
    public static class Common {
        // 天火圣裁配置
        public final ForgeConfigSpec.DoubleValue heavenFireJudgmentDamageBoost;
        public final ForgeConfigSpec.DoubleValue heavenFireJudgmentHealthCost;
        public final ForgeConfigSpec.DoubleValue heavenFireJudgmentDamageConversionRatio;
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> heavenFireJudgmentGunTypes;
        
        // 天火流血效果配置（两个饰品共用）
        public final ForgeConfigSpec.DoubleValue heavenFireBleedingDamagePerLevel;
        public final ForgeConfigSpec.IntValue heavenFireBleedingMaxLevel;
        public final ForgeConfigSpec.IntValue heavenFireBleedingDuration;
        public final ForgeConfigSpec.IntValue heavenFireApocalypseDelayDuration;
        
        // 虚数侵染效果配置
        public final ForgeConfigSpec.DoubleValue imaginaryInfectionAmpPerLevel;
        public final ForgeConfigSpec.IntValue imaginaryInfectionMaxLevel;
        public final ForgeConfigSpec.IntValue imaginaryInfectionDuration;
        public final ForgeConfigSpec.DoubleValue imaginaryInfectionResistanceReduction;
        
        // 按饰品分级的虚数侵染上限
        public final ForgeConfigSpec.IntValue judgementKeyImaginaryInfectionMaxLevel;
        public final ForgeConfigSpec.IntValue judgmentImaginaryInfectionMaxLevel;
        public final ForgeConfigSpec.IntValue apocalypseImaginaryInfectionMaxLevel;
        public final ForgeConfigSpec.IntValue endlessImaginaryInfectionMaxLevel;

        // 裁决之键配置
        public final ForgeConfigSpec.DoubleValue judgementProcChance;
        public final ForgeConfigSpec.DoubleValue judgementDirectDamagePercent;
        public final ForgeConfigSpec.DoubleValue judgementCollapseProcChance;

        // 天火劫灭配置
        public final ForgeConfigSpec.DoubleValue heavenFireApocalypseDamageBoost;
        public final ForgeConfigSpec.DoubleValue heavenFireApocalypseExplosionRadius;
        public final ForgeConfigSpec.DoubleValue heavenFireApocalypseExplosionDamage;
        public final ForgeConfigSpec.DoubleValue heavenFireApocalypseHealthCost;
        public final ForgeConfigSpec.DoubleValue brahmaBeastsHealthCostReduction;
        public final ForgeConfigSpec.DoubleValue heavenFireApocalypseNearbyPlayerDamageBoost;
        public final ForgeConfigSpec.IntValue heavenFireApocalypseNearbyPlayerPotionAmplifier;
        public final ForgeConfigSpec.IntValue heavenFireApocalypseNearbyPlayerDuration;
        public final ForgeConfigSpec.DoubleValue heavenFireApocalypseNearbyPlayerRadius;
        public final ForgeConfigSpec.DoubleValue heavenFireApocalypseDamageConversionRatio;
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> heavenFireApocalypseGunTypes;
        
        // 膛线配置
        public final ForgeConfigSpec.DoubleValue riflingDamageBoost;
        
        // 分裂膛室配置
        public final ForgeConfigSpec.DoubleValue splitChamberBulletCountBoost;
        
        // 卑劣加速配置
        public final ForgeConfigSpec.DoubleValue despicableAccelerationFireRateBoost;
        public final ForgeConfigSpec.DoubleValue despicableAccelerationDamageReduction;
        
        // 并合膛线配置
        public final ForgeConfigSpec.DoubleValue mergedRiflingDamageBoost;
        public final ForgeConfigSpec.DoubleValue mergedRiflingMovementSpeedBoost;
        
        // 合金钻头配置
        public final ForgeConfigSpec.DoubleValue alloyDrillArmorPenetrationBoost;
        
        // 我小心海也绝非鳝类配置
        public final ForgeConfigSpec.DoubleValue carefulHeartLauncherDamageBoost;
        public final ForgeConfigSpec.DoubleValue carefulHeartExplosionDamageBoost;
        public final ForgeConfigSpec.DoubleValue carefulHeartExplosionRadiusBoost;
        public final ForgeConfigSpec.DoubleValue carefulHeartExplosionEnabled;
        
        // 烈焰风暴配置
        public final ForgeConfigSpec.DoubleValue blazeStormExplosionRadiusBoost;
        public final ForgeConfigSpec.DoubleValue blazeStormExplosionDamageBoost;
        public final ForgeConfigSpec.DoubleValue blazeStormExplosionEnabled;
        
        // 烈焰风暴Prime配置
        public final ForgeConfigSpec.DoubleValue blazeStormPrimeExplosionRadiusBoost;
        public final ForgeConfigSpec.DoubleValue blazeStormPrimeExplosionDamageBoost;
        public final ForgeConfigSpec.DoubleValue blazeStormPrimeExplosionEnabled;
        
        
        // 撕裂Prime配置
        public final ForgeConfigSpec.DoubleValue rippingPrimeFireRateBoost;
        public final ForgeConfigSpec.DoubleValue rippingPrimePenetrationBoost;
        
        // 抵近射击Prime配置
        public final ForgeConfigSpec.DoubleValue closeCombatPrimeShotgunDamageBoost;
        
        // 极恶精准配置
        public final ForgeConfigSpec.DoubleValue evilAccuracyRecoilReduction;
        public final ForgeConfigSpec.DoubleValue evilAccuracyFireRateReduction;
        
        // 极限速度配置
        public final ForgeConfigSpec.DoubleValue limitSpeedBulletSpeedBoost;
        
        // 凶恶延伸配置
        public final ForgeConfigSpec.DoubleValue ferociousExtensionRangeBoost;
        
        // 抵近射击配置
        public final ForgeConfigSpec.DoubleValue closeRangeShotDamageBoost;
        
        // 重装火力配置
        public final ForgeConfigSpec.DoubleValue heavyFirepowerDamageBoost;
        public final ForgeConfigSpec.DoubleValue heavyFirepowerAccuracyReduction;
        
        // 黄蜂蜇刺配置
        public final ForgeConfigSpec.DoubleValue waspStingerDamageBoost;
        
        // 预言契约配置
        public final ForgeConfigSpec.DoubleValue prophecyPactDamageBoost;
        
        // 恶性扩散配置
        public final ForgeConfigSpec.DoubleValue malignantSpreadDamageBoost;
        public final ForgeConfigSpec.DoubleValue malignantSpreadAccuracyReduction;
        
        // 膛室配置
        public final ForgeConfigSpec.DoubleValue chamberSniperDamageBoost;
        
        // 膛室Prime配置
        public final ForgeConfigSpec.DoubleValue chamberPrimeSniperDamageBoost;
        
        // 战术上膛配置
        public final ForgeConfigSpec.DoubleValue tacticalReloadSpeedBoost;
        
        // 过载弹匣配置
        public final ForgeConfigSpec.DoubleValue overloadedMagazineCapacityBoost;
        public final ForgeConfigSpec.DoubleValue overloadedMagazineReloadSpeedReduction;
        
        // 地狱弹膛配置
        public final ForgeConfigSpec.DoubleValue infernalChamberBulletCountBoost;
        
        // 持续火力配置
        public final ForgeConfigSpec.DoubleValue sustainedFireReloadSpeedBoost;
        
        // 感染弹匣配置
        public final ForgeConfigSpec.DoubleValue infectedMagazineCapacityBoost;
        public final ForgeConfigSpec.DoubleValue infectedMagazineReloadSpeedReduction;
        
        // 致命洪流配置
        public final ForgeConfigSpec.DoubleValue deadlySurgeFireRateBoost;
        public final ForgeConfigSpec.DoubleValue deadlySurgeBulletCountBoost;
        
        // 弹头扩散配置
        public final ForgeConfigSpec.DoubleValue bulletSpreadBulletCountBoost;
        
        // 压迫点配置
        public final ForgeConfigSpec.DoubleValue oppressionPointMeleeDamageBoost;
        
        // 压迫点Prime配置
        public final ForgeConfigSpec.DoubleValue oppressionPointPrimeMeleeDamageBoost;
        
        // 爆发装填配置
        public final ForgeConfigSpec.DoubleValue burstReloadReloadSpeedBoost;
        
        // 剑风配置
        public final ForgeConfigSpec.DoubleValue swordWindMeleeRangeBoost;
        
        // 剑风Prime配置
        public final ForgeConfigSpec.DoubleValue swordWindPrimeMeleeRangeBoost;
        
        // 腐败弹匣配置
        public final ForgeConfigSpec.DoubleValue corruptMagazineCapacityBoost;
        public final ForgeConfigSpec.DoubleValue corruptMagazineReloadSpeedReduction;
        
        // 重口径配置
        public final ForgeConfigSpec.DoubleValue heavyCaliberTagDamageBoost;
        public final ForgeConfigSpec.DoubleValue heavyCaliberTagInaccuracyBoost;
        
        // 弹匣增幅配置
        public final ForgeConfigSpec.DoubleValue magazineBoostReloadSpeedBoost;
        
        // 红-有-三配置
        public final ForgeConfigSpec.DoubleValue redMovementTagSpeedBoost;
        
        // 希奥拉配置
        public final ForgeConfigSpec.IntValue xioraBaseResistance;
        
        // 夏日沙滩配置
        public final ForgeConfigSpec.DoubleValue summerBeachELCurseReduction;
        public final ForgeConfigSpec.IntValue summerBeachBaseResistance;
        
        // 梵天百兽配置
        public final ForgeConfigSpec.DoubleValue brahmaBeastsELCurseReduction;
        
        // 救世配置
        public final ForgeConfigSpec.DoubleValue salvationELCurseReduction;
        public final ForgeConfigSpec.DoubleValue salvationDamageReduction;
        public final ForgeConfigSpec.IntValue salvationResistanceLevel;
        
        // 无烬终焉配置
        public final ForgeConfigSpec.DoubleValue endlessDamageBoost;
        public final ForgeConfigSpec.DoubleValue endlessExplosionDamage;
        public final ForgeConfigSpec.DoubleValue endlessNearbyPlayerDamageBoost;
        public final ForgeConfigSpec.IntValue endlessNearbyPlayerPotionAmplifier;
        public final ForgeConfigSpec.IntValue endlessNearbyPlayerDuration;
        public final ForgeConfigSpec.DoubleValue endlessNearbyPlayerRadius;
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> endlessGunTypes;

        // 士兵基础挂牌配置
        public final ForgeConfigSpec.DoubleValue soldierBasicTagDamageBoost;
        
        // 士兵特定挂牌配置
        public final ForgeConfigSpec.DoubleValue soldierSpecificTagDamageBoost;
        
        // 乌拉尔银狼配置
        public final ForgeConfigSpec.DoubleValue uralWolfTagHeadshotMultiplierBoost;
        
        // 耗竭装填配置
        public final ForgeConfigSpec.DoubleValue depletedReloadMagazineCapacityPenalty;
        public final ForgeConfigSpec.DoubleValue depletedReloadReloadSpeedBoost;
        
        // 爆发装填Prime配置
        public final ForgeConfigSpec.DoubleValue burstReloadPrimeReloadSpeedBoost;
        
        // 战术上膛Prime配置
        public final ForgeConfigSpec.DoubleValue tacticalReloadPrimeReloadSpeedBoost;
        
        // 霰弹扩充Prime配置
        public final ForgeConfigSpec.DoubleValue shotgunExpansionPrimeCapacityBoost;
        
        // 弹匣增幅Prime配置
        public final ForgeConfigSpec.DoubleValue magazineBoostPrimeCapacityBoost;
        
        // 串联弹匣Prime配置
        public final ForgeConfigSpec.DoubleValue tandemMagazinePrimeCapacityBoost;
        
        // 霰弹扩充配置
        public final ForgeConfigSpec.DoubleValue shotgunExpansionCapacityBoost;
        
        // 弹匣增幅配置
        public final ForgeConfigSpec.DoubleValue magazineBoostCapacityBoost;
        
        // 串联弹匣配置
        public final ForgeConfigSpec.DoubleValue tandemMagazineCapacityBoost;
        
        // 裂隙碎银配置
        public final ForgeConfigSpec.DoubleValue riftSilverChestSpawnChance;
        
        // 掎角一阵配置
        public final ForgeConfigSpec.DoubleValue kikakuIchijinHealthMultiplier;
        public final ForgeConfigSpec.BooleanValue kikakuIchijinDestroyUnbreakableBlocks;
        public final ForgeConfigSpec.BooleanValue kikakuIchijinDestroyNormalBlocks;
        
        // Apotheosis集成配置
        public final ForgeConfigSpec.BooleanValue enableApotheosisIntegration;



        // 饰品互斥配置
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> curioConflicts;

        // 虚数抗性每点提高的伤害保留比例
        public final ForgeConfigSpec.DoubleValue imaginaryDamageResistanceBonusPerPoint;

        // 虚数崩解配置
        public final ForgeConfigSpec.DoubleValue collapsePercentPerLevel;
        public final ForgeConfigSpec.DoubleValue collapsePercentPerDebuff;
        public final ForgeConfigSpec.IntValue collapseMaxDebuffCount;

        // 无烬终焉伤害转换比例
        public final ForgeConfigSpec.DoubleValue endlessDamageConversionRatio;

        // ========== 新增33个饰品配置字段(G/R/S/P/M) ==========
        public final ForgeConfigSpec.DoubleValue criticalDelayCritChanceBoost;
        public final ForgeConfigSpec.DoubleValue criticalDelayFireRateReduction;
        public final ForgeConfigSpec.DoubleValue lethalCritCritChance;
        public final ForgeConfigSpec.DoubleValue weaknessSenseCritDamage;
        public final ForgeConfigSpec.DoubleValue argonScopeBaseCritChance;
        public final ForgeConfigSpec.IntValue argonScopeDuration;
        public final ForgeConfigSpec.DoubleValue gildedArgonScopeBaseCritChance;
        public final ForgeConfigSpec.DoubleValue gildedArgonScopeCritChancePerLevel;
        public final ForgeConfigSpec.DoubleValue gildedArgonScopeHeadshotKillExtra;
        public final ForgeConfigSpec.IntValue gildedArgonScopeDuration;
        public final ForgeConfigSpec.IntValue gildedArgonScopeMaxStacks;
        public final ForgeConfigSpec.DoubleValue sharpBulletBaseCritDamage;
        public final ForgeConfigSpec.IntValue sharpBulletDuration;
        public final ForgeConfigSpec.DoubleValue gildedRifleAptitudePerHarmful;
        public final ForgeConfigSpec.IntValue gildedRifleAptitudeDuration;
        public final ForgeConfigSpec.IntValue gildedRifleAptitudeMaxStacks;
        public final ForgeConfigSpec.DoubleValue gildedSplitChamberBulletCountBase;
        public final ForgeConfigSpec.DoubleValue gildedSplitChamberBulletCountPerLevel;
        public final ForgeConfigSpec.IntValue gildedSplitChamberDuration;
        public final ForgeConfigSpec.IntValue gildedSplitChamberMaxStacks;
        public final ForgeConfigSpec.DoubleValue destructionCritDamage;
        public final ForgeConfigSpec.DoubleValue destructionPrimeCritDamage;
        public final ForgeConfigSpec.DoubleValue thunderBarrelCritChance;
        public final ForgeConfigSpec.DoubleValue thunderBarrelPrimeCritChance;
        public final ForgeConfigSpec.DoubleValue laserScopeBaseCritChance;
        public final ForgeConfigSpec.IntValue laserScopeDuration;
        public final ForgeConfigSpec.DoubleValue fragmentShotBaseCritDamage;
        public final ForgeConfigSpec.IntValue fragmentShotDuration;
        public final ForgeConfigSpec.DoubleValue gildedShotgunSavvyPerHarmful;
        public final ForgeConfigSpec.IntValue gildedShotgunSavvyDuration;
        public final ForgeConfigSpec.IntValue gildedShotgunSavvyMaxStacks;
        public final ForgeConfigSpec.DoubleValue gildedInfernalChamberBulletCountBase;
        public final ForgeConfigSpec.DoubleValue gildedInfernalChamberBulletCountPerLevel;
        public final ForgeConfigSpec.IntValue gildedInfernalChamberDuration;
        public final ForgeConfigSpec.IntValue gildedInfernalChamberMaxStacks;
        public final ForgeConfigSpec.DoubleValue weaknessMasteryCritDamage;
        public final ForgeConfigSpec.DoubleValue weaknessMasteryPrimeCritDamage;
        public final ForgeConfigSpec.DoubleValue hollowPointCritDamage;
        public final ForgeConfigSpec.DoubleValue hollowPointPistolDamageReduction;
        public final ForgeConfigSpec.DoubleValue pistolMasteryCritChance;
        public final ForgeConfigSpec.DoubleValue pistolMasteryPrimeCritChance;
        public final ForgeConfigSpec.DoubleValue hydraulicCrosshairBaseCritChance;
        public final ForgeConfigSpec.IntValue hydraulicCrosshairDuration;
        public final ForgeConfigSpec.DoubleValue gildedHydraulicCrosshairBaseCritChance;
        public final ForgeConfigSpec.DoubleValue gildedHydraulicCrosshairCritChancePerLevel;
        public final ForgeConfigSpec.DoubleValue gildedHydraulicCrosshairHeadshotKillExtra;
        public final ForgeConfigSpec.IntValue gildedHydraulicCrosshairDuration;
        public final ForgeConfigSpec.IntValue gildedHydraulicCrosshairMaxStacks;
        public final ForgeConfigSpec.DoubleValue sharpAmmoBaseCritDamage;
        public final ForgeConfigSpec.IntValue sharpAmmoDuration;
        public final ForgeConfigSpec.DoubleValue gildedMarksmanPerHarmful;
        public final ForgeConfigSpec.IntValue gildedMarksmanDuration;
        public final ForgeConfigSpec.IntValue gildedMarksmanMaxStacks;
        public final ForgeConfigSpec.DoubleValue gildedBulletSpreadBulletCountBase;
        public final ForgeConfigSpec.DoubleValue gildedBulletSpreadBulletCountPerLevel;
        public final ForgeConfigSpec.IntValue gildedBulletSpreadDuration;
        public final ForgeConfigSpec.IntValue gildedBulletSpreadMaxStacks;
        public final ForgeConfigSpec.DoubleValue steelSlashCritChance;
        public final ForgeConfigSpec.DoubleValue dismembermentCritDamage;
        public final ForgeConfigSpec.DoubleValue sacrificeOppressionMeleeDamage;
        public final ForgeConfigSpec.DoubleValue sacrificeSteelCritChance;
        public final ForgeConfigSpec.DoubleValue gildedSteelSlashCritChanceBase;
        public final ForgeConfigSpec.DoubleValue gildedSteelSlashCritDamagePerLevel;
        public final ForgeConfigSpec.IntValue gildedSteelSlashDuration;
        public final ForgeConfigSpec.IntValue gildedSteelSlashMaxStacks;
        public final ForgeConfigSpec.DoubleValue conditionOverloadPerHarmful;
        public final ForgeConfigSpec.DoubleValue sacrificeSetBonus;

        
        public Common(ForgeConfigSpec.Builder builder) {
            builder.comment("TACZ Curios 饰品配置").push("tcc_curios");
            
            // 天火圣裁配置
            builder.comment("天火圣裁饰品配置").push("heaven_fire_judgment");
            heavenFireJudgmentDamageBoost = builder
                    .comment("通用枪械伤害加成 (默认: 3.25 = 325%)")
                    .defineInRange("damageBoost", 3.25, -1, 100);
            heavenFireJudgmentHealthCost = builder
                    .comment("触发时扣除的当前生命值比例 (默认: -0.3 = -30%)")
                    .defineInRange("healthCost", -0.3, -1, 1);
            heavenFireJudgmentDamageConversionRatio = builder
                    .comment("伤害降低99%，并转换为虚数伤害")
                    .defineInRange("damageConversionRatio", 0.01, 0, 1);
            heavenFireJudgmentGunTypes = builder
                    .comment("天火圣裁生效的枪械类型列表 (可选: pistol, rifle, shotgun, sniper, smg, mg, rpg)")
                    .defineList("gunTypes", List.of("pistol"), o -> o instanceof String);
            builder.pop();
            
            // 天火流血效果配置（两个饰品共用）
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
            
            // 虚数侵染效果配置
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
                    .comment("虚数侵染降低的虚数抗性值 (默认: 10)")
                    .defineInRange("resistanceReduction", 10.0, 0, 100);
            imaginaryDamageResistanceBonusPerPoint = builder
                    .comment("每点虚数抗性提高的伤害保留比例 (默认: 0.01 = 1%/点)")
                    .defineInRange("damageResistanceBonusPerPoint", 0.01, 0, 1);
            builder.pop();
            

            // 虚数崩解配置
            builder.comment("虚数崩解配置（虚数崩解基于虚数侵染层数和负面效果种数造成额外伤害）").push("imaginary_collapse");
            collapsePercentPerLevel = builder
                    .comment("每层虚数侵染的崩解增伤比例 (默认: 0.01 = 1%/层)")
                    .defineInRange("percentPerLevel", 0.01, 0, 1);
            collapsePercentPerDebuff = builder
                    .comment("每种负面效果的崩解增伤比例 (默认: 0.1 = 10%/种)")
                    .defineInRange("percentPerDebuff", 0.1, 0, 1);
            collapseMaxDebuffCount = builder
                    .comment("崩解计入的负面效果种数上限 (默认: 5)")
                    .defineInRange("maxDebuffCount", 5, 1, 20);
            builder.pop();

            // 按饰品分级的虚数侵染上限
            builder.comment("按饰品分级的虚数侵染上限（当攻击者携带对应饰品时，目标虚数侵染不会超过此等级）").push("imaginary_infection_per_curio");
            judgementKeyImaginaryInfectionMaxLevel = builder
                    .comment("裁决之键的虚数侵染上限 (默认: 9)")
                    .defineInRange("judgementKeyMaxLevel", 9, 1, 99);
            judgmentImaginaryInfectionMaxLevel = builder
                    .comment("天火圣裁的虚数侵染上限 (默认: 3)")
                    .defineInRange("judgmentMaxLevel", 3, 1, 99);
            apocalypseImaginaryInfectionMaxLevel = builder
                    .comment("天火劫灭的虚数侵染上限 (默认: 6)")
                    .defineInRange("apocalypseMaxLevel", 6, 1, 99);
            endlessImaginaryInfectionMaxLevel = builder
                    .comment("劫灭无尽的虚数侵染上限 (默认: 9)")
                    .defineInRange("endlessMaxLevel", 9, 1, 99);
            builder.pop();
            
            // 裁决之键配置
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
            builder.pop();
            
            // 天火劫灭配置
            builder.comment("天火劫灭饰品配置").push("heaven_fire_apocalypse");
            heavenFireApocalypseDamageBoost = builder
                    .comment("通用枪械伤害加成 (默认: 10.0 = 1000%)")
                    .defineInRange("damageBoost", 10.0, -1, 1000);
            heavenFireApocalypseExplosionRadius = builder
                    .comment("爆炸范围加成 (默认: 10)")
                    .defineInRange("explosionRadius", 10.0, -1, 100);
            heavenFireApocalypseExplosionDamage = builder
                    .comment("爆炸伤害加成 (默认: 10.0 = 1000%)")
                    .defineInRange("explosionDamage", 10.0, -1, 100);
            heavenFireApocalypseHealthCost = builder
                    .comment("触发时扣除的当前生命值比例 (默认: -1.0 = -100%)")
                    .defineInRange("healthCost", -1.0, -1, 1);
            brahmaBeastsHealthCostReduction = builder
                    .comment("装备梵天百兽时天火劫灭扣血比例的减少值 (默认: 0.6 = 从扣100%变为扣40%，即保留60%血量)")
                    .defineInRange("brahmaBeastsHealthCostReduction", 0.6, 0, 1);
            heavenFireApocalypseNearbyPlayerDamageBoost = builder
                    .comment("附近玩家获得的 bullet_gundamage 每级伤害加成 (默认: 1.0 = 100%/级)")
                    .defineInRange("nearbyPlayerDamageBoost", 1.0, -1, 100);
            heavenFireApocalypseNearbyPlayerPotionAmplifier = builder
                    .comment("附近玩家获得的药水效果等级 (0=1级, 默认: 0)")
                    .defineInRange("nearbyPlayerPotionAmplifier", 0, 0, 999);
            heavenFireApocalypseNearbyPlayerDuration = builder
                    .comment("附近玩家获得伤害加成的持续时间(秒) (默认: 15)")
                    .defineInRange("nearbyPlayerDuration", 15, -1, 300);
            heavenFireApocalypseNearbyPlayerRadius = builder
                    .comment("影响附近玩家的范围 (默认: 32)")
                    .defineInRange("nearbyPlayerRadius", 32.0, -1, 100);
            heavenFireApocalypseDamageConversionRatio = builder
                    .comment("伤害降低90%，并转换为虚数伤害")
                    .defineInRange("damageConversionRatio", 0.01, 0, 1);
            heavenFireApocalypseGunTypes = builder
                    .comment("天火劫灭生效的枪械类型列表 (可选: pistol, rifle, shotgun, sniper, smg, mg, rpg)")
                    .defineList("gunTypes", List.of("pistol"), o -> o instanceof String);
            builder.pop();
            
            // 膛线配置
            builder.comment("膛线饰品配置").push("rifling");
            riflingDamageBoost = builder
                    .comment("特定枪械伤害加成 (默认: 1.65 = 165%)")
                    .defineInRange("damageBoost", 1.65, -1, 100);
            builder.pop();
            
            // 分裂膛室配置
            builder.comment("分裂膛室饰品配置").push("split_chamber");
            splitChamberBulletCountBoost = builder
                    .comment("弹头数量加成 (默认: 0.9 = 90%)")
                    .defineInRange("bulletCountBoost", 0.9, -1, 100);
            builder.pop();
            
            // 卑劣加速配置
            builder.comment("卑劣加速饰品配置").push("despicable_acceleration");
            despicableAccelerationFireRateBoost = builder
                    .comment("射击速度加成 (默认: +0.9 = +90%)")
                    .defineInRange("fireRateBoost", 0.9, -1, 100);
            despicableAccelerationDamageReduction = builder
                    .comment("伤害降低 (默认: -0.15 = -15%)")
                    .defineInRange("damageReduction", -0.15, -1, 0);
            builder.pop();
            
            // 并合膛线配置
            builder.comment("并合膛线饰品配置").push("merged_rifling");
            mergedRiflingDamageBoost = builder
                    .comment("特定枪械伤害加成 (默认: 1.55 = 155%)")
                    .defineInRange("damageBoost", 1.55, -1, 100);
            mergedRiflingMovementSpeedBoost = builder
                    .comment("持枪移动速度加成 (默认: 0.25 = 25%)")
                    .defineInRange("movementSpeedBoost", 0.25, -1, 100);
            builder.pop();
            
            // 合金钻头配置
            builder.comment("合金钻头饰品配置").push("alloy_drill");
            alloyDrillArmorPenetrationBoost = builder
                    .comment("护甲穿透加成 (默认: 2.0 = 200%)")
                    .defineInRange("armorPenetrationBoost", 2.0, -1, 100);
            builder.pop();
            
            // 我小心海也绝非鳝类配置
            builder.comment("我小心海也绝非鳝类饰品配置").push("careful_heart");
            carefulHeartLauncherDamageBoost = builder
                    .comment("发射器伤害加成 (默认: 3.0 = 300%)")
                    .defineInRange("launcherDamageBoost", 3.0, -1, 100);
            carefulHeartExplosionDamageBoost = builder
                    .comment("爆炸伤害加成 (默认: 3.0 = 300%)")
                    .defineInRange("explosionDamageBoost", 3.0, -1, 100);
            carefulHeartExplosionRadiusBoost = builder
                    .comment("爆炸范围加成 (默认: 3.0 = 300%)")
                    .defineInRange("explosionRadiusBoost", 3.0, -1, 100);
            carefulHeartExplosionEnabled = builder
                    .comment("爆炸启用属性 (默认: 2.0)")
                    .defineInRange("explosionEnabled", 2.0, -1, 100);
            builder.pop();
            


            // ========== 新增33个饰品配置段 ==========
            
            // G-01 关键延迟
            builder.comment("关键延迟饰品配置").push("critical_delay");
            criticalDelayCritChanceBoost = builder.comment("暴击几率加成 (默认: 2.0)").defineInRange("critChanceBoost", 2.0, -1, 100);
            criticalDelayFireRateReduction = builder.comment("射速降低 (默认: -0.2)").defineInRange("fireRateReduction", -0.2, -1, 0);
            builder.pop();
            
            // R-01 致命一击
            builder.comment("致命一击饰品配置").push("lethal_crit");
            lethalCritCritChance = builder.comment("暴击几率加成 (默认: 1.5)").defineInRange("critChance", 1.5, -1, 100);
            builder.pop();
            
            // R-02 弱点感应
            builder.comment("弱点感应饰品配置").push("weakness_sense");
            weaknessSenseCritDamage = builder.comment("暴击伤害加成 (默认: 1.2)").defineInRange("critDamage", 1.2, -1, 100);
            builder.pop();
            
            // R-03 氩晶瞄具
            builder.comment("氩晶瞄具饰品配置").push("argon_scope");
            argonScopeBaseCritChance = builder.comment("基础暴击几率 (默认: 1.35)").defineInRange("baseCritChance", 1.35, -1, 100);
            argonScopeDuration = builder.comment("Buff持续时间(秒) (默认: 9)").defineInRange("duration", 9, 1, 300);
            builder.pop();
            
            // R-04 镀层氩晶瞄具
            builder.comment("镀层氩晶瞄具饰品配置").push("gilded_argon_scope");
            gildedArgonScopeBaseCritChance = builder.comment("基础暴击几率 (默认: 1.2)").defineInRange("baseCritChance", 1.2, -1, 100);
            gildedArgonScopeCritChancePerLevel = builder.comment("Buff每级暴击几率 (默认: 0.4)").defineInRange("critChancePerLevel", 0.4, -1, 100);
            gildedArgonScopeHeadshotKillExtra = builder.comment("爆头击杀暴击率额外加成 (默认: 0.4)").defineInRange("headshotKillExtra", 0.4, -1, 100);
            gildedArgonScopeDuration = builder.comment("Buff持续时间(秒) (默认: 12)").defineInRange("duration", 12, 1, 300);
            gildedArgonScopeMaxStacks = builder.comment("最大叠加层数 (默认: 5)").defineInRange("maxStacks", 5, 1, 20);
            builder.pop();
            
            // R-05 尖刃弹头
            builder.comment("尖刃弹头饰品配置").push("sharp_bullet");
            sharpBulletBaseCritDamage = builder.comment("基础暴击伤害 (默认: 1.2)").defineInRange("baseCritDamage", 1.2, -1, 100);
            sharpBulletDuration = builder.comment("Buff持续时间(秒) (默认: 9)").defineInRange("duration", 9, 1, 300);
            builder.pop();
            
            // R-06 镀层步枪才能
            builder.comment("镀层步枪才能饰品配置").push("gilded_rifle_aptitude");
            gildedRifleAptitudePerHarmful = builder.comment("每负面效果种数增伤比例 (默认: 0.4)").defineInRange("perHarmful", 0.4, -1, 100);
            gildedRifleAptitudeDuration = builder.comment("Buff持续时间(秒) (默认: 20)").defineInRange("duration", 20, 1, 300);
            gildedRifleAptitudeMaxStacks = builder.comment("最大叠加层数 (默认: 2)").defineInRange("maxStacks", 2, 1, 20);
            builder.pop();
            
            // R-07 镀层分裂膛室
            builder.comment("镀层分裂膛室饰品配置").push("gilded_split_chamber");
            gildedSplitChamberBulletCountBase = builder.comment("基础弹头数量加成 (默认: 0.8)").defineInRange("bulletCountBase", 0.8, -1, 100);
            gildedSplitChamberBulletCountPerLevel = builder.comment("Buff每级额外弹头数量 (默认: 0.3)").defineInRange("bulletCountPerLevel", 0.3, -1, 100);
            gildedSplitChamberDuration = builder.comment("Buff持续时间(秒) (默认: 20)").defineInRange("duration", 20, 1, 300);
            gildedSplitChamberMaxStacks = builder.comment("最大叠加层数 (默认: 5)").defineInRange("maxStacks", 5, 1, 20);
            builder.pop();
            
            // S-01 破灭
            builder.comment("破灭饰品配置").push("destruction");
            destructionCritDamage = builder.comment("暴击伤害加成 (默认: 0.6)").defineInRange("critDamage", 0.6, -1, 100);
            builder.pop();
            
            // S-02 破灭Prime
            builder.comment("破灭Prime饰品配置").push("destruction_prime");
            destructionPrimeCritDamage = builder.comment("暴击伤害加成 (默认: 1.1)").defineInRange("critDamage", 1.1, -1, 100);
            builder.pop();
            
            // S-03 雷筒
            builder.comment("雷筒饰品配置").push("thunder_barrel");
            thunderBarrelCritChance = builder.comment("暴击几率加成 (默认: 0.9)").defineInRange("critChance", 0.9, -1, 100);
            builder.pop();
            
            // S-04 雷筒Prime
            builder.comment("雷筒Prime饰品配置").push("thunder_barrel_prime");
            thunderBarrelPrimeCritChance = builder.comment("暴击几率加成 (默认: 1.65)").defineInRange("critChance", 1.65, -1, 100);
            builder.pop();
            
            // S-05 雷射瞄具
            builder.comment("雷射瞄具饰品配置").push("laser_scope");
            laserScopeBaseCritChance = builder.comment("基础暴击几率 (默认: 1.2)").defineInRange("baseCritChance", 1.2, -1, 100);
            laserScopeDuration = builder.comment("Buff持续时间(秒) (默认: 9)").defineInRange("duration", 9, 1, 300);
            builder.pop();
            
            // S-06 破片射击
            builder.comment("破片射击饰品配置").push("fragment_shot");
            fragmentShotBaseCritDamage = builder.comment("基础暴击伤害 (默认: 0.99)").defineInRange("baseCritDamage", 0.99, -1, 100);
            fragmentShotDuration = builder.comment("Buff持续时间(秒) (默认: 9)").defineInRange("duration", 9, 1, 300);
            builder.pop();
            
            // S-07 镀层通晓霰弹枪
            builder.comment("镀层通晓霰弹枪饰品配置").push("gilded_shotgun_savvy");
            gildedShotgunSavvyPerHarmful = builder.comment("每负面效果种数增伤比例 (默认: 0.4)").defineInRange("perHarmful", 0.4, -1, 100);
            gildedShotgunSavvyDuration = builder.comment("Buff持续时间(秒) (默认: 20)").defineInRange("duration", 20, 1, 300);
            gildedShotgunSavvyMaxStacks = builder.comment("最大叠加层数 (默认: 2)").defineInRange("maxStacks", 2, 1, 20);
            builder.pop();
            
            // S-08 镀层地狱弹膛
            builder.comment("镀层地狱弹膛饰品配置").push("gilded_infernal_chamber");
            gildedInfernalChamberBulletCountBase = builder.comment("基础弹头数量加成 (默认: 1.1)").defineInRange("bulletCountBase", 1.1, -1, 100);
            gildedInfernalChamberBulletCountPerLevel = builder.comment("Buff每级额外弹头数量 (默认: 0.3)").defineInRange("bulletCountPerLevel", 0.3, -1, 100);
            gildedInfernalChamberDuration = builder.comment("Buff持续时间(秒) (默认: 20)").defineInRange("duration", 20, 1, 300);
            gildedInfernalChamberMaxStacks = builder.comment("最大叠加层数 (默认: 5)").defineInRange("maxStacks", 5, 1, 20);
            builder.pop();
            
            // P-01 弱点专精
            builder.comment("弱点专精饰品配置").push("weakness_mastery");
            weaknessMasteryCritDamage = builder.comment("暴击伤害加成 (默认: 0.6)").defineInRange("critDamage", 0.6, -1, 100);
            builder.pop();
            
            // P-02 弱点专精Prime
            builder.comment("弱点专精Prime饰品配置").push("weakness_mastery_prime");
            weaknessMasteryPrimeCritDamage = builder.comment("暴击伤害加成 (默认: 1.1)").defineInRange("critDamage", 1.1, -1, 100);
            builder.pop();
            
            // P-03 空尖弹
            builder.comment("空尖弹饰品配置").push("hollow_point");
            hollowPointCritDamage = builder.comment("暴击伤害加成 (默认: 0.6)").defineInRange("critDamage", 0.6, -1, 100);
            hollowPointPistolDamageReduction = builder.comment("手枪伤害降低 (默认: -0.15)").defineInRange("pistolDamageReduction", -0.15, -1, 0);
            builder.pop();
            
            // P-04 手枪精通
            builder.comment("手枪精通饰品配置").push("pistol_mastery");
            pistolMasteryCritChance = builder.comment("暴击几率加成 (默认: 1.2)").defineInRange("critChance", 1.2, -1, 100);
            builder.pop();
            
            // P-05 手枪精通Prime
            builder.comment("手枪精通Prime饰品配置").push("pistol_mastery_prime");
            pistolMasteryPrimeCritChance = builder.comment("暴击几率加成 (默认: 1.87)").defineInRange("critChance", 1.87, -1, 100);
            builder.pop();
            
            // P-06 液压准心
            builder.comment("液压准心饰品配置").push("hydraulic_crosshair");
            hydraulicCrosshairBaseCritChance = builder.comment("基础暴击几率 (默认: 1.35)").defineInRange("baseCritChance", 1.35, -1, 100);
            hydraulicCrosshairDuration = builder.comment("Buff持续时间(秒) (默认: 9)").defineInRange("duration", 9, 1, 300);
            builder.pop();
            
            // P-07 镀层液压准心
            builder.comment("镀层液压准心饰品配置").push("gilded_hydraulic_crosshair");
            gildedHydraulicCrosshairBaseCritChance = builder.comment("基础暴击几率 (默认: 1.2)").defineInRange("baseCritChance", 1.2, -1, 100);
            gildedHydraulicCrosshairCritChancePerLevel = builder.comment("Buff每级暴击几率 (默认: 0.4)").defineInRange("critChancePerLevel", 0.4, -1, 100);
            gildedHydraulicCrosshairHeadshotKillExtra = builder.comment("爆头击杀暴击率额外加成 (默认: 0.4)").defineInRange("headshotKillExtra", 0.4, -1, 100);
            gildedHydraulicCrosshairDuration = builder.comment("Buff持续时间(秒) (默认: 12)").defineInRange("duration", 12, 1, 300);
            gildedHydraulicCrosshairMaxStacks = builder.comment("最大叠加层数 (默认: 5)").defineInRange("maxStacks", 5, 1, 20);
            builder.pop();
            
            // P-08 尖锐子弹
            builder.comment("尖锐子弹饰品配置").push("sharp_ammo");
            sharpAmmoBaseCritDamage = builder.comment("基础暴击伤害 (默认: 0.75)").defineInRange("baseCritDamage", 0.75, -1, 100);
            sharpAmmoDuration = builder.comment("Buff持续时间(秒) (默认: 9)").defineInRange("duration", 9, 1, 300);
            builder.pop();
            
            // P-09 镀层准确射手
            builder.comment("镀层准确射手饰品配置").push("gilded_marksman");
            gildedMarksmanPerHarmful = builder.comment("每负面效果种数增伤比例 (默认: 0.4)").defineInRange("perHarmful", 0.4, -1, 100);
            gildedMarksmanDuration = builder.comment("Buff持续时间(秒) (默认: 20)").defineInRange("duration", 20, 1, 300);
            gildedMarksmanMaxStacks = builder.comment("最大叠加层数 (默认: 3)").defineInRange("maxStacks", 3, 1, 20);
            builder.pop();
            
            // P-10 镀层弹头扩散
            builder.comment("镀层弹头扩散饰品配置").push("gilded_bullet_spread");
            gildedBulletSpreadBulletCountBase = builder.comment("基础弹头数量加成 (默认: 1.1)").defineInRange("bulletCountBase", 1.1, -1, 100);
            gildedBulletSpreadBulletCountPerLevel = builder.comment("Buff每级额外弹头数量 (默认: 0.3)").defineInRange("bulletCountPerLevel", 0.3, -1, 100);
            gildedBulletSpreadDuration = builder.comment("Buff持续时间(秒) (默认: 20)").defineInRange("duration", 20, 1, 300);
            gildedBulletSpreadMaxStacks = builder.comment("最大叠加层数 (默认: 4)").defineInRange("maxStacks", 4, 1, 20);
            builder.pop();
            
            // M-01 斩铁
            builder.comment("斩铁饰品配置").push("steel_slash");
            steelSlashCritChance = builder.comment("暴击几率加成 (默认: 1.2)").defineInRange("critChance", 1.2, -1, 100);
            builder.pop();
            
            // M-02 肢解
            builder.comment("肢解饰品配置").push("dismemberment");
            dismembermentCritDamage = builder.comment("暴击伤害加成 (默认: 0.9)").defineInRange("critDamage", 0.9, -1, 100);
            builder.pop();
            
            // M-03 牺牲压迫点
            builder.comment("牺牲压迫点饰品配置").push("sacrifice_oppression");
            sacrificeOppressionMeleeDamage = builder.comment("近战伤害加成 (默认: 1.1)").defineInRange("meleeDamage", 1.1, -1, 100);
            builder.pop();
            
            // M-04 牺牲斩铁
            builder.comment("牺牲斩铁饰品配置").push("sacrifice_steel");
            sacrificeSteelCritChance = builder.comment("暴击几率加成 (默认: 2.2)").defineInRange("critChance", 2.2, -1, 100);
            builder.pop();
            
            // M-05 镀层斩铁
            builder.comment("镀层斩铁饰品配置").push("gilded_steel_slash");
            gildedSteelSlashCritChanceBase = builder.comment("基础暴击几率 (默认: 1.1)").defineInRange("critChanceBase", 1.1, -1, 100);
            gildedSteelSlashCritDamagePerLevel = builder.comment("Buff每级暴击伤害 (默认: 0.3)").defineInRange("critDamagePerLevel", 0.3, -1, 100);
            gildedSteelSlashDuration = builder.comment("Buff持续时间(秒) (默认: 20)").defineInRange("duration", 20, 1, 300);
            gildedSteelSlashMaxStacks = builder.comment("最大叠加层数 (默认: 4)").defineInRange("maxStacks", 4, 1, 20);
            builder.pop();
            
            // M-06 异况超量
            builder.comment("异况超量饰品配置").push("condition_overload");
            conditionOverloadPerHarmful = builder.comment("每负面效果种数增伤比例 (默认: 0.8)").defineInRange("perHarmful", 0.8, -1, 100);
            builder.pop();
            
            // M-07 牺牲套装组合
            builder.comment("牺牲套装组合配置").push("sacrifice_set");
            sacrificeSetBonus = builder.comment("套装加成倍率 (默认: 1.25)").defineInRange("setBonus", 1.25, -1, 100);
            builder.pop();

            // 虚数崩解配置
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
        
        // 烈焰风暴Prime配置
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
            
            // 撕裂Prime配置
            builder.comment("撕裂Prime饰品配置").push("ripping_prime");
            rippingPrimeFireRateBoost = builder
                    .comment("射速加成 (默认: 0.55 = 55%)")
                    .defineInRange("fireRateBoost", 0.55, -1, 100);
            rippingPrimePenetrationBoost = builder
                    .comment("穿透加成 (默认: 2.2)")
                    .defineInRange("penetrationBoost", 2.2, -1, 100);
            builder.pop();
            
            // 抵近射击Prime配置
            builder.comment("抵近射击Prime饰品配置").push("close_combat_prime");
            closeCombatPrimeShotgunDamageBoost = builder
                    .comment("霰弹枪伤害加成 (默认: 1.65 = 165%)")
                    .defineInRange("shotgunDamageBoost", 1.65, -1, 100);
            builder.pop();
            
            // 极恶精准配置
            builder.comment("极恶精准饰品配置").push("evil_accuracy");
            evilAccuracyRecoilReduction = builder
                    .comment("后坐力降低 (默认: -0.9 = -90%)")
                    .defineInRange("recoilReduction", -0.9, -1, 1);
            evilAccuracyFireRateReduction = builder
                    .comment("射速降低 (默认: -0.36 = -36%)")
                    .defineInRange("fireRateReduction", -0.36, -1, 0);
            builder.pop();
            
            // 极限速度配置
            builder.comment("极限速度饰品配置").push("limit_speed");
            limitSpeedBulletSpeedBoost = builder
                    .comment("弹药速度加成 (默认: 0.6 = 60%)")
                    .defineInRange("bulletSpeedBoost", 0.6, -1, 100);
            builder.pop();
            
            // 凶恶延伸配置
            builder.comment("凶恶延伸饰品配置").push("ferocious_extension");
            ferociousExtensionRangeBoost = builder
                    .comment("子弹射程加成 (默认: 1.2 = 120%)")
                    .defineInRange("rangeBoost", 1.2, -1, 100);
            builder.pop();
            
            // 抵近射击配置
            builder.comment("抵近射击饰品配置").push("close_range_shot");
            closeRangeShotDamageBoost = builder
                    .comment("霰弹枪伤害加成 (默认: 0.9 = 90%)")
                    .defineInRange("damageBoost", 0.9, -1, 100);
            builder.pop();
            
            // 重装火力配置
            builder.comment("重装火力饰品配置").push("heavy_firepower");
            heavyFirepowerDamageBoost = builder
                    .comment("手枪伤害加成 (默认: +1.65 = +165%)")
                    .defineInRange("damageBoost", 1.65, -1, 100);
            heavyFirepowerAccuracyReduction = builder
                    .comment("扩散程度增加 (默认: +0.55 = +55%)")
                    .defineInRange("accuracyReduction", 0.55, -1, 1);
            builder.pop();
            
            // 黄蜂蜇刺配置
            builder.comment("黄蜂蜇刺饰品配置").push("wasp_stinger");
            waspStingerDamageBoost = builder
                    .comment("手枪伤害加成 (默认: 2.2 = 220%)")
                    .defineInRange("damageBoost", 2.2, -1, 100);
            builder.pop();
            
            // 预言契约配置
            builder.comment("预言契约饰品配置").push("prophecy_pact");
            prophecyPactDamageBoost = builder
                    .comment("手枪伤害加成 (默认: 0.9 = 90%)")
                    .defineInRange("damageBoost", 0.9, -1, 100);
            builder.pop();
            
            // 恶性扩散配置
            builder.comment("恶性扩散饰品配置").push("malignant_spread");
            malignantSpreadDamageBoost = builder
                    .comment("霰弹枪伤害加成 (默认: +1.65 = +165%)")
                    .defineInRange("damageBoost", 1.65, -1, 100);
            malignantSpreadAccuracyReduction = builder
                    .comment("扩散程度增加 (默认: +0.55 = +55%)")
                    .defineInRange("accuracyReduction", 0.55, -1, 1);
            builder.pop();
            
            // 膛室配置
            builder.comment("膛室饰品配置").push("chamber");
            chamberSniperDamageBoost = builder
                    .comment("狙击枪伤害加成 (默认: 0.4 = 40%)")
                    .defineInRange("sniperDamageBoost", 0.4, -1, 100);
            builder.pop();
            
            // 膛室Prime配置
            builder.comment("膛室Prime饰品配置").push("chamber_prime");
            chamberPrimeSniperDamageBoost = builder
                    .comment("狙击枪伤害加成 (默认: 1.0 = 100%)")
                    .defineInRange("sniperDamageBoost", 1.0, -1, 100);
            builder.pop();
            
            // 战术上膛配置
            builder.comment("战术上膛饰品配置").push("tactical_reload");
            tacticalReloadSpeedBoost = builder
                    .comment("霰弹枪装填时间加成 (默认: -0.6 = -60%)")
                    .defineInRange("reloadSpeedBoost", -0.6, -1, 100);
            builder.pop();
            
            // 过载弹匣配置
            builder.comment("过载弹匣饰品配置").push("overloaded_magazine");
            overloadedMagazineCapacityBoost = builder
                    .comment("霰弹枪弹匣容量加成 (默认: +0.6 = +60%)")
                    .defineInRange("capacityBoost", 0.6, -1, 100);
            overloadedMagazineReloadSpeedReduction = builder
                    .comment("装填时间增加 (默认: +0.18 = +18%)")
                    .defineInRange("reloadSpeedReduction", 0.18, -1, 1);
            builder.pop();
            
            // 地狱弹膛配置
            builder.comment("地狱弹膛饰品配置").push("infernal_chamber");
            infernalChamberBulletCountBoost = builder
                    .comment("霰弹枪弹头数量加成 (默认: 1.2 = 120%)")
                    .defineInRange("bulletCountBoost", 1.2, -1, 100);
            builder.pop();
            
            // 持续火力配置
            builder.comment("持续火力饰品配置").push("sustained_fire");
            sustainedFireReloadSpeedBoost = builder
                    .comment("手枪装填时间加成 (默认: -0.48 = -48%)")
                    .defineInRange("reloadSpeedBoost", -0.48, -1, 100);
            builder.pop();
            
            // 感染弹匣配置
            builder.comment("感染弹匣饰品配置").push("infected_magazine");
            infectedMagazineCapacityBoost = builder
                    .comment("手枪弹匣容量加成 (默认: +0.6 = +60%)")
                    .defineInRange("capacityBoost", 0.6, -1, 100);
            infectedMagazineReloadSpeedReduction = builder
                    .comment("装填时间增加 (默认: +0.3 = +30%)")
                    .defineInRange("reloadSpeedReduction", 0.3, -1, 1);
            builder.pop();
            
            // 致命洪流配置
            builder.comment("致命洪流饰品配置").push("deadly_surge");
            deadlySurgeFireRateBoost = builder
                    .comment("手枪射速加成 (默认: 0.6 = 60%)")
                    .defineInRange("fireRateBoost", 0.6, -1, 100);
            deadlySurgeBulletCountBoost = builder
                    .comment("弹头数量加成 (默认: 0.6 = 60%)")
                    .defineInRange("bulletCountBoost", 0.6, -1, 100);
            builder.pop();
            
            // 弹头扩散配置
            builder.comment("弹头扩散饰品配置").push("bullet_spread");
            bulletSpreadBulletCountBoost = builder
                    .comment("手枪弹头数量加成 (默认: 1.2 = 120%)")
                    .defineInRange("bulletCountBoost", 1.2, -1, 100);
            builder.pop();
            
            // 压迫点配置
            builder.comment("压迫点饰品配置").push("oppression_point");
            oppressionPointMeleeDamageBoost = builder
                    .comment("近战伤害加成 (默认: 1.2 = 120%)")
                    .defineInRange("meleeDamageBoost", 1.2, -1, 100);
            builder.pop();
            
            // 压迫点Prime配置
            builder.comment("压迫点Prime饰品配置").push("oppression_point_prime");
            oppressionPointPrimeMeleeDamageBoost = builder
                    .comment("近战伤害加成 (默认: 1.65 = 165%)")
                    .defineInRange("meleeDamageBoost", 1.65, -1, 100);
            builder.pop();
            
            // 爆发装填配置
            builder.comment("爆发装填饰品配置").push("burst_reload");
            burstReloadReloadSpeedBoost = builder
                    .comment("装填时间加成 (默认: -0.3 = -30%)")
                    .defineInRange("reloadSpeedBoost",-0.3, -1, 100);
            builder.pop();
            
            // 剑风配置
            builder.comment("剑风饰品配置").push("sword_wind");
            swordWindMeleeRangeBoost = builder
                    .comment("近战距离加成 (默认: 1.1)")
                    .defineInRange("meleeRangeBoost", 1.1, -1, 100);
            builder.pop();
            
            // 剑风Prime配置
            builder.comment("剑风Prime饰品配置").push("sword_wind_prime");
            swordWindPrimeMeleeRangeBoost = builder
                    .comment("近战距离加成 (默认: 3)")
                    .defineInRange("meleeRangeBoost", 3.0, -1, 100);
            builder.pop();
            
            // 腐败弹匣配置
            builder.comment("腐败弹匣饰品配置").push("corrupt_magazine");
            corruptMagazineCapacityBoost = builder
                    .comment("弹匣容量加成 (默认: +0.66 = +66%)")
                    .defineInRange("capacityBoost", 0.66, -1, 100);
            corruptMagazineReloadSpeedReduction = builder
                    .comment("装填时间增加 (默认: +0.33 = +33%)")
                    .defineInRange("reloadSpeedReduction", 0.33, -1, 1);
            builder.pop();
            
            // 重口径配置
            builder.comment("重口径饰品配置").push("heavy_caliber_tag");
            heavyCaliberTagDamageBoost = builder
                    .comment("特定枪械伤害加成 (默认: +1.65 = +165%)")
                    .defineInRange("damageBoost", 1.65, -1, 100);
            heavyCaliberTagInaccuracyBoost = builder
                    .comment("扩散程度加成 (默认: +0.55 = +55%)")
                    .defineInRange("inaccuracyBoost", 0.55, -1, 100);
            builder.pop();
            
            // 红-有-三配置
            builder.comment("红-有-三饰品配置").push("red_movement_tag");
            redMovementTagSpeedBoost = builder
                    .comment("移动速度加成 (默认: 1.5 = 150%)")
                    .defineInRange("speedBoost", 1.5, -1, 100);
            builder.pop();
            
            // 希奥拉配置
            builder.comment("希奥拉饰品配置").push("xiora");
            xioraBaseResistance = builder
                    .comment("希奥拉虚数抗性基础值 (默认: 21)")
                    .defineInRange("baseResistance", 21, 0, 1000);
            builder.pop();
            
            // 夏日沙滩配置
            builder.comment("夏日沙滩饰品配置").push("summer_beach");
            summerBeachELCurseReduction = builder
                    .comment("夏日沙滩对第四诅咒效果的削弱比例 (默认: 0.25 = 抵消25%的诅咒效果)")
                    .defineInRange("elCurseReduction", 0.25, 0, 1);
            summerBeachBaseResistance = builder
                    .comment("夏日沙滩虚数抗性基础值 (默认: 41)")
                    .defineInRange("baseResistance", 41, 0, 1000);
            builder.pop();
            
            // 梵天百兽配置
            builder.comment("梵天百兽饰品配置").push("brahma_beasts");
            brahmaBeastsELCurseReduction = builder
                    .comment("梵天百兽对第四诅咒效果的削弱比例 (默认: 0.5 = 抵消50%的诅咒效果)")
                    .defineInRange("elCurseReduction", 0.5, 0, 1);
            builder.pop();
            
            // 救世配置
            builder.comment("救世饰品配置").push("salvation");
            salvationELCurseReduction = builder
                    .comment("救世对第四诅咒效果的削弱比例 (默认: 1.0 = 完全免疫第四诅咒)")
                    .defineInRange("elCurseReduction", 1.0, 0, 1);
            salvationDamageReduction = builder
                    .comment("救世伤害降低比例 (默认: 0.3 = 30%)")
                    .defineInRange("damageReduction", 0.3, 0, 1);
            salvationResistanceLevel = builder
                    .comment("救世抗性提升等级 (默认: 2 = 抗性III)")
                    .defineInRange("resistanceLevel", 2, 0, 10);
            builder.pop();
            
            // 无烬终焉配置
            builder.comment("无烬终焉饰品配置").push("endless");
            endlessDamageBoost = builder
                    .comment("无烬终焉通用枪械伤害加成 (默认: 10.0 = 1000%，与天火劫灭一致)")
                    .defineInRange("damageBoost", 10.0, -1, 100);
            endlessExplosionDamage = builder
                    .comment("无烬终焉爆炸伤害加成 (默认: 10.0 = 1000%，与天火劫灭一致)")
                    .defineInRange("explosionDamage", 10.0, -1, 100);
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
            endlessGunTypes = builder
                    .comment("无烬终焉生效的枪械类型列表 (可选: pistol, rifle, shotgun, sniper, smg, mg, rpg)")
                    .defineList("gunTypes", List.of("pistol"), o -> o instanceof String);
            endlessDamageConversionRatio = builder
                    .comment("无烬终焉伤害转换比例：受到的伤害乘以此值后转为虚数伤害 (默认: 0.01 = 1%)")
                    .defineInRange("damageConversionRatio", 0.01, 0, 1);
            builder.pop();
            
            // 士兵基础挂牌配置
            builder.comment("士兵基础挂牌饰品配置").push("soldier_basic_tag");
            soldierBasicTagDamageBoost = builder
                    .comment("通用枪械伤害加成 (默认: 0.3 = 30%)")
                    .defineInRange("damageBoost", 0.3, -1, 100);
            builder.pop();
            
            // 弹匣增幅配置
            builder.comment("弹匣增幅饰品配置").push("magazine_boost");
            magazineBoostReloadSpeedBoost = builder
                    .comment("装填时间加成 (默认: -0.3 = -30%)")
                    .defineInRange("reloadSpeedBoost",-0.3, -1, 100);
            magazineBoostCapacityBoost = builder
                    .comment("弹匣容量加成 (默认: 0.3 = 30%)")
                    .defineInRange("capacityBoost", 0.3, -1, 100);
            builder.pop();
            
            // 士兵特定挂牌配置
            builder.comment("士兵特定挂牌饰品配置").push("soldier_specific_tag");
            soldierSpecificTagDamageBoost = builder
                    .comment("通用枪械伤害加成 (默认: 0.55 = 55%)")
                    .defineInRange("damageBoost", 0.55, -1, 100);
            builder.pop();
            
            // 乌拉尔银狼配置
            builder.comment("乌拉尔银狼饰品配置").push("ural_wolf_tag");
            uralWolfTagHeadshotMultiplierBoost = builder
                    .comment("爆头倍率加成 (默认: 1.5 = 150%)")
                    .defineInRange("headshotMultiplierBoost", 1.5, -1, 100);
            builder.pop();
            
            // 耗竭装填配置
            builder.comment("耗竭装填饰品配置").push("depleted_reload");
            depletedReloadMagazineCapacityPenalty = builder
                    .comment("弹匣容量减少 (默认: -0.6 = -60%)")
                    .defineInRange("magazineCapacityPenalty", -0.6, -1, 0);
            depletedReloadReloadSpeedBoost = builder
                    .comment("装填时间加成 (默认: -0.48 = -48%)")
                    .defineInRange("reloadSpeedBoost", -0.48, -1, 100);
            builder.pop();
            
            // 爆发装填Prime配置
            builder.comment("爆发装填Prime饰品配置").push("burst_reload_prime");
            burstReloadPrimeReloadSpeedBoost = builder
                    .comment("装填时间加成 (默认: -0.55 = -55%)")
                    .defineInRange("reloadSpeedBoost", -0.55, -1, 100);
            builder.pop();
            
            // 战术上膛Prime配置
            builder.comment("战术上膛Prime饰品配置").push("tactical_reload_prime");
            tacticalReloadPrimeReloadSpeedBoost = builder
                    .comment("装填时间加成 (默认: -0.99 = -100%)")
                    .defineInRange("reloadSpeedBoost", -0.99, -1, 100);
            builder.pop();
            
            // 霰弹扩充Prime配置
            builder.comment("霰弹扩充Prime饰品配置").push("shotgun_expansion_prime");
            shotgunExpansionPrimeCapacityBoost = builder
                    .comment("弹匣容量加成 (默认: 1.1 = 110%)")
                    .defineInRange("capacityBoost", 1.1, -1, 100);
            builder.pop();
            
            // 弹匣增幅Prime配置
            builder.comment("弹匣增幅Prime饰品配置").push("magazine_boost_prime");
            magazineBoostPrimeCapacityBoost = builder
                    .comment("弹匣容量加成 (默认: 0.55 = 55%)")
                    .defineInRange("capacityBoost", 0.55, -1, 100);
            builder.pop();
            
            // 串联弹匣Prime配置
            builder.comment("串联弹匣Prime饰品配置").push("tandem_magazine_prime");
            tandemMagazinePrimeCapacityBoost = builder
                    .comment("弹匣容量加成 (默认: 0.55 = 55%)")
                    .defineInRange("capacityBoost", 0.55, -1, 100);
            builder.pop();
            
            // 霰弹扩充配置
            builder.comment("霰弹扩充饰品配置").push("shotgun_expansion");
            shotgunExpansionCapacityBoost = builder
                    .comment("弹匣容量加成 (默认: 0.6 = 60%)")
                    .defineInRange("capacityBoost", 0.6, -1, 100);
            builder.pop();
            

            // 串联弹匣配置
            builder.comment("串联弹匣饰品配置").push("tandem_magazine");
            tandemMagazineCapacityBoost = builder
                    .comment("弹匣容量加成 (默认: 0.3 = 30%)")
                    .defineInRange("capacityBoost", 0.3, -1, 100);
            builder.pop();
            
            // 裂隙碎银配置
            builder.comment("裂隙碎银配置").push("rift_silver");
            riftSilverChestSpawnChance = builder
                    .comment("裂隙碎银在箱子中的生成几率 (默认: 0.05 = 5%)")
                    .defineInRange("chestSpawnChance", 0.05, -1, 1);
            builder.pop();
            
            // 掎角一阵配置
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
            
            // Apotheosis集成配置
            builder.comment("Apotheosis神化属性集成配置").push("apotheosis_integration");
            enableApotheosisIntegration = builder
                    .comment("是否启用TCC饰品的Apotheosis神化属性支持 (默认: true)")
                    .define("enableApotheosisIntegration", true);
            builder.pop();
            
            // 饰品互斥配置
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
            
            builder.pop();
        }
    }
    
    public static void registerConfigs() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, COMMON_SPEC);
    }
}
