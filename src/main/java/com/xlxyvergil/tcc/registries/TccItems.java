package com.xlxyvergil.tcc.registries;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.items.curios.tcc.AlloyDrill;
import com.xlxyvergil.tcc.items.curios.tcc.BlazeStorm;
import com.xlxyvergil.tcc.items.curios.tcc.BlazeStormPrime; 
import com.xlxyvergil.tcc.items.curios.tcc.BulletSpread;
import com.xlxyvergil.tcc.items.curios.tcc.CarefulHeart;
import com.xlxyvergil.tcc.items.curios.tcc.Chamber;
import com.xlxyvergil.tcc.items.curios.tcc.ChamberPrime;
import com.xlxyvergil.tcc.items.curios.tcc.CloseCombatPrime;
import com.xlxyvergil.tcc.items.curios.tcc.CloseRangeShot;
import com.xlxyvergil.tcc.items.materials.CoreFusion;
import com.xlxyvergil.tcc.items.materials.FusionVesselItem;
import com.xlxyvergil.tcc.items.curios.tcc.CorruptMagazine;
import com.xlxyvergil.tcc.items.curios.tcc.DeadlySurge;
import com.xlxyvergil.tcc.items.curios.tcc.DespicableAcceleration;
import com.xlxyvergil.tcc.items.curios.tcc.EvilAccuracy;
import com.xlxyvergil.tcc.items.curios.tcc.FerociousExtension;
import com.xlxyvergil.tcc.items.curios.bound.HeavenFireApocalypse;
import com.xlxyvergil.tcc.items.curios.bound.HeavenFireJudgment;
import com.xlxyvergil.tcc.items.curios.tcc.HeavyCaliberTag;
import com.xlxyvergil.tcc.items.curios.tcc.HeavyFirepower;
import com.xlxyvergil.tcc.items.curios.tcc.InfectedMagazine;
import com.xlxyvergil.tcc.items.curios.tcc.InfernalChamber;
import com.xlxyvergil.tcc.items.curios.tcc.KikakuIchijin;
import com.xlxyvergil.tcc.items.curios.tcc.LimitSpeed;
import com.xlxyvergil.tcc.items.curios.tcc.BurstReload;
import com.xlxyvergil.tcc.items.curios.tcc.MalignantSpread;
import com.xlxyvergil.tcc.items.curios.tcc.MergedRifling;
import com.xlxyvergil.tcc.items.curios.tcc.OverloadedMagazine;
import com.xlxyvergil.tcc.items.curios.tcc.ProphecyPact;
import com.xlxyvergil.tcc.items.curios.tcc.RedMovementTag;
import com.xlxyvergil.tcc.items.curios.tcc.Rifling;
import com.xlxyvergil.tcc.items.materials.RiftSilver;
import com.xlxyvergil.tcc.items.curios.tcc.RippingPrime;
import com.xlxyvergil.tcc.items.curios.tcc.SoldierBasicTag;
import com.xlxyvergil.tcc.items.curios.tcc.SoldierSpecificTag;
import com.xlxyvergil.tcc.items.curios.tcc.SplitChamber;
import com.xlxyvergil.tcc.items.curios.tcc.OppressionPoint;
import com.xlxyvergil.tcc.items.curios.tcc.OppressionPointPrime;
import com.xlxyvergil.tcc.items.curios.tcc.SustainedFire;
import com.xlxyvergil.tcc.items.curios.tcc.SwordWind;
import com.xlxyvergil.tcc.items.curios.tcc.SwordWindPrime;
import com.xlxyvergil.tcc.items.curios.tcc.TacticalReload;
import com.xlxyvergil.tcc.items.curios.tcc.UralWolfTag;
import com.xlxyvergil.tcc.items.curios.tcc.WaspStinger;
import com.xlxyvergil.tcc.items.curios.tcc.DepletedReload;
import com.xlxyvergil.tcc.items.curios.tcc.BurstReloadPrime;
import com.xlxyvergil.tcc.items.curios.tcc.TacticalReloadPrime;
import com.xlxyvergil.tcc.items.curios.tcc.ShotgunExpansionPrime;
import com.xlxyvergil.tcc.items.curios.tcc.MagazineBoostPrime;
import com.xlxyvergil.tcc.items.curios.tcc.MagazineBoost;
import com.xlxyvergil.tcc.items.curios.tcc.TandemMagazinePrime;
import com.xlxyvergil.tcc.items.curios.tcc.ShotgunExpansion;
import com.xlxyvergil.tcc.items.curios.tcc.TandemMagazine;
import com.xlxyvergil.tcc.items.curios.bound.Su;
import com.xlxyvergil.tcc.items.curios.bound.SummerBeach;
import com.xlxyvergil.tcc.items.curios.bound.BrahmaBeasts;
import com.xlxyvergil.tcc.items.curios.bound.Salvation;
import com.xlxyvergil.tcc.items.curios.bound.HeavenFireApocalypseEndless;
import com.xlxyvergil.tcc.items.curios.tcc.CriticalDelay;
import com.xlxyvergil.tcc.items.curios.tcc.LethalCrit;
import com.xlxyvergil.tcc.items.curios.tcc.WeaknessSense;
import com.xlxyvergil.tcc.items.curios.tcc.Destruction;
import com.xlxyvergil.tcc.items.curios.tcc.DestructionPrime;
import com.xlxyvergil.tcc.items.curios.tcc.ThunderBarrel;
import com.xlxyvergil.tcc.items.curios.tcc.ThunderBarrelPrime;
import com.xlxyvergil.tcc.items.curios.tcc.WeaknessMastery;
import com.xlxyvergil.tcc.items.curios.tcc.WeaknessMasteryPrime;
import com.xlxyvergil.tcc.items.curios.tcc.HollowPoint;
import com.xlxyvergil.tcc.items.curios.tcc.PistolMastery;
import com.xlxyvergil.tcc.items.curios.tcc.PistolMasteryPrime;
import com.xlxyvergil.tcc.items.curios.tcc.SteelSlash;
import com.xlxyvergil.tcc.items.curios.tcc.Dismemberment;
import com.xlxyvergil.tcc.items.curios.tcc.SacrificeOppression;
import com.xlxyvergil.tcc.items.curios.tcc.SacrificeSteel;
import com.xlxyvergil.tcc.items.curios.tcc.ArgonScope;
import com.xlxyvergil.tcc.items.curios.tcc.GildedArgonScope;
import com.xlxyvergil.tcc.items.curios.tcc.SharpBullet;
import com.xlxyvergil.tcc.items.curios.tcc.GildedSplitChamber;
import com.xlxyvergil.tcc.items.curios.tcc.LaserScope;
import com.xlxyvergil.tcc.items.curios.tcc.FragmentShot;
import com.xlxyvergil.tcc.items.curios.tcc.GildedInfernalChamber;
import com.xlxyvergil.tcc.items.curios.tcc.HydraulicCrosshair;
import com.xlxyvergil.tcc.items.curios.tcc.GildedHydraulicCrosshair;
import com.xlxyvergil.tcc.items.curios.tcc.SharpAmmo;
import com.xlxyvergil.tcc.items.curios.tcc.GildedBulletSpread;
import com.xlxyvergil.tcc.items.curios.tcc.GildedSteelSlash;
import com.xlxyvergil.tcc.items.curios.tcc.GildedRifleAptitude;
import com.xlxyvergil.tcc.items.curios.tcc.GildedShotgunSavvy;
import com.xlxyvergil.tcc.items.curios.tcc.GildedMarksman;
import com.xlxyvergil.tcc.items.curios.tcc.ConditionOverload;
import com.xlxyvergil.tcc.items.curios.bound.AoMie;
import com.xlxyvergil.tcc.items.curios.bound.Aponia;
import com.xlxyvergil.tcc.items.curios.bound.BushiShiwu;
import com.xlxyvergil.tcc.items.curios.bound.CuiyaoZhiGe;
import com.xlxyvergil.tcc.items.curios.bound.DizangYuhun;
import com.xlxyvergil.tcc.items.curios.bound.DuchenZhiYu;
import com.xlxyvergil.tcc.items.curios.bound.Eden;
import com.xlxyvergil.tcc.items.curios.bound.DominanceKey;
import com.xlxyvergil.tcc.items.curios.bound.EdenStar;
import com.xlxyvergil.tcc.items.curios.bound.FanchenNandu;
import com.xlxyvergil.tcc.items.curios.bound.Fusheng;
import com.xlxyvergil.tcc.items.curios.bound.HeiyuanBaihua;
import com.xlxyvergil.tcc.items.curios.bound.Hua;
import com.xlxyvergil.tcc.items.curios.bound.Huangjin;
import com.xlxyvergil.tcc.items.curios.bound.Jielv;
import com.xlxyvergil.tcc.items.curios.bound.Fanxing;
import com.xlxyvergil.tcc.items.curios.bound.Griseo;
import com.xlxyvergil.tcc.items.curios.bound.WangshiDeKuqiu;
import com.xlxyvergil.tcc.items.curios.bound.Kongmeng;
import com.xlxyvergil.tcc.items.curios.bound.Kosma;
import com.xlxyvergil.tcc.items.curios.bound.LaZhiYan;
import com.xlxyvergil.tcc.items.curios.bound.LimingZhiShao;
import com.xlxyvergil.tcc.items.curios.bound.LuejiZhiShou;
import com.xlxyvergil.tcc.items.curios.bound.HuajieZhiyan;
import com.xlxyvergil.tcc.items.curios.bound.HuishiZhijuan;
import com.xlxyvergil.tcc.items.curios.bound.IslandBoomRaven;
import com.xlxyvergil.tcc.items.curios.bound.Imer;
import com.xlxyvergil.tcc.items.curios.bound.JudgementKey;
import com.xlxyvergil.tcc.items.curios.bound.Juezhe;
import com.xlxyvergil.tcc.items.curios.bound.Kalpas;
import com.xlxyvergil.tcc.items.curios.bound.Mebius;
import com.xlxyvergil.tcc.items.curios.bound.PadoPhilipis;
import com.xlxyvergil.tcc.items.curios.bound.QidianChonggou;
import com.xlxyvergil.tcc.items.curios.bound.QinshiZhijian;
import com.xlxyvergil.tcc.items.curios.bound.ShenenJiejie;
import com.xlxyvergil.tcc.items.curios.bound.ShenzuiZhijian;
import com.xlxyvergil.tcc.items.curios.bound.Tianhui;
import com.xlxyvergil.tcc.items.curios.bound.TingzhiZhijian;
import com.xlxyvergil.tcc.items.curios.bound.Luoxuan;
import com.xlxyvergil.tcc.items.curios.bound.MetaMorph;
import com.xlxyvergil.tcc.items.curios.bound.QianjieYicheng;
import com.xlxyvergil.tcc.items.curios.bound.QishiZhijian;
import com.xlxyvergil.tcc.items.curios.bound.ShijieFanyan;
import com.xlxyvergil.tcc.items.curios.bound.VillV;
import com.xlxyvergil.tcc.items.curios.bound.WanwuXiumian;
import com.xlxyvergil.tcc.items.curios.bound.XukongWancang;
import com.xlxyvergil.tcc.items.curios.bound.XukongWancangYZTH;
import com.xlxyvergil.tcc.items.curios.bound.YinguoZhuanlun;
import com.xlxyvergil.tcc.items.curios.bound.YongjieZhijian;
import com.xlxyvergil.tcc.items.curios.bound.YuxiZhixia;
import com.xlxyvergil.tcc.items.curios.bound.Raven;
import com.xlxyvergil.tcc.items.curios.bound.SevenThunders;
import com.xlxyvergil.tcc.items.curios.bound.SevenThundersThunderSeen;
import com.xlxyvergil.tcc.items.curios.bound.Shesha;
import com.xlxyvergil.tcc.items.curios.bound.ShijieZhiShe;
import com.xlxyvergil.tcc.items.curios.bound.SiZhiYi;
import com.xlxyvergil.tcc.items.curios.bound.TuntianZhijian;
import com.xlxyvergil.tcc.items.curios.bound.WangshiDeHuanmeng;
import com.xlxyvergil.tcc.items.curios.bound.WangshiDeSheying;
import com.xlxyvergil.tcc.items.curios.bound.Wuxian;
import com.xlxyvergil.tcc.items.curios.bound.Xiora;
import com.xlxyvergil.tcc.items.curios.bound.Xuguang;
import com.xlxyvergil.tcc.items.curios.bound.YeZhiTong;
import com.xlxyvergil.tcc.items.curios.bound.WangshiDeKuqiuMingzhiqi;
import com.xlxyvergil.tcc.items.curios.bound.YuhunShixian;
import com.xlxyvergil.tcc.items.curios.bound.Yuduchen;
import com.xlxyvergil.tcc.items.curios.bound.ZhenWo;
import com.xlxyvergil.tcc.items.materials.CollapseCrystal;
import com.xlxyvergil.tcc.items.materials.JiuChanZhiYuan; 

import net.minecraft.ChatFormatting;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.registries.IForgeRegistry;


public class TccItems {

    public static final Rarity RIFT = Rarity.create("tcc_rift", ChatFormatting.LIGHT_PURPLE);
    
    
    public static final Item SOLDIER_BASIC_TAG = new SoldierBasicTag(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.UNCOMMON));
    
    
    public static final Item SOLDIER_SPECIFIC_TAG = new SoldierSpecificTag(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.EPIC));
    
    
    public static final Item HEAVY_CALIBER_TAG = new HeavyCaliberTag(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.RARE));
    
    
    public static final Item RED_MOVEMENT_TAG = new RedMovementTag(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.UNCOMMON));
    
    
    public static final Item SUMMER_BEACH = new SummerBeach(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.RARE));
    
    
    public static final Item BRAHMA_BEASTS = new BrahmaBeasts(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.EPIC));
    
    
    public static final Item SALVATION = new Salvation(new Item.Properties()
            .stacksTo(64)
            .rarity(RIFT));

    public static final Item XIORA = new Xiora(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.RARE));

    public static final Item RAVEN = new Raven(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.EPIC));

    public static final Item ISLAND_BOOM_RAVEN = new IslandBoomRaven(new Item.Properties()
            .stacksTo(64)
            .rarity(RIFT));
    
    
    public static final Item HEAVEN_FIRE_APOCALYPSE_ENDLESS = new HeavenFireApocalypseEndless(new Item.Properties()
            .stacksTo(64)
            .rarity(RIFT));

    public static final Item SEVEN_THUNDERS = new SevenThunders(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.RARE));

    public static final Item SEVEN_THUNDERS_THUNDER_SEEN = new SevenThundersThunderSeen(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.EPIC));

    public static final Item JUDGEMENT_KEY = new JudgementKey(new Item.Properties()
            .stacksTo(64)
            .rarity(RIFT));
    
    
    public static final Item URAL_WOLF_TAG = new UralWolfTag(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.UNCOMMON));
    
    
    public static final Item HEAVEN_FIRE_JUDGMENT = new HeavenFireJudgment(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.RARE));
    
    
    public static final Item HEAVEN_FIRE_APOCALYPSE = new HeavenFireApocalypse(new Item.Properties()
            .stacksTo(64)
            .rarity(RIFT));
    
    
    public static final Item DESPICABLE_ACCELERATION = new DespicableAcceleration(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.RARE));
    
    
    public static final Item MERGED_RIFLING = new MergedRifling(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.EPIC));
    
    
    public static final Item ALLOY_DRILL = new AlloyDrill(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.RARE));
    
    
    public static final Item CAREFUL_HEART = new CarefulHeart(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.EPIC));
    
    
    public static final Item BLAZE_STORM = new BlazeStorm(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.RARE));
    
    
    public static final Item BLAZE_STORM_PRIME = new BlazeStormPrime(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.EPIC));
    
    
    public static final Item RIPPING_PRIME = new RippingPrime(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.EPIC));
    
    
    public static final Item CLOSE_COMBAT_PRIME = new CloseCombatPrime(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.EPIC));
    
    
    public static final Item EVIL_ACCURACY = new EvilAccuracy(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.RARE));
    
    
    public static final Item LIMIT_SPEED = new LimitSpeed(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.UNCOMMON));
    
    
    public static final Item FEROCIOUS_EXTENSION = new FerociousExtension(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.UNCOMMON));
    
    
    public static final Item RIFLING = new Rifling(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.UNCOMMON));
    
    
    public static final Item CLOSE_RANGE_SHOT = new CloseRangeShot(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.UNCOMMON));
    
    
    public static final Item HEAVY_FIREPOWER = new HeavyFirepower(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.RARE));
    
    
    public static final Item WASP_STINGER = new WaspStinger(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.UNCOMMON));
    
    
    public static final Item PROPHECY_PACT = new ProphecyPact(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.COMMON));
    
    
    public static final Item MALIGNANT_SPREAD = new MalignantSpread(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.RARE));
    
    
    public static final Item CHAMBER = new Chamber(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.RARE));
    
    
    public static final Item CHAMBER_PRIME = new ChamberPrime(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.EPIC));
    
    
    public static final Item BURST_RELOAD = new BurstReload(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.COMMON));
    
    
    public static final Item CORRUPT_MAGAZINE = new CorruptMagazine(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.RARE));
    
    
    public static final Item SPLIT_CHAMBER = new SplitChamber(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.RARE));
    
    
    public static final Item TACTICAL_RELOAD = new TacticalReload(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.UNCOMMON));
    
    
    public static final Item OVERLOADED_MAGAZINE = new OverloadedMagazine(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.RARE));
    
    
    public static final Item INFERNAL_CHAMBER = new InfernalChamber(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.RARE));
    
    
    public static final Item SUSTAINED_FIRE = new SustainedFire(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.COMMON));
    
    
    public static final Item INFECTED_MAGAZINE = new InfectedMagazine(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.RARE));
    
    
    public static final Item DEADLY_SURGE = new DeadlySurge(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.RARE));
    
    
    public static final Item BULLET_SPREAD = new BulletSpread(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.RARE));
    
    
    public static final Item CORE_FUSION = new CoreFusion(new Item.Properties());
    
    
    public static final Item RIFT_SILVER = new RiftSilver(new Item.Properties());
    
    
    public static final Item FUSION_VESSEL = new FusionVesselItem(new Item.Properties()
            .stacksTo(64));
    
    
    public static final Item OPPRESSION_POINT = new OppressionPoint(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.COMMON));
    
    
    public static final Item OPPRESSION_POINT_PRIME = new OppressionPointPrime(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.EPIC));
    
    
    public static final Item SWORD_WIND = new SwordWind(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.COMMON));
    
    
    public static final Item SWORD_WIND_PRIME = new SwordWindPrime(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.EPIC));
    
    
    public static final Item DEPLETED_RELOAD = new DepletedReload(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.RARE));
    
    
    public static final Item BURST_RELOAD_PRIME = new BurstReloadPrime(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.EPIC));
    
    
    public static final Item TACTICAL_RELOAD_PRIME = new TacticalReloadPrime(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.EPIC));
    
    
    public static final Item SHOTGUN_EXPANSION_PRIME = new ShotgunExpansionPrime(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.EPIC));
    
    
    public static final Item MAGAZINE_BOOST_PRIME = new MagazineBoostPrime(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.EPIC));
    
    
    public static final Item TANDEM_MAGAZINE_PRIME = new TandemMagazinePrime(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.EPIC));
    
    
    public static final Item SHOTGUN_EXPANSION = new ShotgunExpansion(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.COMMON));
    
    
    public static final Item MAGAZINE_BOOST = new MagazineBoost(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.COMMON));
    
    
    public static final Item TANDEM_MAGAZINE = new TandemMagazine(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.COMMON));
    
    
    public static final Item KIKAKU_ICHIJIN = new KikakuIchijin(new Item.Properties()
            .stacksTo(64)
            .rarity(RIFT));
    
    
    
    
    public static final Item CRITICAL_DELAY = new CriticalDelay(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.RARE));
    
    
    public static final Item LETHAL_CRIT = new LethalCrit(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.COMMON));
    
    
    public static final Item WEAKNESS_SENSE = new WeaknessSense(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.RARE));
    
    
    public static final Item DESTRUCTION = new Destruction(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.RARE));
    
    
    public static final Item DESTRUCTION_PRIME = new DestructionPrime(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.EPIC));
    
    
    public static final Item THUNDER_BARREL = new ThunderBarrel(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.COMMON));
    
    
    public static final Item THUNDER_BARREL_PRIME = new ThunderBarrelPrime(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.EPIC));
    
    
    public static final Item WEAKNESS_MASTERY = new WeaknessMastery(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.UNCOMMON));
    
    
    public static final Item WEAKNESS_MASTERY_PRIME = new WeaknessMasteryPrime(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.EPIC));
    
    
    public static final Item HOLLOW_POINT = new HollowPoint(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.RARE));
    
    
    public static final Item PISTOL_MASTERY = new PistolMastery(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.COMMON));
    
    
    public static final Item PISTOL_MASTERY_PRIME = new PistolMasteryPrime(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.EPIC));
    
    
    public static final Item STEEL_SLASH = new SteelSlash(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.COMMON));
    
    
    public static final Item DISMEMBERMENT = new Dismemberment(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.COMMON));
    
    
    public static final Item SACRIFICE_OPPRESSION = new SacrificeOppression(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.EPIC));
    
    
    public static final Item SACRIFICE_STEEL = new SacrificeSteel(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.EPIC));

    

    
    public static final Item ARGON_SCOPE = new ArgonScope(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.RARE));

    
    public static final Item GILDED_ARGON_SCOPE = new GildedArgonScope(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.EPIC));

    
    public static final Item SHARP_BULLET = new SharpBullet(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.UNCOMMON));

    
    public static final Item GILDED_SPLIT_CHAMBER = new GildedSplitChamber(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.EPIC));

    
    public static final Item LASER_SCOPE = new LaserScope(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.UNCOMMON));

    
    public static final Item FRAGMENT_SHOT = new FragmentShot(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.COMMON));

    
    public static final Item GILDED_INFERNAL_CHAMBER = new GildedInfernalChamber(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.EPIC));

    
    public static final Item HYDRAULIC_CROSSHAIR = new HydraulicCrosshair(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.COMMON));

    
    public static final Item GILDED_HYDRAULIC_CROSSHAIR = new GildedHydraulicCrosshair(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.EPIC));

    
    public static final Item SHARP_AMMO = new SharpAmmo(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.UNCOMMON));

    
    public static final Item GILDED_BULLET_SPREAD = new GildedBulletSpread(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.EPIC));

    
    public static final Item GILDED_STEEL_SLASH = new GildedSteelSlash(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.EPIC));

    

    
    public static final Item GILDED_RIFLE_APTITUDE = new GildedRifleAptitude(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.EPIC));

    
    public static final Item GILDED_SHOTGUN_SAVVY = new GildedShotgunSavvy(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.EPIC));

    
    public static final Item GILDED_MARKSMAN = new GildedMarksman(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.EPIC));

    
    public static final Item CONDITION_OVERLOAD = new ConditionOverload(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.RARE));

    
    public static final Item COLLAPSE_CRYSTAL = new CollapseCrystal(new Item.Properties()
            .stacksTo(64));

    
    public static final Item JIU_CHAN_ZHI_YUAN = new JiuChanZhiYuan(new Item.Properties()
            .stacksTo(64));

    

    
    public static final Item GRISEO = new Griseo(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.RARE));

    
    public static final Item QIANJIE_YICHENG = new QianjieYicheng(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.RARE));

    
    public static final Item HUISHI_ZHIJUAN = new HuishiZhijuan(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.EPIC));

    
    public static final Item YONGJIE_ZHIJIAN = new YongjieZhijian(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.EPIC));

    
    public static final Item FANXING = new Fanxing(new Item.Properties()
            .stacksTo(64)
            .rarity(RIFT));

    
    public static final Item SHIJIE_FANYAN = new ShijieFanyan(new Item.Properties()
            .stacksTo(64)
            .rarity(RIFT));

    

    
    public static final Item VILL_V = new VillV(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.RARE));

    
    public static final Item XUKONG_WANCANG = new XukongWancang(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.RARE));

    
    public static final Item YUXI_ZHIXIA = new YuxiZhixia(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.EPIC));

    
    public static final Item QISHI_ZHIJIAN = new QishiZhijian(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.EPIC));

    
    public static final Item LUOXUAN = new Luoxuan(new Item.Properties()
            .stacksTo(64)
            .rarity(RIFT));

    
    public static final Item XUKONG_WANCANG_YZTH = new XukongWancangYZTH(new Item.Properties()
            .stacksTo(64)
            .rarity(RIFT));

    

    
    public static final Item KALPAS = new Kalpas(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.RARE));

    
    public static final Item IMER = new Imer(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.RARE));

    
    public static final Item HUAJIE_ZHIYAN = new HuajieZhiyan(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.EPIC));

    
    public static final Item DOMINANCE_KEY = new DominanceKey(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.EPIC));

    
    public static final Item AOMIE = new AoMie(new Item.Properties()
            .stacksTo(64)
            .rarity(RIFT));

    
    public static final Item META_MORPH = new MetaMorph(new Item.Properties()
            .stacksTo(64)
            .rarity(RIFT));

    

    
    public static final Item SU = new Su(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.RARE));

    
    public static final Item WANWU_XIUMIAN = new WanwuXiumian(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.RARE));

    
    public static final Item JUEZHE = new Juezhe(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.EPIC));

    
    public static final Item TINGZHI_ZHIJIAN = new TingzhiZhijian(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.EPIC));

    
    public static final Item TIANHUI = new Tianhui(new Item.Properties()
            .stacksTo(64)
            .rarity(RIFT));

    
    public static final Item YINGUO_ZHUANLUN = new YinguoZhuanlun(new Item.Properties()
            .stacksTo(64)
            .rarity(RIFT));

    

    
    
    public static final Item APONIA = new Aponia(new Item.Properties()
            .stacksTo(64).rarity(Rarity.RARE));
    
    public static final Item SHENZUI_ZHIJIAN = new ShenzuiZhijian(new Item.Properties()
            .stacksTo(64).rarity(Rarity.EPIC));
    
    public static final Item JIELV = new Jielv(new Item.Properties()
            .stacksTo(64).rarity(RIFT));

    
    
    public static final Item WANGSHI_DE_KUQIU = new WangshiDeKuqiu(new Item.Properties()
            .stacksTo(64).rarity(Rarity.RARE));
    
    public static final Item WANGSHI_DE_KUQIU_MINGZHIQI = new WangshiDeKuqiuMingzhiqi(new Item.Properties()
            .stacksTo(64).rarity(Rarity.EPIC));
    
    public static final Item SHENEN_JIEJIE = new ShenenJiejie(new Item.Properties()
            .stacksTo(64).rarity(RIFT));

    
    
    public static final Item EDEN = new Eden(new Item.Properties()
            .stacksTo(64).rarity(Rarity.RARE));
    
    public static final Item CUIYAO_ZHI_GE = new CuiyaoZhiGe(new Item.Properties()
            .stacksTo(64).rarity(Rarity.EPIC));
    
    public static final Item HUANGJIN = new Huangjin(new Item.Properties()
            .stacksTo(64).rarity(RIFT));

    
    
    public static final Item EDEN_STAR = new EdenStar(new Item.Properties()
            .stacksTo(64).rarity(Rarity.RARE));
    
    public static final Item TUNTIAN_ZHIJIAN = new TuntianZhijian(new Item.Properties()
            .stacksTo(64).rarity(Rarity.EPIC));
    
    public static final Item QIDIAN_CHONGGOU = new QidianChonggou(new Item.Properties()
            .stacksTo(64).rarity(RIFT));

    
    
    public static final Item KOSMA = new Kosma(new Item.Properties()
            .stacksTo(64).rarity(Rarity.RARE));
    
    public static final Item LIMING_ZHI_SHAO = new LimingZhiShao(new Item.Properties()
            .stacksTo(64).rarity(Rarity.EPIC));
    
    public static final Item XUGUANG = new Xuguang(new Item.Properties()
            .stacksTo(64).rarity(RIFT));

    
    
    public static final Item DIZANG_YUHUN = new DizangYuhun(new Item.Properties()
            .stacksTo(64).rarity(Rarity.RARE));
    
    public static final Item QINSHI_ZHIJIAN = new QinshiZhijian(new Item.Properties()
            .stacksTo(64).rarity(Rarity.EPIC));
    
    public static final Item YUHUN_SHIXIAN = new YuhunShixian(new Item.Properties()
            .stacksTo(64).rarity(RIFT));

    
    
    public static final Item MEBIUS = new Mebius(new Item.Properties()
            .stacksTo(64).rarity(Rarity.RARE));
    
    public static final Item SHIJIE_ZHI_SHE = new ShijieZhiShe(new Item.Properties()
            .stacksTo(64).rarity(Rarity.EPIC));
    
    public static final Item WUXIAN = new Wuxian(new Item.Properties()
            .stacksTo(64).rarity(RIFT));

    
    
    public static final Item WANGSHI_DE_SHEYING = new WangshiDeSheying(new Item.Properties()
            .stacksTo(64).rarity(Rarity.RARE));
    
    public static final Item SHESHA = new Shesha(new Item.Properties()
            .stacksTo(64).rarity(RIFT));
    
    public static final Item SI_ZHI_YI = new SiZhiYi(new Item.Properties()
            .stacksTo(64).rarity(Rarity.EPIC));

    
    
    public static final Item HUA = new Hua(new Item.Properties()
            .stacksTo(64).rarity(Rarity.RARE));
    
    public static final Item DUCHEN_ZHI_YU = new DuchenZhiYu(new Item.Properties()
            .stacksTo(64).rarity(Rarity.EPIC));
    
    public static final Item FUSHENG = new Fusheng(new Item.Properties()
            .stacksTo(64).rarity(RIFT));

    
    
    public static final Item YUDUCHEN = new Yuduchen(new Item.Properties()
            .stacksTo(64).rarity(Rarity.RARE));
    
    public static final Item FANCHEN_NANDU = new FanchenNandu(new Item.Properties()
            .stacksTo(64).rarity(Rarity.EPIC));
    
    public static final Item BUSHI_SHIWU = new BushiShiwu(new Item.Properties()
            .stacksTo(64).rarity(RIFT));

    
    
    public static final Item PADO_PHILIPIS = new PadoPhilipis(new Item.Properties()
            .stacksTo(64).rarity(Rarity.RARE));
    
    public static final Item LUEJI_ZHI_SHOU = new LuejiZhiShou(new Item.Properties()
            .stacksTo(64).rarity(Rarity.EPIC));
    
    public static final Item KONGMENG = new Kongmeng(new Item.Properties()
            .stacksTo(64).rarity(RIFT));

    
    
    public static final Item WANGSHI_DE_HUANMENG = new WangshiDeHuanmeng(new Item.Properties()
            .stacksTo(64).rarity(Rarity.RARE));
    
    public static final Item LA_ZHI_YAN = new LaZhiYan(new Item.Properties()
            .stacksTo(64).rarity(RIFT));
    
    public static final Item YE_ZHI_TONG = new YeZhiTong(new Item.Properties()
            .stacksTo(64).rarity(Rarity.EPIC));

    

    
    public static final Item ZEN_WO = new ZhenWo(new Item.Properties()
            .stacksTo(64).rarity(RIFT));

    
    public static final Item HEIYUAN_BAIHUA = new HeiyuanBaihua(new Item.Properties()
            .stacksTo(64).rarity(RIFT));

    private TccItems() {}

    public static void init(IForgeRegistry<Item> registry) {
        registry.register(id("soldier_basic_tag"), SOLDIER_BASIC_TAG);
        registry.register(id("soldier_specific_tag"), SOLDIER_SPECIFIC_TAG);
        registry.register(id("heavy_caliber_tag"), HEAVY_CALIBER_TAG);
        registry.register(id("red_movement_tag"), RED_MOVEMENT_TAG);
        registry.register(id("summer_beach"), SUMMER_BEACH);
        registry.register(id("brahma_beasts"), BRAHMA_BEASTS);
        registry.register(id("salvation"), SALVATION);
        registry.register(id("xiora"), XIORA);
        registry.register(id("raven"), RAVEN);
        registry.register(id("island_boom_raven"), ISLAND_BOOM_RAVEN);
        registry.register(id("heaven_fire_apocalypse_endless"), HEAVEN_FIRE_APOCALYPSE_ENDLESS);
        registry.register(id("seven_thunders"), SEVEN_THUNDERS);
        registry.register(id("seven_thunders_thunder_seen"), SEVEN_THUNDERS_THUNDER_SEEN);
        registry.register(id("judgement_key"), JUDGEMENT_KEY);
        registry.register(id("ural_wolf_tag"), URAL_WOLF_TAG);
        registry.register(id("heaven_fire_judgment"), HEAVEN_FIRE_JUDGMENT);
        registry.register(id("heaven_fire_apocalypse"), HEAVEN_FIRE_APOCALYPSE);
        registry.register(id("despicable_acceleration"), DESPICABLE_ACCELERATION);
        registry.register(id("merged_rifling"), MERGED_RIFLING);
        registry.register(id("alloy_drill"), ALLOY_DRILL);
        registry.register(id("careful_heart"), CAREFUL_HEART);
        registry.register(id("blaze_storm"), BLAZE_STORM);
        registry.register(id("blaze_storm_prime"), BLAZE_STORM_PRIME);
        registry.register(id("ripping_prime"), RIPPING_PRIME);
        registry.register(id("close_combat_prime"), CLOSE_COMBAT_PRIME);
        registry.register(id("evil_accuracy"), EVIL_ACCURACY);
        registry.register(id("limit_speed"), LIMIT_SPEED);
        registry.register(id("ferocious_extension"), FEROCIOUS_EXTENSION);
        registry.register(id("rifling"), RIFLING);
        registry.register(id("close_range_shot"), CLOSE_RANGE_SHOT);
        registry.register(id("heavy_firepower"), HEAVY_FIREPOWER);
        registry.register(id("wasp_stinger"), WASP_STINGER);
        registry.register(id("prophecy_pact"), PROPHECY_PACT);
        registry.register(id("malignant_spread"), MALIGNANT_SPREAD);
        registry.register(id("chamber"), CHAMBER);
        registry.register(id("chamber_prime"), CHAMBER_PRIME);
        registry.register(id("burst_reload"), BURST_RELOAD);
        registry.register(id("corrupt_magazine"), CORRUPT_MAGAZINE);
        registry.register(id("split_chamber"), SPLIT_CHAMBER);
        registry.register(id("tactical_reload"), TACTICAL_RELOAD);
        registry.register(id("overloaded_magazine"), OVERLOADED_MAGAZINE);
        registry.register(id("infernal_chamber"), INFERNAL_CHAMBER);
        registry.register(id("sustained_fire"), SUSTAINED_FIRE);
        registry.register(id("infected_magazine"), INFECTED_MAGAZINE);
        registry.register(id("deadly_surge"), DEADLY_SURGE);
        registry.register(id("bullet_spread"), BULLET_SPREAD);
        registry.register(id("core_fusion"), CORE_FUSION);
        registry.register(id("rift_silver"), RIFT_SILVER);
        registry.register(id("fusion_vessel"), FUSION_VESSEL);
        registry.register(id("oppression_point"), OPPRESSION_POINT);
        registry.register(id("oppression_point_prime"), OPPRESSION_POINT_PRIME);
        registry.register(id("sword_wind"), SWORD_WIND);
        registry.register(id("sword_wind_prime"), SWORD_WIND_PRIME);
        registry.register(id("depleted_reload"), DEPLETED_RELOAD);
        registry.register(id("burst_reload_prime"), BURST_RELOAD_PRIME);
        registry.register(id("tactical_reload_prime"), TACTICAL_RELOAD_PRIME);
        registry.register(id("shotgun_expansion_prime"), SHOTGUN_EXPANSION_PRIME);
        registry.register(id("magazine_boost_prime"), MAGAZINE_BOOST_PRIME);
        registry.register(id("tandem_magazine_prime"), TANDEM_MAGAZINE_PRIME);
        registry.register(id("shotgun_expansion"), SHOTGUN_EXPANSION);
        registry.register(id("magazine_boost"), MAGAZINE_BOOST);
        registry.register(id("tandem_magazine"), TANDEM_MAGAZINE);
        registry.register(id("kikaku_ichijin"), KIKAKU_ICHIJIN);
        registry.register(id("critical_delay"), CRITICAL_DELAY);
        registry.register(id("lethal_crit"), LETHAL_CRIT);
        registry.register(id("weakness_sense"), WEAKNESS_SENSE);
        registry.register(id("destruction"), DESTRUCTION);
        registry.register(id("destruction_prime"), DESTRUCTION_PRIME);
        registry.register(id("thunder_barrel"), THUNDER_BARREL);
        registry.register(id("thunder_barrel_prime"), THUNDER_BARREL_PRIME);
        registry.register(id("weakness_mastery"), WEAKNESS_MASTERY);
        registry.register(id("weakness_mastery_prime"), WEAKNESS_MASTERY_PRIME);
        registry.register(id("hollow_point"), HOLLOW_POINT);
        registry.register(id("pistol_mastery"), PISTOL_MASTERY);
        registry.register(id("pistol_mastery_prime"), PISTOL_MASTERY_PRIME);
        registry.register(id("steel_slash"), STEEL_SLASH);
        registry.register(id("dismemberment"), DISMEMBERMENT);
        registry.register(id("sacrifice_oppression"), SACRIFICE_OPPRESSION);
        registry.register(id("sacrifice_steel"), SACRIFICE_STEEL);
        registry.register(id("argon_scope"), ARGON_SCOPE);
        registry.register(id("gilded_argon_scope"), GILDED_ARGON_SCOPE);
        registry.register(id("sharp_bullet"), SHARP_BULLET);
        registry.register(id("gilded_split_chamber"), GILDED_SPLIT_CHAMBER);
        registry.register(id("laser_scope"), LASER_SCOPE);
        registry.register(id("fragment_shot"), FRAGMENT_SHOT);
        registry.register(id("gilded_infernal_chamber"), GILDED_INFERNAL_CHAMBER);
        registry.register(id("hydraulic_crosshair"), HYDRAULIC_CROSSHAIR);
        registry.register(id("gilded_hydraulic_crosshair"), GILDED_HYDRAULIC_CROSSHAIR);
        registry.register(id("sharp_ammo"), SHARP_AMMO);
        registry.register(id("gilded_bullet_spread"), GILDED_BULLET_SPREAD);
        registry.register(id("gilded_steel_slash"), GILDED_STEEL_SLASH);
        registry.register(id("gilded_rifle_aptitude"), GILDED_RIFLE_APTITUDE);
        registry.register(id("gilded_shotgun_savvy"), GILDED_SHOTGUN_SAVVY);
        registry.register(id("gilded_marksman"), GILDED_MARKSMAN);
        registry.register(id("condition_overload"), CONDITION_OVERLOAD);
        registry.register(id("collapse_crystal"), COLLAPSE_CRYSTAL);
        registry.register(id("jiu_chan_zhi_yuan"), JIU_CHAN_ZHI_YUAN);
        registry.register(id("griseo"), GRISEO);
        registry.register(id("qianjie_yicheng"), QIANJIE_YICHENG);
        registry.register(id("huishi_zhijuan"), HUISHI_ZHIJUAN);
        registry.register(id("yongjie_zhijian"), YONGJIE_ZHIJIAN);
        registry.register(id("fanxing"), FANXING);
        registry.register(id("shijie_fanyan"), SHIJIE_FANYAN);
        registry.register(id("vill_v"), VILL_V);
        registry.register(id("xukong_wancang"), XUKONG_WANCANG);
        registry.register(id("yuxi_zhixia"), YUXI_ZHIXIA);
        registry.register(id("qishi_zhijian"), QISHI_ZHIJIAN);
        registry.register(id("luoxuan"), LUOXUAN);
        registry.register(id("xukong_wancang_yzth"), XUKONG_WANCANG_YZTH);
        registry.register(id("kalpas"), KALPAS);
        registry.register(id("imer"), IMER);
        registry.register(id("huajie_zhiyan"), HUAJIE_ZHIYAN);
        registry.register(id("dominance_key"), DOMINANCE_KEY);
        registry.register(id("aomie"), AOMIE);
        registry.register(id("meta_morph"), META_MORPH);
        registry.register(id("su"), SU);
        registry.register(id("wanwu_xiumian"), WANWU_XIUMIAN);
        registry.register(id("juezhe"), JUEZHE);
        registry.register(id("tingzhi_zhijian"), TINGZHI_ZHIJIAN);
        registry.register(id("tianhui"), TIANHUI);
        registry.register(id("yinguo_zhuanlun"), YINGUO_ZHUANLUN);
        registry.register(id("aponia"), APONIA);
        registry.register(id("shenzui_zhijian"), SHENZUI_ZHIJIAN);
        registry.register(id("jielv"), JIELV);
        registry.register(id("wangshi_de_kuqiu"), WANGSHI_DE_KUQIU);
        registry.register(id("wangshi_de_kuqiu_mingzhiqi"), WANGSHI_DE_KUQIU_MINGZHIQI);
        registry.register(id("shenen_jiejie"), SHENEN_JIEJIE);
        registry.register(id("eden"), EDEN);
        registry.register(id("cuiyao_zhi_ge"), CUIYAO_ZHI_GE);
        registry.register(id("huangjin"), HUANGJIN);
        registry.register(id("eden_star"), EDEN_STAR);
        registry.register(id("tuntian_zhijian"), TUNTIAN_ZHIJIAN);
        registry.register(id("qidian_chonggou"), QIDIAN_CHONGGOU);
        registry.register(id("kosma"), KOSMA);
        registry.register(id("liming_zhi_shao"), LIMING_ZHI_SHAO);
        registry.register(id("xuguang"), XUGUANG);
        registry.register(id("dizang_yuhun"), DIZANG_YUHUN);
        registry.register(id("qinshi_zhijian"), QINSHI_ZHIJIAN);
        registry.register(id("yuhun_shixian"), YUHUN_SHIXIAN);
        registry.register(id("mebius"), MEBIUS);
        registry.register(id("shijie_zhi_she"), SHIJIE_ZHI_SHE);
        registry.register(id("wuxian"), WUXIAN);
        registry.register(id("wangshi_de_sheying"), WANGSHI_DE_SHEYING);
        registry.register(id("shesha"), SHESHA);
        registry.register(id("si_zhi_yi"), SI_ZHI_YI);
        registry.register(id("hua"), HUA);
        registry.register(id("duchen_zhi_yu"), DUCHEN_ZHI_YU);
        registry.register(id("fusheng"), FUSHENG);
        registry.register(id("yuduchen"), YUDUCHEN);
        registry.register(id("fanchen_nandu"), FANCHEN_NANDU);
        registry.register(id("bushi_shiwu"), BUSHI_SHIWU);
        registry.register(id("pado_philipis"), PADO_PHILIPIS);
        registry.register(id("lueji_zhi_shou"), LUEJI_ZHI_SHOU);
        registry.register(id("kongmeng"), KONGMENG);
        registry.register(id("wangshi_de_huanmeng"), WANGSHI_DE_HUANMENG);
        registry.register(id("la_zhi_yan"), LA_ZHI_YAN);
        registry.register(id("ye_zhi_tong"), YE_ZHI_TONG);
        registry.register(id("zhen_wo"), ZEN_WO);
        registry.register(id("heiyuan_baihua"), HEIYUAN_BAIHUA);
    }

    private static ResourceLocation id(String path) {
        return new ResourceLocation(TaczCurios.MODID, path);
    }
}
