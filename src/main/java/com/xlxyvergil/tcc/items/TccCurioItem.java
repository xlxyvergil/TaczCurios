package com.xlxyvergil.tcc.items;

import com.xlxyvergil.tcc.util.FusionData;
import net.minecraft.world.item.ItemStack;

/**
 * tcc 饰品基类 - 负责 tcc 可升级（融合等级）饰品的等级效果功能。
 * <p>
 * 效果值按当前融合等级换算：满级时返回满级配置值，低等级按比例衰减。
 */
public abstract class TccCurioItem extends BaseCurioItem {

    public TccCurioItem(Properties properties) {
        super(properties);
    }

    /**
     * 按当前融合等级换算满级配置值。
     *
     * @param maxValue 满级效果值
     * @param stack    饰品物品栈（用于读取融合等级）
     * @return 当前等级下的实际效果值
     */
    protected final double fusedValue(double maxValue, ItemStack stack) {
        return FusionData.from(stack).getActualValue(maxValue);
    }
}
