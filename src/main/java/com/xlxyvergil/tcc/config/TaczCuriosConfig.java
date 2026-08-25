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
        public final ForgeConfigSpec.IntValue apocalypseImaginaryInfectionMaxLevel;
        public final ForgeConfigSpec.IntValue endlessImaginaryInfectionMaxLevel;
        public final ForgeConfigSpec.IntValue shijieFanyanImaginaryInfectionMaxLevel;
        public final ForgeConfigSpec.IntValue xukongWancangYZTHImaginaryInfectionMaxLevel;
        public final ForgeConfigSpec.IntValue metaMorphImaginaryInfectionMaxLevel;
        public final ForgeConfigSpec.IntValue yinguoZhuanlunImaginaryInfectionMaxLevel;

        // 裁决之键配置
        public final ForgeConfigSpec.DoubleValue judgementProcChance;
        public final ForgeConfigSpec.DoubleValue judgementDirectDamagePercent;
        public final ForgeConfigSpec.DoubleValue judgementCollapseProcChance;
        public final ForgeConfigSpec.DoubleValue judgementKeyCritChance;
        public final ForgeConfigSpec.DoubleValue judgementKeyCritDamage;

        // 涤罪七雷配置
        public final ForgeConfigSpec.DoubleValue sevenThundersHeadshotMultiplier;
        public final ForgeConfigSpec.DoubleValue sevenThundersCritChance;
        public final ForgeConfigSpec.DoubleValue sevenThundersCritDamage;

        // 雷鸣见（涤罪七雷·雷见）配置
        public final ForgeConfigSpec.DoubleValue sevenThundersThunderSeenHeadshotMultiplier;
        public final ForgeConfigSpec.DoubleValue sevenThundersThunderSeenCritChance;
        public final ForgeConfigSpec.DoubleValue sevenThundersThunderSeenCritDamage;
        public final ForgeConfigSpec.DoubleValue sevenThundersThunderSeenProcChance;
        public final ForgeConfigSpec.DoubleValue sevenThundersThunderSeenExtraHpDamage;

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
        public final ForgeConfigSpec.DoubleValue xioraArmorMultiplier;
        public final ForgeConfigSpec.DoubleValue xioraSpeedMultiplier;

        // 夜袭渡鸦配置
        public final ForgeConfigSpec.DoubleValue ravenArmorMultiplier;
        public final ForgeConfigSpec.DoubleValue ravenSpeedMultiplier;
        public final ForgeConfigSpec.IntValue ravenInvisRefreshInterval;
        public final ForgeConfigSpec.IntValue ravenInvisDuration;
        public final ForgeConfigSpec.IntValue ravenInvisBreakDelay;

        // 岛爆渡鸦配置
        public final ForgeConfigSpec.DoubleValue islandBoomRavenArmorMultiplier;
        public final ForgeConfigSpec.DoubleValue islandBoomRavenSpeedMultiplier;
        public final ForgeConfigSpec.IntValue islandBoomRavenInvisRefreshInterval;
        public final ForgeConfigSpec.IntValue islandBoomRavenInvisDuration;
        public final ForgeConfigSpec.IntValue islandBoomRavenInvisBreakDelay;
        public final ForgeConfigSpec.IntValue islandBoomRavenRegenAmplifier;
        public final ForgeConfigSpec.IntValue islandBoomRavenRegenRefreshThreshold;
        public final ForgeConfigSpec.IntValue islandBoomRavenRegenDuration;
        
        // 救世配置
        public final ForgeConfigSpec.DoubleValue salvationDamageReduction;
        public final ForgeConfigSpec.IntValue salvationResistanceLevel;
        
        // 无烬终焉配置
        public final ForgeConfigSpec.DoubleValue endlessDamageBoost;
        public final ForgeConfigSpec.DoubleValue endlessExplosionDamage;
        public final ForgeConfigSpec.DoubleValue endlessImaginaryResistanceDamagePerPoint;
        public final ForgeConfigSpec.DoubleValue endlessNearbyPlayerDamageBoost;
        public final ForgeConfigSpec.IntValue endlessNearbyPlayerPotionAmplifier;
        public final ForgeConfigSpec.IntValue endlessNearbyPlayerDuration;
        public final ForgeConfigSpec.DoubleValue endlessNearbyPlayerRadius;

        // ==== 吸收饰品通用配置 ====
        /** 吸收饰品触发血量阈值 (0~1, 默认 0.25 = 25%) */
        public final ForgeConfigSpec.DoubleValue curioAbsorptionTriggerHp;
        /** 吸收效果等级 (默认 4 = ABSORPTION IV) */
        public final ForgeConfigSpec.IntValue curioAbsorptionLevel;
        /** 吸收效果持续时间(秒) (默认 60) */
        public final ForgeConfigSpec.DoubleValue curioAbsorptionDuration;
        /** 吸收效果冷却(秒) (默认 180) */
        public final ForgeConfigSpec.DoubleValue curioAbsorptionCooldown;

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
        
        // 掎角一阵配置
        public final ForgeConfigSpec.DoubleValue kikakuIchijinHealthMultiplier;
        public final ForgeConfigSpec.BooleanValue kikakuIchijinDestroyUnbreakableBlocks;
        public final ForgeConfigSpec.BooleanValue kikakuIchijinDestroyNormalBlocks;
        


        // ========== 逐火之蛾/神之键 新饰品配置 ==========

        // 格蕾修配置
        public final ForgeConfigSpec.IntValue griseoHurtCooldownTicks;

        // 千界一乘配置
        public final ForgeConfigSpec.IntValue qianjieYichengLuck;

        // 绘世之卷配置
        public final ForgeConfigSpec.IntValue huishiZhijuanBaseCooldown;
        public final ForgeConfigSpec.IntValue huishiZhijuanLuckPerTick;
        public final ForgeConfigSpec.IntValue huishiZhijuanMaxCooldown;

        // 永劫之键配置
        public final ForgeConfigSpec.IntValue yongjieZhijianLuck;
        public final ForgeConfigSpec.DoubleValue yongjieZhijianCritChancePerLuck;
        public final ForgeConfigSpec.DoubleValue yongjieZhijianCritDamagePerLuck;

        // 繁星配置
        public final ForgeConfigSpec.IntValue fanxingBaseCooldown;
        public final ForgeConfigSpec.IntValue fanxingLuckPerTick;
        public final ForgeConfigSpec.IntValue fanxingMaxCooldown;
        public final ForgeConfigSpec.DoubleValue fanxingLuckPerResistance;

        // 视界反演配置
        public final ForgeConfigSpec.IntValue shijieFanyanLuck;
        public final ForgeConfigSpec.DoubleValue shijieFanyanCritChancePerLuck;
        public final ForgeConfigSpec.DoubleValue shijieFanyanCritDamagePerLuck;
        public final ForgeConfigSpec.DoubleValue shijieFanyanCollapseBaseChance;
        public final ForgeConfigSpec.DoubleValue shijieFanyanCollapsePerLuck;

        // ========== 重型武器系列饰品配置 ==========

        // 维尔薇配置
        public final ForgeConfigSpec.DoubleValue villVTriggerHpRatio;
        public final ForgeConfigSpec.IntValue villVAbsorptionLevel;
        public final ForgeConfigSpec.DoubleValue villVAbsorptionDuration;
        public final ForgeConfigSpec.DoubleValue villVCooldownSeconds;

        // 虚空万藏配置
        public final ForgeConfigSpec.DoubleValue xukongWancangImaginaryDamage;
        public final ForgeConfigSpec.DoubleValue xukongWancangAmmoRegenPercent;
        public final ForgeConfigSpec.DoubleValue xukongWancangHeatMax;
        public final ForgeConfigSpec.DoubleValue xukongWancangHeatCooling;

        // 愚戏之匣配置
        public final ForgeConfigSpec.DoubleValue yuxiZhixiaTriggerHpRatio;
        public final ForgeConfigSpec.IntValue yuxiZhixiaAbsorptionLevel;
        public final ForgeConfigSpec.DoubleValue yuxiZhixiaAbsorptionDuration;
        public final ForgeConfigSpec.DoubleValue yuxiZhixiaCooldownSeconds;

        // 启示之键配置
        public final ForgeConfigSpec.DoubleValue qishiZhijianImaginaryDamage;
        public final ForgeConfigSpec.DoubleValue qishiZhijianAmmoRegenPercent;
        public final ForgeConfigSpec.DoubleValue qishiZhijianHeatMax;
        public final ForgeConfigSpec.DoubleValue qishiZhijianHeatCooling;

        // 螺旋配置
        public final ForgeConfigSpec.IntValue luoxuanAbsorptionInterval;
        public final ForgeConfigSpec.IntValue luoxuanAbsorptionLevel;
        public final ForgeConfigSpec.IntValue luoxuanAbsorptionDuration;

        // 虚空万藏·雨众天华配置
        public final ForgeConfigSpec.DoubleValue xukongWancangYZTHImaginaryDamage;
        public final ForgeConfigSpec.DoubleValue xukongWancangYZTHAmmoRegenPercent;
        public final ForgeConfigSpec.IntValue xukongWancangYZTHInfectionDuration;
        public final ForgeConfigSpec.DoubleValue xukongWancangYZTHHeatMax;
        public final ForgeConfigSpec.DoubleValue xukongWancangYZTHHeatCooling;

        // 适应系统通用配置
        public final ForgeConfigSpec.IntValue adaptationMaxCount;

        // 千劫配置
        public final ForgeConfigSpec.IntValue kalpasMaxSlots;
        public final ForgeConfigSpec.DoubleValue kalpasAdaptFactor;
        public final ForgeConfigSpec.IntValue kalpasDecaySeconds;

        // 伊默尔配置
        public final ForgeConfigSpec.DoubleValue imerAttackDamageBonus;

        // 坏劫之焱配置
        public final ForgeConfigSpec.IntValue huajieZhiyanMaxSlots;
        public final ForgeConfigSpec.DoubleValue huajieZhiyanAdaptFactor;
        public final ForgeConfigSpec.IntValue huajieZhiyanDecaySeconds;
        public final ForgeConfigSpec.DoubleValue huajieZhiyanHealthPerResistance;

        // 支配之键配置
        public final ForgeConfigSpec.DoubleValue dominanceKeyHealthToAttackPercent;
        public final ForgeConfigSpec.DoubleValue dominanceKeyImaginaryDamageScale;

        // 鏖灭配置
        public final ForgeConfigSpec.IntValue aoMieMaxSlots;
        public final ForgeConfigSpec.DoubleValue aoMieAdaptFactor;
        public final ForgeConfigSpec.IntValue aoMieDecaySeconds;
        public final ForgeConfigSpec.DoubleValue aoMieHealthPerResistance;

        // Meta-Morph配置
        public final ForgeConfigSpec.DoubleValue metaMorphHealthToAttackPercent;
        public final ForgeConfigSpec.DoubleValue metaMorphResistanceToAttackPercent;
        public final ForgeConfigSpec.DoubleValue metaMorphLifeStealPerResistance;

        // 苏配置
        public final ForgeConfigSpec.DoubleValue suMaxHealthReduction;
        public final ForgeConfigSpec.DoubleValue suDamageTakenFactor;

        // 万物休眠配置
        public final ForgeConfigSpec.DoubleValue wanwuXiumianOverheal;
        public final ForgeConfigSpec.DoubleValue wanwuXiumianAmmoRegenPercent;

        // 觉者配置
        public final ForgeConfigSpec.DoubleValue juezheMaxHealthReduction;
        public final ForgeConfigSpec.DoubleValue juezheDamageTakenFactor;

        // 停滞之键配置
        public final ForgeConfigSpec.DoubleValue tingzhiZhijianOverheal;
        public final ForgeConfigSpec.DoubleValue tingzhiZhijianAmmoBasePercent;
        public final ForgeConfigSpec.DoubleValue tingzhiZhijianAmmoResistanceScale;

        // 天慧配置
        public final ForgeConfigSpec.DoubleValue tianhuiMaxHealthReduction;
        public final ForgeConfigSpec.DoubleValue tianhuiResistanceScale;
        public final ForgeConfigSpec.DoubleValue tianhuiMinDamageFactor;

        // 因果转轮配置
        public final ForgeConfigSpec.DoubleValue yinguoZhuanlunOverheal;
        public final ForgeConfigSpec.DoubleValue yinguoZhuanlunAmmoResistanceScale;

        // 逐火之蛾「真我」配置
        public final ForgeConfigSpec.DoubleValue zhenWoImaginaryResistance;
        public final ForgeConfigSpec.DoubleValue zhenWoAllAttributesPercent;
        public final ForgeConfigSpec.DoubleValue zhenWoTriggerHpRatio;
        public final ForgeConfigSpec.DoubleValue zhenWoBarrierRadius;
        public final ForgeConfigSpec.IntValue zhenWoSlownessAmplifier;
        public final ForgeConfigSpec.IntValue zhenWoSlownessDurationSeconds;
        public final ForgeConfigSpec.IntValue zhenWoBarrierDurationSeconds;
        public final ForgeConfigSpec.DoubleValue zhenWoDamagePercent;
        public final ForgeConfigSpec.IntValue zhenWoCooldownSeconds;

        // 黑渊白花·创灭螺旋配置
        public final ForgeConfigSpec.DoubleValue heiyuanBaihuaDamagePercent;

        // ========== 新系列饰品配置（戒律/黄金/旭光/无限/浮生/空梦）==========

        // 戒律系列·人物线（tcc_3rd）：随机 debuff
        public final ForgeConfigSpec.DoubleValue aponiaDebuffChance;
        public final ForgeConfigSpec.IntValue aponiaDebuffDurationSeconds;
        public final ForgeConfigSpec.IntValue aponiaDebuffCount;
        public final ForgeConfigSpec.DoubleValue shenzuiZhijianDebuffChance;
        public final ForgeConfigSpec.IntValue shenzuiZhijianDebuffDurationSeconds;
        public final ForgeConfigSpec.IntValue shenzuiZhijianDebuffCount;
        public final ForgeConfigSpec.DoubleValue jielvDebuffChance;
        public final ForgeConfigSpec.IntValue jielvDebuffDurationSeconds;
        public final ForgeConfigSpec.IntValue jielvDebuffCount;

        // 戒律系列·神之键线（tcc_tdk）：崩坏病
        public final ForgeConfigSpec.IntValue yudaDeShiyueDiseaseDurationSeconds;
        public final ForgeConfigSpec.IntValue yudaDeShiyueDiseaseAmplifier;
        public final ForgeConfigSpec.IntValue yueshuZhiJianDiseaseDurationSeconds;
        public final ForgeConfigSpec.IntValue yueshuZhiJianDiseaseAmplifier;
        public final ForgeConfigSpec.IntValue shenenJiejieDiseaseDurationSeconds;
        public final ForgeConfigSpec.IntValue shenenJiejieDiseaseAmplifier;

        // 黄金系列·人物线（tcc_3rd）：友方光环
        public final ForgeConfigSpec.DoubleValue edenAuraRange;
        public final ForgeConfigSpec.IntValue edenIntervalSeconds;
        public final ForgeConfigSpec.IntValue edenBuffDurationSeconds;
        public final ForgeConfigSpec.IntValue edenBuffAmplifier;
        public final ForgeConfigSpec.DoubleValue cuiyaoZhiGeAuraRange;
        public final ForgeConfigSpec.IntValue cuiyaoZhiGeIntervalSeconds;
        public final ForgeConfigSpec.IntValue cuiyaoZhiGeBuffDurationSeconds;
        public final ForgeConfigSpec.IntValue cuiyaoZhiGeBuffAmplifier;
        public final ForgeConfigSpec.DoubleValue huangjinAuraRange;
        public final ForgeConfigSpec.IntValue huangjinIntervalSeconds;
        public final ForgeConfigSpec.IntValue huangjinBuffDurationSeconds;
        public final ForgeConfigSpec.IntValue huangjinBuffAmplifier;

        // 黄金系列·神之键线（tcc_tdk）：瞬移失效
        public final ForgeConfigSpec.DoubleValue edenStarTeleportRange;
        public final ForgeConfigSpec.DoubleValue tuntianZhijianTeleportRange;
        public final ForgeConfigSpec.DoubleValue qidianChonggouTeleportRange;

        // 旭光系列·人物线（tcc_3rd）：攻速攻伤
        public final ForgeConfigSpec.DoubleValue kosmaAttackSpeedPercent;
        public final ForgeConfigSpec.DoubleValue kosmaAttackDamagePercent;
        public final ForgeConfigSpec.DoubleValue limingZhiShaoAttackSpeedPercent;
        public final ForgeConfigSpec.DoubleValue limingZhiShaoAttackDamagePercent;
        public final ForgeConfigSpec.DoubleValue limingZhiShaoCritChancePercent;
        public final ForgeConfigSpec.DoubleValue xuguangAttackSpeedPercent;
        public final ForgeConfigSpec.DoubleValue xuguangAttackDamagePercent;
        public final ForgeConfigSpec.DoubleValue xuguangCritDamagePercent;

        // 旭光系列·神之键线（tcc_tdk）：削甲
        public final ForgeConfigSpec.DoubleValue dizangYuhunStripPercent;
        public final ForgeConfigSpec.DoubleValue qinshiZhijianStripPercent;

        // 无限系列·人物线（tcc_3rd）：击杀累计
        public final ForgeConfigSpec.DoubleValue mebiusPerTypeBonus;
        public final ForgeConfigSpec.DoubleValue shijieZhiShePerTypeBonus;
        public final ForgeConfigSpec.DoubleValue wuxianPerTypeBonus;

        // 无限系列·神之键线（tcc_tdk）：移除正面 buff
        public final ForgeConfigSpec.DoubleValue wangshiDeSheyingRemoveChance;
        public final ForgeConfigSpec.DoubleValue siZhiYiRemoveChance;

        // 浮生系列·人物线（tcc_3rd）：百分比护甲/韧性
        public final ForgeConfigSpec.DoubleValue huaArmorPercent;
        public final ForgeConfigSpec.DoubleValue duchenZhiYuArmorPercent;

        // 浮生系列·神之键线（tcc_tdk）：停止 AI
        public final ForgeConfigSpec.DoubleValue yuduchenStopChance;
        public final ForgeConfigSpec.IntValue yuduchenStopDurationSeconds;
        public final ForgeConfigSpec.DoubleValue fanchenNanduStopChance;
        public final ForgeConfigSpec.IntValue fanchenNanduStopDurationSeconds;
        public final ForgeConfigSpec.IntValue bushiShiwuStopDurationSeconds;

        // 空梦系列·人物线（tcc_3rd）：钓鱼/战利品
        public final ForgeConfigSpec.DoubleValue padoPhilipisSpecialFishChance;
        public final ForgeConfigSpec.DoubleValue luejiZhiShouSpecialFishChance;
        public final ForgeConfigSpec.DoubleValue kongmengSpecialFishChance;

        // 空梦系列·神之键线（tcc_tdk）：双倍伤害
        public final ForgeConfigSpec.DoubleValue wangshiDeHuanmengDamageMultiplier;
        public final ForgeConfigSpec.DoubleValue laZhiYanDamageMultiplier;
        public final ForgeConfigSpec.DoubleValue yeZhiTongDamageMultiplier;

        // 饰品互斥配置
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> curioConflicts;

        // 虚数崩解配置
        public final ForgeConfigSpec.DoubleValue collapsePercentPerLevel;
        public final ForgeConfigSpec.DoubleValue collapsePercentPerDebuff;
        public final ForgeConfigSpec.IntValue collapseMaxDebuffCount;

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

        public final ForgeConfigSpec.DoubleValue fusionGrowthCoefficient;
        public final ForgeConfigSpec.IntValue fusionEbcCommon;
        public final ForgeConfigSpec.IntValue fusionEbcUncommon;
        public final ForgeConfigSpec.IntValue fusionEbcRare;
        public final ForgeConfigSpec.IntValue fusionEbcEpic;
        public final ForgeConfigSpec.IntValue fusionMaxLevelCommon;
        public final ForgeConfigSpec.IntValue fusionMaxLevelUncommon;
        public final ForgeConfigSpec.IntValue fusionMaxLevelRare;
        public final ForgeConfigSpec.IntValue fusionMaxLevelEpic;
        public final ForgeConfigSpec.IntValue fusionVesselCapacity;

        // 融合容器战利品配置
        public final ForgeConfigSpec.IntValue fusionVesselNetherMin;
        public final ForgeConfigSpec.IntValue fusionVesselNetherMax;
        public final ForgeConfigSpec.IntValue fusionVesselEndMin;
        public final ForgeConfigSpec.IntValue fusionVesselEndMax;

        // 融合容器战利品出现几率
        public final ForgeConfigSpec.DoubleValue fusionVesselNetherChance;
        public final ForgeConfigSpec.DoubleValue fusionVesselEndChance;

        
        public Common(ForgeConfigSpec.Builder builder) {
            builder.comment("TACZ Curios 饰品配置").push("tcc_curios");
            
            // 天火圣裁配置
            builder.comment("天火圣裁饰品配置").push("heaven_fire_judgment");
            heavenFireJudgmentDamageBoost = builder
                    .comment("通用枪械伤害加成 (默认: 0.5 = 50%)")
                    .defineInRange("damageBoost", 0.5, -1, 100);
            heavenFireJudgmentHealthCost = builder
                    .comment("触发时扣除的当前生命值比例 (默认: -0.3 = -30%)")
                    .defineInRange("healthCost", -0.3, -1, 1);
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
            apocalypseImaginaryInfectionMaxLevel = builder
                    .comment("天火劫灭的虚数侵染上限 (默认: 6)")
                    .defineInRange("apocalypseMaxLevel", 6, 1, 99);
            endlessImaginaryInfectionMaxLevel = builder
                    .comment("劫灭无尽的虚数侵染上限 (默认: 9)")
                    .defineInRange("endlessMaxLevel", 9, 1, 99);
            shijieFanyanImaginaryInfectionMaxLevel = builder
                    .comment("视界反演的虚数侵染上限 (默认: 9)")
                    .defineInRange("shijieFanyanMaxLevel", 9, 1, 99);
            xukongWancangYZTHImaginaryInfectionMaxLevel = builder
                    .comment("雨众天华的虚数侵染上限 (默认: 9)")
                    .defineInRange("xukongWancangYZTHMaxLevel", 9, 1, 99);
            metaMorphImaginaryInfectionMaxLevel = builder
                    .comment("Meta-Morph的虚数侵染上限 (默认: 9)")
                    .defineInRange("metaMorphMaxLevel", 9, 1, 99);
            yinguoZhuanlunImaginaryInfectionMaxLevel = builder
                    .comment("因果转轮的虚数侵染上限 (默认: 9)")
                    .defineInRange("yinguoZhuanlunMaxLevel", 9, 1, 99);
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
            judgementKeyCritChance = builder
                    .comment("暴击几率加成 (默认: 1.0 = +100%)")
                    .defineInRange("critChance", 1.0, -1, 100);
            judgementKeyCritDamage = builder
                    .comment("暴击伤害加成 (默认: 1.5 = +150%)")
                    .defineInRange("critDamage", 1.5, -1, 100);
            builder.pop();

            // 涤罪七雷配置
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

            // 雷鸣见配置
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
            
            // 天火劫灭配置
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
            argonScopeBaseCritChance = builder.comment("基础暴击几率 (基础值: 0.15)").defineInRange("baseCritChance", 0.15, -1, 100);
            argonScopeDuration = builder.comment("Buff持续时间(秒) (基础值: 9)").defineInRange("duration", 9, 1, 300);
            builder.pop();
            
            // R-04 镀层氩晶瞄具
            builder.comment("镀层氩晶瞄具饰品配置").push("gilded_argon_scope");
            gildedArgonScopeBaseCritChance = builder.comment("基础暴击几率 (基础值: 0.11)").defineInRange("baseCritChance", 0.11, -1, 100);
            gildedArgonScopeCritChancePerLevel = builder.comment("叠层Buff每级暴击几率 (基础值: 0.033, 满级: +40%/级)").defineInRange("critChancePerLevel", 0.033, -1, 100);
            gildedArgonScopeHeadshotKillExtra = builder.comment("爆头击杀暴击率额外加成 (基础值: 0.4)").defineInRange("headshotKillExtra", 0.4, -1, 100);
            gildedArgonScopeDuration = builder.comment("Buff持续时间(秒) (基础值: 12)").defineInRange("duration", 12, 1, 300);
            gildedArgonScopeMaxStacks = builder.comment("最大buff等级 (基础值: 60)").defineInRange("maxStacks", 60, 1, 200);
            builder.pop();
            
            // R-05 尖刃弹头
            builder.comment("尖刃弹头饰品配置").push("sharp_bullet");
            sharpBulletBaseCritDamage = builder.comment("基础暴击伤害 (基础值: 0.13)").defineInRange("baseCritDamage", 0.13, -1, 100);
            sharpBulletDuration = builder.comment("Buff持续时间(秒) (默认: 9)").defineInRange("duration", 9, 1, 300);
            builder.pop();
            
            // R-06 镀层步枪才能
            builder.comment("镀层步枪才能饰品配置").push("gilded_rifle_aptitude");
            gildedRifleAptitudePerHarmful = builder.comment("每负面效果种数增伤比例 (默认: 0.4)").defineInRange("perHarmful", 0.4, -1, 100);
            builder.pop();
            
            // R-07 镀层分裂膛室
            builder.comment("镀层分裂膛室饰品配置").push("gilded_split_chamber");
            gildedSplitChamberBulletCountBase = builder.comment("基础弹头数量加成 (基础值: 0.8)").defineInRange("bulletCountBase", 0.8, -1, 100);
            gildedSplitChamberBulletCountPerLevel = builder.comment("叠层Buff每级额外弹头数量 (基础值: 0.025, 满级: +30%/级)").defineInRange("bulletCountPerLevel", 0.025, -1, 100);
            gildedSplitChamberDuration = builder.comment("Buff持续时间(秒) (基础值: 20)").defineInRange("duration", 20, 1, 300);
            gildedSplitChamberMaxStacks = builder.comment("最大buff等级 (基础值: 60)").defineInRange("maxStacks", 60, 1, 200);
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
            laserScopeBaseCritChance = builder.comment("基础暴击几率 (基础值: 0.13)").defineInRange("baseCritChance", 0.13, -1, 100);
            laserScopeDuration = builder.comment("Buff持续时间(秒) (默认: 9)").defineInRange("duration", 9, 1, 300);
            builder.pop();
            
            // S-06 破片射击
            builder.comment("破片射击饰品配置").push("fragment_shot");
            fragmentShotBaseCritDamage = builder.comment("基础暴击伤害 (基础值: 0.11)").defineInRange("baseCritDamage", 0.11, -1, 100);
            fragmentShotDuration = builder.comment("Buff持续时间(秒) (默认: 9)").defineInRange("duration", 9, 1, 300);
            builder.pop();
            
            // S-07 镀层通晓霰弹枪
            builder.comment("镀层通晓霰弹枪饰品配置").push("gilded_shotgun_savvy");
            gildedShotgunSavvyPerHarmful = builder.comment("每负面效果种数增伤比例 (默认: 0.4)").defineInRange("perHarmful", 0.4, -1, 100);
            builder.pop();
            
            // S-08 镀层地狱弹膛
            builder.comment("镀层地狱弹膛饰品配置").push("gilded_infernal_chamber");
            gildedInfernalChamberBulletCountBase = builder.comment("基础弹头数量加成 (基础值: 1.1)").defineInRange("bulletCountBase", 1.1, -1, 100);
            gildedInfernalChamberBulletCountPerLevel = builder.comment("叠层Buff每级额外弹头数量 (基础值: 0.025, 满级: +30%/级)").defineInRange("bulletCountPerLevel", 0.025, -1, 100);
            gildedInfernalChamberDuration = builder.comment("Buff持续时间(秒) (基础值: 20)").defineInRange("duration", 20, 1, 300);
            gildedInfernalChamberMaxStacks = builder.comment("最大buff等级 (基础值: 60)").defineInRange("maxStacks", 60, 1, 200);
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
            hydraulicCrosshairBaseCritChance = builder.comment("基础暴击几率 (基础值: 0.15)").defineInRange("baseCritChance", 0.15, -1, 100);
            hydraulicCrosshairDuration = builder.comment("Buff持续时间(秒) (默认: 9)").defineInRange("duration", 9, 1, 300);
            builder.pop();
            
            // P-07 镀层液压准心
            builder.comment("镀层液压准心饰品配置").push("gilded_hydraulic_crosshair");
            gildedHydraulicCrosshairBaseCritChance = builder.comment("基础暴击几率 (基础值: 0.11)").defineInRange("baseCritChance", 0.11, -1, 100);
            gildedHydraulicCrosshairCritChancePerLevel = builder.comment("叠层Buff每级暴击几率 (基础值: 0.033, 满级: +40%/级)").defineInRange("critChancePerLevel", 0.033, -1, 100);
            gildedHydraulicCrosshairHeadshotKillExtra = builder.comment("爆头击杀暴击率额外加成 (基础值: 0.4)").defineInRange("headshotKillExtra", 0.4, -1, 100);
            gildedHydraulicCrosshairDuration = builder.comment("Buff持续时间(秒) (基础值: 12)").defineInRange("duration", 12, 1, 300);
            gildedHydraulicCrosshairMaxStacks = builder.comment("最大buff等级 (基础值: 60)").defineInRange("maxStacks", 60, 1, 200);
            builder.pop();
            
            // P-08 尖锐子弹
            builder.comment("尖锐子弹饰品配置").push("sharp_ammo");
            sharpAmmoBaseCritDamage = builder.comment("基础暴击伤害 (基础值: 0.08)").defineInRange("baseCritDamage", 0.08, -1, 100);
            sharpAmmoDuration = builder.comment("Buff持续时间(秒) (默认: 9)").defineInRange("duration", 9, 1, 300);
            builder.pop();
            
            // P-09 镀层准确射手
            builder.comment("镀层准确射手饰品配置").push("gilded_marksman");
            gildedMarksmanPerHarmful = builder.comment("每负面效果种数增伤比例 (默认: 0.4)").defineInRange("perHarmful", 0.4, -1, 100);
            builder.pop();
            
            // P-10 镀层弹头扩散
            builder.comment("镀层弹头扩散饰品配置").push("gilded_bullet_spread");
            gildedBulletSpreadBulletCountBase = builder.comment("基础弹头数量加成 (基础值: 1.1)").defineInRange("bulletCountBase", 1.1, -1, 100);
            gildedBulletSpreadBulletCountPerLevel = builder.comment("叠层Buff每级额外弹头数量 (基础值: 0.025, 满级: +30%/级)").defineInRange("bulletCountPerLevel", 0.025, -1, 100);
            gildedBulletSpreadDuration = builder.comment("Buff持续时间(秒) (基础值: 20)").defineInRange("duration", 20, 1, 300);
            gildedBulletSpreadMaxStacks = builder.comment("最大buff等级 (基础值: 48)").defineInRange("maxStacks", 48, 1, 200);
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
            gildedSteelSlashCritChanceBase = builder.comment("基础暴击几率 (基础值: 1.1)").defineInRange("critChanceBase", 1.1, -1, 100);
            gildedSteelSlashCritDamagePerLevel = builder.comment("叠层Buff每级暴击伤害 (基础值: 0.025, 满级: +30%/级)").defineInRange("critDamagePerLevel", 0.025, -1, 100);
            gildedSteelSlashDuration = builder.comment("Buff持续时间(秒) (基础值: 20)").defineInRange("duration", 20, 1, 300);
            gildedSteelSlashMaxStacks = builder.comment("最大buff等级 (基础值: 48)").defineInRange("maxStacks", 48, 1, 200);
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
            xioraArmorMultiplier = builder
                    .comment("护甲乘数 (默认: -0.2 = 护甲降低20%)")
                    .defineInRange("armorMultiplier", -0.2, -1, 100);
            xioraSpeedMultiplier = builder
                    .comment("移速乘数 (默认: 0.5 = +50%)")
                    .defineInRange("speedMultiplier", 0.5, -1, 100);
            builder.pop();

            // 夜袭渡鸦配置
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

            // 岛爆渡鸦配置
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
            
            // 夏日沙滩配置
            builder.comment("夏日沙滩饰品配置").push("summer_beach");
            builder.pop();
            
            // 救世配置
            builder.comment("救世饰品配置").push("salvation");
            salvationDamageReduction = builder
                    .comment("救世伤害减免比例 (默认: 0.5 = 减免50%)")
                    .defineInRange("damageReduction", 0.5, 0, 1);
            salvationResistanceLevel = builder
                    .comment("救世抗性提升等级 (默认: 2 = 抗性III)")
                    .defineInRange("resistanceLevel", 2, 0, 10);
            builder.pop();
            
            // 无烬终焉配置
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
                    .comment("装填时间加成 (默认: -0.9 = -90%)")
                    .defineInRange("reloadSpeedBoost", -0.9, -1, 100);
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
            
            // ==== 吸收饰品通用配置 ====
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

            // 格蕾修配置
            builder.comment("格蕾修饰品配置").push("griseo");
            griseoHurtCooldownTicks = builder
                    .comment("受伤冷却基础冷却(tick) (默认: 10 = 0.5秒)")
                    .defineInRange("hurtCooldownTicks", 10, 1, 12000);
            builder.pop();

            // 千界一乘配置
            builder.comment("千界一乘饰品配置").push("qianjie_yicheng");
            qianjieYichengLuck = builder
                    .comment("幸运值加成 (默认: 20)")
                    .defineInRange("luck", 20, 0, 1000);
            builder.pop();

            // 绘世之卷配置
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

            // 永劫之键配置
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

            // 繁星配置
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

            // 视界反演配置
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

            // 维尔薇配置
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

            // 虚空万藏配置
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

            // 愚戏之匣配置
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

            // 启示之键配置
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

            // 螺旋配置
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

            // 虚空万藏·雨众天华配置
            builder.comment("虚空万藏·雨众天华饰品配置").push("xukong_wancang_yzth");
            xukongWancangYZTHImaginaryDamage = builder
                    .comment("基础额外虚数伤害 (默认: 20.0)")
                    .defineInRange("imaginaryDamage", 20.0, 0, 10000);
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

            // 适应系统通用配置
            builder.comment("适应系统通用配置").push("adaptation");
            adaptationMaxCount = builder
                    .comment("同类型伤害适应最大叠加次数 (默认: 4，范围: 1~1000)")
                    .defineInRange("maxCount", 4, 1, 1000);
            builder.pop();

            // 千劫配置
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

            // 伊默尔配置
            builder.comment("伊默尔饰品配置").push("imer");
            imerAttackDamageBonus = builder
                    .comment("攻击伤害加成 (默认: 0.1 = 10%，MULTIPLY_BASE)")
                    .defineInRange("attackDamageBonus", 0.1, 0.0, 10.0);
            builder.pop();

            // 坏劫之焱配置
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

            // 支配之键配置
            builder.comment("支配之键饰品配置").push("dominance_key");
            dominanceKeyHealthToAttackPercent = builder
                    .comment("最大生命值转攻击力比例 (默认: 0.10 = 10%，攻击加成 = 最大生命值 * 此值 / 100)")
                    .defineInRange("healthToAttackPercent", 0.10, 0.0, 10.0);
            dominanceKeyImaginaryDamageScale = builder
                    .comment("攻击力转虚数伤害比例 (默认: 0.3 = 30%)")
                    .defineInRange("imaginaryDamageScale", 0.3, 0.0, 10.0);
            builder.pop();

            // 鏖灭配置
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

            // Meta-Morph配置
            builder.comment("Meta-Morph饰品配置").push("meta_morph");
            metaMorphHealthToAttackPercent = builder
                    .comment("最大生命值转攻击力比例 (默认: 0.10 = 10%)")
                    .defineInRange("healthToAttackPercent", 0.10, 0.0, 10.0);
            metaMorphResistanceToAttackPercent = builder
                    .comment("每点虚数抗性转化为攻击力的比例 (默认: 0.10 = 10%)")
                    .defineInRange("resistanceToAttackPercent", 0.10, 0.0, 10.0);
            metaMorphLifeStealPerResistance = builder
                    .comment("每点虚数抗性提供的生命偷取 (默认: 0.01)")
                    .defineInRange("lifeStealPerResistance", 0.01, 0.0, 1.0);
            builder.pop();

            // 苏配置
            builder.comment("苏饰品配置").push("su");
            suMaxHealthReduction = builder
                    .comment("最大生命值减少比例 (默认: -0.3)")
                    .defineInRange("maxHealthReduction", -0.3, -1.0, 0.0);
            suDamageTakenFactor = builder
                    .comment("受到伤害降低比例 (默认: 0.1 = 降低10%)")
                    .defineInRange("damageTakenFactor", 0.1, 0.0, 1.0);
            builder.pop();

            // 万物休眠配置
            builder.comment("万物休眠饰品配置").push("wanwu_xiumian");
            wanwuXiumianOverheal = builder
                    .comment("超量治疗值 (默认: 0.3)")
                    .defineInRange("overheal", 0.3, 0.0, 10.0);
            wanwuXiumianAmmoRegenPercent = builder
                    .comment("每秒弹药恢复百分比 (默认: 0.05 = 5%)")
                    .defineInRange("ammoRegenPercent", 0.05, 0.0, 1.0);
            builder.pop();

            // 觉者配置
            builder.comment("觉者饰品配置").push("juezhe");
            juezheMaxHealthReduction = builder
                    .comment("最大生命值减少比例 (默认: -0.4)")
                    .defineInRange("maxHealthReduction", -0.4, -1.0, 0.0);
            juezheDamageTakenFactor = builder
                    .comment("受到伤害降低比例 (默认: 0.3 = 降低30%)")
                    .defineInRange("damageTakenFactor", 0.3, 0.0, 1.0);
            builder.pop();

            // 停滞之键配置
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

            // 天慧配置
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

            // 因果转轮配置
            builder.comment("因果转轮饰品配置").push("yinguo_zhuanlun");
            yinguoZhuanlunOverheal = builder
                    .comment("超量治疗值 (默认: 1.0)")
                    .defineInRange("overheal", 1.0, 0.0, 10.0);
            yinguoZhuanlunAmmoResistanceScale = builder
                    .comment("每点虚数抗性提供的弹药恢复系数 (默认: 0.01)")
                    .defineInRange("ammoResistanceScale", 0.01, 0.0, 1.0);
            builder.pop();

            // 逐火之蛾「真我」配置
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
                    .comment("结界影响范围（格） (默认: 128)")
                    .defineInRange("barrierRadius", 128.0, 1.0, 512.0);
            zhenWoSlownessAmplifier = builder
                    .comment("缓慢效果等级（0=缓慢I，默认: 8 = 缓慢IX）")
                    .defineInRange("slownessAmplifier", 8, 0, 255);
            zhenWoSlownessDurationSeconds = builder
                    .comment("缓慢效果持续时间（秒） (默认: 60)")
                    .defineInRange("slownessDurationSeconds", 60, 1, 600);
            zhenWoBarrierDurationSeconds = builder
                    .comment("结界持续时间（秒） (默认: 30)")
                    .defineInRange("barrierDurationSeconds", 30, 1, 600);
            zhenWoDamagePercent = builder
                    .comment("结界每秒对范围内实体造成佩戴者最大血量的比例 (默认: 1.0 = 100%)")
                    .defineInRange("damagePercent", 1.0, 0.0, 100.0);
            zhenWoCooldownSeconds = builder
                    .comment("结界触发后的冷却时间（秒） (默认: 60)")
                    .defineInRange("cooldownSeconds", 60, 1, 3600);
            builder.pop();

            // 黑渊白花·创灭螺旋配置
            builder.comment("黑渊白花·创灭螺旋饰品配置").push("heiyuan_baihua");
            heiyuanBaihuaDamagePercent = builder
                    .comment("每次造成伤害时附加佩戴者当前血量的比例 (默认: 1.0 = 100%)")
                    .defineInRange("damagePercent", 1.0, 0.0, 100.0);
            builder.pop();

            // ========== 新系列饰品配置 ==========

            // 戒律系列·人物线：随机 debuff
            builder.comment("阿波尼亚饰品配置").push("aponia");
            aponiaDebuffChance = builder
                    .comment("施加随机 debuff 概率 (默认: 0.15 = 15%)")
                    .defineInRange("debuffChance", 0.15, 0.0, 1.0);
            aponiaDebuffDurationSeconds = builder
                    .comment("debuff 时长（秒） (默认: 15)")
                    .defineInRange("debuffDurationSeconds", 15, 1, 3600);
            aponiaDebuffCount = builder
                    .comment("施加 debuff 数量 (默认: 1)")
                    .defineInRange("debuffCount", 1, 1, 100);
            builder.pop();

            builder.comment("深罪之槛饰品配置").push("shenzui_zhijian");
            shenzuiZhijianDebuffChance = builder
                    .comment("施加随机 debuff 概率 (默认: 0.15 = 15%)")
                    .defineInRange("debuffChance", 0.15, 0.0, 1.0);
            shenzuiZhijianDebuffDurationSeconds = builder
                    .comment("debuff 时长（秒） (默认: 15)")
                    .defineInRange("debuffDurationSeconds", 15, 1, 3600);
            shenzuiZhijianDebuffCount = builder
                    .comment("施加 debuff 数量 (默认: 2)")
                    .defineInRange("debuffCount", 2, 1, 100);
            builder.pop();

            builder.comment("戒律饰品配置").push("jielv");
            jielvDebuffChance = builder
                    .comment("施加随机 debuff 概率 (默认: 0.15 = 15%)")
                    .defineInRange("debuffChance", 0.15, 0.0, 1.0);
            jielvDebuffDurationSeconds = builder
                    .comment("debuff 时长（秒） (默认: 15)")
                    .defineInRange("debuffDurationSeconds", 15, 1, 3600);
            jielvDebuffCount = builder
                    .comment("施加 debuff 数量 (默认: 3)")
                    .defineInRange("debuffCount", 3, 1, 100);
            builder.pop();

            // 戒律系列·神之键线：崩坏病（犹大的誓约 / 约束之键 尚未实现，预留配置）
            builder.comment("犹大的誓约饰品配置（未实现，预留）").push("yuda_de_shiyue");
            yudaDeShiyueDiseaseDurationSeconds = builder
                    .comment("崩坏病时长（秒） (默认: 15)")
                    .defineInRange("diseaseDurationSeconds", 15, 1, 3600);
            yudaDeShiyueDiseaseAmplifier = builder
                    .comment("崩坏病等级（0=易伤20%，默认: 0 = I 级）")
                    .defineInRange("diseaseAmplifier", 0, 0, 255);
            builder.pop();

            builder.comment("约束之键饰品配置（未实现，预留）").push("yueshu_zhi_jian");
            yueshuZhiJianDiseaseDurationSeconds = builder
                    .comment("崩坏病时长（秒） (默认: 15)")
                    .defineInRange("diseaseDurationSeconds", 15, 1, 3600);
            yueshuZhiJianDiseaseAmplifier = builder
                    .comment("崩坏病等级（0=易伤20%，默认: 1 = II 级）")
                    .defineInRange("diseaseAmplifier", 1, 0, 255);
            builder.pop();

            builder.comment("第零额定功率·神恩结界饰品配置").push("shenen_jiejie");
            shenenJiejieDiseaseDurationSeconds = builder
                    .comment("崩坏病时长（秒） (默认: 15)")
                    .defineInRange("diseaseDurationSeconds", 15, 1, 3600);
            shenenJiejieDiseaseAmplifier = builder
                    .comment("崩坏病等级（0=易伤20%，默认: 2 = III 级）")
                    .defineInRange("diseaseAmplifier", 2, 0, 255);
            builder.pop();

            // 黄金系列·人物线：友方光环
            builder.comment("伊甸饰品配置").push("eden");
            edenAuraRange = builder
                    .comment("光环范围（格） (默认: 36)")
                    .defineInRange("auraRange", 36.0, 1.0, 512.0);
            edenIntervalSeconds = builder
                    .comment("buff 施加间隔（秒） (默认: 5)")
                    .defineInRange("intervalSeconds", 5, 1, 3600);
            edenBuffDurationSeconds = builder
                    .comment("buff 持续时长（秒） (默认: 30)")
                    .defineInRange("buffDurationSeconds", 30, 1, 3600);
            edenBuffAmplifier = builder
                    .comment("buff 等级（0=I 级，默认: 0）")
                    .defineInRange("buffAmplifier", 0, 0, 255);
            builder.pop();

            builder.comment("璀耀之歌饰品配置").push("cuiyao_zhi_ge");
            cuiyaoZhiGeAuraRange = builder
                    .comment("光环范围（格） (默认: 36)")
                    .defineInRange("auraRange", 36.0, 1.0, 512.0);
            cuiyaoZhiGeIntervalSeconds = builder
                    .comment("buff 施加间隔（秒） (默认: 5)")
                    .defineInRange("intervalSeconds", 5, 1, 3600);
            cuiyaoZhiGeBuffDurationSeconds = builder
                    .comment("buff 持续时长（秒） (默认: 30)")
                    .defineInRange("buffDurationSeconds", 30, 1, 3600);
            cuiyaoZhiGeBuffAmplifier = builder
                    .comment("buff 等级（0=I 级，默认: 1 = II 级）")
                    .defineInRange("buffAmplifier", 1, 0, 255);
            builder.pop();

            builder.comment("黄金饰品配置").push("huangjin");
            huangjinAuraRange = builder
                    .comment("光环范围（格） (默认: 36)")
                    .defineInRange("auraRange", 36.0, 1.0, 512.0);
            huangjinIntervalSeconds = builder
                    .comment("buff 施加间隔（秒） (默认: 5)")
                    .defineInRange("intervalSeconds", 5, 1, 3600);
            huangjinBuffDurationSeconds = builder
                    .comment("buff 持续时长（秒） (默认: 30)")
                    .defineInRange("buffDurationSeconds", 30, 1, 3600);
            huangjinBuffAmplifier = builder
                    .comment("buff 等级（0=I 级，默认: 2 = III 级）")
                    .defineInRange("buffAmplifier", 2, 0, 255);
            builder.pop();

            // 黄金系列·神之键线：瞬移失效
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

            // 旭光系列·人物线：攻速攻伤
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

            // 旭光系列·神之键线：削甲
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

            // 无限系列·人物线：击杀累计
            builder.comment("梅比乌斯饰品配置").push("mebius");
            mebiusPerTypeBonus = builder
                    .comment("每击杀一种实体类型的全属性加成（小数，默认: 0.01 = +1%）")
                    .defineInRange("perTypeBonus", 0.01, 0.0, 100.0);
            builder.pop();

            builder.comment("噬界之蛇饰品配置").push("shijie_zhi_she");
            shijieZhiShePerTypeBonus = builder
                    .comment("每击杀一种实体类型的全属性加成（小数，默认: 0.015 = +1.5%）")
                    .defineInRange("perTypeBonus", 0.015, 0.0, 100.0);
            builder.pop();

            builder.comment("无限饰品配置").push("wuxian");
            wuxianPerTypeBonus = builder
                    .comment("每击杀一种实体类型的全属性加成（小数，默认: 0.02 = +2%）")
                    .defineInRange("perTypeBonus", 0.02, 0.0, 100.0);
            builder.pop();

            // 无限系列·神之键线：移除正面 buff
            builder.comment("往世的蛇影饰品配置").push("wangshi_de_sheying");
            wangshiDeSheyingRemoveChance = builder
                    .comment("造成伤害移除目标正面 buff 概率 (默认: 0.10 = 10%)")
                    .defineInRange("removeChance", 0.10, 0.0, 1.0);
            builder.pop();

            builder.comment("往世的蛇影·死之衣饰品配置").push("si_zhi_yi");
            siZhiYiRemoveChance = builder
                    .comment("造成伤害移除目标正面 buff 概率 (默认: 0.20 = 20%)")
                    .defineInRange("removeChance", 0.20, 0.0, 1.0);
            builder.pop();

            // 浮生系列·人物线：百分比护甲/韧性
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

            // 浮生系列·神之键线：停止 AI
            builder.comment("羽渡尘饰品配置").push("yuduchen");
            yuduchenStopChance = builder
                    .comment("攻击停止目标 AI 概率 (默认: 0.05 = 5%)")
                    .defineInRange("stopChance", 0.05, 0.0, 1.0);
            yuduchenStopDurationSeconds = builder
                    .comment("停止 AI 时长（秒） (默认: 5)")
                    .defineInRange("stopDurationSeconds", 5, 1, 3600);
            builder.pop();

            builder.comment("凡尘难渡饰品配置").push("fanchen_nandu");
            fanchenNanduStopChance = builder
                    .comment("攻击停止目标 AI 概率 (默认: 0.15 = 15%)")
                    .defineInRange("stopChance", 0.15, 0.0, 1.0);
            fanchenNanduStopDurationSeconds = builder
                    .comment("停止 AI 时长（秒） (默认: 5)")
                    .defineInRange("stopDurationSeconds", 5, 1, 3600);
            builder.pop();

            builder.comment("不识时务饰品配置").push("bushi_shiwu");
            bushiShiwuStopDurationSeconds = builder
                    .comment("停止 AI 时长（秒） (默认: 5)")
                    .defineInRange("stopDurationSeconds", 5, 1, 3600);
            builder.pop();

            // 空梦系列·人物线：钓鱼/战利品
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

            // 空梦系列·神之键线：双倍伤害
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

            // ==== 融合升级配置 ====
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

            // ==== 融合容器战利品配置 ====
            builder.comment("融合容器战利品生成配置").push("fusion_vessel_loot");
            fusionVesselNetherMin = builder
                    .comment("下界要塞/堡垒遗迹中融合容器包含的内融核心最小数量（默认: 200）")
                    .defineInRange("netherMin", 200, 1, Integer.MAX_VALUE);
            fusionVesselNetherMax = builder
                    .comment("下界要塞/堡垒遗迹中融合容器包含的内融核心最大数量（默认: 1000）")
                    .defineInRange("netherMax", 1000, 1, Integer.MAX_VALUE);
            fusionVesselEndMin = builder
                    .comment("末地城中融合容器包含的内融核心最小数量（默认: 3000）")
                    .defineInRange("endMin", 3000, 1, Integer.MAX_VALUE);
            fusionVesselEndMax = builder
                    .comment("末地城中融合容器包含的内融核心最大数量（默认: 10000）")
                    .defineInRange("endMax", 10000, 1, Integer.MAX_VALUE);
            fusionVesselNetherChance = builder
                    .comment("下界要塞/堡垒遗迹中出现融合容器的几率（0~1，默认: 0.01 = 1%）")
                    .defineInRange("netherChance", 0.01, 0.0, 1.0);
            fusionVesselEndChance = builder
                    .comment("末地城战利品箱中出现融合容器的几率（0~1，默认: 0.005 = 0.5%）")
                    .defineInRange("endChance", 0.005, 0.0, 1.0);
            builder.pop();
        }
    }
    
    public static void registerConfigs() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, COMMON_SPEC);
    }
}
