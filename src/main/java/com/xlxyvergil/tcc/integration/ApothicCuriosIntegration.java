package com.xlxyvergil.tcc.integration;

import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;

import java.util.function.Predicate;

/**
 * Apotheosis 神化属性集成
 * 为 TaczCurios 饰品添加词缀和宝石支持
 */
public class ApothicCuriosIntegration {
    
    private static final String APOTHEOSIS_MODID = "apotheosis";
    private static boolean initialized = false;
    
    /**
     * 初始化神化属性集成
     */
    public static void init() {
        if (initialized) return;
        
        // 检查 Apotheosis 是否加载
        if (!ModList.get().isLoaded(APOTHEOSIS_MODID)) {
            return;
        }
        
        try {
            // 为所有 Curios 插槽类型注册战利品类别
            registerCurioLootCategories();
            initialized = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 为 Curios 插槽注册战利品类别
     * 参考 Apothic-Curios 的实现
     */
    private static void registerCurioLootCategories() {
        // 获取所有已注册的 Curios 插槽类型 ID
        CuriosApi.getSlotHelper().getSlotTypeIds().forEach(slotId -> {
            String categoryId = "curios:" + slotId;
            
            // 创建插槽验证器
            SlotContext slotContext = new SlotContext(slotId, null, 0, false, false);
            Predicate<ItemStack> validator = stack -> CuriosApi.isStackValid(slotContext, stack);
            
            // 使用假的装备槽位（与 Apothic-Curios 一致）
            EquipmentSlot[] fakeSlots = {EquipmentSlot.LEGS};
            
            // 注册战利品类别
            LootCategory.register(null, categoryId, validator, fakeSlots);
        });
    }
    
    /**
     * 检查是否已初始化
     */
    public static boolean isInitialized() {
        return initialized;
    }
}
