package com.xlxyvergil.tcc.registries;

import com.xlxyvergil.tcc.items.AlloyDrill;
import com.xlxyvergil.tcc.items.CarefulHeart;
import com.xlxyvergil.tcc.items.DespicableAcceleration;
import com.xlxyvergil.tcc.items.HeavenFireApocalypse;
import com.xlxyvergil.tcc.items.HeavenFireJudgment;
import com.xlxyvergil.tcc.items.HeavyCaliberTag;
import com.xlxyvergil.tcc.items.MergedRifling;
import com.xlxyvergil.tcc.items.RedMovementTag;
import com.xlxyvergil.tcc.items.SoldierBasicTag;
import com.xlxyvergil.tcc.items.SoldierSpecificTag;
import com.xlxyvergil.tcc.items.UralWolfTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * 物品注册类，参考Enigmatic-Legacy的EnigmaticItems实现
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
    
    // 重口径标签 - 将10%攻击力转换为枪械伤害倍率，但增加150%枪械重量
    public static final RegistryObject<Item> HEAVY_CALIBER_TAG = ITEMS.register("heavy_caliber_tag", 
        () -> new HeavyCaliberTag(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.RARE)));
    
    // 红-有-三标签 - 提供50%持枪移动速度加成
    public static final RegistryObject<Item> RED_MOVEMENT_TAG = ITEMS.register("red_movement_tag", 
        () -> new RedMovementTag(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.UNCOMMON)));
    
    // 乌拉尔银狼标签 - 提供150%爆头倍率加成
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
}