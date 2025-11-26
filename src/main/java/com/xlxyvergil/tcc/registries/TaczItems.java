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
import com.xlxyvergil.tcc.items.LimitSpeed;
import com.xlxyvergil.tcc.items.MagazineBoost;
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
import com.xlxyvergil.tcc.items.SustainedFire;
import com.xlxyvergil.tcc.items.TacticalReload;
import com.xlxyvergil.tcc.items.UralWolfTag;
import com.xlxyvergil.tcc.items.WaspStinger;

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
    
    // 弹匣增幅 - 提升30%装填速度
    public static final RegistryObject<Item> MAGAZINE_BOOST = ITEMS.register("magazine_boost", 
        () -> new MagazineBoost(new Item.Properties()
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
}