package com.xlxyvergil.tcc.util;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

import javax.annotation.Nullable;

/**
 * 集中管理饰品升级的 NBT 读写、公式计算与 Config 读取；被属性修饰符应用、Tooltip 显示、异况事件处理器统一调用。
 */
public class FusionUpgradeUtil {

    private static final String NBT_KEY_LEVEL = "tcc_fusion_level";

    // NBT 读写

    /**
     * 读取饰品当前等级；无 NBT 标签时返回 0（新饰品初始等级）。
     */
    public static int getLevel(ItemStack stack) {
        if (stack.isEmpty()) return 0;
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(NBT_KEY_LEVEL)) {
            return tag.getInt(NBT_KEY_LEVEL);
        }
        return 0;
    }

    public static void setLevel(ItemStack stack, int level) {
        stack.getOrCreateTag().putInt(NBT_KEY_LEVEL, Math.max(0, level));
    }

    // Config 读取

    public static double getGrowthCoefficient() {
        return TaczCuriosConfig.COMMON.fusionGrowthCoefficient.get();
    }

    /**
     * 指定稀有度的封顶等级；RIFT 及非 tcc_slot 稀有度返回 0（不参与升级）。
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
     * 指定稀有度的 EBC（基础内融核心消耗）。
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

    // 公式计算

    public static double getActualValue(double maxLevelValue, FusionData data) {
        return getActualValue(maxLevelValue, data.level(), data.rarity());
    }

    /**
     * 按等级反推实际值并截断到 2 位小数：实际值 = 满级值 × (1 + 等级 × C) / (1 + 最大等级 × C)。
     * Lv.0 为 满级值 / (1 + 最大等级 × C)，Lv.MAX 为满级值；maxLevel ≤ 1（不参与升级）时直接返回满级值。
     * 截断而非四舍五入，保证 7.2% → 7%、-7.2% → -7%，与 tooltip 显示一致。
     */
    public static double getActualValue(double maxLevelValue, int level, @Nullable Rarity rarity) {
        if (rarity == null) return maxLevelValue;
        int maxLevel = getMaxLevel(rarity);
        if (maxLevel <= 1) return maxLevelValue;
        double maxMultiplier = 1.0 + maxLevel * getGrowthCoefficient();
        double levelMultiplier = 1.0 + level * getGrowthCoefficient();
        double raw = maxLevelValue * levelMultiplier / maxMultiplier;
        return (int)(raw * 10000.0) / 10000.0;
    }

    /**
     * 从 0 级升至 targetLevel 所需内融核心总数：Cost = EBC × (2^targetLevel - 1)；targetLevel ≤ 0 返回 0。
     */
    public static int getUpgradeCost(int targetLevel, Rarity rarity) {
        if (targetLevel <= 0) return 0;
        int ebc = getEBC(rarity);
        // 2^targetLevel - 1，使用位移避免浮点误差
        return ebc * ((1 << targetLevel) - 1);
    }

    /**
     * 分解产出 CoreFusion 数量：COMMON 为 (10/3)×等级+5，UNCOMMON 为 7.5×等级+10，RARE 为 12.5×等级+15，EPIC 为 (1+等级)×20。
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
