package com.xlxyvergil.tcc.network;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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
            if (config.has("heavenFireApocalypseDelayDuration"))
                TaczCuriosConfig.COMMON.heavenFireApocalypseDelayDuration.set(config.get("heavenFireApocalypseDelayDuration").getAsInt());
            
            // 虚数侵染效果配置
            if (config.has("imaginaryInfectionAmpPerLevel"))
                TaczCuriosConfig.COMMON.imaginaryInfectionAmpPerLevel.set(config.get("imaginaryInfectionAmpPerLevel").getAsDouble());
            if (config.has("imaginaryInfectionMaxLevel"))
                TaczCuriosConfig.COMMON.imaginaryInfectionMaxLevel.set(config.get("imaginaryInfectionMaxLevel").getAsInt());
            if (config.has("imaginaryInfectionDuration"))
                TaczCuriosConfig.COMMON.imaginaryInfectionDuration.set(config.get("imaginaryInfectionDuration").getAsInt());
            if (config.has("imaginaryInfectionResistanceReduction"))
                TaczCuriosConfig.COMMON.imaginaryInfectionResistanceReduction.set(config.get("imaginaryInfectionResistanceReduction").getAsDouble());
            if (config.has("judgmentImaginaryInfectionMaxLevel"))
                TaczCuriosConfig.COMMON.judgmentImaginaryInfectionMaxLevel.set(config.get("judgmentImaginaryInfectionMaxLevel").getAsInt());
            if (config.has("apocalypseImaginaryInfectionMaxLevel"))
                TaczCuriosConfig.COMMON.apocalypseImaginaryInfectionMaxLevel.set(config.get("apocalypseImaginaryInfectionMaxLevel").getAsInt());
            if (config.has("endlessImaginaryInfectionMaxLevel"))
                TaczCuriosConfig.COMMON.endlessImaginaryInfectionMaxLevel.set(config.get("endlessImaginaryInfectionMaxLevel").getAsInt());
            
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
            if (config.has("summerBeachELCurseReduction"))
                TaczCuriosConfig.COMMON.summerBeachELCurseReduction.set(config.get("summerBeachELCurseReduction").getAsDouble());
            if (config.has("summerBeachObtainEntity"))
                TaczCuriosConfig.COMMON.summerBeachObtainEntity.set(config.get("summerBeachObtainEntity").getAsString());
            if (config.has("summerBeachEvolutionRequirements")) {
                java.util.List<java.util.List<String>> list = new java.util.ArrayList<>();
                JsonArray outerArr = config.getAsJsonArray("summerBeachEvolutionRequirements");
                for (JsonElement outerElem : outerArr) {
                    JsonArray innerArr = outerElem.getAsJsonArray();
                    java.util.List<String> innerList = new java.util.ArrayList<>();
                    for (int i = 0; i < innerArr.size(); i++) {
                        innerList.add(innerArr.get(i).getAsString());
                    }
                    list.add(innerList);
                }
                @SuppressWarnings({"unchecked", "rawtypes"})
                net.minecraftforge.common.ForgeConfigSpec.ConfigValue raw = (net.minecraftforge.common.ForgeConfigSpec.ConfigValue) TaczCuriosConfig.COMMON.summerBeachEvolutionRequirements;
                raw.set(list);
            }
            if (config.has("summerBeachMaxKillResistance"))
                TaczCuriosConfig.COMMON.summerBeachMaxKillResistance.set(config.get("summerBeachMaxKillResistance").getAsInt());
            if (config.has("summerBeachBaseResistance"))
                TaczCuriosConfig.COMMON.summerBeachBaseResistance.set(config.get("summerBeachBaseResistance").getAsInt());
            if (config.has("summerBeachResistanceEntities")) {
                java.util.List<java.util.List<String>> list = new java.util.ArrayList<>();
                JsonArray outerArr = config.getAsJsonArray("summerBeachResistanceEntities");
                for (JsonElement outerElem : outerArr) {
                    JsonArray innerArr = outerElem.getAsJsonArray();
                    java.util.List<String> innerList = new java.util.ArrayList<>();
                    for (int i = 0; i < innerArr.size(); i++) {
                        innerList.add(innerArr.get(i).getAsString());
                    }
                    list.add(innerList);
                }
                @SuppressWarnings({"unchecked", "rawtypes"})
                net.minecraftforge.common.ForgeConfigSpec.ConfigValue raw = (net.minecraftforge.common.ForgeConfigSpec.ConfigValue) TaczCuriosConfig.COMMON.summerBeachResistanceEntities;
                raw.set(list);
            }
            
            // 梵天百兽配置
            if (config.has("brahmaBeastsELCurseReduction"))
                TaczCuriosConfig.COMMON.brahmaBeastsELCurseReduction.set(config.get("brahmaBeastsELCurseReduction").getAsDouble());
            if (config.has("brahmaBeastsEvolutionRequirements")) {
                java.util.List<java.util.List<String>> list = new java.util.ArrayList<>();
                JsonArray outerArr = config.getAsJsonArray("brahmaBeastsEvolutionRequirements");
                for (JsonElement outerElem : outerArr) {
                    JsonArray innerArr = outerElem.getAsJsonArray();
                    java.util.List<String> innerList = new java.util.ArrayList<>();
                    for (int i = 0; i < innerArr.size(); i++) {
                        innerList.add(innerArr.get(i).getAsString());
                    }
                    list.add(innerList);
                }
                @SuppressWarnings({"unchecked", "rawtypes"})
                net.minecraftforge.common.ForgeConfigSpec.ConfigValue rawBb = (net.minecraftforge.common.ForgeConfigSpec.ConfigValue) TaczCuriosConfig.COMMON.brahmaBeastsEvolutionRequirements;
                rawBb.set(list);
            }
            if (config.has("brahmaBeastsMaxKillResistance"))
                TaczCuriosConfig.COMMON.brahmaBeastsMaxKillResistance.set(config.get("brahmaBeastsMaxKillResistance").getAsInt());
            if (config.has("brahmaBeastsBaseResistance"))
                TaczCuriosConfig.COMMON.brahmaBeastsBaseResistance.set(config.get("brahmaBeastsBaseResistance").getAsInt());
            if (config.has("brahmaBeastsResistanceEntities")) {
                java.util.List<java.util.List<String>> list = new java.util.ArrayList<>();
                JsonArray outerArr = config.getAsJsonArray("brahmaBeastsResistanceEntities");
                for (JsonElement outerElem : outerArr) {
                    JsonArray innerArr = outerElem.getAsJsonArray();
                    java.util.List<String> innerList = new java.util.ArrayList<>();
                    for (int i = 0; i < innerArr.size(); i++) {
                        innerList.add(innerArr.get(i).getAsString());
                    }
                    list.add(innerList);
                }
                @SuppressWarnings({"unchecked", "rawtypes"})
                net.minecraftforge.common.ForgeConfigSpec.ConfigValue raw = (net.minecraftforge.common.ForgeConfigSpec.ConfigValue) TaczCuriosConfig.COMMON.brahmaBeastsResistanceEntities;
                raw.set(list);
            }
            
            // 救世配置
            if (config.has("salvationELCurseReduction"))
                TaczCuriosConfig.COMMON.salvationELCurseReduction.set(config.get("salvationELCurseReduction").getAsDouble());
            if (config.has("salvationDamageReduction"))
                TaczCuriosConfig.COMMON.salvationDamageReduction.set(config.get("salvationDamageReduction").getAsDouble());
            if (config.has("salvationResistanceLevel"))
                TaczCuriosConfig.COMMON.salvationResistanceLevel.set(config.get("salvationResistanceLevel").getAsInt());
            
            // 无烬终焉配置
            if (config.has("endlessDamageBoost"))
                TaczCuriosConfig.COMMON.endlessDamageBoost.set(config.get("endlessDamageBoost").getAsDouble());
            if (config.has("endlessExplosionDamage"))
                TaczCuriosConfig.COMMON.endlessExplosionDamage.set(config.get("endlessExplosionDamage").getAsDouble());
            if (config.has("endlessNearbyPlayerDamageBoost"))
                TaczCuriosConfig.COMMON.endlessNearbyPlayerDamageBoost.set(config.get("endlessNearbyPlayerDamageBoost").getAsDouble());
            if (config.has("endlessNearbyPlayerPotionAmplifier"))
                TaczCuriosConfig.COMMON.endlessNearbyPlayerPotionAmplifier.set(config.get("endlessNearbyPlayerPotionAmplifier").getAsInt());
            if (config.has("endlessNearbyPlayerDuration"))
                TaczCuriosConfig.COMMON.endlessNearbyPlayerDuration.set(config.get("endlessNearbyPlayerDuration").getAsInt());
            if (config.has("endlessNearbyPlayerRadius"))
                TaczCuriosConfig.COMMON.endlessNearbyPlayerRadius.set(config.get("endlessNearbyPlayerRadius").getAsDouble());
            if (config.has("endlessGunTypes")) {
                java.util.List<String> list = new java.util.ArrayList<>();
                JsonArray arr = config.getAsJsonArray("endlessGunTypes");
                for (JsonElement e : arr) list.add(e.getAsString());
                TaczCuriosConfig.COMMON.endlessGunTypes.set(list);
            }

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
            
            // ========== 新增33个饰品配置同步 ==========
            if (config.has("criticalDelayCritChanceBoost")) TaczCuriosConfig.COMMON.criticalDelayCritChanceBoost.set(config.get("criticalDelayCritChanceBoost").getAsDouble());
            if (config.has("criticalDelayFireRateReduction")) TaczCuriosConfig.COMMON.criticalDelayFireRateReduction.set(config.get("criticalDelayFireRateReduction").getAsDouble());
            if (config.has("lethalCritCritChance")) TaczCuriosConfig.COMMON.lethalCritCritChance.set(config.get("lethalCritCritChance").getAsDouble());
            if (config.has("weaknessSenseCritDamage")) TaczCuriosConfig.COMMON.weaknessSenseCritDamage.set(config.get("weaknessSenseCritDamage").getAsDouble());
            if (config.has("argonScopeBaseCritChance")) TaczCuriosConfig.COMMON.argonScopeBaseCritChance.set(config.get("argonScopeBaseCritChance").getAsDouble());
            if (config.has("argonScopeCritChancePerLevel")) TaczCuriosConfig.COMMON.argonScopeCritChancePerLevel.set(config.get("argonScopeCritChancePerLevel").getAsDouble());
            if (config.has("argonScopeDuration")) TaczCuriosConfig.COMMON.argonScopeDuration.set(config.get("argonScopeDuration").getAsInt());
            if (config.has("gildedArgonScopeBaseCritChance")) TaczCuriosConfig.COMMON.gildedArgonScopeBaseCritChance.set(config.get("gildedArgonScopeBaseCritChance").getAsDouble());
            if (config.has("gildedArgonScopeCritChancePerLevel")) TaczCuriosConfig.COMMON.gildedArgonScopeCritChancePerLevel.set(config.get("gildedArgonScopeCritChancePerLevel").getAsDouble());
            if (config.has("gildedArgonScopeHeadshotKillExtra")) TaczCuriosConfig.COMMON.gildedArgonScopeHeadshotKillExtra.set(config.get("gildedArgonScopeHeadshotKillExtra").getAsDouble());
            if (config.has("gildedArgonScopeDuration")) TaczCuriosConfig.COMMON.gildedArgonScopeDuration.set(config.get("gildedArgonScopeDuration").getAsInt());
            if (config.has("gildedArgonScopeMaxStacks")) TaczCuriosConfig.COMMON.gildedArgonScopeMaxStacks.set(config.get("gildedArgonScopeMaxStacks").getAsInt());
            if (config.has("sharpBulletBaseCritDamage")) TaczCuriosConfig.COMMON.sharpBulletBaseCritDamage.set(config.get("sharpBulletBaseCritDamage").getAsDouble());
            if (config.has("sharpBulletCritDamagePerLevel")) TaczCuriosConfig.COMMON.sharpBulletCritDamagePerLevel.set(config.get("sharpBulletCritDamagePerLevel").getAsDouble());
            if (config.has("sharpBulletDuration")) TaczCuriosConfig.COMMON.sharpBulletDuration.set(config.get("sharpBulletDuration").getAsInt());
            if (config.has("gildedRifleAptitudePerHarmful")) TaczCuriosConfig.COMMON.gildedRifleAptitudePerHarmful.set(config.get("gildedRifleAptitudePerHarmful").getAsDouble());
            if (config.has("gildedRifleAptitudeDuration")) TaczCuriosConfig.COMMON.gildedRifleAptitudeDuration.set(config.get("gildedRifleAptitudeDuration").getAsInt());
            if (config.has("gildedRifleAptitudeMaxStacks")) TaczCuriosConfig.COMMON.gildedRifleAptitudeMaxStacks.set(config.get("gildedRifleAptitudeMaxStacks").getAsInt());
            if (config.has("gildedSplitChamberBulletCountBase")) TaczCuriosConfig.COMMON.gildedSplitChamberBulletCountBase.set(config.get("gildedSplitChamberBulletCountBase").getAsDouble());
            if (config.has("gildedSplitChamberBulletCountPerLevel")) TaczCuriosConfig.COMMON.gildedSplitChamberBulletCountPerLevel.set(config.get("gildedSplitChamberBulletCountPerLevel").getAsDouble());
            if (config.has("gildedSplitChamberDuration")) TaczCuriosConfig.COMMON.gildedSplitChamberDuration.set(config.get("gildedSplitChamberDuration").getAsInt());
            if (config.has("gildedSplitChamberMaxStacks")) TaczCuriosConfig.COMMON.gildedSplitChamberMaxStacks.set(config.get("gildedSplitChamberMaxStacks").getAsInt());
            if (config.has("destructionCritDamage")) TaczCuriosConfig.COMMON.destructionCritDamage.set(config.get("destructionCritDamage").getAsDouble());
            if (config.has("destructionPrimeCritDamage")) TaczCuriosConfig.COMMON.destructionPrimeCritDamage.set(config.get("destructionPrimeCritDamage").getAsDouble());
            if (config.has("thunderBarrelCritChance")) TaczCuriosConfig.COMMON.thunderBarrelCritChance.set(config.get("thunderBarrelCritChance").getAsDouble());
            if (config.has("thunderBarrelPrimeCritChance")) TaczCuriosConfig.COMMON.thunderBarrelPrimeCritChance.set(config.get("thunderBarrelPrimeCritChance").getAsDouble());
            if (config.has("laserScopeBaseCritChance")) TaczCuriosConfig.COMMON.laserScopeBaseCritChance.set(config.get("laserScopeBaseCritChance").getAsDouble());
            if (config.has("laserScopeCritChancePerLevel")) TaczCuriosConfig.COMMON.laserScopeCritChancePerLevel.set(config.get("laserScopeCritChancePerLevel").getAsDouble());
            if (config.has("laserScopeDuration")) TaczCuriosConfig.COMMON.laserScopeDuration.set(config.get("laserScopeDuration").getAsInt());
            if (config.has("fragmentShotBaseCritDamage")) TaczCuriosConfig.COMMON.fragmentShotBaseCritDamage.set(config.get("fragmentShotBaseCritDamage").getAsDouble());
            if (config.has("fragmentShotCritDamagePerLevel")) TaczCuriosConfig.COMMON.fragmentShotCritDamagePerLevel.set(config.get("fragmentShotCritDamagePerLevel").getAsDouble());
            if (config.has("fragmentShotDuration")) TaczCuriosConfig.COMMON.fragmentShotDuration.set(config.get("fragmentShotDuration").getAsInt());
            if (config.has("gildedShotgunSavvyPerHarmful")) TaczCuriosConfig.COMMON.gildedShotgunSavvyPerHarmful.set(config.get("gildedShotgunSavvyPerHarmful").getAsDouble());
            if (config.has("gildedShotgunSavvyDuration")) TaczCuriosConfig.COMMON.gildedShotgunSavvyDuration.set(config.get("gildedShotgunSavvyDuration").getAsInt());
            if (config.has("gildedShotgunSavvyMaxStacks")) TaczCuriosConfig.COMMON.gildedShotgunSavvyMaxStacks.set(config.get("gildedShotgunSavvyMaxStacks").getAsInt());
            if (config.has("gildedInfernalChamberBulletCountBase")) TaczCuriosConfig.COMMON.gildedInfernalChamberBulletCountBase.set(config.get("gildedInfernalChamberBulletCountBase").getAsDouble());
            if (config.has("gildedInfernalChamberBulletCountPerLevel")) TaczCuriosConfig.COMMON.gildedInfernalChamberBulletCountPerLevel.set(config.get("gildedInfernalChamberBulletCountPerLevel").getAsDouble());
            if (config.has("gildedInfernalChamberDuration")) TaczCuriosConfig.COMMON.gildedInfernalChamberDuration.set(config.get("gildedInfernalChamberDuration").getAsInt());
            if (config.has("gildedInfernalChamberMaxStacks")) TaczCuriosConfig.COMMON.gildedInfernalChamberMaxStacks.set(config.get("gildedInfernalChamberMaxStacks").getAsInt());
            if (config.has("weaknessMasteryCritDamage")) TaczCuriosConfig.COMMON.weaknessMasteryCritDamage.set(config.get("weaknessMasteryCritDamage").getAsDouble());
            if (config.has("weaknessMasteryPrimeCritDamage")) TaczCuriosConfig.COMMON.weaknessMasteryPrimeCritDamage.set(config.get("weaknessMasteryPrimeCritDamage").getAsDouble());
            if (config.has("hollowPointCritDamage")) TaczCuriosConfig.COMMON.hollowPointCritDamage.set(config.get("hollowPointCritDamage").getAsDouble());
            if (config.has("hollowPointPistolDamageReduction")) TaczCuriosConfig.COMMON.hollowPointPistolDamageReduction.set(config.get("hollowPointPistolDamageReduction").getAsDouble());
            if (config.has("pistolMasteryCritChance")) TaczCuriosConfig.COMMON.pistolMasteryCritChance.set(config.get("pistolMasteryCritChance").getAsDouble());
            if (config.has("pistolMasteryPrimeCritChance")) TaczCuriosConfig.COMMON.pistolMasteryPrimeCritChance.set(config.get("pistolMasteryPrimeCritChance").getAsDouble());
            if (config.has("hydraulicCrosshairBaseCritChance")) TaczCuriosConfig.COMMON.hydraulicCrosshairBaseCritChance.set(config.get("hydraulicCrosshairBaseCritChance").getAsDouble());
            if (config.has("hydraulicCrosshairCritChancePerLevel")) TaczCuriosConfig.COMMON.hydraulicCrosshairCritChancePerLevel.set(config.get("hydraulicCrosshairCritChancePerLevel").getAsDouble());
            if (config.has("hydraulicCrosshairDuration")) TaczCuriosConfig.COMMON.hydraulicCrosshairDuration.set(config.get("hydraulicCrosshairDuration").getAsInt());
            if (config.has("gildedHydraulicCrosshairBaseCritChance")) TaczCuriosConfig.COMMON.gildedHydraulicCrosshairBaseCritChance.set(config.get("gildedHydraulicCrosshairBaseCritChance").getAsDouble());
            if (config.has("gildedHydraulicCrosshairCritChancePerLevel")) TaczCuriosConfig.COMMON.gildedHydraulicCrosshairCritChancePerLevel.set(config.get("gildedHydraulicCrosshairCritChancePerLevel").getAsDouble());
            if (config.has("gildedHydraulicCrosshairHeadshotKillExtra")) TaczCuriosConfig.COMMON.gildedHydraulicCrosshairHeadshotKillExtra.set(config.get("gildedHydraulicCrosshairHeadshotKillExtra").getAsDouble());
            if (config.has("gildedHydraulicCrosshairDuration")) TaczCuriosConfig.COMMON.gildedHydraulicCrosshairDuration.set(config.get("gildedHydraulicCrosshairDuration").getAsInt());
            if (config.has("gildedHydraulicCrosshairMaxStacks")) TaczCuriosConfig.COMMON.gildedHydraulicCrosshairMaxStacks.set(config.get("gildedHydraulicCrosshairMaxStacks").getAsInt());
            if (config.has("sharpAmmoBaseCritDamage")) TaczCuriosConfig.COMMON.sharpAmmoBaseCritDamage.set(config.get("sharpAmmoBaseCritDamage").getAsDouble());
            if (config.has("sharpAmmoCritDamagePerLevel")) TaczCuriosConfig.COMMON.sharpAmmoCritDamagePerLevel.set(config.get("sharpAmmoCritDamagePerLevel").getAsDouble());
            if (config.has("sharpAmmoDuration")) TaczCuriosConfig.COMMON.sharpAmmoDuration.set(config.get("sharpAmmoDuration").getAsInt());
            if (config.has("gildedMarksmanPerHarmful")) TaczCuriosConfig.COMMON.gildedMarksmanPerHarmful.set(config.get("gildedMarksmanPerHarmful").getAsDouble());
            if (config.has("gildedMarksmanDuration")) TaczCuriosConfig.COMMON.gildedMarksmanDuration.set(config.get("gildedMarksmanDuration").getAsInt());
            if (config.has("gildedMarksmanMaxStacks")) TaczCuriosConfig.COMMON.gildedMarksmanMaxStacks.set(config.get("gildedMarksmanMaxStacks").getAsInt());
            if (config.has("gildedBulletSpreadBulletCountBase")) TaczCuriosConfig.COMMON.gildedBulletSpreadBulletCountBase.set(config.get("gildedBulletSpreadBulletCountBase").getAsDouble());
            if (config.has("gildedBulletSpreadBulletCountPerLevel")) TaczCuriosConfig.COMMON.gildedBulletSpreadBulletCountPerLevel.set(config.get("gildedBulletSpreadBulletCountPerLevel").getAsDouble());
            if (config.has("gildedBulletSpreadDuration")) TaczCuriosConfig.COMMON.gildedBulletSpreadDuration.set(config.get("gildedBulletSpreadDuration").getAsInt());
            if (config.has("gildedBulletSpreadMaxStacks")) TaczCuriosConfig.COMMON.gildedBulletSpreadMaxStacks.set(config.get("gildedBulletSpreadMaxStacks").getAsInt());
            if (config.has("steelSlashCritChance")) TaczCuriosConfig.COMMON.steelSlashCritChance.set(config.get("steelSlashCritChance").getAsDouble());
            if (config.has("dismembermentCritDamage")) TaczCuriosConfig.COMMON.dismembermentCritDamage.set(config.get("dismembermentCritDamage").getAsDouble());
            if (config.has("sacrificeOppressionMeleeDamage")) TaczCuriosConfig.COMMON.sacrificeOppressionMeleeDamage.set(config.get("sacrificeOppressionMeleeDamage").getAsDouble());
            if (config.has("sacrificeSteelCritChance")) TaczCuriosConfig.COMMON.sacrificeSteelCritChance.set(config.get("sacrificeSteelCritChance").getAsDouble());
            if (config.has("gildedSteelSlashCritChanceBase")) TaczCuriosConfig.COMMON.gildedSteelSlashCritChanceBase.set(config.get("gildedSteelSlashCritChanceBase").getAsDouble());
            if (config.has("gildedSteelSlashCritDamagePerLevel")) TaczCuriosConfig.COMMON.gildedSteelSlashCritDamagePerLevel.set(config.get("gildedSteelSlashCritDamagePerLevel").getAsDouble());
            if (config.has("gildedSteelSlashDuration")) TaczCuriosConfig.COMMON.gildedSteelSlashDuration.set(config.get("gildedSteelSlashDuration").getAsInt());
            if (config.has("gildedSteelSlashMaxStacks")) TaczCuriosConfig.COMMON.gildedSteelSlashMaxStacks.set(config.get("gildedSteelSlashMaxStacks").getAsInt());
            if (config.has("conditionOverloadPerHarmful")) TaczCuriosConfig.COMMON.conditionOverloadPerHarmful.set(config.get("conditionOverloadPerHarmful").getAsDouble());
            if (config.has("sacrificeSetBonus")) TaczCuriosConfig.COMMON.sacrificeSetBonus.set(config.get("sacrificeSetBonus").getAsDouble());
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
        config.addProperty("heavenFireApocalypseDelayDuration", TaczCuriosConfig.COMMON.heavenFireApocalypseDelayDuration.get());
        
        // 虚数侵染效果配置
        config.addProperty("imaginaryInfectionAmpPerLevel", TaczCuriosConfig.COMMON.imaginaryInfectionAmpPerLevel.get());
        config.addProperty("imaginaryInfectionMaxLevel", TaczCuriosConfig.COMMON.imaginaryInfectionMaxLevel.get());
        config.addProperty("imaginaryInfectionDuration", TaczCuriosConfig.COMMON.imaginaryInfectionDuration.get());
        config.addProperty("imaginaryInfectionResistanceReduction", TaczCuriosConfig.COMMON.imaginaryInfectionResistanceReduction.get());
        config.addProperty("judgmentImaginaryInfectionMaxLevel", TaczCuriosConfig.COMMON.judgmentImaginaryInfectionMaxLevel.get());
        config.addProperty("apocalypseImaginaryInfectionMaxLevel", TaczCuriosConfig.COMMON.apocalypseImaginaryInfectionMaxLevel.get());
        config.addProperty("endlessImaginaryInfectionMaxLevel", TaczCuriosConfig.COMMON.endlessImaginaryInfectionMaxLevel.get());
        
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
        config.addProperty("summerBeachELCurseReduction", TaczCuriosConfig.COMMON.summerBeachELCurseReduction.get());
        config.addProperty("summerBeachObtainEntity", TaczCuriosConfig.COMMON.summerBeachObtainEntity.get());
        {
            JsonArray outerArr = new JsonArray();
            for (java.util.List<String> inner : TaczCuriosConfig.COMMON.summerBeachEvolutionRequirements.get()) {
                JsonArray innerArr = new JsonArray();
                for (String s : inner) innerArr.add(s);
                outerArr.add(innerArr);
            }
            config.add("summerBeachEvolutionRequirements", outerArr);
        }
        config.addProperty("summerBeachMaxKillResistance", TaczCuriosConfig.COMMON.summerBeachMaxKillResistance.get());
        config.addProperty("summerBeachBaseResistance", TaczCuriosConfig.COMMON.summerBeachBaseResistance.get());
        {
            JsonArray outerArr = new JsonArray();
            for (java.util.List<String> inner : TaczCuriosConfig.COMMON.summerBeachResistanceEntities.get()) {
                JsonArray innerArr = new JsonArray();
                for (String s : inner) innerArr.add(s);
                outerArr.add(innerArr);
            }
            config.add("summerBeachResistanceEntities", outerArr);
        }
        
        // 梵天百兽配置
        config.addProperty("brahmaBeastsELCurseReduction", TaczCuriosConfig.COMMON.brahmaBeastsELCurseReduction.get());
        {
            JsonArray outerArr = new JsonArray();
            for (java.util.List<String> inner : TaczCuriosConfig.COMMON.brahmaBeastsEvolutionRequirements.get()) {
                JsonArray innerArr = new JsonArray();
                for (String s : inner) innerArr.add(s);
                outerArr.add(innerArr);
            }
            config.add("brahmaBeastsEvolutionRequirements", outerArr);
        }
        config.addProperty("brahmaBeastsMaxKillResistance", TaczCuriosConfig.COMMON.brahmaBeastsMaxKillResistance.get());
        config.addProperty("brahmaBeastsBaseResistance", TaczCuriosConfig.COMMON.brahmaBeastsBaseResistance.get());
        {
            JsonArray outerArr = new JsonArray();
            for (java.util.List<String> inner : TaczCuriosConfig.COMMON.brahmaBeastsResistanceEntities.get()) {
                JsonArray innerArr = new JsonArray();
                for (String s : inner) innerArr.add(s);
                outerArr.add(innerArr);
            }
            config.add("brahmaBeastsResistanceEntities", outerArr);
        }
        
        // 救世配置
        config.addProperty("salvationELCurseReduction", TaczCuriosConfig.COMMON.salvationELCurseReduction.get());
        config.addProperty("salvationDamageReduction", TaczCuriosConfig.COMMON.salvationDamageReduction.get());
        config.addProperty("salvationResistanceLevel", TaczCuriosConfig.COMMON.salvationResistanceLevel.get());
        
        // 无烬终焉配置
        config.addProperty("endlessDamageBoost", TaczCuriosConfig.COMMON.endlessDamageBoost.get());
        config.addProperty("endlessExplosionDamage", TaczCuriosConfig.COMMON.endlessExplosionDamage.get());
        config.addProperty("endlessNearbyPlayerDamageBoost", TaczCuriosConfig.COMMON.endlessNearbyPlayerDamageBoost.get());
        config.addProperty("endlessNearbyPlayerPotionAmplifier", TaczCuriosConfig.COMMON.endlessNearbyPlayerPotionAmplifier.get());
        config.addProperty("endlessNearbyPlayerDuration", TaczCuriosConfig.COMMON.endlessNearbyPlayerDuration.get());
        config.addProperty("endlessNearbyPlayerRadius", TaczCuriosConfig.COMMON.endlessNearbyPlayerRadius.get());
        {
            JsonArray arr = new JsonArray();
            for (String s : TaczCuriosConfig.COMMON.endlessGunTypes.get()) arr.add(s);
            config.add("endlessGunTypes", arr);
        }
        
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
        
        // ========== 新增33个饰品配置同步 ==========
        config.addProperty("criticalDelayCritChanceBoost", TaczCuriosConfig.COMMON.criticalDelayCritChanceBoost.get());
        config.addProperty("criticalDelayFireRateReduction", TaczCuriosConfig.COMMON.criticalDelayFireRateReduction.get());
        config.addProperty("lethalCritCritChance", TaczCuriosConfig.COMMON.lethalCritCritChance.get());
        config.addProperty("weaknessSenseCritDamage", TaczCuriosConfig.COMMON.weaknessSenseCritDamage.get());
        config.addProperty("argonScopeBaseCritChance", TaczCuriosConfig.COMMON.argonScopeBaseCritChance.get());
        config.addProperty("argonScopeCritChancePerLevel", TaczCuriosConfig.COMMON.argonScopeCritChancePerLevel.get());
        config.addProperty("argonScopeDuration", TaczCuriosConfig.COMMON.argonScopeDuration.get());
        config.addProperty("gildedArgonScopeBaseCritChance", TaczCuriosConfig.COMMON.gildedArgonScopeBaseCritChance.get());
        config.addProperty("gildedArgonScopeCritChancePerLevel", TaczCuriosConfig.COMMON.gildedArgonScopeCritChancePerLevel.get());
        config.addProperty("gildedArgonScopeHeadshotKillExtra", TaczCuriosConfig.COMMON.gildedArgonScopeHeadshotKillExtra.get());
        config.addProperty("gildedArgonScopeDuration", TaczCuriosConfig.COMMON.gildedArgonScopeDuration.get());
        config.addProperty("gildedArgonScopeMaxStacks", TaczCuriosConfig.COMMON.gildedArgonScopeMaxStacks.get());
        config.addProperty("sharpBulletBaseCritDamage", TaczCuriosConfig.COMMON.sharpBulletBaseCritDamage.get());
        config.addProperty("sharpBulletCritDamagePerLevel", TaczCuriosConfig.COMMON.sharpBulletCritDamagePerLevel.get());
        config.addProperty("sharpBulletDuration", TaczCuriosConfig.COMMON.sharpBulletDuration.get());
        config.addProperty("gildedRifleAptitudePerHarmful", TaczCuriosConfig.COMMON.gildedRifleAptitudePerHarmful.get());
        config.addProperty("gildedRifleAptitudeDuration", TaczCuriosConfig.COMMON.gildedRifleAptitudeDuration.get());
        config.addProperty("gildedRifleAptitudeMaxStacks", TaczCuriosConfig.COMMON.gildedRifleAptitudeMaxStacks.get());
        config.addProperty("gildedSplitChamberBulletCountBase", TaczCuriosConfig.COMMON.gildedSplitChamberBulletCountBase.get());
        config.addProperty("gildedSplitChamberBulletCountPerLevel", TaczCuriosConfig.COMMON.gildedSplitChamberBulletCountPerLevel.get());
        config.addProperty("gildedSplitChamberDuration", TaczCuriosConfig.COMMON.gildedSplitChamberDuration.get());
        config.addProperty("gildedSplitChamberMaxStacks", TaczCuriosConfig.COMMON.gildedSplitChamberMaxStacks.get());
        config.addProperty("destructionCritDamage", TaczCuriosConfig.COMMON.destructionCritDamage.get());
        config.addProperty("destructionPrimeCritDamage", TaczCuriosConfig.COMMON.destructionPrimeCritDamage.get());
        config.addProperty("thunderBarrelCritChance", TaczCuriosConfig.COMMON.thunderBarrelCritChance.get());
        config.addProperty("thunderBarrelPrimeCritChance", TaczCuriosConfig.COMMON.thunderBarrelPrimeCritChance.get());
        config.addProperty("laserScopeBaseCritChance", TaczCuriosConfig.COMMON.laserScopeBaseCritChance.get());
        config.addProperty("laserScopeCritChancePerLevel", TaczCuriosConfig.COMMON.laserScopeCritChancePerLevel.get());
        config.addProperty("laserScopeDuration", TaczCuriosConfig.COMMON.laserScopeDuration.get());
        config.addProperty("fragmentShotBaseCritDamage", TaczCuriosConfig.COMMON.fragmentShotBaseCritDamage.get());
        config.addProperty("fragmentShotCritDamagePerLevel", TaczCuriosConfig.COMMON.fragmentShotCritDamagePerLevel.get());
        config.addProperty("fragmentShotDuration", TaczCuriosConfig.COMMON.fragmentShotDuration.get());
        config.addProperty("gildedShotgunSavvyPerHarmful", TaczCuriosConfig.COMMON.gildedShotgunSavvyPerHarmful.get());
        config.addProperty("gildedShotgunSavvyDuration", TaczCuriosConfig.COMMON.gildedShotgunSavvyDuration.get());
        config.addProperty("gildedShotgunSavvyMaxStacks", TaczCuriosConfig.COMMON.gildedShotgunSavvyMaxStacks.get());
        config.addProperty("gildedInfernalChamberBulletCountBase", TaczCuriosConfig.COMMON.gildedInfernalChamberBulletCountBase.get());
        config.addProperty("gildedInfernalChamberBulletCountPerLevel", TaczCuriosConfig.COMMON.gildedInfernalChamberBulletCountPerLevel.get());
        config.addProperty("gildedInfernalChamberDuration", TaczCuriosConfig.COMMON.gildedInfernalChamberDuration.get());
        config.addProperty("gildedInfernalChamberMaxStacks", TaczCuriosConfig.COMMON.gildedInfernalChamberMaxStacks.get());
        config.addProperty("weaknessMasteryCritDamage", TaczCuriosConfig.COMMON.weaknessMasteryCritDamage.get());
        config.addProperty("weaknessMasteryPrimeCritDamage", TaczCuriosConfig.COMMON.weaknessMasteryPrimeCritDamage.get());
        config.addProperty("hollowPointCritDamage", TaczCuriosConfig.COMMON.hollowPointCritDamage.get());
        config.addProperty("hollowPointPistolDamageReduction", TaczCuriosConfig.COMMON.hollowPointPistolDamageReduction.get());
        config.addProperty("pistolMasteryCritChance", TaczCuriosConfig.COMMON.pistolMasteryCritChance.get());
        config.addProperty("pistolMasteryPrimeCritChance", TaczCuriosConfig.COMMON.pistolMasteryPrimeCritChance.get());
        config.addProperty("hydraulicCrosshairBaseCritChance", TaczCuriosConfig.COMMON.hydraulicCrosshairBaseCritChance.get());
        config.addProperty("hydraulicCrosshairCritChancePerLevel", TaczCuriosConfig.COMMON.hydraulicCrosshairCritChancePerLevel.get());
        config.addProperty("hydraulicCrosshairDuration", TaczCuriosConfig.COMMON.hydraulicCrosshairDuration.get());
        config.addProperty("gildedHydraulicCrosshairBaseCritChance", TaczCuriosConfig.COMMON.gildedHydraulicCrosshairBaseCritChance.get());
        config.addProperty("gildedHydraulicCrosshairCritChancePerLevel", TaczCuriosConfig.COMMON.gildedHydraulicCrosshairCritChancePerLevel.get());
        config.addProperty("gildedHydraulicCrosshairHeadshotKillExtra", TaczCuriosConfig.COMMON.gildedHydraulicCrosshairHeadshotKillExtra.get());
        config.addProperty("gildedHydraulicCrosshairDuration", TaczCuriosConfig.COMMON.gildedHydraulicCrosshairDuration.get());
        config.addProperty("gildedHydraulicCrosshairMaxStacks", TaczCuriosConfig.COMMON.gildedHydraulicCrosshairMaxStacks.get());
        config.addProperty("sharpAmmoBaseCritDamage", TaczCuriosConfig.COMMON.sharpAmmoBaseCritDamage.get());
        config.addProperty("sharpAmmoCritDamagePerLevel", TaczCuriosConfig.COMMON.sharpAmmoCritDamagePerLevel.get());
        config.addProperty("sharpAmmoDuration", TaczCuriosConfig.COMMON.sharpAmmoDuration.get());
        config.addProperty("gildedMarksmanPerHarmful", TaczCuriosConfig.COMMON.gildedMarksmanPerHarmful.get());
        config.addProperty("gildedMarksmanDuration", TaczCuriosConfig.COMMON.gildedMarksmanDuration.get());
        config.addProperty("gildedMarksmanMaxStacks", TaczCuriosConfig.COMMON.gildedMarksmanMaxStacks.get());
        config.addProperty("gildedBulletSpreadBulletCountBase", TaczCuriosConfig.COMMON.gildedBulletSpreadBulletCountBase.get());
        config.addProperty("gildedBulletSpreadBulletCountPerLevel", TaczCuriosConfig.COMMON.gildedBulletSpreadBulletCountPerLevel.get());
        config.addProperty("gildedBulletSpreadDuration", TaczCuriosConfig.COMMON.gildedBulletSpreadDuration.get());
        config.addProperty("gildedBulletSpreadMaxStacks", TaczCuriosConfig.COMMON.gildedBulletSpreadMaxStacks.get());
        config.addProperty("steelSlashCritChance", TaczCuriosConfig.COMMON.steelSlashCritChance.get());
        config.addProperty("dismembermentCritDamage", TaczCuriosConfig.COMMON.dismembermentCritDamage.get());
        config.addProperty("sacrificeOppressionMeleeDamage", TaczCuriosConfig.COMMON.sacrificeOppressionMeleeDamage.get());
        config.addProperty("sacrificeSteelCritChance", TaczCuriosConfig.COMMON.sacrificeSteelCritChance.get());
        config.addProperty("gildedSteelSlashCritChanceBase", TaczCuriosConfig.COMMON.gildedSteelSlashCritChanceBase.get());
        config.addProperty("gildedSteelSlashCritDamagePerLevel", TaczCuriosConfig.COMMON.gildedSteelSlashCritDamagePerLevel.get());
        config.addProperty("gildedSteelSlashDuration", TaczCuriosConfig.COMMON.gildedSteelSlashDuration.get());
        config.addProperty("gildedSteelSlashMaxStacks", TaczCuriosConfig.COMMON.gildedSteelSlashMaxStacks.get());
        config.addProperty("conditionOverloadPerHarmful", TaczCuriosConfig.COMMON.conditionOverloadPerHarmful.get());
        config.addProperty("sacrificeSetBonus", TaczCuriosConfig.COMMON.sacrificeSetBonus.get());
        
        return new ConfigSyncPacket(GSON.toJson(config));
    }
}
