package com.xlxyvergil.tcc.integration;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.TaczCurios;
import daripher.apothiccurios.ApothicCuriosMod;
import net.minecraftforge.fml.ModList;

/**
 * Apotheosis 神化属性集成
 * 为 TaczCurios 饰品添加词缀和宝石支持
 * 
 * 注意：此集成需要 Apothic-Curios 模组才能生效
 * Apothic-Curios 会自动处理所有 Curios 插槽的战利品类别注册和词缀效果应用
 */
public class ApothicCuriosIntegration {
    
    private static final String APOTHIC_CURIOS_MODID = "apothiccurios";
    private static boolean initialized = false;
    
    /**
     * 初始化神化属性集成
     * 必须在 mod 构造函数中调用，确保在词缀数据加载前完成注册
     */
    public static void init() {
        if (initialized) return;
        
        // 检查是否启用了Apotheosis集成
        if (!TaczCuriosConfig.COMMON.enableApotheosisIntegration.get()) {
            return;
        }
        
        // 检查 Apothic-Curios 是否加载（它负责让饰品支持神化属性）
        if (!ModList.get().isLoaded(APOTHIC_CURIOS_MODID)) {
            return;
        }
        
        try {
            // 必须在早期注册 tcc_slot 的 LootCategory，确保词缀数据加载时能找到
            ApothicCuriosMod.registerCurioLootCategory("curios:tcc_slot");
            initialized = true;
        } catch (Exception e) {
            TaczCurios.LOGGER.error("Failed to initialize Apotheosis integration", e);
        }
    }
    
    /**
     * 检查是否已初始化
     */
    public static boolean isInitialized() {
        return initialized;
    }
}
