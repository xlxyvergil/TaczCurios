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

// 鐗╁搧鍦ㄧ被鍔犺浇鏃剁洿鎺ヤ互闈欐€佸瓧娈靛疄渚嬪寲锛屼粠鑰岄伩鍏嶅紩鍏ュ鏉傜殑娉ㄥ唽绯荤粺銆?
public class TccItems {

    public static final Rarity RIFT = Rarity.create("tcc_rift", ChatFormatting.LIGHT_PURPLE);
    
    // 澹叺鍩虹鎸傜墝 - 鎻愪緵50%鎵€鏈夋灙姊板熀纭€浼ゅ鍔犳垚
    public static final Item SOLDIER_BASIC_TAG = new SoldierBasicTag(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.UNCOMMON));
    
    // 澹叺鐗瑰畾鎸傜墝 - 鎻愪緵50%鐗瑰畾鏋浼ゅ鍔犳垚锛堢嫏鍑绘灙锛?
    public static final Item SOLDIER_SPECIFIC_TAG = new SoldierSpecificTag(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.EPIC));
    
    // 閲嶅彛寰?- 鎻愬崌165%姝ユ灙銆佺嫏鍑绘灙銆佸啿閿嬫灙銆佹満鏋€侀噸鍨嬫鍣ㄤ激瀹筹紝澧炲姞55%涓嶇簿鍑嗗害
    public static final Item HEAVY_CALIBER_TAG = new HeavyCaliberTag(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.RARE));
    
    // 绾?鏈?涓?- 鎻愪緵50%鎸佹灙绉诲姩閫熷害鍔犳垚
    public static final Item RED_MOVEMENT_TAG = new RedMovementTag(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.UNCOMMON));
    
    // 澶忔棩娌欐哗 - 鎻愪緵20鐐硅櫄鏁版姉鎬?
    public static final Item SUMMER_BEACH = new SummerBeach(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.RARE));
    
    // 姊靛ぉ鐧惧吔 - 鎻愪緵40鐐硅櫄鏁版姉鎬э紝澧炲己澶╃伀楗板搧鏁堟灉
    public static final Item BRAHMA_BEASTS = new BrahmaBeasts(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.EPIC));
    
    // 鏁戜笘 - 瑁傞殭绾чグ鍝?
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
    
    // 澶╃伀鍔伃路鏃犵儸缁堢剦 - 瑁傞殭绾?
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
    
    // 涔屾媺灏旈摱鐙?- 鎻愪緵150%鐖嗗ご鍊嶇巼鍔犳垚
    public static final Item URAL_WOLF_TAG = new UralWolfTag(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.UNCOMMON));
    
    // 澶╃伀鍦ｈ - 浠ョ帺瀹剁敓鍛藉€?0%鎻愬崌浼ゅ锛岄€犳垚浼ゅ鍚庢墸闄?0%鐢熷懡鍊?
    public static final Item HEAVEN_FIRE_JUDGMENT = new HeavenFireJudgment(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.RARE));
    
    // 澶╃伀鍔伃 - 浠ョ帺瀹剁敓鍛藉€?00%鎻愬崌浼ゅ銆佺垎鐐歌寖鍥村拰鐖嗙偢浼ゅ锛岄€犳垚浼ゅ鍚庢墸闄?00%鐢熷懡鍊?
    public static final Item HEAVEN_FIRE_APOCALYPSE = new HeavenFireApocalypse(new Item.Properties()
            .stacksTo(64)
            .rarity(RIFT));
    
    // 鍗戝姡鍔犻€?- 鎻愬崌90%灏勫嚮閫熷害锛屼絾闄嶄綆15%閫氱敤浼ゅ鍜屽叏閮ㄧ壒瀹氭灙姊颁激瀹?
    public static final Item DESPICABLE_ACCELERATION = new DespicableAcceleration(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.RARE));
    
    // 骞跺悎鑶涚嚎 - 鎻愬崌155%閫氱敤浼ゅ鍜?5%鎸佹灙绉诲姩閫熷害
    public static final Item MERGED_RIFLING = new MergedRifling(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.EPIC));
    
    // 鍚堥噾閽诲ご - 鎻愬崌200%绌块€忚兘鍔?
    public static final Item ALLOY_DRILL = new AlloyDrill(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.RARE));
    
    // 鎴戝皬蹇冩捣涔熺粷闈為碀绫?- 鎻愬崌300%閲嶅瀷姝﹀櫒浼ゅ鍔犳垚锛?00%鐖嗙偢浼ゅ鍔犳垚锛?00%鐖嗙偢鑼冨洿鍔犳垚
    public static final Item CAREFUL_HEART = new CarefulHeart(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.EPIC));
    
    // 鐑堢劙椋庢毚 - 澧炲姞24%鐖嗙偢鑼冨洿锛堜箻绠楋級锛屽鍔?4%鐖嗙偢浼ゅ锛堜箻绠楋級
    public static final Item BLAZE_STORM = new BlazeStorm(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.RARE));
    
    // 鐑堢劙椋庢毚Prime - 澧炲姞66%鐖嗙偢鑼冨洿锛堜箻绠楋級锛屽鍔?6%鐖嗙偢浼ゅ锛堜箻绠楋級
    public static final Item BLAZE_STORM_PRIME = new BlazeStormPrime(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.EPIC));
    
    // 鎾曡Prime - 澧炲姞55%灏勯€燂紙涔樼畻锛夊鍔?.2绌块€忥紙鍔犵畻锛?
    public static final Item RIPPING_PRIME = new RippingPrime(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.EPIC));
    
    // 鎶佃繎灏勫嚮Prime - 澧炲姞165%鐗瑰畾鏋浼ゅ灞炴€э紙涔樼畻锛?
    public static final Item CLOSE_COMBAT_PRIME = new CloseCombatPrime(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.EPIC));
    
    // 鏋佹伓绮惧噯 - 闄嶄綆90%鍚庡潗鍔涳紝闄嶄綆36%灏勯€燂紙閮藉姞绠楋級
    public static final Item EVIL_ACCURACY = new EvilAccuracy(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.RARE));
    
    // 鏋侀檺閫熷害 - 鎻愰珮60%寮硅嵂閫熷害锛堝姞绠楋級
    public static final Item LIMIT_SPEED = new LimitSpeed(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.UNCOMMON));
    
    // 鍑舵伓寤朵几 - 鎻愰珮120%瀛愬脊灏勭▼锛堜箻绠楋級
    public static final Item FEROCIOUS_EXTENSION = new FerociousExtension(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.UNCOMMON));
    
    // 鑶涚嚎 - 鎻愬崌165%鐗瑰畾鏋浼ゅ
    public static final Item RIFLING = new Rifling(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.UNCOMMON));
    
    // 鎶佃繎灏勫嚮 - 鎻愬崌90%闇板脊鏋激瀹?
    public static final Item CLOSE_RANGE_SHOT = new CloseRangeShot(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.UNCOMMON));
    
    // 閲嶈鐏姏 - 鎻愬崌165%鎵嬫灙浼ゅ锛屾彁楂?5%涓嶇簿鍑嗗害
    public static final Item HEAVY_FIREPOWER = new HeavyFirepower(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.RARE));
    
    // 榛勮渹铚囧埡 - 鎻愬崌220%鎵嬫灙浼ゅ
    public static final Item WASP_STINGER = new WaspStinger(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.UNCOMMON));
    
    // 棰勮█濂戠害 - 鎻愬崌90%鎵嬫灙浼ゅ
    public static final Item PROPHECY_PACT = new ProphecyPact(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.COMMON));
    
    // 鎭舵€ф墿鏁?- 鎻愬崌165%闇板脊鏋激瀹筹紝鎻愰珮55%涓嶇簿鍑嗗害
    public static final Item MALIGNANT_SPREAD = new MalignantSpread(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.RARE));
    
    // 鑶涘 - 褰撶帺瀹舵墜鎸佺嫏鍑绘灙涓斿脊澶规弧瀛愬脊鏃讹紝绗竴鍙戝瓙寮规彁鍗?0%浼ゅ
    public static final Item CHAMBER = new Chamber(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.RARE));
    
    // 鑶涘Prime - 褰撶帺瀹舵墜鎸佺嫏鍑绘灙涓斿脊澶规弧瀛愬脊鏃讹紝绗竴鍙戝瓙寮规彁鍗?00%浼ゅ
    public static final Item CHAMBER_PRIME = new ChamberPrime(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.EPIC));
    
    // 鐖嗗彂瑁呭～ - 鎻愬崌30%瑁呭～閫熷害
    public static final Item BURST_RELOAD = new BurstReload(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.COMMON));
    
    // 鑵愯触寮瑰專 - 鎻愬崌66%寮瑰專瀹归噺锛岄檷浣?3%瑁呭～閫熷害
    public static final Item CORRUPT_MAGAZINE = new CorruptMagazine(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.RARE));
    
    // 鍒嗚鑶涘 - 鎻愬崌90%寮瑰ご鏁伴噺
    public static final Item SPLIT_CHAMBER = new SplitChamber(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.RARE));
    
    // 鎴樻湳涓婅啗 - 鎻愬崌60%瑁呭～閫熷害锛堜粎闄愰湴寮规灙锛?
    public static final Item TACTICAL_RELOAD = new TacticalReload(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.UNCOMMON));
    
    // 杩囪浇寮瑰專 - 鎻愬崌60%寮瑰專瀹归噺锛岄檷浣?8%瑁呭～閫熷害锛堜粎闄愰湴寮规灙锛?
    public static final Item OVERLOADED_MAGAZINE = new OverloadedMagazine(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.RARE));
    
    // 鍦扮嫳寮硅啗 - 鎻愬崌120%寮瑰ご鏁伴噺锛堜粎闄愰湴寮规灙锛?
    public static final Item INFERNAL_CHAMBER = new InfernalChamber(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.RARE));
    
    // 鎸佺画鐏姏 - 鎻愬崌48%瑁呭～閫熷害锛堜粎闄愭墜鏋級
    public static final Item SUSTAINED_FIRE = new SustainedFire(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.COMMON));
    
    // 鎰熸煋寮瑰專 - 鎻愬崌60%寮瑰專瀹归噺锛岄檷浣?0%瑁呭～閫熷害锛堜粎闄愭墜鏋級
    public static final Item INFECTED_MAGAZINE = new InfectedMagazine(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.RARE));
    
    // 鑷村懡娲祦 - 鎻愬崌60%灏勯€熷拰60%寮瑰ご鏁伴噺锛堜粎闄愭墜鏋級
    public static final Item DEADLY_SURGE = new DeadlySurge(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.RARE));
    
    // 寮瑰ご鎵╂暎 - 鎻愬崌120%寮瑰ご鏁伴噺锛堜粎闄愭墜鏋級
    public static final Item BULLET_SPREAD = new BulletSpread(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.RARE));
    
    // 鍐呰瀺鏍稿績 - 閫氳繃鐔旂倝鐕冪儳楗板搧鑾峰緱
    public static final Item CORE_FUSION = new CoreFusion(new Item.Properties());
    
    // 瑁傞殭纰庨摱 - 鐢ㄤ簬闅忔満鎶藉彇楗板搧
    public static final Item RIFT_SILVER = new RiftSilver(new Item.Properties());
    
    // 铻嶅悎瀹瑰櫒 - 鐢ㄤ簬瀛樺偍 CoreFusion 鍜岄グ鍝佸崌绾?
    public static final Item FUSION_VESSEL = new FusionVesselItem(new Item.Properties()
            .stacksTo(64));
    
    // 鍘嬭揩鐐?- 鎻愬崌120%杩戞垬浼ゅ
    public static final Item OPPRESSION_POINT = new OppressionPoint(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.COMMON));
    
    // 鍘嬭揩鐐筆rime - 鎻愬崌165%杩戞垬浼ゅ
    public static final Item OPPRESSION_POINT_PRIME = new OppressionPointPrime(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.EPIC));
    
    // 鍓戦 - 鎻愬崌1.1杩戞垬璺濈
    public static final Item SWORD_WIND = new SwordWind(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.COMMON));
    
    // 鍓戦Prime - 鎻愬崌3杩戞垬璺濈
    public static final Item SWORD_WIND_PRIME = new SwordWindPrime(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.EPIC));
    
    // 鑰楃瑁呭～ - 闄嶄綆60%寮瑰專瀹归噺锛屾彁鍗?8%瑁呭～閫熷害锛堜粎闄愮嫏鍑绘灙锛?
    public static final Item DEPLETED_RELOAD = new DepletedReload(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.RARE));
    
    // 鐖嗗彂瑁呭～Prime - 鎻愬崌55%瑁呭～閫熷害锛堜粎闄愭鏋€佺嫏鍑绘灙銆佸啿閿嬫灙銆佹満鏋€侀噸鍨嬫鍣級
    public static final Item BURST_RELOAD_PRIME = new BurstReloadPrime(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.EPIC));
    
    // 鎴樻湳涓婅啗Prime - 鎻愬崌100%瑁呭～閫熷害锛堜粎闄愰湴寮规灙锛?
    public static final Item TACTICAL_RELOAD_PRIME = new TacticalReloadPrime(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.EPIC));
    
    // 闇板脊鎵╁厖Prime - 鎻愬崌110%寮瑰專瀹归噺锛堜粎闄愰湴寮规灙锛?
    public static final Item SHOTGUN_EXPANSION_PRIME = new ShotgunExpansionPrime(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.EPIC));
    
    // 寮瑰專澧炲箙Prime - 鎻愬崌55%寮瑰專瀹归噺锛堜粎闄愭鏋€佺嫏鍑绘灙銆佸啿閿嬫灙銆佹満鏋€侀噸鍨嬫鍣級
    public static final Item MAGAZINE_BOOST_PRIME = new MagazineBoostPrime(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.EPIC));
    
    // 涓茶仈寮瑰專Prime - 鎻愬崌55%寮瑰專瀹归噺锛堜粎闄愭墜鏋級
    public static final Item TANDEM_MAGAZINE_PRIME = new TandemMagazinePrime(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.EPIC));
    
    // 闇板脊鎵╁厖 - 鎻愬崌60%寮瑰專瀹归噺锛堜粎闄愰湴寮规灙锛?
    public static final Item SHOTGUN_EXPANSION = new ShotgunExpansion(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.COMMON));
    
    // 寮瑰專澧炲箙 - 鎻愬崌30%寮瑰專瀹归噺锛堜粎闄愭鏋€佺嫏鍑绘灙銆佸啿閿嬫灙銆佹満鏋€侀噸鍨嬫鍣級
    public static final Item MAGAZINE_BOOST = new MagazineBoost(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.COMMON));
    
    // 涓茶仈寮瑰專 - 鎻愬崌30%寮瑰專瀹归噺锛堜粎闄愭墜鏋級
    public static final Item TANDEM_MAGAZINE = new TandemMagazine(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.COMMON));
    
    // 鎺庤涓€闃?- 瑁傞殭绾чグ鍝?
    public static final Item KIKAKU_ICHIJIN = new KikakuIchijin(new Item.Properties()
            .stacksTo(64)
            .rarity(RIFT));
    
    // Phase 2: 16涓柊澧炲父椹诲睘鎬чグ鍝?
    
    // 鍏抽敭寤惰繜 - 鏋閫氱敤锛屾毚鍑诲嚑鐜?200%锛屽皠閫?20%
    public static final Item CRITICAL_DELAY = new CriticalDelay(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.RARE));
    
    // 鑷村懡涓€鍑?- 姝ユ灙绫伙紝鏆村嚮鍑犵巼+150%
    public static final Item LETHAL_CRIT = new LethalCrit(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.COMMON));
    
    // 寮辩偣鎰熷簲 - 姝ユ灙绫伙紝鏆村嚮浼ゅ+120%
    public static final Item WEAKNESS_SENSE = new WeaknessSense(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.RARE));
    
    // 鐮寸伃 - 闇板脊鏋紝鏆村嚮浼ゅ+60%
    public static final Item DESTRUCTION = new Destruction(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.RARE));
    
    // 鐮寸伃Prime - 闇板脊鏋紝鏆村嚮浼ゅ+110%
    public static final Item DESTRUCTION_PRIME = new DestructionPrime(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.EPIC));
    
    // 闆风瓛 - 闇板脊鏋紝鏆村嚮鍑犵巼+90%
    public static final Item THUNDER_BARREL = new ThunderBarrel(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.COMMON));
    
    // 闆风瓛Prime - 闇板脊鏋紝鏆村嚮鍑犵巼+165%
    public static final Item THUNDER_BARREL_PRIME = new ThunderBarrelPrime(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.EPIC));
    
    // 寮辩偣涓撶簿 - 鎵嬫灙锛屾毚鍑讳激瀹?60%
    public static final Item WEAKNESS_MASTERY = new WeaknessMastery(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.UNCOMMON));
    
    // 寮辩偣涓撶簿Prime - 鎵嬫灙锛屾毚鍑讳激瀹?110%
    public static final Item WEAKNESS_MASTERY_PRIME = new WeaknessMasteryPrime(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.EPIC));
    
    // 绌哄皷寮?- 鎵嬫灙锛屾毚鍑讳激瀹?60%锛屾墜鏋激瀹?15%
    public static final Item HOLLOW_POINT = new HollowPoint(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.RARE));
    
    // 鎵嬫灙绮鹃€?- 鎵嬫灙锛屾毚鍑诲嚑鐜?120%
    public static final Item PISTOL_MASTERY = new PistolMastery(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.COMMON));
    
    // 鎵嬫灙绮鹃€歅rime - 鎵嬫灙锛屾毚鍑诲嚑鐜?187%
    public static final Item PISTOL_MASTERY_PRIME = new PistolMasteryPrime(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.EPIC));
    
    // 鏂╅搧 - 杩戞垬锛屾毚鍑诲嚑鐜?120%
    public static final Item STEEL_SLASH = new SteelSlash(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.COMMON));
    
    // 鑲㈣В - 杩戞垬锛屾毚鍑讳激瀹?90%
    public static final Item DISMEMBERMENT = new Dismemberment(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.COMMON));
    
    // 鐗虹壊鍘嬭揩鐐?- 杩戞垬锛岃繎鎴樹激瀹?110%
    public static final Item SACRIFICE_OPPRESSION = new SacrificeOppression(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.EPIC));
    
    // 鐗虹壊鏂╅搧 - 杩戞垬锛屾毚鍑诲嚑鐜?220%
    public static final Item SACRIFICE_STEEL = new SacrificeSteel(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.EPIC));

    // Phase 3: 12涓狟uff瑙﹀彂楗板搧

    // 姘╂櫠鐬勫叿 - 姝ユ灙锛岀垎澶磋Е鍙態uff
    public static final Item ARGON_SCOPE = new ArgonScope(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.RARE));

    // 闀€灞傛癌鏅剁瀯鍏?- 姝ユ灙锛岀垎澶?鍑绘潃瑙﹀彂Buff锛堝彔鍔?灞傦級
    public static final Item GILDED_ARGON_SCOPE = new GildedArgonScope(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.EPIC));

    // 灏栧垉寮瑰ご - 姝ユ灙锛屽嚮鏉€瑙﹀彂Buff
    public static final Item SHARP_BULLET = new SharpBullet(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.UNCOMMON));

    // 闀€灞傚垎瑁傝啗瀹?- 姝ユ灙锛屽嚮鏉€瑙﹀彂Buff锛堝彔鍔?灞傦級
    public static final Item GILDED_SPLIT_CHAMBER = new GildedSplitChamber(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.EPIC));

    // 闆峰皠鐬勫叿 - 闇板脊鏋紝鐖嗗ご瑙﹀彂Buff
    public static final Item LASER_SCOPE = new LaserScope(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.UNCOMMON));

    // 鐮寸墖灏勫嚮 - 闇板脊鏋紝鍑绘潃瑙﹀彂Buff
    public static final Item FRAGMENT_SHOT = new FragmentShot(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.COMMON));

    // 闀€灞傚湴鐙卞脊鑶?- 闇板脊鏋紝鍑绘潃瑙﹀彂Buff锛堝彔鍔?灞傦級
    public static final Item GILDED_INFERNAL_CHAMBER = new GildedInfernalChamber(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.EPIC));

    // 娑插帇鍑嗗績 - 鎵嬫灙锛岀垎澶磋Е鍙態uff
    public static final Item HYDRAULIC_CROSSHAIR = new HydraulicCrosshair(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.COMMON));

    // 闀€灞傛恫鍘嬪噯蹇?- 鎵嬫灙锛岀垎澶?鍑绘潃瑙﹀彂Buff锛堝彔鍔?灞傦級
    public static final Item GILDED_HYDRAULIC_CROSSHAIR = new GildedHydraulicCrosshair(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.EPIC));

    // 灏栭攼瀛愬脊 - 鎵嬫灙锛屽嚮鏉€瑙﹀彂Buff
    public static final Item SHARP_AMMO = new SharpAmmo(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.UNCOMMON));

    // 闀€灞傚脊澶存墿鏁?- 鎵嬫灙锛屽嚮鏉€瑙﹀彂Buff锛堝彔鍔?灞傦級
    public static final Item GILDED_BULLET_SPREAD = new GildedBulletSpread(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.EPIC));

    // 闀€灞傛柀閾?- 杩戞垬锛屽嚮鏉€瑙﹀彂Buff锛堝彔鍔?灞傦級
    public static final Item GILDED_STEEL_SLASH = new GildedSteelSlash(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.EPIC));

    // Phase 4: 5涓壒娈婇グ鍝?

    // 闀€灞傛鏋墠鑳?- 姝ユ灙锛屽嚮鏉€瑙﹀彂Buff锛堟湁瀹虫晥鏋滀箻绠楋紝2灞傦級
    public static final Item GILDED_RIFLE_APTITUDE = new GildedRifleAptitude(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.EPIC));

    // 闀€灞傞€氭檽闇板脊鏋?- 闇板脊锛屽嚮鏉€瑙﹀彂Buff锛堟湁瀹虫晥鏋滀箻绠楋紝2灞傦級
    public static final Item GILDED_SHOTGUN_SAVVY = new GildedShotgunSavvy(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.EPIC));

    // 闀€灞傚噯纭皠鎵?- 鎵嬫灙锛屽嚮鏉€瑙﹀彂Buff锛堟湁瀹虫晥鏋滀箻绠楋紝3灞傦級
    public static final Item GILDED_MARKSMAN = new GildedMarksman(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.EPIC));

    // 寮傚喌瓒呴噺 - 杩戞垬锛岀洰鏍囪礋闈㈡晥鏋滅鏁板浼?
    public static final Item CONDITION_OVERLOAD = new ConditionOverload(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.RARE));

    // 宕╁潖缁撴櫠 - 鐢ㄤ簬鍗镐笅3rd鍜宼dk妲戒綅鐨勯グ鍝?
    public static final Item COLLAPSE_CRYSTAL = new CollapseCrystal(new Item.Properties()
            .stacksTo(64));

    // 绾犵紶涔嬬紭 - 涓庣涔嬮敭/閫愮伀涔嬭浘楗板搧鍚堟垚鏃剁敓鎴怤BT涓€鑷寸殑鍓湰锛屼粎娑堣€楄嚜韬?
    public static final Item JIU_CHAN_ZHI_YUAN = new JiuChanZhiYuan(new Item.Properties()
            .stacksTo(64));

    // 閫愮伀涔嬭浘/绁炰箣閿?鏂伴グ鍝?

    // 鏍艰暰淇?- 3rd妲戒綅锛岀█鏈夌骇锛屽彈浼ゅ喎鍗村熀纭€10tick锛屽叏閮ㄦ灙姊扮被鍨?
    public static final Item GRISEO = new Griseo(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.RARE));

    // 鍗冪晫涓€涔?- tdk妲戒綅锛岀█鏈夌骇锛?20骞歌繍鍊硷紝鍏ㄩ儴鏋绫诲瀷
    public static final Item QIANJIE_YICHENG = new QianjieYicheng(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.RARE));

    // 缁樹笘涔嬪嵎 - 3rd妲戒綅锛屼紶璇寸骇锛屽彈浼ゅ喎鍗村熀纭€10tick+骞歌繍缂╂斁涓婇檺40tick锛屽叏閮ㄦ灙姊扮被鍨?
    public static final Item HUISHI_ZHIJUAN = new HuishiZhijuan(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.EPIC));

    // 姘稿姭涔嬮敭 - tdk妲戒綅锛屼紶璇寸骇锛?20骞歌繍+骞歌繍缂╂斁鏆村嚮锛屽叏閮ㄦ灙姊扮被鍨?
    public static final Item YONGJIE_ZHIJIAN = new YongjieZhijian(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.EPIC));

    // 绻佹槦 - 3rd妲戒綅锛岃闅欑骇锛屽彈浼ゅ喎鍗村熀纭€20tick+骞歌繍缂╂斁涓婇檺120tick锛屽叏閮ㄦ灙姊扮被鍨?
    public static final Item FANXING = new Fanxing(new Item.Properties()
            .stacksTo(64)
            .rarity(RIFT));

    // 瑙嗙晫鍙嶆紨 - tdk妲戒綅锛岃闅欑骇锛?40骞歌繍+鏆村嚮+铏氭暟宕╄В+渚垫煋+铏氭暟浼ゅ杞崲锛屽叏閮ㄦ灙姊扮被鍨?
    public static final Item SHIJIE_FANYAN = new ShijieFanyan(new Item.Properties()
            .stacksTo(64)
            .rarity(RIFT));

    // 閲嶅瀷姝﹀櫒绯诲垪楗板搧

    // 缁村皵钖?- 3rd妲戒綅锛岀█鏈夌骇锛孒P<20%榛勫績Lv1 60s鍐峰嵈锛岄噸鍨嬫鍣?
    public static final Item VILL_V = new VillV(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.RARE));

    // 铏氱┖涓囪棌 - tdk妲戒綅锛岀█鏈夌骇锛?10铏氭暟浼ゅ+5%/s寮硅嵂鎭㈠锛岄噸鍨嬫鍣?
    public static final Item XUKONG_WANCANG = new XukongWancang(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.RARE));

    // 鎰氭垙涔嬪專 - 3rd妲戒綅锛屼紶璇寸骇锛孒P<30%榛勫績Lv2 60s鍐峰嵈锛岄噸鍨嬫鍣?
    public static final Item YUXI_ZHIXIA = new YuxiZhixia(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.EPIC));

    // 鍚ず涔嬮敭 - tdk妲戒綅锛屼紶璇寸骇锛?20铏氭暟浼ゅ+10%/s寮硅嵂鎭㈠锛岄噸鍨嬫鍣?
    public static final Item QISHI_ZHIJIAN = new QishiZhijian(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.EPIC));

    // 铻烘棆 - 3rd妲戒綅锛岃闅欑骇锛屾瘡30s榛勫績Lv3锛岄噸鍨嬫鍣?
    public static final Item LUOXUAN = new Luoxuan(new Item.Properties()
            .stacksTo(64)
            .rarity(RIFT));

    // 铏氱┖涓囪棌路闆ㄤ紬澶╁崕 - tdk妲戒綅锛岃闅欑骇锛?20+atk*0.1铏氭暟浼ゅ+20%/s寮硅嵂鎭㈠+铏氭暟杞崲+蹇呭畾渚垫煋锛岄噸鍨嬫鍣?
    public static final Item XUKONG_WANCANG_YZTH = new XukongWancangYZTH(new Item.Properties()
            .stacksTo(64)
            .rarity(RIFT));

    // 鍗冨姭/浼婇粯灏旂郴鍒楅グ鍝?

    // 鍗冨姭 - 3rd妲戒綅锛岀█鏈夌骇锛?21铏氭暟鎶楁€?閫傚簲3/0.8/20s锛屽叏閮ㄦ灙姊扮被鍨?
    public static final Item KALPAS = new Kalpas(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.RARE));

    // 浼婇粯灏?- tdk妲戒綅锛岀█鏈夌骇锛屾渶澶х敓鍛藉€尖啋鏀诲嚮鍔涳紝鍏ㄩ儴鏋绫诲瀷
    public static final Item IMER = new Imer(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.RARE));

    // 鍧忓姭涔嬬劚 - 3rd妲戒綅锛屼紶璇寸骇锛?21铏氭暟鎶楁€?閫傚簲4/0.7/20s+鎶楁€р啋鐢熷懡鍊硷紝鍏ㄩ儴鏋绫诲瀷
    public static final Item HUAJIE_ZHIYAN = new HuajieZhiyan(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.EPIC));

    // 鏀厤涔嬮敭 - tdk妲戒綅锛屼紶璇寸骇锛屾渶澶х敓鍛藉€尖啋鏀诲嚮鍔?atk*0.3铏氭暟浼ゅ锛屽叏閮ㄦ灙姊扮被鍨?
    public static final Item DOMINANCE_KEY = new DominanceKey(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.EPIC));

    // 閺栫伃 - 3rd妲戒綅锛岃闅欑骇锛?21铏氭暟鎶楁€?閫傚簲6/0.5/20s+鎶楁€р啋鐢熷懡鍊硷紝鍏ㄩ儴鏋绫诲瀷
    public static final Item AOMIE = new AoMie(new Item.Properties()
            .stacksTo(64)
            .rarity(RIFT));

    // Meta-Morph - tdk妲戒綅锛岃闅欑骇锛屾渶澶х敓鍛藉€尖啋鏀诲嚮鍔?atk铏氭暟浼ゅ+鎶楁€р啋鐢熷懡鍋峰彇锛屽叏閮ㄦ灙姊扮被鍨?
    public static final Item META_MORPH = new MetaMorph(new Item.Properties()
            .stacksTo(64)
            .rarity(RIFT));

    // 鑻忕郴鍒楅グ鍝?

    // 鑻?- 3rd妲戒綅锛岀█鏈夌骇锛?31铏氭暟鎶楁€?30%HP-dmg10%鍑忎激锛屾鏋?
    public static final Item SU = new Su(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.RARE));

    // 涓囩墿浼戠湢 - tdk妲戒綅锛岀█鏈夌骇锛宱verheal0.3+5%/s寮硅嵂锛屾鏋?
    public static final Item WANWU_XIUMIAN = new WanwuXiumian(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.RARE));

    // 瑙夎€?- 3rd妲戒綅锛屼紶璇寸骇锛?31铏氭暟鎶楁€?40%HP-dmg30%鍑忎激锛屾鏋?
    public static final Item JUEZHE = new Juezhe(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.EPIC));

    // 鍋滄粸涔嬮敭 - tdk妲戒綅锛屼紶璇寸骇锛宱verheal0.5+寮硅嵂(10+res/200)%锛屾鏋?
    public static final Item TINGZHI_ZHIJIAN = new TingzhiZhijian(new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.EPIC));

    // 澶╂収 - 3rd妲戒綅锛岃闅欑骇锛?31铏氭暟鎶楁€?50%HP-dmg(0.7-res/100)鍑忎激锛屾鏋?
    public static final Item TIANHUI = new Tianhui(new Item.Properties()
            .stacksTo(64)
            .rarity(RIFT));

    // 鍥犳灉杞疆 - tdk妲戒綅锛岃闅欑骇锛宱verheal1+寮硅嵂res/100%+铏氭暟浼ゅ锛屾鏋?
    public static final Item YINGUO_ZHUANLUN = new YinguoZhuanlun(new Item.Properties()
            .stacksTo(64)
            .rarity(RIFT));

    // 鏂扮郴鍒楋細鎴掑緥/榛勯噾/鏃厜/鏃犻檺/娴敓/绌烘ⅵ锛堝悇 6 浠讹級

    // --- 鎴掑緥锛堥湴寮规灙锛夛細浜虹墿绾?3rd ---
    // 闃挎尝灏间簹 - 鏀诲嚮15%姒傜巼鏂藉姞1涓殢鏈篸ebuff锛?5绉掞級
    public static final Item APONIA = new Aponia(new Item.Properties()
            .stacksTo(64).rarity(Rarity.RARE));
    // 娣辩姜涔嬫 - 鏀诲嚮15%姒傜巼鏂藉姞2涓殢鏈篸ebuff锛?5绉掞級
    public static final Item SHENZUI_ZHIJIAN = new ShenzuiZhijian(new Item.Properties()
            .stacksTo(64).rarity(Rarity.EPIC));
    // 鎴掑緥 - 鏀诲嚮15%姒傜巼鏂藉姞3涓殢鏈篸ebuff锛?5绉掞級
    public static final Item JIELV = new Jielv(new Item.Properties()
            .stacksTo(64).rarity(RIFT));

    // --- 鎴掑緥锛堥湴寮规灙锛夛細绁炰箣閿嚎 tdk ---
    // 寰€涓栫殑鑻﹀洑 - 鏀诲嚮鎸夎櫄鏁版姉鎬ф鐜囨柦鍔犲穿鍧忕梾I绾э紙鏄撲激20%锛?
    public static final Item WANGSHI_DE_KUQIU = new WangshiDeKuqiu(new Item.Properties()
            .stacksTo(64).rarity(Rarity.RARE));
    // 寰€涓栫殑鑻﹀洑路鍛戒箣濂?- 宕╁潖鐥匢I绾э紙鏄撲激40%锛?
    public static final Item WANGSHI_DE_KUQIU_MINGZHIQI = new WangshiDeKuqiuMingzhiqi(new Item.Properties()
            .stacksTo(64).rarity(Rarity.EPIC));
    // 绗浂棰濆畾鍔熺巼路绁炴仼缁撶晫 - 宕╁潖鐥匢II绾э紙鏄撲激60%锛?浼ゅ杞櫄鏁?渚垫煋
    public static final Item SHENEN_JIEJIE = new ShenenJiejie(new Item.Properties()
            .stacksTo(64).rarity(RIFT));

    // --- 榛勯噾锛堟墜鏋級锛氫汉鐗╃嚎 3rd ---
    // 浼婄敻 - 36鏍煎唴鐜╁姣?绉掕幏寰?涓闈uff锛圛绾э級
    public static final Item EDEN = new Eden(new Item.Properties()
            .stacksTo(64).rarity(Rarity.RARE));
    // 鐠€鑰€涔嬫瓕 - 鍚屼笂锛圛I绾э級
    public static final Item CUIYAO_ZHI_GE = new CuiyaoZhiGe(new Item.Properties()
            .stacksTo(64).rarity(Rarity.EPIC));
    // 榛勯噾 - 鍚屼笂锛圛II绾э級
    public static final Item HUANGJIN = new Huangjin(new Item.Properties()
            .stacksTo(64).rarity(RIFT));

    // --- 榛勯噾锛堟墜鏋級锛氱涔嬮敭绾?tdk ---
    // 浼婄敻涔嬫槦 - 16鏍煎唴闈炵帺瀹跺疄浣撶灛绉诲繀瀹氬け鏁?
    public static final Item EDEN_STAR = new EdenStar(new Item.Properties()
            .stacksTo(64).rarity(Rarity.RARE));
    // 鍚炲櫖涔嬮敭 - 32鏍?
    public static final Item TUNTIAN_ZHIJIAN = new TuntianZhijian(new Item.Properties()
            .stacksTo(64).rarity(Rarity.EPIC));
    // 绗笁棰濆畾鍔熺巼路濂囩偣閲嶆瀯 - 64鏍?浼ゅ杞櫄鏁?渚垫煋
    public static final Item QIDIAN_CHONGGOU = new QidianChonggou(new Item.Properties()
            .stacksTo(64).rarity(RIFT));

    // --- 鏃厜锛堣繎鎴橈級锛氫汉鐗╃嚎 3rd ---
    // 绉戞柉榄?- 鏀婚€?8%銆佹敾浼?5%
    public static final Item KOSMA = new Kosma(new Item.Properties()
            .stacksTo(64).rarity(Rarity.RARE));
    // 榛庢槑涔嬪摠 - 鏀婚€?15%銆佹敾浼?10%銆佹毚鍑荤巼+5%
    public static final Item LIMING_ZHI_SHAO = new LimingZhiShao(new Item.Properties()
            .stacksTo(64).rarity(Rarity.EPIC));
    // 鏃厜 - 鏀婚€?25%銆佹敾浼?20%銆佹毚鍑讳激瀹?30%
    public static final Item XUGUANG = new Xuguang(new Item.Properties()
            .stacksTo(64).rarity(RIFT));

    // --- 鏃厜锛堣繎鎴橈級锛氱涔嬮敭绾?tdk ---
    // 鍦拌棌寰￠瓊 - 鏀诲嚮蹇呭畾鍓婂噺鐩爣褰撳墠鎶ょ敳/闊ф€?%锛堟寔涔咃級
    public static final Item DIZANG_YUHUN = new DizangYuhun(new Item.Properties()
            .stacksTo(64).rarity(Rarity.RARE));
    // 渚佃殌涔嬮敭 - 鍓婂噺10%
    public static final Item QINSHI_ZHIJIAN = new QinshiZhijian(new Item.Properties()
            .stacksTo(64).rarity(Rarity.EPIC));
    // 寰￠瓊绀虹幇 - 鎸夎櫄鏁版姉鎬х櫨鍒嗘瘮鍓婂噺+浼ゅ杞櫄鏁?渚垫煋
    public static final Item YUHUN_SHIXIAN = new YuhunShixian(new Item.Properties()
            .stacksTo(64).rarity(RIFT));

    // --- 鏃犻檺锛堣交鏈烘灙锛夛細浜虹墿绾?3rd ---
    // 姊呮瘮涔屾柉 - 姣忕瀹炰綋绫诲瀷鍑绘潃绱锛屽叏灞炴€?1%
    public static final Item MEBIUS = new Mebius(new Item.Properties()
            .stacksTo(64).rarity(Rarity.RARE));
    // 鍣晫涔嬭泧 - 鍏ㄥ睘鎬?1.5%
    public static final Item SHIJIE_ZHI_SHE = new ShijieZhiShe(new Item.Properties()
            .stacksTo(64).rarity(Rarity.EPIC));
    // 鏃犻檺 - 鍏ㄥ睘鎬?2%
    public static final Item WUXIAN = new Wuxian(new Item.Properties()
            .stacksTo(64).rarity(RIFT));

    // --- 鏃犻檺锛堣交鏈烘灙锛夛細绁炰箣閿嚎 tdk ---
    // 寰€涓栫殑铔囧奖 - 閫犳垚浼ゅ10%姒傜巼绉婚櫎鐩爣1涓闈uff
    public static final Item WANGSHI_DE_SHEYING = new WangshiDeSheying(new Item.Properties()
            .stacksTo(64).rarity(Rarity.RARE));
    // 鑸嶆矙 - 铏氭暟鎶楁€ф鐜?浼ゅ杞櫄鏁?渚垫煋
    public static final Item SHESHA = new Shesha(new Item.Properties()
            .stacksTo(64).rarity(RIFT));
    // 寰€涓栫殑铔囧奖路姝讳箣琛?- 20%姒傜巼
    public static final Item SI_ZHI_YI = new SiZhiYi(new Item.Properties()
            .stacksTo(64).rarity(Rarity.EPIC));

    // --- 娴敓锛堣繎鎴橈級锛氫汉鐗╃嚎 3rd ---
    // 鍗?- 鎶ょ敳+20%銆佹姢鐢查煣鎬?20%
    public static final Item HUA = new Hua(new Item.Properties()
            .stacksTo(64).rarity(Rarity.RARE));
    // 娓″皹涔嬬窘 - 鎶ょ敳+50%銆佹姢鐢查煣鎬?50%
    public static final Item DUCHEN_ZHI_YU = new DuchenZhiYu(new Item.Properties()
            .stacksTo(64).rarity(Rarity.EPIC));
    // 娴敓 - 鎶ょ敳/闊ф€?铏氭暟鎶楁€х櫨鍒嗘瘮
    public static final Item FUSHENG = new Fusheng(new Item.Properties()
            .stacksTo(64).rarity(RIFT));

    // --- 娴敓锛堣繎鎴橈級锛氱涔嬮敭绾?tdk ---
    // 缇芥浮灏?- 鏀诲嚮5%姒傜巼鍋滄鐩爣AI 5绉?
    public static final Item YUDUCHEN = new Yuduchen(new Item.Properties()
            .stacksTo(64).rarity(Rarity.RARE));
    // 鍑″皹闅炬浮 - 15%姒傜巼
    public static final Item FANCHEN_NANDU = new FanchenNandu(new Item.Properties()
            .stacksTo(64).rarity(Rarity.EPIC));
    // 涓嶈瘑鏃跺姟 - 铏氭暟鎶楁€ф鐜?浼ゅ杞櫄鏁?渚垫煋
    public static final Item BUSHI_SHIWU = new BushiShiwu(new Item.Properties()
            .stacksTo(64).rarity(RIFT));

    // --- 绌烘ⅵ锛堥湴寮规灙锛夛細浜虹墿绾?3rd锛堜笁闃舵晥鏋滀竴鑷达級 ---
    // 甯曟湹鑿插埄鏂?- 閽撻奔0.01%鑾峰緱涓嬬晫涔嬫槦/榫欒泲+鍑绘潃鎴樺埄鍝佺炕鍊?
    public static final Item PADO_PHILIPIS = new PadoPhilipis(new Item.Properties()
            .stacksTo(64).rarity(Rarity.RARE));
    // 鎺犻泦涔嬪吔
    public static final Item LUEJI_ZHI_SHOU = new LuejiZhiShou(new Item.Properties()
            .stacksTo(64).rarity(Rarity.EPIC));
    // 绌烘ⅵ
    public static final Item KONGMENG = new Kongmeng(new Item.Properties()
            .stacksTo(64).rarity(RIFT));

    // --- 绌烘ⅵ锛堥湴寮规灙锛夛細绁炰箣閿嚎 tdk ---
    // 寰€涓栫殑骞绘ⅵ - 閫犳垚浼ゅ姒傜巼1.5鍊嶏紙姒傜巼=铏氭暟鎶楁€э級
    public static final Item WANGSHI_DE_HUANMENG = new WangshiDeHuanmeng(new Item.Properties()
            .stacksTo(64).rarity(Rarity.RARE));
    // 鎷変箣鐪?- 2鍊?浼ゅ杞櫄鏁?渚垫煋
    public static final Item LA_ZHI_YAN = new LaZhiYan(new Item.Properties()
            .stacksTo(64).rarity(RIFT));
    // 寰€涓栫殑骞绘ⅵ路澶滀箣鐬?- 1.8鍊?
    public static final Item YE_ZHI_TONG = new YeZhiTong(new Item.Properties()
            .stacksTo(64).rarity(Rarity.EPIC));

    // 鏈€缁堥樁娈电嫭绔嬮グ鍝?

    // 閫愮伀涔嬭浘銆岀湡鎴戙€? tcc_3rd妲斤紝瑁傞殭绾э紝铏氭暟鎶楁€?60锛屽叏灞炴€?50%锛屼綆琛€閲忕粨鐣?
    public static final Item ZEN_WO = new ZhenWo(new Item.Properties()
            .stacksTo(64).rarity(RIFT));

    // 榛戞笂鐧借姳路鍒涚伃铻烘棆 - tcc_tdk妲斤紝瑁傞殭绾э紝姣忔閫犳垚浼ゅ闄勫姞鑷韩褰撳墠琛€閲?00%铏氭暟浼ゅ
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
