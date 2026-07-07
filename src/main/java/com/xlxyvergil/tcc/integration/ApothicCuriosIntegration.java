package com.xlxyvergil.tcc.integration;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraftforge.fml.ModList;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;

/**
 * Apotheosis 神化属性集成
 * 
 * 兼容 Apothic-Curios 和 Apotheosis-Artifice 环境
 * 使用 CuriosApi 直接验证饰品所属的插槽类型
 */
public class ApothicCuriosIntegration {
    
    private static final String APOTHIC_CURIOS_MODID = "apothiccurios";
    private static final String ARTIFICE_MODID = "apotheosis_artifice";
    private static final EquipmentSlot FAKE_SLOT = EquipmentSlot.LEGS;
    private static boolean initialized = false;
    
    public static void init() {
        if (initialized) return;
        
        if (!TaczCuriosConfig.COMMON.enableApotheosisIntegration.get()) {
            return;
        }
        
        // 至少需要安装其中一个 mod 才能生效
        boolean hasApothicCurios = ModList.get().isLoaded(APOTHIC_CURIOS_MODID);
        boolean hasArtifice = ModList.get().isLoaded(ARTIFICE_MODID);
        if (!hasApothicCurios && !hasArtifice) {
            return;
        }
        
        registerCurioLootCategory("curios:tcc_slot");
        registerCurioLootCategory("curios:tcc_3rd");
        registerCurioLootCategory("curios:tcc_tdk");
        initialized = true;
    }
    
    private static void registerCurioLootCategory(String id) {
        if (LootCategory.byId(id) != null) return;
        String slotId = id.replace("curios:", "");
        SlotContext slotContext = new SlotContext(slotId, null, 0, false, false);
        
        LootCategory.register(null, id,
            s -> !s.isEmpty() && CuriosApi.isStackValid(slotContext, s),
            new EquipmentSlot[]{FAKE_SLOT});
    }
    
    public static boolean isInitialized() {
        return initialized;
    }
}
