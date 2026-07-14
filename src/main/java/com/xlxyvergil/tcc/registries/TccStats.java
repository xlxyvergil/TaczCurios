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

    // ===== 新增统计在此声明 =====

    private TccStats() {}

    /** 注册所有自定义统计到 BuiltInRegistries.CUSTOM_STAT */
    public static void register() {
        registerCustom(ZOMBIE_VILLAGER_CURED, StatFormatter.DEFAULT);

        // ===== 新增统计在此注册 =====
    }

    private static void registerCustom(ResourceLocation key, StatFormatter formatter) {
        Registry.register(BuiltInRegistries.CUSTOM_STAT, key, key);
        Stats.CUSTOM.get(key, formatter);
    }
}
