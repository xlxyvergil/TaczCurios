package com.xlxyvergil.tcc.util;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

/**
 * 融合升级工具类 — 集中管理饰品升级的 NBT 读写、公式计算与 Config 读取。
 * 
 * <p>被属性修饰符应用、Tooltip 显示、异况事件处理器统一调用。</p>
 */
public class FusionUpgradeUtil {

    private static final String NBT_KEY_LEVEL = "tcc_fusion_level";

    // ========== NBT 读写 ==========

    /**
     * 读取饰品当前等级。
     * <p>无 NBT 标签时默认返回 1（新饰品初始等级）。</p>
     */
    public static int getLevel(ItemStack stack) {
        if (stack.isEmpty()) return 0;
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(NBT_KEY_LEVEL)) {
            return tag.getInt(NBT_KEY_LEVEL);
        }
        return 1;
    }

    /**
     * 写入饰品等级。
     */
    public static void setLevel(ItemStack stack, int level) {
        stack.getOrCreateTag().putInt(NBT_KEY_LEVEL, Math.max(0, level));
    }

    // ========== Config 读取 ==========

    public static double getGrowthCoefficient() {
        return TaczCuriosConfig.COMMON.fusionGrowthCoefficient.get();
    }

    /**
     * 获取指定稀有度的封顶等级。
     * RIFT / 非 tcc_slot 稀有度返回 0（不参与升级）。
     */
    public static int getMaxLevel(Rarity rarity) {
        return switch (rarity) {
            case COMMON   -> TaczCuriosConfig.COMMON.fusionMaxLevelCommon.get();
            case UNCOMMON -> TaczCuriosConfig.COMMON.fusionMaxLevelUncommon.get();
            case RARE     -> TaczCuriosConfig.COMMON.fusionMaxLevelRare.get();
            case EPIC     -> TaczCuriosConfig.COMMON.fusionMaxLevelEpic.get();
            default       -> 0;
        };
    }

    /**
     * 获取指定稀有度的 EBC（基础内融核心消耗）。
     */
    public static int getEBC(Rarity rarity) {
        return switch (rarity) {
            case COMMON   -> TaczCuriosConfig.COMMON.fusionEbcCommon.get();
            case UNCOMMON -> TaczCuriosConfig.COMMON.fusionEbcUncommon.get();
            case RARE     -> TaczCuriosConfig.COMMON.fusionEbcRare.get();
            case EPIC     -> TaczCuriosConfig.COMMON.fusionEbcEpic.get();
            default       -> 0;
        };
    }

    // ========== 公式计算 ==========

    /**
     * 计算指定等级的实际属性值，截断到 2 位小数。
     * <p>公式：实际值 = 基础值 × (1 + (等级 - 1) × C)</p>
     * <p>等级 1 时等于基础值本身，等级 ≥ 2 时按 C 增长。</p>
     * <p>截断（而非四舍五入）确保 7.2% → 7%、-7.2% → -7%，与 tooltip 显示一致。</p>
     *
     * @param baseValue Config 中配置的基础值（Lv.1 时的值）
     * @param level     当前饰品等级
     * @return 按等级缩放后的实际值（截断到 2 位小数）
     */
    public static double getActualValue(double baseValue, int level) {
        double raw = baseValue * (1 + (level - 1) * getGrowthCoefficient());
        return (int)(raw * 100.0) / 100.0;
    }

    /**
     * 从 0 级升至 targetLevel 所需的内融核心总数。
     * <p>公式：{@code Cost = EBC × (2^targetLevel - 1)}</p>
     *
     * @param targetLevel 目标等级（≥1）
     * @param rarity      饰品稀有度
     * @return 所需 CoreFusion 总数，若 targetLevel ≤ 0 则返回 0
     */
    public static int getUpgradeCost(int targetLevel, Rarity rarity) {
        if (targetLevel <= 0) return 0;
        int ebc = getEBC(rarity);
        // 2^targetLevel - 1，使用位移避免浮点误差
        return ebc * ((1 << targetLevel) - 1);
    }

    /**
     * 计算分解产出 CoreFusion 数量。
     * <p>公式：</p>
     * <ul>
     *   <li>COMMON:   (10 ÷ 3) × 等级 + 5</li>
     *   <li>UNCOMMON: 7.5 × 等级 + 10</li>
     *   <li>RARE:     12.5 × 等级 + 15</li>
     *   <li>EPIC:     (1 + 等级) × 20</li>
     * </ul>
     *
     * @param rarity 饰品稀有度
     * @param level  饰品当前等级
     * @return 分解获得的 CoreFusion 数量（向下取整）
     */
    public static int getDecompositionOutput(Rarity rarity, int level) {
        return switch (rarity) {
            case COMMON   -> (int) Math.floor((10.0 / 3.0) * level + 5);
            case UNCOMMON -> (int) Math.floor(7.5 * level + 10);
            case RARE     -> (int) Math.floor(12.5 * level + 15);
            case EPIC     -> (1 + level) * 20;
            default       -> 0;
        };
    }
}
