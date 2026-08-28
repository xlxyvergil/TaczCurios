package com.xlxyvergil.tcc.items;

import com.xlxyvergil.tcc.util.FusionData;
import net.minecraft.world.item.ItemStack;

/**
 * tcc 饰品基类，处理可升级（融合等级）饰品的等级效果换算：满级返回满级配置值，低等级按比例衰减。
 */
public abstract class TccCurioItem extends BaseCurioItem {

    public TccCurioItem(Properties properties) {
        super(properties);
    }

    /**
     * 按当前融合等级换算满级配置值。
     */
    protected final double fusedValue(double maxValue, ItemStack stack) {
        return FusionData.from(stack).getActualValue(maxValue);
    }
}
