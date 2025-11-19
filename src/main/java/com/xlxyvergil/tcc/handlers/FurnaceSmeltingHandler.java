package com.xlxyvergil.tcc.handlers;

import com.xlxyvergil.tcc.TaczCurios;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;

/**
 * 处理饰品在熔炉中烧制时产生不同数量内融合心的事件处理器
 */
@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FurnaceSmeltingHandler {
    
    // 定义不同稀有度饰品烧制后的内融合心产出数量
    private static final Map<String, Integer> RARITY_SMELTING_OUTPUT = new HashMap<>();
    
    static {
        RARITY_SMELTING_OUTPUT.put("tcc:common_curios", 1);
        RARITY_SMELTING_OUTPUT.put("tcc:uncommon_curios", 3);
        RARITY_SMELTING_OUTPUT.put("tcc:rare_curios", 5);
        RARITY_SMELTING_OUTPUT.put("tcc:epic_curios", 9);
    }
}