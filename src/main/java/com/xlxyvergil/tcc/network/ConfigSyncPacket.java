package com.xlxyvergil.tcc.network;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ConfigSyncPacket {
    private static final Gson GSON = new Gson();
    private final String configJson;

    public ConfigSyncPacket(String configJson) {
        this.configJson = configJson;
    }

    public static void encode(ConfigSyncPacket packet, FriendlyByteBuf buf) {
        buf.writeUtf(packet.configJson);
    }

    public static ConfigSyncPacket decode(FriendlyByteBuf buf) {
        return new ConfigSyncPacket(buf.readUtf(32767));
    }

    public static void handle(ConfigSyncPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            if (net.minecraftforge.fml.loading.FMLEnvironment.dist == net.minecraftforge.api.distmarker.Dist.CLIENT) {
                handleOnClient(packet.configJson);
            }
        });
        context.setPacketHandled(true);
    }

    private static void handleOnClient(String configJson) {
        JsonObject config = GSON.fromJson(configJson, JsonObject.class);

        // 天火圣裁配置
            if (config.has("heavenFireJudgmentDamageBoost"))
                TaczCuriosConfig.COMMON.heavenFireJudgmentDamageBoost.set(config.get("heavenFireJudgmentDamageBoost").getAsDouble());
            if (config.has("heavenFireJudgmentHealthCost"))
                TaczCuriosConfig.COMMON.heavenFireJudgmentHealthCost.set(config.get("heavenFireJudgmentHealthCost").getAsDouble());
            if (config.has("heavenFireJudgmentDamageConversionRatio"))
                TaczCuriosConfig.COMMON.heavenFireJudgmentDamageConversionRatio.set(config.get("heavenFireJudgmentDamageConversionRatio").getAsDouble());
            if (config.has("heavenFireJudgmentGunTypes")) {
                java.util.List<String> list = new java.util.ArrayList<>();
                JsonArray arr = config.getAsJsonArray("heavenFireJudgmentGunTypes");
                for (JsonElement e : arr) list.add(e.getAsString());
                TaczCuriosConfig.COMMON.heavenFireJudgmentGunTypes.set(list);
            }
            
            // 天火流血效果配置（两个饰品共用）
            if (config.has("heavenFireBleedingDamagePerLevel"))
                TaczCuriosConfig.COMMON.heavenFireBleedingDamagePerLevel.set(config.get("heavenFireBleedingDamagePerLevel").getAsDouble());
            if (config.has("heavenFireBleedingMaxLevel"))
                TaczCuriosConfig.COMMON.heavenFireBleedingMaxLevel.set(config.get("heavenFireBleedingMaxLevel").getAsInt());
            if (config.has("heavenFireBleedingDuration"))
                TaczCuriosConfig.COMMON.heavenFireBleedingDuration.set(config.get("heavenFireBleedingDuration").getAsInt());
            
            // 虚数流血效果配置
            if (config.has("imaginaryBleedingDamagePerLevel"))
                TaczCuriosConfig.COMMON.imaginaryBleedingDamagePerLevel.set(config.get("imaginaryBleedingDamagePerLevel").getAsDouble());
            if (config.has("imaginaryBleedingMaxLevel"))
                TaczCuriosConfig.COMMON.imaginaryBleedingMaxLevel.set(config.get("imaginaryBleedingMaxLevel").getAsInt());
            if (config.has("imaginaryBleedingDuration"))
                TaczCuriosConfig.COMMON.imaginaryBleedingDuration.set(config.get("imaginaryBleedingDuration").getAsInt());
            
            // 天火劫灭配置
            if (config.has("heavenFireApocalypseDamageBoost"))
                TaczCuriosConfig.COMMON.heavenFireApocalypseDamageBoost.set(config.get("heavenFireApocalypseDamageBoost").getAsDouble());
            if (config.has("heavenFireApocalypseExplosionRadius"))
                TaczCuriosConfig.COMMON.heavenFireApocalypseExplosionRadius.set(config.get("heavenFireApocalypseExplosionRadius").getAsDouble());
            if (config.has("heavenFireApocalypseExplosionDamage"))
                TaczCuriosConfig.COMMON.heavenFireApocalypseExplosionDamage.set(config.get("heavenFireApocalypseExplosionDamage").getAsDouble());
            if (config.has("heavenFireApocalypseHealthCost"))
                TaczCuriosConfig.COMMON.heavenFireApocalypseHealthCost.set(config.get("heavenFireApocalypseHealthCost").getAsDouble());
            if (config.has("brahmaBeastsHealthCostReduction"))
                TaczCuriosConfig.COMMON.brahmaBeastsHealthCostReduction.set(config.get("brahmaBeastsHealthCostReduction").getAsDouble());
            if (config.has("heavenFireApocalypseNearbyPlayerDamageBoost"))
                TaczCuriosConfig.COMMON.heavenFireApocalypseNearbyPlayerDamageBoost.set(config.get("heavenFireApocalypseNearbyPlayerDamageBoost").getAsDouble());
            if (config.has("heavenFireApocalypseNearbyPlayerDuration"))
                TaczCuriosConfig.COMMON.heavenFireApocalypseNearbyPlayerDuration.set(config.get("heavenFireApocalypseNearbyPlayerDuration").getAsInt());
            if (config.has("heavenFireApocalypseNearbyPlayerRadius"))
                TaczCuriosConfig.COMMON.heavenFireApocalypseNearbyPlayerRadius.set(config.get("heavenFireApocalypseNearbyPlayerRadius").getAsDouble());
            if (config.has("heavenFireApocalypseNearbyPlayerPotionAmplifier"))
                TaczCuriosConfig.COMMON.heavenFireApocalypseNearbyPlayerPotionAmplifier.set(config.get("heavenFireApocalypseNearbyPlayerPotionAmplifier").getAsInt());
            if (config.has("heavenFireApocalypseDamageConversionRatio"))
                TaczCuriosConfig.COMMON.heavenFireApocalypseDamageConversionRatio.set(config.get("heavenFireApocalypseDamageConversionRatio").getAsDouble());
            if (config.has("heavenFireApocalypseGunTypes")) {
                java.util.List<String> list = new java.util.ArrayList<>();
                JsonArray arr = config.getAsJsonArray("heavenFireApocalypseGunTypes");
                for (JsonElement e : arr) list.add(e.getAsString());
                TaczCuriosConfig.COMMON.heavenFireApocalypseGunTypes.set(list);
            }
            
            // 膛线配置
            if (config.has("riflingDamageBoost"))
                TaczCuriosConfig.COMMON.riflingDamageBoost.set(config.get("riflingDamageBoost").getAsDouble());
            
            // 分裂膛室配置
            if (config.has("splitChamberBulletCountBoost"))
                TaczCuriosConfig.COMMON.splitChamberBulletCountBoost.set(config.get("splitChamberBulletCountBoost").getAsDouble());
            
            // 卑劣加速配置
            if (config.has("despicableAccelerationFireRateBoost"))
                TaczCuriosConfig.COMMON.despicableAccelerationFireRateBoost.set(config.get("despicableAccelerationFireRateBoost").getAsDouble());
            if (config.has("despicableAccelerationDamageReduction"))
                TaczCuriosConfig.COMMON.despicableAccelerationDamageReduction.set(config.get("despicableAccelerationDamageReduction").getAsDouble());
            
            // 并合膛线配置
            if (config.has("mergedRiflingDamageBoost"))
                TaczCuriosConfig.COMMON.mergedRiflingDamageBoost.set(config.get("mergedRiflingDamageBoost").getAsDouble());
            if (config.has("mergedRiflingMovementSpeedBoost"))
                TaczCuriosConfig.COMMON.mergedRiflingMovementSpeedBoost.set(config.get("mergedRiflingMovementSpeedBoost").getAsDouble());
            
            // 合金钻头配置
            if (config.has("alloyDrillArmorPenetrationBoost"))
                TaczCuriosConfig.COMMON.alloyDrillArmorPenetrationBoost.set(config.get("alloyDrillArmorPenetrationBoost").getAsDouble());
            
            // 我小心海也绝非鳝类配置
            if (config.has("carefulHeartLauncherDamageBoost"))
                TaczCuriosConfig.COMMON.carefulHeartLauncherDamageBoost.set(config.get("carefulHeartLauncherDamageBoost").getAsDouble());
            if (config.has("carefulHeartExplosionDamageBoost"))
                TaczCuriosConfig.COMMON.carefulHeartExplosionDamageBoost.set(config.get("carefulHeartExplosionDamageBoost").getAsDouble());
            if (config.has("carefulHeartExplosionRadiusBoost"))
                TaczCuriosConfig.COMMON.carefulHeartExplosionRadiusBoost.set(config.get("carefulHeartExplosionRadiusBoost").getAsDouble());
            if (config.has("carefulHeartExplosionEnabled"))
                TaczCuriosConfig.COMMON.carefulHeartExplosionEnabled.set(config.get("carefulHeartExplosionEnabled").getAsDouble());
            
            // 烈焰风暴配置
            if (config.has("blazeStormExplosionRadiusBoost"))
                TaczCuriosConfig.COMMON.blazeStormExplosionRadiusBoost.set(config.get("blazeStormExplosionRadiusBoost").getAsDouble());
            if (config.has("blazeStormExplosionDamageBoost"))
                TaczCuriosConfig.COMMON.blazeStormExplosionDamageBoost.set(config.get("blazeStormExplosionDamageBoost").getAsDouble());
            if (config.has("blazeStormExplosionEnabled"))
                TaczCuriosConfig.COMMON.blazeStormExplosionEnabled.set(config.get("blazeStormExplosionEnabled").getAsDouble());
            
            // 烈焰风暴Prime配置
            if (config.has("blazeStormPrimeExplosionRadiusBoost"))
                TaczCuriosConfig.COMMON.blazeStormPrimeExplosionRadiusBoost.set(config.get("blazeStormPrimeExplosionRadiusBoost").getAsDouble());
            if (config.has("blazeStormPrimeExplosionDamageBoost"))
                TaczCuriosConfig.COMMON.blazeStormPrimeExplosionDamageBoost.set(config.get("blazeStormPrimeExplosionDamageBoost").getAsDouble());
            if (config.has("blazeStormPrimeExplosionEnabled"))
                TaczCuriosConfig.COMMON.blazeStormPrimeExplosionEnabled.set(config.get("blazeStormPrimeExplosionEnabled").getAsDouble());
            
            // 撕裂Prime配置
            if (config.has("rippingPrimeFireRateBoost"))
                TaczCuriosConfig.COMMON.rippingPrimeFireRateBoost.set(config.get("rippingPrimeFireRateBoost").getAsDouble());
            if (config.has("rippingPrimePenetrationBoost"))
                TaczCuriosConfig.COMMON.rippingPrimePenetrationBoost.set(config.get("rippingPrimePenetrationBoost").getAsDouble());
            
            // 抵近射击Prime配置
            if (config.has("closeCombatPrimeShotgunDamageBoost"))
                TaczCuriosConfig.COMMON.closeCombatPrimeShotgunDamageBoost.set(config.get("closeCombatPrimeShotgunDamageBoost").getAsDouble());
            
            // 极恶精准配置
            if (config.has("evilAccuracyRecoilReduction"))
                TaczCuriosConfig.COMMON.evilAccuracyRecoilReduction.set(config.get("evilAccuracyRecoilReduction").getAsDouble());
            if (config.has("evilAccuracyFireRateReduction"))
                TaczCuriosConfig.COMMON.evilAccuracyFireRateReduction.set(config.get("evilAccuracyFireRateReduction").getAsDouble());
            
            // 极限速度配置
            if (config.has("limitSpeedBulletSpeedBoost"))
                TaczCuriosConfig.COMMON.limitSpeedBulletSpeedBoost.set(config.get("limitSpeedBulletSpeedBoost").getAsDouble());
            
            // 凶恶延伸配置
            if (config.has("ferociousExtensionRangeBoost"))
                TaczCuriosConfig.COMMON.ferociousExtensionRangeBoost.set(config.get("ferociousExtensionRangeBoost").getAsDouble());
            
            // 抵近射击配置
            if (config.has("closeRangeShotDamageBoost"))
                TaczCuriosConfig.COMMON.closeRangeShotDamageBoost.set(config.get("closeRangeShotDamageBoost").getAsDouble());
            
            // 重装火力配置
            if (config.has("heavyFirepowerDamageBoost"))
                TaczCuriosConfig.COMMON.heavyFirepowerDamageBoost.set(config.get("heavyFirepowerDamageBoost").getAsDouble());
            if (config.has("heavyFirepowerAccuracyReduction"))
                TaczCuriosConfig.COMMON.heavyFirepowerAccuracyReduction.set(config.get("heavyFirepowerAccuracyReduction").getAsDouble());
            
            // 黄蜂蜇刺配置
            if (config.has("waspStingerDamageBoost"))
                TaczCuriosConfig.COMMON.waspStingerDamageBoost.set(config.get("waspStingerDamageBoost").getAsDouble());
            
            // 预言契约配置
            if (config.has("prophecyPactDamageBoost"))
                TaczCuriosConfig.COMMON.prophecyPactDamageBoost.set(config.get("prophecyPactDamageBoost").getAsDouble());
            
            // 恶性扩散配置
            if (config.has("malignantSpreadDamageBoost"))
                TaczCuriosConfig.COMMON.malignantSpreadDamageBoost.set(config.get("malignantSpreadDamageBoost").getAsDouble());
            if (config.has("malignantSpreadAccuracyReduction"))
                TaczCuriosConfig.COMMON.malignantSpreadAccuracyReduction.set(config.get("malignantSpreadAccuracyReduction").getAsDouble());
            
            // 膛室配置
            if (config.has("chamberSniperDamageBoost"))
                TaczCuriosConfig.COMMON.chamberSniperDamageBoost.set(config.get("chamberSniperDamageBoost").getAsDouble());
            
            // 膛室Prime配置
            if (config.has("chamberPrimeSniperDamageBoost"))
                TaczCuriosConfig.COMMON.chamberPrimeSniperDamageBoost.set(config.get("chamberPrimeSniperDamageBoost").getAsDouble());
            
            // 战术上膛配置
            if (config.has("tacticalReloadSpeedBoost"))
                TaczCuriosConfig.COMMON.tacticalReloadSpeedBoost.set(config.get("tacticalReloadSpeedBoost").getAsDouble());
            
            // 过载弹匣配置
            if (config.has("overloadedMagazineCapacityBoost"))
                TaczCuriosConfig.COMMON.overloadedMagazineCapacityBoost.set(config.get("overloadedMagazineCapacityBoost").getAsDouble());
            if (config.has("overloadedMagazineReloadSpeedReduction"))
                TaczCuriosConfig.COMMON.overloadedMagazineReloadSpeedReduction.set(config.get("overloadedMagazineReloadSpeedReduction").getAsDouble());
            
            // 地狱弹膛配置
            if (config.has("infernalChamberBulletCountBoost"))
                TaczCuriosConfig.COMMON.infernalChamberBulletCountBoost.set(config.get("infernalChamberBulletCountBoost").getAsDouble());
            
            // 持续火力配置
            if (config.has("sustainedFireReloadSpeedBoost"))
                TaczCuriosConfig.COMMON.sustainedFireReloadSpeedBoost.set(config.get("sustainedFireReloadSpeedBoost").getAsDouble());
            
            // 感染弹匣配置
            if (config.has("infectedMagazineCapacityBoost"))
                TaczCuriosConfig.COMMON.infectedMagazineCapacityBoost.set(config.get("infectedMagazineCapacityBoost").getAsDouble());
            if (config.has("infectedMagazineReloadSpeedReduction"))
                TaczCuriosConfig.COMMON.infectedMagazineReloadSpeedReduction.set(config.get("infectedMagazineReloadSpeedReduction").getAsDouble());
            
            // 致命洪流配置
            if (config.has("deadlySurgeFireRateBoost"))
                TaczCuriosConfig.COMMON.deadlySurgeFireRateBoost.set(config.get("deadlySurgeFireRateBoost").getAsDouble());
            if (config.has("deadlySurgeBulletCountBoost"))
                TaczCuriosConfig.COMMON.deadlySurgeBulletCountBoost.set(config.get("deadlySurgeBulletCountBoost").getAsDouble());
            
            // 弹头扩散配置
            if (config.has("bulletSpreadBulletCountBoost"))
                TaczCuriosConfig.COMMON.bulletSpreadBulletCountBoost.set(config.get("bulletSpreadBulletCountBoost").getAsDouble());
            
            // 压迫点配置
            if (config.has("oppressionPointMeleeDamageBoost"))
                TaczCuriosConfig.COMMON.oppressionPointMeleeDamageBoost.set(config.get("oppressionPointMeleeDamageBoost").getAsDouble());
            
            // 压迫点Prime配置
            if (config.has("oppressionPointPrimeMeleeDamageBoost"))
                TaczCuriosConfig.COMMON.oppressionPointPrimeMeleeDamageBoost.set(config.get("oppressionPointPrimeMeleeDamageBoost").getAsDouble());
            
            // 爆发装填配置
            if (config.has("burstReloadReloadSpeedBoost"))
                TaczCuriosConfig.COMMON.burstReloadReloadSpeedBoost.set(config.get("burstReloadReloadSpeedBoost").getAsDouble());
            
            // 剑风配置
            if (config.has("swordWindMeleeRangeBoost"))
                TaczCuriosConfig.COMMON.swordWindMeleeRangeBoost.set(config.get("swordWindMeleeRangeBoost").getAsDouble());
            
            // 剑风Prime配置
            if (config.has("swordWindPrimeMeleeRangeBoost"))
                TaczCuriosConfig.COMMON.swordWindPrimeMeleeRangeBoost.set(config.get("swordWindPrimeMeleeRangeBoost").getAsDouble());
            
            // 腐败弹匣配置
            if (config.has("corruptMagazineCapacityBoost"))
                TaczCuriosConfig.COMMON.corruptMagazineCapacityBoost.set(config.get("corruptMagazineCapacityBoost").getAsDouble());
            if (config.has("corruptMagazineReloadSpeedReduction"))
                TaczCuriosConfig.COMMON.corruptMagazineReloadSpeedReduction.set(config.get("corruptMagazineReloadSpeedReduction").getAsDouble());
            
            // 重口径配置
            if (config.has("heavyCaliberTagDamageBoost"))
                TaczCuriosConfig.COMMON.heavyCaliberTagDamageBoost.set(config.get("heavyCaliberTagDamageBoost").getAsDouble());
            if (config.has("heavyCaliberTagInaccuracyBoost"))
                TaczCuriosConfig.COMMON.heavyCaliberTagInaccuracyBoost.set(config.get("heavyCaliberTagInaccuracyBoost").getAsDouble());
            
            // 红-有-三配置
            if (config.has("redMovementTagSpeedBoost"))
                TaczCuriosConfig.COMMON.redMovementTagSpeedBoost.set(config.get("redMovementTagSpeedBoost").getAsDouble());
            
            // 夏日沙滩配置
            if (config.has("summerBeachHeavenFireMultiplier"))
                TaczCuriosConfig.COMMON.summerBeachHeavenFireMultiplier.set(config.get("summerBeachHeavenFireMultiplier").getAsDouble());
            if (config.has("summerBeachObtainEntity"))
                TaczCuriosConfig.COMMON.summerBeachObtainEntity.set(config.get("summerBeachObtainEntity").getAsString());
            if (config.has("summerBeachEvolutionEntity"))
                TaczCuriosConfig.COMMON.summerBeachEvolutionEntity.set(config.get("summerBeachEvolutionEntity").getAsString());
            
            // 梵天百兽配置
            if (config.has("brahmaBeastsHeavenFireMultiplier"))
                TaczCuriosConfig.COMMON.brahmaBeastsHeavenFireMultiplier.set(config.get("brahmaBeastsHeavenFireMultiplier").getAsDouble());
            if (config.has("brahmaBeastsEvolutionEntity"))
                TaczCuriosConfig.COMMON.brahmaBeastsEvolutionEntity.set(config.get("brahmaBeastsEvolutionEntity").getAsString());
            
            // 救世配置
            if (config.has("salvationHeavenFireMultiplier"))
                TaczCuriosConfig.COMMON.salvationHeavenFireMultiplier.set(config.get("salvationHeavenFireMultiplier").getAsDouble());
            if (config.has("salvationDamageReduction"))
                TaczCuriosConfig.COMMON.salvationDamageReduction.set(config.get("salvationDamageReduction").getAsDouble());
            if (config.has("salvationResistanceLevel"))
                TaczCuriosConfig.COMMON.salvationResistanceLevel.set(config.get("salvationResistanceLevel").getAsInt());
            if (config.has("salvationImaginaryResistance"))
                TaczCuriosConfig.COMMON.salvationImaginaryResistance.set(config.get("salvationImaginaryResistance").getAsDouble());
            
            // 无烬终焉配置
            if (config.has("endlessDamageBoost"))
                TaczCuriosConfig.COMMON.endlessDamageBoost.set(config.get("endlessDamageBoost").getAsDouble());
            if (config.has("endlessExplosionDamage"))
                TaczCuriosConfig.COMMON.endlessExplosionDamage.set(config.get("endlessExplosionDamage").getAsDouble());
            
            // 弹匣增幅配置
            if (config.has("magazineBoostReloadSpeedBoost"))
                TaczCuriosConfig.COMMON.magazineBoostReloadSpeedBoost.set(config.get("magazineBoostReloadSpeedBoost").getAsDouble());
            
            // 士兵基础挂牌配置
            if (config.has("soldierBasicTagDamageBoost"))
                TaczCuriosConfig.COMMON.soldierBasicTagDamageBoost.set(config.get("soldierBasicTagDamageBoost").getAsDouble());
            
            // 士兵特定挂牌配置
            if (config.has("soldierSpecificTagDamageBoost"))
                TaczCuriosConfig.COMMON.soldierSpecificTagDamageBoost.set(config.get("soldierSpecificTagDamageBoost").getAsDouble());
            
            // 乌拉尔银狼配置
            if (config.has("uralWolfTagHeadshotMultiplierBoost"))
                TaczCuriosConfig.COMMON.uralWolfTagHeadshotMultiplierBoost.set(config.get("uralWolfTagHeadshotMultiplierBoost").getAsDouble());
            
            // 耗竭装填配置
            if (config.has("depletedReloadMagazineCapacityPenalty"))
                TaczCuriosConfig.COMMON.depletedReloadMagazineCapacityPenalty.set(config.get("depletedReloadMagazineCapacityPenalty").getAsDouble());
            if (config.has("depletedReloadReloadSpeedBoost"))
                TaczCuriosConfig.COMMON.depletedReloadReloadSpeedBoost.set(config.get("depletedReloadReloadSpeedBoost").getAsDouble());
            
            // 爆发装填Prime配置
            if (config.has("burstReloadPrimeReloadSpeedBoost"))
                TaczCuriosConfig.COMMON.burstReloadPrimeReloadSpeedBoost.set(config.get("burstReloadPrimeReloadSpeedBoost").getAsDouble());
            
            // 战术上膛Prime配置
            if (config.has("tacticalReloadPrimeReloadSpeedBoost"))
                TaczCuriosConfig.COMMON.tacticalReloadPrimeReloadSpeedBoost.set(config.get("tacticalReloadPrimeReloadSpeedBoost").getAsDouble());
            
            // 霰弹扩充Prime配置
            if (config.has("shotgunExpansionPrimeCapacityBoost"))
                TaczCuriosConfig.COMMON.shotgunExpansionPrimeCapacityBoost.set(config.get("shotgunExpansionPrimeCapacityBoost").getAsDouble());
            
            // 弹匣增幅Prime配置
            if (config.has("magazineBoostPrimeCapacityBoost"))
                TaczCuriosConfig.COMMON.magazineBoostPrimeCapacityBoost.set(config.get("magazineBoostPrimeCapacityBoost").getAsDouble());
            
            // 串联弹匣Prime配置
            if (config.has("tandemMagazinePrimeCapacityBoost"))
                TaczCuriosConfig.COMMON.tandemMagazinePrimeCapacityBoost.set(config.get("tandemMagazinePrimeCapacityBoost").getAsDouble());
            
            // 霰弹扩充配置
            if (config.has("shotgunExpansionCapacityBoost"))
                TaczCuriosConfig.COMMON.shotgunExpansionCapacityBoost.set(config.get("shotgunExpansionCapacityBoost").getAsDouble());
            
            // 弹匣增幅配置
            if (config.has("magazineBoostCapacityBoost"))
                TaczCuriosConfig.COMMON.magazineBoostCapacityBoost.set(config.get("magazineBoostCapacityBoost").getAsDouble());
            
            // 串联弹匣配置
            if (config.has("tandemMagazineCapacityBoost"))
                TaczCuriosConfig.COMMON.tandemMagazineCapacityBoost.set(config.get("tandemMagazineCapacityBoost").getAsDouble());
            
            // 裂隙碎银配置
            if (config.has("riftSilverChestSpawnChance"))
                TaczCuriosConfig.COMMON.riftSilverChestSpawnChance.set(config.get("riftSilverChestSpawnChance").getAsDouble());
            
            // 掎角一阵配置
            if (config.has("kikakuIchijinHealthMultiplier"))
                TaczCuriosConfig.COMMON.kikakuIchijinHealthMultiplier.set(config.get("kikakuIchijinHealthMultiplier").getAsDouble());
            if (config.has("kikakuIchijinDestroyUnbreakableBlocks"))
                TaczCuriosConfig.COMMON.kikakuIchijinDestroyUnbreakableBlocks.set(config.get("kikakuIchijinDestroyUnbreakableBlocks").getAsBoolean());
            if (config.has("kikakuIchijinDestroyNormalBlocks"))
                TaczCuriosConfig.COMMON.kikakuIchijinDestroyNormalBlocks.set(config.get("kikakuIchijinDestroyNormalBlocks").getAsBoolean());
            
            // Apotheosis集成配置
            if (config.has("enableApotheosisIntegration"))
                TaczCuriosConfig.COMMON.enableApotheosisIntegration.set(config.get("enableApotheosisIntegration").getAsBoolean());
            if (config.has("curioConflicts")) {
                java.util.List<String> conflicts = new java.util.ArrayList<>();
                JsonArray arr = config.getAsJsonArray("curioConflicts");
                for (JsonElement e : arr) conflicts.add(e.getAsString());
                TaczCuriosConfig.COMMON.curioConflicts.set(conflicts);
        }
    }

    public static ConfigSyncPacket fromServer() {
        JsonObject config = new JsonObject();
        
        // 天火圣裁配置
        config.addProperty("heavenFireJudgmentDamageBoost", TaczCuriosConfig.COMMON.heavenFireJudgmentDamageBoost.get());
        config.addProperty("heavenFireJudgmentHealthCost", TaczCuriosConfig.COMMON.heavenFireJudgmentHealthCost.get());
        config.addProperty("heavenFireJudgmentDamageConversionRatio", TaczCuriosConfig.COMMON.heavenFireJudgmentDamageConversionRatio.get());
        {
            JsonArray arr = new JsonArray();
            for (String s : TaczCuriosConfig.COMMON.heavenFireJudgmentGunTypes.get()) arr.add(s);
            config.add("heavenFireJudgmentGunTypes", arr);
        }
        
        // 天火流血效果配置（两个饰品共用）
        config.addProperty("heavenFireBleedingDamagePerLevel", TaczCuriosConfig.COMMON.heavenFireBleedingDamagePerLevel.get());
        config.addProperty("heavenFireBleedingMaxLevel", TaczCuriosConfig.COMMON.heavenFireBleedingMaxLevel.get());
        config.addProperty("heavenFireBleedingDuration", TaczCuriosConfig.COMMON.heavenFireBleedingDuration.get());
        
        // 虚数流血效果配置
        config.addProperty("imaginaryBleedingDamagePerLevel", TaczCuriosConfig.COMMON.imaginaryBleedingDamagePerLevel.get());
        config.addProperty("imaginaryBleedingMaxLevel", TaczCuriosConfig.COMMON.imaginaryBleedingMaxLevel.get());
        config.addProperty("imaginaryBleedingDuration", TaczCuriosConfig.COMMON.imaginaryBleedingDuration.get());
        
        // 天火劫灭配置
        config.addProperty("heavenFireApocalypseDamageBoost", TaczCuriosConfig.COMMON.heavenFireApocalypseDamageBoost.get());
        config.addProperty("heavenFireApocalypseExplosionRadius", TaczCuriosConfig.COMMON.heavenFireApocalypseExplosionRadius.get());
        config.addProperty("heavenFireApocalypseExplosionDamage", TaczCuriosConfig.COMMON.heavenFireApocalypseExplosionDamage.get());
        config.addProperty("heavenFireApocalypseHealthCost", TaczCuriosConfig.COMMON.heavenFireApocalypseHealthCost.get());
        config.addProperty("brahmaBeastsHealthCostReduction", TaczCuriosConfig.COMMON.brahmaBeastsHealthCostReduction.get());
        config.addProperty("heavenFireApocalypseNearbyPlayerDamageBoost", TaczCuriosConfig.COMMON.heavenFireApocalypseNearbyPlayerDamageBoost.get());
        config.addProperty("heavenFireApocalypseNearbyPlayerDuration", TaczCuriosConfig.COMMON.heavenFireApocalypseNearbyPlayerDuration.get());
        config.addProperty("heavenFireApocalypseNearbyPlayerRadius", TaczCuriosConfig.COMMON.heavenFireApocalypseNearbyPlayerRadius.get());
        config.addProperty("heavenFireApocalypseNearbyPlayerPotionAmplifier", TaczCuriosConfig.COMMON.heavenFireApocalypseNearbyPlayerPotionAmplifier.get());
        config.addProperty("heavenFireApocalypseDamageConversionRatio", TaczCuriosConfig.COMMON.heavenFireApocalypseDamageConversionRatio.get());
        {
            JsonArray arr = new JsonArray();
            for (String s : TaczCuriosConfig.COMMON.heavenFireApocalypseGunTypes.get()) arr.add(s);
            config.add("heavenFireApocalypseGunTypes", arr);
        }
        
        // 膛线配置
        config.addProperty("riflingDamageBoost", TaczCuriosConfig.COMMON.riflingDamageBoost.get());
        
        // 分裂膛室配置
        config.addProperty("splitChamberBulletCountBoost", TaczCuriosConfig.COMMON.splitChamberBulletCountBoost.get());
        
        // 卑劣加速配置
        config.addProperty("despicableAccelerationFireRateBoost", TaczCuriosConfig.COMMON.despicableAccelerationFireRateBoost.get());
        config.addProperty("despicableAccelerationDamageReduction", TaczCuriosConfig.COMMON.despicableAccelerationDamageReduction.get());
        
        // 并合膛线配置
        config.addProperty("mergedRiflingDamageBoost", TaczCuriosConfig.COMMON.mergedRiflingDamageBoost.get());
        config.addProperty("mergedRiflingMovementSpeedBoost", TaczCuriosConfig.COMMON.mergedRiflingMovementSpeedBoost.get());
        
        // 合金钻头配置
        config.addProperty("alloyDrillArmorPenetrationBoost", TaczCuriosConfig.COMMON.alloyDrillArmorPenetrationBoost.get());
        
        // 我小心海也绝非鳝类配置
        config.addProperty("carefulHeartLauncherDamageBoost", TaczCuriosConfig.COMMON.carefulHeartLauncherDamageBoost.get());
        config.addProperty("carefulHeartExplosionDamageBoost", TaczCuriosConfig.COMMON.carefulHeartExplosionDamageBoost.get());
        config.addProperty("carefulHeartExplosionRadiusBoost", TaczCuriosConfig.COMMON.carefulHeartExplosionRadiusBoost.get());
        config.addProperty("carefulHeartExplosionEnabled", TaczCuriosConfig.COMMON.carefulHeartExplosionEnabled.get());
        
        // 烈焰风暴配置
        config.addProperty("blazeStormExplosionRadiusBoost", TaczCuriosConfig.COMMON.blazeStormExplosionRadiusBoost.get());
        config.addProperty("blazeStormExplosionDamageBoost", TaczCuriosConfig.COMMON.blazeStormExplosionDamageBoost.get());
        config.addProperty("blazeStormExplosionEnabled", TaczCuriosConfig.COMMON.blazeStormExplosionEnabled.get());
        
        // 烈焰风暴Prime配置
        config.addProperty("blazeStormPrimeExplosionRadiusBoost", TaczCuriosConfig.COMMON.blazeStormPrimeExplosionRadiusBoost.get());
        config.addProperty("blazeStormPrimeExplosionDamageBoost", TaczCuriosConfig.COMMON.blazeStormPrimeExplosionDamageBoost.get());
        config.addProperty("blazeStormPrimeExplosionEnabled", TaczCuriosConfig.COMMON.blazeStormPrimeExplosionEnabled.get());
        
        // 撕裂Prime配置
        config.addProperty("rippingPrimeFireRateBoost", TaczCuriosConfig.COMMON.rippingPrimeFireRateBoost.get());
        config.addProperty("rippingPrimePenetrationBoost", TaczCuriosConfig.COMMON.rippingPrimePenetrationBoost.get());
        
        // 抵近射击Prime配置
        config.addProperty("closeCombatPrimeShotgunDamageBoost", TaczCuriosConfig.COMMON.closeCombatPrimeShotgunDamageBoost.get());
        
        // 极恶精准配置
        config.addProperty("evilAccuracyRecoilReduction", TaczCuriosConfig.COMMON.evilAccuracyRecoilReduction.get());
        config.addProperty("evilAccuracyFireRateReduction", TaczCuriosConfig.COMMON.evilAccuracyFireRateReduction.get());
        
        // 极限速度配置
        config.addProperty("limitSpeedBulletSpeedBoost", TaczCuriosConfig.COMMON.limitSpeedBulletSpeedBoost.get());
        
        // 凶恶延伸配置
        config.addProperty("ferociousExtensionRangeBoost", TaczCuriosConfig.COMMON.ferociousExtensionRangeBoost.get());
        
        // 抵近射击配置
        config.addProperty("closeRangeShotDamageBoost", TaczCuriosConfig.COMMON.closeRangeShotDamageBoost.get());
        
        // 重装火力配置
        config.addProperty("heavyFirepowerDamageBoost", TaczCuriosConfig.COMMON.heavyFirepowerDamageBoost.get());
        config.addProperty("heavyFirepowerAccuracyReduction", TaczCuriosConfig.COMMON.heavyFirepowerAccuracyReduction.get());
        
        // 黄蜂蜇刺配置
        config.addProperty("waspStingerDamageBoost", TaczCuriosConfig.COMMON.waspStingerDamageBoost.get());
        
        // 预言契约配置
        config.addProperty("prophecyPactDamageBoost", TaczCuriosConfig.COMMON.prophecyPactDamageBoost.get());
        
        // 恶性扩散配置
        config.addProperty("malignantSpreadDamageBoost", TaczCuriosConfig.COMMON.malignantSpreadDamageBoost.get());
        config.addProperty("malignantSpreadAccuracyReduction", TaczCuriosConfig.COMMON.malignantSpreadAccuracyReduction.get());
        
        // 膛室配置
        config.addProperty("chamberSniperDamageBoost", TaczCuriosConfig.COMMON.chamberSniperDamageBoost.get());
        
        // 膛室Prime配置
        config.addProperty("chamberPrimeSniperDamageBoost", TaczCuriosConfig.COMMON.chamberPrimeSniperDamageBoost.get());
        
        // 战术上膛配置
        config.addProperty("tacticalReloadSpeedBoost", TaczCuriosConfig.COMMON.tacticalReloadSpeedBoost.get());
        
        // 过载弹匣配置
        config.addProperty("overloadedMagazineCapacityBoost", TaczCuriosConfig.COMMON.overloadedMagazineCapacityBoost.get());
        config.addProperty("overloadedMagazineReloadSpeedReduction", TaczCuriosConfig.COMMON.overloadedMagazineReloadSpeedReduction.get());
        
        // 地狱弹膛配置
        config.addProperty("infernalChamberBulletCountBoost", TaczCuriosConfig.COMMON.infernalChamberBulletCountBoost.get());
        
        // 持续火力配置
        config.addProperty("sustainedFireReloadSpeedBoost", TaczCuriosConfig.COMMON.sustainedFireReloadSpeedBoost.get());
        
        // 感染弹匣配置
        config.addProperty("infectedMagazineCapacityBoost", TaczCuriosConfig.COMMON.infectedMagazineCapacityBoost.get());
        config.addProperty("infectedMagazineReloadSpeedReduction", TaczCuriosConfig.COMMON.infectedMagazineReloadSpeedReduction.get());
        
        // 致命洪流配置
        config.addProperty("deadlySurgeFireRateBoost", TaczCuriosConfig.COMMON.deadlySurgeFireRateBoost.get());
        config.addProperty("deadlySurgeBulletCountBoost", TaczCuriosConfig.COMMON.deadlySurgeBulletCountBoost.get());
        
        // 弹头扩散配置
        config.addProperty("bulletSpreadBulletCountBoost", TaczCuriosConfig.COMMON.bulletSpreadBulletCountBoost.get());
        
        // 压迫点配置
        config.addProperty("oppressionPointMeleeDamageBoost", TaczCuriosConfig.COMMON.oppressionPointMeleeDamageBoost.get());
        
        // 压迫点Prime配置
        config.addProperty("oppressionPointPrimeMeleeDamageBoost", TaczCuriosConfig.COMMON.oppressionPointPrimeMeleeDamageBoost.get());
        
        // 爆发装填配置
        config.addProperty("burstReloadReloadSpeedBoost", TaczCuriosConfig.COMMON.burstReloadReloadSpeedBoost.get());
        
        // 剑风配置
        config.addProperty("swordWindMeleeRangeBoost", TaczCuriosConfig.COMMON.swordWindMeleeRangeBoost.get());
        
        // 剑风Prime配置
        config.addProperty("swordWindPrimeMeleeRangeBoost", TaczCuriosConfig.COMMON.swordWindPrimeMeleeRangeBoost.get());
        
        // 腐败弹匣配置
        config.addProperty("corruptMagazineCapacityBoost", TaczCuriosConfig.COMMON.corruptMagazineCapacityBoost.get());
        config.addProperty("corruptMagazineReloadSpeedReduction", TaczCuriosConfig.COMMON.corruptMagazineReloadSpeedReduction.get());
        
        // 重口径配置
        config.addProperty("heavyCaliberTagDamageBoost", TaczCuriosConfig.COMMON.heavyCaliberTagDamageBoost.get());
        config.addProperty("heavyCaliberTagInaccuracyBoost", TaczCuriosConfig.COMMON.heavyCaliberTagInaccuracyBoost.get());
        
        // 红-有-三配置
        config.addProperty("redMovementTagSpeedBoost", TaczCuriosConfig.COMMON.redMovementTagSpeedBoost.get());
        
        // 夏日沙滩配置
        config.addProperty("summerBeachHeavenFireMultiplier", TaczCuriosConfig.COMMON.summerBeachHeavenFireMultiplier.get());
        config.addProperty("summerBeachObtainEntity", TaczCuriosConfig.COMMON.summerBeachObtainEntity.get());
        config.addProperty("summerBeachEvolutionEntity", TaczCuriosConfig.COMMON.summerBeachEvolutionEntity.get());
        
        // 梵天百兽配置
        config.addProperty("brahmaBeastsHeavenFireMultiplier", TaczCuriosConfig.COMMON.brahmaBeastsHeavenFireMultiplier.get());
        config.addProperty("brahmaBeastsEvolutionEntity", TaczCuriosConfig.COMMON.brahmaBeastsEvolutionEntity.get());
        
        // 救世配置
        config.addProperty("salvationHeavenFireMultiplier", TaczCuriosConfig.COMMON.salvationHeavenFireMultiplier.get());
        config.addProperty("salvationDamageReduction", TaczCuriosConfig.COMMON.salvationDamageReduction.get());
        config.addProperty("salvationResistanceLevel", TaczCuriosConfig.COMMON.salvationResistanceLevel.get());
        config.addProperty("salvationImaginaryResistance", TaczCuriosConfig.COMMON.salvationImaginaryResistance.get());
        
        // 无烬终焉配置
        config.addProperty("endlessDamageBoost", TaczCuriosConfig.COMMON.endlessDamageBoost.get());
        config.addProperty("endlessExplosionDamage", TaczCuriosConfig.COMMON.endlessExplosionDamage.get());
        
        // 弹匣增幅配置
        config.addProperty("magazineBoostReloadSpeedBoost", TaczCuriosConfig.COMMON.magazineBoostReloadSpeedBoost.get());
        
        // 士兵基础挂牌配置
        config.addProperty("soldierBasicTagDamageBoost", TaczCuriosConfig.COMMON.soldierBasicTagDamageBoost.get());
        
        // 士兵特定挂牌配置
        config.addProperty("soldierSpecificTagDamageBoost", TaczCuriosConfig.COMMON.soldierSpecificTagDamageBoost.get());
        
        // 乌拉尔银狼配置
        config.addProperty("uralWolfTagHeadshotMultiplierBoost", TaczCuriosConfig.COMMON.uralWolfTagHeadshotMultiplierBoost.get());
        
        // 耗竭装填配置
        config.addProperty("depletedReloadMagazineCapacityPenalty", TaczCuriosConfig.COMMON.depletedReloadMagazineCapacityPenalty.get());
        config.addProperty("depletedReloadReloadSpeedBoost", TaczCuriosConfig.COMMON.depletedReloadReloadSpeedBoost.get());
        
        // 爆发装填Prime配置
        config.addProperty("burstReloadPrimeReloadSpeedBoost", TaczCuriosConfig.COMMON.burstReloadPrimeReloadSpeedBoost.get());
        
        // 战术上膛Prime配置
        config.addProperty("tacticalReloadPrimeReloadSpeedBoost", TaczCuriosConfig.COMMON.tacticalReloadPrimeReloadSpeedBoost.get());
        
        // 霰弹扩充Prime配置
        config.addProperty("shotgunExpansionPrimeCapacityBoost", TaczCuriosConfig.COMMON.shotgunExpansionPrimeCapacityBoost.get());
        
        // 弹匣增幅Prime配置
        config.addProperty("magazineBoostPrimeCapacityBoost", TaczCuriosConfig.COMMON.magazineBoostPrimeCapacityBoost.get());
        
        // 串联弹匣Prime配置
        config.addProperty("tandemMagazinePrimeCapacityBoost", TaczCuriosConfig.COMMON.tandemMagazinePrimeCapacityBoost.get());
        
        // 霰弹扩充配置
        config.addProperty("shotgunExpansionCapacityBoost", TaczCuriosConfig.COMMON.shotgunExpansionCapacityBoost.get());
        
        // 弹匣增幅配置
        config.addProperty("magazineBoostCapacityBoost", TaczCuriosConfig.COMMON.magazineBoostCapacityBoost.get());
        
        // 串联弹匣配置
        config.addProperty("tandemMagazineCapacityBoost", TaczCuriosConfig.COMMON.tandemMagazineCapacityBoost.get());
        
        // 裂隙碎银配置
        config.addProperty("riftSilverChestSpawnChance", TaczCuriosConfig.COMMON.riftSilverChestSpawnChance.get());
        
        // 掎角一阵配置
        config.addProperty("kikakuIchijinHealthMultiplier", TaczCuriosConfig.COMMON.kikakuIchijinHealthMultiplier.get());
        config.addProperty("kikakuIchijinDestroyUnbreakableBlocks", TaczCuriosConfig.COMMON.kikakuIchijinDestroyUnbreakableBlocks.get());
        config.addProperty("kikakuIchijinDestroyNormalBlocks", TaczCuriosConfig.COMMON.kikakuIchijinDestroyNormalBlocks.get());
        
        // Apotheosis集成配置
        config.addProperty("enableApotheosisIntegration", TaczCuriosConfig.COMMON.enableApotheosisIntegration.get());
        {
            JsonArray arr = new JsonArray();
            for (String s : TaczCuriosConfig.COMMON.curioConflicts.get()) arr.add(s);
            config.add("curioConflicts", arr);
        }
        
        return new ConfigSyncPacket(GSON.toJson(config));
    }
}
