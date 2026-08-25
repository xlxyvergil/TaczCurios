package com.xlxyvergil.tcc.registries;

import com.xlxyvergil.tcc.TaczCurios;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.Stats;

/**
 * 统一管理 TCC 所有自定义统计信息。
 * 新增统计只需：1) 在下方声明 ResourceLocation 常量  2) 在 {@link #register()} 中调用 registerCustom
 */
public final class TccStats {

    /** 玩家治愈僵尸村民的累计次数 */
    public static final ResourceLocation ZOMBIE_VILLAGER_CURED =
            new ResourceLocation(TaczCurios.MODID, "zombie_villager_cured");

    /** 玩家合成物品的累计次数（PVill-V 成就需求） */
    public static final ResourceLocation ITEMS_CRAFTED =
            new ResourceLocation(TaczCurios.MODID, "items_crafted");

    // ===== 饰品佩戴存活时长统计 =====

    /** 佩戴 griseo 所生存的时间（tick），死亡不重置 */
    public static final ResourceLocation PLAY_TIME_GRISEO =
            new ResourceLocation(TaczCurios.MODID, "play_time_griseo");

    /** 佩戴 huishi_zhijuan 所生存的时间（tick），死亡不重置 */
    public static final ResourceLocation PLAY_TIME_HUISHI_ZHIJUAN =
            new ResourceLocation(TaczCurios.MODID, "play_time_huishi_zhijuan");

    /** 佩戴 fanxing 所生存的时间（tick），死亡不重置 */
    public static final ResourceLocation PLAY_TIME_FANXING =
            new ResourceLocation(TaczCurios.MODID, "play_time_fanxing");

    /** 佩戴 qishi_zhijian 所存活的时间（tick），死亡重置归 0 */
    public static final ResourceLocation PLAY_TIME_QISHI_ZHIJIAN =
            new ResourceLocation(TaczCurios.MODID, "play_time_qishi_zhijian");

    // ===== 新增统计在此声明 =====

    /** 食用 6 种鱼类食物的累计次数（空梦成就 1：tcc:fish_food_eaten） */
    public static final ResourceLocation FISH_FOOD_EATEN =
            new ResourceLocation(TaczCurios.MODID, "fish_food_eaten");

    /** 装备帕朵菲利斯时钓鱼成功次数（空梦成就 2：tcc:fish_caught_while_equipped） */
    public static final ResourceLocation FISH_CAUGHT_WHILE_EQUIPPED =
            new ResourceLocation(TaczCurios.MODID, "fish_caught_while_equipped");

    private TccStats() {}

    /** 注册所有自定义统计到 BuiltInRegistries.CUSTOM_STAT */
    public static void register() {
        registerCustom(ZOMBIE_VILLAGER_CURED, StatFormatter.DEFAULT);
        registerCustom(ITEMS_CRAFTED, StatFormatter.DEFAULT);

        // 饰品佩戴存活时长统计
        registerCustom(PLAY_TIME_GRISEO, StatFormatter.DEFAULT);
        registerCustom(PLAY_TIME_HUISHI_ZHIJUAN, StatFormatter.DEFAULT);
        registerCustom(PLAY_TIME_FANXING, StatFormatter.DEFAULT);
        registerCustom(PLAY_TIME_QISHI_ZHIJIAN, StatFormatter.DEFAULT);

        // 新系列统计
        registerCustom(FISH_FOOD_EATEN, StatFormatter.DEFAULT);
        registerCustom(FISH_CAUGHT_WHILE_EQUIPPED, StatFormatter.DEFAULT);

        // ===== 新增统计在此注册 =====
    }

    private static void registerCustom(ResourceLocation key, StatFormatter formatter) {
        Registry.register(BuiltInRegistries.CUSTOM_STAT, key, key);
        Stats.CUSTOM.get(key, formatter);
    }
}
