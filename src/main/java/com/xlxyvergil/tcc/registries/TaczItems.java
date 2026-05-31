package com.xlxyvergil.tcc.registries;

import com.xlxyvergil.tcc.items.AlloyDrill;
import com.xlxyvergil.tcc.items.BlazeStorm;
import com.xlxyvergil.tcc.items.BlazeStormPrime;
import com.xlxyvergil.tcc.items.BulletSpread;
import com.xlxyvergil.tcc.items.CarefulHeart;
import com.xlxyvergil.tcc.items.Chamber;
import com.xlxyvergil.tcc.items.ChamberPrime;
import com.xlxyvergil.tcc.items.CloseCombatPrime;
import com.xlxyvergil.tcc.items.CloseRangeShot;
import com.xlxyvergil.tcc.items.CoreFusion;
import com.xlxyvergil.tcc.items.CorruptMagazine;
import com.xlxyvergil.tcc.items.DeadlySurge;
import com.xlxyvergil.tcc.items.DespicableAcceleration;
import com.xlxyvergil.tcc.items.EvilAccuracy;
import com.xlxyvergil.tcc.items.FerociousExtension;
import com.xlxyvergil.tcc.items.HeavenFireApocalypse;
import com.xlxyvergil.tcc.items.HeavenFireJudgment;
import com.xlxyvergil.tcc.items.HeavyCaliberTag;
import com.xlxyvergil.tcc.items.HeavyFirepower;
import com.xlxyvergil.tcc.items.InfectedMagazine;
import com.xlxyvergil.tcc.items.InfernalChamber;
import com.xlxyvergil.tcc.items.KikakuIchijin;
import com.xlxyvergil.tcc.items.LimitSpeed;
import com.xlxyvergil.tcc.items.BurstReload;
import com.xlxyvergil.tcc.items.MalignantSpread;
import com.xlxyvergil.tcc.items.MergedRifling;
import com.xlxyvergil.tcc.items.OverloadedMagazine;
import com.xlxyvergil.tcc.items.ProphecyPact;
import com.xlxyvergil.tcc.items.RedMovementTag;
import com.xlxyvergil.tcc.items.Rifling;
import com.xlxyvergil.tcc.items.RiftSilver;
import com.xlxyvergil.tcc.items.RippingPrime;
import com.xlxyvergil.tcc.items.SoldierBasicTag;
import com.xlxyvergil.tcc.items.SoldierSpecificTag;
import com.xlxyvergil.tcc.items.SplitChamber;
import com.xlxyvergil.tcc.items.OppressionPoint;
import com.xlxyvergil.tcc.items.OppressionPointPrime;
import com.xlxyvergil.tcc.items.SustainedFire;
import com.xlxyvergil.tcc.items.SwordWind;
import com.xlxyvergil.tcc.items.SwordWindPrime;
import com.xlxyvergil.tcc.items.TacticalReload;
import com.xlxyvergil.tcc.items.UralWolfTag;
import com.xlxyvergil.tcc.items.WaspStinger;
import com.xlxyvergil.tcc.items.DepletedReload;
import com.xlxyvergil.tcc.items.BurstReloadPrime;
import com.xlxyvergil.tcc.items.TacticalReloadPrime;
import com.xlxyvergil.tcc.items.ShotgunExpansionPrime;
import com.xlxyvergil.tcc.items.MagazineBoostPrime;
import com.xlxyvergil.tcc.items.TandemMagazinePrime;
import com.xlxyvergil.tcc.items.ShotgunExpansion;
import com.xlxyvergil.tcc.items.MagazineBoost;
import com.xlxyvergil.tcc.items.TandemMagazine;
import com.xlxyvergil.tcc.items.SummerBeach;
import com.xlxyvergil.tcc.items.BrahmaBeasts;
import com.xlxyvergil.tcc.items.Salvation;
import com.xlxyvergil.tcc.items.HeavenFireApocalypseEndless;
import com.xlxyvergil.tcc.items.CriticalDelay;
import com.xlxyvergil.tcc.items.LethalCrit;
import com.xlxyvergil.tcc.items.WeaknessSense;
import com.xlxyvergil.tcc.items.Destruction;
import com.xlxyvergil.tcc.items.DestructionPrime;
import com.xlxyvergil.tcc.items.ThunderBarrel;
import com.xlxyvergil.tcc.items.ThunderBarrelPrime;
import com.xlxyvergil.tcc.items.WeaknessMastery;
import com.xlxyvergil.tcc.items.WeaknessMasteryPrime;
import com.xlxyvergil.tcc.items.HollowPoint;
import com.xlxyvergil.tcc.items.PistolMastery;
import com.xlxyvergil.tcc.items.PistolMasteryPrime;
import com.xlxyvergil.tcc.items.SteelSlash;
import com.xlxyvergil.tcc.items.Dismemberment;
import com.xlxyvergil.tcc.items.SacrificeOppression;
import com.xlxyvergil.tcc.items.SacrificeSteel;
import com.xlxyvergil.tcc.items.ArgonScope;
import com.xlxyvergil.tcc.items.GildedArgonScope;
import com.xlxyvergil.tcc.items.SharpBullet;
import com.xlxyvergil.tcc.items.GildedSplitChamber;
import com.xlxyvergil.tcc.items.LaserScope;
import com.xlxyvergil.tcc.items.FragmentShot;
import com.xlxyvergil.tcc.items.GildedInfernalChamber;
import com.xlxyvergil.tcc.items.HydraulicCrosshair;
import com.xlxyvergil.tcc.items.GildedHydraulicCrosshair;
import com.xlxyvergil.tcc.items.SharpAmmo;
import com.xlxyvergil.tcc.items.GildedBulletSpread;
import com.xlxyvergil.tcc.items.GildedSteelSlash;
import com.xlxyvergil.tcc.items.GildedRifleAptitude;
import com.xlxyvergil.tcc.items.GildedShotgunSavvy;
import com.xlxyvergil.tcc.items.GildedMarksman;
import com.xlxyvergil.tcc.items.ConditionOverload;
import com.xlxyvergil.tcc.items.SacrificeSetBonus;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * 直接在这里注册所有物品，避免复杂的注册系统
 */
public class TaczItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, "tcc");
    
    // 士兵基础挂牌 - 提供50%所有枪械基础伤害加成
    public static final RegistryObject<Item> SOLDIER_BASIC_TAG = ITEMS.register("soldier_basic_tag", 
        () -> new SoldierBasicTag(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.UNCOMMON)));
    
    // 士兵特定挂牌 - 提供50%特定枪械伤害加成（狙击枪）
    public static final RegistryObject<Item> SOLDIER_SPECIFIC_TAG = ITEMS.register("soldier_specific_tag", 
        () -> new SoldierSpecificTag(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.UNCOMMON)));
    
    // 重口径 - 提升165%步枪、狙击枪、冲锋枪、机枪、发射器伤害，增加55%不精准度
    public static final RegistryObject<Item> HEAVY_CALIBER_TAG = ITEMS.register("heavy_caliber_tag", 
        () -> new HeavyCaliberTag(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.RARE)));
    
    // 红-有-三 - 提供50%持枪移动速度加成
    public static final RegistryObject<Item> RED_MOVEMENT_TAG = ITEMS.register("red_movement_tag", 
        () -> new RedMovementTag(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.UNCOMMON)));
    
    // 夏日沙滩 - 提供20点虚数抗性
    public static final RegistryObject<Item> SUMMER_BEACH = ITEMS.register("summer_beach", 
        () -> new SummerBeach(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.COMMON)));
    
    // 梵天百兽 - 提供40点虚数抗性，增强天火饰品效果
    public static final RegistryObject<Item> BRAHMA_BEASTS = ITEMS.register("brahma_beasts", 
        () -> new BrahmaBeasts(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.EPIC)));
    
    // 救世 - 裂隙级饰品
    public static final RegistryObject<Item> SALVATION = ITEMS.register("salvation", 
        () -> new Salvation(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.EPIC)));
    
    // 天火劫灭·无烬终焉 - 裂隙级
    public static final RegistryObject<Item> HEAVEN_FIRE_APOCALYPSE_ENDLESS = ITEMS.register("heaven_fire_apocalypse_endless", 
        () -> new HeavenFireApocalypseEndless(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.EPIC)));
    
    // 乌拉尔银狼 - 提供150%爆头倍率加成
    public static final RegistryObject<Item> URAL_WOLF_TAG = ITEMS.register("ural_wolf_tag", 
        () -> new UralWolfTag(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.RARE)));
    
    // 天火圣裁 - 以玩家生命值50%提升伤害，造成伤害后扣除50%生命值
    public static final RegistryObject<Item> HEAVEN_FIRE_JUDGMENT = ITEMS.register("heaven_fire_judgment", 
        () -> new HeavenFireJudgment(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.RARE)));
    
    // 天火劫灭 - 以玩家生命值100%提升伤害、爆炸范围和爆炸伤害，造成伤害后扣除100%生命值
    public static final RegistryObject<Item> HEAVEN_FIRE_APOCALYPSE = ITEMS.register("heaven_fire_apocalypse", 
        () -> new HeavenFireApocalypse(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.EPIC)));
    
    // 卑劣加速 - 提升90%射击速度，但降低15%通用伤害和全部特定枪械伤害
    public static final RegistryObject<Item> DESPICABLE_ACCELERATION = ITEMS.register("despicable_acceleration", 
        () -> new DespicableAcceleration(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.EPIC)));
    
    // 并合膛线 - 提升155%通用伤害和25%持枪移动速度
    public static final RegistryObject<Item> MERGED_RIFLING = ITEMS.register("merged_rifling", 
        () -> new MergedRifling(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.UNCOMMON)));
    
    // 合金钻头 - 提升200%穿透能力
    public static final RegistryObject<Item> ALLOY_DRILL = ITEMS.register("alloy_drill", 
        () -> new AlloyDrill(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.RARE)));
    
    // 我小心海也绝非鳝类 - 提升300%发射器伤害加成，300%爆炸伤害加成，300%爆炸范围加成
    public static final RegistryObject<Item> CAREFUL_HEART = ITEMS.register("careful_heart", 
        () -> new CarefulHeart(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.EPIC)));
    
    // 烈焰风暴 - 增加24%爆炸范围（乘算），增加24%爆炸伤害（乘算）
    public static final RegistryObject<Item> BLAZE_STORM = ITEMS.register("blaze_storm", 
        () -> new BlazeStorm(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.RARE)));
    
    // 烈焰风暴Prime - 增加66%爆炸范围（乘算），增加66%爆炸伤害（乘算）
    public static final RegistryObject<Item> BLAZE_STORM_PRIME = ITEMS.register("blaze_storm_prime", 
        () -> new BlazeStormPrime(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.EPIC)));
    
    // 撕裂Prime - 增加55%射速（乘算）增加2.2穿透（加算）
    public static final RegistryObject<Item> RIPPING_PRIME = ITEMS.register("ripping_prime", 
        () -> new RippingPrime(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.EPIC)));
    
    // 抵近射击Prime - 增加165%特定枪械伤害属性（乘算）
    public static final RegistryObject<Item> CLOSE_COMBAT_PRIME = ITEMS.register("close_combat_prime", 
        () -> new CloseCombatPrime(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.EPIC)));
    
    // 极恶精准 - 降低90%后坐力，降低36%射速（都加算）
    public static final RegistryObject<Item> EVIL_ACCURACY = ITEMS.register("evil_accuracy", 
        () -> new EvilAccuracy(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.RARE)));
    
    // 极限速度 - 提高60%弹药速度（加算）
    public static final RegistryObject<Item> LIMIT_SPEED = ITEMS.register("limit_speed", 
        () -> new LimitSpeed(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.UNCOMMON)));
    
    // 凶恶延伸 - 提高120%子弹射程（乘算）
    public static final RegistryObject<Item> FEROCIOUS_EXTENSION = ITEMS.register("ferocious_extension", 
        () -> new FerociousExtension(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.UNCOMMON)));
    
    // 膛线 - 提升165%特定枪械伤害
    public static final RegistryObject<Item> RIFLING = ITEMS.register("rifling", 
        () -> new Rifling(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.RARE)));
    
    // 抵近射击 - 提升90%霰弹枪伤害
    public static final RegistryObject<Item> CLOSE_RANGE_SHOT = ITEMS.register("close_range_shot", 
        () -> new CloseRangeShot(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.RARE)));
    
    // 重装火力 - 提升165%手枪伤害，提高55%不精准度
    public static final RegistryObject<Item> HEAVY_FIREPOWER = ITEMS.register("heavy_firepower", 
        () -> new HeavyFirepower(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.RARE)));
    
    // 黄蜂蜇刺 - 提升220%手枪伤害
    public static final RegistryObject<Item> WASP_STINGER = ITEMS.register("wasp_stinger", 
        () -> new WaspStinger(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.RARE)));
    
    // 预言契约 - 提升90%手枪伤害
    public static final RegistryObject<Item> PROPHECY_PACT = ITEMS.register("prophecy_pact", 
        () -> new ProphecyPact(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.COMMON)));
    
    // 恶性扩散 - 提升165%霰弹枪伤害，提高55%不精准度
    public static final RegistryObject<Item> MALIGNANT_SPREAD = ITEMS.register("malignant_spread", 
        () -> new MalignantSpread(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.RARE)));
    
    // 膛室 - 当玩家手持狙击枪且弹夹满子弹时，第一发子弹提升40%伤害
    public static final RegistryObject<Item> CHAMBER = ITEMS.register("chamber", 
        () -> new Chamber(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.RARE)));
    
    // 膛室Prime - 当玩家手持狙击枪且弹夹满子弹时，第一发子弹提升100%伤害
    public static final RegistryObject<Item> CHAMBER_PRIME = ITEMS.register("chamber_prime", 
        () -> new ChamberPrime(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.EPIC)));
    
    // 爆发装填 - 提升30%装填速度
    public static final RegistryObject<Item> BURST_RELOAD = ITEMS.register("burst_reload", 
        () -> new BurstReload(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.COMMON)));
    
    // 腐败弹匣 - 提升66%弹匣容量，降低33%装填速度
    public static final RegistryObject<Item> CORRUPT_MAGAZINE = ITEMS.register("corrupt_magazine", 
        () -> new CorruptMagazine(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.RARE)));
    
    // 分裂膛室 - 提升90%弹头数量
    public static final RegistryObject<Item> SPLIT_CHAMBER = ITEMS.register("split_chamber", 
        () -> new SplitChamber(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.RARE)));
    
    // 战术上膛 - 提升60%装填速度（仅限霰弹枪）
    public static final RegistryObject<Item> TACTICAL_RELOAD = ITEMS.register("tactical_reload", 
        () -> new TacticalReload(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.UNCOMMON)));
    
    // 过载弹匣 - 提升60%弹匣容量，降低18%装填速度（仅限霰弹枪）
    public static final RegistryObject<Item> OVERLOADED_MAGAZINE = ITEMS.register("overloaded_magazine", 
        () -> new OverloadedMagazine(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.RARE)));
    
    // 地狱弹膛 - 提升120%弹头数量（仅限霰弹枪）
    public static final RegistryObject<Item> INFERNAL_CHAMBER = ITEMS.register("infernal_chamber", 
        () -> new InfernalChamber(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.RARE)));
    
    // 持续火力 - 提升48%装填速度（仅限手枪）
    public static final RegistryObject<Item> SUSTAINED_FIRE = ITEMS.register("sustained_fire", 
        () -> new SustainedFire(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.COMMON)));
    
    // 感染弹匣 - 提升60%弹匣容量，降低30%装填速度（仅限手枪）
    public static final RegistryObject<Item> INFECTED_MAGAZINE = ITEMS.register("infected_magazine", 
        () -> new InfectedMagazine(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.RARE)));
    
    // 致命洪流 - 提升60%射速和60%弹头数量（仅限手枪）
    public static final RegistryObject<Item> DEADLY_SURGE = ITEMS.register("deadly_surge", 
        () -> new DeadlySurge(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.RARE)));
    
    // 弹头扩散 - 提升120%弹头数量（仅限手枪）
    public static final RegistryObject<Item> BULLET_SPREAD = ITEMS.register("bullet_spread", 
        () -> new BulletSpread(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.RARE)));
    
    // 内融核心 - 通过熔炉燃烧饰品获得
    public static final RegistryObject<Item> CORE_FUSION = ITEMS.register("core_fusion", 
        () -> new CoreFusion(new Item.Properties()));
    
    // 裂隙碎银 - 用于随机抽取饰品
    public static final RegistryObject<Item> RIFT_SILVER = ITEMS.register("rift_silver", 
        () -> new RiftSilver(new Item.Properties()));
    
    // 压迫点 - 提升120%近战伤害
    public static final RegistryObject<Item> OPPRESSION_POINT = ITEMS.register("oppression_point", 
        () -> new OppressionPoint(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.COMMON)));
    
    // 压迫点Prime - 提升165%近战伤害
    public static final RegistryObject<Item> OPPRESSION_POINT_PRIME = ITEMS.register("oppression_point_prime", 
        () -> new OppressionPointPrime(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.EPIC)));
    
    // 剑风 - 提升1.1近战距离
    public static final RegistryObject<Item> SWORD_WIND = ITEMS.register("sword_wind", 
        () -> new SwordWind(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.COMMON)));
    
    // 剑风Prime - 提升3近战距离
    public static final RegistryObject<Item> SWORD_WIND_PRIME = ITEMS.register("sword_wind_prime", 
        () -> new SwordWindPrime(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.EPIC)));
    
    // 耗竭装填 - 降低60%弹匣容量，提升48%装填速度（仅限狙击枪）
    public static final RegistryObject<Item> DEPLETED_RELOAD = ITEMS.register("depleted_reload", 
        () -> new DepletedReload(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.RARE)));
    
    // 爆发装填Prime - 提升55%装填速度（仅限步枪、狙击枪、冲锋枪、机枪、发射器）
    public static final RegistryObject<Item> BURST_RELOAD_PRIME = ITEMS.register("burst_reload_prime", 
        () -> new BurstReloadPrime(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.EPIC)));
    
    // 战术上膛Prime - 提升100%装填速度（仅限霰弹枪）
    public static final RegistryObject<Item> TACTICAL_RELOAD_PRIME = ITEMS.register("tactical_reload_prime", 
        () -> new TacticalReloadPrime(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.EPIC)));
    
    // 霰弹扩充Prime - 提升110%弹匣容量（仅限霰弹枪）
    public static final RegistryObject<Item> SHOTGUN_EXPANSION_PRIME = ITEMS.register("shotgun_expansion_prime", 
        () -> new ShotgunExpansionPrime(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.EPIC)));
    
    // 弹匣增幅Prime - 提升55%弹匣容量（仅限步枪、狙击枪、冲锋枪、机枪、发射器）
    public static final RegistryObject<Item> MAGAZINE_BOOST_PRIME = ITEMS.register("magazine_boost_prime", 
        () -> new MagazineBoostPrime(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.EPIC)));
    
    // 串联弹匣Prime - 提升55%弹匣容量（仅限手枪）
    public static final RegistryObject<Item> TANDEM_MAGAZINE_PRIME = ITEMS.register("tandem_magazine_prime", 
        () -> new TandemMagazinePrime(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.EPIC)));
    
    // 霰弹扩充 - 提升60%弹匣容量（仅限霰弹枪）
    public static final RegistryObject<Item> SHOTGUN_EXPANSION = ITEMS.register("shotgun_expansion", 
        () -> new ShotgunExpansion(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.COMMON)));
    
    // 弹匣增幅 - 提升30%弹匣容量（仅限步枪、狙击枪、冲锋枪、机枪、发射器）
    public static final RegistryObject<Item> MAGAZINE_BOOST = ITEMS.register("magazine_boost", 
        () -> new MagazineBoost(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.COMMON)));
    
    // 串联弹匣 - 提升30%弹匣容量（仅限手枪）
    public static final RegistryObject<Item> TANDEM_MAGAZINE = ITEMS.register("tandem_magazine", 
        () -> new TandemMagazine(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.COMMON)));
    
    // 掎角一阵 - 裂隙级饰品
    public static final RegistryObject<Item> KIKAKU_ICHIJIN = ITEMS.register("kikaku_ichijin", 
        () -> new KikakuIchijin(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.EPIC)));
    
    // ========== Phase 2: 16个新增常驻属性饰品 ==========
    
    // G-01 关键延迟 - 枪械通用，暴击几率+200%，射速-20%
    public static final RegistryObject<Item> CRITICAL_DELAY = ITEMS.register("critical_delay",
        () -> new CriticalDelay(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.RARE)));
    
    // R-01 致命一击 - 步枪类，暴击几率+150%
    public static final RegistryObject<Item> LETHAL_CRIT = ITEMS.register("lethal_crit",
        () -> new LethalCrit(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.COMMON)));
    
    // R-02 弱点感应 - 步枪类，暴击伤害+120%
    public static final RegistryObject<Item> WEAKNESS_SENSE = ITEMS.register("weakness_sense",
        () -> new WeaknessSense(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.RARE)));
    
    // S-01 破灭 - 霰弹枪，暴击伤害+60%
    public static final RegistryObject<Item> DESTRUCTION = ITEMS.register("destruction",
        () -> new Destruction(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.RARE)));
    
    // S-02 破灭Prime - 霰弹枪，暴击伤害+110%
    public static final RegistryObject<Item> DESTRUCTION_PRIME = ITEMS.register("destruction_prime",
        () -> new DestructionPrime(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.EPIC)));
    
    // S-03 雷筒 - 霰弹枪，暴击几率+90%
    public static final RegistryObject<Item> THUNDER_BARREL = ITEMS.register("thunder_barrel",
        () -> new ThunderBarrel(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.COMMON)));
    
    // S-04 雷筒Prime - 霰弹枪，暴击几率+165%
    public static final RegistryObject<Item> THUNDER_BARREL_PRIME = ITEMS.register("thunder_barrel_prime",
        () -> new ThunderBarrelPrime(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.EPIC)));
    
    // P-01 弱点专精 - 手枪，暴击伤害+60%
    public static final RegistryObject<Item> WEAKNESS_MASTERY = ITEMS.register("weakness_mastery",
        () -> new WeaknessMastery(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.UNCOMMON)));
    
    // P-02 弱点专精Prime - 手枪，暴击伤害+110%
    public static final RegistryObject<Item> WEAKNESS_MASTERY_PRIME = ITEMS.register("weakness_mastery_prime",
        () -> new WeaknessMasteryPrime(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.EPIC)));
    
    // P-03 空尖弹 - 手枪，暴击伤害+60%，手枪伤害-15%
    public static final RegistryObject<Item> HOLLOW_POINT = ITEMS.register("hollow_point",
        () -> new HollowPoint(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.RARE)));
    
    // P-04 手枪精通 - 手枪，暴击几率+120%
    public static final RegistryObject<Item> PISTOL_MASTERY = ITEMS.register("pistol_mastery",
        () -> new PistolMastery(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.COMMON)));
    
    // P-05 手枪精通Prime - 手枪，暴击几率+187%
    public static final RegistryObject<Item> PISTOL_MASTERY_PRIME = ITEMS.register("pistol_mastery_prime",
        () -> new PistolMasteryPrime(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.EPIC)));
    
    // M-01 斩铁 - 近战，暴击几率+120%
    public static final RegistryObject<Item> STEEL_SLASH = ITEMS.register("steel_slash",
        () -> new SteelSlash(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.COMMON)));
    
    // M-02 肢解 - 近战，暴击伤害+90%
    public static final RegistryObject<Item> DISMEMBERMENT = ITEMS.register("dismemberment",
        () -> new Dismemberment(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.COMMON)));
    
    // M-03 牺牲压迫点 - 近战，近战伤害+110%
    public static final RegistryObject<Item> SACRIFICE_OPPRESSION = ITEMS.register("sacrifice_oppression",
        () -> new SacrificeOppression(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.EPIC)));
    
    // M-04 牺牲斩铁 - 近战，暴击几率+220%
    public static final RegistryObject<Item> SACRIFICE_STEEL = ITEMS.register("sacrifice_steel",
        () -> new SacrificeSteel(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.EPIC)));

    // ========== Phase 3: 12个Buff触发饰品 ==========

    // R-03 氩晶瞄具 - 步枪，爆头触发Buff
    public static final RegistryObject<Item> ARGON_SCOPE = ITEMS.register("argon_scope",
        () -> new ArgonScope(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.RARE)));

    // R-04 镀层氩晶瞄具 - 步枪，爆头/击杀触发Buff（叠加5层）
    public static final RegistryObject<Item> GILDED_ARGON_SCOPE = ITEMS.register("gilded_argon_scope",
        () -> new GildedArgonScope(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.EPIC)));

    // R-05 尖刃弹头 - 步枪，击杀触发Buff
    public static final RegistryObject<Item> SHARP_BULLET = ITEMS.register("sharp_bullet",
        () -> new SharpBullet(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.UNCOMMON)));

    // R-07 镀层分裂膛室 - 步枪，击杀触发Buff（叠加5层）
    public static final RegistryObject<Item> GILDED_SPLIT_CHAMBER = ITEMS.register("gilded_split_chamber",
        () -> new GildedSplitChamber(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.EPIC)));

    // S-05 雷射瞄具 - 霰弹枪，爆头触发Buff
    public static final RegistryObject<Item> LASER_SCOPE = ITEMS.register("laser_scope",
        () -> new LaserScope(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.UNCOMMON)));

    // S-06 破片射击 - 霰弹枪，击杀触发Buff
    public static final RegistryObject<Item> FRAGMENT_SHOT = ITEMS.register("fragment_shot",
        () -> new FragmentShot(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.COMMON)));

    // S-08 镀层地狱弹膛 - 霰弹枪，击杀触发Buff（叠加5层）
    public static final RegistryObject<Item> GILDED_INFERNAL_CHAMBER = ITEMS.register("gilded_infernal_chamber",
        () -> new GildedInfernalChamber(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.EPIC)));

    // P-06 液压准心 - 手枪，爆头触发Buff
    public static final RegistryObject<Item> HYDRAULIC_CROSSHAIR = ITEMS.register("hydraulic_crosshair",
        () -> new HydraulicCrosshair(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.COMMON)));

    // P-07 镀层液压准心 - 手枪，爆头/击杀触发Buff（叠加5层）
    public static final RegistryObject<Item> GILDED_HYDRAULIC_CROSSHAIR = ITEMS.register("gilded_hydraulic_crosshair",
        () -> new GildedHydraulicCrosshair(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.EPIC)));

    // P-08 尖锐子弹 - 手枪，击杀触发Buff
    public static final RegistryObject<Item> SHARP_AMMO = ITEMS.register("sharp_ammo",
        () -> new SharpAmmo(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.UNCOMMON)));

    // P-10 镀层弹头扩散 - 手枪，击杀触发Buff（叠加4层）
    public static final RegistryObject<Item> GILDED_BULLET_SPREAD = ITEMS.register("gilded_bullet_spread",
        () -> new GildedBulletSpread(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.EPIC)));

    // M-05 镀层斩铁 - 近战，击杀触发Buff（叠加4层）
    public static final RegistryObject<Item> GILDED_STEEL_SLASH = ITEMS.register("gilded_steel_slash",
        () -> new GildedSteelSlash(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.EPIC)));

    // ========== Phase 4: 5个特殊饰品 ==========

    // R-06 镀层步枪才能 - 步枪，击杀触发Buff（有害效果乘算，2层）
    public static final RegistryObject<Item> GILDED_RIFLE_APTITUDE = ITEMS.register("gilded_rifle_aptitude",
        () -> new GildedRifleAptitude(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.EPIC)));

    // S-07 镀层通晓霰弹枪 - 霰弹，击杀触发Buff（有害效果乘算，2层）
    public static final RegistryObject<Item> GILDED_SHOTGUN_SAVVY = ITEMS.register("gilded_shotgun_savvy",
        () -> new GildedShotgunSavvy(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.EPIC)));

    // P-09 镀层准确射手 - 手枪，击杀触发Buff（有害效果乘算，3层）
    public static final RegistryObject<Item> GILDED_MARKSMAN = ITEMS.register("gilded_marksman",
        () -> new GildedMarksman(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.EPIC)));

    // M-06 异况超量 - 近战，目标负面效果种数增伤
    public static final RegistryObject<Item> CONDITION_OVERLOAD = ITEMS.register("condition_overload",
        () -> new ConditionOverload(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.RARE)));

    // M-07 牺牲套装组合 - 近战，牺牲压迫点+牺牲斩铁各提升25%
    public static final RegistryObject<Item> SACRIFICE_SET_BONUS = ITEMS.register("sacrifice_set_bonus",
        () -> new SacrificeSetBonus(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.EPIC)));
}