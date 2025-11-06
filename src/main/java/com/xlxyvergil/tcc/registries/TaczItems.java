package com.xlxyvergil.tcc.registries;

import com.xlxyvergil.tcc.items.HeavyCaliberTag;
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
}