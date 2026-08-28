package com.xlxyvergil.tcc.util;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

import javax.annotation.Nullable;

/**
 * 融合数据 — 轻量级不可变快照，封装饰品当前的融合等级与稀有度。
 * 通过 from(ItemStack) 工厂方法从道具 NBT 读取生成；写入 NBT 仍通过 FusionUpgradeUtil.setLevel 完成。
 */
public record FusionData(int level, @Nullable Rarity rarity) {

    /** 从道具读取当前融合等级与稀有度。道具为空时返回 level=0 / rarity=null。 */
    public static FusionData from(ItemStack stack) {
        if (stack.isEmpty()) return new FusionData(0, null);
        return new FusionData(
                FusionUpgradeUtil.getLevel(stack),
                stack.getItem().getRarity(stack)
        );
    }

    /** 根据当前稀有度获取封顶等级。稀有度为 null 时返回 0（不参与升级）。 */
    public int maxLevel() {
        if (rarity == null) return 0;
        return FusionUpgradeUtil.getMaxLevel(rarity);
    }

    /** 是否可升级（封顶等级 > 0）。 */
    public boolean isUpgradeable() {
        return maxLevel() > 0;
    }

    /**
     * 根据满级 Config 值反推当前等级的实际值。
     * 等价于 FusionUpgradeUtil.getActualValue(double, int, Rarity)。
     */
    public double getActualValue(double maxLevelValue) {
        return FusionUpgradeUtil.getActualValue(maxLevelValue, level, rarity);
    }
}
