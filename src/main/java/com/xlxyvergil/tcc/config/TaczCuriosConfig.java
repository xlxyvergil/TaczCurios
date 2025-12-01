package com.xlxyvergil.tcc.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

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
        public final ForgeConfigSpec.DoubleValue heavenFireJudgmentHealthDrain;
        public final ForgeConfigSpec.IntValue heavenFireJudgmentDrainDuration;
        
        // 天火劫灭配置
        public final ForgeConfigSpec.DoubleValue heavenFireApocalypseDamageBoost;
        public final ForgeConfigSpec.DoubleValue heavenFireApocalypseExplosionRadius;
        public final ForgeConfigSpec.DoubleValue heavenFireApocalypseExplosionDamage;
        public final ForgeConfigSpec.DoubleValue heavenFireApocalypseHealthCost;
        public final ForgeConfigSpec.DoubleValue heavenFireApocalypseNearbyPlayerDamageBoost;
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
        
        // 烈焰风暴配置
        public final ForgeConfigSpec.DoubleValue blazeStormExplosionRadiusBoost;
        public final ForgeConfigSpec.DoubleValue blazeStormExplosionDamageBoost;
        
        // 烈焰风暴Prime配置
        public final ForgeConfigSpec.DoubleValue blazeStormPrimeExplosionRadiusBoost;
        public final ForgeConfigSpec.DoubleValue blazeStormPrimeExplosionDamageBoost;
        
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
        
        // 弹匣增幅配置（新）
        public final ForgeConfigSpec.DoubleValue magazineBoostCapacityBoost;
        
        // 串联弹匣配置
        public final ForgeConfigSpec.DoubleValue tandemMagazineCapacityBoost;
        
        public Common(ForgeConfigSpec.Builder builder) {
            builder.comment("TACZ Curios 饰品配置").push("tcc_curios");
            
            // 天火圣裁配置
            builder.comment("天火圣裁饰品配置").push("heaven_fire_judgment");
            heavenFireJudgmentDamageBoost = builder
                    .comment("通用枪械伤害加成 (默认: 3.25 = 325%)")
                    .defineInRange("damageBoost", 3.25, 0, 100);
            heavenFireJudgmentHealthCost = builder
                    .comment("触发时扣除的当前生命值比例 (默认: 0.3 = 30%)")
                    .defineInRange("healthCost", 0.3, 0, 1);
            heavenFireJudgmentHealthDrain = builder
                    .comment("每秒消耗的最大生命值比例 (默认: 0.05 = 5%)")
                    .defineInRange("healthDrain", 0.05, 0, 1);
            heavenFireJudgmentDrainDuration = builder
                    .comment("生命值消耗持续时间(秒) (默认: 5)")
                    .defineInRange("drainDuration", 5, 0, 300);
            builder.pop();
            
            // 天火劫灭配置
            builder.comment("天火劫灭饰品配置").push("heaven_fire_apocalypse");
            heavenFireApocalypseDamageBoost = builder
                    .comment("通用枪械伤害加成 (默认: 10.0 = 1000%)")
                    .defineInRange("damageBoost", 10.0, 0, 1000);
            heavenFireApocalypseExplosionRadius = builder
                    .comment("爆炸范围加成 (默认: 10)")
                    .defineInRange("explosionRadius", 10.0, 0, 100);
            heavenFireApocalypseExplosionDamage = builder
                    .comment("爆炸伤害加成 (默认: 10.0 = 1000%)")
                    .defineInRange("explosionDamage", 10.0, 0, 100);
            heavenFireApocalypseHealthCost = builder
                    .comment("触发时扣除的当前生命值比例 (默认: 1.0 = 100%)")
                    .defineInRange("healthCost", 1.0, 0, 1);
            heavenFireApocalypseNearbyPlayerDamageBoost = builder
                    .comment("附近玩家获得的伤害加成 (默认: 1.0 = 100%)")
                    .defineInRange("nearbyPlayerDamageBoost", 1.0, 0, 100);
            heavenFireApocalypseNearbyPlayerDuration = builder
                    .comment("附近玩家获得伤害加成的持续时间(秒) (默认: 15)")
                    .defineInRange("nearbyPlayerDuration", 15, 0, 300);
            heavenFireApocalypseNearbyPlayerRadius = builder
                    .comment("影响附近玩家的范围 (默认: 32)")
                    .defineInRange("nearbyPlayerRadius", 32.0, 0, 100);
            builder.pop();
            
            // 膛线配置
            builder.comment("膛线饰品配置").push("rifling");
            riflingDamageBoost = builder
                    .comment("特定枪械伤害加成 (默认: 1.65 = 165%)")
                    .defineInRange("damageBoost", 1.65, 0, 100);
            builder.pop();
            
            // 分裂膛室配置
            builder.comment("分裂膛室饰品配置").push("split_chamber");
            splitChamberBulletCountBoost = builder
                    .comment("弹头数量加成 (默认: 0.9 = 90%)")
                    .defineInRange("bulletCountBoost", 0.9, 0, 100);
            builder.pop();
            
            // 卑劣加速配置
            builder.comment("卑劣加速饰品配置").push("despicable_acceleration");
            despicableAccelerationFireRateBoost = builder
                    .comment("射击速度加成 (默认: 0.9 = 90%)")
                    .defineInRange("fireRateBoost", 0.9, 0, 100);
            despicableAccelerationDamageReduction = builder
                    .comment("通用伤害降低 (默认: 0.15 = 15%)")
                    .defineInRange("damageReduction", 0.15, 0, 1);
            builder.pop();
            
            // 并合膛线配置
            builder.comment("并合膛线饰品配置").push("merged_rifling");
            mergedRiflingDamageBoost = builder
                    .comment("特定枪械伤害加成 (默认: 1.55 = 155%)")
                    .defineInRange("damageBoost", 1.55, 0, 100);
            mergedRiflingMovementSpeedBoost = builder
                    .comment("持枪移动速度加成 (默认: 0.25 = 25%)")
                    .defineInRange("movementSpeedBoost", 0.25, 0, 100);
            builder.pop();
            
            // 合金钻头配置
            builder.comment("合金钻头饰品配置").push("alloy_drill");
            alloyDrillArmorPenetrationBoost = builder
                    .comment("护甲穿透加成 (默认: 2.0 = 200%)")
                    .defineInRange("armorPenetrationBoost", 2.0, 0, 100);
            builder.pop();
            
            // 我小心海也绝非鳝类配置
            builder.comment("我小心海也绝非鳝类饰品配置").push("careful_heart");
            carefulHeartLauncherDamageBoost = builder
                    .comment("发射器伤害加成 (默认: 3.0 = 300%)")
                    .defineInRange("launcherDamageBoost", 3.0, 0, 100);
            carefulHeartExplosionDamageBoost = builder
                    .comment("爆炸伤害加成 (默认: 3.0 = 300%)")
                    .defineInRange("explosionDamageBoost", 3.0, 0, 100);
            carefulHeartExplosionRadiusBoost = builder
                    .comment("爆炸范围加成 (默认: 3.0 = 300%)")
                    .defineInRange("explosionRadiusBoost", 3.0, 0, 100);
            builder.pop();
            
            // 烈焰风暴配置
            builder.comment("烈焰风暴饰品配置").push("blaze_storm");
            blazeStormExplosionRadiusBoost = builder
                    .comment("爆炸范围加成 (默认: 0.24 = 24%)")
                    .defineInRange("explosionRadiusBoost", 0.24, 0, 100);
            blazeStormExplosionDamageBoost = builder
                    .comment("爆炸伤害加成 (默认: 0.24 = 24%)")
                    .defineInRange("explosionDamageBoost", 0.24, 0, 100);
            builder.pop();
            
            // 烈焰风暴Prime配置
            builder.comment("烈焰风暴Prime饰品配置").push("blaze_storm_prime");
            blazeStormPrimeExplosionRadiusBoost = builder
                    .comment("爆炸范围加成 (默认: 0.66 = 66%)")
                    .defineInRange("explosionRadiusBoost", 0.66, 0, 100);
            blazeStormPrimeExplosionDamageBoost = builder
                    .comment("爆炸伤害加成 (默认: 0.66 = 66%)")
                    .defineInRange("explosionDamageBoost", 0.66, 0, 100);
            builder.pop();
            
            // 撕裂Prime配置
            builder.comment("撕裂Prime饰品配置").push("ripping_prime");
            rippingPrimeFireRateBoost = builder
                    .comment("射速加成 (默认: 0.55 = 55%)")
                    .defineInRange("fireRateBoost", 0.55, 0, 100);
            rippingPrimePenetrationBoost = builder
                    .comment("穿透加成 (默认: 2.2)")
                    .defineInRange("penetrationBoost", 2.2, 0, 100);
            builder.pop();
            
            // 抵近射击Prime配置
            builder.comment("抵近射击Prime饰品配置").push("close_combat_prime");
            closeCombatPrimeShotgunDamageBoost = builder
                    .comment("霰弹枪伤害加成 (默认: 1.65 = 165%)")
                    .defineInRange("shotgunDamageBoost", 1.65, 0, 100);
            builder.pop();
            
            // 极恶精准配置
            builder.comment("极恶精准饰品配置").push("evil_accuracy");
            evilAccuracyRecoilReduction = builder
                    .comment("后坐力降低 (默认: 0.9 = 90%)")
                    .defineInRange("recoilReduction", 0.9, 0, 1);
            evilAccuracyFireRateReduction = builder
                    .comment("射速降低 (默认: 0.36 = 36%)")
                    .defineInRange("fireRateReduction", 0.36, 0, 1);
            builder.pop();
            
            // 极限速度配置
            builder.comment("极限速度饰品配置").push("limit_speed");
            limitSpeedBulletSpeedBoost = builder
                    .comment("弹药速度加成 (默认: 0.6 = 60%)")
                    .defineInRange("bulletSpeedBoost", 0.6, 0, 100);
            builder.pop();
            
            // 凶恶延伸配置
            builder.comment("凶恶延伸饰品配置").push("ferocious_extension");
            ferociousExtensionRangeBoost = builder
                    .comment("子弹射程加成 (默认: 1.2 = 120%)")
                    .defineInRange("rangeBoost", 1.2, 0, 100);
            builder.pop();
            
            // 抵近射击配置
            builder.comment("抵近射击饰品配置").push("close_range_shot");
            closeRangeShotDamageBoost = builder
                    .comment("霰弹枪伤害加成 (默认: 0.9 = 90%)")
                    .defineInRange("damageBoost", 0.9, 0, 100);
            builder.pop();
            
            // 重装火力配置
            builder.comment("重装火力饰品配置").push("heavy_firepower");
            heavyFirepowerDamageBoost = builder
                    .comment("手枪伤害加成 (默认: 1.65 = 165%)")
                    .defineInRange("damageBoost", 1.65, 0, 100);
            heavyFirepowerAccuracyReduction = builder
                    .comment("精准度降低 (默认: 0.55 = 55%)")
                    .defineInRange("accuracyReduction", 0.55, 0, 1);
            builder.pop();
            
            // 黄蜂蜇刺配置
            builder.comment("黄蜂蜇刺饰品配置").push("wasp_stinger");
            waspStingerDamageBoost = builder
                    .comment("手枪伤害加成 (默认: 2.2 = 220%)")
                    .defineInRange("damageBoost", 2.2, 0, 100);
            builder.pop();
            
            // 预言契约配置
            builder.comment("预言契约饰品配置").push("prophecy_pact");
            prophecyPactDamageBoost = builder
                    .comment("手枪伤害加成 (默认: 0.9 = 90%)")
                    .defineInRange("damageBoost", 0.9, 0, 100);
            builder.pop();
            
            // 恶性扩散配置
            builder.comment("恶性扩散饰品配置").push("malignant_spread");
            malignantSpreadDamageBoost = builder
                    .comment("霰弹枪伤害加成 (默认: 1.65 = 165%)")
                    .defineInRange("damageBoost", 1.65, 0, 100);
            malignantSpreadAccuracyReduction = builder
                    .comment("精准度降低 (默认: 0.55 = 55%)")
                    .defineInRange("accuracyReduction", 0.55, 0, 1);
            builder.pop();
            
            // 膛室配置
            builder.comment("膛室饰品配置").push("chamber");
            chamberSniperDamageBoost = builder
                    .comment("狙击枪伤害加成 (默认: 0.4 = 40%)")
                    .defineInRange("sniperDamageBoost", 0.4, 0, 100);
            builder.pop();
            
            // 膛室Prime配置
            builder.comment("膛室Prime饰品配置").push("chamber_prime");
            chamberPrimeSniperDamageBoost = builder
                    .comment("狙击枪伤害加成 (默认: 1.0 = 100%)")
                    .defineInRange("sniperDamageBoost", 1.0, 0, 100);
            builder.pop();
            
            // 战术上膛配置
            builder.comment("战术上膛饰品配置").push("tactical_reload");
            tacticalReloadSpeedBoost = builder
                    .comment("霰弹枪装填速度加成 (默认: 0.6 = 60%)")
                    .defineInRange("reloadSpeedBoost", 0.6, 0, 100);
            builder.pop();
            
            // 过载弹匣配置
            builder.comment("过载弹匣饰品配置").push("overloaded_magazine");
            overloadedMagazineCapacityBoost = builder
                    .comment("霰弹枪弹匣容量加成 (默认: 0.6 = 60%)")
                    .defineInRange("capacityBoost", 0.6, 0, 100);
            overloadedMagazineReloadSpeedReduction = builder
                    .comment("装填速度降低 (默认: 0.18 = 18%)")
                    .defineInRange("reloadSpeedReduction", 0.18, 0, 1);
            builder.pop();
            
            // 地狱弹膛配置
            builder.comment("地狱弹膛饰品配置").push("infernal_chamber");
            infernalChamberBulletCountBoost = builder
                    .comment("霰弹枪弹头数量加成 (默认: 1.2 = 120%)")
                    .defineInRange("bulletCountBoost", 1.2, 0, 100);
            builder.pop();
            
            // 持续火力配置
            builder.comment("持续火力饰品配置").push("sustained_fire");
            sustainedFireReloadSpeedBoost = builder
                    .comment("手枪装填速度加成 (默认: 0.48 = 48%)")
                    .defineInRange("reloadSpeedBoost", 0.48, 0, 100);
            builder.pop();
            
            // 感染弹匣配置
            builder.comment("感染弹匣饰品配置").push("infected_magazine");
            infectedMagazineCapacityBoost = builder
                    .comment("手枪弹匣容量加成 (默认: 0.6 = 60%)")
                    .defineInRange("capacityBoost", 0.6, 0, 100);
            infectedMagazineReloadSpeedReduction = builder
                    .comment("装填速度降低 (默认: 0.3 = 30%)")
                    .defineInRange("reloadSpeedReduction", 0.3, 0, 1);
            builder.pop();
            
            // 致命洪流配置
            builder.comment("致命洪流饰品配置").push("deadly_surge");
            deadlySurgeFireRateBoost = builder
                    .comment("手枪射速加成 (默认: 0.6 = 60%)")
                    .defineInRange("fireRateBoost", 0.6, 0, 100);
            deadlySurgeBulletCountBoost = builder
                    .comment("弹头数量加成 (默认: 0.6 = 60%)")
                    .defineInRange("bulletCountBoost", 0.6, 0, 100);
            builder.pop();
            
            // 弹头扩散配置
            builder.comment("弹头扩散饰品配置").push("bullet_spread");
            bulletSpreadBulletCountBoost = builder
                    .comment("手枪弹头数量加成 (默认: 1.2 = 120%)")
                    .defineInRange("bulletCountBoost", 1.2, 0, 100);
            builder.pop();
            
            // 压迫点配置
            builder.comment("压迫点饰品配置").push("oppression_point");
            oppressionPointMeleeDamageBoost = builder
                    .comment("近战伤害加成 (默认: 1.2 = 120%)")
                    .defineInRange("meleeDamageBoost", 1.2, 0, 100);
            builder.pop();
            
            // 压迫点Prime配置
            builder.comment("压迫点Prime饰品配置").push("oppression_point_prime");
            oppressionPointPrimeMeleeDamageBoost = builder
                    .comment("近战伤害加成 (默认: 1.65 = 165%)")
                    .defineInRange("meleeDamageBoost", 1.65, 0, 100);
            builder.pop();
            
            // 剑风配置
            builder.comment("剑风饰品配置").push("sword_wind");
            swordWindMeleeRangeBoost = builder
                    .comment("近战距离加成 (默认: 1.1)")
                    .defineInRange("meleeRangeBoost", 1.1, 0, 100);
            builder.pop();
            
            // 剑风Prime配置
            builder.comment("剑风Prime饰品配置").push("sword_wind_prime");
            swordWindPrimeMeleeRangeBoost = builder
                    .comment("近战距离加成 (默认: 3)")
                    .defineInRange("meleeRangeBoost", 3.0, 0, 100);
            builder.pop();
            
            // 腐败弹匣配置
            builder.comment("腐败弹匣饰品配置").push("corrupt_magazine");
            corruptMagazineCapacityBoost = builder
                    .comment("弹匣容量加成 (默认: 0.66 = 66%)")
                    .defineInRange("capacityBoost", 0.66, 0, 100);
            corruptMagazineReloadSpeedReduction = builder
                    .comment("装填速度降低 (默认: 0.33 = 33%)")
                    .defineInRange("reloadSpeedReduction", 0.33, 0, 1);
            builder.pop();
            
            // 重口径配置
            builder.comment("重口径饰品配置").push("heavy_caliber_tag");
            heavyCaliberTagDamageBoost = builder
                    .comment("特定枪械伤害加成 (默认: 1.65 = 165%)")
                    .defineInRange("damageBoost", 1.65, 0, 100);
            heavyCaliberTagInaccuracyBoost = builder
                    .comment("不精准度加成 (默认: 0.55 = 55%)")
                    .defineInRange("inaccuracyBoost", 0.55, 0, 100);
            builder.pop();
            
            // 红-有-三配置
            builder.comment("红-有-三饰品配置").push("red_movement_tag");
            redMovementTagSpeedBoost = builder
                    .comment("移动速度加成 (默认: 1.5 = 150%)")
                    .defineInRange("speedBoost", 1.5, 0, 100);
            builder.pop();
            
            // 士兵基础挂牌配置
            builder.comment("士兵基础挂牌饰品配置").push("soldier_basic_tag");
            soldierBasicTagDamageBoost = builder
                    .comment("通用枪械伤害加成 (默认: 0.3 = 30%)")
                    .defineInRange("damageBoost", 0.3, 0, 100);
            builder.pop();
            
            // 弹匣增幅配置
            builder.comment("弹匣增幅饰品配置").push("magazine_boost");
            magazineBoostReloadSpeedBoost = builder
                    .comment("装填速度加成 (默认: 0.3 = 30%)")
                    .defineInRange("reloadSpeedBoost", 0.3, 0, 100);
            builder.pop();
            
            // 士兵特定挂牌配置
            builder.comment("士兵特定挂牌饰品配置").push("soldier_specific_tag");
            soldierSpecificTagDamageBoost = builder
                    .comment("通用枪械伤害加成 (默认: 0.55 = 55%)")
                    .defineInRange("damageBoost", 0.55, 0, 100);
            builder.pop();
            
            // 乌拉尔银狼配置
            builder.comment("乌拉尔银狼饰品配置").push("ural_wolf_tag");
            uralWolfTagHeadshotMultiplierBoost = builder
                    .comment("爆头倍率加成 (默认: 1.5 = 150%)")
                    .defineInRange("headshotMultiplierBoost", 1.5, 0, 100);
            builder.pop();
            
            // 耗竭装填配置
            builder.comment("耗竭装填饰品配置").push("depleted_reload");
            depletedReloadMagazineCapacityPenalty = builder
                    .comment("弹匣容量减少 (默认: -0.6 = -60%)")
                    .defineInRange("magazineCapacityPenalty", -0.6, -1, 0);
            depletedReloadReloadSpeedBoost = builder
                    .comment("装填速度加成 (默认: 0.48 = 48%)")
                    .defineInRange("reloadSpeedBoost", 0.48, 0, 100);
            builder.pop();
            
            // 爆发装填Prime配置
            builder.comment("爆发装填Prime饰品配置").push("burst_reload_prime");
            burstReloadPrimeReloadSpeedBoost = builder
                    .comment("装填速度加成 (默认: 0.55 = 55%)")
                    .defineInRange("reloadSpeedBoost", 0.55, 0, 100);
            builder.pop();
            
            // 战术上膛Prime配置
            builder.comment("战术上膛Prime饰品配置").push("tactical_reload_prime");
            tacticalReloadPrimeReloadSpeedBoost = builder
                    .comment("装填速度加成 (默认: 1.0 = 100%)")
                    .defineInRange("reloadSpeedBoost", 1.0, 0, 100);
            builder.pop();
            
            // 霰弹扩充Prime配置
            builder.comment("霰弹扩充Prime饰品配置").push("shotgun_expansion_prime");
            shotgunExpansionPrimeCapacityBoost = builder
                    .comment("弹匣容量加成 (默认: 1.1 = 110%)")
                    .defineInRange("capacityBoost", 1.1, 0, 100);
            builder.pop();
            
            // 弹匣增幅Prime配置
            builder.comment("弹匣增幅Prime饰品配置").push("magazine_boost_prime");
            magazineBoostPrimeCapacityBoost = builder
                    .comment("弹匣容量加成 (默认: 0.55 = 55%)")
                    .defineInRange("capacityBoost", 0.55, 0, 100);
            builder.pop();
            
            // 串联弹匣Prime配置
            builder.comment("串联弹匣Prime饰品配置").push("tandem_magazine_prime");
            tandemMagazinePrimeCapacityBoost = builder
                    .comment("弹匣容量加成 (默认: 0.55 = 55%)")
                    .defineInRange("capacityBoost", 0.55, 0, 100);
            builder.pop();
            
            // 霰弹扩充配置
            builder.comment("霰弹扩充饰品配置").push("shotgun_expansion");
            shotgunExpansionCapacityBoost = builder
                    .comment("弹匣容量加成 (默认: 0.6 = 60%)")
                    .defineInRange("capacityBoost", 0.6, 0, 100);
            builder.pop();
            
            // 弹匣增幅配置（新）
            builder.comment("弹匣增幅饰品配置（新）").push("magazine_boost_new");
            magazineBoostCapacityBoost = builder
                    .comment("弹匣容量加成 (默认: 0.3 = 30%)")
                    .defineInRange("capacityBoost", 0.3, 0, 100);
            builder.pop();
            
            // 串联弹匣配置
            builder.comment("串联弹匣饰品配置").push("tandem_magazine");
            tandemMagazineCapacityBoost = builder
                    .comment("弹匣容量加成 (默认: 0.3 = 30%)")
                    .defineInRange("capacityBoost", 0.3, 0, 100);
            builder.pop();
            
            builder.pop();
        }
    }
    
    public static void registerConfigs() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, COMMON_SPEC);
    }
}